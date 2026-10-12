package com.bluemoon.model;

/**
 * Tai khoan dang nhap. Lop mo hinh thuan, khong chua ma truy cap CSDL.
 */
public class TaiKhoan {

    private final int id;
    private final String tenDangNhap;
    private final String matKhauBam;
    private final String hoTen;
    private final VaiTro vaiTro;
    private final boolean hoatDong;

    /**
     * Tao tai khoan.
     *
     * @param id khoa chinh
     * @param tenDangNhap ten dang nhap
     * @param matKhauBam mat khau da bam BCrypt
     * @param hoTen ho ten
     * @param vaiTro vai tro
     * @param hoatDong false neu tai khoan bi khoa
     */
    public TaiKhoan(int id, String tenDangNhap, String matKhauBam,
            String hoTen, VaiTro vaiTro, boolean hoatDong) {
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.matKhauBam = matKhauBam;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.hoatDong = hoatDong;
    }

    public int getId() {
        return id;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getMatKhauBam() {
        return matKhauBam;
    }

    public String getHoTen() {
        return hoTen;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public boolean isHoatDong() {
        return hoatDong;
    }
}
