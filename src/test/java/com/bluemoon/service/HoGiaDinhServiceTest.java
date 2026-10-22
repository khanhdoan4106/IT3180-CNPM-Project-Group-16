package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiNhanKhau;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HoGiaDinhServiceTest {

    private static final LocalDate HOM_NAY = LocalDate.of(2026, 11, 2);

    private KhoGia kho;
    private HoGiaDinhService service;
    private int soCccd;

    @BeforeEach
    void chuanBi() {
        kho = new KhoGia();
        Clock dongHo = Clock.fixed(
                HOM_NAY.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toInstant(),
                ZoneId.of("Asia/Ho_Chi_Minh"));
        service = new HoGiaDinhService(kho.hoDao, kho.nhanKhauDao, dongHo);
    }

    private static ThongTinHo hopLe(String ma) {
        return new ThongTinHo(ma, LoaiCanHo.CAN_HO, "6", "75.5",
                "0987654321", LocalDate.of(2023, 7, 15));
    }

    /** Chu ho hop le, moi lan goi mot so CCCD khac nhau. */
    private ThongTinNhanKhau chuHo() {
        return chuHo(String.format("0011820%05d", ++soCccd));
    }

    private static ThongTinNhanKhau chuHo(String cccd) {
        return new ThongTinNhanKhau("Nguyen Thi Lan",
                LocalDate.of(1982, 8, 25), GioiTinh.NU, cccd, null);
    }

    private HoGiaDinh them(ThongTinHo tt) {
        return service.them(tt, chuHo());
    }

    @Test
    void themDayDuThanhCongVaHienTrongDanhSach() {
        HoGiaDinh ho = them(hopLe("A0601"));
        assertTrue(ho.getId() > 0);
        assertEquals(TrangThaiHo.DANG_O, ho.getTrangThai());
        assertEquals(1, service.timKiem("", false).size());
    }

    @Test
    void themHoTaoLuonChuHoVaDemMotThanhVien() {
        HoGiaDinh ho = them(hopLe("A0601"));
        List<NhanKhau> thanhVien = kho.nhanKhauDao.theoHo(ho.getId());
        assertEquals(1, thanhVien.size());
        assertEquals(QuanHeChuHo.CHU_HO, thanhVien.get(0).getQuanHe());
        TomTatHo dong = service.timKiem("", false).get(0);
        assertEquals("Nguyen Thi Lan", dong.tenChuHo());
        assertEquals(1, dong.soThanhVien());
    }

    @Test
    void quanHeNhapVaoBiBoQuaChuHoLuonLaChuHo() {
        ThongTinNhanKhau tt = new ThongTinNhanKhau("Le Van A",
                LocalDate.of(1990, 1, 1), GioiTinh.NAM, "001090000001",
                QuanHeChuHo.CON);
        HoGiaDinh ho = service.them(hopLe("A0601"), tt);
        assertEquals(QuanHeChuHo.CHU_HO,
                kho.nhanKhauDao.theoHo(ho.getId()).get(0).getQuanHe());
    }

    @Test
    void dienTichLuuDungGiaTriDaNhap() {
        ThongTinHo tt = new ThongTinHo("A0602", LoaiCanHo.CAN_HO, "6",
                "75,50", "", LocalDate.of(2024, 1, 1));
        HoGiaDinh ho = them(tt);
        assertEquals(0, new BigDecimal("75.5").compareTo(ho.getDienTich()));
    }

    @Test
    void thieuTruongBatBuocBaoDuCacTruongVaKhongLuu() {
        ThongTinHo tt = new ThongTinHo(" ", null, "", "", "", null);
        ThongTinNhanKhau trong = new ThongTinNhanKhau("  ", null, null, "",
                null);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(tt, trong));
        for (String truong : List.of("Ma can ho", "Loai can ho", "Tang",
                "Dien tich", "Ngay chuyen den", "Ho ten chu ho",
                "Ngay sinh chu ho", "Gioi tinh chu ho")) {
            assertTrue(e.getMessage().contains(truong), truong);
        }
        assertTrue(kho.ho.isEmpty());
        assertTrue(kho.nhanKhau.isEmpty());
    }

    @Test
    void trungMaCanHoBaoTrungVaKhongLuu() {
        them(hopLe("A0601"));
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> them(hopLe("a0601 ")));
        assertTrue(e.getMessage().contains("A0601"));
        assertEquals(1, kho.ho.size());
        assertEquals(1, kho.nhanKhau.size());
    }

    @Test
    void cccdChuHoDaTonTaiThiKhongLuuHo() {
        service.them(hopLe("A0601"), chuHo("001182000001"));
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(hopLe("A0602"), chuHo("001182000001")));
        assertTrue(e.getMessage().contains("CCCD"));
        assertEquals(1, kho.ho.size());
    }

    @Test
    void cccdChuHoSaiDinhDangBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.them(hopLe("A0601"), chuHo("12345")));
        assertTrue(kho.ho.isEmpty());
    }

    @Test
    void chuHoTuDu14TuoiThieuCccdBiTuChoi() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(hopLe("A0601"), chuHo("")));
        assertTrue(e.getMessage().contains("So CCCD chu ho"));
        assertTrue(kho.ho.isEmpty());
    }

    @Test
    void hoChuyenDiThiMoiThanhVienChuyenDiVaCccdDungLaiDuoc() {
        HoGiaDinh cu = service.them(hopLe("A0601"), chuHo("001182000001"));
        service.danhDauChuyenDi(cu.getId());
        for (NhanKhau n : kho.nhanKhauDao.theoHo(cu.getId())) {
            assertEquals(TrangThaiNhanKhau.DA_CHUYEN_DI, n.getTrangThai());
        }
        HoGiaDinh moi = service.them(hopLe("A0602"), chuHo("001182000001"));
        assertEquals(1, service.timKiem("A0602", false).get(0)
                .soThanhVien());
        assertEquals(0, service.timKiem("A0601", true).get(0)
                .soThanhVien());
        assertTrue(moi.getId() > cu.getId());
    }

    @Test
    void canHoCoHoCuDaChuyenDiThiThemDuocHoMoi() {
        HoGiaDinh cu = them(hopLe("A0601"));
        service.danhDauChuyenDi(cu.getId());
        HoGiaDinh moi = them(hopLe("A0601"));
        assertEquals(2, service.timKiem("A0601", true).size());
        assertEquals(1, service.timKiem("A0601", false).size());
        assertEquals(moi.getId(), service.timKiem("A0601", false).get(0)
                .ho().getId());
    }

    @Test
    void maCanHoDuocChuanHoaVietHoa() {
        assertEquals("A0601", them(hopLe("  a0601 ")).getMaCanHo());
    }

    @Test
    void dienTichKhongHopLeBiTuChoi() {
        for (String dt : List.of("0", "-5", "abc", "451", "75.555")) {
            ThongTinHo tt = new ThongTinHo("A01", LoaiCanHo.CAN_HO, "6", dt,
                    "", LocalDate.of(2024, 1, 1));
            assertThrows(NghiepVuException.class, () -> them(tt), dt);
        }
    }

    @Test
    void tangNgoaiPhamViBiTuChoi() {
        for (String tang : List.of("0", "31", "2.5")) {
            ThongTinHo tt = new ThongTinHo("A01", LoaiCanHo.CAN_HO, tang,
                    "70", "", LocalDate.of(2024, 1, 1));
            assertThrows(NghiepVuException.class, () -> them(tt), tang);
        }
    }

    @Test
    void soDienThoaiSaiBiTuChoiConDeTrongThiLuuNull() {
        ThongTinHo sai = new ThongTinHo("A01", LoaiCanHo.CAN_HO, "6", "70",
                "12345", LocalDate.of(2024, 1, 1));
        assertThrows(NghiepVuException.class, () -> them(sai));
        ThongTinHo trong = new ThongTinHo("A01", LoaiCanHo.CAN_HO, "6", "70",
                "  ", LocalDate.of(2024, 1, 1));
        assertNull(them(trong).getSoDienThoai());
    }

    @Test
    void ngayChuyenDenTrongTuongLaiBiTuChoi() {
        ThongTinHo tt = new ThongTinHo("A01", LoaiCanHo.CAN_HO, "6", "70",
                "", HOM_NAY.plusDays(1));
        assertThrows(NghiepVuException.class, () -> them(tt));
    }

    @Test
    void suaGiuNguyenMaCuaChinhMinhKhongBaoTrung() {
        HoGiaDinh ho = them(hopLe("A0601"));
        ThongTinHo tt = new ThongTinHo("A0601", LoaiCanHo.CAN_HO, "6", "80",
                "", LocalDate.of(2023, 7, 15));
        HoGiaDinh sua = service.capNhat(ho.getId(), tt);
        assertEquals(0, new BigDecimal("80").compareTo(
                kho.ho.get(ho.getId()).getDienTich()));
        assertEquals(ho.getId(), sua.getId());
        assertEquals(1, kho.nhanKhau.size());
    }

    @Test
    void suaSangMaCuaHoKhacBiBaoTrung() {
        them(hopLe("A0601"));
        HoGiaDinh b = them(hopLe("A0602"));
        assertThrows(NghiepVuException.class,
                () -> service.capNhat(b.getId(), hopLe("A0601")));
        assertEquals("A0602", kho.ho.get(b.getId()).getMaCanHo());
    }

    @Test
    void suaThieuTruongBaoLoiVaKhongLuu() {
        HoGiaDinh ho = them(hopLe("A0601"));
        ThongTinHo tt = new ThongTinHo("A0601", LoaiCanHo.CAN_HO, "", "70",
                "", LocalDate.of(2023, 7, 15));
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.capNhat(ho.getId(), tt));
        assertTrue(e.getMessage().contains("Tang"));
        assertEquals(6, kho.ho.get(ho.getId()).getTang());
    }

    @Test
    void hoDaChuyenDiKhongSuaDuoc() {
        HoGiaDinh ho = them(hopLe("A0601"));
        service.danhDauChuyenDi(ho.getId());
        assertThrows(NghiepVuException.class,
                () -> service.capNhat(ho.getId(), hopLe("A0601")));
        assertThrows(NghiepVuException.class,
                () -> service.danhDauChuyenDi(ho.getId()));
    }

    @Test
    void timKiemMacDinhAnHoDaChuyenDi() {
        HoGiaDinh ho = them(hopLe("A0601"));
        service.danhDauChuyenDi(ho.getId());
        assertFalse(service.timKiem("", true).isEmpty());
        assertTrue(service.timKiem("", false).isEmpty());
    }

    @Test
    void timKiemTheoTenChuHo() {
        them(hopLe("A0601"));
        service.them(hopLe("A0602"), new ThongTinNhanKhau("Le Minh Quan",
                LocalDate.of(1990, 11, 2), GioiTinh.NAM, "001090005678",
                null));
        List<TomTatHo> kq = service.timKiem("Minh Quan", false);
        assertEquals(1, kq.size());
        assertEquals("A0602", kq.get(0).ho().getMaCanHo());
    }
}
