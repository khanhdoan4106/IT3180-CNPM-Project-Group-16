package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TrangThaiNhanKhau;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NhanKhauServiceTest {

    private static final LocalDate HOM_NAY = LocalDate.of(2026, 11, 2);
    private static final String CCCD_CHU_HO = "001182004567";

    private KhoGia kho;
    private HoGiaDinhService hoService;
    private NhanKhauService service;
    private HoGiaDinh ho;

    @BeforeEach
    void chuanBi() {
        kho = new KhoGia();
        Clock dongHo = Clock.fixed(
                HOM_NAY.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toInstant(),
                ZoneId.of("Asia/Ho_Chi_Minh"));
        hoService = new HoGiaDinhService(kho.hoDao, kho.nhanKhauDao, dongHo);
        service = new NhanKhauService(kho.nhanKhauDao, kho.hoDao, dongHo);
        ho = themHo("A0601", CCCD_CHU_HO);
    }

    private HoGiaDinh themHo(String ma, String cccdChuHo) {
        return hoService.them(
                new ThongTinHo(ma, LoaiCanHo.CAN_HO, "6", "75.5", "",
                        LocalDate.of(2023, 7, 15)),
                new ThongTinNhanKhau("Nguyen Thi Lan",
                        LocalDate.of(1982, 8, 25), GioiTinh.NU, cccdChuHo,
                        null));
    }

    /** Con 11 tuoi: chua bat buoc co CCCD. */
    private static ThongTinNhanKhau con(String cccd) {
        return new ThongTinNhanKhau("Hoang Minh Chau",
                LocalDate.of(2015, 5, 14), GioiTinh.NU, cccd,
                QuanHeChuHo.CON);
    }

    private static ThongTinNhanKhau nguoiLon(String ten, String cccd) {
        return new ThongTinNhanKhau(ten, LocalDate.of(1980, 1, 30),
                GioiTinh.NAM, cccd, QuanHeChuHo.KHAC);
    }

    private NhanKhau chuHo() {
        return kho.nhanKhauDao.theoHo(ho.getId()).stream()
                .filter(n -> n.getQuanHe() == QuanHeChuHo.CHU_HO)
                .findFirst().orElseThrow();
    }

    @Test
    void themNhanKhauLuuVaoHoVaCapNhatSoThanhVien() {
        NhanKhau nk = service.them(ho.getId(), con("001310002345"));
        assertTrue(nk.getId() > 0);
        assertEquals(ho.getId(), nk.getHoId());
        assertEquals("Hoang Minh Chau", nk.getHoTen());
        assertEquals(LocalDate.of(2015, 5, 14), nk.getNgaySinh());
        assertEquals(GioiTinh.NU, nk.getGioiTinh());
        assertEquals("001310002345", nk.getSoCccd());
        assertEquals(2, service.theoHo(ho.getId()).size());
        assertEquals(2, hoService.timKiem("A0601", false).get(0)
                .soThanhVien());
    }

    @Test
    void luuDungQuanHeDaChon() {
        int i = 0;
        for (QuanHeChuHo qh : List.of(QuanHeChuHo.VO_CHONG, QuanHeChuHo.CON,
                QuanHeChuHo.KHAC)) {
            ThongTinNhanKhau tt = new ThongTinNhanKhau("Nguoi " + i,
                    LocalDate.of(2000, 1, 1), GioiTinh.NAM,
                    "00100000000" + i++, qh);
            NhanKhau nk = service.them(ho.getId(), tt);
            assertEquals(qh, kho.nhanKhau.get(nk.getId()).getQuanHe());
        }
    }

    @Test
    void themChuHoThuHaiBiTuChoi() {
        ThongTinNhanKhau tt = new ThongTinNhanKhau("Hoang Van Nam",
                LocalDate.of(1980, 1, 30), GioiTinh.NAM, "001080007890",
                QuanHeChuHo.CHU_HO);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(), tt));
        assertTrue(e.getMessage().contains("một chủ hộ"));
        assertEquals(1, service.theoHo(ho.getId()).size());
    }

    @Test
    void suaThanhVienThanhChuHoBiTuChoi() {
        NhanKhau nk = service.them(ho.getId(), con(""));
        ThongTinNhanKhau tt = new ThongTinNhanKhau(nk.getHoTen(),
                nk.getNgaySinh(), nk.getGioiTinh(), "", QuanHeChuHo.CHU_HO);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.capNhat(nk.getId(), tt));
        assertTrue(e.getMessage().contains("một chủ hộ"));
        assertEquals(QuanHeChuHo.CON,
                kho.nhanKhau.get(nk.getId()).getQuanHe());
    }

    @Test
    void chuHoKhongDoiDuocQuanHe() {
        NhanKhau ch = chuHo();
        ThongTinNhanKhau tt = new ThongTinNhanKhau(ch.getHoTen(),
                ch.getNgaySinh(), ch.getGioiTinh(), ch.getSoCccd(),
                QuanHeChuHo.VO_CHONG);
        assertThrows(NghiepVuException.class,
                () -> service.capNhat(ch.getId(), tt));
        assertEquals(QuanHeChuHo.CHU_HO, chuHo().getQuanHe());
    }

    @Test
    void suaThongTinChuHoGiuQuanHeThiDuoc() {
        NhanKhau ch = chuHo();
        ThongTinNhanKhau tt = new ThongTinNhanKhau("Nguyen Thi Lan Anh",
                ch.getNgaySinh(), ch.getGioiTinh(), ch.getSoCccd(),
                QuanHeChuHo.CHU_HO);
        service.capNhat(ch.getId(), tt);
        assertEquals("Nguyen Thi Lan Anh", chuHo().getHoTen());
        assertEquals("Nguyen Thi Lan Anh",
                hoService.timKiem("A0601", false).get(0).tenChuHo());
    }

    @Test
    void cccdDaTonTaiBiCanhBaoVaKhongLuu() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(), con(CCCD_CHU_HO)));
        assertTrue(e.getMessage().contains(CCCD_CHU_HO));
        assertEquals(1, service.theoHo(ho.getId()).size());
    }

    @Test
    void cccdTrungVoiNguoiOHoKhacCungBiCanhBao() {
        HoGiaDinh khac = themHo("A0602", "001182009999");
        assertThrows(NghiepVuException.class,
                () -> service.them(khac.getId(), con(CCCD_CHU_HO)));
    }

    @Test
    void suaGiuCccdCuaChinhMinhKhongBaoTrung() {
        NhanKhau nk = service.them(ho.getId(), con("001310002345"));
        ThongTinNhanKhau tt = new ThongTinNhanKhau("Hoang Minh Chau Anh",
                nk.getNgaySinh(), nk.getGioiTinh(), "001310002345",
                QuanHeChuHo.CON);
        assertEquals("Hoang Minh Chau Anh",
                service.capNhat(nk.getId(), tt).getHoTen());
    }

    @Test
    void suaSangCccdCuaNguoiKhacBiBaoTrung() {
        NhanKhau nk = service.them(ho.getId(), con("001310002345"));
        assertThrows(NghiepVuException.class,
                () -> service.capNhat(nk.getId(), con(CCCD_CHU_HO)));
        assertEquals("001310002345",
                kho.nhanKhau.get(nk.getId()).getSoCccd());
    }

    @Test
    void cccdDeTrongLuuNullVaNhieuNguoiCungDeTrong() {
        NhanKhau a = service.them(ho.getId(), con(""));
        NhanKhau b = service.them(ho.getId(), con("  "));
        assertNull(a.getSoCccd());
        assertNull(b.getSoCccd());
        assertEquals(3, service.theoHo(ho.getId()).size());
    }

    @Test
    void cccdDuocBoKhoangTrangVaDauCham() {
        NhanKhau nk = service.them(ho.getId(), con(" 001 310.002 345 "));
        assertEquals("001310002345", nk.getSoCccd());
    }

    @Test
    void cccdSaiDinhDangBiTuChoi() {
        for (String cccd : List.of("12345", "00131000234a",
                "0013100023456")) {
            assertThrows(NghiepVuException.class,
                    () -> service.them(ho.getId(), con(cccd)), cccd);
        }
        assertEquals(1, service.theoHo(ho.getId()).size());
    }

    @Test
    void thieuTruongBatBuocBaoDuCacTruongVaKhongLuu() {
        ThongTinNhanKhau tt = new ThongTinNhanKhau(" ", null, null, "",
                null);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(), tt));
        for (String truong : List.of("Họ tên", "Ngày sinh", "Giới tính",
                "Quan hệ với chủ hộ")) {
            assertTrue(e.getMessage().contains(truong), truong);
        }
        assertEquals(1, service.theoHo(ho.getId()).size());
    }

    @Test
    void ngaySinhSauHomNayBiTuChoi() {
        ThongTinNhanKhau tt = new ThongTinNhanKhau("Be Moi",
                HOM_NAY.plusDays(1), GioiTinh.NAM, "", QuanHeChuHo.CON);
        assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(), tt));
    }

    @Test
    void hoKhongTonTaiHoacDaChuyenDiKhongThemDuoc() {
        assertThrows(NghiepVuException.class,
                () -> service.them(999, con("")));
        hoService.danhDauChuyenDi(ho.getId());
        assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(), con("")));
        NhanKhau ch = chuHo();
        assertThrows(NghiepVuException.class,
                () -> service.capNhat(ch.getId(), new ThongTinNhanKhau(
                        "Ten Moi", ch.getNgaySinh(), ch.getGioiTinh(), "",
                        QuanHeChuHo.CHU_HO)));
    }

    @Test
    void tuDu14TuoiBatBuocCoCccd() {
        ThongTinNhanKhau du14 = new ThongTinNhanKhau("Tron 14",
                HOM_NAY.minusYears(14), GioiTinh.NAM, "", QuanHeChuHo.CON);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(), du14));
        assertTrue(e.getMessage().contains("Số CCCD"));
        ThongTinNhanKhau chua14 = new ThongTinNhanKhau("Gan 14",
                HOM_NAY.minusYears(14).plusDays(1), GioiTinh.NU, "",
                QuanHeChuHo.CON);
        assertNull(service.them(ho.getId(), chua14).getSoCccd());
    }

    @Test
    void thanhVienChuyenDiKhongTinhVaoSoThanhVien() {
        NhanKhau nk = service.them(ho.getId(),
                nguoiLon("Hoang Van Nam", "001080007890"));
        service.danhDauChuyenDi(nk.getId());
        assertEquals(TrangThaiNhanKhau.DA_CHUYEN_DI,
                kho.nhanKhau.get(nk.getId()).getTrangThai());
        assertEquals(1, hoService.timKiem("A0601", false).get(0)
                .soThanhVien());
        assertEquals(2, service.theoHo(ho.getId()).size());
    }

    @Test
    void nguoiChuyenDiKhaiBaoLaiDuocOHoKhacVoiCungCccd() {
        NhanKhau nk = service.them(ho.getId(),
                nguoiLon("Hoang Van Nam", "001080007890"));
        HoGiaDinh moi = themHo("A0602", "001182009999");
        assertThrows(NghiepVuException.class, () -> service.them(
                moi.getId(), nguoiLon("Hoang Van Nam", "001080007890")));
        service.danhDauChuyenDi(nk.getId());
        NhanKhau lai = service.them(moi.getId(),
                nguoiLon("Hoang Van Nam", "001080007890"));
        assertEquals(moi.getId(), lai.getHoId());
    }

    @Test
    void chuHoKhongChuyenDiRiengDuoc() {
        NhanKhau ch = chuHo();
        assertThrows(NghiepVuException.class,
                () -> service.danhDauChuyenDi(ch.getId()));
        assertEquals(TrangThaiNhanKhau.DANG_O, chuHo().getTrangThai());
    }

    @Test
    void nguoiDaChuyenDiKhongSuaVaKhongChuyenDiLaiDuoc() {
        NhanKhau nk = service.them(ho.getId(), con(""));
        service.danhDauChuyenDi(nk.getId());
        assertThrows(NghiepVuException.class,
                () -> service.capNhat(nk.getId(), con("")));
        assertThrows(NghiepVuException.class,
                () -> service.danhDauChuyenDi(nk.getId()));
    }

    @Test
    void haiMayCungNhapMotCccdThiBaoTrungCccd() {
        kho.trungKhiGhi = "uq_nk_cccd_dang_o";
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(), con("001310002345")));
        assertEquals("Số CCCD 001310002345 đã tồn tại", e.getMessage());
    }

    @Test
    void haiMayCungThemChuHoThiBaoMotChuHo() {
        NhanKhau nk = service.them(ho.getId(), con(""));
        kho.trungKhiGhi = "uq_nk_chu_ho";
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.capNhat(nk.getId(), con("")));
        assertEquals(NhanKhauService.LOI_HAI_CHU_HO, e.getMessage());
    }

    private static ThongTinNhanKhau nguoiTamTru(String cccd, String noi) {
        return new ThongTinNhanKhau("Tran Van Thue", LocalDate.of(1995, 3, 3),
                GioiTinh.NAM, cccd, QuanHeChuHo.KHAC, LoaiCuTru.TAM_TRU, noi);
    }

    private static ThongTinDot dotTamTru(LocalDate tu, LocalDate den) {
        return new ThongTinDot(null, tu, den, null);
    }

    @Test
    void themNguoiTamTruLuuLuonDotTamTruDauTien() {
        NhanKhau nk = service.them(ho.getId(),
                nguoiTamTru("036095001111", " Hai Hau,  Nam Dinh "),
                dotTamTru(HOM_NAY, HOM_NAY.plusMonths(6)));
        assertEquals(LoaiCuTru.TAM_TRU, nk.getLoaiCuTru());
        assertEquals("Hai Hau, Nam Dinh", nk.getNoiThuongTru());
        assertEquals(1, kho.dot.size());
        assertEquals(nk.getId(),
                kho.dot.values().iterator().next().getNhanKhauId());
        assertEquals(LoaiDotCuTru.TAM_TRU,
                kho.dot.values().iterator().next().getLoai());
    }

    @Test
    void nguoiTamTruThieuDotHoacNoiThuongTruThiKhongLuuGi() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(),
                        nguoiTamTru("036095001111", ""), null));
        assertTrue(e.getMessage().contains("Nơi thường trú"));
        assertTrue(e.getMessage().contains("Ngày bắt đầu tạm trú"));
        assertTrue(e.getMessage().contains("Ngày kết thúc tạm trú"));
        assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(),
                        nguoiTamTru("036095001111", "Nam Dinh"),
                        dotTamTru(HOM_NAY, HOM_NAY.minusDays(1))));
        assertEquals(1, kho.nhanKhau.size());
        assertTrue(kho.dot.isEmpty());
    }

    @Test
    void nguoiTamTruTrungCccdThiKhongLuuDot() {
        assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(),
                        nguoiTamTru(CCCD_CHU_HO, "Nam Dinh"),
                        dotTamTru(HOM_NAY, HOM_NAY.plusMonths(6))));
        assertTrue(kho.dot.isEmpty());
    }

    @Test
    void nguoiThuongTruBoQuaDotTamTru() {
        service.them(ho.getId(), con(""),
                dotTamTru(HOM_NAY, HOM_NAY.plusMonths(6)));
        assertTrue(kho.dot.isEmpty());
    }

    @Test
    void suaKhongDoiLoaiCuTruNhungSuaDuocNoiThuongTru() {
        NhanKhau nk = service.them(ho.getId(),
                nguoiTamTru("036095001111", "Nam Dinh"),
                dotTamTru(HOM_NAY, HOM_NAY.plusMonths(6)));
        NhanKhau sua = service.capNhat(nk.getId(), new ThongTinNhanKhau(
                nk.getHoTen(), nk.getNgaySinh(), nk.getGioiTinh(),
                nk.getSoCccd(), nk.getQuanHe(), LoaiCuTru.THUONG_TRU,
                "Thai Binh"));
        assertEquals(LoaiCuTru.TAM_TRU, sua.getLoaiCuTru());
        assertEquals("Thai Binh", kho.nhanKhau.get(nk.getId())
                .getNoiThuongTru());
        assertThrows(NghiepVuException.class,
                () -> service.capNhat(nk.getId(), nguoiTamTru(
                        "036095001111", " ")));
    }

    @Test
    void theoHoChiLayThanhVienCuaHoDo() {
        HoGiaDinh khac = themHo("A0602", "001182009999");
        service.them(khac.getId(), con(""));
        assertEquals(1, service.theoHo(ho.getId()).size());
        assertEquals(2, service.theoHo(khac.getId()).size());
    }
}
