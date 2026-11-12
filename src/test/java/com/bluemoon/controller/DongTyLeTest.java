package com.bluemoon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bluemoon.service.ThongKeService.TongQuan;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Dòng phụ của thẻ "Đã thu tháng này" ở Trang chính (US-016). */
class DongTyLeTest {

    private static final YearMonth T11 = YearMonth.of(2026, 11);

    private static TongQuan tq(boolean daChot, Integer phanTram) {
        return new TongQuan(T11, BigDecimal.ZERO, daChot, phanTram,
                List.of());
    }

    @Test
    void daChotHienPhanTram() {
        assertEquals("72% tổng phải thu",
                TrangChinhController.dongTyLe(tq(true, 72)));
    }

    @Test
    void chuaChotBaoRo() {
        assertEquals("Tháng 11 chưa chốt công nợ",
                TrangChinhController.dongTyLe(tq(false, null)));
    }

    @Test
    void daChotKhongCoKhoanPhaiThu() {
        assertEquals("Tháng 11 không có khoản phải thu",
                TrangChinhController.dongTyLe(tq(true, null)));
    }
}
