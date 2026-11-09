package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.Database;
import com.bluemoon.dao.BaoCaoNhanKhauDaoImpl;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.DotCuTruDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.NhanKhauDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.PhieuThuMoi;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiDongGop;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.service.BaoCaoNhanKhauService;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.KhoanPhiService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.ThongBaoPhiService;
import com.bluemoon.service.ThuPhiService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Thu phi, huy phieu, dong gop, thong bao phi tren MySQL that. */
class PhieuThuIT {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T11 = YearMonth.of(2026, 11);

    private final KhoanPhiDaoImpl khoanPhiDao = new KhoanPhiDaoImpl();
    private final HoGiaDinhDaoImpl hoDao = new HoGiaDinhDaoImpl();
    private final PhieuThuDaoImpl phieuDao = new PhieuThuDaoImpl();
    private ThuPhiService thuPhi;
    private CongNoService congNo;
    private TaiKhoan admin;
    private int quy;
    private int hoId;

    @BeforeEach
    void chuanBi() {
        admin = CsdlThu.dungLai();
        Clock ngay3T12 = Clock.fixed(LocalDateTime.of(2026, 12, 3, 9, 30)
                .atZone(VN).toInstant(), VN);
        thuPhi = new ThuPhiService(phieuDao, khoanPhiDao, ngay3T12);
        congNo = new CongNoService(new CongNoDaoImpl(), khoanPhiDao, hoDao,
                ngay3T12);
        for (String ten : List.of(KhoanPhiService.PHI_DICH_VU,
                KhoanPhiService.PHI_QUAN_LY)) {
            khoanPhiDao.them(new KhoanPhi(0, ten, LoaiKhoanPhi.BAT_BUOC,
                    CachTinhPhi.THEO_DIEN_TICH, new BigDecimal(
                            ten.equals(KhoanPhiService.PHI_DICH_VU) ? "5000"
                                    : "7000"),
                    LocalDate.of(2026, 1, 1), null,
                    TrangThaiKhoanPhi.DANG_THU));
        }
        quy = khoanPhiDao.them(new KhoanPhi(0, "Quỹ vì người nghèo 2026",
                LoaiKhoanPhi.TU_NGUYEN, CachTinhPhi.KHONG_CO_DINH, null,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 31),
                TrangThaiKhoanPhi.DANG_THU));
        hoId = hoDao.them(new HoGiaDinh(0, "A0601", LoaiCanHo.CAN_HO, 6,
                new BigDecimal("75.50"), null, LocalDate.of(2026, 1, 1),
                TrangThaiHo.DANG_O),
                new NhanKhau(0, 0, "Nguyễn Thị Lan", LocalDate.of(1982, 8, 25),
                        GioiTinh.NU, "001182004567", QuanHeChuHo.CHU_HO,
                        TrangThaiNhanKhau.DANG_O, LoaiCuTru.THUONG_TRU, null),
                null, admin.getId());
        congNo.chot(admin, T11);
        congNo.chot(admin, YearMonth.of(2026, 12));
    }

    private List<Integer> moiKhoan() {
        return phieuDao.khoanChuaDong(hoId).stream()
                .map(KhoanNo::phaiNopId).toList();
    }

    @Test
    void thuTatCaRoiHuyKhoiPhucCongNo() {
        assertEquals(4, moiKhoan().size());
        PhieuThu p = thuPhi.thu(admin, hoId, moiKhoan(), null,
                HinhThucThanhToan.TIEN_MAT);
        assertEquals("PT-2026-00001", p.soBienLai());
        // 75,5 m2 x (5.000 + 7.000) x 2 thang.
        assertEquals(0, new BigDecimal("1812000").compareTo(p.tongTien()));
        assertEquals("Nguyễn Thị Lan", p.tenChuHo());
        assertEquals("Quản trị viên", p.tenNguoiThu());
        assertEquals("Phí dịch vụ tháng 11/2026", p.chiTiet().get(0).moTa());
        assertTrue(moiKhoan().isEmpty());
        assertEquals(0, congNo.tongChuaDong(admin, hoId).signum());
        assertTrue(congNo.theoThang(admin, T11).stream()
                .allMatch(x -> x.daDong()));
        int idDv = congNo.theoThang(admin, T11).get(0).id();
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> congNo.suaSoTien(admin, idDv, "1.000", null));
        assertEquals(CongNoService.LOI_DA_THU, e.getMessage());

        PhieuThu huy = thuPhi.huy(admin, p.id(), "Thu nhầm");
        assertTrue(huy.daHuy());
        assertEquals("Quản trị viên", huy.tenNguoiHuy());
        assertEquals(4, moiKhoan().size());
        congNo.suaSoTien(admin, idDv, "1.000", "Sau khi hủy phiếu");
        assertEquals("PT-2026-00002", thuPhi.thu(admin, hoId, moiKhoan(),
                null, HinhThucThanhToan.CHUYEN_KHOAN).soBienLai());
        assertEquals(2, thuPhi.lichSu(admin, hoId).size());
    }

    @Test
    void csdlChanThuTrungMotKhoanPhaiNop() throws SQLException {
        int id = moiKhoan().get(0);
        thuPhi.thu(admin, hoId, List.of(id), null, HinhThucThanhToan.TIEN_MAT);
        // Bo qua Service, ghi thang chi tiet thu hai: UNIQUE chan lai.
        try (Connection c = Database.getConnection();
                PreparedStatement ps = c.prepareStatement("INSERT INTO "
                        + "phieu_thu_chi_tiet (phieu_thu_id, khoan_phi_id, "
                        + "phai_nop_id, so_tien) SELECT 1, khoan_phi_id, id, "
                        + "so_tien FROM phai_nop WHERE id = ?")) {
            ps.setInt(1, id);
            assertThrows(SQLException.class, ps::executeUpdate);
        }
    }

    @Test
    void phieuGiaLapHoanTacKhongLamNhaySo() {
        int id = moiKhoan().get(0);
        thuPhi.thu(admin, hoId, List.of(id), null, HinhThucThanhToan.TIEN_MAT);
        // Lap phieu voi khoan da thu: DAO tu choi va hoan tac ca so da cap.
        assertThrows(DaoException.class, () -> phieuDao.lapPhieu(
                new PhieuThuMoi(hoId,
                        HinhThucThanhToan.TIEN_MAT, admin.getId(),
                        LocalDateTime.of(2026, 12, 3, 10, 0), List.of(
                                new PhieuThuMoi.Dong(1, id,
                                        new BigDecimal("377500"))))));
        PhieuThu tiep = thuPhi.thu(admin, hoId, List.of(moiKhoan().get(0)),
                null, HinhThucThanhToan.TIEN_MAT);
        assertEquals("PT-2026-00002", tiep.soBienLai());
    }

    @Test
    void dongGopNhieuLanVaGhiKhongDong() {
        PhieuThu khong = thuPhi.thu(admin, hoId, List.of(), Map.of(quy, "0"),
                HinhThucThanhToan.TIEN_MAT);
        assertNull(khong.hinhThuc());
        assertTrue(khong.khongThuTien());
        assertEquals(TrangThaiDongGop.KHONG_DONG, thuPhi.thongTin(admin, hoId)
                .dongGop().get(0).trangThai());
        thuPhi.thu(admin, hoId, List.of(), Map.of(quy, "100.000"),
                HinhThucThanhToan.TIEN_MAT);
        thuPhi.thu(admin, hoId, List.of(), Map.of(quy, "50.000"),
                HinhThucThanhToan.TIEN_MAT);
        var q = thuPhi.thongTin(admin, hoId).dongGop().get(0);
        assertEquals(TrangThaiDongGop.DA_DONG, q.trangThai());
        assertEquals(0, new BigDecimal("150000").compareTo(q.tongDaDong()));
        assertEquals(3, q.soLan());
    }

    @Test
    void inBienLaiDemSoLanIn() {
        PhieuThu p = thuPhi.thu(admin, hoId, moiKhoan(), null,
                HinhThucThanhToan.TIEN_MAT);
        thuPhi.daIn(admin, p.id());
        assertEquals(1, thuPhi.choIn(admin, p.id()).soLanIn());
        assertEquals(1, thuPhi.homNay(admin).size());
    }

    @Test
    void traCuuThayHoDaChuyenDiConNo() {
        hoDao.danhDauChuyenDi(hoId, admin.getId());
        assertEquals(1, thuPhi.traCuu(admin, "a0601").size());
        thuPhi.thu(admin, hoId, moiKhoan(), null, HinhThucThanhToan.TIEN_MAT);
        assertThrows(NghiepVuException.class,
                () -> thuPhi.traCuu(admin, "A0601"));
    }

    @Test
    void thongBaoPhiVaDanhSachTamVang() {
        ThongBaoPhiService thongBao = new ThongBaoPhiService(phieuDao,
                new CongNoDaoImpl());
        var tb = thongBao.motHo(admin, YearMonth.of(2026, 12), hoId);
        assertEquals(2, tb.khoanThang().size());
        assertEquals(2, tb.noCu().size());
        assertEquals(1, thongBao.tatCa(admin, T11).size());

        int nkId = new NhanKhauDaoImpl().theoHo(hoId).get(0)
                .getId();
        new DotCuTruDaoImpl().them(new DotCuTru(0, nkId,
                LoaiDotCuTru.TAM_VANG, LocalDate.of(2026, 12, 1),
                LocalDate.of(2027, 1, 31), "Công tác"), admin.getId());
        BaoCaoNhanKhauService baoCao = new BaoCaoNhanKhauService(
                new BaoCaoNhanKhauDaoImpl());
        var ds = baoCao.tamVang(admin, LocalDate.of(2026, 12, 3));
        assertEquals("Nguyễn Thị Lan", ds.get(0).hoTen());
        assertEquals("A0601", ds.get(0).maCanHo());
        assertThrows(NghiepVuException.class,
                () -> baoCao.tamVang(admin, LocalDate.of(2026, 11, 30)));
        assertFalse(ds.isEmpty());
    }

    @Test
    void haiMayCungThuMotKhoanChiMotPhieuThanhCong() throws Exception {
        List<Integer> ds = moiKhoan();
        CountDownLatch batDau = new CountDownLatch(1);
        Callable<Boolean> thu = () -> {
            batDau.await();
            try {
                thuPhi.thu(admin, hoId, ds, null, HinhThucThanhToan.TIEN_MAT);
                return true;
            } catch (NghiepVuException e) {
                return false;
            }
        };
        ExecutorService may = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> a = may.submit(thu);
            Future<Boolean> b = may.submit(thu);
            batDau.countDown();
            assertTrue(a.get() ^ b.get());
        } finally {
            may.shutdown();
        }
        assertEquals(1, thuPhi.homNay(admin).size());
        assertEquals("PT-2026-00001", thuPhi.homNay(admin).get(0).soBienLai());
    }
}
