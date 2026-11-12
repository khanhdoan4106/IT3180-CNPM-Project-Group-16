package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.NoDongDao.TomTatNo;
import com.bluemoon.dao.NoDongDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.HoNoDong;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.PhieuThuMoi;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.NoDongService;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Hộ nợ đọng trên MySQL thật (US-017, AC-048, AC-049, giả định 21, 25,
 * 33). Bốn tháng chốt T8 – T11/2026, hai khoản bắt buộc mỗi tháng:
 * <ul>
 * <li>A0601 (75,5 m²): đóng T8 – T10, T11 nợ (một phiếu T11 đã hủy) → 1
 * tháng;</li>
 * <li>A0602 (60 m²): nợ cả bốn tháng → 4 tháng;</li>
 * <li>A0603 (80 m²): T8 đóng phí dịch vụ, nợ phí quản lý; T9 hai khoản
 * sửa về 0 đ; T10, T11 nợ cả hai khoản → 3 tháng;</li>
 * <li>A0604, A0605 (50 m²): đóng T8, nợ T9 – T11 → 3 tháng, tổng nợ bằng
 * nhau; A0604 đã chuyển đi.</li>
 * </ul>
 */
class NoDongIT {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T8 = YearMonth.of(2026, 8);
    private static final YearMonth T9 = YearMonth.of(2026, 9);
    private static final YearMonth T10 = YearMonth.of(2026, 10);
    private static final YearMonth T11 = YearMonth.of(2026, 11);
    private static final LocalDateTime LUC =
            LocalDateTime.of(2026, 11, 10, 9, 0);

    private final KhoanPhiDaoImpl khoanPhiDao = new KhoanPhiDaoImpl();
    private final HoGiaDinhDaoImpl hoDao = new HoGiaDinhDaoImpl();
    private final PhieuThuDaoImpl phieuDao = new PhieuThuDaoImpl();
    private final CongNoDaoImpl congNoDao = new CongNoDaoImpl();
    private final NoDongService noDong =
            new NoDongService(new NoDongDaoImpl());
    private TaiKhoan admin;
    private CongNoService congNo;
    private KhoanPhi dichVu;
    private KhoanPhi quanLy;
    private KhoanPhi quy;
    private int hoA;
    private int hoB;
    private int hoC;
    private int hoD;
    private int hoE;
    private final List<Integer> phieuAThang10 = new ArrayList<>();

