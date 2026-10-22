package com.bluemoon.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.nio.charset.StandardCharsets;

/**
 * Bam va kiem tra mat khau bang BCrypt. Khong bao gio luu mat khau goc.
 */
public final class MatKhauUtil {

    private static final int DO_KHO = 12;
    /** BCrypt ban 2a chi nhan toi da 71 byte UTF-8, dai hon thi nem loi. */
    public static final int TOI_DA_BYTE = 71;

    private MatKhauUtil() {
    }

    /**
     * Bam mat khau.
     *
     * @param matKhau mat khau goc
     * @return chuoi BCrypt
     */
    public static String bam(String matKhau) {
        return BCrypt.withDefaults().hashToString(DO_KHO,
                matKhau.toCharArray());
    }

    /**
     * Kiem tra mat khau voi gia tri da bam.
     *
     * @param matKhau mat khau nguoi dung nhap
     * @param matKhauBam chuoi BCrypt da luu
     * @return true neu khop
     */
    public static boolean kiemTra(String matKhau, String matKhauBam) {
        if (quaDai(matKhau)) {
            return false;
        }
        return BCrypt.verifyer()
                .verify(matKhau.toCharArray(), matKhauBam).verified;
    }

    /**
     * Mat khau co vuot gioi han cua BCrypt khong.
     *
     * @param matKhau mat khau goc
     * @return true neu dai hon {@link #TOI_DA_BYTE} byte UTF-8
     */
    public static boolean quaDai(String matKhau) {
        return matKhau.getBytes(StandardCharsets.UTF_8).length > TOI_DA_BYTE;
    }
}
