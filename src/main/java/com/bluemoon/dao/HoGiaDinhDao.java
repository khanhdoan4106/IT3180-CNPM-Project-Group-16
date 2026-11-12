package com.bluemoon.dao;

import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.TomTatHo;
import java.util.List;
import java.util.Optional;

/**
 * Truy cap bang ho_gia_dinh.
 */
public interface HoGiaDinhDao {

    /**
     * Them ho gia dinh moi cung chu ho (va dot tam tru dau tien neu chu ho
     * la nguoi tam tru) trong mot giao dich, de ho luon co dung mot chu ho.
     *
     * @param ho ho gia dinh chua co id
     * @param chuHo nhan khau chu ho, chua co id va hoId
     * @param dotTamTru dot tam tru dau tien cua chu ho, null neu thuong tru
     * @param nguoiGhi id tai khoan thao tac, ghi vao lich su (US-027)
     * @return khoa chinh cua ho vua sinh
     */
    int them(HoGiaDinh ho, NhanKhau chuHo, DotCuTru dotTamTru, int nguoiGhi);

    /**
     * Cap nhat thong tin ho (theo id) va ghi nhat ky cac truong thuc su
     * doi, trong mot giao dich (TS-002).
     *
     * @param ho ho gia dinh da co id
     * @param nguoiId id tai khoan thao tac, ghi vao nhat ky
     */
    void capNhat(HoGiaDinh ho, int nguoiId);

    /**
     * Danh dau ho va moi thanh vien dang o cua ho la da chuyen di, trong
     * mot giao dich.
     *
     * @param id khoa chinh ho
     * @param nguoiGhi id tai khoan thao tac, ghi vao lich su (US-027)
     */
    void danhDauChuyenDi(int id, int nguoiGhi);

    /**
     * Tim ho theo khoa chinh.
     *
     * @param id khoa chinh
     * @return ho gia dinh, rong neu khong co
     */
    Optional<HoGiaDinh> timTheoId(int id);

    /**
     * Kiem tra ma can ho da co ho dang o hay chua.
     *
     * @param maCanHo ma can ho da chuan hoa
     * @param boQuaId id ho duoc bo qua khi kiem tra (khi sua), 0 neu them moi
     * @return true neu da co ho khac dang o can ho nay
     */
    boolean tonTaiMaDangO(String maCanHo, int boQuaId);

    /**
     * Tim ho theo ma can ho hoac ten chu ho (RAW-027). Tu khoa rong tra ve
     * tat ca.
     *
     * @param tuKhoa tu khoa tim kiem
     * @param gomDaChuyenDi true de lay ca ho da chuyen di
     * @return danh sach kem ten chu ho va so thanh vien, sap xep theo ma
     *         can ho
     */
    List<TomTatHo> timKiem(String tuKhoa, boolean gomDaChuyenDi);
}
