package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.ThaoTac;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Cai dat JDBC cua {@link CongNoDao}. Luon dung PreparedStatement.
 */
public class CongNoDaoImpl implements CongNoDao {

    /** Tao dot neu chua co; da co thi khong doi gi. */
    private static final String SQL_TAO_DOT = "INSERT INTO dot_thu (thang, "
            + "nguoi_chot) VALUES (?, ?) ON DUPLICATE KEY UPDATE id = id";
    /** Khoa dong dot de cac lan chot cung thang chay noi tiep. */
    private static final String SQL_KHOA_DOT =
            "SELECT id FROM dot_thu WHERE thang = ? FOR UPDATE";
    /**
     * Them khoan neu chua co. An toan vi dong dot_thu da khoa: cac lan chot
     * cung thang chay noi tiep.
     */
    private static final String SQL_THEM_KHOAN = "INSERT INTO phai_nop "
            + "(dot_thu_id, ho_id, khoan_phi_id, dien_tich, don_gia, "
            + "so_tien_goc, so_tien) SELECT ?, ?, ?, ?, ?, ?, ? FROM DUAL "
            + "WHERE NOT EXISTS (SELECT 1 FROM phai_nop WHERE dot_thu_id = ? "
            + "AND ho_id = ? AND khoan_phi_id = ?)";
    private static final String SQL_DA_CHOT =
            "SELECT 1 FROM dot_thu WHERE thang = ?";
    private static final String CHON = "SELECT p.id, p.ho_id, h.ma_can_ho, "
            + "p.khoan_phi_id, k.ten, p.dien_tich, p.don_gia, p.so_tien_goc, "
            + "p.so_tien, p.nguoi_sua, p.ngay_sua, p.ghi_chu, EXISTS (SELECT 1 "
            + "FROM phieu_thu_chi_tiet ct WHERE ct.phai_nop_da_thu = p.id) "
            + "AS da_dong FROM phai_nop p "
            + "JOIN ho_gia_dinh h ON h.id = p.ho_id "
            + "JOIN khoan_phi k ON k.id = p.khoan_phi_id ";
    private static final String SQL_THEO_THANG = CHON
            + "JOIN dot_thu d ON d.id = p.dot_thu_id WHERE d.thang = ? "
            + "ORDER BY h.ma_can_ho, k.ten";
    private static final String SQL_THEO_ID = CHON + "WHERE p.id = ?";
    /** Khoan da co phieu thu con hieu luc (cot sinh phai_nop_da_thu). */
    private static final String DA_THU = "SELECT 1 FROM phieu_thu_chi_tiet "
            + "ct WHERE ct.phai_nop_da_thu = ";
    private static final String SQL_TONG_NO = "SELECT COALESCE(SUM(p.so_tien)"
            + ", 0) FROM phai_nop p WHERE p.ho_id = ? AND NOT EXISTS ("
            + DA_THU + "p.id)";
    private static final String SQL_HO_CUA_KHOAN =
            "SELECT ho_id FROM phai_nop WHERE id = ?";
    private static final String SQL_KHOA_HO =
            "SELECT id FROM ho_gia_dinh WHERE id = ? FOR UPDATE";
    private static final String SQL_KHOA_KHOAN =
            "SELECT so_tien FROM phai_nop WHERE id = ? FOR UPDATE";
    /** Ten khoan, thang, ma can ho de ghi nhat ky (doc thuong, khong khoa). */
    private static final String SQL_MO_TA_KHOAN = "SELECT k.ten, d.thang, "
            + "h.ma_can_ho FROM phai_nop p "
            + "JOIN khoan_phi k ON k.id = p.khoan_phi_id "
            + "JOIN dot_thu d ON d.id = p.dot_thu_id "
            + "JOIN ho_gia_dinh h ON h.id = p.ho_id WHERE p.id = ?";
    private static final String SQL_SUA = "UPDATE phai_nop SET so_tien = ?, "
            + "nguoi_sua = ?, ngay_sua = NOW(), ghi_chu = ? WHERE id = ?";
    private static final String SQL_DA_THU = DA_THU + "?";

