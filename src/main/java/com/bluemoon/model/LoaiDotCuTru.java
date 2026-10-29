package com.bluemoon.model;

/**
 * Loai mot dot ghi nhan: tam tru hoac tam vang (RAW-004, RAW-005).
 */
public enum LoaiDotCuTru {
    TAM_TRU("Tạm trú"),
    TAM_VANG("Tạm vắng");

    private final String nhan;

    LoaiDotCuTru(String nhan) {
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
