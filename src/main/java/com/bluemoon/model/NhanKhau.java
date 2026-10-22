package com.bluemoon.model;

import java.time.LocalDate;

/**
 * Mot nhan khau thuoc mot ho gia dinh (US-002). Chu ho la nhan khau co quan
 * he {@link QuanHeChuHo#CHU_HO}. Lop mo hinh thuan, khong truy cap CSDL.
 */
public class NhanKhau {

    private final int id;
    private final int hoId;
    private final String hoTen;
    private final LocalDate ngaySinh;
    private final GioiTinh gioiTinh;
    private final String soCccd;
    private final QuanHeChuHo quanHe;

    /**
     * Tao nhan khau.
     *
     * @param id khoa chinh, 0 neu chua luu
     * @param hoId id ho gia dinh, 0 neu ho chua luu
     * @param hoTen ho ten
     * @param ngaySinh ngay sinh
     * @param gioiTinh gioi tinh
     * @param soCccd so CCCD 12 chu so, null neu chua co (tre em)
     * @param quanHe quan he voi chu ho
     */
    public NhanKhau(int id, int hoId, String hoTen, LocalDate ngaySinh,
            GioiTinh gioiTinh, String soCccd, QuanHeChuHo quanHe) {
        this.id = id;
        this.hoId = hoId;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.soCccd = soCccd;
        this.quanHe = quanHe;
    }

    /**
     * Tao ban sao voi khoa chinh moi (sau khi them vao CSDL).
     *
     * @param idMoi khoa chinh
     * @return nhan khau co id
     */
    public NhanKhau voiId(int idMoi) {
        return new NhanKhau(idMoi, hoId, hoTen, ngaySinh, gioiTinh, soCccd,
                quanHe);
    }

    /**
     * Tao ban sao gan vao ho (khi them ho cung chu ho).
     *
     * @param hoIdMoi id ho gia dinh
     * @return nhan khau thuoc ho
     */
    public NhanKhau voiHoId(int hoIdMoi) {
        return new NhanKhau(id, hoIdMoi, hoTen, ngaySinh, gioiTinh, soCccd,
                quanHe);
    }

    public int getId() {
        return id;
    }

    public int getHoId() {
        return hoId;
    }

    public String getHoTen() {
        return hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public GioiTinh getGioiTinh() {
        return gioiTinh;
    }

    public String getSoCccd() {
        return soCccd;
    }

    public QuanHeChuHo getQuanHe() {
        return quanHe;
    }
}
