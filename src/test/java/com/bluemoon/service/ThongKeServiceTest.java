package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.KhoanPhiDao;
import com.bluemoon.dao.ThongKeDao;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThongKeKhoan;
import com.bluemoon.model.ThuThang;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tổng thu tháng, tỷ lệ thu và biểu đồ 6 tháng (US-016, AC-046, AC-047),
 * thống kê theo khoản (US-019, AC-052, AC-053) trên DAO giả.
 */
class ThongKeServiceTest {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final TaiKhoan QT = new TaiKhoan(1, "admin", "x",
            "Quản trị", VaiTro.QUAN_TRI, true);
    private static final TaiKhoan THU_QUY = new TaiKhoan(2, "thuquy", "x",
            "Thủ quỹ", VaiTro.THU_QUY, true);
    private static final YearMonth T11 = YearMonth.of(2026, 11);
    private static final KhoanPhi DICH_VU = new KhoanPhi(1, "Phí dịch vụ",
            LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
            new BigDecimal("5000"), LocalDate.of(2026, 1, 1), null,
            TrangThaiKhoanPhi.DANG_THU);
    private static final KhoanPhi QUY = new KhoanPhi(2, "Quỹ vì người nghèo",
            LoaiKhoanPhi.TU_NGUYEN, CachTinhPhi.KHONG_CO_DINH, null,
            LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 31),
            TrangThaiKhoanPhi.DANG_THU);

    /** DAO giả: số liệu đặt sẵn, đếm số lần gọi. */
    private static class DaoGia implements ThongKeDao {
        private final Map<YearMonth, BigDecimal> thu = new HashMap<>();
        private final Map<YearMonth, TyLeThu> tyLe = new HashMap<>();
        private final List<YearMonth> daChot = new ArrayList<>();
        private int soLanTheoThang;
        private YearMonth tu;
        private YearMonth den;

        @Override
        public Map<YearMonth, BigDecimal> thuTheoThang(YearMonth tuThang,
                YearMonth denThang) {
            soLanTheoThang++;
            tu = tuThang;
            den = denThang;
            Map<YearMonth, BigDecimal> kq = new HashMap<>();
            thu.forEach((t, v) -> {
                if (!t.isBefore(tuThang) && !t.isAfter(denThang)) {
                    kq.put(t, v);
                }
            });
            return kq;
        }

        @Override
        public Optional<TyLeThu> tyLeThu(YearMonth thang) {
            return Optional.ofNullable(tyLe.get(thang));
        }

        @Override
        public List<YearMonth> thangDaChot() {
            return daChot;
        }

        @Override
        public SoLieuKhoan khoanBatBuoc(int khoanPhiId, YearMonth thang) {
            return new SoLieuKhoan(new BigDecimal("700000"),
                    new BigDecimal("1000000"), 4, 3, 0);
        }

        @Override
        public SoLieuKhoan khoanTuNguyen(int khoanPhiId) {
            return new SoLieuKhoan(new BigDecimal("150000"),
                    BigDecimal.ZERO, 0, 2, 1);
        }
    }

    /** Danh mục khoản giả, chỉ cần danhSach. */
    private static class KhoanGia implements KhoanPhiDao {
        @Override
        public int them(KhoanPhi khoan, int nguoiId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void capNhatDonGia(int id, BigDecimal donGia, int nguoiId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<KhoanPhi> timTheoId(int id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<KhoanPhi> danhSach() {
            return List.of(QUY, DICH_VU);
        }

        @Override
        public boolean tonTaiTen(String ten, LocalDate tuNgay) {
            throw new UnsupportedOperationException();
        }
    }

    private DaoGia dao;
    private ThongKeService service;

    @BeforeEach
    void chuanBi() {
        dao = new DaoGia();
        Clock ngay12T11 = Clock.fixed(LocalDateTime.of(2026, 11, 12, 9, 0)
                .atZone(VN).toInstant(), VN);
        service = new ThongKeService(dao, new KhoanGia(), ngay12T11);
    }

    @Test
    void caHaiVaiTroDeuXemDuocNguoiKhacBiTuChoi() {
        service.tongQuan(QT);
        service.tongQuan(THU_QUY);
        service.theoKhoan(THU_QUY, QUY, null);
        assertEquals(2, service.cacKhoan(THU_QUY).size());
        for (Runnable r : List.<Runnable>of(
                () -> service.tongQuan(null),
                () -> service.tongThuThang(QT.voiHoatDong(false), T11),
                () -> service.cacKhoan(null),
                () -> service.thangDaChot(null),
                () -> service.theoKhoan(null, QUY, null))) {
            NghiepVuException e = assertThrows(NghiepVuException.class,
                    r::run);
            assertEquals(PhanQuyen.LOI_CHUA_DANG_NHAP, e.getMessage());
        }
    }

    /** AC-047: tháng không có phiếu thu là 0 đ. */
    @Test
    void thangKhongCoGiaoDichLaKhong() {
        assertEquals(BigDecimal.ZERO, service.tongThuThang(QT, T11));
        dao.thu.put(T11, new BigDecimal("186450000"));
        assertEquals(new BigDecimal("186450000"),
                service.tongThuThang(QT, T11));
        assertEquals(BigDecimal.ZERO,
                service.tongThuThang(QT, T11.minusMonths(1)));
    }

    /** AC-046: tổng quan tháng hiện tại theo đồng hồ (giờ CSDL). */
    @Test
    void tongQuanThangHienTaiCoTyLeKhiDaChot() {
        dao.thu.put(T11, new BigDecimal("720000"));
        dao.tyLe.put(T11, new ThongKeDao.TyLeThu(new BigDecimal("719999"),
                new BigDecimal("1000000")));
        ThongKeService.TongQuan tq = service.tongQuan(QT);
        assertEquals(T11, tq.thang());
        assertEquals(new BigDecimal("720000"), tq.daThu());
        assertTrue(tq.daChot());
        // Lam tron xuong: 71,9999% hien 71%, chi thu du moi la 100%.
        assertEquals(71, tq.phanTram());
    }

    @Test
    void thangChuaChotKhongTinhTyLe() {
        dao.thu.put(T11, new BigDecimal("500000"));
        ThongKeService.TongQuan tq = service.tongQuan(QT);
        assertFalse(tq.daChot());
        assertNull(tq.phanTram());
        assertEquals(new BigDecimal("500000"), tq.daThu());
    }

    @Test
    void daChotMaKhongCoKhoanPhaiNopThiKhongChiaChoKhong() {
        dao.tyLe.put(T11, new ThongKeDao.TyLeThu(BigDecimal.ZERO,
                BigDecimal.ZERO));
        ThongKeService.TongQuan tq = service.tongQuan(QT);
        assertTrue(tq.daChot());
        assertNull(tq.phanTram());
    }

    @Test
    void sauThangCoThangTrongBangKhongVaChiMotLanDoc() {
        dao.thu.put(YearMonth.of(2026, 7), new BigDecimal("205000000"));
        dao.thu.put(T11, new BigDecimal("186000000"));
        dao.thu.put(YearMonth.of(2026, 5), new BigDecimal("999"));
        List<ThuThang> ds = service.tongQuan(QT).sauThang();
        assertEquals(1, dao.soLanTheoThang, "Một truy vấn cho 6 tháng");
        assertEquals(YearMonth.of(2026, 6), dao.tu);
        assertEquals(T11, dao.den);
        assertEquals(List.of(
                new ThuThang(YearMonth.of(2026, 6), BigDecimal.ZERO),
                new ThuThang(YearMonth.of(2026, 7),
                        new BigDecimal("205000000")),
                new ThuThang(YearMonth.of(2026, 8), BigDecimal.ZERO),
                new ThuThang(YearMonth.of(2026, 9), BigDecimal.ZERO),
                new ThuThang(YearMonth.of(2026, 10), BigDecimal.ZERO),
                new ThuThang(T11, new BigDecimal("186000000"))), ds);
    }

    @Test
    void cacKhoanBatBuocTruoc() {
        assertEquals(List.of(DICH_VU, QUY), service.cacKhoan(QT));
    }

    /** AC-052: khoản bắt buộc theo tháng đã chốt. */
    @Test
    void khoanBatBuocTheoThangDaChot() {
        dao.daChot.add(T11);
        ThongKeKhoan tk = service.theoKhoan(QT, DICH_VU, T11);
        assertTrue(tk.batBuoc());
        assertEquals(T11, tk.thang());
        assertEquals(new BigDecimal("700000"), tk.tongThu());
        assertEquals(new BigDecimal("1000000"), tk.tongPhaiThu());
        assertEquals(4, tk.soHoPhaiDong());
        assertEquals(3, tk.soHoDaDong());
    }

    @Test
    void khoanBatBuocCanThangDaChot() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.theoKhoan(QT, DICH_VU, null));
        assertEquals("Chưa chọn tháng", e.getMessage());
        e = assertThrows(NghiepVuException.class,
                () -> service.theoKhoan(QT, DICH_VU, T11));
        assertEquals("Tháng 11/2026 chưa chốt công nợ", e.getMessage());
        e = assertThrows(NghiepVuException.class,
                () -> service.theoKhoan(QT, null, T11));
        assertEquals("Chưa chọn khoản thu", e.getMessage());
    }

    /** Khoản tự nguyện: đợt là thời gian thu, không cần tháng. */
    @Test
    void khoanTuNguyenKhongCanThang() {
        ThongKeKhoan tk = service.theoKhoan(QT, QUY, null);
        assertFalse(tk.batBuoc());
        assertNull(tk.thang());
        assertEquals(new BigDecimal("150000"), tk.tongThu());
        assertEquals(2, tk.soHoDaDong());
        assertEquals(1, tk.soHoKhongDong());
    }
}
