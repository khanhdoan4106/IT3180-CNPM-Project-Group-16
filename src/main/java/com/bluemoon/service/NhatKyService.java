package com.bluemoon.service;

import com.bluemoon.dao.NhatKyDao;
import com.bluemoon.model.NhatKy;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.VaiTro;
import java.time.LocalDate;
import java.util.List;

/**
 * Xem nhat ky thao tac (TS-002, AC-068, AC-069). Chi Quan tri vien xem;
 * nhat ky chi doc, viec ghi nam trong giao dich cua tung thao tac.
 */
public class NhatKyService {

    /** So ngay cua khoang loc mac dinh, tinh ca hom nay. */
    public static final int SO_NGAY_MAC_DINH = 7;

    private final NhatKyDao dao;

    /**
     * Tao dich vu.
     *
     * @param dao nguon du lieu nhat ky
     */
    public NhatKyService(NhatKyDao dao) {
        this.dao = dao;
    }

    /**
     * Ngay dau cua khoang mac dinh: {@link #SO_NGAY_MAC_DINH} ngay gan nhat,
     * tinh ca hom nay.
     *
     * @param homNay ngay hom nay
     * @return ngay dau khoang
     */
    public static LocalDate tuNgayMacDinh(LocalDate homNay) {
        return homNay.minusDays(SO_NGAY_MAC_DINH - 1L);
    }

    /**
     * Nhat ky trong khoang ngay (tinh ca hai dau), moi nhat truoc.
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien
     * @param tuNgay ngay dau
     * @param denNgay ngay cuoi
     * @param thaoTac chi lay loai nay; null la moi loai
     * @return danh sach nhat ky
     * @throws NghiepVuException neu khong co quyen, thieu ngay hoac tu ngay
     *         sau den ngay
     */
    public List<NhatKy> theoKhoang(TaiKhoan nguoi, LocalDate tuNgay,
            LocalDate denNgay, ThaoTac thaoTac) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        if (tuNgay == null || denNgay == null) {
            throw new NghiepVuException("Vui lòng chọn từ ngày và đến ngày");
        }
        if (tuNgay.isAfter(denNgay)) {
            throw new NghiepVuException(
                    "Từ ngày không được sau đến ngày");
        }
        return dao.theoKhoang(tuNgay, denNgay, thaoTac);
    }
}
