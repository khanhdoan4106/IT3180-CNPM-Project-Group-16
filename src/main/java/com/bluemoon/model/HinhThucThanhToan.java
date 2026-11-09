package com.bluemoon.model;

/**
 * Hinh thuc thanh toan cua phieu thu (US-012, AC-036).
 */
public enum HinhThucThanhToan {
    TIEN_MAT("Tiền mặt"),
    CHUYEN_KHOAN("Chuyển khoản");

    private final String nhan;

    HinhThucThanhToan(String nhan) {
        this.nhan = nhan;
    }

    /**
     * Ten hien thi tren giao dien.
     *
     * @return nhan hien thi
     */
    public String getNhan() {
        return nhan;
    }

    @Override
    public String toString() {
        return nhan;
    }
}
