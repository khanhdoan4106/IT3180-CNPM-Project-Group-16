package com.bluemoon.model;

/**
 * Khoan thu con thu hay da ngung (khong xoa).
 */
public enum TrangThaiKhoanPhi {
    DANG_THU("Đang thu"),
    DA_DONG("Đã đóng");

    private final String nhan;

    TrangThaiKhoanPhi(String nhan) {
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
