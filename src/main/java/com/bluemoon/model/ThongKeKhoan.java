package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Thống kê thu của một khoản trong một đợt (US-019, giả định 45). Chỉ tính
 * phiếu thu chưa hủy; khoản chưa có giao dịch thì tổng thu là 0 (AC-053).
 *
 * <p>Khoản bắt buộc: đợt là tháng đã chốt ({@code thang}); có tổng phải thu,
 * số hộ phải đóng (khoản lớn hơn 0 đ, giả định 33) và số hộ đã đóng.
 * Khoản tự nguyện: đợt là khoảng thời gian thu của khoản ({@code thang} là
 * null); có số hộ đã đóng (tổng lớn hơn 0 đ) và số hộ ghi "không đóng"
 * (chỉ có lần 0 đ, giả định 29).
 *
 * @param khoan khoản thu
 * @param thang tháng đã chốt (khoản bắt buộc), null với khoản tự nguyện
 * @param tongThu tổng đã thu
 * @param tongPhaiThu tổng phải thu (khoản bắt buộc), 0 với khoản tự nguyện
 * @param soHoPhaiDong số hộ phải đóng (khoản bắt buộc)
 * @param soHoDaDong số hộ đã đóng
 * @param soHoKhongDong số hộ ghi "không đóng" (khoản tự nguyện)
 */
public record ThongKeKhoan(KhoanPhi khoan, YearMonth thang,
        BigDecimal tongThu, BigDecimal tongPhaiThu, int soHoPhaiDong,
        int soHoDaDong, int soHoKhongDong) {

    /**
     * Khoản bắt buộc hay không.
     *
     * @return true nếu khoản bắt buộc
     */
    public boolean batBuoc() {
        return khoan.loai() == LoaiKhoanPhi.BAT_BUOC;
    }
}
