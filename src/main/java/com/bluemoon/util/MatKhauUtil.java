package com.bluemoon.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Bam va kiem tra mat khau bang BCrypt. Khong bao gio luu mat khau goc.
 */
public final class MatKhauUtil {

    private static final int DO_KHO = 12;

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
        return BCrypt.verifyer()
                .verify(matKhau.toCharArray(), matKhauBam).verified;
    }
}
