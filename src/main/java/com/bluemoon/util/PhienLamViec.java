package com.bluemoon.util;

import com.bluemoon.model.TaiKhoan;

/**
 * Luu tai khoan dang dang nhap trong suot phien su dung.
 */
public final class PhienLamViec {

    private static TaiKhoan nguoiDung;

    private PhienLamViec() {
    }

    /**
     * Dat nguoi dung hien tai.
     *
     * @param taiKhoan tai khoan vua dang nhap
     */
    public static void dat(TaiKhoan taiKhoan) {
        nguoiDung = taiKhoan;
    }

    /**
     * Lay nguoi dung hien tai.
     *
     * @return tai khoan, null neu chua dang nhap
     */
    public static TaiKhoan hienTai() {
        return nguoiDung;
    }

    /** Xoa phien (dang xuat). */
    public static void xoa() {
        nguoiDung = null;
    }
}
