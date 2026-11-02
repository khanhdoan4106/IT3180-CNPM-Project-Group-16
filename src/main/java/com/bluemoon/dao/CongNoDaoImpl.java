package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.PhaiNop;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            + "p.so_tien, p.nguoi_sua, p.ngay_sua, p.ghi_chu FROM phai_nop p "
            + "JOIN ho_gia_dinh h ON h.id = p.ho_id "
            + "JOIN khoan_phi k ON k.id = p.khoan_phi_id ";
    private static final String SQL_THEO_THANG = CHON
            + "JOIN dot_thu d ON d.id = p.dot_thu_id WHERE d.thang = ? "
            + "ORDER BY h.ma_can_ho, k.ten";
    private static final String SQL_THEO_ID = CHON + "WHERE p.id = ?";
    /** Sprint 3 them dieu kien chua co phieu thu con hieu luc. */
    private static final String SQL_TONG_NO =
            "SELECT COALESCE(SUM(so_tien), 0) FROM phai_nop WHERE ho_id = ?";
    private static final String SQL_SUA = "UPDATE phai_nop SET so_tien = ?, "
            + "nguoi_sua = ?, ngay_sua = NOW(), ghi_chu = ? WHERE id = ?";

    @Override
    public int chot(YearMonth thang, int nguoiChot, List<PhaiNop> khoan) {
        Date ngayDau = Date.valueOf(thang.atDay(1));
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int dotId = layDot(conn, ngayDau, nguoiChot);
                int soMoi = themKhoan(conn, dotId, khoan);
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
            List<PhaiNop> khoan) throws SQLException {
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
                soMoi += ps.executeUpdate();
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

    @Override
    public void suaSoTien(int id, BigDecimal soTien, int nguoiSua,
            String ghiChu) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SUA)) {
            ps.setBigDecimal(1, soTien);
            ps.setInt(2, nguoiSua);
            if (ghiChu == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, ghiChu);
            }
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Loi sua khoan phai nop", e);
        }
    }

    @Override
    public boolean daCoPhieuThu(int id) {
        // Sprint 3 thay bang truy van bang phieu_thu.
        return false;
    }

    private static PhaiNop doc(ResultSet rs) throws SQLException {
        int nguoiSua = rs.getInt("nguoi_sua");
        Integer nguoi = rs.wasNull() ? null : nguoiSua;
        Timestamp ngay = rs.getTimestamp("ngay_sua");
        return new PhaiNop(rs.getInt("id"), rs.getInt("ho_id"),
                rs.getString("ma_can_ho"), rs.getInt("khoan_phi_id"),
                rs.getString("ten"), rs.getBigDecimal("dien_tich"),
                rs.getBigDecimal("don_gia"), rs.getBigDecimal("so_tien_goc"),
                rs.getBigDecimal("so_tien"), nguoi,
                ngay == null ? null : ngay.toLocalDateTime(),
                rs.getString("ghi_chu"));
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
