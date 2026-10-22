package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Cai dat JDBC cua {@link DotCuTruDao}. Luon dung PreparedStatement.
 */
public class DotCuTruDaoImpl implements DotCuTruDao {

    private static final String COT =
            "t.id, t.nhan_khau_id, t.loai, t.tu_ngay, t.den_ngay, t.ly_do";
    private static final String SQL_THEM =
            "INSERT INTO tam_tru_tam_vang (nhan_khau_id, loai, tu_ngay, "
                    + "den_ngay, ly_do) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_KHOA_NHAN_KHAU =
            "SELECT id FROM nhan_khau WHERE id = ? FOR UPDATE";
    private static final String SQL_CHONG_NGAY =
            "SELECT 1 FROM tam_tru_tam_vang WHERE nhan_khau_id = ? "
                    + "AND tu_ngay <= ? AND ? <= den_ngay LIMIT 1";
    private static final String SQL_THEO_NHAN_KHAU =
            "SELECT " + COT + " FROM tam_tru_tam_vang t "
                    + "WHERE t.nhan_khau_id = ? ORDER BY t.tu_ngay DESC";
    private static final String SQL_THEO_HO =
            "SELECT " + COT + " FROM tam_tru_tam_vang t "
                    + "JOIN nhan_khau n ON n.id = t.nhan_khau_id "
                    + "WHERE n.ho_id = ? ORDER BY t.tu_ngay DESC";

    @Override
    public int them(DotCuTru dot) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                khoaNhanKhau(conn, dot.getNhanKhauId());
                if (chongNgay(conn, dot)) {
                    throw new TrungKhoaException(CHONG_NGAY, null);
                }
                int id = them(conn, dot);
                conn.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw TrungKhoaException.boc("Loi them tam tru/tam vang", e);
        }
    }

    /**
     * Them dot tren ket noi co san, de goi trong giao dich them nhan khau
     * cung dot tam tru dau tien.
     */
    static int them(Connection conn, DotCuTru dot) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_THEM,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, dot.getNhanKhauId());
            ps.setString(2, dot.getLoai().name());
            ps.setDate(3, Date.valueOf(dot.getTuNgay()));
            ps.setDate(4, Date.valueOf(dot.getDenNgay()));
            if (dot.getLyDo() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, dot.getLyDo());
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Khong lay duoc id dot moi");
                }
                return keys.getInt(1);
            }
        }
    }

    @Override
    public List<DotCuTru> theoNhanKhau(int nhanKhauId) {
        return doc(SQL_THEO_NHAN_KHAU, nhanKhauId,
                "Loi lay tam tru/tam vang cua nhan khau");
    }

    @Override
    public List<DotCuTru> theoHo(int hoId) {
        return doc(SQL_THEO_HO, hoId, "Loi lay tam tru/tam vang cua ho");
    }

    private static void khoaNhanKhau(Connection conn, int nhanKhauId)
            throws SQLException {
        try (PreparedStatement ps =
                conn.prepareStatement(SQL_KHOA_NHAN_KHAU)) {
            ps.setInt(1, nhanKhauId);
            ps.executeQuery().close();
        }
    }

    private static boolean chongNgay(Connection conn, DotCuTru dot)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_CHONG_NGAY)) {
            ps.setInt(1, dot.getNhanKhauId());
            ps.setDate(2, Date.valueOf(dot.getDenNgay()));
            ps.setDate(3, Date.valueOf(dot.getTuNgay()));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static List<DotCuTru> doc(String sql, int khoa, String loi) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, khoa);
            List<DotCuTru> ketQua = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ketQua.add(new DotCuTru(
                            rs.getInt("id"),
                            rs.getInt("nhan_khau_id"),
                            LoaiDotCuTru.valueOf(rs.getString("loai")),
                            rs.getDate("tu_ngay").toLocalDate(),
                            rs.getDate("den_ngay").toLocalDate(),
                            rs.getString("ly_do")));
                }
            }
            return ketQua;
        } catch (SQLException e) {
            throw new DaoException(loi, e);
        }
    }
}
