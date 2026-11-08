package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.KetQuaTinhCongNo;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CongNoServiceTest {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T12 = YearMonth.of(2026, 12);
    private static final LocalDate DAU_NAM = LocalDate.of(2026, 1, 1);

    private KhoGia khoHo;
    private KhoThuPhiGia kho;
    private CongNoService service;
    private TaiKhoan admin;
    private TaiKhoan thuQuy;
    private int nextHo = 1;

    @BeforeEach
    void chuanBi() {
        khoHo = new KhoGia();
        kho = new KhoThuPhiGia();
        Clock ngay3T12 = Clock.fixed(LocalDate.of(2026, 12, 3)
                .atStartOfDay(VN).toInstant(), VN);
        service = new CongNoService(kho.congNoDao, kho.khoanPhiDao,
                khoHo.hoDao, ngay3T12);
        admin = new TaiKhoan(1, "admin", "x", "Quản trị", VaiTro.QUAN_TRI,
                true);
        thuQuy = new TaiKhoan(2, "thuquy", "x", "Thủ quỹ", VaiTro.THU_QUY,
                true);
        themKhoan(KhoanPhiService.PHI_DICH_VU, LoaiKhoanPhi.BAT_BUOC, "5000");
        themKhoan(KhoanPhiService.PHI_QUAN_LY, LoaiKhoanPhi.BAT_BUOC, "7000");
        themKhoan("Quỹ biển đảo", LoaiKhoanPhi.TU_NGUYEN, "50000");
        themHo("A0601", "75.50", TrangThaiHo.DANG_O);
        themHo("K01", "35.00", TrangThaiHo.DANG_O);
    }

    private void themKhoan(String ten, LoaiKhoanPhi loai, String donGia) {
        kho.khoanPhiDao.them(new KhoanPhi(0, ten, loai,
                loai == LoaiKhoanPhi.BAT_BUOC ? CachTinhPhi.THEO_DIEN_TICH
                        : CachTinhPhi.CO_DINH_THEO_HO,
                new BigDecimal(donGia), DAU_NAM, null,
                TrangThaiKhoanPhi.DANG_THU));
    }

    private int themHo(String ma, String dienTich, TrangThaiHo trangThai) {
        int id = nextHo++;
        khoHo.ho.put(id, new HoGiaDinh(id, ma, LoaiCanHo.CAN_HO, 6,
                dienTich == null ? null : new BigDecimal(dienTich), null,
                DAU_NAM, trangThai));
        return id;
    }

    private static BigDecimal tien(String s) {
        return new BigDecimal(s);
    }

    private static PhaiNop tim(List<PhaiNop> ds, String ma, String khoan) {
        return ds.stream().filter(p -> p.maCanHo().equals(ma)
                && p.tenKhoan().equals(khoan)).findFirst().orElseThrow();
    }

    @Test
    void xemTruocTinhTheoDienTichNhanDonGia() {
        // AC-015, AC-016, AC-018, AC-027
        KetQuaTinhCongNo kq = service.xemTruoc(admin, T12);
        assertEquals(4, kq.khoan().size());
        assertEquals(2, kq.soHo());
        assertEquals(0, tien("377500").compareTo(
                tim(kq.khoan(), "A0601", "Phí dịch vụ").soTien()));
        assertEquals(0, tien("528500").compareTo(
                tim(kq.khoan(), "A0601", "Phí quản lý").soTien()));
        assertEquals(0, tien("1326000").compareTo(kq.tongTien()));
        assertTrue(kho.phaiNop.isEmpty(), "xem truoc khong duoc luu");
    }

    @Test
    void lamTronDenDong() {
        int id = themHo("B0303", "33.33", TrangThaiHo.DANG_O);
        kho.khoanPhiDao.capNhatDonGia(1, tien("5555"));
        PhaiNop p = service.xemTruoc(admin, T12).khoan().stream()
                .filter(x -> x.hoId() == id && x.khoanPhiId() == 1)
                .findFirst().orElseThrow();
        // 33,33 x 5.555 = 185.148,15
        assertEquals(0, tien("185148").compareTo(p.soTien()));
    }

    @Test
    void boQuaHoDaChuyenDiVaKhoanTuNguyen() {
        themHo("A1205", "92.00", TrangThaiHo.DA_CHUYEN_DI);
        KetQuaTinhCongNo kq = service.xemTruoc(admin, T12);
        assertFalse(kq.khoan().stream()
                .anyMatch(p -> p.maCanHo().equals("A1205")));
        assertFalse(kq.khoan().stream()
                .anyMatch(p -> p.tenKhoan().equals("Quỹ biển đảo")));
    }

    @Test
    void hoChuaCoDienTichBiBoQuaVaCanhBao() {
        // AC-029
        themHo("PH01", null, TrangThaiHo.DANG_O);
        themHo("PH02", "0", TrangThaiHo.DANG_O);
        KetQuaTinhCongNo kq = service.xemTruoc(admin, T12);
        assertEquals(List.of("PH01", "PH02"), kq.hoBoQua());
        assertEquals(4, kq.khoan().size());
    }

    @Test
    void tuChoiThangTuongLaiVaKhiChuaCoKhoanBatBuoc() {
        assertThrows(NghiepVuException.class,
                () -> service.xemTruoc(admin, YearMonth.of(2027, 1)));
        assertThrows(NghiepVuException.class,
                () -> service.xemTruoc(admin, null));
        kho.khoan.clear();
        assertThrows(NghiepVuException.class,
                () -> service.xemTruoc(admin, T12));
    }

    @Test
    void chotLuuVaChotLaiKhongTaoTrung() {
        // AC-027, AC-028
        assertTrue(service.canNhacChot());
        CongNoService.KetQuaChot lan1 = service.chot(thuQuy, T12);
        assertTrue(lan1.lanDau());
        assertEquals(4, lan1.soMoi());
        assertFalse(service.canNhacChot());

        CongNoService.KetQuaChot lan2 = service.chot(admin, T12);
        assertFalse(lan2.lanDau());
        assertEquals(0, lan2.soMoi());
        assertEquals(4, service.theoThang(admin, T12).size());
    }

    @Test
    void chotLaiBoSungHoMoiVaGiuKhoanDaSua() {
        service.chot(admin, T12);
        int a0601 = tim(service.theoThang(admin, T12), "A0601",
                "Phí dịch vụ").id();
        service.suaSoTien(admin, a0601, "350.000", "Nhập sai");
        themHo("B0303", "60.00", TrangThaiHo.DANG_O);

        assertEquals(2, service.chot(admin, T12).soMoi());
        List<PhaiNop> ds = service.theoThang(admin, T12);
        assertEquals(6, ds.size());
        assertEquals(0, tien("350000").compareTo(
                tim(ds, "A0601", "Phí dịch vụ").soTien()));
    }

    @Test
    void doiDonGiaSauKhiChotKhongDoiThangDaChot() {
        service.chot(admin, T12);
        kho.khoanPhiDao.capNhatDonGia(1, tien("6000"));
        assertEquals(0, tien("377500").compareTo(tim(
                service.theoThang(admin, T12), "A0601", "Phí dịch vụ")
                .soTien()));
    }

    @Test
    void suaSoTienGiuSoGocVaNguoiSua() {
        // AC-025
        service.chot(admin, T12);
        int id = tim(service.theoThang(admin, T12), "K01", "Phí quản lý")
                .id();
        PhaiNop p = service.suaSoTien(thuQuy, id, "200.000", "  Giảm  ");
        assertEquals(0, tien("200000").compareTo(p.soTien()));
        assertEquals(0, tien("245000").compareTo(p.soTienGoc()));
        assertEquals(2, p.nguoiSua());
        assertEquals("Giảm", p.ghiChu());
        assertTrue(p.daSua());
    }

    @Test
    void tuChoiSuaKhoanDaCoPhieuThu() {
        // AC-026
        service.chot(admin, T12);
        int id = tim(service.theoThang(admin, T12), "K01", "Phí quản lý")
                .id();
        kho.daCoPhieuThu.add(id);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.suaSoTien(admin, id, "1.000", null));
        assertEquals(CongNoService.LOI_DA_THU, e.getMessage());
        assertEquals(0, tien("245000").compareTo(
                kho.phaiNop.get(id).soTien()));
    }

    @Test
    void tuChoiSoTienSaiHoacKhongDoi() {
        service.chot(admin, T12);
        int id = tim(service.theoThang(admin, T12), "K01", "Phí quản lý")
                .id();
        for (String sai : new String[] {"-1", "abc", "", "245.000",
                "1.000.000.001"}) {
            assertThrows(NghiepVuException.class,
                    () -> service.suaSoTien(admin, id, sai, null));
        }
        assertThrows(NghiepVuException.class,
                () -> service.suaSoTien(admin, 999, "1", null));
    }

    @Test
    void canDangNhapMoiChotDuoc() {
        assertThrows(NghiepVuException.class,
                () -> service.chot(null, T12));
        TaiKhoan biKhoa = new TaiKhoan(3, "cu", "x", "Cũ", VaiTro.THU_QUY,
                false);
        assertThrows(NghiepVuException.class,
                () -> service.chot(biKhoa, T12));
    }

    @Test
    void tongNoChuaDongCuaHo() {
        service.chot(admin, T12);
        int k01 = tim(service.theoThang(admin, T12), "K01", "Phí dịch vụ")
                .hoId();
        assertEquals(0, tien("420000").compareTo(
                service.tongChuaDong(admin, k01)));
        int daThu = tim(service.theoThang(admin, T12), "K01",
                "Phí quản lý").id();
        kho.daCoPhieuThu.add(daThu);
        assertEquals(0, tien("175000").compareTo(
                service.tongChuaDong(thuQuy, k01)));
    }

    private int themHoTu(String ma, LocalDate ngayChuyenDen) {
        int id = nextHo++;
        khoHo.ho.put(id, new HoGiaDinh(id, ma, LoaiCanHo.CAN_HO, 6,
                tien("80"), null, ngayChuyenDen, TrangThaiHo.DANG_O));
        return id;
    }

    private void themKhoanBatBuoc(String ten, LocalDate tu, LocalDate den) {
        kho.khoanPhiDao.them(new KhoanPhi(0, ten, LoaiKhoanPhi.BAT_BUOC,
                CachTinhPhi.THEO_DIEN_TICH, tien("1000"), tu, den,
                TrangThaiKhoanPhi.DANG_THU));
    }

    private static boolean coHo(KetQuaTinhCongNo kq, String ma) {
        return kq.khoan().stream().anyMatch(p -> p.maCanHo().equals(ma));
    }

    private static boolean coKhoan(KetQuaTinhCongNo kq, String ten) {
        return kq.khoan().stream().anyMatch(p -> p.tenKhoan().equals(ten));
    }

    @Test
    void khongTinhHoChuyenDenSauThangChot() {
        // Loi 1: chot thang cu tung tinh ca ho chua o.
        themHoTu("B0909", LocalDate.of(2026, 12, 5));
        assertFalse(coHo(service.xemTruoc(admin, YearMonth.of(2026, 11)),
                "B0909"));
        assertEquals(4, service.chot(admin, YearMonth.of(2026, 11)).soMoi());
        // Den giua thang van tinh du thang (gia dinh 15).
        assertTrue(coHo(service.xemTruoc(admin, T12), "B0909"));
        themHoTu("C0101", LocalDate.of(2026, 12, 31));
        assertTrue(coHo(service.xemTruoc(admin, T12), "C0101"));
    }

    @Test
    void chiTinhKhoanCoHieuLucTrongThang() {
        themKhoanBatBuoc("Phí mới", LocalDate.of(2026, 12, 15), null);
        themKhoanBatBuoc("Phí cũ", DAU_NAM, LocalDate.of(2026, 10, 31));
        KetQuaTinhCongNo t11 = service.xemTruoc(admin, YearMonth.of(2026, 11));
        assertFalse(coKhoan(t11, "Phí mới"), "chua bat dau");
        assertFalse(coKhoan(t11, "Phí cũ"), "da ket thuc");
        assertTrue(coKhoan(service.xemTruoc(admin, T12), "Phí mới"));
        assertTrue(coKhoan(service.xemTruoc(admin, YearMonth.of(2026, 10)),
                "Phí cũ"));
    }

    @Test
    void thangChuaCoKhoanApDungThiBaoRoVaKhongChot() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.chot(admin, YearMonth.of(2025, 6)));
        assertTrue(e.getMessage().startsWith("Tháng 6/2025 chưa có khoản"),
                e.getMessage());
        assertTrue(kho.thangDaChot.isEmpty());
    }

    @Test
    void thangChuaCoHoNaoThiBaoRoVaKhongChot() {
        themKhoanBatBuoc("Phí năm trước", LocalDate.of(2025, 1, 1), null);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.chot(admin, YearMonth.of(2025, 12)));
        assertTrue(e.getMessage().startsWith("Tháng 12/2025 chưa có hộ nào"),
                e.getMessage());
        assertTrue(kho.thangDaChot.isEmpty());
    }
}
