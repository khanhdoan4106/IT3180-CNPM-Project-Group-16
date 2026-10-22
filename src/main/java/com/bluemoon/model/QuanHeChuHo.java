package com.bluemoon.model;

/**
 * Quan he cua nhan khau voi chu ho (RAW-003, AC-006). Moi ho co dung mot
 * nhan khau CHU_HO.
 */
public enum QuanHeChuHo {
    CHU_HO("Chu ho"),
    VO_CHONG("Vo/chong"),
    CON("Con"),
    KHAC("Khac");

    private final String nhan;

    QuanHeChuHo(String nhan) {
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
