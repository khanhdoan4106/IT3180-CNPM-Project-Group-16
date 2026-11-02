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
    private final String soDienThoai;

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
        this(id, tenDangNhap, matKhauBam, hoTen, vaiTro, hoatDong, null);
    }

    /**
     * Tao tai khoan kem so dien thoai lien he.
     *
     * @param id khoa chinh
     * @param tenDangNhap ten dang nhap
     * @param matKhauBam mat khau da bam BCrypt
     * @param hoTen ho ten
     * @param vaiTro vai tro
     * @param hoatDong false neu tai khoan bi khoa
     * @param soDienThoai so dien thoai, co the null
     */
    public TaiKhoan(int id, String tenDangNhap, String matKhauBam,
            String hoTen, VaiTro vaiTro, boolean hoatDong,
            String soDienThoai) {
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.matKhauBam = matKhauBam;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.hoatDong = hoatDong;
        this.soDienThoai = soDienThoai;
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

    /**
     * So dien thoai lien he (US-023).
     *
     * @return so dien thoai, null neu chua co
     */
    public String getSoDienThoai() {
        return soDienThoai;
    }

    /**
     * Ban sao voi thong tin ca nhan moi.
     *
     * @param hoTenMoi ho ten
     * @param sdtMoi so dien thoai, co the null
     * @return tai khoan sau khi doi
     */
    public TaiKhoan voiThongTin(String hoTenMoi, String sdtMoi) {
        return new TaiKhoan(id, tenDangNhap, matKhauBam, hoTenMoi, vaiTro,
                hoatDong, sdtMoi);
    }

    /**
     * Ban sao voi vai tro moi.
     *
     * @param vaiTroMoi vai tro
     * @return tai khoan sau khi doi
     */
    public TaiKhoan voiVaiTro(VaiTro vaiTroMoi) {
        return new TaiKhoan(id, tenDangNhap, matKhauBam, hoTen, vaiTroMoi,
                hoatDong, soDienThoai);
    }

    /**
     * Ban sao voi trang thai khoa/mo moi.
     *
     * @param hoatDongMoi false de khoa
     * @return tai khoan sau khi doi
     */
    public TaiKhoan voiHoatDong(boolean hoatDongMoi) {
        return new TaiKhoan(id, tenDangNhap, matKhauBam, hoTen, vaiTro,
                hoatDongMoi, soDienThoai);
    }
}
