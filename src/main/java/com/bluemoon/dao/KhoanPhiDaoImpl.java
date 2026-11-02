package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.TrangThaiKhoanPhi;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Cai dat JDBC cua {@link KhoanPhiDao}. Luon dung PreparedStatement.
 */
public class KhoanPhiDaoImpl implements KhoanPhiDao {

    private static final String COT = "id, ten, loai, cach_tinh, don_gia, "
            + "tu_ngay, den_ngay, trang_thai";
    private static final String SQL_THEM = "INSERT INTO khoan_phi (ten, "
            + "loai, cach_tinh, don_gia, tu_ngay, den_ngay) "
            + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_DON_GIA =
            "UPDATE khoan_phi SET don_gia = ? WHERE id = ?";
    private static final String SQL_THEO_ID =
            "SELECT " + COT + " FROM khoan_phi WHERE id = ?";
    private static final String SQL_DANH_SACH = "SELECT " + COT
            + " FROM khoan_phi ORDER BY loai = 'TU_NGUYEN', tu_ngay DESC, ten";
    private static final String SQL_TRUNG_TEN =
            "SELECT 1 FROM khoan_phi WHERE ten = ? AND tu_ngay = ? LIMIT 1";

    @Override
    public int them(KhoanPhi k) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEM,
                        Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, k.ten());
            ps.setString(2, k.loai().name());
            ps.setString(3, k.cachTinh().name());
            if (k.donGia() == null) {
                ps.setNull(4, Types.DECIMAL);
            } else {
                ps.setBigDecimal(4, k.donGia());
            }
            ps.setDate(5, Date.valueOf(k.tuNgay()));
            if (k.denNgay() == null) {
                ps.setNull(6, Types.DATE);
            } else {
                ps.setDate(6, Date.valueOf(k.denNgay()));
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Khong lay duoc id khoan thu moi");
                }
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw TrungKhoaException.boc("Loi them khoan thu", e);
        }
    }

    @Override
    public void capNhatDonGia(int id, BigDecimal donGia) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DON_GIA)) {
            ps.setBigDecimal(1, donGia);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Loi cap nhat don gia", e);
        }
    }

    @Override
    public Optional<KhoanPhi> timTheoId(int id) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_THEO_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(doc(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tim khoan thu", e);
        }
    }

    @Override
    public List<KhoanPhi> danhSach() {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DANH_SACH);
                ResultSet rs = ps.executeQuery()) {
            List<KhoanPhi> kq = new ArrayList<>();
            while (rs.next()) {
                kq.add(doc(rs));
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi lay danh muc khoan thu", e);
        }
    }

    @Override
    public boolean tonTaiTen(String ten, LocalDate tuNgay) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TRUNG_TEN)) {
            ps.setString(1, ten);
            ps.setDate(2, Date.valueOf(tuNgay));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi kiem tra ten khoan thu", e);
        }
    }

    private static KhoanPhi doc(ResultSet rs) throws SQLException {
        Date den = rs.getDate("den_ngay");
        return new KhoanPhi(rs.getInt("id"), rs.getString("ten"),
                LoaiKhoanPhi.valueOf(rs.getString("loai")),
                CachTinhPhi.valueOf(rs.getString("cach_tinh")),
                rs.getBigDecimal("don_gia"),
                rs.getDate("tu_ngay").toLocalDate(),
                den == null ? null : den.toLocalDate(),
                TrangThaiKhoanPhi.valueOf(rs.getString("trang_thai")));
    }
}
