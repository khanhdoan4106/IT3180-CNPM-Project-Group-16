package com.bluemoon.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * So tien lon nhat nhap duoc: 1 ty dong. Vua cot DECIMAL(12,2) cua
     * don gia, va dien tich toi da (450 m2) x don gia toi da van vua cot
     * DECIMAL(14,2) cua khoan phai nop.
     */
    public static final BigDecimal TOI_DA = new BigDecimal("1000000000");

    private SoTien() {
    }

    /**
     * Doc so tien nguoi dung nhap.
     *
     * @param giaTri chuoi tren form, co the null
     * @param tenTruong ten truong, dung trong thong bao loi
     * @return so tien, null neu bo trong
     * @throws NghiepVuException neu khong phai so nguyen khong am hoac lon
     *         hon {@link #TOI_DA}
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
        BigDecimal soTien = new BigDecimal(s.replace(".", "")
                .replace(" ", ""));
        if (soTien.compareTo(TOI_DA) > 0) {
            throw new NghiepVuException(tenTruong + " tối đa "
                    + hienThi(TOI_DA));
        }
        return soTien;
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

    private static final String[] CHU_SO = {"không", "một", "hai", "ba",
        "bốn", "năm", "sáu", "bảy", "tám", "chín"};
    private static final String[] HANG = {"", " nghìn", " triệu", " tỷ",
        " nghìn tỷ"};

    /**
     * Doc so tien thanh chu de in bien lai, vd 1.283.500 thanh "Một triệu
     * hai trăm tám mươi ba nghìn năm trăm đồng".
     *
     * @param soTien so tien nguyen dong, khong am, nho hon 10^15
     * @return so tien bang chu, viet hoa chu dau, ket thuc "đồng"
     */
    public static String bangChu(BigDecimal soTien) {
        long n = soTien.setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
        if (n < 0 || n >= 1_000_000_000_000_000L) {
            throw new IllegalArgumentException("Ngoài phạm vi: " + soTien);
        }
        if (n == 0) {
            return "Không đồng";
        }
        List<String> nhom = new ArrayList<>();
        int hang = 0;
        while (n > 0) {
            int ba = (int) (n % 1000);
            n /= 1000;
            if (ba > 0) {
                nhom.add(0, docBaSo(ba, n > 0) + HANG[hang]);
            }
            hang++;
        }
        String chu = String.join(" ", nhom);
        return Character.toUpperCase(chu.charAt(0)) + chu.substring(1)
                + " đồng";
    }

    /** Doc nhom ba chu so; du = true khi con nhom lon hon dung truoc. */
    private static String docBaSo(int ba, boolean du) {
        int tram = ba / 100;
        int chuc = ba / 10 % 10;
        int donVi = ba % 10;
        List<String> chu = new ArrayList<>();
        if (tram > 0 || du) {
            chu.add(CHU_SO[tram] + " trăm");
        }
        if (chuc == 0) {
            if (donVi > 0 && (tram > 0 || du)) {
                chu.add("linh");
            }
        } else if (chuc == 1) {
            chu.add("mười");
        } else {
            chu.add(CHU_SO[chuc] + " mươi");
        }
        if (donVi == 1 && chuc > 1) {
            chu.add("mốt");
        } else if (donVi == 5 && chuc > 0) {
            chu.add("lăm");
        } else if (donVi > 0) {
            chu.add(CHU_SO[donVi]);
        }
        return String.join(" ", chu);
    }
}
