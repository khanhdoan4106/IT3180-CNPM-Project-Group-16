package com.bluemoon.model;

/**
 * Loai dang ky cu tru cua nhan khau (US-003). Day la loai dang ky, khong doi
 * theo ngay: nguoi tam tru het han ma chua gia han van la TAM_TRU.
 */
public enum LoaiCuTru {
    THUONG_TRU("Thường trú"),
    TAM_TRU("Tạm trú");

    private final String nhan;

    LoaiCuTru(String nhan) {
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
