package com.bluemoon.model;

import java.time.LocalDate;

/**
 * Mot dot tam tru hoac tam vang cua nhan khau (US-003), co ngay bat dau va
 * ngay ket thuc. Trang thai "dang tam tru/tam vang" tinh theo ngay, khong
 * luu. Lop mo hinh thuan, khong truy cap CSDL.
 */
public class DotCuTru {

    private final int id;
    private final int nhanKhauId;
    private final LoaiDotCuTru loai;
    private final LocalDate tuNgay;
    private final LocalDate denNgay;
    private final String lyDo;

    /**
     * Tao dot cu tru.
     *
     * @param id khoa chinh, 0 neu chua luu
     * @param nhanKhauId id nhan khau, 0 neu nhan khau chua luu
     * @param loai tam tru hoac tam vang
     * @param tuNgay ngay bat dau
     * @param denNgay ngay ket thuc, khong truoc ngay bat dau
     * @param lyDo ly do, co the null
     */
    public DotCuTru(int id, int nhanKhauId, LoaiDotCuTru loai,
            LocalDate tuNgay, LocalDate denNgay, String lyDo) {
        this.id = id;
        this.nhanKhauId = nhanKhauId;
        this.loai = loai;
        this.tuNgay = tuNgay;
        this.denNgay = denNgay;
        this.lyDo = lyDo;
    }

    /**
     * Tao ban sao voi khoa chinh moi (sau khi them vao CSDL).
     *
     * @param idMoi khoa chinh
     * @return dot co id
     */
    public DotCuTru voiId(int idMoi) {
        return new DotCuTru(idMoi, nhanKhauId, loai, tuNgay, denNgay, lyDo);
    }

    /**
     * Tao ban sao gan vao nhan khau (khi them nhan khau cung dot dau tien).
     *
     * @param nhanKhauIdMoi id nhan khau
     * @return dot thuoc nhan khau
     */
    public DotCuTru voiNhanKhauId(int nhanKhauIdMoi) {
        return new DotCuTru(id, nhanKhauIdMoi, loai, tuNgay, denNgay, lyDo);
    }

    /**
     * Dot co chua ngay da cho khong.
     *
     * @param ngay ngay can xet
     * @return true neu tuNgay &lt;= ngay &lt;= denNgay
     */
    public boolean dangHieuLuc(LocalDate ngay) {
        return !ngay.isBefore(tuNgay) && !ngay.isAfter(denNgay);
    }

    /**
     * Hai dot co chung it nhat mot ngay khong.
     *
     * @param khac dot khac
     * @return true neu chong ngay
     */
    public boolean chongNgay(DotCuTru khac) {
        return !tuNgay.isAfter(khac.denNgay) && !khac.tuNgay.isAfter(denNgay);
    }

    public int getId() {
        return id;
    }

    public int getNhanKhauId() {
        return nhanKhauId;
    }

    public LoaiDotCuTru getLoai() {
        return loai;
    }

    public LocalDate getTuNgay() {
        return tuNgay;
    }

    public LocalDate getDenNgay() {
        return denNgay;
    }

    public String getLyDo() {
        return lyDo;
    }
}
