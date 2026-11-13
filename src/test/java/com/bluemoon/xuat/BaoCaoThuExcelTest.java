package com.bluemoon.xuat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.BaoCaoThu;
import com.bluemoon.model.LoaiKhoanPhi;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tệp báo cáo thu (US-018, AC-050, giả định 41), đọc lại bằng POI: tiêu đề,
 * thứ tự cột, ô tiền là số, ô ngày là ngày, phiếu thu nhiều tháng cộng vào
 * một cột, ô trống khác ô 0, dòng Tổng cộng, tên sheet.
 */
class BaoCaoThuExcelTest {

    private static final YearMonth T11 = YearMonth.of(2026, 11);
    private static final int DV = 1;
    private static final int QL = 2;
    private static final int NGHEO = 3;
    private static final int KHUYEN_HOC = 4;

    private static Workbook wb;
    private static Sheet sh;

    private static BaoCaoThu.ChiTiet ct(int phieu, String ma, String chuHo,
            String tong, int ngay, String so, int khoan, String soTien) {
        String ten = switch (khoan) {
            case DV -> "Phí dịch vụ";
            case QL -> "Phí quản lý";
            case NGHEO -> "Quỹ vì người nghèo 2026";
            default -> "Quỹ khuyến học 2026";
        };
        LoaiKhoanPhi loai = khoan <= QL ? LoaiKhoanPhi.BAT_BUOC
                : LoaiKhoanPhi.TU_NGUYEN;
        return new BaoCaoThu.ChiTiet(phieu, ma, chuHo, new BigDecimal(tong),
                LocalDateTime.of(2026, 11, ngay, 9, 30), so, khoan, ten,
                loai, new BigDecimal(soTien));
    }

    /** Dữ liệu chung: hai phiếu, cột gặp lẫn lộn để kiểm thứ tự cột. */
    static BaoCaoThu mau() {
        List<BaoCaoThu.ChiTiet> ds = new ArrayList<>();
        // Phieu 10: quy 0 d, phi dich vu thang 10 va 11, phi quan ly.
        ds.add(ct(10, "A0601", "Nguyễn Thị Lan", "1283500", 3,
                "PT-2026-00010", NGHEO, "0"));
        ds.add(ct(10, "A0601", "Nguyễn Thị Lan", "1283500", 3,
                "PT-2026-00010", DV, "377500"));
        ds.add(ct(10, "A0601", "Nguyễn Thị Lan", "1283500", 3,
                "PT-2026-00010", DV, "377500"));
        ds.add(ct(10, "A0601", "Nguyễn Thị Lan", "1283500", 3,
                "PT-2026-00010", QL, "528500"));
        // Phieu 11: ho khong con chu ho, phi dich vu va quy khuyen hoc.
        ds.add(ct(11, "A0602", null, "500000", 5, "PT-2026-00011",
                KHUYEN_HOC, "200000"));
        ds.add(ct(11, "A0602", null, "500000", 5, "PT-2026-00011",
                DV, "300000"));
        return BaoCaoThu.gom(T11, ds);
    }

    @BeforeAll
    static void ghiRoiDocLai() throws IOException {
        ByteArrayOutputStream ra = new ByteArrayOutputStream();
        BaoCaoThuExcel.ghi(mau(), LocalDate.of(2026, 12, 1), ra);
        wb = new XSSFWorkbook(new ByteArrayInputStream(ra.toByteArray()));
        sh = wb.getSheetAt(0);
    }

    private static Cell o(int dong, int cot) {
        Row r = sh.getRow(dong);
        return r == null ? null : r.getCell(cot);
    }

    private static List<String> dongChu(int dong) {
        List<String> kq = new ArrayList<>();
        for (Cell c : sh.getRow(dong)) {
            kq.add(c.getStringCellValue());
        }
        return kq;
    }

    private static void tien(double mong, Cell c) {
        assertEquals(CellType.NUMERIC, c.getCellType(), "ô tiền là số");
        assertEquals("#,##0", c.getCellStyle().getDataFormatString());
        assertEquals(mong, c.getNumericCellValue());
    }

