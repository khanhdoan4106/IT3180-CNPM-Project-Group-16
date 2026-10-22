package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Cai dat JDBC cua {@link TaiKhoanDao}. Luon dung PreparedStatement.
 */
public class TaiKhoanDaoImpl implements TaiKhoanDao {

    private static final String SQL_TIM =
            "SELECT id, ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro, "
                    + "hoat_dong FROM tai_khoan WHERE ten_dang_nhap = ?";
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
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(new TaiKhoan(
                        rs.getInt("id"),
                        rs.getString("ten_dang_nhap"),
                        rs.getString("mat_khau_bam"),
                        rs.getString("ho_ten"),
                        VaiTro.valueOf(rs.getString("vai_tro")),
                        rs.getBoolean("hoat_dong")));
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tim tai khoan", e);
        }
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
