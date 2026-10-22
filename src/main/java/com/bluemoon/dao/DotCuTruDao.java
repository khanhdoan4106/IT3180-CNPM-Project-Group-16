package com.bluemoon.dao;

import com.bluemoon.model.DotCuTru;
import java.util.List;

/**
 * Truy cap bang tam_tru_tam_vang.
 */
public interface DotCuTruDao {

    /** Ten rang buoc bao khi dot moi chong ngay dot da co cua cung nguoi. */
    String CHONG_NGAY = "chong_ngay_tam_tru_tam_vang";

    /**
     * Them mot dot. Trong cung giao dich, khoa dong nhan khau roi kiem tra
     * lai chong ngay, de hai may cung luu khong lot.
     *
     * @param dot dot chua co id, da co nhanKhauId
     * @return khoa chinh vua sinh
     * @throws TrungKhoaException voi ten {@link #CHONG_NGAY} neu chong ngay
     */
    int them(DotCuTru dot);

    /**
     * Lay cac dot cua mot nhan khau, moi nhat truoc.
     *
     * @param nhanKhauId id nhan khau
     * @return danh sach dot
     */
    List<DotCuTru> theoNhanKhau(int nhanKhauId);

    /**
     * Lay cac dot cua moi nhan khau trong mot ho.
     *
     * @param hoId id ho gia dinh
     * @return danh sach dot
     */
    List<DotCuTru> theoHo(int hoId);
}
