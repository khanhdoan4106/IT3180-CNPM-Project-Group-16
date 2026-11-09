package com.bluemoon.model;

import java.time.LocalDate;

/**
 * Mot dong cua danh sach nhan khau tam vang (US-004, AC-012).
 *
 * @param hoTen ho ten nhan khau
 * @param ngaySinh ngay sinh
 * @param soCccd so CCCD, co the null
 * @param maCanHo can ho
 * @param tuNgay ngay bat dau tam vang
 * @param denNgay ngay ket thuc tam vang
 * @param lyDo ly do, co the null
 */
public record TamVang(String hoTen, LocalDate ngaySinh, String soCccd,
        String maCanHo, LocalDate tuNgay, LocalDate denNgay, String lyDo) {
}
