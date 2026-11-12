package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.HoNoDong;
import com.bluemoon.model.TrangThaiHo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/** Cài đặt JDBC của {@link NoDongDao}. */
public class NoDongDaoImpl implements NoDongDao {

    /**
     * Khoản nợ: lớn hơn 0 đ và chưa có phiếu thu còn hiệu lực (cột sinh
     * phai_nop_da_thu chỉ có giá trị khi phiếu chưa hủy).
     */
    private static final String KHOAN_NO = "p.so_tien > 0 AND NOT EXISTS ("
            + "SELECT 1 FROM phieu_thu_chi_tiet ct "
            + "WHERE ct.phai_nop_da_thu = p.id)";

    /**
     * Chủ hộ là nhân khẩu có quan hệ "chủ hộ" của hộ (cột sinh chu_ho_cua,
     * mỗi hộ tối đa một người), kể cả khi đã chuyển đi cùng hộ.
     */
    private static final String SQL_HO_NO = "SELECT h.id, h.ma_can_ho, "
            + "h.trang_thai, c.ho_ten AS chu_ho, "
            + "COUNT(DISTINCT p.dot_thu_id) AS so_thang, "
            + "MIN(d.thang) AS tu_thang, SUM(p.so_tien) AS tong_no "
            + "FROM phai_nop p JOIN dot_thu d ON d.id = p.dot_thu_id "
            + "JOIN ho_gia_dinh h ON h.id = p.ho_id "
            + "LEFT JOIN nhan_khau c ON c.chu_ho_cua = h.id "
            + "WHERE " + KHOAN_NO + " "
            + "GROUP BY h.id, h.ma_can_ho, h.trang_thai, c.ho_ten "
            + "HAVING COUNT(DISTINCT p.dot_thu_id) >= ? "
            + "ORDER BY so_thang DESC, tong_no DESC, h.ma_can_ho";

    private static final String SQL_TOM_TAT = "SELECT COUNT(*) AS con_no, "
            + "COUNT(CASE WHEN x.so_thang >= ? THEN 1 END) AS no_dong FROM "
            + "(SELECT p.ho_id, COUNT(DISTINCT p.dot_thu_id) AS so_thang "
            + "FROM phai_nop p WHERE " + KHOAN_NO + " GROUP BY p.ho_id) x";

    @Override
    public List<HoNoDong> hoNo(int soThangToiThieu) {
        List<HoNoDong> kq = new ArrayList<>();
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_HO_NO)) {
            ps.setInt(1, soThangToiThieu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(new HoNoDong(rs.getInt("id"),
                            rs.getString("ma_can_ho"), rs.getString("chu_ho"),
                            TrangThaiHo.valueOf(rs.getString("trang_thai")),
                            rs.getInt("so_thang"), YearMonth.from(
                                    rs.getDate("tu_thang").toLocalDate()),
                            rs.getBigDecimal("tong_no")));
                }
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi doc ho no dong", e);
        }
    }

    @Override
    public TomTatNo tomTat(int nguong) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_TOM_TAT)) {
            ps.setInt(1, nguong);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new TomTatNo(rs.getInt("con_no"),
                        rs.getInt("no_dong"));
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tom tat no dong", e);
        }
    }
}
