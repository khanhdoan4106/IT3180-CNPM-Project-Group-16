package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.Database;
import com.bluemoon.dao.DongHoCsdl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.NhatKyDaoImpl;
import com.bluemoon.dao.NoDongDaoImpl;
import com.bluemoon.dao.ThongKeDaoImpl;
import com.bluemoon.model.HoNoDong;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThuThang;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.NhatKyService;
import com.bluemoon.service.NoDongService;
import com.bluemoon.service.ThongKeService;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * database/seed-demo.sql chạy sau schema.sql + seed.sql trên CSDL thử: số hộ,
 * nhân khẩu, mỗi hộ đúng một chủ hộ, CCCD không trùng và đủ cho người từ 14
 * tuổi, tổng phiếu khớp chi tiết, có hộ nợ từ 3 tháng (kể cả hộ đã chuyển
 * đi), các màn Trang chính, Hộ nợ đọng, Nhật ký đọc ra số liệu; chạy lại
 * không thêm gì.
 */
class SeedDemoIT {

    private TaiKhoan admin;

    @BeforeEach
    void chuanBi() throws SQLException {
        CsdlThu.dungLai();
        try (Connection c = Database.getConnection();
                Statement st = c.createStatement()) {
            // Bo tai khoan gia cua CsdlThu de seed.sql tao admin that.
            st.executeUpdate("DELETE FROM tai_khoan");
        }
        CsdlThu.chaySeed();
        CsdlThu.chaySeedDemo();
        admin = new TaiKhoan(so("SELECT id FROM tai_khoan"
                + " WHERE ten_dang_nhap = 'admin'"), "admin", "x",
                "Nguyễn Thị Thu Hương", VaiTro.QUAN_TRI, true);
    }

