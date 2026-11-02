package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.BienDong;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.LoaiBienDong;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Cai dat JDBC cua {@link BienDongDao} va ham ghi dung chung cho cac DAO
 * khac (ghi trong giao dich cua thao tac goc).
 */
public class BienDongDaoImpl implements BienDongDao {

    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String SQL_GHI = "INSERT INTO bien_dong_nhan_khau "
            + "(ho_id, nhan_khau_id, loai, ngay, ghi_chu, nguoi_ghi) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
    /** Ghi chuyen di cho moi thanh vien dang o cua ho (truoc khi doi). */
    private static final String SQL_GHI_CA_HO = "INSERT INTO "
            + "bien_dong_nhan_khau (ho_id, nhan_khau_id, loai, ngay, ghi_chu,"
            + " nguoi_ghi) SELECT ho_id, id, 'CHUYEN_DI', CURDATE(), ?, ? "
            + "FROM nhan_khau WHERE ho_id = ? AND trang_thai = 'DANG_O'";
    private static final String CHON = "SELECT b.id, b.ho_id, h.ma_can_ho, "
            + "b.nhan_khau_id, n.ho_ten, b.loai, b.ngay, b.ghi_chu, "
            + "t.ho_ten AS nguoi_ghi, b.thoi_diem FROM bien_dong_nhan_khau b "
            + "JOIN ho_gia_dinh h ON h.id = b.ho_id "
            + "JOIN nhan_khau n ON n.id = b.nhan_khau_id "
            + "JOIN tai_khoan t ON t.id = b.nguoi_ghi ";
    private static final String SQL_THEO_HO = CHON + "WHERE b.ho_id = ? "
            + "ORDER BY b.ngay DESC, b.id DESC";
    private static final String SQL_GAN_DAY = CHON
            + "ORDER BY b.thoi_diem DESC, b.id DESC LIMIT ?";

    /**
     * Ghi mot bien dong tren ket noi dang trong giao dich.
     *
     * @param conn ket noi cua giao dich goc
     * @param hoId ho
     * @param nhanKhauId nhan khau
     * @param loai loai bien dong
     * @param ngay ngay bien dong; null la hom nay
     * @param ghiChu ghi chu, co the null
     * @param nguoiGhi id tai khoan ghi nhan
     * @throws SQLException loi CSDL, de giao dich goc hoan tac
     */
    static void ghi(Connection conn, int hoId, int nhanKhauId,
            LoaiBienDong loai, LocalDate ngay, String ghiChu, int nguoiGhi)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_GHI)) {
            ps.setInt(1, hoId);
            ps.setInt(2, nhanKhauId);
            ps.setString(3, loai.name());
            ps.setDate(4, Date.valueOf(ngay == null ? LocalDate.now() : ngay));
            if (ghiChu == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, ghiChu);
            }
            ps.setInt(6, nguoiGhi);
            ps.executeUpdate();
        }
    }

    /**
     * Ghi bien dong tam tru/tam vang cua mot dot.
     *
     * @param conn ket noi cua giao dich goc
     * @param hoId ho cua nhan khau
     * @param dot dot vua them, da co nhanKhauId
     * @param nguoiGhi id tai khoan ghi nhan
     * @throws SQLException loi CSDL
     */
    static void ghiDot(Connection conn, int hoId, DotCuTru dot, int nguoiGhi)
            throws SQLException {
        String ghiChu = "Đến " + dot.getDenNgay().format(NGAY)
                + (dot.getLyDo() == null || dot.getLyDo().isBlank() ? ""
                        : ". " + dot.getLyDo());
        ghi(conn, hoId, dot.getNhanKhauId(),
                LoaiBienDong.valueOf(dot.getLoai().name()), dot.getTuNgay(),
                ghiChu.length() > 200 ? ghiChu.substring(0, 200) : ghiChu,
                nguoiGhi);
    }

    /**
     * Ghi chuyen di cho moi thanh vien dang o cua ho. Goi truoc khi doi
     * trang thai thanh vien.
     *
     * @param conn ket noi cua giao dich goc
     * @param hoId ho chuyen di
     * @param ghiChu ghi chu
     * @param nguoiGhi id tai khoan ghi nhan
     * @throws SQLException loi CSDL
     */
    static void ghiCaHoChuyenDi(Connection conn, int hoId, String ghiChu,
            int nguoiGhi) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_GHI_CA_HO)) {
            ps.setString(1, ghiChu);
            ps.setInt(2, nguoiGhi);
            ps.setInt(3, hoId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<BienDong> theoHo(int hoId) {
        return doc(SQL_THEO_HO, hoId, "Loi lay lich su bien dong cua ho");
    }

    @Override
    public List<BienDong> ganDay(int soDong) {
        return doc(SQL_GAN_DAY, soDong, "Loi lay bien dong gan day");
    }

    private static List<BienDong> doc(String sql, int thamSo, String loi) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, thamSo);
            List<BienDong> kq = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(new BienDong(rs.getInt("id"), rs.getInt("ho_id"),
                            rs.getString("ma_can_ho"),
                            rs.getInt("nhan_khau_id"),
                            rs.getString("ho_ten"),
                            LoaiBienDong.valueOf(rs.getString("loai")),
                            rs.getDate("ngay").toLocalDate(),
                            rs.getString("ghi_chu"),
                            rs.getString("nguoi_ghi"),
                            rs.getTimestamp("thoi_diem").toLocalDateTime()));
                }
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException(loi, e);
        }
    }
}
