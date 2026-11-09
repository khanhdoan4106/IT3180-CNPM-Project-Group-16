package com.bluemoon.controller;

import com.bluemoon.model.PhieuThu;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dai tom tat tren bang phieu thu hom nay: so phieu, tien da thu (cac phieu
 * chua huy) va so phieu da huy. Chi cong de hien thi, khong phai nghiep vu.
 *
 * @param soPhieu so phieu, ke ca phieu da huy
 * @param daThu tong tien cac phieu chua huy
 * @param soHuy so phieu da huy
 */
record TomTatPhieuHomNay(int soPhieu, BigDecimal daThu, int soHuy) {

    /**
     * Tom tat danh sach phieu.
     *
     * @param ds phieu trong ngay
     * @return so phieu, tien da thu, so phieu da huy
     */
    static TomTatPhieuHomNay tu(List<PhieuThu> ds) {
        BigDecimal thu = BigDecimal.ZERO;
        int huy = 0;
        for (PhieuThu p : ds) {
            if (p.daHuy()) {
                huy++;
            } else {
                thu = thu.add(p.tongTien());
            }
        }
        return new TomTatPhieuHomNay(ds.size(), thu, huy);
    }
}
