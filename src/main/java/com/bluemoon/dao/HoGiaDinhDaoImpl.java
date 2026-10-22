package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.TomTatHo;
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
            "id, ma_can_ho, loai_can_ho, tang, dien_tich, so_dien_thoai, "
                    + "ngay_chuyen_den, trang_thai";
    private static final String SQL_THEM =
            "INSERT INTO ho_gia_dinh (ma_can_ho, loai_can_ho, tang, "
                    + "dien_tich, so_dien_thoai, ngay_chuyen_den, "
                    + "trang_thai) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SUA =
            "UPDATE ho_gia_dinh SET ma_can_ho = ?, loai_can_ho = ?, "
                    + "tang = ?, dien_tich = ?, so_dien_thoai = ?, "
                    + "ngay_chuyen_den = ? WHERE id = ?";
    private static final String SQL_DOI_TRANG_THAI =
            "UPDATE ho_gia_dinh SET trang_thai = ? WHERE id = ?";
    private static final String SQL_TIM_ID =
            "SELECT " + COT + " FROM ho_gia_dinh WHERE id = ?";
    private static final String SQL_TRUNG_MA =
            "SELECT 1 FROM ho_gia_dinh WHERE ma_can_ho = ? "
                    + "AND trang_thai = 'DANG_O' AND id <> ? LIMIT 1";
    private static final String SQL_TIM_KIEM =
            "SELECT h.id, h.ma_can_ho, h.loai_can_ho, h.tang, h.dien_tich, "
                    + "h.so_dien_thoai, h.ngay_chuyen_den, h.trang_thai, "
                    + "c.ho_ten AS ten_chu_ho, (SELECT COUNT(*) FROM "
                    + "nhan_khau n WHERE n.ho_id = h.id) AS so_thanh_vien "
                    + "FROM ho_gia_dinh h LEFT JOIN nhan_khau c "
                    + "ON c.ho_id = h.id AND c.quan_he = 'CHU_HO' "
                    + "WHERE (h.ma_can_ho LIKE ? OR c.ho_ten LIKE ?) "
                    + "AND (? OR h.trang_thai = 'DANG_O') "
                    + "ORDER BY h.ma_can_ho, h.ngay_chuyen_den DESC";

    @Override
    public int them(HoGiaDinh ho, NhanKhau chuHo) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int id = themHo(conn, ho);
                NhanKhauDaoImpl.them(conn, chuHo.voiHoId(id));
                conn.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Loi them ho gia dinh", e);
        }
    }

    private static int themHo(Connection conn, HoGiaDinh ho)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_THEM,
                Statement.RETURN_GENERATED_KEYS)) {
            ganThongTin(ps, ho);
            ps.setString(7, ho.getTrangThai().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Khong lay duoc id ho moi");
                }
                return keys.getInt(1);
            }
        }
    }

    @Override
    public void capNhat(HoGiaDinh ho) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_SUA)) {
            ganThongTin(ps, ho);
            ps.setInt(7, ho.getId());
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
    public List<TomTatHo> timKiem(String tuKhoa, boolean gomDaChuyenDi) {
        String mau = "%" + thoatLike(tuKhoa == null ? "" : tuKhoa.trim())
                + "%";
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TIM_KIEM)) {
            ps.setString(1, mau);
            ps.setString(2, mau);
            ps.setBoolean(3, gomDaChuyenDi);
            List<TomTatHo> ketQua = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(new TomTatHo(docDong(rs),
                            rs.getString("ten_chu_ho"),
                            rs.getInt("so_thanh_vien")));
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
        if (ho.getSoDienThoai() == null) {
            ps.setNull(5, Types.VARCHAR);
        } else {
            ps.setString(5, ho.getSoDienThoai());
        }
        ps.setDate(6, Date.valueOf(ho.getNgayChuyenDen()));
    }

    private static HoGiaDinh docDong(ResultSet rs) throws SQLException {
        return new HoGiaDinh(
                rs.getInt("id"),
                rs.getString("ma_can_ho"),
                LoaiCanHo.valueOf(rs.getString("loai_can_ho")),
                rs.getInt("tang"),
                rs.getBigDecimal("dien_tich"),
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
