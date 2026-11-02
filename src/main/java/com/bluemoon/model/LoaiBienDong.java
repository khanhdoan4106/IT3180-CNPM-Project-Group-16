package com.bluemoon.model;

/**
 * Loai bien dong nhan khau trong ho (US-027).
 */
public enum LoaiBienDong {
    CHUYEN_DEN("Chuyển đến"),
    CHUYEN_DI("Chuyển đi"),
    TAM_TRU("Tạm trú"),
    TAM_VANG("Tạm vắng"),
    DOI_CHU_HO("Đổi chủ hộ");

    private final String nhan;

    LoaiBienDong(String nhan) {
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
