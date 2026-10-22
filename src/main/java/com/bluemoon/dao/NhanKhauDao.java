package com.bluemoon.dao;

import com.bluemoon.model.NhanKhau;
import java.util.List;
import java.util.Optional;

/**
 * Truy cap bang nhan_khau.
 */
public interface NhanKhauDao {

    /**
     * Them nhan khau vao ho.
     *
     * @param nk nhan khau chua co id, da co hoId
     * @return khoa chinh vua sinh
     */
    int them(NhanKhau nk);

    /**
     * Cap nhat thong tin nhan khau (theo id). Khong doi ho.
     *
     * @param nk nhan khau da co id
     */
    void capNhat(NhanKhau nk);

    /**
     * Tim nhan khau theo khoa chinh.
     *
     * @param id khoa chinh
     * @return nhan khau, rong neu khong co
     */
    Optional<NhanKhau> timTheoId(int id);

    /**
     * Lay cac nhan khau cua mot ho, chu ho dung dau.
     *
     * @param hoId id ho gia dinh
     * @return danh sach nhan khau
     */
    List<NhanKhau> theoHo(int hoId);

    /**
     * Kiem tra so CCCD da thuoc nhan khau khac chua.
     *
     * @param soCccd so CCCD da chuan hoa
     * @param boQuaId id nhan khau duoc bo qua (khi sua), 0 neu them moi
     * @return true neu da co nhan khau khac mang so nay
     */
    boolean tonTaiCccd(String soCccd, int boQuaId);

    /**
     * Kiem tra ho da co chu ho chua.
     *
     * @param hoId id ho gia dinh
     * @param boQuaId id nhan khau duoc bo qua (khi sua), 0 neu them moi
     * @return true neu ho da co nhan khau khac la chu ho
     */
    boolean coChuHo(int hoId, int boQuaId);
}
