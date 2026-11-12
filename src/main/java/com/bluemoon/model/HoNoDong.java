package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Một hộ còn nợ (US-017, AC-049, giả định 25). Khoản nợ là khoản phải nộp
 * lớn hơn 0 đ chưa nằm trong phiếu thu chưa hủy nào; số tháng nợ là số
 * tháng chốt còn ít nhất một khoản nợ của hộ.
 *
 * @param hoId id hộ
 * @param maCanHo mã căn hộ
 * @param chuHo họ tên chủ hộ cuối cùng của hộ (với hộ đã chuyển đi là người
 *        làm chủ hộ lúc chuyển đi); null nếu hộ không còn ai là chủ hộ
 * @param trangThai trạng thái hộ (hộ đã chuyển đi vẫn tính, giả định 21)
 * @param soThangNo số tháng chốt còn khoản nợ
 * @param noTuThang tháng nợ sớm nhất
 * @param tongNo tổng tiền các khoản nợ
 */
public record HoNoDong(int hoId, String maCanHo, String chuHo,
        TrangThaiHo trangThai, int soThangNo, YearMonth noTuThang,
        BigDecimal tongNo) {

    /**
     * Hộ đã chuyển đi hay chưa.
     *
     * @return true nếu hộ đã chuyển đi
     */
    public boolean daChuyenDi() {
        return trangThai == TrangThaiHo.DA_CHUYEN_DI;
    }
}
