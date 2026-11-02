package com.bluemoon.dao;

import com.bluemoon.model.BienDong;
import java.util.List;

/**
 * Doc lich su bien dong nhan khau (US-027). Viec ghi nam trong giao dich
 * cua cac DAO ho gia dinh, nhan khau, dot tam tru/tam vang.
 */
public interface BienDongDao {

    /**
     * Lich su cua mot ho, moi nhat truoc.
     *
     * @param hoId khoa chinh ho
     * @return danh sach bien dong
     */
    List<BienDong> theoHo(int hoId);

    /**
     * Cac bien dong gan day nhat cua toan chung cu (Trang chinh).
     *
     * @param soDong so dong toi da
     * @return danh sach bien dong, moi nhat truoc
     */
    List<BienDong> ganDay(int soDong);
}
