package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

/**
 * Ket qua tinh cong no mot thang (US-009): cac khoan phai nop va cac ho bi
 * bo qua vi chua co dien tich (AC-029).
 *
 * @param thang thang tinh
 * @param khoan cac khoan phai nop, chua co id
 * @param hoBoQua ma can ho bi bo qua
 */
public record KetQuaTinhCongNo(YearMonth thang, List<PhaiNop> khoan,
        List<String> hoBoQua) {

    /**
     * Tao ket qua, sao chep danh sach de khong bi sua tu ben ngoai.
     *
     * @param thang thang tinh
     * @param khoan cac khoan phai nop
     * @param hoBoQua ma can ho bi bo qua
     */
    public KetQuaTinhCongNo {
        khoan = List.copyOf(khoan);
        hoBoQua = List.copyOf(hoBoQua);
    }

    /**
     * Tong so tien phai thu cua thang.
     *
     * @return tong so tien
     */
    public BigDecimal tongTien() {
        return khoan.stream().map(PhaiNop::soTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * So ho co cong no.
     *
     * @return so ho khac nhau trong danh sach
     */
    public long soHo() {
        return khoan.stream().mapToInt(PhaiNop::hoId).distinct().count();
    }
}
