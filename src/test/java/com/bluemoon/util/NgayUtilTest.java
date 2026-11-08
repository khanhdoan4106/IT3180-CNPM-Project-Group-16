package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class NgayUtilTest {

    @Test
    void docNgayHopLe() {
        assertEquals(LocalDate.of(2026, 11, 8),
                NgayUtil.docChuoi("08/11/2026"));
        assertEquals(LocalDate.of(2024, 2, 29),
                NgayUtil.docChuoi(" 29/02/2024 "));
        assertEquals(LocalDate.of(2026, 12, 31),
                NgayUtil.docChuoi("31/12/2026"));
    }

    @Test
    void ngayKhongTonTaiBiTuChoiChuKhongDoiNgam() {
        // Loi 3: kieu SMART tung doi 31/02/2026 thanh 28/02/2026.
        assertNull(NgayUtil.docChuoi("31/02/2026"));
        assertNull(NgayUtil.docChuoi("29/02/2026"));
        assertNull(NgayUtil.docChuoi("31/04/2026"));
        assertNull(NgayUtil.docChuoi("00/01/2026"));
        assertNull(NgayUtil.docChuoi("12/13/2026"));
    }

    @Test
    void saiDinhDangHoacTrongTraVeNull() {
        assertNull(NgayUtil.docChuoi("8/11/2026"));
        assertNull(NgayUtil.docChuoi("2026-11-08"));
        assertNull(NgayUtil.docChuoi("abc"));
        assertNull(NgayUtil.docChuoi(""));
        assertNull(NgayUtil.docChuoi(null));
    }

    @Test
    void hienThiRoiDocLaiDuoc() {
        LocalDate ngay = LocalDate.of(2026, 3, 5);
        assertEquals("05/03/2026", NgayUtil.hien(ngay));
        assertEquals(ngay, NgayUtil.docChuoi(NgayUtil.hien(ngay)));
        assertEquals("", NgayUtil.hien(null));
    }
}
