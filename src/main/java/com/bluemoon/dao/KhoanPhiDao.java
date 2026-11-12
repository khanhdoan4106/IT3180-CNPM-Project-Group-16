package com.bluemoon.dao;

import com.bluemoon.model.KhoanPhi;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Truy cap bang khoan_phi (US-005, US-006).
 */
public interface KhoanPhiDao {

    /** Rang buoc UNIQUE (ten, tu_ngay): trung ten trong cung dot. */
    String TRUNG_TEN_DOT = "uq_kp_ten_dot";

    /**
     * Them khoan thu moi va ghi nhat ky trong cung giao dich (TS-002).
     *
     * @param khoan khoan thu chua co id
     * @param nguoiId id tai khoan thao tac, ghi vao nhat ky
     * @return khoa chinh vua sinh
     */
    int them(KhoanPhi khoan, int nguoiId);

    /**
     * Doi don gia cua mot khoan thu. Cac thang da chot giu don gia cu trong
     * phai_nop. Ghi nhat ky don gia cu va moi trong cung giao dich.
     *
     * @param id khoa chinh
     * @param donGia don gia moi
     * @param nguoiId id tai khoan thao tac, ghi vao nhat ky
     */
    void capNhatDonGia(int id, BigDecimal donGia, int nguoiId);

    /**
     * Tim khoan thu theo khoa chinh.
     *
     * @param id khoa chinh
     * @return khoan thu, rong neu khong co
     */
    Optional<KhoanPhi> timTheoId(int id);

    /**
     * Toan bo danh muc, khoan bat buoc truoc, roi theo ngay bat dau moi nhat.
     *
     * @return danh sach khoan thu
     */
    List<KhoanPhi> danhSach();

    /**
     * Kiem tra ten da dung trong cung dot chua (AC-020).
     *
     * @param ten ten khoan thu da chuan hoa
     * @param tuNgay ngay bat dau cua dot
     * @return true neu da co khoan cung ten, cung ngay bat dau
     */
    boolean tonTaiTen(String ten, LocalDate tuNgay);
}
