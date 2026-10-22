package com.bluemoon.service;

import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.QuanHeChuHo;
import java.time.LocalDate;

/**
 * Du lieu nguoi dung nhap tren form nhan khau, giu dang tho de service kiem
 * tra va bao loi tung truong.
 *
 * @param hoTen ho ten
 * @param ngaySinh ngay sinh, null neu chua chon
 * @param gioiTinh gioi tinh, null neu chua chon
 * @param soCccd so CCCD, co the de trong (duoi 14 tuoi)
 * @param quanHe quan he voi chu ho, null neu chua chon
 * @param loaiCuTru loai dang ky cu tru, null neu chua chon (US-003)
 * @param noiThuongTru noi thuong tru, bat buoc voi nguoi tam tru
 */
public record ThongTinNhanKhau(String hoTen, LocalDate ngaySinh,
        GioiTinh gioiTinh, String soCccd, QuanHeChuHo quanHe,
        LoaiCuTru loaiCuTru, String noiThuongTru) {

    /**
     * Form cua nguoi thuong tru (khong co noi thuong tru rieng).
     *
     * @param hoTen ho ten
     * @param ngaySinh ngay sinh
     * @param gioiTinh gioi tinh
     * @param soCccd so CCCD
     * @param quanHe quan he voi chu ho
     */
    public ThongTinNhanKhau(String hoTen, LocalDate ngaySinh,
            GioiTinh gioiTinh, String soCccd, QuanHeChuHo quanHe) {
        this(hoTen, ngaySinh, gioiTinh, soCccd, quanHe,
                LoaiCuTru.THUONG_TRU, null);
    }
}