    @BeforeEach
    void chuanBi() {
        admin = CsdlThu.dungLai();
        Clock ngay12T11 = Clock.fixed(LocalDateTime.of(2026, 11, 12, 9, 0)
                .atZone(VN).toInstant(), VN);
        congNo = new CongNoService(congNoDao, khoanPhiDao, hoDao, ngay12T11);
        dichVu = batBuoc("Phí dịch vụ", "5000");
        quanLy = batBuoc("Phí quản lý", "7000");
        KhoanPhi k = new KhoanPhi(0, "Quỹ vì người nghèo 2026",
                LoaiKhoanPhi.TU_NGUYEN, CachTinhPhi.KHONG_CO_DINH, null,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 12, 31),
                TrangThaiKhoanPhi.DANG_THU);
        quy = k.voiId(khoanPhiDao.them(k, admin.getId()));
        hoA = ho("A0601", "75.50", "001182004561");
        hoB = ho("A0602", "60.00", "001182004562");
        hoC = ho("A0603", "80.00", "001182004563");
        hoD = ho("A0604", "50.00", "001182004564");
        hoE = ho("A0605", "50.00", "001182004565");
        for (YearMonth t : List.of(T8, T9, T10, T11)) {
            congNo.chot(admin, t);
        }
        for (YearMonth t : List.of(T8, T9)) {
            thu(hoA, t, dichVu);
            thu(hoA, t, quanLy);
        }
        phieuAThang10.add(thu(hoA, T10, dichVu));
        phieuAThang10.add(thu(hoA, T10, quanLy));
        huy(thu(hoA, T11, dichVu));
        thu(hoC, T8, dichVu);
        suaVeKhong(hoC, T9, dichVu);
        suaVeKhong(hoC, T9, quanLy);
        for (int ho : List.of(hoD, hoE)) {
            thu(ho, T8, dichVu);
            thu(ho, T8, quanLy);
        }
        hoDao.danhDauChuyenDi(hoD, admin.getId());
        // Dong gop tu nguyen (ke ca ghi 0 d) khong tao no.
        dongGop(hoA, "0");
        dongGop(hoB, "50000");
    }

    private KhoanPhi batBuoc(String ten, String donGia) {
        KhoanPhi k = new KhoanPhi(0, ten, LoaiKhoanPhi.BAT_BUOC,
                CachTinhPhi.THEO_DIEN_TICH, new BigDecimal(donGia),
                LocalDate.of(2026, 1, 1), null, TrangThaiKhoanPhi.DANG_THU);
        return k.voiId(khoanPhiDao.them(k, admin.getId()));
    }

    private int ho(String ma, String dienTich, String cccd) {
        return hoDao.them(new HoGiaDinh(0, ma, LoaiCanHo.CAN_HO, 6,
                new BigDecimal(dienTich), null, LocalDate.of(2026, 1, 1),
                TrangThaiHo.DANG_O),
                new NhanKhau(0, 0, "Chủ hộ " + ma, LocalDate.of(1980, 1, 1),
                        GioiTinh.NU, cccd, QuanHeChuHo.CHU_HO,
                        TrangThaiNhanKhau.DANG_O, LoaiCuTru.THUONG_TRU, null),
                null, admin.getId());
    }

    private PhaiNop phaiNop(int hoId, YearMonth thang, KhoanPhi k) {
        return congNo.theoThang(admin, thang).stream()
                .filter(p -> p.hoId() == hoId && p.khoanPhiId() == k.id())
                .findFirst().orElseThrow();
    }

    private int thu(int hoId, YearMonth thang, KhoanPhi k) {
        PhaiNop p = phaiNop(hoId, thang, k);
        return phieuDao.lapPhieu(new PhieuThuMoi(hoId,
                HinhThucThanhToan.TIEN_MAT, admin.getId(), LUC,
                List.of(new PhieuThuMoi.Dong(k.id(), p.id(), p.soTien()))))
                .id();
    }

    private void huy(int phieuId) {
        phieuDao.huy(phieuId, admin.getId(), LUC.plusMinutes(5), "Thu nhầm");
    }

    private void suaVeKhong(int hoId, YearMonth thang, KhoanPhi k) {
        congNoDao.suaSoTien(phaiNop(hoId, thang, k).id(), BigDecimal.ZERO,
                admin.getId(), "Miễn phí");
    }

    private void dongGop(int hoId, String soTien) {
        BigDecimal tien = new BigDecimal(soTien);
        phieuDao.lapPhieu(new PhieuThuMoi(hoId,
                tien.signum() == 0 ? null : HinhThucThanhToan.TIEN_MAT,
                admin.getId(), LUC,
                List.of(new PhieuThuMoi.Dong(quy.id(), null, tien))));
    }

    private Map<String, HoNoDong> theoMa(List<HoNoDong> ds) {
        return ds.stream().collect(Collectors.toMap(HoNoDong::maCanHo,
                Function.identity()));
    }

    private static void bang(String mong, BigDecimal thuc) {
        assertEquals(0, new BigDecimal(mong).compareTo(thuc),
                "mong " + mong + ", thực " + thuc);
    }

    /** AC-048: chỉ hộ nợ từ 3 tháng trở lên, đúng thứ tự. */
    @Test
    void locNoTuBaThangDungThuTu() {
        List<HoNoDong> ds = noDong.hoNo(admin, true);
        // 4 thang truoc; 3 thang xep tong no giam dan; bang nhau xep ma.
        assertEquals(List.of("A0602", "A0603", "A0604", "A0605"),
                ds.stream().map(HoNoDong::maCanHo).toList());
    }

    /** AC-049: số tháng nợ, nợ từ tháng, tổng nợ, chủ hộ. */
    @Test
    void soThangTongNoVaChuHoDung() {
        Map<String, HoNoDong> m = theoMa(noDong.hoNo(admin, false));
        HoNoDong b = m.get("A0602");
        assertEquals(4, b.soThangNo());
        assertEquals(T8, b.noTuThang());
        bang("2880000", b.tongNo()); // 60 x 12.000 x 4
        assertEquals("Chủ hộ A0602", b.chuHo());
        // C: T8 chi no phi quan ly, T9 da mien, T10 va T11 no hai khoan
        // cung thang chi tinh mot thang.
        HoNoDong c = m.get("A0603");
        assertEquals(3, c.soThangNo());
        assertEquals(T8, c.noTuThang());
        bang("2480000", c.tongNo()); // 80 x 7.000 + 80 x 12.000 x 2
        assertEquals(T9, m.get("A0605").noTuThang());
        bang("1800000", m.get("A0605").tongNo());
    }

    /** Khoản đã thu không tính; phiếu đã hủy thì khoản trở lại nợ. */
    @Test
    void khoanDaThuKhongTinhPhieuHuyTinhLai() {
        HoNoDong a = theoMa(noDong.hoNo(admin, false)).get("A0601");
        assertEquals(1, a.soThangNo());
        assertEquals(T11, a.noTuThang());
        bang("906000", a.tongNo()); // 75,5 x 12.000, phieu DV T11 da huy
        thu(hoA, T11, dichVu);
        thu(hoA, T11, quanLy);
        assertTrue(theoMa(noDong.hoNo(admin, false)).get("A0601") == null,
                "Đóng đủ thì không còn nợ");
    }

    /**
     * Số tháng nợ đếm tháng, không đếm khoản: A0601 nợ hai khoản ở mỗi
     * tháng T10, T11 (4 khoản) vẫn chỉ là 2 tháng, chưa phải nợ đọng.
     */
    @Test
    void nhieuKhoanTrongHaiThangChuaPhaiNoDong() {
        phieuAThang10.forEach(this::huy);
        HoNoDong a = theoMa(noDong.hoNo(admin, false)).get("A0601");
        assertEquals(2, a.soThangNo());
        assertEquals(T10, a.noTuThang());
        assertTrue(noDong.hoNo(admin, true).stream()
                .noneMatch(h -> h.maCanHo().equals("A0601")));
        TomTatNo t = noDong.tomTat(admin);
        assertEquals(5, t.soHoConNo());
        assertEquals(4, t.soHoNoDong());
    }

    /** Giả định 21: hộ đã chuyển đi vẫn có, kèm trạng thái và chủ hộ cũ. */
    @Test
    void hoDaChuyenDiVanCo() {
        HoNoDong d = theoMa(noDong.hoNo(admin, true)).get("A0604");
        assertEquals(TrangThaiHo.DA_CHUYEN_DI, d.trangThai());
        assertTrue(d.daChuyenDi());
        assertEquals("Chủ hộ A0604", d.chuHo());
        assertEquals(3, d.soThangNo());
    }

    /** Khoản 0 đ và khoản tự nguyện không tạo nợ; tóm tắt Trang chính. */
    @Test
    void khoanKhongDongVaTuNguyenKhongTinhTomTat() {
        List<HoNoDong> tatCa = noDong.hoNo(admin, false);
        assertEquals(List.of("A0602", "A0603", "A0604", "A0605", "A0601"),
                tatCa.stream().map(HoNoDong::maCanHo).toList());
        TomTatNo t = noDong.tomTat(admin);
        assertEquals(5, t.soHoConNo());
        assertEquals(4, t.soHoNoDong());
        // Mien ca hai khoan T10, T11 cua C: con 1 thang (T8 phi quan ly).
        for (YearMonth th : List.of(T10, T11)) {
            suaVeKhong(hoC, th, dichVu);
            suaVeKhong(hoC, th, quanLy);
        }
        HoNoDong c = theoMa(noDong.hoNo(admin, false)).get("A0603");
        assertEquals(1, c.soThangNo());
        bang("560000", c.tongNo());
        assertEquals(3, noDong.tomTat(admin).soHoNoDong());
    }
}
