package com.bluemoon.dao;

import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.TrangThaiNhanKhau;
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
     * Them nguoi tam tru cung dot tam tru dau tien trong mot giao dich, de
     * khong co nguoi tam tru nao khong co thoi han.
     *
     * @param nk nhan khau chua co id, da co hoId
     * @param dot dot tam tru dau tien, chua co nhanKhauId
     * @return khoa chinh cua nhan khau vua sinh
     */
    int themKemDot(NhanKhau nk, DotCuTru dot);

    /**
     * Cap nhat thong tin nhan khau (theo id). Khong doi ho.
     *
     * @param nk nhan khau da co id
     */
    void capNhat(NhanKhau nk);

    /**
     * Doi trang thai cu tru cua mot nhan khau.
     *
     * @param id khoa chinh
     * @param trangThai trang thai moi
     */
    void capNhatTrangThai(int id, TrangThaiNhanKhau trangThai);

    /**
     * Tim nhan khau theo khoa chinh.
     *
     * @param id khoa chinh
     * @return nhan khau, rong neu khong co
     */
    Optional<NhanKhau> timTheoId(int id);

    /**
     * Lay moi nhan khau cua mot ho (ca nguoi da chuyen di): nguoi dang o
     * truoc, chu ho dung dau.
     *
     * @param hoId id ho gia dinh
     * @return danh sach nhan khau
     */
    List<NhanKhau> theoHo(int hoId);

    /**
     * Kiem tra so CCCD da thuoc mot nhan khau dang o khac chua. Nguoi da
     * chuyen di khong tinh, de khai bao lai duoc o ho moi.
     *
     * @param soCccd so CCCD da chuan hoa
     * @param boQuaId id nhan khau duoc bo qua (khi sua), 0 neu them moi
     * @return true neu da co nhan khau dang o khac mang so nay
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
