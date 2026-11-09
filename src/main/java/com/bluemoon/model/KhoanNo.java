package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Mot khoan phai nop chua dong cua ho (US-010, US-015): khoan bat buoc cua
 * mot thang da chot, chua co phieu thu con hieu luc.
 *
 * @param phaiNopId khoa chinh khoan phai nop
 * @param hoId ho phai nop
 * @param maCanHo ma can ho, de hien thi
 * @param khoanPhiId khoan thu trong danh muc
 * @param tenKhoan ten khoan thu
 * @param thang thang chot
 * @param soTien so tien phai nop hien tai
 */
public record KhoanNo(int phaiNopId, int hoId, String maCanHo, int khoanPhiId,
        String tenKhoan, YearMonth thang, BigDecimal soTien) {

    /**
     * Ten khoan kem thang, vd "Phí dịch vụ tháng 11/2026".
     *
     * @return mo ta de hien thi va in
     */
    public String moTa() {
        return tenKhoan + " tháng " + thang.getMonthValue() + "/"
                + thang.getYear();
    }
}
