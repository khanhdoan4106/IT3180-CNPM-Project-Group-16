package com.bluemoon.dao;

import com.bluemoon.model.HoNoDong;
import java.util.List;

/**
 * Hộ nợ đọng (US-017, giả định 25). Khoản nợ là khoản phải nộp lớn hơn 0 đ
 * (khoản sửa về 0 đ không phải nợ, giả định 33) chưa nằm trong phiếu thu
 * chưa hủy nào; phiếu đã hủy thì khoản trở lại nợ. Khoản tự nguyện không
 * có khoản phải nộp nên không tạo nợ. Tính cả hộ đã chuyển đi (giả định 21).
 */
public interface NoDongDao {

    /**
     * Các hộ có từ {@code soThangToiThieu} tháng nợ trở lên, trong một truy
     * vấn GROUP BY; xếp số tháng nợ giảm dần, rồi tổng nợ giảm dần, rồi mã
     * căn hộ.
     *
     * @param soThangToiThieu số tháng nợ tối thiểu, từ 1
     * @return danh sách hộ
     */
    List<HoNoDong> hoNo(int soThangToiThieu);

    /**
     * Đếm hộ còn nợ cho Trang chính.
     *
     * @param nguong số tháng nợ để tính là nợ đọng
     * @return số hộ còn nợ và số hộ nợ từ {@code nguong} tháng trở lên
     */
    TomTatNo tomTat(int nguong);

    /**
     * Tóm tắt nợ.
     *
     * @param soHoConNo số hộ còn ít nhất một tháng nợ
     * @param soHoNoDong số hộ nợ từ ngưỡng trở lên
     */
    record TomTatNo(int soHoConNo, int soHoNoDong) {
    }
}
