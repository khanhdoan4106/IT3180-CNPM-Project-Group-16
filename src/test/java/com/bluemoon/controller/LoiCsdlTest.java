package com.bluemoon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.TrungKhoaException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class LoiCsdlTest {

    @Test
    void khongMoDuocKetNoiLaMatKetNoi() {
        assertEquals(LoiCsdl.MAT_KET_NOI, LoiCsdl.thongBao(
                new CauHinhException("Không kết nối được MySQL",
                        new SQLException("Access denied", "28000"))));
    }

    @Test
    void thieuCauHinhKhongPhaiMatKetNoi() {
        assertEquals(LoiCsdl.SAI_CAU_HINH, LoiCsdl.thongBao(
                new CauHinhException("Thiếu cấu hình CSDL")));
    }

    @Test
    void matKetNoiGiuaChungLaMatKetNoi() {
        // Connector/J bao mat ket noi bang SQLState 08S01.
        SQLException matMang = new SQLException("Communications link",
                "08S01");
        assertEquals(LoiCsdl.MAT_KET_NOI, LoiCsdl.thongBao(
                new DaoException("Loi tim ho", matMang)));
    }

    @Test
    void cauLenhBiTuChoiLaLoiDuLieu() {
        // Vd so vuot khoang (22003) hay thieu cot khi CSDL con ban cu
        // (42S22): co ket noi, khong phai "khong ket noi duoc".
        assertEquals(LoiCsdl.LOI_DU_LIEU, LoiCsdl.thongBao(new DaoException(
                "Loi", new SQLException("Out of range", "22003"))));
        assertEquals(LoiCsdl.LOI_DU_LIEU, LoiCsdl.thongBao(new DaoException(
                "Loi", new SQLException("Unknown column", "42S22"))));
        assertEquals(LoiCsdl.LOI_DU_LIEU, LoiCsdl.thongBao(
                new TrungKhoaException("uq_khac", null)));
    }
}
