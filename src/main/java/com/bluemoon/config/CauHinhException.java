package com.bluemoon.config;

/**
 * Loi khi doc cau hinh hoac ket noi co so du lieu.
 */
public class CauHinhException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Tao loi cau hinh.
     *
     * @param message noi dung loi
     */
    public CauHinhException(String message) {
        super(message);
    }

    /**
     * Tao loi cau hinh kem nguyen nhan.
     *
     * @param message noi dung loi
     * @param cause nguyen nhan goc
     */
    public CauHinhException(String message, Throwable cause) {
        super(message, cause);
    }
}
