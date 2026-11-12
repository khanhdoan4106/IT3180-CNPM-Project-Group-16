package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KhoanPhiServiceTest {

    private static final LocalDate DAU_NAM = LocalDate.of(2026, 1, 1);
    private static final LocalDate T12 = LocalDate.of(2026, 12, 1);
    private static final LocalDate CUOI_NAM = LocalDate.of(2026, 12, 31);

    private KhoThuPhiGia kho;
    private KhoanPhiService service;
    private TaiKhoan admin;
    private TaiKhoan thuQuy;
    private int dichVu;
    private int quanLy;

    @BeforeEach
    void chuanBi() {
        kho = new KhoThuPhiGia();
        service = new KhoanPhiService(kho.khoanPhiDao);
        admin = new TaiKhoan(1, "admin", "x", "Quản trị", VaiTro.QUAN_TRI,
                true);
        thuQuy = new TaiKhoan(2, "thuquy", "x", "Thủ quỹ", VaiTro.THU_QUY,
                true);
        dichVu = kho.khoanPhiDao.them(batBuoc(KhoanPhiService.PHI_DICH_VU,
                "5000"), 1);
        quanLy = kho.khoanPhiDao.them(batBuoc(KhoanPhiService.PHI_QUAN_LY,
                "7000"), 1);
    }

    private static KhoanPhi batBuoc(String ten, String donGia) {
        return new KhoanPhi(0, ten, LoaiKhoanPhi.BAT_BUOC,
                CachTinhPhi.THEO_DIEN_TICH, new BigDecimal(donGia), DAU_NAM,
                null, TrangThaiKhoanPhi.DANG_THU);
    }

    @Test
    void doiDonGiaPhiDichVuTrongKhoang() {
        KhoanPhi k = service.suaDonGia(admin, dichVu, "16.500");
        assertEquals(new BigDecimal("16500"), k.donGia());
        assertEquals(new BigDecimal("16500"),
                kho.khoan.get(dichVu).donGia());
    }

    @Test
    void tuChoiDonGiaPhiDichVuNgoaiKhoang() {
        // AC-017
        for (String sai : new String[] {"2.499", "16.501", "0"}) {
            assertThrows(NghiepVuException.class,
                    () -> service.suaDonGia(admin, dichVu, sai));
        }
        assertEquals(new BigDecimal("5000"), kho.khoan.get(dichVu).donGia());
    }

    @Test
    void phiQuanLyKhongDuoiBayNghin() {
        // AC-016, RAW-005
        assertThrows(NghiepVuException.class,
                () -> service.suaDonGia(admin, quanLy, "6.999"));
        assertEquals(new BigDecimal("8000"),
                service.suaDonGia(admin, quanLy, "8000").donGia());
    }

    @Test
    void soQuaLonBiChanOServiceTruocKhiToiCsdl() {
        // Truoc day vuot DECIMAL(12,2) o MySQL va bao "Khong ket noi".
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.suaDonGia(admin, quanLy, "99999999999999"));
        assertEquals("Đơn giá tối đa 1.000.000.000 đ", e.getMessage());
        assertEquals(new BigDecimal("7000"), kho.khoan.get(quanLy).donGia());
        assertThrows(NghiepVuException.class, () -> service.taoKhoanTuNguyen(
                admin, "Quỹ A", T12, CUOI_NAM, "1.000.000.001"));
    }

    @Test
    void tuChoiDonGiaKhongPhaiSo() {
        assertThrows(NghiepVuException.class,
                () -> service.suaDonGia(admin, dichVu, "5,5k"));
        assertThrows(NghiepVuException.class,
                () -> service.suaDonGia(admin, dichVu, " "));
    }

    @Test
    void thuQuyKhongDoiDuocDonGia() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.suaDonGia(thuQuy, dichVu, "6000"));
        assertEquals(PhanQuyen.LOI_KHONG_CO_QUYEN, e.getMessage());
        assertEquals(2, service.danhSach(thuQuy).size());
    }

    @Test
    void taoKhoanTuNguyenKhongCoDinh() {
        // AC-019, AC-021
        KhoanPhi k = service.taoKhoanTuNguyen(admin,
                "  Quỹ   khuyến học 2026 ", T12, CUOI_NAM, "");
        assertEquals("Quỹ khuyến học 2026", k.ten());
        assertEquals(CachTinhPhi.KHONG_CO_DINH, k.cachTinh());
        assertNull(k.donGia());
        assertEquals(3, service.danhSach(admin).size());
    }

    @Test
    void taoKhoanTuNguyenCoDinhTheoHo() {
        KhoanPhi k = service.taoKhoanTuNguyen(admin, "Quỹ biển đảo", T12,
                CUOI_NAM, "50.000 đ");
        assertEquals(CachTinhPhi.CO_DINH_THEO_HO, k.cachTinh());
        assertEquals(new BigDecimal("50000"), k.donGia());
    }

    @Test
    void tuChoiTrungTenTrongCungDot() {
        // AC-020: cung ngay bat dau la cung dot; dot khac thi duoc
        service.taoKhoanTuNguyen(admin, "Quỹ biển đảo", T12, CUOI_NAM, "");
        assertThrows(NghiepVuException.class, () -> service.taoKhoanTuNguyen(
                admin, "Quỹ biển đảo", T12, CUOI_NAM, ""));
        service.taoKhoanTuNguyen(admin, "Quỹ biển đảo",
                LocalDate.of(2027, 3, 1), LocalDate.of(2027, 3, 31), "");
    }

    @Test
    void trungTenDoMayKhacLuuCungLuc() {
        kho.trungKhiThem = true;
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.taoKhoanTuNguyen(admin, "Quỹ biển đảo", T12,
                        CUOI_NAM, ""));
        assertEquals("Khoản thu \"Quỹ biển đảo\" đã có trong đợt này",
                e.getMessage());
    }

    @Test
    void tuChoiThieuHoacSaiThoiGian() {
        assertThrows(NghiepVuException.class, () -> service.taoKhoanTuNguyen(
                admin, "Quỹ A", null, CUOI_NAM, ""));
        assertThrows(NghiepVuException.class, () -> service.taoKhoanTuNguyen(
                admin, "Quỹ A", CUOI_NAM, T12, ""));
        assertThrows(NghiepVuException.class, () -> service.taoKhoanTuNguyen(
                admin, " ", T12, CUOI_NAM, ""));
    }

    @Test
    void chiQuanTriVienTaoDuocKhoanThu() {
        assertThrows(NghiepVuException.class, () -> service.taoKhoanTuNguyen(
                thuQuy, "Quỹ A", T12, CUOI_NAM, ""));
        assertThrows(NghiepVuException.class, () -> service.taoKhoanTuNguyen(
                null, "Quỹ A", T12, CUOI_NAM, ""));
    }
}
