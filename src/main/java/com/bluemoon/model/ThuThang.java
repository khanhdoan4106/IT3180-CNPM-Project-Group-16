package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Tổng tiền đã thu trong một tháng (US-016, giả định 24): tổng tiền các
 * phiếu thu chưa hủy lập trong tháng đó.
 *
 * @param thang tháng
 * @param tong tổng tiền, 0 nếu tháng không có phiếu
 */
public record ThuThang(YearMonth thang, BigDecimal tong) {
}
