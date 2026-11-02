package com.bluemoon.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Mot dong lich su bien dong nhan khau trong ho (US-027).
 *
 * @param id khoa chinh
 * @param hoId ho xay ra bien dong
 * @param maCanHo ma can ho, de hien thi
 * @param nhanKhauId nhan khau bien dong
 * @param hoTen ho ten nhan khau, de hien thi
 * @param loai loai bien dong
 * @param ngay ngay bien dong (ngay chuyen den, ngay bat dau dot...)
 * @param ghiChu ghi chu, co the null
 * @param nguoiGhi ho ten nguoi ghi nhan
 * @param thoiDiem thoi diem ghi vao he thong
 */
public record BienDong(int id, int hoId, String maCanHo, int nhanKhauId,
        String hoTen, LoaiBienDong loai, LocalDate ngay, String ghiChu,
        String nguoiGhi, LocalDateTime thoiDiem) {
}
