package com.bluemoon.service;

import com.bluemoon.dao.DotCuTruDao;
import com.bluemoon.dao.TrungKhoaException;

/**
 * Doi loi trung khoa UNIQUE cua CSDL thanh thong bao nghiep vu. Service da
 * kiem tra trung truoc khi luu; loi nay chi con xay ra khi hai may cung luu
 * mot luc (AC-071), va nguoi dung phai thay cung thong bao nhu khi kiem tra.
 */
final class LoiTrung {

    private LoiTrung() {
    }

    /**
     * Thong bao ung voi rang buoc bi vi pham.
     *
     * @param e loi trung khoa
     * @param maCanHo ma can ho dang luu, null neu khong lien quan
     * @param soCccd so CCCD dang luu, null neu khong co
     * @return loi nghiep vu; rang buoc khac thi nem lai {@code e}
     */
    static NghiepVuException doi(TrungKhoaException e, String maCanHo,
            String soCccd) {
        switch (e.getTenRangBuoc()) {
            case "uq_ho_ma_dang_o":
                return new NghiepVuException(
                        "Mã căn hộ " + maCanHo + " đã có hộ đang ở");
            case "uq_nk_cccd_dang_o":
                return new NghiepVuException(
                        "Số CCCD " + soCccd + " đã tồn tại");
            case "uq_nk_chu_ho":
                return new NghiepVuException(NhanKhauService.LOI_HAI_CHU_HO);
            case DotCuTruDao.CHONG_NGAY:
                return new NghiepVuException(DotCuTruService.LOI_CHONG_NGAY);
            default:
                throw e;
        }
    }
}
