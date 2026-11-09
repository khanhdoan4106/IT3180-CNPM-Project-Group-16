package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.LoaiBienDong;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TrangThaiNhanKhau;
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
            "id, ho_id, ho_ten, ngay_sinh, gioi_tinh, so_cccd, quan_he, "
                    + "trang_thai, loai_cu_tru, noi_thuong_tru";
    private static final String SQL_THEM =
            "INSERT INTO nhan_khau (ho_ten, ngay_sinh, gioi_tinh, so_cccd, "
                    + "quan_he, noi_thuong_tru, ho_id, trang_thai, "
                    + "loai_cu_tru) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_SUA =
            "UPDATE nhan_khau SET ho_ten = ?, ngay_sinh = ?, gioi_tinh = ?, "
                    + "so_cccd = ?, quan_he = ?, noi_thuong_tru = ? "
                    + "WHERE id = ?";
    private static final String SQL_DOI_TRANG_THAI =
            "UPDATE nhan_khau SET trang_thai = ? WHERE id = ?";
    private static final String SQL_KHOA = "SELECT ho_id, trang_thai "
            + "FROM nhan_khau WHERE id = ? FOR UPDATE";
    private static final String SQL_KHOA_HAI = "SELECT id, ho_id, ho_ten, "
            + "trang_thai, quan_he FROM nhan_khau WHERE id IN (?, ?) "
            + "FOR UPDATE";
    private static final String SQL_CHU_HO_CU = "UPDATE nhan_khau SET "
            + "quan_he = ?, trang_thai = ? WHERE id = ?";
    private static final String SQL_CHU_HO_MOI =
            "UPDATE nhan_khau SET quan_he = 'CHU_HO' WHERE id = ?";
    private static final String SQL_TIM_ID =
            "SELECT " + COT + " FROM nhan_khau WHERE id = ?";
    private static final String SQL_THEO_HO =
            "SELECT " + COT + " FROM nhan_khau WHERE ho_id = ? "
                    + "ORDER BY trang_thai = 'DANG_O' DESC, "
                    + "quan_he = 'CHU_HO' DESC, ngay_sinh, id";
    private static final String SQL_TRUNG_CCCD =
            "SELECT 1 FROM nhan_khau WHERE so_cccd = ? "
                    + "AND trang_thai = 'DANG_O' AND id <> ? LIMIT 1";
    private static final String SQL_CO_CHU_HO =
            "SELECT 1 FROM nhan_khau WHERE ho_id = ? AND quan_he = 'CHU_HO' "
                    + "AND id <> ? LIMIT 1";

    @Override
    public int them(NhanKhau nk, int nguoiGhi) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int id = them(conn, nk);
                ghiChuyenDen(conn, nk, id, nguoiGhi);
                conn.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw TrungKhoaException.boc("Loi them nhan khau", e);
        }
    }

    private static void ghiChuyenDen(Connection conn, NhanKhau nk, int id,
            int nguoiGhi) throws SQLException {
        BienDongDaoImpl.ghi(conn, nk.getHoId(), id, LoaiBienDong.CHUYEN_DEN,
                null, "Thêm vào hộ, quan hệ: " + nk.getQuanHe().getNhan(),
                nguoiGhi);
    }

    @Override
    public int themKemDot(NhanKhau nk, DotCuTru dot, int nguoiGhi) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int id = them(conn, nk);
                ghiChuyenDen(conn, nk, id, nguoiGhi);
                DotCuTru dau = dot.voiNhanKhauId(id);
                DotCuTruDaoImpl.them(conn, dau);
                BienDongDaoImpl.ghiDot(conn, nk.getHoId(), dau, nguoiGhi);
                conn.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw TrungKhoaException.boc("Loi them nhan khau tam tru", e);
        }
    }

    /**
     * Them nhan khau tren ket noi co san, de goi trong giao dich them ho
     * cung chu ho hoac them nguoi tam tru cung dot dau tien.
     */
    static int them(Connection conn, NhanKhau nk) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_THEM,
                Statement.RETURN_GENERATED_KEYS)) {
            ganThongTin(ps, nk);
            ps.setInt(7, nk.getHoId());
            ps.setString(8, nk.getTrangThai().name());
            ps.setString(9, nk.getLoaiCuTru().name());
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
            ps.setInt(7, nk.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw TrungKhoaException.boc("Loi cap nhat nhan khau", e);
        }
    }

    @Override
    public void capNhatTrangThai(int id, TrangThaiNhanKhau trangThai,
            int nguoiGhi) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement khoa = conn.prepareStatement(SQL_KHOA);
                    PreparedStatement ps =
                            conn.prepareStatement(SQL_DOI_TRANG_THAI)) {
                khoa.setInt(1, id);
                int hoId;
                boolean dangO;
                try (ResultSet rs = khoa.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Khong tim thay nhan khau");
                    }
                    hoId = rs.getInt("ho_id");
                    dangO = "DANG_O".equals(rs.getString("trang_thai"));
                }
                ps.setString(1, trangThai.name());
                ps.setInt(2, id);
                ps.executeUpdate();
                if (dangO && trangThai == TrangThaiNhanKhau.DA_CHUYEN_DI) {
                    BienDongDaoImpl.ghi(conn, hoId, id,
                            LoaiBienDong.CHUYEN_DI, null, "Rời hộ", nguoiGhi);
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Loi doi trang thai nhan khau", e);
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
        if (nk.getNoiThuongTru() == null) {
            ps.setNull(6, Types.VARCHAR);
        } else {
            ps.setString(6, nk.getNoiThuongTru());
        }
    }

    private static NhanKhau docDong(ResultSet rs) throws SQLException {
        return new NhanKhau(
                rs.getInt("id"),
                rs.getInt("ho_id"),
                rs.getString("ho_ten"),
                rs.getDate("ngay_sinh").toLocalDate(),
                GioiTinh.valueOf(rs.getString("gioi_tinh")),
                rs.getString("so_cccd"),
                QuanHeChuHo.valueOf(rs.getString("quan_he")),
                TrangThaiNhanKhau.valueOf(rs.getString("trang_thai")),
                LoaiCuTru.valueOf(rs.getString("loai_cu_tru")),
                rs.getString("noi_thuong_tru"));
    }

    @Override
    public void doiChuHo(int chuHoCuId, int chuHoMoiId, QuanHeChuHo quanHeCu,
            boolean cuChuyenDi, int nguoiGhi) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement khoa = conn.prepareStatement(SQL_KHOA_HAI);
                    PreparedStatement cu = conn.prepareStatement(SQL_CHU_HO_CU);
                    PreparedStatement moi =
                            conn.prepareStatement(SQL_CHU_HO_MOI)) {
                khoa.setInt(1, chuHoCuId);
                khoa.setInt(2, chuHoMoiId);
                int hoId = 0;
                String tenCu = "";
                try (ResultSet rs = khoa.executeQuery()) {
                    while (rs.next()) {
                        hoId = rs.getInt("ho_id");
                        boolean dangO = TrangThaiNhanKhau.DANG_O.name()
                                .equals(rs.getString("trang_thai"));
                        if (rs.getInt("id") == chuHoCuId) {
                            tenCu = rs.getString("ho_ten");
                            // Duoi khoa: chu ho cu van la chu ho dang o.
                            if (!dangO || !QuanHeChuHo.CHU_HO.name().equals(
                                    rs.getString("quan_he"))) {
                                throw new XungDotException(
                                        XungDotException.Loai.NHAN_KHAU_DOI,
                                        "chu ho cu " + chuHoCuId);
                            }
                        } else if (!dangO) {
                            throw new XungDotException(
                                    XungDotException.Loai.NHAN_KHAU_DOI,
                                    "chu ho moi " + chuHoMoiId);
                        }
                    }
                }
                cu.setString(1, quanHeCu.name());
                cu.setString(2, cuChuyenDi ? TrangThaiNhanKhau.DA_CHUYEN_DI
                        .name() : TrangThaiNhanKhau.DANG_O.name());
                cu.setInt(3, chuHoCuId);
                cu.executeUpdate();
                moi.setInt(1, chuHoMoiId);
                moi.executeUpdate();
                BienDongDaoImpl.ghi(conn, hoId, chuHoMoiId,
                        LoaiBienDong.DOI_CHU_HO, null,
                        "Làm chủ hộ thay " + tenCu, nguoiGhi);
                if (cuChuyenDi) {
                    BienDongDaoImpl.ghi(conn, hoId, chuHoCuId,
                            LoaiBienDong.CHUYEN_DI, null,
                            "Chủ hộ cũ rời hộ", nguoiGhi);
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw TrungKhoaException.boc("Loi doi chu ho", e);
        }
    }
}
