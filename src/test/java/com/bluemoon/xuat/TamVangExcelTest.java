package com.bluemoon.xuat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.TamVang;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

/** Tệp danh sách tạm vắng (AC-014, giả định 36): đủ trường như bản in. */
class TamVangExcelTest {

    private static final LocalDate NGAY = LocalDate.of(2026, 11, 12);

    private static Sheet ghiRoiDoc(List<TamVang> ds) throws IOException {
        ByteArrayOutputStream ra = new ByteArrayOutputStream();
        TamVangExcel.ghi(ds, NGAY, ra);
        Workbook wb = new XSSFWorkbook(
                new ByteArrayInputStream(ra.toByteArray()));
        return wb.getSheetAt(0);
    }

    private static LocalDate ngay(Cell c) {
        assertTrue(DateUtil.isCellDateFormatted(c), "ô ngày là ngày");
        assertEquals("dd/mm/yyyy", c.getCellStyle().getDataFormatString());
        return c.getLocalDateTimeCellValue().toLocalDate();
    }

    @Test
    void duTruongNhuBanIn() throws IOException {
        Sheet sh = ghiRoiDoc(List.of(
                new TamVang("Hoàng Văn Nam", LocalDate.of(1990, 5, 2),
                        "001090001234", "A0601", LocalDate.of(2026, 10, 28),
                        LocalDate.of(2026, 12, 28), "Đi công tác"),
                new TamVang("Bé Na", LocalDate.of(2015, 1, 9), null,
                        "A0601", LocalDate.of(2026, 11, 1),
                        LocalDate.of(2026, 11, 30), null)));
        assertEquals("Tạm vắng", sh.getSheetName());
        assertEquals("DANH SÁCH NHÂN KHẨU TẠM VẮNG NGÀY 12/11/2026",
                sh.getRow(0).getCell(0).getStringCellValue());
        List<String> tieuDe = new ArrayList<>();
        sh.getRow(TamVangExcel.DONG_TIEU_DE)
                .forEach(c -> tieuDe.add(c.getStringCellValue()));
        assertEquals(List.of("STT", "Họ và tên", "Ngày sinh", "Số CCCD",
                "Căn hộ", "Từ ngày", "Đến ngày", "Lý do"), tieuDe);
        assertEquals(TamVangExcel.DONG_TIEU_DE + 1,
                sh.getPaneInformation().getHorizontalSplitPosition());

        var r = sh.getRow(TamVangExcel.DONG_TIEU_DE + 1);
        assertEquals(1.0, r.getCell(0).getNumericCellValue());
        assertEquals("Hoàng Văn Nam", r.getCell(1).getStringCellValue());
        assertEquals(LocalDate.of(1990, 5, 2), ngay(r.getCell(2)));
        // CCCD la chu (giu so 0 dau), khong phai so.
        assertEquals("001090001234", r.getCell(3).getStringCellValue());
        assertEquals("A0601", r.getCell(4).getStringCellValue());
        assertEquals(LocalDate.of(2026, 10, 28), ngay(r.getCell(5)));
        assertEquals(LocalDate.of(2026, 12, 28), ngay(r.getCell(6)));
        assertEquals("Đi công tác", r.getCell(7).getStringCellValue());

        var r2 = sh.getRow(TamVangExcel.DONG_TIEU_DE + 2);
        assertEquals(2.0, r2.getCell(0).getNumericCellValue());
        assertNull(r2.getCell(3), "Chưa có CCCD: ô trống");
        assertNull(r2.getCell(7), "Không có lý do: ô trống");
        assertEquals("danh-sach-tam-vang-2026-11-12.xlsx",
                TamVangExcel.tenTep(NGAY));
    }
}
