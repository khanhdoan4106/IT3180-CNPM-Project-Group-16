package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
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

    private static ThongTinNhanKhau con(String cccd) {
        return new ThongTinNhanKhau("Hoang Minh Chau",
                LocalDate.of(2010, 5, 14), GioiTinh.NU, cccd,
                QuanHeChuHo.CON);
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
        assertEquals(LocalDate.of(2010, 5, 14), nk.getNgaySinh());
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
                LocalDate.of(1980, 1, 30), GioiTinh.NAM, "",
                QuanHeChuHo.CHU_HO);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(ho.getId(), tt));
        assertTrue(e.getMessage().contains("mot chu ho"));
        assertEquals(1, service.theoHo(ho.getId()).size());
    }

    @Test
    void suaThanhVienThanhChuHoBiTuChoi() {
        NhanKhau nk = service.them(ho.getId(), con(""));
        ThongTinNhanKhau tt = new ThongTinNhanKhau(nk.getHoTen(),
                nk.getNgaySinh(), nk.getGioiTinh(), "", QuanHeChuHo.CHU_HO);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.capNhat(nk.getId(), tt));
        assertTrue(e.getMessage().contains("mot chu ho"));
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
        for (String truong : List.of("Ho ten", "Ngay sinh", "Gioi tinh",
                "Quan he voi chu ho")) {
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
    void theoHoChiLayThanhVienCuaHoDo() {
        HoGiaDinh khac = themHo("A0602", "001182009999");
        service.them(khac.getId(), con(""));
        assertEquals(1, service.theoHo(ho.getId()).size());
        assertEquals(2, service.theoHo(khac.getId()).size());
    }
}
