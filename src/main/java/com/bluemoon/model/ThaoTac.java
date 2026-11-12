package com.bluemoon.model;

/**
 * Loai thao tac ghi vao nhat ky (TS-002, gia dinh 44). Ma luu trong cot
 * nhat_ky.thao_tac la ten hang so; danh sach phai khop rang buoc
 * chk_nky_thao_tac trong database/schema.sql (ThaoTacTest kiem tra).
 * Khong ghi dang nhap va viec tu doi mat khau.
 */
public enum ThaoTac {
    LAP_PHIEU_THU("Lập phiếu thu"),
    HUY_PHIEU_THU("Hủy phiếu thu"),
    SUA_SO_TIEN_PHAI_NOP("Sửa số tiền phải nộp"),
    TAO_KHOAN_TU_NGUYEN("Tạo khoản thu tự nguyện"),
    SUA_DON_GIA("Sửa đơn giá"),
    CHOT_CONG_NO("Chốt công nợ"),
    THEM_HO("Thêm hộ"),
    SUA_HO("Sửa hộ"),
    HO_CHUYEN_DI("Hộ chuyển đi"),
    THEM_NHAN_KHAU("Thêm nhân khẩu"),
    SUA_NHAN_KHAU("Sửa nhân khẩu"),
    NHAN_KHAU_CHUYEN_DI("Nhân khẩu chuyển đi"),
    DOI_CHU_HO("Đổi chủ hộ"),
    GHI_TAM_TRU_TAM_VANG("Ghi tạm trú/tạm vắng"),
    TAO_TAI_KHOAN("Tạo tài khoản"),
    DOI_VAI_TRO("Đổi vai trò"),
    KHOA_TAI_KHOAN("Khóa tài khoản"),
    MO_KHOA_TAI_KHOAN("Mở khóa tài khoản"),
    DAT_LAI_MAT_KHAU("Đặt lại mật khẩu");

    private final String nhan;

    ThaoTac(String nhan) {
        this.nhan = nhan;
    }

    /**
     * Ma luu trong CSDL.
     *
     * @return ma thao tac
     */
    public String getMa() {
        return name();
    }

    /**
     * Ten hien thi tren giao dien.
     *
     * @return nhan hien thi
     */
    public String getNhan() {
        return nhan;
    }

    @Override
    public String toString() {
        return nhan;
    }
}