    @Override
    public int chot(YearMonth thang, int nguoiChot, List<PhaiNop> khoan) {
        Date ngayDau = Date.valueOf(thang.atDay(1));
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int dotId = layDot(conn, ngayDau, nguoiChot);
                Set<Integer> hoCoKhoanMoi = new HashSet<>();
                int soMoi = themKhoan(conn, dotId, khoan, hoCoKhoanMoi);
                NhatKyDaoImpl.ghi(conn, nguoiChot, ThaoTac.CHOT_CONG_NO,
                        "Tháng " + NoiDungNhatKy.thang(thang), soMoi == 0
                        ? "Không có khoản mới (các hộ đã đủ khoản của tháng)"
                        : "Thêm " + soMoi + " khoản phải nộp cho "
                                + hoCoKhoanMoi.size() + " hộ");
                conn.commit();
                return soMoi;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw TrungKhoaException.boc("Loi chot cong no", e);
        }
    }

    private static int layDot(Connection conn, Date ngayDau, int nguoiChot)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_TAO_DOT)) {
            ps.setDate(1, ngayDau);
            ps.setInt(2, nguoiChot);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(SQL_KHOA_DOT)) {
            ps.setDate(1, ngayDau);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Khong tao duoc dot thu");
                }
                return rs.getInt(1);
            }
        }
    }

    private static int themKhoan(Connection conn, int dotId,
            List<PhaiNop> khoan, Set<Integer> hoCoKhoanMoi)
            throws SQLException {
        int soMoi = 0;
        try (PreparedStatement ps = conn.prepareStatement(SQL_THEM_KHOAN)) {
            for (PhaiNop p : khoan) {
                ps.setInt(1, dotId);
                ps.setInt(2, p.hoId());
                ps.setInt(3, p.khoanPhiId());
                ps.setBigDecimal(4, p.dienTich());
                ps.setBigDecimal(5, p.donGia());
                ps.setBigDecimal(6, p.soTienGoc());
                ps.setBigDecimal(7, p.soTien());
                ps.setInt(8, dotId);
                ps.setInt(9, p.hoId());
                ps.setInt(10, p.khoanPhiId());
                if (ps.executeUpdate() > 0) {
                    soMoi++;
                    hoCoKhoanMoi.add(p.hoId());
                }
            }
        }
        return soMoi;
    }

    @Override
    public boolean daChot(YearMonth thang) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DA_CHOT)) {
            ps.setDate(1, Date.valueOf(thang.atDay(1)));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi kiem tra thang da chot", e);
        }
    }

    @Override
    public List<PhaiNop> theoThang(YearMonth thang) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL_THEO_THANG)) {
            ps.setDate(1, Date.valueOf(thang.atDay(1)));
            List<PhaiNop> kq = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(doc(rs));
                }
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi lay cong no cua thang", e);
        }
    }

    @Override
    public Optional<PhaiNop> timTheoId(int id) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEO_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(doc(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tim khoan phai nop", e);
        }
    }

    /**
     * Sua trong mot giao dich, khoa theo cung thu tu voi lap phieu thu
     * (ho_gia_dinh, roi phai_nop) nen khong deadlock voi viec thu; chi sau
     * khi giu khoa moi kiem tra khoan da thu chua.
     */
    @Override
    public boolean suaSoTien(int id, BigDecimal soTien, int nguoiSua,
            String ghiChu) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int hoId = hoCuaKhoan(conn, id);
                khoa(conn, SQL_KHOA_HO, hoId);
                BigDecimal cu = (BigDecimal) khoa(conn, SQL_KHOA_KHOAN, id);
                try (PreparedStatement ps = conn.prepareStatement(SQL_DA_THU)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(SQL_SUA)) {
                    ps.setBigDecimal(1, soTien);
                    ps.setInt(2, nguoiSua);
                    if (ghiChu == null) {
                        ps.setNull(3, Types.VARCHAR);
                    } else {
                        ps.setString(3, ghiChu);
                    }
                    ps.setInt(4, id);
                    ps.executeUpdate();
                }
                ghiNhatKySua(conn, id, nguoiSua, cu, soTien, ghiChu);
                conn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw DangBiKhoaException.boc("Loi sua khoan phai nop", e);
        }
    }

    private static int hoCuaKhoan(Connection conn, int id)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_HO_CUA_KHOAN)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new XungDotException(
                            XungDotException.Loai.KHONG_TIM_THAY,
                            "phai nop " + id);
                }
                return rs.getInt(1);
            }
        }
    }

    /** Khoa mot dong; tra ve cot dau tien, doc duoi khoa. */
    private static Object khoa(Connection conn, String sql, int id)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new XungDotException(
                            XungDotException.Loai.KHONG_TIM_THAY, "id " + id);
                }
                return rs.getObject(1);
            }
        }
    }

    private static void ghiNhatKySua(Connection conn, int id, int nguoiSua,
            BigDecimal cu, BigDecimal moi, String ghiChu)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_MO_TA_KHOAN)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                YearMonth thang = YearMonth.from(
                        rs.getDate("thang").toLocalDate());
                NhatKyDaoImpl.ghi(conn, nguoiSua,
                        ThaoTac.SUA_SO_TIEN_PHAI_NOP,
                        HoGiaDinhDaoImpl.doiTuong(rs.getString("ma_can_ho")),
                        rs.getString("ten") + " tháng "
                                + NoiDungNhatKy.thang(thang) + ": "
                                + NoiDungNhatKy.tien(cu) + " → "
                                + NoiDungNhatKy.tien(moi)
                                + (ghiChu == null || ghiChu.isBlank() ? ""
                                        : ". Lý do: " + ghiChu));
            }
        }
    }

    @Override
    public boolean daCoPhieuThu(int id) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DA_THU)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi kiem tra khoan da thu", e);
        }
    }

    private static PhaiNop doc(ResultSet rs) throws SQLException {
        int nguoiSua = rs.getInt("nguoi_sua");
        Integer nguoi = rs.wasNull() ? null : nguoiSua;
        LocalDateTime ngay = rs.getObject("ngay_sua", LocalDateTime.class);
        return new PhaiNop(rs.getInt("id"), rs.getInt("ho_id"),
                rs.getString("ma_can_ho"), rs.getInt("khoan_phi_id"),
                rs.getString("ten"), rs.getBigDecimal("dien_tich"),
                rs.getBigDecimal("don_gia"), rs.getBigDecimal("so_tien_goc"),
                rs.getBigDecimal("so_tien"), nguoi,
                ngay,
                rs.getString("ghi_chu"), rs.getBoolean("da_dong"));
    }

    @Override
    public BigDecimal tongChuaDong(int hoId) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TONG_NO)) {
            ps.setInt(1, hoId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tinh no cua ho", e);
        }
    }
}
