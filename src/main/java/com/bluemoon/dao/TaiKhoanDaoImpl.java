package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.TaiKhoan;
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

/**
 * Cai dat JDBC cua {@link TaiKhoanDao}. Luon dung PreparedStatement.
 */
public class TaiKhoanDaoImpl implements TaiKhoanDao {

    private static final String COT = "SELECT id, ten_dang_nhap, "
            + "mat_khau_bam, ho_ten, vai_tro, hoat_dong, so_dien_thoai "
            + "FROM tai_khoan ";
    private static final String SQL_TIM = COT + "WHERE ten_dang_nhap = ?";
    private static final String SQL_THEO_ID = COT + "WHERE id = ?";
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
    public void capNhatVaiTro(int id, VaiTro vaiTro) {
        capNhat(SQL_VAI_TRO, id, vaiTro.name(), "Loi doi vai tro");
    }

    @Override
    public void capNhatHoatDong(int id, boolean hoatDong) {
        capNhat(SQL_HOAT_DONG, id, hoatDong, "Loi khoa tai khoan");
    }

    private static void capNhat(String sql, int id, Object giaTri,
            String loi) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, giaTri);
            ps.setInt(2, id);
            ps.executeUpdate();
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
    public int them(TaiKhoan tk) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEM,
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhauBam());
            ps.setString(3, tk.getHoTen());
            ps.setString(4, tk.getVaiTro().name());
            ps.setBoolean(5, tk.isHoatDong());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Khong lay duoc id tai khoan moi");
                }
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw TrungKhoaException.boc("Loi them tai khoan", e);
        }
    }
}
