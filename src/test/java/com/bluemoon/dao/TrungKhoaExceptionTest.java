package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Test;

class TrungKhoaExceptionTest {

    @Test
    void trungKhoaDocDungTenRangBuoc() {
        SQLException e = new SQLIntegrityConstraintViolationException(
                "Duplicate entry '001182004567' for key "
                        + "'nhan_khau.uq_nk_cccd_dang_o'", "23000", 1062);
        DaoException boc = TrungKhoaException.boc("Loi them", e);
        assertInstanceOf(TrungKhoaException.class, boc);
        assertEquals("uq_nk_cccd_dang_o",
                ((TrungKhoaException) boc).getTenRangBuoc());
    }

    @Test
    void loiKhacGiuLaDaoException() {
        SQLException e = new SQLIntegrityConstraintViolationException(
                "Cannot add or update a child row", "23000", 1452);
        assertFalse(TrungKhoaException.boc("Loi them", e)
                instanceof TrungKhoaException);
    }
}
