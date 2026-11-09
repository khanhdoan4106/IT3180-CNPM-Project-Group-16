package com.bluemoon.model;

/**
 * Tinh trang dong gop cua mot ho cho mot quy tu nguyen (AC-023, gia dinh
 * 29): "Khong dong" la da hoi va ho tu choi (ghi 0 d), khac "Chua thu".
 */
public enum TrangThaiDongGop {
    CHUA_THU("Chưa thu"),
    KHONG_DONG("Không đóng"),
    DA_DONG("Đã đóng");

    private final String nhan;

    TrangThaiDongGop(String nhan) {
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
