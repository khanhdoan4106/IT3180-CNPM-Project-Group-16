package com.bluemoon.service;

import com.bluemoon.dao.BienDongDao;
import com.bluemoon.model.BienDong;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import java.util.List;

/**
 * Xem lich su bien dong nhan khau (US-027). Lich su chi them, khong sua,
 * khong xoa; viec ghi nam trong giao dich cua thao tac tren ho, nhan khau.
 */
public class BienDongService {

    /** So dong toi da hien o Trang chinh. */
    public static final int GAN_DAY = 5;

    private final BienDongDao dao;

    /**
     * Tao dich vu.
     *
     * @param dao nguon du lieu lich su
     */
    public BienDongService(BienDongDao dao) {
        this.dao = dao;
    }

    /**
     * Lich su cua mot ho, moi nhat truoc.
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien (AC-060)
     * @param hoId khoa chinh ho
     * @return danh sach bien dong
     */
    public List<BienDong> theoHo(TaiKhoan nguoi, int hoId) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        return dao.theoHo(hoId);
    }

    /**
     * Cac bien dong moi nhat cua toan chung cu.
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien (AC-060)
     * @return toi da {@link #GAN_DAY} dong
     */
    public List<BienDong> ganDay(TaiKhoan nguoi) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        return dao.ganDay(GAN_DAY);
    }
}
