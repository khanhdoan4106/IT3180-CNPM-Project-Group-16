package com.bluemoon.model;

/**
 * Loai can ho trong toa nha BlueMoon: kiot tang 1, can ho, penthouse
 * tang thuong (RAW-033).
 */
public enum LoaiCanHo {
    CAN_HO("Can ho"),
    KIOT("Kiot"),
    PENTHOUSE("Penthouse");

    private final String nhan;

    LoaiCanHo(String nhan) {
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
