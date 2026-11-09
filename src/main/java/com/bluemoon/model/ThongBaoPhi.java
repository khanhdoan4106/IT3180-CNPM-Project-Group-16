package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * Thong bao cac khoan can dong cua mot ho (US-015, AC-044): khoan cua thang
 * thong bao va khoan cua cac thang truoc chua dong.
 *
 * @param ho ho kem ten chu ho
 * @param thang thang thong bao
 * @param khoanThang khoan chua dong cua thang thong bao
 * @param noCu khoan chua dong cua cac thang truoc
 */
public record ThongBaoPhi(TomTatHo ho, YearMonth thang,
        List<KhoanNo> khoanThang, List<KhoanNo> noCu) {

    /**
     * Tong so tien can dong.
     *
     * @return tong khoan cua thang va no cu
     */
    public BigDecimal tong() {
        return cong(khoanThang).add(cong(noCu));
    }

    /**
     * Tong mot nhom khoan.
     *
     * @param ds cac khoan
     * @return tong so tien
     */
    public static BigDecimal cong(List<KhoanNo> ds) {
        return ds.stream().map(KhoanNo::soTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
