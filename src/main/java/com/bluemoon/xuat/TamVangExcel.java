package com.bluemoon.xuat;

import com.bluemoon.model.TamVang;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Tệp Excel danh sách nhân khẩu tạm vắng (US-004, AC-014, giả định 36,
 * 42), cùng các trường như bản in.
 */
public final class TamVangExcel {

    /** Dòng tiêu đề bảng (chỉ số từ 0). */
    public static final int DONG_TIEU_DE = 2;
    /** Tên sheet. */
    public static final String TEN_SHEET = "Tạm vắng";
    /** Các cột, theo thứ tự. */
    public static final List<String> COT = List.of("STT", "Họ và tên",
            "Ngày sinh", "Số CCCD", "Căn hộ", "Từ ngày", "Đến ngày",
            "Lý do");

    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final String MAU_NGAY = "dd/mm/yyyy";

    private TamVangExcel() {
    }

    /**
     * Tên tệp gợi ý, vd "danh-sach-tam-vang-2026-11-12.xlsx".
     *
     * @param ngay ngày xem danh sách
     * @return tên tệp
     */
    public static String tenTep(LocalDate ngay) {
        return "danh-sach-tam-vang-" + ngay + ".xlsx";
    }

    /**
     * Ghi danh sách ra luồng dạng .xlsx.
     *
     * @param ds danh sách tạm vắng, ít nhất một người
     * @param ngay ngày xem danh sách
     * @param ra luồng ghi
     * @throws IOException lỗi ghi
     */
    public static void ghi(List<TamVang> ds, LocalDate ngay, OutputStream ra)
            throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            TepExcel.KieuO k = new TepExcel.KieuO(wb);
            Sheet sh = wb.createSheet(TEN_SHEET);
            TepExcel.chu(sh.createRow(0), 0, "DANH SÁCH NHÂN KHẨU TẠM VẮNG"
                    + " NGÀY " + ngay.format(NGAY), k.tieuDeTrang);
            int[] doDai = new int[COT.size()];
            Row td = sh.createRow(DONG_TIEU_DE);
            for (int i = 0; i < COT.size(); i++) {
                TepExcel.chu(td, i, COT.get(i), k.tieuDe);
                TepExcel.dai(doDai, i, COT.get(i));
            }
            int so = DONG_TIEU_DE + 1;
            int stt = 1;
            for (TamVang v : ds) {
                Row r = sh.createRow(so++);
                r.createCell(0).setCellValue(stt);
                TepExcel.dai(doDai, 0, String.valueOf(stt++));
                TepExcel.chu(r, 1, v.hoTen(), null);
                TepExcel.dai(doDai, 1, v.hoTen());
                TepExcel.ngay(r, 2, v.ngaySinh(), k.ngay);
                TepExcel.dai(doDai, 2, MAU_NGAY);
                TepExcel.chu(r, 3, v.soCccd(), null);
                TepExcel.dai(doDai, 3, v.soCccd());
                TepExcel.chu(r, 4, v.maCanHo(), null);
                TepExcel.dai(doDai, 4, v.maCanHo());
                TepExcel.ngay(r, 5, v.tuNgay(), k.ngay);
                TepExcel.dai(doDai, 5, MAU_NGAY);
                TepExcel.ngay(r, 6, v.denNgay(), k.ngay);
                TepExcel.dai(doDai, 6, MAU_NGAY);
                TepExcel.chu(r, 7, v.lyDo(), null);
                TepExcel.dai(doDai, 7, v.lyDo());
            }
            TepExcel.hoanTat(sh, COT.size(), DONG_TIEU_DE, doDai);
            wb.write(ra);
        }
    }
}
