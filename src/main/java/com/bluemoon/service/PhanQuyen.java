package com.bluemoon.service;

import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import java.util.Arrays;

/**
 * Kiem tra quyen o tang Service (lop thu hai sau viec an menu o Controller,
 * NFR-002, AC-062). Bang quyen: docs/design/architecture.md.
 */
public final class PhanQuyen {

    /** Thong bao khi chua dang nhap. */
    public static final String LOI_CHUA_DANG_NHAP =
            "Phiên làm việc đã hết, vui lòng đăng nhập lại";
    /** Thong bao khi vai tro khong duoc dung chuc nang. */
    public static final String LOI_KHONG_CO_QUYEN =
            "Tài khoản của bạn không có quyền dùng chức năng này";

    private PhanQuyen() {
    }

    /**
     * Yeu cau nguoi dung da dang nhap, tai khoan con hoat dong va co mot
     * trong cac vai tro cho phep.
     *
     * @param nguoi tai khoan dang dang nhap, co the null
     * @param choPhep cac vai tro duoc dung chuc nang
     * @return chinh tai khoan do, de dung tiep (vd ghi nguoi sua)
     * @throws NghiepVuException neu chua dang nhap hoac khong co quyen
     */
    public static TaiKhoan yeuCau(TaiKhoan nguoi, VaiTro... choPhep) {
        if (nguoi == null || !nguoi.isHoatDong()) {
            throw new NghiepVuException(LOI_CHUA_DANG_NHAP);
        }
        if (!Arrays.asList(choPhep).contains(nguoi.getVaiTro())) {
            throw new NghiepVuException(LOI_KHONG_CO_QUYEN);
        }
        return nguoi;
    }
}
