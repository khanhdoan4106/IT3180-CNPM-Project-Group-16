package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Phieu thu cua mot ho (bang phieu_thu, US-011 den US-014, US-007).
 *
 * @param id khoa chinh
 * @param nam nam cua so bien lai
 * @param so so thu tu trong nam
 * @param hoId ho nop tien
 * @param maCanHo ma can ho
 * @param tenChuHo ho ten chu ho, co the null
 * @param tongTien tong cac dong
 * @param hinhThuc hinh thuc thanh toan; null khi phieu khong thu tien
 * @param nguoiThuId tai khoan lap phieu
 * @param tenNguoiThu ho ten nguoi lap phieu
 * @param thoiDiem thoi diem lap
 * @param soLanIn so lan da in bien lai
 * @param daHuy phieu da bi huy chua
 * @param tenNguoiHuy ho ten nguoi huy, null neu chua huy
 * @param thoiDiemHuy thoi diem huy, null neu chua huy
 * @param lyDoHuy ly do huy, null neu chua huy
 * @param chiTiet cac dong cua phieu
 */
public record PhieuThu(int id, int nam, int so, int hoId, String maCanHo,
        String tenChuHo, BigDecimal tongTien, HinhThucThanhToan hinhThuc,
        int nguoiThuId, String tenNguoiThu, LocalDateTime thoiDiem,
        int soLanIn, boolean daHuy, String tenNguoiHuy,
        LocalDateTime thoiDiemHuy, String lyDoHuy,
        List<ChiTietPhieuThu> chiTiet) {

    /**
     * So bien lai, vd "PT-2026-00001" (gia dinh 31).
     *
     * @return so bien lai
     */
    public String soBienLai() {
        return soBienLai(nam, so);
    }

    /**
     * Dinh dang so bien lai.
     *
     * @param nam nam
     * @param so so thu tu trong nam
     * @return vd "PT-2026-00001"
     */
    public static String soBienLai(int nam, int so) {
        return String.format("PT-%d-%05d", nam, so);
    }

    /**
     * Phieu chi gom khoan 0 d: luu va co so, khong in (gia dinh 30).
     *
     * @return true neu tong tien bang 0
     */
    public boolean khongThuTien() {
        return tongTien.signum() == 0;
    }
}
