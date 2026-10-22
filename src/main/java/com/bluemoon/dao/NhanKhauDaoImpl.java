package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Cai dat JDBC cua {@link NhanKhauDao}. Luon dung PreparedStatement.
 */
public class NhanKhauDaoImpl implements NhanKhauDao {

    private static final String COT =
            "id, ho_id, ho_ten, ngay_sinh, gioi_tinh, so_cccd, quan_he";
    private static final String SQL_THEM =
            "INSERT INTO nhan_khau (ho_ten, ngay_sinh, gioi_tinh, so_cccd, "
                    + "quan_he, ho_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_SUA =
            "UPDATE nhan_khau SET ho_ten = ?, ngay_sinh = ?, gioi_tinh = ?, "
                    + "so_cccd = ?, quan_he = ? WHERE id = ?";
    private static final String SQL_TIM_ID =
            "SELECT " + COT + " FROM nhan_khau WHERE id = ?";
    private static final String SQL_THEO_HO =
            "SELECT " + COT + " FROM nhan_khau WHERE ho_id = ? "
                    + "ORDER BY quan_he = 'CHU_HO' DESC, ngay_sinh, id";
    private static final String SQL_TRUNG_CCCD =
            "SELECT 1 FROM nhan_khau WHERE so_cccd = ? AND id <> ? LIMIT 1";
    private static final String SQL_CO_CHU_HO =
            "SELECT 1 FROM nhan_khau WHERE ho_id = ? AND quan_he = 'CHU_HO' "
                    + "AND id <> ? LIMIT 1";

    @Override
    public int them(NhanKhau nk) {
        try (Connection conn = Database.getConnection()) {
            return them(conn, nk);
        } catch (SQLException e) {
            throw new DaoException("Loi them nhan khau", e);
        }
    }

    /**
     * Them nhan khau tren ket noi co san, de goi trong giao dich them ho
     * cung chu ho.
     */
    static int them(Connection conn, NhanKhau nk) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_THEM,
                Statement.RETURN_GENERATED_KEYS)) {
            ganThongTin(ps, nk);
            ps.setInt(6, nk.getHoId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Khong lay duoc id nhan khau moi");
                }
                return keys.getInt(1);
            }
        }
    }

    @Override
    public void capNhat(NhanKhau nk) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SUA)) {
            ganThongTin(ps, nk);
            ps.setInt(6, nk.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Loi cap nhat nhan khau", e);
        }
    }

    @Override
    public Optional<NhanKhau> timTheoId(int id) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TIM_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(docDong(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tim nhan khau", e);
        }
    }

    @Override
    public List<NhanKhau> theoHo(int hoId) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEO_HO)) {
            ps.setInt(1, hoId);
            List<NhanKhau> ketQua = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(docDong(rs));
                }
            }
            return ketQua;
        } catch (SQLException e) {
            throw new DaoException("Loi lay nhan khau cua ho", e);
        }
    }

    @Override
    public boolean tonTaiCccd(String soCccd, int boQuaId) {
        return coDong(SQL_TRUNG_CCCD, soCccd, boQuaId,
                "Loi kiem tra so CCCD");
    }

    @Override
    public boolean coChuHo(int hoId, int boQuaId) {
        return coDong(SQL_CO_CHU_HO, hoId, boQuaId,
                "Loi kiem tra chu ho");
    }

    private static boolean coDong(String sql, Object khoa, int boQuaId,
            String loi) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, khoa);
            ps.setInt(2, boQuaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException(loi, e);
        }
    }

    private static void ganThongTin(PreparedStatement ps, NhanKhau nk)
            throws SQLException {
        ps.setString(1, nk.getHoTen());
        ps.setDate(2, Date.valueOf(nk.getNgaySinh()));
        ps.setString(3, nk.getGioiTinh().name());
        if (nk.getSoCccd() == null) {
            ps.setNull(4, Types.CHAR);
        } else {
            ps.setString(4, nk.getSoCccd());
        }
        ps.setString(5, nk.getQuanHe().name());
    }

    private static NhanKhau docDong(ResultSet rs) throws SQLException {
        return new NhanKhau(
                rs.getInt("id"),
                rs.getInt("ho_id"),
                rs.getString("ho_ten"),
                rs.getDate("ngay_sinh").toLocalDate(),
                GioiTinh.valueOf(rs.getString("gioi_tinh")),
                rs.getString("so_cccd"),
                QuanHeChuHo.valueOf(rs.getString("quan_he")));
    }
}
