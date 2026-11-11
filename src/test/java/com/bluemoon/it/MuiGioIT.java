package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.BienDongDaoImpl;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
import com.bluemoon.model.BienDong;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.service.BienDongService;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.ThuPhiService;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Giờ ghi và đọc CSDL không phụ thuộc múi giờ của máy chạy ứng dụng. Trước
 * đây {@code Timestamp.valueOf} và {@code getTimestamp().toLocalDateTime()}
 * dịch giờ khi JVM không ở Việt Nam: phiếu lập 23:30 ngày 31/12 bị lưu thành
 * 06:30 ngày 01/01 năm sau và ràng buộc {@code chk_pt_nam} từ chối.
 */
class MuiGioIT {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T12 = YearMonth.of(2026, 12);

    private final TimeZone muiGioCu = TimeZone.getDefault();
    private final PhieuThuDaoImpl phieuDao = new PhieuThuDaoImpl();
    private final CongNoDaoImpl congNoDao = new CongNoDaoImpl();
    private TaiKhoan admin;
    private int hoId;

    private static Clock dongHo(int gio, int phut) {
        return Clock.fixed(LocalDateTime.of(2026, 12, 31, gio, phut)
                .atZone(VN).toInstant(), VN);
    }

    private ThuPhiService thuPhi(Clock dongHo) {
        return new ThuPhiService(phieuDao, new KhoanPhiDaoImpl(), dongHo);
    }

    private CongNoService congNo(Clock dongHo) {
        return new CongNoService(congNoDao, new KhoanPhiDaoImpl(),
                new HoGiaDinhDaoImpl(), dongHo);
    }

    @BeforeEach
    void chuanBi() {
        admin = CsdlThu.dungLai();
        KhoanPhiDaoImpl khoanPhiDao = new KhoanPhiDaoImpl();
        khoanPhiDao.them(new KhoanPhi(0, "Phí dịch vụ",
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                new BigDecimal("5000"), LocalDate.of(2026, 1, 1), null,
                TrangThaiKhoanPhi.DANG_THU));
        hoId = new HoGiaDinhDaoImpl().them(new HoGiaDinh(0, "A0601",
                LoaiCanHo.CAN_HO, 6, new BigDecimal("75.50"), null,
                LocalDate.of(2026, 1, 1), TrangThaiHo.DANG_O),
                new NhanKhau(0, 0, "Nguyễn Thị Lan", LocalDate.of(1982, 8, 25),
                        GioiTinh.NU, "001182004567", QuanHeChuHo.CHU_HO,
                        TrangThaiNhanKhau.DANG_O, LoaiCuTru.THUONG_TRU, null),
                null, admin.getId());
        congNo(dongHo(23, 30)).chot(admin, T12);
    }

    @AfterEach
    void khoiPhucMuiGio() {
        TimeZone.setDefault(muiGioCu);
    }

    @ParameterizedTest
    @ValueSource(strings = {"UTC", "America/New_York", "Pacific/Kiritimati",
        "Asia/Ho_Chi_Minh"})
    void lapVaHuyPhieuCuoiNamKhiJvmOMuiGioKhac(String muiGio) {
        TimeZone.setDefault(TimeZone.getTimeZone(muiGio));
        Clock luc2330 = dongHo(23, 30);
        List<Integer> khoan = phieuDao.khoanChuaDong(hoId).stream()
                .map(KhoanNo::phaiNopId).toList();

        PhieuThu p = thuPhi(luc2330).thu(admin, hoId, khoan, null,
                HinhThucThanhToan.TIEN_MAT);

        assertEquals("PT-2026-00001", p.soBienLai());
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 30), p.thoiDiem(),
                "JVM " + muiGio + " không được làm lệch giờ lập phiếu");
        assertEquals(1, phieuDao.trongNgay(LocalDate.of(2026, 12, 31)).size());
        assertEquals(0, phieuDao.trongNgay(LocalDate.of(2027, 1, 1)).size());

        PhieuThu huy = thuPhi(dongHo(23, 45)).huy(admin, p.id(),
                "Thu nhầm hộ bên cạnh");
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 45),
                huy.thoiDiemHuy());
    }

    @Test
    void moiPhienMysqlODatGioVietNamDuMayChuODatGioKhac() throws Exception {
        try (java.sql.Connection conn = com.bluemoon.config.Database
                .getConnection();
                java.sql.Statement st = conn.createStatement();
                java.sql.ResultSet rs = st.executeQuery(
                        "SELECT @@session.time_zone")) {
            rs.next();
            assertEquals("+07:00", rs.getString(1));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"UTC", "America/New_York", "Pacific/Kiritimati"})
    void gioCotMacDinhCuaCsdlDocLaiDungGioVietNam(String muiGio) {
        TimeZone.setDefault(TimeZone.getTimeZone(muiGio));
        LocalDateTime bayGio = LocalDateTime.now(VN);

        // thoi_diem cua bien_dong_nhan_khau do CSDL dien (DEFAULT NOW).
        List<BienDong> bd = new BienDongService(new BienDongDaoImpl())
                .ganDay(admin);
        assertTrue(gan(bd.get(0).thoiDiem(), bayGio), "biến động "
                + bd.get(0).thoiDiem() + " so với " + bayGio);

        // ngay_sua cua phai_nop do CSDL dien (NOW()).
        PhaiNop p = congNo(dongHo(23, 30)).theoThang(admin, T12).get(0);
        PhaiNop sua = congNo(dongHo(23, 30)).suaSoTien(admin, p.id(),
                "1.000", "Miễn giảm");
        assertTrue(gan(sua.ngaySua(), bayGio), "ngày sửa " + sua.ngaySua()
                + " so với " + bayGio);
    }

    /** Hai thoi diem gan nhau: cach nhau khong qua 5 phut (luc chay test). */
    private static boolean gan(LocalDateTime a, LocalDateTime b) {
        return Duration.between(a, b).abs().compareTo(Duration.ofMinutes(5))
                < 0;
    }
}
