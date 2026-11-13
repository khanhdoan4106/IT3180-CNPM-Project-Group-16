package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TamVang;
import com.bluemoon.model.VaiTro;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BaoCaoNhanKhauServiceTest {

    @TempDir
    Path thuMuc;

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

    /** AC-014: xuất danh sách đang xem ra tệp .xlsx đọc lại được. */
    @Test
    void xuatTamVangRaTepExcel() throws IOException {
        BaoCaoNhanKhauService s = new BaoCaoNhanKhauService(ngay ->
                List.of(lan));
        Path tep = thuMuc.resolve("danh-sach-tam-vang-2026-12-03.xlsx");
        s.xuatTamVang(admin, s.tamVang(admin, NGAY), NGAY, tep);
        try (XSSFWorkbook wb = new XSSFWorkbook(Files.newInputStream(tep))) {
            assertEquals("Nguyễn Thị Lan", wb.getSheetAt(0).getRow(3)
                    .getCell(1).getStringCellValue());
        }
    }

    /** Xuất tệp chỉ Quản trị viên, như báo cáo nhân khẩu; rỗng thì từ chối. */
    @Test
    void xuatTamVangKiemQuyenVaDuLieu() {
        BaoCaoNhanKhauService s = new BaoCaoNhanKhauService(ngay ->
                List.of(lan));
        TaiKhoan thuQuy = new TaiKhoan(2, "tq", "x", "Thủ quỹ",
                VaiTro.THU_QUY, true);
        Path tep = thuMuc.resolve("a.xlsx");
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> s.xuatTamVang(thuQuy, List.of(lan), NGAY, tep));
        assertEquals(PhanQuyen.LOI_KHONG_CO_QUYEN, e.getMessage());
        e = assertThrows(NghiepVuException.class,
                () -> s.xuatTamVang(admin, List.of(), NGAY, tep));
        assertEquals("Không có dữ liệu để xuất.", e.getMessage());
        assertFalse(Files.exists(tep), "Không tạo tệp khi bị từ chối");
        e = assertThrows(NghiepVuException.class, () -> s.xuatTamVang(admin,
                List.of(lan), NGAY, thuMuc.resolve("x").resolve("a.xlsx")));
        assertTrue(e.getMessage().contains("thư mục"));
    }
}
