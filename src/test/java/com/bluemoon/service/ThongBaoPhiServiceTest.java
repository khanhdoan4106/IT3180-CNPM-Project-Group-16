package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThongBaoPhi;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ThongBaoPhiServiceTest {

    private static final YearMonth T11 = YearMonth.of(2026, 11);
    private static final YearMonth T12 = YearMonth.of(2026, 12);

    private KhoThuPhiGia kho;
    private KhoPhieuThuGia khoPhieu;
    private ThongBaoPhiService service;
    private TaiKhoan thuQuy;
    private int dichVu;
    private int a0601;
    private int b0202;

    @BeforeEach
    void chuanBi() {
        kho = new KhoThuPhiGia();
        khoPhieu = new KhoPhieuThuGia(kho);
        service = new ThongBaoPhiService(khoPhieu, kho.congNoDao);
        thuQuy = new TaiKhoan(2, "thuquy", "x", "Thủ quỹ", VaiTro.THU_QUY,
                true);
        khoPhieu.themTaiKhoan(thuQuy);
        dichVu = kho.khoanPhiDao.them(new KhoanPhi(0,
                KhoanPhiService.PHI_DICH_VU, LoaiKhoanPhi.BAT_BUOC,
                CachTinhPhi.THEO_DIEN_TICH, new BigDecimal("5000"),
                LocalDate.of(2026, 1, 1), null, TrangThaiKhoanPhi.DANG_THU));
        a0601 = khoPhieu.themHo("A0601", TrangThaiHo.DANG_O, "Nguyễn Thị Lan");
        b0202 = khoPhieu.themHo("B0202", TrangThaiHo.DANG_O, "Trần Văn Bình");
        khoPhieu.themPhaiNop(a0601, dichVu, T11, "377500");
        khoPhieu.themPhaiNop(a0601, dichVu, T12, "377500");
        khoPhieu.themPhaiNop(b0202, dichVu, T12, "250000");
    }

    @Test
    void thongBaoLietKeKhoanThangVaNoCu() {
        ThongBaoPhi tb = service.motHo(thuQuy, T12, a0601);
        assertEquals(1, tb.khoanThang().size());
        assertEquals(1, tb.noCu().size());
        assertEquals(0, new BigDecimal("755000").compareTo(tb.tong()));
        // Thong bao thang 11 khong lay khoan cua thang 12.
        assertEquals(0, new BigDecimal("377500").compareTo(
                service.motHo(thuQuy, T11, a0601).tong()));
    }

    @Test
    void hoKhongCoKhoanCanDongThiBao() {
        new ThuPhiService(khoPhieu, kho.khoanPhiDao).thu(thuQuy, b0202,
                khoPhieu.khoanChuaDong(b0202).stream()
                        .map(KhoanNo::phaiNopId).toList(),
                null, HinhThucThanhToan.TIEN_MAT);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.motHo(thuQuy, T12, b0202));
        assertEquals("Hộ B0202 không có khoản cần đóng đến tháng 12/2026",
                e.getMessage());
    }

    @Test
    void thangChuaChotChuaInDuoc() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.tatCa(thuQuy, YearMonth.of(2027, 1)));
        assertEquals("tháng 1/2027 chưa chốt công nợ, chưa in thông báo được",
                e.getMessage());
    }

    @Test
    void inTatCaBoQuaHoDaChuyenDi() {
        int cu = khoPhieu.themHo("C0303", TrangThaiHo.DA_CHUYEN_DI, "Hộ cũ");
        khoPhieu.themPhaiNop(cu, dichVu, T11, "100000");
        List<ThongBaoPhi> ds = service.tatCa(thuQuy, T12);
        assertEquals(List.of("A0601", "B0202"), ds.stream()
                .map(t -> t.ho().ho().getMaCanHo()).toList());
    }
}
