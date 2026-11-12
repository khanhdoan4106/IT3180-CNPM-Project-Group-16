package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.BaoCaoThu;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.PhieuThu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/** Cài đặt JDBC của {@link BaoCaoThuDao}. */
public class BaoCaoThuDaoImpl implements BaoCaoThuDao {

    /**
     * Mỗi dòng kết quả là một dòng chi tiết của phiếu; chủ hộ là chủ hộ hiện
     * tại của hộ, như biên lai (giả định 37). Lọc thời điểm theo khoảng để
     * dùng được chỉ mục idx_pt_thoi_diem.
     */
    private static final String SQL = "SELECT pt.id, pt.nam, pt.so, "
            + "pt.thoi_diem, pt.tong_tien, h.ma_can_ho, c.ho_ten AS chu_ho, "
            + "ct.khoan_phi_id, k.ten, k.loai, ct.so_tien "
            + "FROM phieu_thu pt "
            + "JOIN ho_gia_dinh h ON h.id = pt.ho_id "
            + "LEFT JOIN nhan_khau c ON c.chu_ho_cua = h.id "
            + "JOIN phieu_thu_chi_tiet ct ON ct.phieu_thu_id = pt.id "
            + "JOIN khoan_phi k ON k.id = ct.khoan_phi_id "
            + "WHERE pt.da_huy = FALSE "
            + "AND pt.thoi_diem >= ? AND pt.thoi_diem < ? "
            + "ORDER BY pt.thoi_diem, pt.id, ct.id";

    @Override
    public BaoCaoThu theoThang(YearMonth thang) {
        List<BaoCaoThu.ChiTiet> ds = new ArrayList<>();
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setObject(1, thang.atDay(1).atStartOfDay());
            ps.setObject(2, thang.plusMonths(1).atDay(1).atStartOfDay());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(new BaoCaoThu.ChiTiet(rs.getInt("id"),
                            rs.getString("ma_can_ho"), rs.getString("chu_ho"),
                            rs.getBigDecimal("tong_tien"),
                            rs.getObject("thoi_diem", LocalDateTime.class),
                            PhieuThu.soBienLai(rs.getInt("nam"),
                                    rs.getInt("so")),
                            rs.getInt("khoan_phi_id"), rs.getString("ten"),
                            LoaiKhoanPhi.valueOf(rs.getString("loai")),
                            rs.getBigDecimal("so_tien")));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Loi doc bao cao thu", e);
        }
        return BaoCaoThu.gom(thang, ds);
    }
}
