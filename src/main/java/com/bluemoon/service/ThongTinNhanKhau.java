package com.bluemoon.service;

import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.QuanHeChuHo;
import java.time.LocalDate;

/**
 * Du lieu nguoi dung nhap tren form nhan khau, giu dang tho de service kiem
 * tra va bao loi tung truong.
 *
 * @param hoTen ho ten
 * @param ngaySinh ngay sinh, null neu chua chon
 * @param gioiTinh gioi tinh, null neu chua chon
 * @param soCccd so CCCD, co the de trong (tre em chua co)
 * @param quanHe quan he voi chu ho, null neu chua chon
 */
public record ThongTinNhanKhau(String hoTen, LocalDate ngaySinh,
        GioiTinh gioiTinh, String soCccd, QuanHeChuHo quanHe) {
}
