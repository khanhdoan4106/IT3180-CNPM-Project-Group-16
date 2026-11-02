package com.bluemoon.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Doc va hien thi so tien dong (so nguyen, nhom hang nghin bang dau cham).
 */
public final class SoTien {

    /** Cho phep "350000", "350.000", "350 000", co the kem "đ". */
    private static final Pattern MAU =
            Pattern.compile("\\d{1,3}([. ]\\d{3})*|\\d+");

    private SoTien() {
    }

    /**
     * Doc so tien nguoi dung nhap.
     *
     * @param giaTri chuoi tren form, co the null
     * @param tenTruong ten truong, dung trong thong bao loi
     * @return so tien, null neu bo trong
     * @throws NghiepVuException neu khong phai so nguyen khong am
     */
    public static BigDecimal doc(String giaTri, String tenTruong) {
        String s = giaTri == null ? "" : giaTri.strip();
        if (s.endsWith("đ")) {
            s = s.substring(0, s.length() - 1).strip();
        }
        if (s.isEmpty()) {
            return null;
        }
        if (!MAU.matcher(s).matches()) {
            throw new NghiepVuException(tenTruong
                    + " phải là số nguyên không âm, ví dụ 5.000");
        }
        return new BigDecimal(s.replace(".", "").replace(" ", ""));
    }

    /**
     * Hien thi so tien kieu Viet Nam, vd 1.260.000 đ.
     *
     * @param soTien so tien
     * @return chuoi hien thi
     */
    public static String hienThi(BigDecimal soTien) {
        NumberFormat f = NumberFormat.getIntegerInstance(
                Locale.forLanguageTag("vi-VN"));
        return f.format(soTien) + " đ";
    }
}
