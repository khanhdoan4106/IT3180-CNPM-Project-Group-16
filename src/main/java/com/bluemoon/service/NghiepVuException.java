package com.bluemoon.service;

/**
 * Loi nghiep vu co thong bao hien duoc cho nguoi dung.
 */
public class NghiepVuException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Tao loi nghiep vu.
     *
     * @param message thong bao cho nguoi dung
     */
    public NghiepVuException(String message) {
        super(message);
    }
}
