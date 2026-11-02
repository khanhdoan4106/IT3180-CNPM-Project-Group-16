package com.bluemoon.model;

/**
 * Cach tinh so tien phai nop cua mot khoan thu.
 */
public enum CachTinhPhi {
    THEO_DIEN_TICH("Theo diện tích"),
    CO_DINH_THEO_HO("Cố định theo hộ"),
    KHONG_CO_DINH("Không cố định");

    private final String nhan;

    CachTinhPhi(String nhan) {
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
