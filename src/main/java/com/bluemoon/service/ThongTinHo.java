package com.bluemoon.service;

import com.bluemoon.model.LoaiCanHo;
import java.time.LocalDate;

/**
 * Du lieu nguoi dung nhap tren form ho gia dinh, giu nguyen dang chuoi de
 * service kiem tra va bao loi tung truong (AC-003).
 *
 * @param maCanHo ma can ho
 * @param loaiCanHo loai can ho, null neu chua chon
 * @param tang so tang dang chuoi
 * @param dienTich dien tich m2 dang chuoi, chap nhan dau phay hoac cham
 * @param tenChuHo ho ten chu ho
 * @param soDienThoai so dien thoai, co the de trong
 * @param ngayChuyenDen ngay chuyen den, null neu chua chon
 */
public record ThongTinHo(String maCanHo, LoaiCanHo loaiCanHo, String tang,
        String dienTich, String tenChuHo, String soDienThoai,
        LocalDate ngayChuyenDen) {
}
