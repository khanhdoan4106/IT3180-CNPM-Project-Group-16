package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ho gia dinh dang o (hoac da o) mot can ho. Dien tich la co so tinh phi
 * dich vu va phi quan ly (US-005). Lop mo hinh thuan, khong truy cap CSDL.
 */
public class HoGiaDinh {

    private final int id;
    private final String maCanHo;
    private final LoaiCanHo loaiCanHo;
    private final int tang;
    private final BigDecimal dienTich;
    private final String tenChuHo;
    private final String soDienThoai;
    private final LocalDate ngayChuyenDen;
    private final TrangThaiHo trangThai;

    /**
     * Tao ho gia dinh.
     *
     * @param id khoa chinh, 0 neu chua luu
     * @param maCanHo ma can ho, vd A0601
     * @param loaiCanHo loai can ho
     * @param tang so tang (1-30)
     * @param dienTich dien tich m2
     * @param tenChuHo ho ten chu ho
     * @param soDienThoai so dien thoai lien he, co the null
     * @param ngayChuyenDen ngay ho chuyen den
     * @param trangThai trang thai cu tru
     */
    public HoGiaDinh(int id, String maCanHo, LoaiCanHo loaiCanHo, int tang,
            BigDecimal dienTich, String tenChuHo, String soDienThoai,
            LocalDate ngayChuyenDen, TrangThaiHo trangThai) {
        this.id = id;
        this.maCanHo = maCanHo;
        this.loaiCanHo = loaiCanHo;
        this.tang = tang;
        this.dienTich = dienTich;
        this.tenChuHo = tenChuHo;
        this.soDienThoai = soDienThoai;
        this.ngayChuyenDen = ngayChuyenDen;
        this.trangThai = trangThai;
    }

    /**
     * Tao ban sao voi khoa chinh moi (sau khi them vao CSDL).
     *
     * @param idMoi khoa chinh
     * @return ho gia dinh co id
     */
    public HoGiaDinh voiId(int idMoi) {
        return new HoGiaDinh(idMoi, maCanHo, loaiCanHo, tang, dienTich,
                tenChuHo, soDienThoai, ngayChuyenDen, trangThai);
    }

    /**
     * Tao ban sao voi trang thai moi.
     *
     * @param trangThaiMoi trang thai
     * @return ho gia dinh voi trang thai moi
     */
    public HoGiaDinh voiTrangThai(TrangThaiHo trangThaiMoi) {
        return new HoGiaDinh(id, maCanHo, loaiCanHo, tang, dienTich,
                tenChuHo, soDienThoai, ngayChuyenDen, trangThaiMoi);
    }

    public int getId() {
        return id;
    }

    public String getMaCanHo() {
        return maCanHo;
    }

    public LoaiCanHo getLoaiCanHo() {
        return loaiCanHo;
    }

    public int getTang() {
        return tang;
    }

    public BigDecimal getDienTich() {
        return dienTich;
    }

    public String getTenChuHo() {
        return tenChuHo;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public LocalDate getNgayChuyenDen() {
        return ngayChuyenDen;
    }

    public TrangThaiHo getTrangThai() {
        return trangThai;
    }
}
