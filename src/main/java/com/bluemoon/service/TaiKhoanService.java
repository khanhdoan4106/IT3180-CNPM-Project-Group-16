package com.bluemoon.service;

import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.MatKhauUtil;
import java.util.regex.Pattern;

/**
 * Nghiep vu tai khoan: dang ky (US-020), dang nhap (US-021) va doi mat khau
 * (US-023).
 */
public class TaiKhoanService {

    /** Do dai mat khau toi thieu. */
    public static final int MAT_KHAU_TOI_THIEU = 8;
    /** Do dai toi da cua ho ten, theo cot ho_ten. */
    public static final int HO_TEN_TOI_DA = 100;
    /** Chu khong dau, so, dau cham, gach duoi; 3-50 ky tu (cot 50). */
    private static final Pattern TEN_HOP_LE =
            Pattern.compile("[A-Za-z0-9._]{3,50}");

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
     * Dang ky tai khoan moi (AC-054 den AC-056). Chi Quan tri vien dang ky
     * duoc, vi v1.0 chi nguoi duoc Ban quan tri cap moi co tai khoan
     * (RAW-025).
     *
     * @param nguoiTao tai khoan dang dang nhap
     * @param tenDangNhap ten dang nhap moi
     * @param hoTen ho ten nguoi dung
     * @param matKhau mat khau goc
     * @param nhapLai mat khau nhap lai
     * @param vaiTro vai tro cua tai khoan moi
     * @return tai khoan vua tao
     * @throws NghiepVuException neu khong co quyen, thong tin sai hoac trung
     *         ten dang nhap
     */
    public TaiKhoan dangKy(TaiKhoan nguoiTao, String tenDangNhap,
            String hoTen, String matKhau, String nhapLai, VaiTro vaiTro) {
        if (nguoiTao == null || nguoiTao.getVaiTro() != VaiTro.QUAN_TRI) {
            throw new NghiepVuException(
                    "Chi Quan tri vien moi duoc dang ky tai khoan");
        }
        String ten = tenDangNhap == null ? "" : tenDangNhap.trim();
        if (!TEN_HOP_LE.matcher(ten).matches()) {
            throw new NghiepVuException("Ten dang nhap gom 3-50 ky tu: chu"
                    + " khong dau, so, dau cham hoac gach duoi");
        }
        String hoTenSach = hoTen == null ? "" : hoTen.trim();
        if (hoTenSach.isEmpty()) {
            throw new NghiepVuException("Vui long nhap ho ten");
        }
        if (hoTenSach.length() > HO_TEN_TOI_DA) {
            throw new NghiepVuException("Ho ten toi da " + HO_TEN_TOI_DA
                    + " ky tu");
        }
        if (vaiTro == null) {
            throw new NghiepVuException("Vui long chon vai tro");
        }
        kiemTraMatKhauMoi(matKhau, "Mat khau");
        if (!matKhau.equals(nhapLai)) {
            throw new NghiepVuException("Mat khau nhap lai khong khop");
        }
        if (dao.timTheoTenDangNhap(ten).isPresent()) {
            throw trungTen(ten);
        }
        String bam = MatKhauUtil.bam(matKhau);
        try {
            int id = dao.them(
                    new TaiKhoan(0, ten, bam, hoTenSach, vaiTro, true));
            return new TaiKhoan(id, ten, bam, hoTenSach, vaiTro, true);
        } catch (TrungKhoaException e) {
            // ten_dang_nhap la rang buoc UNIQUE duy nhat cua bang; loi nay
            // chi xay ra khi hai may cung dang ky mot ten mot luc.
            throw trungTen(ten);
        }
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
        kiemTraMatKhauMoi(matKhauMoi, "Mat khau moi");
        dao.capNhatMatKhau(tk.getId(), MatKhauUtil.bam(matKhauMoi));
    }

    private static void kiemTraMatKhauMoi(String matKhau, String ten) {
        if (matKhau == null || matKhau.length() < MAT_KHAU_TOI_THIEU) {
            throw new NghiepVuException(ten + " phai co it nhat "
                    + MAT_KHAU_TOI_THIEU + " ky tu");
        }
        if (MatKhauUtil.quaDai(matKhau)) {
            throw new NghiepVuException(ten + " qua dai");
        }
    }

    private static NghiepVuException trungTen(String ten) {
        return new NghiepVuException("Ten dang nhap " + ten
                + " da ton tai");
    }

    private static NghiepVuException saiThongTin() {
        return new NghiepVuException("Sai ten dang nhap hoac mat khau");
    }
}
