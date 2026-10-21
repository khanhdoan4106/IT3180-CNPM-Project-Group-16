package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.TrangThaiHo;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HoGiaDinhServiceTest {

    private static final LocalDate HOM_NAY = LocalDate.of(2026, 11, 2);

    /** DAO gia lap trong bo nho, khong can MySQL. */
    private static class DaoGia implements HoGiaDinhDao {
        private final Map<Integer, HoGiaDinh> theoId = new LinkedHashMap<>();
        private int idTiepTheo = 1;

        @Override
        public int them(HoGiaDinh ho) {
            int id = idTiepTheo++;
            theoId.put(id, ho.voiId(id));
            return id;
        }

        @Override
        public void capNhat(HoGiaDinh ho) {
            theoId.put(ho.getId(), ho);
        }

        @Override
        public void capNhatTrangThai(int id, TrangThaiHo trangThai) {
            theoId.put(id, theoId.get(id).voiTrangThai(trangThai));
        }

        @Override
        public Optional<HoGiaDinh> timTheoId(int id) {
            return Optional.ofNullable(theoId.get(id));
        }

        @Override
        public boolean tonTaiMaDangO(String maCanHo, int boQuaId) {
            return theoId.values().stream().anyMatch(h -> h.getId() != boQuaId
                    && h.getTrangThai() == TrangThaiHo.DANG_O
                    && h.getMaCanHo().equals(maCanHo));
        }

        @Override
        public List<HoGiaDinh> timKiem(String tuKhoa, boolean gomDaChuyenDi) {
            List<HoGiaDinh> kq = new ArrayList<>();
            for (HoGiaDinh h : theoId.values()) {
                boolean khop = h.getMaCanHo().contains(tuKhoa)
                        || h.getTenChuHo().contains(tuKhoa);
                if (khop && (gomDaChuyenDi
                        || h.getTrangThai() == TrangThaiHo.DANG_O)) {
                    kq.add(h);
                }
            }
            return kq;
        }
    }

    private DaoGia dao;
    private HoGiaDinhService service;

    @BeforeEach
    void chuanBi() {
        dao = new DaoGia();
        Clock dongHo = Clock.fixed(
                HOM_NAY.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh"))
                        .toInstant(),
                ZoneId.of("Asia/Ho_Chi_Minh"));
        service = new HoGiaDinhService(dao, dongHo);
    }

    private static ThongTinHo hopLe(String ma) {
        return new ThongTinHo(ma, LoaiCanHo.CAN_HO, "6", "75.5",
                "Nguyen Thi Lan", "0987654321", LocalDate.of(2023, 7, 15));
    }

    @Test
    void themDayDuThanhCongVaHienTrongDanhSach() {
        HoGiaDinh ho = service.them(hopLe("A0601"));
        assertTrue(ho.getId() > 0);
        assertEquals(TrangThaiHo.DANG_O, ho.getTrangThai());
        assertEquals(1, service.timKiem("", false).size());
    }

    @Test
    void dienTichLuuDungGiaTriDaNhap() {
        ThongTinHo tt = new ThongTinHo("A0602", LoaiCanHo.CAN_HO, "6",
                "75,50", "Le Van A", "", LocalDate.of(2024, 1, 1));
        HoGiaDinh ho = service.them(tt);
        assertEquals(0, new BigDecimal("75.5").compareTo(ho.getDienTich()));
    }

    @Test
    void thieuTruongBatBuocBaoDuCacTruongVaKhongLuu() {
        ThongTinHo tt = new ThongTinHo(" ", null, "", "", "  ", "", null);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(tt));
        for (String truong : List.of("Ma can ho", "Loai can ho", "Tang",
                "Dien tich", "Ten chu ho", "Ngay chuyen den")) {
            assertTrue(e.getMessage().contains(truong), truong);
        }
        assertTrue(dao.theoId.isEmpty());
    }

    @Test
    void trungMaCanHoBaoTrungVaKhongLuu() {
        service.them(hopLe("A0601"));
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.them(hopLe("a0601 ")));
        assertTrue(e.getMessage().contains("A0601"));
        assertEquals(1, dao.theoId.size());
    }

    @Test
    void canHoCoHoCuDaChuyenDiThiThemDuocHoMoi() {
        HoGiaDinh cu = service.them(hopLe("A0601"));
        service.danhDauChuyenDi(cu.getId());
        HoGiaDinh moi = service.them(hopLe("A0601"));
        assertEquals(2, service.timKiem("A0601", true).size());
        assertEquals(1, service.timKiem("A0601", false).size());
        assertEquals(moi.getId(), service.timKiem("A0601", false).get(0)
                .getId());
    }

    @Test
    void maCanHoDuocChuanHoaVietHoa() {
        assertEquals("A0601", service.them(hopLe("  a0601 ")).getMaCanHo());
    }

    @Test
    void dienTichKhongHopLeBiTuChoi() {
        for (String dt : List.of("0", "-5", "abc", "451", "75.555")) {
            ThongTinHo tt = new ThongTinHo("A01", LoaiCanHo.CAN_HO, "6", dt,
                    "Le Van A", "", LocalDate.of(2024, 1, 1));
            assertThrows(NghiepVuException.class, () -> service.them(tt), dt);
        }
    }

    @Test
    void tangNgoaiPhamViBiTuChoi() {
        for (String tang : List.of("0", "31", "2.5")) {
            ThongTinHo tt = new ThongTinHo("A01", LoaiCanHo.CAN_HO, tang,
                    "70", "Le Van A", "", LocalDate.of(2024, 1, 1));
            assertThrows(NghiepVuException.class, () -> service.them(tt),
                    tang);
        }
    }

    @Test
    void soDienThoaiSaiBiTuChoiConDeTrongThiLuuNull() {
        ThongTinHo sai = new ThongTinHo("A01", LoaiCanHo.CAN_HO, "6", "70",
                "Le Van A", "12345", LocalDate.of(2024, 1, 1));
        assertThrows(NghiepVuException.class, () -> service.them(sai));
        ThongTinHo trong = new ThongTinHo("A01", LoaiCanHo.CAN_HO, "6", "70",
                "Le Van A", "  ", LocalDate.of(2024, 1, 1));
        assertNull(service.them(trong).getSoDienThoai());
    }

    @Test
    void ngayChuyenDenTrongTuongLaiBiTuChoi() {
        ThongTinHo tt = new ThongTinHo("A01", LoaiCanHo.CAN_HO, "6", "70",
                "Le Van A", "", HOM_NAY.plusDays(1));
        assertThrows(NghiepVuException.class, () -> service.them(tt));
    }

    @Test
    void suaGiuNguyenMaCuaChinhMinhKhongBaoTrung() {
        HoGiaDinh ho = service.them(hopLe("A0601"));
        ThongTinHo tt = new ThongTinHo("A0601", LoaiCanHo.CAN_HO, "6", "80",
                "Nguyen Thi Lan", "", LocalDate.of(2023, 7, 15));
        HoGiaDinh sua = service.capNhat(ho.getId(), tt);
        assertEquals(0, new BigDecimal("80").compareTo(
                dao.theoId.get(ho.getId()).getDienTich()));
        assertEquals(ho.getId(), sua.getId());
    }

    @Test
    void suaSangMaCuaHoKhacBiBaoTrung() {
        service.them(hopLe("A0601"));
        HoGiaDinh b = service.them(hopLe("A0602"));
        assertThrows(NghiepVuException.class,
                () -> service.capNhat(b.getId(), hopLe("A0601")));
        assertEquals("A0602", dao.theoId.get(b.getId()).getMaCanHo());
    }

    @Test
    void hoDaChuyenDiKhongSuaDuoc() {
        HoGiaDinh ho = service.them(hopLe("A0601"));
        service.danhDauChuyenDi(ho.getId());
        assertThrows(NghiepVuException.class,
                () -> service.capNhat(ho.getId(), hopLe("A0601")));
        assertThrows(NghiepVuException.class,
                () -> service.danhDauChuyenDi(ho.getId()));
    }

    @Test
    void timKiemMacDinhAnHoDaChuyenDi() {
        HoGiaDinh ho = service.them(hopLe("A0601"));
        service.danhDauChuyenDi(ho.getId());
        assertFalse(service.timKiem("", true).isEmpty());
        assertTrue(service.timKiem("", false).isEmpty());
    }
}
