package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.NhatKy;
import com.bluemoon.model.ThaoTac;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cai dat JDBC cua {@link NhatKyDao} va ham ghi dung chung cho cac DAO
 * khac, goi tren ket noi cua giao dich goc (giong
 * {@link BienDongDaoImpl}): thao tac loi hoac hoan tac thi khong con dong
 * nhat ky nao.
 */
public class NhatKyDaoImpl implements NhatKyDao {

    /** Do dai toi da cot doi_tuong. */
    static final int DOI_TUONG_TOI_DA = 150;
    /** Do dai toi da cot noi_dung. */
    static final int NOI_DUNG_TOI_DA = 1000;

    private static final String SQL_GHI = "INSERT INTO nhat_ky "
            + "(tai_khoan_id, thao_tac, doi_tuong, noi_dung) "
            + "VALUES (?, ?, ?, ?)";
    private static final String SQL_THEO_KHOANG = "SELECT n.id, "
            + "n.thoi_diem, n.tai_khoan_id, t.ho_ten, t.ten_dang_nhap, "
            + "n.thao_tac, n.doi_tuong, n.noi_dung FROM nhat_ky n "
            + "JOIN tai_khoan t ON t.id = n.tai_khoan_id "
            + "WHERE n.thoi_diem >= ? AND n.thoi_diem < ? "
            + "AND (? IS NULL OR n.thao_tac = ?) "
            + "ORDER BY n.thoi_diem DESC, n.id DESC";

    /**
     * Ghi mot dong nhat ky tren ket noi dang trong giao dich. Thoi diem do
     * CSDL dien (phien MySQL dat +07:00).
     *
     * @param conn ket noi cua giao dich goc
     * @param nguoiId id tai khoan lam thao tac
     * @param thaoTac loai thao tac
     * @param doiTuong doi tuong, vd "Hộ A-1203"
     * @param noiDung noi dung thay doi, co the null; khong chua mat khau
     * @throws SQLException loi CSDL, de giao dich goc hoan tac
     */
    static void ghi(Connection conn, int nguoiId, ThaoTac thaoTac,
            String doiTuong, String noiDung) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_GHI)) {
            ps.setInt(1, nguoiId);
            ps.setString(2, thaoTac.getMa());
            ps.setString(3, catNgan(doiTuong, DOI_TUONG_TOI_DA));
            if (noiDung == null || noiDung.isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, catNgan(noiDung, NOI_DUNG_TOI_DA));
            }
            ps.executeUpdate();
        }
    }

    private static String catNgan(String s, int toiDa) {
        return s.length() <= toiDa ? s : s.substring(0, toiDa - 1) + "…";
    }

    @Override
    public List<NhatKy> theoKhoang(LocalDate tuNgay, LocalDate denNgay,
            ThaoTac thaoTac) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL_THEO_KHOANG)) {
            ps.setObject(1, tuNgay.atStartOfDay());
            ps.setObject(2, denNgay.plusDays(1).atStartOfDay());
            String ma = thaoTac == null ? null : thaoTac.getMa();
            ps.setString(3, ma);
            ps.setString(4, ma);
            List<NhatKy> kq = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(new NhatKy(rs.getInt("id"),
                            rs.getObject("thoi_diem", LocalDateTime.class),
                            rs.getInt("tai_khoan_id"),
                            rs.getString("ho_ten"),
                            rs.getString("ten_dang_nhap"),
                            ThaoTac.valueOf(rs.getString("thao_tac")),
                            rs.getString("doi_tuong"),
                            rs.getString("noi_dung")));
                }
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi doc nhat ky thao tac", e);
        }
    }
}
