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
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TinhTrangCuTru;
import com.bluemoon.model.TrangThaiHo;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DotCuTruServiceTest {

    private static final TaiKhoan QT = KhoGia.QT;

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
        ho = hoService.them(QT,
                new ThongTinHo("A0601", LoaiCanHo.CAN_HO, "6", "75.5", "",
                        LocalDate.of(2023, 7, 15)),
                new ThongTinNhanKhau("Nguyen Thi Lan",
                        LocalDate.of(1982, 8, 25), GioiTinh.NU,
                        "001182004567", null));
        thuongTru = kho.nhanKhauDao.theoHo(ho.getId()).get(0);
        tamTru = nkService.them(QT, ho.getId(),
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
        return service.tinhTrangTheoHo(QT, ho.getId())
                .getOrDefault(nk.getId(), TinhTrangCuTru.TRONG);
    }

    @Test
    void ghiNhanTamVangVaCapNhatTinhTrang() {
        DotCuTru d = service.ghiNhan(QT, thuongTru.getId(),
                tamVang(HOM_NAY.minusDays(3), HOM_NAY.plusDays(10)));
        assertTrue(d.getId() > 0);
        assertEquals("Cong tac", d.getLyDo());
        assertEquals(1, service.theoNhanKhau(QT, thuongTru.getId()).size());
        assertEquals(HOM_NAY.plusDays(10), tinhTrang(thuongTru).tamVangDen());
    }

    @Test
    void thieuNgayBatDauHoacKetThucThiBaoLoiVaKhongLuu() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(QT, thuongTru.getId(),
                        tamVang(null, null)));
        assertTrue(e.getMessage().contains("Ngày bắt đầu"));
        assertTrue(e.getMessage().contains("Ngày kết thúc"));
        assertTrue(service.theoNhanKhau(QT, thuongTru.getId()).isEmpty());
    }

    @Test
    void ngayKetThucTruocNgayBatDauBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(QT, thuongTru.getId(),
                        tamVang(HOM_NAY, HOM_NAY.minusDays(1))));
        assertTrue(service.theoNhanKhau(QT, thuongTru.getId()).isEmpty());
    }

    @Test
    void dotMotNgayDuocChapNhan() {
        service.ghiNhan(QT, thuongTru.getId(), tamVang(HOM_NAY, HOM_NAY));
        assertEquals(HOM_NAY, tinhTrang(thuongTru).tamVangDen());
    }

    @Test
    void cacDotCuaCungMotNguoiKhongDuocChongNgay() {
        service.ghiNhan(QT, thuongTru.getId(), tamVang(LocalDate.of(2026, 1, 1),
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
                    () -> service.ghiNhan(QT, thuongTru.getId(),
                            tamVang(k[0], k[1])), k[0] + " - " + k[1]);
            assertTrue(e.getMessage().contains("01/01/2026 - 31/01/2026"));
        }
        service.ghiNhan(QT, thuongTru.getId(), tamVang(LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 10)));
        assertEquals(2, service.theoNhanKhau(QT, thuongTru.getId()).size());
    }

    @Test
    void haiMayCungGhiChongNgayThiBaoChongNgay() {
        kho.trungKhiGhi = DotCuTruDao.CHONG_NGAY;
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(QT, thuongTru.getId(),
                        tamVang(HOM_NAY, HOM_NAY.plusDays(1))));
        assertEquals(DotCuTruService.LOI_CHONG_NGAY, e.getMessage());
    }

    @Test
    void tamTruChiGhiChoNguoiTamTruConTamVangChiChoNguoiThuongTru() {
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(QT, thuongTru.getId(),
                        dot(LoaiDotCuTru.TAM_TRU, HOM_NAY,
                                HOM_NAY.plusDays(5))));
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(QT, tamTru.getId(),
                        tamVang(HOM_NAY.plusYears(1),
                                HOM_NAY.plusYears(1).plusDays(5))));
    }

    @Test
    void tamTruHetHanMaChuaGiaHanThiBaoQuaHan() {
        NhanKhau quaHan = nkService.them(QT, ho.getId(),
                new ThongTinNhanKhau("Le Thi Thue", LocalDate.of(1998, 1, 1),
                        GioiTinh.NU, "036198002222", QuanHeChuHo.KHAC,
                        LoaiCuTru.TAM_TRU, "Thai Binh"),
                dot(LoaiDotCuTru.TAM_TRU, HOM_NAY.minusYears(1),
                        HOM_NAY.minusDays(1)));
        TinhTrangCuTru tt = tinhTrang(quaHan);
        assertTrue(tt.quaHanTamTru());
        assertEquals(HOM_NAY.minusDays(1), tt.hanTamTru());

        service.ghiNhan(QT, quaHan.getId(), dot(LoaiDotCuTru.TAM_TRU, HOM_NAY,
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
        service.ghiNhan(QT, thuongTru.getId(), tamVang(HOM_NAY.minusDays(10),
                HOM_NAY.minusDays(1)));
        assertNull(tinhTrang(thuongTru).tamVangDen());
    }

    @Test
    void nguoiDaChuyenDiKhongGhiDuoc() {
        nkService.danhDauChuyenDi(QT, tamTru.getId());
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(QT, tamTru.getId(),
                        dot(LoaiDotCuTru.TAM_TRU, HOM_NAY.plusYears(1),
                                HOM_NAY.plusYears(2))));
        assertFalse(tinhTrang(tamTru).quaHanTamTru());
    }

    @Test
    void hoDaChuyenDiThiKhongGhiDuocChoThanhVienNao() {
        hoService.danhDauChuyenDi(QT, ho.getId());
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(QT, thuongTru.getId(),
                        tamVang(HOM_NAY, HOM_NAY.plusDays(5))));
        assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(QT, tamTru.getId(),
                        dot(LoaiDotCuTru.TAM_TRU, HOM_NAY.plusYears(1),
                                HOM_NAY.plusYears(2))));
        assertEquals(1, kho.dot.size());
    }

    @Test
    void hoDaChuyenDiThiChanDuThanhVienConGhiDangO() {
        kho.ho.put(ho.getId(), ho.voiTrangThai(TrangThaiHo.DA_CHUYEN_DI));
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.ghiNhan(QT, thuongTru.getId(),
                        tamVang(HOM_NAY, HOM_NAY.plusDays(5))));
        assertTrue(e.getMessage().contains("đã chuyển đi"));
        assertEquals(1, kho.dot.size());
    }

    // US-022, US-027

    @Test
    void thuQuyKhongGhiDuocTamTruTamVang() { // AC-060
        assertThrows(NghiepVuException.class, () -> service.ghiNhan(
                KhoGia.TQ, thuongTru.getId(), new ThongTinDot(
                        LoaiDotCuTru.TAM_VANG, HOM_NAY, HOM_NAY.plusDays(5),
                        null)));
    }

    @Test
    void ghiTamVangGhiLichSu() { // US-027
        int truoc = kho.lichSu.size();
        service.ghiNhan(QT, thuongTru.getId(), new ThongTinDot(
                LoaiDotCuTru.TAM_VANG, HOM_NAY, HOM_NAY.plusDays(5), null));
        assertEquals("TAM_VANG:" + ho.getId() + ":" + thuongTru.getId()
                + ":1", kho.lichSu.get(truoc));
    }
}
