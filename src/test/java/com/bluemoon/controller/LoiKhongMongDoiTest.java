package com.bluemoon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.service.NghiepVuException;
import org.junit.jupiter.api.Test;

class LoiKhongMongDoiTest {

    @Test
    void loiNghiepVuGiuNoiDung() {
        assertEquals("Không tìm thấy hộ",
                LoiKhongMongDoi.thongBao(new NghiepVuException(
                        "Không tìm thấy hộ")));
    }

    @Test
    void loiCsdlDiQuaLoiCsdl() {
        assertEquals(LoiCsdl.LOI_DU_LIEU, LoiKhongMongDoi.thongBao(
                new DaoException("x", new RuntimeException())));
        assertEquals(LoiCsdl.SAI_CAU_HINH, LoiKhongMongDoi.thongBao(
                new CauHinhException("x", null)));
    }

    @Test
    void loiLaCoThongBaoChungKhongLoChiTietKyThuat() {
        assertEquals(LoiKhongMongDoi.LOI_CHUNG, LoiKhongMongDoi.thongBao(
                new IllegalStateException("Location is not set")));
    }
}
