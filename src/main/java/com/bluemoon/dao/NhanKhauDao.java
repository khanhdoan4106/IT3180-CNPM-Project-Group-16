package com.bluemoon.dao;

import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
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
     * @param nguoiGhi id tai khoan thao tac, ghi vao lich su (US-027)
     * @return khoa chinh vua sinh
     */
    int them(NhanKhau nk, int nguoiGhi);

    /**
     * Them nguoi tam tru cung dot tam tru dau tien trong mot giao dich, de
     * khong co nguoi tam tru nao khong co thoi han.
     *
     * @param nk nhan khau chua co id, da co hoId
     * @param dot dot tam tru dau tien, chua co nhanKhauId
     * @param nguoiGhi id tai khoan thao tac, ghi vao lich su (US-027)
     * @return khoa chinh cua nhan khau vua sinh
     */
    int themKemDot(NhanKhau nk, DotCuTru dot, int nguoiGhi);

    /**
     * Cap nhat thong tin nhan khau (theo id), khong doi ho, va ghi nhat ky
     * cac truong thuc su doi trong cung giao dich (TS-002).
     *
     * @param nk nhan khau da co id
     * @param nguoiId id tai khoan thao tac, ghi vao nhat ky
     */
    void capNhat(NhanKhau nk, int nguoiId);

    /**
     * Doi trang thai cu tru cua mot nhan khau. Chuyen tu dang o sang da
     * chuyen di thi ghi lich su (US-027) trong cung giao dich.
     *
     * @param id khoa chinh
     * @param trangThai trang thai moi
     * @param nguoiGhi id tai khoan thao tac
     */
    void capNhatTrangThai(int id, TrangThaiNhanKhau trangThai, int nguoiGhi);

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

    /**
     * Doi chu ho trong mot giao dich (US-026): chu ho cu doi sang quan he
     * moi (va chuyen di neu can) truoc, roi gan chu ho moi, de rang buoc
     * "moi ho mot chu ho" khong bi vi pham giua chung. Ghi lich su (US-027).
     *
     * @param chuHoCuId chu ho hien tai
     * @param chuHoMoiId thanh vien duoc chon lam chu ho
     * @param quanHeCu quan he moi cua chu ho cu (khong phai CHU_HO)
     * @param cuChuyenDi true neu chu ho cu roi ho (mat, chuyen di, tach ho)
     * @param nguoiGhi id tai khoan thao tac
     */
    void doiChuHo(int chuHoCuId, int chuHoMoiId, QuanHeChuHo quanHeCu,
            boolean cuChuyenDi, int nguoiGhi);
}
