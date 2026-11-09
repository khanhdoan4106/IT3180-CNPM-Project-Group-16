package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TamVang;
import com.bluemoon.model.VaiTro;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class BaoCaoNhanKhauServiceTest {

    private static final LocalDate NGAY = LocalDate.of(2026, 12, 3);
    private final TaiKhoan admin = new TaiKhoan(1, "admin", "x", "Quản trị",
            VaiTro.QUAN_TRI, true);
    private final TamVang lan = new TamVang("Nguyễn Thị Lan",
            LocalDate.of(1982, 8, 25), "001182004567", "A0601",
            LocalDate.of(2026, 12, 1), LocalDate.of(2027, 1, 31), "Công tác");

    @Test
    void danhSachTamVangTheoNgay() {
        BaoCaoNhanKhauService s = new BaoCaoNhanKhauService(ngay -> {
            assertEquals(NGAY, ngay);
            return List.of(lan);
        });
        assertEquals(List.of(lan), s.tamVang(admin, NGAY));
    }

    @Test
    void khongCoAiTamVangThiBaoKhongCoDuLieu() {
        BaoCaoNhanKhauService s = new BaoCaoNhanKhauService(ngay -> List.of());
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> s.tamVang(admin, NGAY));
        assertEquals("Không có nhân khẩu tạm vắng vào ngày 03/12/2026",
                e.getMessage());
    }

    @Test
    void chiQuanTriVienVaCanChonNgay() {
        BaoCaoNhanKhauService s = new BaoCaoNhanKhauService(ngay ->
                List.of(lan));
        TaiKhoan thuQuy = new TaiKhoan(2, "tq", "x", "Thủ quỹ",
                VaiTro.THU_QUY, true);
        assertThrows(NghiepVuException.class, () -> s.tamVang(thuQuy, NGAY));
        assertThrows(NghiepVuException.class, () -> s.tamVang(admin, null));
    }
}
