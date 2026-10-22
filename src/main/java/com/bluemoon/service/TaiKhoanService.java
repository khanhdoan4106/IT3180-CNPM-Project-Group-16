package com.bluemoon.service;

import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.util.MatKhauUtil;

/**
 * Nghiep vu tai khoan: dang nhap (US-021) va doi mat khau (US-023).
 */
public class TaiKhoanService {

    /** Do dai mat khau toi thieu. */
    public static final int MAT_KHAU_TOI_THIEU = 8;

    private final TaiKhoanDao dao;

    /**
     * Tao dich vu.
     *
     * @param dao nguon du lieu tai khoan
     */
    public TaiKhoanService(TaiKhoanDao dao) {
        this.dao = dao;
    }

    /**
     * Dang nhap. Thong bao loi chung chung de khong lo ten dang nhap co ton
     * tai hay khong.
     *
     * @param tenDangNhap ten dang nhap
     * @param matKhau mat khau goc
     * @return tai khoan da xac thuc
     * @throws NghiepVuException neu sai thong tin hoac tai khoan bi khoa
     */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        if (tenDangNhap == null || tenDangNhap.isBlank()
                || matKhau == null || matKhau.isEmpty()) {
            throw new NghiepVuException(
                    "Vui long nhap ten dang nhap va mat khau");
        }
        TaiKhoan tk = dao.timTheoTenDangNhap(tenDangNhap.trim())
                .orElseThrow(TaiKhoanService::saiThongTin);
        if (!MatKhauUtil.kiemTra(matKhau, tk.getMatKhauBam())) {
            throw saiThongTin();
        }
        if (!tk.isHoatDong()) {
            throw new NghiepVuException("Tai khoan da bi khoa");
        }
        return tk;
    }

    /**
     * Doi mat khau cua chinh nguoi dung.
     *
     * @param tk tai khoan dang dang nhap
     * @param matKhauCu mat khau hien tai
     * @param matKhauMoi mat khau moi
     * @throws NghiepVuException neu mat khau cu sai hoac mat khau moi yeu
     */
    public void doiMatKhau(TaiKhoan tk, String matKhauCu,
            String matKhauMoi) {
        if (!MatKhauUtil.kiemTra(matKhauCu, tk.getMatKhauBam())) {
            throw new NghiepVuException("Mat khau hien tai khong dung");
        }
        if (matKhauMoi == null
                || matKhauMoi.length() < MAT_KHAU_TOI_THIEU) {
            throw new NghiepVuException("Mat khau moi phai co it nhat "
                    + MAT_KHAU_TOI_THIEU + " ky tu");
        }
        dao.capNhatMatKhau(tk.getId(), MatKhauUtil.bam(matKhauMoi));
    }

    private static NghiepVuException saiThongTin() {
        return new NghiepVuException("Sai ten dang nhap hoac mat khau");
    }
}