    @Test
    void tenSheetTieuDeVaNgayLap() {
        assertEquals(1, wb.getNumberOfSheets());
        assertEquals("Thu 11-2026", sh.getSheetName());
        assertEquals("BÁO CÁO THU TIỀN THÁNG 11/2026",
                o(0, 0).getStringCellValue());
        assertEquals("Lập ngày 01/12/2026", o(1, 0).getStringCellValue());
        assertEquals("bao-cao-thu-2026-11.xlsx",
                BaoCaoThuExcel.tenTep(T11));
    }

    /** Khoản bắt buộc trước, quỹ tự nguyện sau, cùng loại theo tên. */
    @Test
    void thuTuCotVaTieuDeDamCoDinh() {
        assertEquals(List.of("STT", "Mã căn hộ", "Chủ hộ", "Phí dịch vụ",
                "Phí quản lý", "Quỹ khuyến học 2026",
                "Quỹ vì người nghèo 2026", "Tổng", "Ngày thu", "Số biên lai"),
                dongChu(BaoCaoThuExcel.DONG_TIEU_DE));
        assertTrue(wb.getFontAt(o(BaoCaoThuExcel.DONG_TIEU_DE, 0)
                .getCellStyle().getFontIndex()).getBold());
        assertEquals(BaoCaoThuExcel.DONG_TIEU_DE + 1,
                sh.getPaneInformation().getHorizontalSplitPosition());
        assertTrue(sh.getColumnWidth(6) > sh.getColumnWidth(0),
                "Cột theo độ dài nội dung");
    }

    /** Một phiếu đóng phí dịch vụ hai tháng: cộng vào một cột. */
    @Test
    void oTienLaSoVaPhieuNhieuThangCongMotCot() {
        int d = BaoCaoThuExcel.DONG_TIEU_DE + 1;
        assertEquals(1.0, o(d, 0).getNumericCellValue());
        assertEquals("A0601", o(d, 1).getStringCellValue());
        assertEquals("Nguyễn Thị Lan", o(d, 2).getStringCellValue());
        tien(755000, o(d, 3));
        tien(528500, o(d, 4));
        tien(1283500, o(d, 7));
        assertEquals("PT-2026-00010", o(d, 9).getStringCellValue());
    }

    /** Khoản không có trong phiếu để trống; đóng góp 0 đ ghi 0. */
    @Test
    void oTrongKhacOKhong() {
        int d = BaoCaoThuExcel.DONG_TIEU_DE + 1;
        assertNull(o(d, 5), "Không đóng quỹ khuyến học: ô trống");
        tien(0, o(d, 6));
        assertNull(o(d + 1, 2), "Hộ không còn chủ hộ: ô trống");
        tien(300000, o(d + 1, 3));
        assertNull(o(d + 1, 4));
        tien(200000, o(d + 1, 5));
    }

    @Test
    void oNgayLaNgay() {
        Cell c = o(BaoCaoThuExcel.DONG_TIEU_DE + 1, 8);
        assertEquals(CellType.NUMERIC, c.getCellType());
        assertTrue(DateUtil.isCellDateFormatted(c));
        assertEquals("dd/mm/yyyy", c.getCellStyle().getDataFormatString());
        assertEquals(LocalDate.of(2026, 11, 3),
                c.getLocalDateTimeCellValue().toLocalDate());
    }

    @Test
    void dongTongCongCongTungCotTien() {
        int d = BaoCaoThuExcel.DONG_TIEU_DE + 3;
        assertEquals("Tổng cộng", o(d, 0).getStringCellValue());
        tien(1055000, o(d, 3));
        tien(528500, o(d, 4));
        tien(200000, o(d, 5));
        tien(0, o(d, 6));
        tien(1783500, o(d, 7));
        assertNull(sh.getRow(d + 1), "Không còn dòng nào sau Tổng cộng");
    }
}
