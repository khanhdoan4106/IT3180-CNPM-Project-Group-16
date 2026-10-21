package com.bluemoon.dao;

import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.TrangThaiHo;
import java.util.List;
import java.util.Optional;

/**
 * Truy cap bang ho_gia_dinh.
 */
public interface HoGiaDinhDao {

    /**
     * Them ho gia dinh moi.
     *
     * @param ho ho gia dinh chua co id
     * @return khoa chinh vua sinh
     */
    int them(HoGiaDinh ho);

    /**
     * Cap nhat thong tin ho (theo id).
     *
     * @param ho ho gia dinh da co id
     */
    void capNhat(HoGiaDinh ho);

    /**
     * Doi trang thai cu tru cua ho.
     *
     * @param id khoa chinh
     * @param trangThai trang thai moi
     */
    void capNhatTrangThai(int id, TrangThaiHo trangThai);

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
     * @return danh sach sap xep theo ma can ho
     */
    List<HoGiaDinh> timKiem(String tuKhoa, boolean gomDaChuyenDi);
}
