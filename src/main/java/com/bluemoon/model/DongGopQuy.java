package com.bluemoon.model;

import java.math.BigDecimal;

/**
 * Dong gop cua mot ho cho mot quy tu nguyen dang mo (US-007): tong cac lan
 * da dong tren phieu con hieu luc (gia dinh 29).
 *
 * @param quy khoan thu tu nguyen
 * @param tongDaDong tong so tien da dong, 0 neu chua dong lan nao
 * @param soLan so lan da ghi, ke ca lan ghi 0 d
 */
public record DongGopQuy(KhoanPhi quy, BigDecimal tongDaDong, int soLan) {

    /**
     * Tinh trang dong gop: co tien thi da dong; chi co lan ghi 0 d thi
     * khong dong; chua co lan nao thi chua thu.
     *
     * @return tinh trang
     */
    public TrangThaiDongGop trangThai() {
        if (tongDaDong.signum() > 0) {
            return TrangThaiDongGop.DA_DONG;
        }
        return soLan > 0 ? TrangThaiDongGop.KHONG_DONG
                : TrangThaiDongGop.CHUA_THU;
    }
}
