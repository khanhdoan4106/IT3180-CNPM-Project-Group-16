package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.TrangThaiHo;
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
 * Cai dat JDBC cua {@link HoGiaDinhDao}. Luon dung PreparedStatement.
 */
public class HoGiaDinhDaoImpl implements HoGiaDinhDao {

    private static final String COT =
            "id, ma_can_ho, loai_can_ho, tang, dien_tich, ten_chu_ho, "
                    + "so_dien_thoai, ngay_chuyen_den, trang_thai";
    private static final String SQL_THEM =
            "INSERT INTO ho_gia_dinh (ma_can_ho, loai_can_ho, tang, "
                    + "dien_tich, ten_chu_ho, so_dien_thoai, "
                    + "ngay_chuyen_den, trang_thai) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SUA =
            "UPDATE ho_gia_dinh SET ma_can_ho = ?, loai_can_ho = ?, "
                    + "tang = ?, dien_tich = ?, ten_chu_ho = ?, "
                    + "so_dien_thoai = ?, ngay_chuyen_den = ? WHERE id = ?";
    private static final String SQL_DOI_TRANG_THAI =
            "UPDATE ho_gia_dinh SET trang_thai = ? WHERE id = ?";
    private static final String SQL_TIM_ID =
            "SELECT " + COT + " FROM ho_gia_dinh WHERE id = ?";
    private static final String SQL_TRUNG_MA =
            "SELECT 1 FROM ho_gia_dinh WHERE ma_can_ho = ? "
                    + "AND trang_thai = 'DANG_O' AND id <> ? LIMIT 1";
    private static final String SQL_TIM_KIEM =
            "SELECT " + COT + " FROM ho_gia_dinh "
                    + "WHERE (ma_can_ho LIKE ? OR ten_chu_ho LIKE ?) "
                    + "AND (? OR trang_thai = 'DANG_O') "
                    + "ORDER BY ma_can_ho, ngay_chuyen_den DESC";

    @Override
    public int them(HoGiaDinh ho) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEM,
                        Statement.RETURN_GENERATED_KEYS)) {
            ganThongTin(ps, ho);
            ps.setString(8, ho.getTrangThai().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new DaoException("Khong lay duoc id ho moi", null);
                }
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Loi them ho gia dinh", e);
        }
    }

    @Override
    public void capNhat(HoGiaDinh ho) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SUA)) {
            ganThongTin(ps, ho);
            ps.setInt(8, ho.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Loi cap nhat ho gia dinh", e);
        }
    }

    @Override
    public void capNhatTrangThai(int id, TrangThaiHo trangThai) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL_DOI_TRANG_THAI)) {
            ps.setString(1, trangThai.name());
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Loi doi trang thai ho", e);
        }
    }

    @Override
    public Optional<HoGiaDinh> timTheoId(int id) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TIM_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(docDong(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tim ho gia dinh", e);
        }
    }

    @Override
    public boolean tonTaiMaDangO(String maCanHo, int boQuaId) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TRUNG_MA)) {
            ps.setString(1, maCanHo);
            ps.setInt(2, boQuaId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi kiem tra ma can ho", e);
        }
    }

    @Override
    public List<HoGiaDinh> timKiem(String tuKhoa, boolean gomDaChuyenDi) {
        String mau = "%" + thoatLike(tuKhoa == null ? "" : tuKhoa.trim())
                + "%";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TIM_KIEM)) {
            ps.setString(1, mau);
            ps.setString(2, mau);
            ps.setBoolean(3, gomDaChuyenDi);
            List<HoGiaDinh> ketQua = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(docDong(rs));
                }
            }
            return ketQua;
        } catch (SQLException e) {
            throw new DaoException("Loi tim kiem ho gia dinh", e);
        }
    }

    private static void ganThongTin(PreparedStatement ps, HoGiaDinh ho)
            throws SQLException {
        ps.setString(1, ho.getMaCanHo());
        ps.setString(2, ho.getLoaiCanHo().name());
        ps.setInt(3, ho.getTang());
        ps.setBigDecimal(4, ho.getDienTich());
        ps.setString(5, ho.getTenChuHo());
        if (ho.getSoDienThoai() == null) {
            ps.setNull(6, Types.VARCHAR);
        } else {
            ps.setString(6, ho.getSoDienThoai());
        }
        ps.setDate(7, Date.valueOf(ho.getNgayChuyenDen()));
    }

    private static HoGiaDinh docDong(ResultSet rs) throws SQLException {
        return new HoGiaDinh(
                rs.getInt("id"),
                rs.getString("ma_can_ho"),
                LoaiCanHo.valueOf(rs.getString("loai_can_ho")),
                rs.getInt("tang"),
                rs.getBigDecimal("dien_tich"),
                rs.getString("ten_chu_ho"),
                rs.getString("so_dien_thoai"),
                rs.getDate("ngay_chuyen_den").toLocalDate(),
                TrangThaiHo.valueOf(rs.getString("trang_thai")));
    }

    /** Thoat ky tu dac biet cua LIKE de tu khoa duoc tim nguyen van. */
    private static String thoatLike(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%")
                .replace("_", "\\_");
    }
}
