package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Mot khoan trong danh muc khoan thu (bang khoan_phi, US-005, US-006).
 *
 * @param id khoa chinh, 0 khi chua luu
 * @param ten ten khoan thu
 * @param loai bat buoc hoac tu nguyen
 * @param cachTinh cach tinh so tien
 * @param donGia d/m2 hoac d/ho; null khi khong co dinh
 * @param tuNgay ngay bat dau thu (dot)
 * @param denNgay ngay ket thuc; null voi khoan bat buoc thu hang thang
 * @param trangThai dang thu hay da dong
 */
public record KhoanPhi(int id, String ten, LoaiKhoanPhi loai,
        CachTinhPhi cachTinh, BigDecimal donGia, LocalDate tuNgay,
        LocalDate denNgay, TrangThaiKhoanPhi trangThai) {

    /**
     * Ban sao voi khoa chinh moi.
     *
     * @param idMoi khoa chinh vua sinh
     * @return khoan thu da co id
     */
    public KhoanPhi voiId(int idMoi) {
        return new KhoanPhi(idMoi, ten, loai, cachTinh, donGia, tuNgay,
                denNgay, trangThai);
    }

    /**
     * Ban sao voi don gia moi.
     *
     * @param donGiaMoi don gia moi
     * @return khoan thu sau khi doi don gia
     */
    public KhoanPhi voiDonGia(BigDecimal donGiaMoi) {
        return new KhoanPhi(id, ten, loai, cachTinh, donGiaMoi, tuNgay,
                denNgay, trangThai);
    }

    /**
     * Khoan nay co tao cong no hang thang khong: khoan bat buoc dang thu.
     *
     * @return true neu duoc dua vao chot cong no
     */
    public boolean tinhCongNo() {
        return loai == LoaiKhoanPhi.BAT_BUOC
                && trangThai == TrangThaiKhoanPhi.DANG_THU;
    }

    /**
     * Khoan co hieu luc trong thang khong: bat dau khong sau ngay cuoi
     * thang va chua ket thuc truoc ngay dau thang.
     *
     * @param thang thang can xet
     * @return true neu khoan ap dung cho it nhat mot ngay trong thang
     */
    public boolean apDungTrong(YearMonth thang) {
        return !tuNgay.isAfter(thang.atEndOfMonth())
                && (denNgay == null || !denNgay.isBefore(thang.atDay(1)));
    }
}
