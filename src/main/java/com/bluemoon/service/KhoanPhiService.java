package com.bluemoon.service;

import com.bluemoon.dao.KhoanPhiDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Danh muc khoan thu: xem, doi don gia khoan bat buoc (US-005), tao khoan
 * thu tu nguyen theo dot (US-006).
 */
public class KhoanPhiService {

    /** Ten khoan phi dich vu trong danh muc (seed.sql). */
    public static final String PHI_DICH_VU = "Phí dịch vụ";
    /** Ten khoan phi quan ly trong danh muc (seed.sql). */
    public static final String PHI_QUAN_LY = "Phí quản lý";
    /** Don gia phi dich vu thap nhat, d/m2 (RAW-004, AC-015). */
    public static final BigDecimal DICH_VU_TOI_THIEU = new BigDecimal("2500");
    /** Don gia phi dich vu cao nhat, d/m2 (RAW-004, AC-015). */
    public static final BigDecimal DICH_VU_TOI_DA = new BigDecimal("16500");
    /** Don gia phi quan ly thap nhat, d/m2 (RAW-005, AC-016). */
    public static final BigDecimal QUAN_LY_TOI_THIEU = new BigDecimal("7000");
    /** Do dai toi da cua ten khoan thu (cot ten VARCHAR(100)). */
    public static final int TEN_TOI_DA = 100;

    private final KhoanPhiDao dao;

    /**
     * Tao dich vu.
     *
     * @param dao nguon du lieu khoan thu
     */
    public KhoanPhiService(KhoanPhiDao dao) {
        this.dao = dao;
    }

    /**
     * Toan bo danh muc khoan thu (ca Thu quy va Quan tri vien xem duoc).
     *
     * @param nguoi tai khoan dang dang nhap
     * @return danh sach khoan thu
     */
    public List<KhoanPhi> danhSach(TaiKhoan nguoi) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        return dao.danhSach();
    }

    /**
     * Doi don gia mot khoan bat buoc (AC-015 - AC-017). Don gia phi dich vu
     * ngoai 2.500 - 16.500 d/m2, phi quan ly duoi 7.000 d/m2 bi tu choi.
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien
     * @param id khoa chinh khoan thu
     * @param donGiaNhap don gia tren form
     * @return khoan thu sau khi doi
     * @throws NghiepVuException neu khong co quyen, khong tim thay, khong
     *         phai khoan theo dien tich hoac don gia khong hop le
     */
    public KhoanPhi suaDonGia(TaiKhoan nguoi, int id, String donGiaNhap) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        KhoanPhi khoan = dao.timTheoId(id).orElseThrow(
                () -> new NghiepVuException("Không tìm thấy khoản thu"));
        if (khoan.cachTinh() != CachTinhPhi.THEO_DIEN_TICH) {
            throw new NghiepVuException(
                    "Chỉ đổi đơn giá của khoản tính theo diện tích");
        }
        BigDecimal donGia = SoTien.doc(donGiaNhap, "Đơn giá");
        if (donGia == null) {
            throw new NghiepVuException("Chưa nhập đơn giá");
        }
        kiemTraDonGia(khoan.ten(), donGia);
        dao.capNhatDonGia(id, donGia);
        return khoan.voiDonGia(donGia);
    }

    private static void kiemTraDonGia(String ten, BigDecimal donGia) {
        if (PHI_DICH_VU.equals(ten) && (donGia.compareTo(DICH_VU_TOI_THIEU) < 0
                || donGia.compareTo(DICH_VU_TOI_DA) > 0)) {
            throw new NghiepVuException("Đơn giá phí dịch vụ phải từ "
                    + "2.500 đến 16.500 đ/m²");
        }
        if (PHI_QUAN_LY.equals(ten)
                && donGia.compareTo(QUAN_LY_TOI_THIEU) < 0) {
            throw new NghiepVuException(
                    "Đơn giá phí quản lý tối thiểu 7.000 đ/m²");
        }
        if (donGia.signum() <= 0) {
            throw new NghiepVuException("Đơn giá phải lớn hơn 0");
        }
    }

    /**
     * Tao khoan thu tu nguyen theo dot (AC-019 - AC-021). Bo trong so tien
     * thi moi ho dong bao nhieu ghi bay nhieu.
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien
     * @param tenNhap ten khoan thu
     * @param tuNgay ngay bat dau dot
     * @param denNgay ngay ket thuc dot
     * @param soTienNhap so tien moi ho, co the bo trong
     * @return khoan thu vua tao, da co id
     * @throws NghiepVuException neu thieu/sai du lieu hoac trung ten trong
     *         cung dot
     */
    public KhoanPhi taoKhoanTuNguyen(TaiKhoan nguoi, String tenNhap,
            LocalDate tuNgay, LocalDate denNgay, String soTienNhap) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        String ten = tenNhap == null ? ""
                : tenNhap.strip().replaceAll("\\s+", " ");
        if (ten.isEmpty()) {
            throw new NghiepVuException("Chưa nhập tên khoản thu");
        }
        if (ten.length() > TEN_TOI_DA) {
            throw new NghiepVuException(
                    "Tên khoản thu tối đa " + TEN_TOI_DA + " ký tự");
        }
        if (tuNgay == null || denNgay == null) {
            throw new NghiepVuException("Chưa nhập thời gian thu");
        }
        if (denNgay.isBefore(tuNgay)) {
            throw new NghiepVuException(
                    "Ngày kết thúc không được trước ngày bắt đầu");
        }
        BigDecimal soTien = SoTien.doc(soTienNhap, "Số tiền mỗi hộ");
        if (soTien != null && soTien.signum() == 0) {
            soTien = null;
        }
        String loiTrung = "Khoản thu \"" + ten + "\" đã có trong đợt này";
        if (dao.tonTaiTen(ten, tuNgay)) {
            throw new NghiepVuException(loiTrung);
        }
        KhoanPhi khoan = new KhoanPhi(0, ten, LoaiKhoanPhi.TU_NGUYEN,
                soTien == null ? CachTinhPhi.KHONG_CO_DINH
                        : CachTinhPhi.CO_DINH_THEO_HO,
                soTien, tuNgay, denNgay, TrangThaiKhoanPhi.DANG_THU);
        try {
            return khoan.voiId(dao.them(khoan));
        } catch (TrungKhoaException e) {
            if (KhoanPhiDao.TRUNG_TEN_DOT.equals(e.getTenRangBuoc())) {
                throw new NghiepVuException(loiTrung);
            }
            throw e;
        }
    }
}
