package com.bluemoon.xuat;

import com.bluemoon.model.BaoCaoThu;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Tệp Excel báo cáo thu tiền một tháng (US-018, AC-050, giả định 41): tiêu
 * đề, ngày lập, bảng STT, mã căn hộ, chủ hộ, mỗi khoản/quỹ một cột, tổng,
 * ngày thu, số biên lai; dòng cuối "Tổng cộng" cộng từng cột tiền.
 */
public final class BaoCaoThuExcel {

    /** Dòng tiêu đề bảng (chỉ số từ 0). */
    public static final int DONG_TIEU_DE = 3;

    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private BaoCaoThuExcel() {
    }

    /**
     * Tên sheet, vd "Thu 11-2026" (tên sheet không được có dấu "/").
     *
     * @param thang tháng báo cáo
     * @return tên sheet
     */
    public static String tenSheet(YearMonth thang) {
        return "Thu " + thang.getMonthValue() + "-" + thang.getYear();
    }

    /**
     * Tên tệp gợi ý, vd "bao-cao-thu-2026-11.xlsx".
     *
     * @param thang tháng báo cáo
     * @return tên tệp
     */
    public static String tenTep(YearMonth thang) {
        return String.format("bao-cao-thu-%d-%02d.xlsx", thang.getYear(),
                thang.getMonthValue());
    }

    /**
     * Ghi báo cáo ra luồng dạng .xlsx.
     *
     * @param bc báo cáo, ít nhất một dòng
     * @param lapNgay ngày lập báo cáo
     * @param ra luồng ghi
     * @throws IOException lỗi ghi
     */
    public static void ghi(BaoCaoThu bc, LocalDate lapNgay, OutputStream ra)
            throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            TepExcel.KieuO k = new TepExcel.KieuO(wb);
            Sheet sh = wb.createSheet(tenSheet(bc.thang()));
            YearMonth t = bc.thang();
            TepExcel.chu(sh.createRow(0), 0, "BÁO CÁO THU TIỀN THÁNG "
                    + t.getMonthValue() + "/" + t.getYear(), k.tieuDeTrang);
            TepExcel.chu(sh.createRow(1), 0, "Lập ngày "
                    + lapNgay.format(NGAY), null);

            List<String> tieuDe = new ArrayList<>(List.of("STT",
                    "Mã căn hộ", "Chủ hộ"));
            bc.cot().forEach(c -> tieuDe.add(c.ten()));
            tieuDe.addAll(List.of("Tổng", "Ngày thu", "Số biên lai"));
            int soCot = tieuDe.size();
            int cotTong = 3 + bc.cot().size();
            int[] doDai = new int[soCot];
            Row td = sh.createRow(DONG_TIEU_DE);
            for (int i = 0; i < soCot; i++) {
                TepExcel.chu(td, i, tieuDe.get(i), k.tieuDe);
                doDai[i] = tieuDe.get(i).length();
            }

            BigDecimal[] cong = new BigDecimal[bc.cot().size() + 1];
            Arrays.fill(cong, BigDecimal.ZERO);
            int so = DONG_TIEU_DE + 1;
            int stt = 1;
            for (BaoCaoThu.Dong d : bc.dong()) {
                Row r = sh.createRow(so++);
                r.createCell(0).setCellValue(stt);
                TepExcel.dai(doDai, 0, String.valueOf(stt++));
                TepExcel.chu(r, 1, d.maCanHo(), null);
                TepExcel.dai(doDai, 1, d.maCanHo());
                TepExcel.chu(r, 2, d.chuHo(), null);
                TepExcel.dai(doDai, 2, d.chuHo());
                for (int i = 0; i < bc.cot().size(); i++) {
                    BigDecimal tien = d.tien().get(bc.cot().get(i)
                            .khoanPhiId());
                    TepExcel.tien(r, 3 + i, tien, k.tien);
                    if (tien != null) {
                        cong[i] = cong[i].add(tien);
                        TepExcel.dai(doDai, 3 + i, tienChu(tien));
                    }
                }
                TepExcel.tien(r, cotTong, d.tong(), k.tien);
                cong[cong.length - 1] = cong[cong.length - 1].add(d.tong());
                TepExcel.dai(doDai, cotTong, tienChu(d.tong()));
                TepExcel.ngay(r, cotTong + 1, d.thoiDiem().toLocalDate(),
                        k.ngay);
                TepExcel.dai(doDai, cotTong + 1, "dd/mm/yyyy");
                TepExcel.chu(r, cotTong + 2, d.soBienLai(), null);
                TepExcel.dai(doDai, cotTong + 2, d.soBienLai());
            }
            Row tong = sh.createRow(so);
            TepExcel.chu(tong, 0, "Tổng cộng", k.dam);
            for (int i = 0; i < cong.length; i++) {
                TepExcel.tien(tong, 3 + i, cong[i], k.tienDam);
                TepExcel.dai(doDai, 3 + i, tienChu(cong[i]));
            }
            TepExcel.hoanTat(sh, soCot, DONG_TIEU_DE, doDai);
            wb.write(ra);
        }
    }

    private static String tienChu(BigDecimal tien) {
        return NumberFormat.getIntegerInstance(Locale.forLanguageTag(
                "vi-VN")).format(tien);
    }
}
