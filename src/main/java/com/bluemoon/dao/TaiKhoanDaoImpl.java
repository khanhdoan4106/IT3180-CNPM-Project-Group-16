package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.VaiTro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Cai dat JDBC cua {@link TaiKhoanDao}. Luon dung PreparedStatement.
 */
public class TaiKhoanDaoImpl implements TaiKhoanDao {

    private static final String COT = "SELECT id, ten_dang_nhap, "
            + "mat_khau_bam, ho_ten, vai_tro, hoat_dong, so_dien_thoai "
            + "FROM tai_khoan ";
    private static final String SQL_TIM = COT + "WHERE ten_dang_nhap = ?";
    private static final String SQL_THEO_ID = COT + "WHERE id = ?";
    private static final String SQL_KHOA_ID = SQL_THEO_ID + " FOR UPDATE";
    private static final String SQL_DANH_SACH = COT + "ORDER BY ten_dang_nhap";
    private static final String SQL_THONG_TIN = "UPDATE tai_khoan SET "
            + "ho_ten = ?, so_dien_thoai = ? WHERE id = ?";
    private static final String SQL_VAI_TRO =
            "UPDATE tai_khoan SET vai_tro = ? WHERE id = ?";
    private static final String SQL_HOAT_DONG =
            "UPDATE tai_khoan SET hoat_dong = ? WHERE id = ?";
    private static final String SQL_DOI_MAT_KHAU =
            "UPDATE tai_khoan SET mat_khau_bam = ? WHERE id = ?";
    private static final String SQL_THEM =
            "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau_bam, ho_ten, "
                    + "vai_tro, hoat_dong) VALUES (?, ?, ?, ?, ?)";

    @Override
    public Optional<TaiKhoan> timTheoTenDangNhap(String tenDangNhap) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TIM)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(doc(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tim tai khoan", e);
        }
    }

    @Override
    public Optional<TaiKhoan> timTheoId(int id) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEO_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(doc(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tim tai khoan", e);
        }
    }

    @Override
    public List<TaiKhoan> danhSach() {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DANH_SACH);
                ResultSet rs = ps.executeQuery()) {
            List<TaiKhoan> kq = new ArrayList<>();
            while (rs.next()) {
                kq.add(doc(rs));
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi lay danh sach tai khoan", e);
        }
    }

    @Override
    public void capNhatThongTin(int id, String hoTen, String soDienThoai) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THONG_TIN)) {
            ps.setString(1, hoTen);
            if (soDienThoai == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, soDienThoai);
            }
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Loi cap nhat thong tin ca nhan", e);
        }
    }

    @Override
    public void capNhatVaiTro(int id, VaiTro vaiTro, int nguoiId) {
        capNhatCoNhatKy(SQL_VAI_TRO, id, vaiTro.name(), nguoiId,
                "Loi doi vai tro", cu -> new NhatKyDong(ThaoTac.DOI_VAI_TRO,
                        "Vai trò: " + cu.getVaiTro().getNhan() + " → "
                                + vaiTro.getNhan()));
    }

    @Override
    public void capNhatHoatDong(int id, boolean hoatDong, int nguoiId) {
        capNhatCoNhatKy(SQL_HOAT_DONG, id, hoatDong, nguoiId,
                "Loi khoa tai khoan", cu -> new NhatKyDong(hoatDong
                        ? ThaoTac.MO_KHOA_TAI_KHOAN : ThaoTac.KHOA_TAI_KHOAN,
                        "Trạng thái: " + trangThai(cu.isHoatDong()) + " → "
                                + trangThai(hoatDong)));
    }

    @Override
    public void datLaiMatKhau(int id, String matKhauBam, int nguoiId) {
        // Khong bao gio dua mat khau hay ma bam vao nhat ky.
        capNhatCoNhatKy(SQL_DOI_MAT_KHAU, id, matKhauBam, nguoiId,
                "Loi dat lai mat khau", cu -> new NhatKyDong(
                        ThaoTac.DAT_LAI_MAT_KHAU, "Đặt mật khẩu tạm"));
    }

    private static String trangThai(boolean hoatDong) {
        return hoatDong ? "Hoạt động" : "Đã khóa";
    }

    /** Doi tuong nhat ky cua mot tai khoan, vd "Tài khoản thuquy1". */
    private static String doiTuong(String tenDangNhap) {
        return "Tài khoản " + tenDangNhap;
    }

    /** Thao tac va noi dung nhat ky, tinh tu tai khoan truoc khi doi. */
    private record NhatKyDong(ThaoTac thaoTac, String noiDung) {
    }

    /**
     * Doi mot cot trong mot giao dich: khoa dong tai khoan, doc gia tri cu,
     * cap nhat roi ghi nhat ky.
     */
    private static void capNhatCoNhatKy(String sql, int id, Object giaTri,
            int nguoiId, String loi,
            Function<TaiKhoan, NhatKyDong> nhatKy) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                TaiKhoan cu;
                try (PreparedStatement ps =
                        conn.prepareStatement(SQL_KHOA_ID)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new XungDotException(
                                    XungDotException.Loai.KHONG_TIM_THAY,
                                    "tai khoan " + id);
                        }
                        cu = doc(rs);
                    }
                }
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setObject(1, giaTri);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
                NhatKyDong dong = nhatKy.apply(cu);
                NhatKyDaoImpl.ghi(conn, nguoiId, dong.thaoTac(),
                        doiTuong(cu.getTenDangNhap()), dong.noiDung());
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException(loi, e);
        }
    }

    private static TaiKhoan doc(ResultSet rs) throws SQLException {
        return new TaiKhoan(
                rs.getInt("id"),
                rs.getString("ten_dang_nhap"),
                rs.getString("mat_khau_bam"),
                rs.getString("ho_ten"),
                VaiTro.valueOf(rs.getString("vai_tro")),
                rs.getBoolean("hoat_dong"),
                rs.getString("so_dien_thoai"));
    }

    @Override
    public void capNhatMatKhau(int id, String matKhauBam) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL_DOI_MAT_KHAU)) {
            ps.setString(1, matKhauBam);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Loi doi mat khau", e);
        }
    }

    @Override
    public int them(TaiKhoan tk, int nguoiId) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = conn.prepareStatement(SQL_THEM,
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, tk.getTenDangNhap());
                    ps.setString(2, tk.getMatKhauBam());
                    ps.setString(3, tk.getHoTen());
                    ps.setString(4, tk.getVaiTro().name());
                    ps.setBoolean(5, tk.isHoatDong());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "Khong lay duoc id tai khoan moi");
                        }
                        id = keys.getInt(1);
                    }
                }
                // Chi ghi ho ten va vai tro, khong ghi mat khau.
                NhatKyDaoImpl.ghi(conn, nguoiId, ThaoTac.TAO_TAI_KHOAN,
                        doiTuong(tk.getTenDangNhap()), "Họ tên: "
                        + tk.getHoTen() + "; vai trò: "
                        + tk.getVaiTro().getNhan());
                conn.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw TrungKhoaException.boc("Loi them tai khoan", e);
        }
    }
}
