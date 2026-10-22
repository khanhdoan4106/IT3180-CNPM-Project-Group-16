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
    private final TrangThaiNhanKhau trangThai;
    private final LoaiCuTru loaiCuTru;
    private final String noiThuongTru;

    /**
     * Tao nhan khau.
     *
     * @param id khoa chinh, 0 neu chua luu
     * @param hoId id ho gia dinh, 0 neu ho chua luu
     * @param hoTen ho ten
     * @param ngaySinh ngay sinh
     * @param gioiTinh gioi tinh
     * @param soCccd so CCCD 12 chu so, null neu chua co (duoi 14 tuoi)
     * @param quanHe quan he voi chu ho
     * @param trangThai trang thai cu tru
     * @param loaiCuTru loai dang ky cu tru (US-003)
     * @param noiThuongTru noi thuong tru, null voi nguoi thuong tru
     */
    public NhanKhau(int id, int hoId, String hoTen, LocalDate ngaySinh,
            GioiTinh gioiTinh, String soCccd, QuanHeChuHo quanHe,
            TrangThaiNhanKhau trangThai, LoaiCuTru loaiCuTru,
            String noiThuongTru) {
        this.id = id;
        this.hoId = hoId;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.soCccd = soCccd;
        this.quanHe = quanHe;
        this.trangThai = trangThai;
        this.loaiCuTru = loaiCuTru;
        this.noiThuongTru = noiThuongTru;
    }

    /**
     * Tao ban sao voi khoa chinh moi (sau khi them vao CSDL).
     *
     * @param idMoi khoa chinh
     * @return nhan khau co id
     */
    public NhanKhau voiId(int idMoi) {
        return new NhanKhau(idMoi, hoId, hoTen, ngaySinh, gioiTinh, soCccd,
                quanHe, trangThai, loaiCuTru, noiThuongTru);
    }

    /**
     * Tao ban sao gan vao ho (khi them ho cung chu ho).
     *
     * @param hoIdMoi id ho gia dinh
     * @return nhan khau thuoc ho
     */
    public NhanKhau voiHoId(int hoIdMoi) {
        return new NhanKhau(id, hoIdMoi, hoTen, ngaySinh, gioiTinh, soCccd,
                quanHe, trangThai, loaiCuTru, noiThuongTru);
    }

    /**
     * Tao ban sao voi trang thai moi.
     *
     * @param trangThaiMoi trang thai
     * @return nhan khau voi trang thai moi
     */
    public NhanKhau voiTrangThai(TrangThaiNhanKhau trangThaiMoi) {
        return new NhanKhau(id, hoId, hoTen, ngaySinh, gioiTinh, soCccd,
                quanHe, trangThaiMoi, loaiCuTru, noiThuongTru);
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

    public TrangThaiNhanKhau getTrangThai() {
        return trangThai;
    }

    public LoaiCuTru getLoaiCuTru() {
        return loaiCuTru;
    }

    public String getNoiThuongTru() {
        return noiThuongTru;
    }
}
