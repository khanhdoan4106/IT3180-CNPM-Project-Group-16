package com.bluemoon.model;

/**
 * Trang thai cu tru cua ho. Ho chuyen di khong bi xoa de giu lich su thu
 * phi.
 */
public enum TrangThaiHo {
    DANG_O("Đang ở"),
    DA_CHUYEN_DI("Đã chuyển đi");

    private final String nhan;

    TrangThaiHo(String nhan) {
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
