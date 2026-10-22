package com.bluemoon.model;

import java.time.LocalDate;

/**
 * Tinh trang cu tru cua mot nhan khau tai mot ngay, tinh tu cac dot tam
 * tru/tam vang (US-003). Khong luu vao CSDL.
 *
 * @param hanTamTru ngay ket thuc muon nhat cua cac dot tam tru, null neu
 *        khong co dot tam tru nao
 * @param quaHanTamTru nguoi dang ky tam tru ma da qua han, chua gia han
 * @param tamVangDen ngay ket thuc dot tam vang dang hieu luc, null neu khong
 *        dang tam vang
 */
public record TinhTrangCuTru(LocalDate hanTamTru, boolean quaHanTamTru,
        LocalDate tamVangDen) {

    /** Tinh trang cua nguoi khong co dot nao. */
    public static final TinhTrangCuTru TRONG =
            new TinhTrangCuTru(null, false, null);
}
