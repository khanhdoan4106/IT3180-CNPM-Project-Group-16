package com.bluemoon.service;

import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.MatKhauUtil;
import java.util.List;
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
    /** So dien thoai Viet Nam 10 chu so (cung quy uoc voi ho gia dinh). */
    private static final Pattern MAU_SDT = Pattern.compile("0\\d{9}");
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
                    "Vui lòng nhập tên đăng nhập và mật khẩu");
        }
        TaiKhoan tk = dao.timTheoTenDangNhap(tenDangNhap.trim())
                .orElseThrow(TaiKhoanService::saiThongTin);
        if (!MatKhauUtil.kiemTra(matKhau, tk.getMatKhauBam())) {
            throw saiThongTin();
        }
        if (!tk.isHoatDong()) {
            throw new NghiepVuException("Tài khoản đã bị khóa");
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
                    "Chỉ Quản trị viên mới được đăng ký tài khoản");
        }
        String ten = tenDangNhap == null ? "" : tenDangNhap.trim();
        if (!TEN_HOP_LE.matcher(ten).matches()) {
            throw new NghiepVuException("Tên đăng nhập gồm 3-50 ký tự: chữ"
                    + " không dấu, số, dấu chấm hoặc gạch dưới");
        }
        String hoTenSach = kiemTraHoTen(hoTen);
        if (vaiTro == null) {
            throw new NghiepVuException("Vui lòng chọn vai trò");
        }
        kiemTraMatKhauMoi(matKhau, "Mật khẩu");
        if (!matKhau.equals(nhapLai)) {
            throw new NghiepVuException("Mật khẩu nhập lại không khớp");
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
            throw new NghiepVuException("Mật khẩu hiện tại không đúng");
        }
        kiemTraMatKhauMoi(matKhauMoi, "Mật khẩu mới");
        dao.capNhatMatKhau(tk.getId(), MatKhauUtil.bam(matKhauMoi));
    }

    /**
     * Doi mat khau cua chinh nguoi dung, co o nhap lai (AC-063, AC-064).
     *
     * @param tk tai khoan dang dang nhap
     * @param matKhauCu mat khau hien tai
     * @param matKhauMoi mat khau moi
     * @param nhapLai mat khau moi nhap lai
     * @throws NghiepVuException neu mat khau cu sai, mat khau moi yeu, nhap
     *         lai khong khop hoac trung mat khau cu
     */
    public void doiMatKhau(TaiKhoan tk, String matKhauCu, String matKhauMoi,
            String nhapLai) {
        PhanQuyen.yeuCau(tk, VaiTro.values());
        if (matKhauMoi != null && !matKhauMoi.equals(nhapLai)) {
            throw new NghiepVuException("Mật khẩu nhập lại không khớp");
        }
        if (matKhauMoi != null && matKhauMoi.equals(matKhauCu)) {
            throw new NghiepVuException(
                    "Mật khẩu mới phải khác mật khẩu hiện tại");
        }
        doiMatKhau(tk, matKhauCu, matKhauMoi);
    }

    /**
     * Cap nhat thong tin ca nhan cua chinh nguoi dung (AC-065): ho ten va so
     * dien thoai lien he. Ten dang nhap va vai tro khong sua o day.
     *
     * @param tk tai khoan dang dang nhap
     * @param hoTen ho ten moi
     * @param soDienThoai so dien thoai, bo trong neu khong co
     * @return tai khoan sau khi cap nhat, de thay vao phien lam viec
     * @throws NghiepVuException neu ho ten trong, qua dai hoac so dien thoai
     *         sai dang
     */
    public TaiKhoan capNhatThongTin(TaiKhoan tk, String hoTen,
            String soDienThoai) {
        PhanQuyen.yeuCau(tk, VaiTro.values());
        String hoTenSach = kiemTraHoTen(hoTen);
        String sdt = soDienThoai == null ? ""
                : soDienThoai.replaceAll("[\\s.]", "");
        if (!sdt.isEmpty() && !MAU_SDT.matcher(sdt).matches()) {
            throw new NghiepVuException(
                    "Số điện thoại gồm 10 chữ số, bắt đầu bằng 0");
        }
        String sdtLuu = sdt.isEmpty() ? null : sdt;
        dao.capNhatThongTin(tk.getId(), hoTenSach, sdtLuu);
        return tk.voiThongTin(hoTenSach, sdtLuu);
    }

    /**
     * Danh sach tai khoan (US-022). Chi Quan tri vien xem duoc.
     *
     * @param nguoi tai khoan dang dang nhap
     * @return tat ca tai khoan
     */
    public List<TaiKhoan> danhSach(TaiKhoan nguoi) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        return dao.danhSach();
    }

    /**
     * Doi vai tro cua tai khoan khac (AC-061). Khong tu doi vai tro cua
     * minh, nen luon con it nhat mot Quan tri vien dang hoat dong.
     *
     * @param nguoi Quan tri vien dang dang nhap
     * @param id tai khoan can doi
     * @param vaiTro vai tro moi
     * @return tai khoan sau khi doi
     */
    public TaiKhoan doiVaiTro(TaiKhoan nguoi, int id, VaiTro vaiTro) {
        TaiKhoan tk = taiKhoanKhac(nguoi, id, "đổi vai trò");
        if (vaiTro == null) {
            throw new NghiepVuException("Vui lòng chọn vai trò");
        }
        dao.capNhatVaiTro(id, vaiTro);
        return tk.voiVaiTro(vaiTro);
    }

    /**
     * Khoa hoac mo khoa tai khoan khac. Khong xoa de giu lich su thu.
     *
     * @param nguoi Quan tri vien dang dang nhap
     * @param id tai khoan can khoa/mo
     * @param hoatDong false de khoa
     * @return tai khoan sau khi doi
     */
    public TaiKhoan datHoatDong(TaiKhoan nguoi, int id, boolean hoatDong) {
        TaiKhoan tk = taiKhoanKhac(nguoi, id,
                hoatDong ? "mở khóa" : "khóa");
        dao.capNhatHoatDong(id, hoatDong);
        return tk.voiHoatDong(hoatDong);
    }

    /**
     * Quan tri vien dat lai mat khau cho nguoi quen mat khau (RAW-035).
     *
     * @param nguoi Quan tri vien dang dang nhap
     * @param id tai khoan can dat lai
     * @param matKhauMoi mat khau tam
     * @param nhapLai mat khau tam nhap lai
     */
    public void datLaiMatKhau(TaiKhoan nguoi, int id, String matKhauMoi,
            String nhapLai) {
        taiKhoanKhac(nguoi, id, "đặt lại mật khẩu");
        kiemTraMatKhauMoi(matKhauMoi, "Mật khẩu mới");
        if (!matKhauMoi.equals(nhapLai)) {
            throw new NghiepVuException("Mật khẩu nhập lại không khớp");
        }
        dao.capNhatMatKhau(id, MatKhauUtil.bam(matKhauMoi));
    }

    /**
     * Doc lai tai khoan dang dang nhap tu CSDL truoc moi lan mo chuc nang,
     * de tai khoan vua bi khoa hoac doi vai tro co hieu luc ngay, khong doi
     * den lan dang nhap sau.
     *
     * @param tk tai khoan trong phien lam viec
     * @return tai khoan moi nhat
     * @throws NghiepVuException neu chua dang nhap, tai khoan khong con hoac
     *         da bi khoa
     */
    public TaiKhoan lamMoiPhien(TaiKhoan tk) {
        if (tk == null) {
            throw new NghiepVuException(PhanQuyen.LOI_CHUA_DANG_NHAP);
        }
        TaiKhoan moi = dao.timTheoId(tk.getId()).orElseThrow(
                () -> new NghiepVuException(PhanQuyen.LOI_CHUA_DANG_NHAP));
        if (!moi.isHoatDong()) {
            throw new NghiepVuException("Tài khoản đã bị khóa");
        }
        return moi;
    }

    private TaiKhoan taiKhoanKhac(TaiKhoan nguoi, int id, String viec) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        if (nguoi.getId() == id) {
            throw new NghiepVuException("Không tự " + viec
                    + " tài khoản của mình");
        }
        return dao.timTheoId(id).orElseThrow(
                () -> new NghiepVuException("Không tìm thấy tài khoản"));
    }

    private static String kiemTraHoTen(String hoTen) {
        String hoTenSach = hoTen == null ? ""
                : hoTen.strip().replaceAll("\\s+", " ");
        if (hoTenSach.isEmpty()) {
            throw new NghiepVuException("Vui lòng nhập họ tên");
        }
        if (hoTenSach.length() > HO_TEN_TOI_DA) {
            throw new NghiepVuException("Họ tên tối đa " + HO_TEN_TOI_DA
                    + " ký tự");
        }
        return hoTenSach;
    }

    private static void kiemTraMatKhauMoi(String matKhau, String ten) {
        if (matKhau == null || matKhau.length() < MAT_KHAU_TOI_THIEU) {
            throw new NghiepVuException(ten + " phải có ít nhất "
                    + MAT_KHAU_TOI_THIEU + " ký tự");
        }
        if (MatKhauUtil.quaDai(matKhau)) {
            throw new NghiepVuException(ten + " quá dài");
        }
    }

    private static NghiepVuException trungTen(String ten) {
        return new NghiepVuException("Tên đăng nhập " + ten
                + " đã tồn tại");
    }

    private static NghiepVuException saiThongTin() {
        return new NghiepVuException("Sai tên đăng nhập hoặc mật khẩu");
    }
}
