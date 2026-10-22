package com.bluemoon.model;

/**
 * Mot dong trong danh sach ho: ho gia dinh kem ten chu ho va so thanh vien
 * (AC-005), lay tu bang nhan_khau.
 *
 * @param ho ho gia dinh
 * @param tenChuHo ho ten chu ho
 * @param soThanhVien so nhan khau cua ho
 */
public record TomTatHo(HoGiaDinh ho, String tenChuHo, int soThanhVien) {

    /** Hien trong o chon ho, vd "A0601 - Nguyen Thi Lan". */
    @Override
    public String toString() {
        return tenChuHo == null ? ho.getMaCanHo()
                : ho.getMaCanHo() + " - " + tenChuHo;
    }
}
