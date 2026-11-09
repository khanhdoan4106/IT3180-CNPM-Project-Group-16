package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLTransactionRollbackException;
import org.junit.jupiter.api.Test;

class DangBiKhoaExceptionTest {

    @Test
    void deadlock1213VaHetHanCho1205ThanhLoiRieng() {
        SQLException deadlock = new SQLTransactionRollbackException(
                "Deadlock found when trying to get lock", "40001", 1213);
        SQLException hetHan = new SQLException(
                "Lock wait timeout exceeded", "HY000", 1205);
        assertInstanceOf(DangBiKhoaException.class,
                DangBiKhoaException.boc("Loi", deadlock));
        assertInstanceOf(DangBiKhoaException.class,
                DangBiKhoaException.boc("Loi", hetHan));
    }

    @Test
    void trungKhoaVaLoiKhacKhongThanhLoiKhoa() {
        SQLException trung = new SQLIntegrityConstraintViolationException(
                "Duplicate entry '1' for key 'uq_pt_so_bien_lai'", "23000",
                1062);
        SQLException khac = new SQLException("Loi khac", "HY000", 1452);
        assertInstanceOf(TrungKhoaException.class,
                DangBiKhoaException.boc("Loi", trung));
        assertFalse(DangBiKhoaException.boc("Loi", khac)
                instanceof DangBiKhoaException);
    }
}
