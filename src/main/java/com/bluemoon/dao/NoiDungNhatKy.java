package com.bluemoon.dao;

import com.bluemoon.model.DotCuTru;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Viet noi dung nhat ky thao tac (TS-002) theo kieu Viet Nam: so tien
 * "1.260.000 đ", dien tich "75,50 m²", ngay "08/11/2026". Khong bao gio dua
 * mat khau hay ma bam vao day.
 */
final class NoiDungNhatKy {

    private static final Locale VI = Locale.forLanguageTag("vi-VN");
    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /** Gia tri trong (null hoac rong) hien the nay. */
    static final String TRONG = "(trống)";

    private NoiDungNhatKy() {
    }

    static String tien(BigDecimal soTien) {
        return soTien == null ? TRONG
                : NumberFormat.getIntegerInstance(VI).format(soTien) + " đ";
    }

    static String dienTich(BigDecimal m2) {
        if (m2 == null) {
            return TRONG;
        }
        DecimalFormat f = new DecimalFormat("#,##0.00",
                DecimalFormatSymbols.getInstance(VI));
        return f.format(m2) + " m²";
    }

    static String ngay(LocalDate ngay) {
        return ngay == null ? TRONG : ngay.format(NGAY);
    }

    static String thang(YearMonth thang) {
        return String.format("%02d/%d", thang.getMonthValue(),
                thang.getYear());
    }

    static String chu(String s) {
        return s == null || s.isBlank() ? TRONG : s;
    }

    /** Doi tuong nhat ky cua mot nhan khau, vd "Nhân khẩu An, hộ A-1203". */
    static String nhanKhau(String hoTen, String maCanHo) {
        return "Nhân khẩu " + hoTen + ", hộ " + maCanHo;
    }

    /** Mot dot tam tru/tam vang, vd "Tạm vắng 01/11/2026 – 30/11/2026". */
    static String dot(DotCuTru dot) {
        String lyDo = dot.getLyDo() == null || dot.getLyDo().isBlank() ? ""
                : ". Lý do: " + dot.getLyDo();
        return dot.getLoai().getNhan() + " " + ngay(dot.getTuNgay()) + " – "
                + ngay(dot.getDenNgay()) + lyDo;
    }

    /**
     * Gom cac truong thuc su doi, dang "Diện tích: 75,50 m² → 80,00 m²".
     */
    static final class ThayDoi {

        private final List<String> cacDoi = new ArrayList<>();

        /**
         * Them mot truong neu gia tri hien thi cu va moi khac nhau.
         *
         * @param truong ten truong
         * @param cu gia tri cu da dinh dang
         * @param moi gia tri moi da dinh dang
         * @return chinh no
         */
        ThayDoi them(String truong, String cu, String moi) {
            if (!Objects.equals(cu, moi)) {
                cacDoi.add(truong + ": " + cu + " → " + moi);
            }
            return this;
        }

        boolean rong() {
            return cacDoi.isEmpty();
        }

        @Override
        public String toString() {
            return String.join("; ", cacDoi);
        }
    }
}
