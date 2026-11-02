package com.bluemoon.model;

/**
 * Loai khoan thu (US-005, US-006).
 */
public enum LoaiKhoanPhi {
    BAT_BUOC("Bắt buộc"),
    TU_NGUYEN("Tự nguyện");

    private final String nhan;

    LoaiKhoanPhi(String nhan) {
        this.nhan = nhan;
    }

    /**
     * Nhan hien thi tieng Viet.
     *
     * @return nhan
     */
    public String getNhan() {
        return nhan;
    }

    @Override
    public String toString() {
        return nhan;
    }
}
