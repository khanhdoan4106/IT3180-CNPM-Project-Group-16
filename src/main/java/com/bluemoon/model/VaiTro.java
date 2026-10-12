package com.bluemoon.model;

/**
 * Vai tro nguoi dung. Danh sach vai tro chua chot voi nhom
 * (xem docs/requirements/11-chot-va-gia-dinh.md).
 */
public enum VaiTro {
    QUAN_TRI("Quan tri vien"),
    THU_QUY("Thu quy");

    private final String nhan;

    VaiTro(String nhan) {
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
}
