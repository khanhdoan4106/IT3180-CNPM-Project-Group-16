package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.Test;

/** Dinh dang noi dung nhat ky kieu Viet Nam va chi ghi truong thuc su doi. */
class NoiDungNhatKyTest {

    @Test
    void dinhDangKieuVietNam() {
        assertEquals("75,50 m²", NoiDungNhatKy.dienTich(
                new BigDecimal("75.5")));
        assertEquals("1.260.000 đ", NoiDungNhatKy.tien(
                new BigDecimal("1260000.00")));
        assertEquals("08/11/2026", NoiDungNhatKy.ngay(
                LocalDate.of(2026, 11, 8)));
        assertEquals("03/2026", NoiDungNhatKy.thang(YearMonth.of(2026, 3)));
        assertEquals(NoiDungNhatKy.TRONG, NoiDungNhatKy.chu(" "));
    }

    @Test
    void chiGhiTruongThucSuDoi() {
        NoiDungNhatKy.ThayDoi doi = new NoiDungNhatKy.ThayDoi()
                .them("Tầng", "12", "12")
                .them("Diện tích", "75,50 m²", "80,00 m²")
                .them("Điện thoại", NoiDungNhatKy.TRONG, "0912345678");
        assertEquals("Diện tích: 75,50 m² → 80,00 m²; "
                + "Điện thoại: (trống) → 0912345678", doi.toString());
        assertTrue(new NoiDungNhatKy.ThayDoi().them("Tầng", "1", "1")
                .rong());
    }
}
