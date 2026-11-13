package com.bluemoon.dao;

import com.bluemoon.model.BaoCaoThu;
import java.time.YearMonth;

/** Dữ liệu báo cáo thu tiền theo tháng (US-018, giả định 41). */
public interface BaoCaoThuDao {

    /**
     * Các phiếu thu chưa hủy lập trong tháng (lọc theo khoảng thời điểm, từ
     * 00:00:00 ngày đầu tháng đến trước 00:00:00 ngày đầu tháng sau), theo
     * thời điểm; các cột là khoản/quỹ có trong các phiếu đó.
     *
     * @param thang tháng báo cáo
     * @return báo cáo, không có dòng nào nếu tháng chưa có phiếu
     */
    BaoCaoThu theoThang(YearMonth thang);
}
