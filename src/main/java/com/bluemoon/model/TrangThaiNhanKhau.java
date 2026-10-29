package com.bluemoon.model;

/**
 * Trang thai cu tru cua nhan khau. Nguoi roi ho khong bi xoa ma doi trang
 * thai, de giu lich su va cho phep khai bao lai o ho khac.
 */
public enum TrangThaiNhanKhau {
    DANG_O("Đang ở"),
    DA_CHUYEN_DI("Đã chuyển đi");

    private final String nhan;

    TrangThaiNhanKhau(String nhan) {
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
