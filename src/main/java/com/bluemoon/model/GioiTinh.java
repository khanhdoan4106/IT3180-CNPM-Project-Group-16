package com.bluemoon.model;

/**
 * Gioi tinh cua nhan khau (RAW-002).
 */
public enum GioiTinh {
    NAM("Nam"),
    NU("Nữ");

    private final String nhan;

    GioiTinh(String nhan) {
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
