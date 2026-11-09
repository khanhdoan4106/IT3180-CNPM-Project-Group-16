package com.bluemoon.dao;

/**
 * Du lieu da thay doi giua luc Service kiem tra va luc ghi (vd may khac vua
 * thu cung khoan). DAO phat hien khi kiem tra lai duoi khoa dong; Service
 * doi thanh thong bao nghiep vu theo {@link #getLoai()}.
 */
public class XungDotException extends DaoException {

    private static final long serialVersionUID = 1L;

    /** Loai xung dot. */
    public enum Loai {
        /** Khong con ban ghi (ho, phieu, khoan phai nop). */
        KHONG_TIM_THAY,
        /** Khoan phai nop da co phieu thu con hieu luc. */
        DA_THU,
        /** So tien khoan phai nop vua bi sua. */
        SO_TIEN_DOI,
        /** Ghi 0 d cho quy ma ho da co lan dong (gia dinh 29). */
        DA_CO_DONG_GOP,
        /** Da dung het 99.999 so bien lai cua nam. */
        HET_SO,
        /** Phieu da bi huy. */
        DA_HUY,
        /** Nhan khau vua doi trang thai hoac quan he o may khac. */
        NHAN_KHAU_DOI
    }

    private final Loai loai;

    /**
     * Tao loi xung dot.
     *
     * @param loai loai xung dot
     * @param chiTiet mo ta cho nhat ky
     */
    public XungDotException(Loai loai, String chiTiet) {
        super(loai + ": " + chiTiet, null);
        this.loai = loai;
    }

    /**
     * Loai xung dot.
     *
     * @return loai
     */
    public Loai getLoai() {
        return loai;
    }
}
