package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.KhoanPhiService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.SoTien;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Chot va sua cong no tren MySQL that (Loi 1, Loi 2 cua ban ra soat). */
class CongNoIT {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T11 = YearMonth.of(2026, 11);

    private final KhoanPhiDaoImpl khoanPhiDao = new KhoanPhiDaoImpl();
    private final HoGiaDinhDaoImpl hoDao = new HoGiaDinhDaoImpl();
    private CongNoService congNo;
    private KhoanPhiService khoanPhi;
    private TaiKhoan admin;
    private int phiDichVu;

    @BeforeEach
    void chuanBi() {
        admin = CsdlThu.dungLai();
        Clock ngay3T12 = Clock.fixed(LocalDate.of(2026, 12, 3)
                .atStartOfDay(VN).toInstant(), VN);
        congNo = new CongNoService(new CongNoDaoImpl(), khoanPhiDao, hoDao,
                ngay3T12);
        khoanPhi = new KhoanPhiService(khoanPhiDao);
        phiDichVu = khoanPhiDao.them(new KhoanPhi(0,
                KhoanPhiService.PHI_DICH_VU, LoaiKhoanPhi.BAT_BUOC,
                CachTinhPhi.THEO_DIEN_TICH, new BigDecimal("5000"),
                LocalDate.of(2026, 1, 1), null, TrangThaiKhoanPhi.DANG_THU), 1);
        themHo("A0601", LocalDate.of(2026, 1, 1), "001182004567");
    }

    private void themHo(String ma, LocalDate ngay, String cccd) {
        hoDao.them(new HoGiaDinh(0, ma, LoaiCanHo.CAN_HO, 6,
                new BigDecimal("75.50"), null, ngay, TrangThaiHo.DANG_O),
                new NhanKhau(0, 0, "Nguyễn Thị Lan", LocalDate.of(1982, 8, 25),
                        GioiTinh.NU, cccd, QuanHeChuHo.CHU_HO,
                        TrangThaiNhanKhau.DANG_O, LoaiCuTru.THUONG_TRU, null),
                null, admin.getId());
    }

    @Test
    void chotThangCuKhongGhiNoHoChuyenDenSau() {
        themHo("B0909", LocalDate.of(2026, 12, 5), "001190009999");
        assertEquals(1, congNo.chot(admin, T11).soMoi());
        assertFalse(congNo.theoThang(admin, T11).stream()
                .anyMatch(p -> p.maCanHo().equals("B0909")));
        assertThrows(NghiepVuException.class,
                () -> congNo.chot(admin, YearMonth.of(2025, 6)));
    }

    @Test
    void donGiaDienSanTuCsdlLuuLaiDuoc() {
        BigDecimal tuCsdl = khoanPhiDao.timTheoId(phiDichVu).orElseThrow()
                .donGia();
        assertEquals("5000.00", tuCsdl.toPlainString());
        // Gia tri dien san (khong doi) va gia tri sua tu do deu luu duoc.
        khoanPhi.suaDonGia(admin, phiDichVu, SoTien.hienThi(tuCsdl));
        khoanPhi.suaDonGia(admin, phiDichVu, "6.000 đ");
        assertEquals(0, new BigDecimal("6000").compareTo(
                khoanPhiDao.timTheoId(phiDichVu).orElseThrow().donGia()));
    }

    @Test
    void soQuaLonBiChanTruocKhiToiMySql() {
        // Truoc day vuot DECIMAL(12,2) thanh DaoException ("khong ket noi").
        int quanLy = khoanPhiDao.them(new KhoanPhi(0,
                KhoanPhiService.PHI_QUAN_LY, LoaiKhoanPhi.BAT_BUOC,
                CachTinhPhi.THEO_DIEN_TICH, new BigDecimal("7000"),
                LocalDate.of(2026, 1, 1), null, TrangThaiKhoanPhi.DANG_THU), 1);
        assertThrows(NghiepVuException.class,
                () -> khoanPhi.suaDonGia(admin, quanLy, "99999999999999"));
        assertThrows(NghiepVuException.class, () -> khoanPhi.taoKhoanTuNguyen(
                admin, "Quỹ A", LocalDate.of(2026, 12, 1),
                LocalDate.of(2026, 12, 31), "99999999999999"));
        // Muc tran van luu duoc vao cot DECIMAL(12,2).
        khoanPhi.suaDonGia(admin, quanLy, "1.000.000.000");
        congNo.chot(admin, T11);
        assertTrue(congNo.theoThang(admin, T11).stream().anyMatch(
                p -> p.soTien().compareTo(new BigDecimal("75500000000")) == 0));
    }

    @Test
    void soTienPhaiNopDienSanTuCsdlLuuLaiDuoc() {
        congNo.chot(admin, T11);
        PhaiNop p = congNo.theoThang(admin, T11).get(0);
        assertEquals("377500.00", p.soTien().toPlainString());
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> congNo.suaSoTien(admin, p.id(),
                        SoTien.hienThi(p.soTien()), ""));
        assertEquals("Số tiền không thay đổi", e.getMessage());
        PhaiNop moi = congNo.suaSoTien(admin, p.id(), "350.000 đ",
                "Giảm theo biên bản");
        assertEquals(0, new BigDecimal("350000").compareTo(moi.soTien()));
        assertTrue(moi.daSua());
    }
}
