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
}
