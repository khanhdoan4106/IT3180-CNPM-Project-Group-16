package com.bluemoon.dao;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CSDL tu choi vi trung khoa UNIQUE. Xay ra khi hai may cung luu mot gia tri
 * (vd cung so CCCD) sau khi ca hai da kiem tra la chua trung. Service doi
 * loi nay thanh thong bao nghiep vu theo ten rang buoc.
 */
public class TrungKhoaException extends DaoException {

    private static final long serialVersionUID = 1L;
    /** Ma loi MySQL ER_DUP_ENTRY. */
    private static final int MA_TRUNG = 1062;
    /** "... for key 'nhan_khau.uq_nk_cccd_dang_o'" (MySQL 8). */
    private static final Pattern TEN_KHOA =
            Pattern.compile("for key '(?:\\w+\\.)?(\\w+)'");

    private final String tenRangBuoc;

    /**
     * Tao loi trung khoa.
     *
     * @param tenRangBuoc ten rang buoc UNIQUE, vd uq_nk_cccd_dang_o
     * @param cause loi goc tu JDBC, co the null
     */
    public TrungKhoaException(String tenRangBuoc, Throwable cause) {
        super("Trung khoa " + tenRangBuoc, cause);
        this.tenRangBuoc = tenRangBuoc;
    }

    /**
     * Ten rang buoc UNIQUE bi vi pham.
     *
     * @return ten rang buoc, rong neu khong doc duoc
     */
    public String getTenRangBuoc() {
        return tenRangBuoc;
    }

    /**
     * Boc loi JDBC: trung khoa UNIQUE thanh {@link TrungKhoaException}, loi
     * khac thanh {@link DaoException}.
     *
     * @param thongBao noi dung loi chung
     * @param e loi goc
     * @return loi da boc, de {@code throw}
     */
    static DaoException boc(String thongBao, SQLException e) {
        if (e.getErrorCode() == MA_TRUNG) {
            Matcher m = TEN_KHOA.matcher(String.valueOf(e.getMessage()));
            return new TrungKhoaException(m.find() ? m.group(1) : "", e);
        }
        return new DaoException(thongBao, e);
    }
}
