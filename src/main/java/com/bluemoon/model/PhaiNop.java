package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Khoan phai nop cua mot ho cho mot khoan bat buoc trong mot thang
 * (bang phai_nop, US-009, US-008). Dien tich va don gia la gia tri luc chot.
 *
 * @param id khoa chinh, 0 khi chua luu
 * @param hoId ho phai nop
 * @param maCanHo ma can ho, de hien thi
 * @param khoanPhiId khoan thu
 * @param tenKhoan ten khoan thu, de hien thi
 * @param dienTich dien tich luc chot (m2)
 * @param donGia don gia luc chot (d/m2)
 * @param soTienGoc so tien he thong tinh
 * @param soTien so tien hien tai, khac so goc neu da sua
 * @param nguoiSua id tai khoan sua gan nhat, null neu chua sua
 * @param ngaySua thoi diem sua gan nhat, null neu chua sua
 * @param ghiChu ly do sua, co the null
 * @param daDong da co phieu thu con hieu luc chua (Sprint 3); khoan da dong
 *        khong sua duoc (AC-026)
 */
public record PhaiNop(int id, int hoId, String maCanHo, int khoanPhiId,
        String tenKhoan, BigDecimal dienTich, BigDecimal donGia,
        BigDecimal soTienGoc, BigDecimal soTien, Integer nguoiSua,
        LocalDateTime ngaySua, String ghiChu, boolean daDong) {

    /**
     * Khoan da duoc sua khac so he thong tinh chua.
     *
     * @return true neu so tien hien tai khac so goc
     */
    public boolean daSua() {
        return soTien.compareTo(soTienGoc) != 0;
    }
}
