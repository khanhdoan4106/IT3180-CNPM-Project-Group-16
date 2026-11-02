package com.bluemoon.dao;

import com.bluemoon.model.PhaiNop;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Truy cap bang dot_thu va phai_nop (US-009, US-008).
 */
public interface CongNoDao {

    /**
     * Chot cong no mot thang trong mot giao dich: tao dot_thu neu chua co,
     * them cac khoan chua co, bo qua khoan da co (AC-028). Hai may chot cung
     * luc cung khong tao trung nho UNIQUE (dot_thu_id, ho_id, khoan_phi_id).
     *
     * @param thang thang chot
     * @param nguoiChot id tai khoan chot
     * @param khoan cac khoan phai nop da tinh
     * @return so khoan moi duoc them
     */
    int chot(YearMonth thang, int nguoiChot, List<PhaiNop> khoan);

    /**
     * Thang da co dot chot chua.
     *
     * @param thang thang can xem
     * @return true neu da chot it nhat mot lan
     */
    boolean daChot(YearMonth thang);

    /**
     * Cac khoan phai nop cua mot thang, theo ma can ho roi ten khoan.
     *
     * @param thang thang can xem
     * @return danh sach, rong neu chua chot
     */
    List<PhaiNop> theoThang(YearMonth thang);

    /**
     * Tim khoan phai nop theo khoa chinh.
     *
     * @param id khoa chinh
     * @return khoan phai nop, rong neu khong co
     */
    Optional<PhaiNop> timTheoId(int id);

    /**
     * Sua so tien cua mot khoan (US-008), ghi nguoi sua, thoi diem va ghi
     * chu; so tien goc giu nguyen.
     *
     * @param id khoa chinh
     * @param soTien so tien moi
     * @param nguoiSua id tai khoan sua
     * @param ghiChu ly do, co the null
     */
    void suaSoTien(int id, BigDecimal soTien, int nguoiSua, String ghiChu);

    /**
     * Khoan da co phieu thu chua (AC-026). Bang phieu_thu them o Sprint 3;
     * truoc do moi khoan deu chua thu.
     *
     * @param id khoa chinh khoan phai nop
     * @return true neu da co phieu thu con hieu luc
     */
    boolean daCoPhieuThu(int id);

    /**
     * Tong so tien cac khoan phai nop chua dong cua mot ho, moi thang.
     * Sprint 3 tru cac khoan da co phieu thu.
     *
     * @param hoId khoa chinh ho
     * @return tong no, 0 neu khong no
     */
    BigDecimal tongChuaDong(int hoId);
}
