package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Mot dong cua phieu thu (bang phieu_thu_chi_tiet).
 *
 * @param id khoa chinh, 0 khi chua luu
 * @param khoanPhiId khoan thu trong danh muc
 * @param tenKhoan ten khoan thu
 * @param phaiNopId khoan phai nop duoc thu; null voi dong gop tu nguyen
 * @param thang thang chot cua khoan phai nop; null voi dong gop tu nguyen
 * @param soTien so tien thu
 */
public record ChiTietPhieuThu(int id, int khoanPhiId, String tenKhoan,
        Integer phaiNopId, YearMonth thang, BigDecimal soTien) {

    /**
     * Ten khoan de in: khoan bat buoc kem thang, quy tu nguyen ghi ten quy.
     *
     * @return mo ta
     */
    public String moTa() {
        return thang == null ? tenKhoan : tenKhoan + " tháng "
                + thang.getMonthValue() + "/" + thang.getYear();
    }
}
