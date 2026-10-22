package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.DotCuTruDao;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TinhTrangCuTru;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DotCuTruServiceTest {

    private static final LocalDate HOM_NAY = LocalDate.of(2026, 11, 2);

    private KhoGia kho;
    private HoGiaDinhService hoService;
    private NhanKhauService nkService;
    private DotCuTruService service;
    private HoGiaDinh ho;
    private NhanKhau thuongTru;
    private NhanKhau tamTru;

    @BeforeEach
    void chuanBi() {
        kho = new KhoGia();
        Clock dongHo = Clock.fixed(
                HOM_NAY.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toInstant(),
                ZoneId.of("Asia/Ho_Chi_Minh"));
        hoService = new HoGiaDinhService(kho.hoDao, kho.nhanKhauDao, dongHo);
        nkService = new NhanKhauService(kho.nhanKhauDao, kho.hoDao, dongHo);
        service = new DotCuTruService(kho.dotDao, kho.nhanKhauDao,
                kho.hoDao, dongHo);
        ho = hoService.them(
                new ThongTinHo("A0601", LoaiCanHo.CAN_HO, "6", "75.5", "",
                        LocalDate.of(2023, 7, 15)),
                new ThongTinNhanKhau("Nguyen Thi Lan",
                        LocalDate.of(1982, 8, 25), GioiTinh.NU,
                        "001182004567", null));
        thuongTru = kho.nhanKhauDao.theoHo(ho.getId()).get(0);
        tamTru = nkService.them(ho.getId(),
                new ThongTinNhanKhau("Tran Van Thue",
                        LocalDate.of(1995, 3, 3), GioiTinh.NAM,
                        "036095001111", QuanHeChuHo.KHAC, LoaiCuTru.TAM_TRU,
                        "Xa Hai Hau, Nam Dinh"),
                dot(LoaiDotCuTru.TAM_TRU, HOM_NAY.minusMonths(6),
                        HOM_NAY.plusMonths(6)));
    }

    private static ThongTinDot dot(LoaiDotCuTru loai, LocalDate tu,
            LocalDate den) {
        return new ThongTinDot(loai, tu, den, "Cong tac");
    }

    private ThongTinDot tamVang(LocalDate tu, LocalDate den) {
        return dot(LoaiDotCuTru.TAM_VANG, tu, den);
    }

    private TinhTrangCuTru tinhTrang(NhanKhau nk) {
        return service.tinhTrangTheoHo(ho.getId())
                .getOrDefault(nk.getId(), TinhTrangCuTru.TRONG);
    }

    @Test
    void ghiNhanTamVangVaCapNhatTinhTrang() {
        DotCuTru d = service.ghiNhan(thuongTru.getId(),
                tamVang(HOM_NAY.minusDays(3), HOM_NAY.plusDays(10)));
        assertTrue(d.getId() > 0);
        assertEquals("Cong tac", d.getLyDo());
        assertEquals(1, service.theoNhanKhau(thuongTru.getId()).size());
        assertEquals(HOM_NAY.plusDays(10), tinhTrang(thuongTru).tamVangDen());
    }

    @Test
    void thieuNgayBatDauHoacKetThucThiBaoLoiVaKhongLuu() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(thuongTru.getId(),
                        tamVang(null, null)));
        assertTrue(e.getMessage().contains("Ngay bat dau"));
        assertTrue(e.getMessage().contains("Ngay ket thuc"));
        assertTrue(service.theoNhanKhau(thuongTru.getId()).isEmpty());
    }

    @Test
    void ngayKetThucTruocNgayBatDauBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(thuongTru.getId(),
                        tamVang(HOM_NAY, HOM_NAY.minusDays(1))));
        assertTrue(service.theoNhanKhau(thuongTru.getId()).isEmpty());
    }

    @Test
    void dotMotNgayDuocChapNhan() {
        service.ghiNhan(thuongTru.getId(), tamVang(HOM_NAY, HOM_NAY));
        assertEquals(HOM_NAY, tinhTrang(thuongTru).tamVangDen());
    }

    @Test
    void cacDotCuaCungMotNguoiKhongDuocChongNgay() {
        service.ghiNhan(thuongTru.getId(), tamVang(LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)));
        for (LocalDate[] k : List.of(
                new LocalDate[] {LocalDate.of(2026, 1, 31),
                        LocalDate.of(2026, 2, 5)},
                new LocalDate[] {LocalDate.of(2025, 12, 20),
                        LocalDate.of(2026, 1, 1)},
                new LocalDate[] {LocalDate.of(2026, 1, 10),
                        LocalDate.of(2026, 1, 12)},
                new LocalDate[] {LocalDate.of(2025, 12, 1),
                        LocalDate.of(2026, 3, 1)})) {
            NghiepVuException e = assertThrows(NghiepVuException.class,
                    () -> service.ghiNhan(thuongTru.getId(),
                            tamVang(k[0], k[1])), k[0] + " - " + k[1]);
            assertTrue(e.getMessage().contains("01/01/2026 - 31/01/2026"));
        }
        service.ghiNhan(thuongTru.getId(), tamVang(LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 10)));
        assertEquals(2, service.theoNhanKhau(thuongTru.getId()).size());
    }

    @Test
    void haiMayCungGhiChongNgayThiBaoChongNgay() {
        kho.trungKhiGhi = DotCuTruDao.CHONG_NGAY;
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(thuongTru.getId(),
                        tamVang(HOM_NAY, HOM_NAY.plusDays(1))));
        assertEquals(DotCuTruService.LOI_CHONG_NGAY, e.getMessage());
    }

    @Test
    void tamTruChiGhiChoNguoiTamTruConTamVangChiChoNguoiThuongTru() {
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(thuongTru.getId(),
                        dot(LoaiDotCuTru.TAM_TRU, HOM_NAY,
                                HOM_NAY.plusDays(5))));
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(tamTru.getId(),
                        tamVang(HOM_NAY.plusYears(1),
                                HOM_NAY.plusYears(1).plusDays(5))));
    }

    @Test
    void tamTruHetHanMaChuaGiaHanThiBaoQuaHan() {
        NhanKhau quaHan = nkService.them(ho.getId(),
                new ThongTinNhanKhau("Le Thi Thue", LocalDate.of(1998, 1, 1),
                        GioiTinh.NU, "036198002222", QuanHeChuHo.KHAC,
                        LoaiCuTru.TAM_TRU, "Thai Binh"),
                dot(LoaiDotCuTru.TAM_TRU, HOM_NAY.minusYears(1),
                        HOM_NAY.minusDays(1)));
        TinhTrangCuTru tt = tinhTrang(quaHan);
        assertTrue(tt.quaHanTamTru());
        assertEquals(HOM_NAY.minusDays(1), tt.hanTamTru());

        service.ghiNhan(quaHan.getId(), dot(LoaiDotCuTru.TAM_TRU, HOM_NAY,
                HOM_NAY.plusMonths(6)));
        tt = tinhTrang(quaHan);
        assertFalse(tt.quaHanTamTru());
        assertEquals(HOM_NAY.plusMonths(6), tt.hanTamTru());
    }

    @Test
    void tamTruConHanKhongBaoQuaHanVaNguoiThuongTruKhongCoHan() {
        assertFalse(tinhTrang(tamTru).quaHanTamTru());
        assertEquals(HOM_NAY.plusMonths(6), tinhTrang(tamTru).hanTamTru());
        assertNull(tinhTrang(thuongTru).hanTamTru());
        assertFalse(tinhTrang(thuongTru).quaHanTamTru());
    }

    @Test
    void tamVangHetHanThiTuHetDangTamVang() {
        service.ghiNhan(thuongTru.getId(), tamVang(HOM_NAY.minusDays(10),
                HOM_NAY.minusDays(1)));
        assertNull(tinhTrang(thuongTru).tamVangDen());
    }

    @Test
    void nguoiDaChuyenDiKhongGhiDuoc() {
        nkService.danhDauChuyenDi(tamTru.getId());
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(tamTru.getId(),
                        dot(LoaiDotCuTru.TAM_TRU, HOM_NAY.plusYears(1),
                                HOM_NAY.plusYears(2))));
        assertFalse(tinhTrang(tamTru).quaHanTamTru());
    }
}
