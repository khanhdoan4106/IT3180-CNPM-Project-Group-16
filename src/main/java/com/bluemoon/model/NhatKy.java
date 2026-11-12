package com.bluemoon.model;

import java.time.LocalDateTime;

/**
 * Mot dong nhat ky thao tac (TS-002). Chi doc: nhat ky khong sua, khong xoa.
 *
 * @param id khoa chinh
 * @param thoiDiem thoi diem ghi (gio CSDL, +07:00)
 * @param taiKhoanId nguoi lam
 * @param hoTen ho ten nguoi lam
 * @param tenDangNhap ten dang nhap nguoi lam
 * @param thaoTac loai thao tac
 * @param doiTuong doi tuong, vd "Hộ A-1203", "Phiếu PT-2026-00012"
 * @param noiDung noi dung thay doi, co the null
 */
public record NhatKy(int id, LocalDateTime thoiDiem, int taiKhoanId,
        String hoTen, String tenDangNhap, ThaoTac thaoTac, String doiTuong,
        String noiDung) {
}
