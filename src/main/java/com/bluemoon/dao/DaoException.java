package com.bluemoon.dao;

/**
 * Loi khi truy cap du lieu.
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Tao loi DAO.
     *
     * @param message noi dung loi
     * @param cause nguyen nhan goc
     */
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
