package com.bluemoon.dao;

import com.bluemoon.config.Database;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Cài đặt JDBC của {@link ThongKeDao}. */
public class ThongKeDaoImpl implements ThongKeDao {

    /**
     * Tổng tiền phiếu chưa hủy theo tháng lập phiếu. Lọc theo khoảng
     * thoi_diem (dùng được chỉ mục idx_pt_thoi_diem), nhóm theo năm, tháng.
     */
    private static final String SQL_THEO_THANG = "SELECT YEAR(thoi_diem) "
            + "AS nam, MONTH(thoi_diem) AS thang, SUM(tong_tien) AS tong "
            + "FROM phieu_thu WHERE da_huy = FALSE "
            + "AND thoi_diem >= ? AND thoi_diem < ? "
            + "GROUP BY YEAR(thoi_diem), MONTH(thoi_diem)";

    /**
     * Tiền đã thu của từng khoản phải nộp, chỉ từ phiếu chưa hủy (một khoản
     * phải nộp có thể nằm ở phiếu đã hủy rồi ở phiếu mới).
     */
    private static final String DA_THU = "(SELECT ct.phai_nop_id, "
            + "SUM(ct.so_tien) AS tien FROM phieu_thu_chi_tiet ct "
            + "JOIN phieu_thu pt ON pt.id = ct.phieu_thu_id "
            + "WHERE pt.da_huy = FALSE AND ct.phai_nop_id IS NOT NULL "
            + "GROUP BY ct.phai_nop_id) da";

    private static final String SQL_TY_LE = "SELECT d.id, "
            + "COALESCE(SUM(da.tien), 0) AS da_thu, "
            + "COALESCE(SUM(pn.so_tien), 0) AS phai_nop FROM dot_thu d "
            + "LEFT JOIN phai_nop pn ON pn.dot_thu_id = d.id "
            + "AND pn.so_tien > 0 "
            + "LEFT JOIN " + DA_THU + " ON da.phai_nop_id = pn.id "
            + "WHERE d.thang = ? GROUP BY d.id";

    private static final String SQL_THANG_DA_CHOT =
            "SELECT thang FROM dot_thu ORDER BY thang DESC";

    /**
     * Mỗi hộ có tối đa một khoản phải nộp cho mỗi khoản trong một đợt
     * (uq_pn_dot_ho_khoan), nên đếm dòng là đếm hộ. Khoản đã sửa về 0 đ
     * không phải nợ (giả định 33) nên không tính vào số phải đóng.
     */
    private static final String SQL_BAT_BUOC = "SELECT "
            + "COALESCE(SUM(da.tien), 0) AS tong_thu, "
            + "COALESCE(SUM(CASE WHEN pn.so_tien > 0 THEN pn.so_tien END), 0)"
            + " AS tong_phai_thu, "
            + "COUNT(CASE WHEN pn.so_tien > 0 THEN 1 END) AS so_phai, "
            + "COUNT(CASE WHEN pn.so_tien > 0 AND da.tien IS NOT NULL "
            + "THEN 1 END) AS so_da FROM phai_nop pn "
            + "JOIN dot_thu d ON d.id = pn.dot_thu_id "
            + "LEFT JOIN " + DA_THU + " ON da.phai_nop_id = pn.id "
            + "WHERE pn.khoan_phi_id = ? AND d.thang = ?";

    /**
     * Gộp các lần đóng góp theo hộ (giả định 29): tổng lớn hơn 0 là "đã
     * đóng", chỉ có lần 0 đ là "không đóng".
     */
    private static final String SQL_TU_NGUYEN = "SELECT "
            + "COALESCE(SUM(h.tong), 0) AS tong_thu, "
            + "COUNT(CASE WHEN h.tong > 0 THEN 1 END) AS so_da, "
            + "COUNT(CASE WHEN h.tong = 0 THEN 1 END) AS so_khong FROM "
            + "(SELECT pt.ho_id, SUM(ct.so_tien) AS tong "
            + "FROM phieu_thu_chi_tiet ct "
            + "JOIN phieu_thu pt ON pt.id = ct.phieu_thu_id "
            + "WHERE ct.khoan_phi_id = ? AND ct.phai_nop_id IS NULL "
            + "AND pt.da_huy = FALSE GROUP BY pt.ho_id) h";

    @Override
    public Map<YearMonth, BigDecimal> thuTheoThang(YearMonth tuThang,
            YearMonth denThang) {
        Map<YearMonth, BigDecimal> kq = new LinkedHashMap<>();
        try (Connection conn = Database.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL_THEO_THANG)) {
            ps.setObject(1, tuThang.atDay(1).atStartOfDay());
            ps.setObject(2, denThang.plusMonths(1).atDay(1).atStartOfDay());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.put(YearMonth.of(rs.getInt("nam"), rs.getInt("thang")),
                            rs.getBigDecimal("tong"));
                }
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi doc tong thu theo thang", e);
        }
    }

    @Override
    public Optional<TyLeThu> tyLeThu(YearMonth thang) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TY_LE)) {
            ps.setDate(1, Date.valueOf(thang.atDay(1)));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new TyLeThu(rs.getBigDecimal("da_thu"),
                        rs.getBigDecimal("phai_nop")));
            }
        } catch (SQLException e) {
            throw new DaoException("Loi doc ty le thu", e);
        }
    }

    @Override
    public List<YearMonth> thangDaChot() {
        List<YearMonth> kq = new ArrayList<>();
        try (Connection conn = Database.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL_THANG_DA_CHOT);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                kq.add(YearMonth.from(rs.getDate("thang").toLocalDate()));
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi doc thang da chot", e);
        }
    }

    @Override
    public SoLieuKhoan khoanBatBuoc(int khoanPhiId, YearMonth thang) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_BAT_BUOC)) {
            ps.setInt(1, khoanPhiId);
            ps.setDate(2, Date.valueOf(thang.atDay(1)));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new SoLieuKhoan(rs.getBigDecimal("tong_thu"),
                        rs.getBigDecimal("tong_phai_thu"),
                        rs.getInt("so_phai"), rs.getInt("so_da"), 0);
            }
        } catch (SQLException e) {
            throw new DaoException("Loi thong ke khoan bat buoc", e);
        }
    }

    @Override
    public SoLieuKhoan khoanTuNguyen(int khoanPhiId) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL_TU_NGUYEN)) {
            ps.setInt(1, khoanPhiId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new SoLieuKhoan(rs.getBigDecimal("tong_thu"),
                        BigDecimal.ZERO, 0, rs.getInt("so_da"),
                        rs.getInt("so_khong"));
            }
        } catch (SQLException e) {
            throw new DaoException("Loi thong ke khoan tu nguyen", e);
        }
    }
}