    private static int so(String sql) throws SQLException {
        try (Connection c = Database.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test
    void soHoNhanKhauVaThuQuyMau() throws SQLException {
        assertEquals(40, CsdlThu.dem("ho_gia_dinh"), "4 hộ seed + 36 hộ demo");
        assertEquals(114, CsdlThu.dem("nhan_khau"),
                "9 nhân khẩu seed + 105 demo");
        assertEquals(1, so("SELECT COUNT(*) FROM ho_gia_dinh"
                + " WHERE trang_thai = 'DA_CHUYEN_DI'"));
        assertEquals(1, so("SELECT COUNT(*) FROM tai_khoan"
                + " WHERE ten_dang_nhap = 'thuquy' AND vai_tro = 'THU_QUY'"));
        assertEquals(6, so("SELECT COUNT(*) FROM tam_tru_tam_vang t"
                + " JOIN nhan_khau n ON n.id = t.nhan_khau_id"
                + " WHERE t.loai = 'TAM_TRU' AND n.so_cccd NOT IN"
                + " ('036093004321', '034199008765')"));
    }

    @Test
    void moiHoDungMotChuHoVaQuanHeHopLe() throws SQLException {
        assertEquals(0, so("SELECT COUNT(*) FROM (SELECT h.id FROM"
                + " ho_gia_dinh h LEFT JOIN nhan_khau n ON n.ho_id = h.id"
                + " AND n.quan_he = 'CHU_HO' GROUP BY h.id"
                + " HAVING COUNT(n.id) <> 1) x"));
        assertEquals(0, so("SELECT COUNT(*) FROM nhan_khau c"
                + " JOIN nhan_khau p ON p.ho_id = c.ho_id"
                + " AND p.quan_he IN ('CHU_HO', 'VO_CHONG')"
                + " WHERE c.quan_he = 'CON'"
                + " AND c.ngay_sinh < p.ngay_sinh + INTERVAL 18 YEAR"),
                "con nhỏ hơn bố mẹ ít nhất 18 tuổi");
    }

    @Test
    void cccdKhongTrungVaDuChoNguoiTu14Tuoi() throws SQLException {
        assertEquals(0, so("SELECT COUNT(*) - COUNT(DISTINCT so_cccd)"
                + " FROM nhan_khau WHERE so_cccd IS NOT NULL"));
        assertEquals(0, so("SELECT COUNT(*) FROM nhan_khau"
                + " WHERE so_cccd IS NULL"
                + " AND ngay_sinh <= CURDATE() - INTERVAL 14 YEAR"));
        assertEquals(0, so("SELECT COUNT(*) FROM nhan_khau"
                + " WHERE so_cccd IS NOT NULL AND so_cccd NOT REGEXP"
                + " '^[0-9]{12}$'"));
        assertTrue(so("SELECT COUNT(*) FROM nhan_khau"
                + " WHERE so_cccd IS NULL") > 0, "có trẻ em");
    }

    @Test
    void tongPhieuKhopChiTietVaKhongCoPhieuTuongLai() throws SQLException {
        assertTrue(CsdlThu.dem("phieu_thu") >= 150, "phiếu 6 tháng");
        assertEquals(0, so("SELECT COUNT(*) FROM phieu_thu p WHERE"
                + " p.tong_tien <> (SELECT SUM(so_tien) FROM"
                + " phieu_thu_chi_tiet c WHERE c.phieu_thu_id = p.id)"));
        assertEquals(0, so("SELECT COUNT(*) FROM phieu_thu"
                + " WHERE thoi_diem > NOW()"));
        assertEquals(0, so("SELECT COUNT(*) FROM phieu_thu p"
                + " JOIN phieu_thu_chi_tiet c ON c.phieu_thu_id = p.id"
                + " JOIN phai_nop pn ON pn.id = c.phai_nop_id"
                + " JOIN dot_thu d ON d.id = pn.dot_thu_id"
                + " WHERE p.thoi_diem < d.ngay_chot"),
                "không thu trước khi chốt tháng của khoản");
        assertEquals(2, so("SELECT COUNT(*) FROM phieu_thu WHERE da_huy"));
        assertEquals(so("SELECT MAX(so) FROM phieu_thu"),
                so("SELECT MAX(so_cuoi) FROM bo_dem_bien_lai"));
    }

    @Test
    void coHoNoTuBaThangKeCaHoDaChuyenDi() {
        List<HoNoDong> no = new NoDongService(new NoDongDaoImpl())
                .hoNo(admin, true);
        assertTrue(no.size() >= 3, "ít nhất 3 hộ nợ từ 3 tháng: " + no);
        assertTrue(no.stream().anyMatch(
                h -> h.trangThai() == TrangThaiHo.DA_CHUYEN_DI));
        assertTrue(new NoDongService(new NoDongDaoImpl()).tomTat(admin)
                .soHoConNo() > no.size(), "có cả hộ nợ 1-2 tháng");
    }

    @Test
    void trangChinhCoSauThangThuVaNhatKyCoNoiDung() {
        ThongKeService tk = new ThongKeService(new ThongKeDaoImpl(),
                new KhoanPhiDaoImpl(), new DongHoCsdl());
        List<ThuThang> sau = tk.tongQuan(admin).sauThang();
        assertEquals(6, sau.size());
        // Năm tháng trước đều có tiền; tháng này có thể chưa (ngày 1-2).
        assertTrue(sau.subList(0, 5).stream()
                .allMatch(t -> t.tong().signum() > 0), sau.toString());
        LocalDate homNay = LocalDate.now(new DongHoCsdl());
        assertFalse(new NhatKyService(new NhatKyDaoImpl()).theoKhoang(admin,
                homNay.minusMonths(6), homNay, null).isEmpty());
    }

    @Test
    void chayLaiKhongThemGi() throws SQLException {
        int ho = CsdlThu.dem("ho_gia_dinh");
        int phieu = CsdlThu.dem("phieu_thu");
        int nhatKy = CsdlThu.dem("nhat_ky");
        CsdlThu.chaySeedDemo();
        assertEquals(ho, CsdlThu.dem("ho_gia_dinh"));
        assertEquals(phieu, CsdlThu.dem("phieu_thu"));
        assertEquals(nhatKy, CsdlThu.dem("nhat_ky"));
    }
}
