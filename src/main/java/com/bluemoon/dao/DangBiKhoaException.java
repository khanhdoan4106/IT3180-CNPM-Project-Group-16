package com.bluemoon.dao;

import java.sql.SQLException;

/**
 * Giao dich bi MySQL huy vi nguoi khac dang thao tac tren cung du lieu:
 * deadlock (ma 1213) hoac cho khoa qua han (ma 1205). Giao dich da hoan tac
 * nen khong sai du lieu; Service doi thanh {@link #THONG_BAO}.
 */
public class DangBiKhoaException extends DaoException {

    /** Thong bao cho nguoi dung. */
    public static final String THONG_BAO = "Có người khác đang thao tác trên"
            + " hộ này, vui lòng thử lại";

    private static final long serialVersionUID = 1L;
    private static final int MA_DEADLOCK = 1213;
    private static final int MA_HET_HAN_CHO = 1205;

    /**
     * Tao loi.
     *
     * @param thongBao noi dung cho nhat ky
     * @param cause loi MySQL goc
     */
    public DangBiKhoaException(String thongBao, Throwable cause) {
        super(thongBao, cause);
    }

    /**
     * Doi loi MySQL thanh loi rieng: 1213 va 1205 thanh
     * {@link DangBiKhoaException}, loi trung khoa (1062) thanh
     * {@link TrungKhoaException}, con lai la {@link DaoException}.
     *
     * @param thongBao noi dung mac dinh
     * @param e loi tu JDBC
     * @return loi DAO tuong ung
     */
    static DaoException boc(String thongBao, SQLException e) {
        if (e.getErrorCode() == MA_DEADLOCK
                || e.getErrorCode() == MA_HET_HAN_CHO) {
            return new DangBiKhoaException(thongBao + " (dang bi khoa)", e);
        }
        return TrungKhoaException.boc(thongBao, e);
    }
}
