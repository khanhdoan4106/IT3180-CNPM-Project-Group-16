package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.TamVang;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cai dat JDBC cua {@link BaoCaoNhanKhauDao}.
 */
public class BaoCaoNhanKhauDaoImpl implements BaoCaoNhanKhauDao {

    private static final String SQL_TAM_VANG = "SELECT n.ho_ten, n.ngay_sinh, "
            + "n.so_cccd, h.ma_can_ho, t.tu_ngay, t.den_ngay, t.ly_do "
            + "FROM tam_tru_tam_vang t "
            + "JOIN nhan_khau n ON n.id = t.nhan_khau_id "
            + "JOIN ho_gia_dinh h ON h.id = n.ho_id "
            + "WHERE t.loai = 'TAM_VANG' AND t.tu_ngay <= ? "
            + "AND t.den_ngay >= ? AND n.trang_thai = 'DANG_O' "
            + "AND h.trang_thai = 'DANG_O' "
            + "ORDER BY h.ma_can_ho, n.ho_ten";

    @Override
    public List<TamVang> tamVang(LocalDate ngay) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TAM_VANG)) {
            ps.setDate(1, Date.valueOf(ngay));
            ps.setDate(2, Date.valueOf(ngay));
            List<TamVang> kq = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(new TamVang(rs.getString("ho_ten"),
                            rs.getDate("ngay_sinh").toLocalDate(),
                            rs.getString("so_cccd"),
                            rs.getString("ma_can_ho"),
                            rs.getDate("tu_ngay").toLocalDate(),
                            rs.getDate("den_ngay").toLocalDate(),
                            rs.getString("ly_do")));
                }
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi lay danh sach tam vang", e);
        }
    }
}
