package com.bluemoon.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Du lieu de lap mot phieu thu, Service da kiem tra hop le.
 *
 * @param hoId ho nop tien
 * @param hinhThuc hinh thuc thanh toan; null khi tong bang 0
 * @param nguoiThu tai khoan lap phieu
 * @param thoiDiem thoi diem lap (tu dong ho cua Service)
 * @param dong cac dong cua phieu
 */
public record PhieuThuMoi(int hoId, HinhThucThanhToan hinhThuc,
        int nguoiThu, LocalDateTime thoiDiem, List<Dong> dong) {

    /**
     * Tong cac dong.
     *
     * @return tong tien
     */
    public BigDecimal tongTien() {
        return dong.stream().map(Dong::soTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Mot dong cua phieu moi.
     *
     * @param khoanPhiId khoan thu
     * @param phaiNopId khoan phai nop; null voi dong gop tu nguyen
     * @param soTien so tien
     */
    public record Dong(int khoanPhiId, Integer phaiNopId, BigDecimal soTien) {
    }
}
