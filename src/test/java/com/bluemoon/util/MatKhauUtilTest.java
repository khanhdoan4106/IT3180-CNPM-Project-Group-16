package com.bluemoon.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MatKhauUtilTest {

    @Test
    void bamRoiKiemTraDung() {
        String bam = MatKhauUtil.bam("matkhau123");
        assertTrue(MatKhauUtil.kiemTra("matkhau123", bam));
    }

    @Test
    void saiMatKhauKhongKhop() {
        String bam = MatKhauUtil.bam("matkhau123");
        assertFalse(MatKhauUtil.kiemTra("khac", bam));
    }

    @Test
    void khongLuuMatKhauGoc() {
        assertNotEquals("matkhau123", MatKhauUtil.bam("matkhau123"));
    }

    @Test
    void matKhauQuaGioiHanBCryptKhongNemLoi() {
        String bam = MatKhauUtil.bam("matkhau123");
        assertFalse(MatKhauUtil.kiemTra("a".repeat(100), bam));
    }

    @Test
    void gioiHanTinhTheoByteUtf8() {
        assertFalse(MatKhauUtil.quaDai("a".repeat(71)));
        assertTrue(MatKhauUtil.quaDai("a".repeat(72)));
        // "a" + chu co dau (2 byte) x 36 = 73 byte, du chi 37 ky tu
        assertTrue(MatKhauUtil.quaDai("a" + "\u00e2".repeat(36)));
    }
}
