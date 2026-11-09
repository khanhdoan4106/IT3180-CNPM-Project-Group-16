package com.bluemoon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.PhieuThu;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class TomTatPhieuHomNayTest {

    private static PhieuThu phieu(int so, String tien, boolean huy,
            int soLanIn, String lyDo) {
        return new PhieuThu(so, 2026, so, 1, "A0601", "Nguyễn Thị Lan",
                new BigDecimal(tien), tien.equals("0") ? null
                        : HinhThucThanhToan.TIEN_MAT, 2, "Trần Thị Bình",
                LocalDateTime.of(2026, 11, 9, 10, so), soLanIn, huy,
                huy ? "Nguyễn Văn An" : null,
                huy ? LocalDateTime.of(2026, 11, 9, 11, 0) : null, lyDo,
                List.of());
    }

    @Test
    void congChiPhieuChuaHuyVaDemPhieuHuy() {
        TomTatPhieuHomNay tt = TomTatPhieuHomNay.tu(List.of(
                phieu(4, "100000", false, 0, null),
                phieu(3, "1250000", true, 0, "Thu nhầm hộ"),
                phieu(2, "0", false, 0, null),
                phieu(1, "1040000", false, 1, null)));
        assertEquals(4, tt.soPhieu());
        assertEquals(0, new BigDecimal("1140000").compareTo(tt.daThu()));
        assertEquals(1, tt.soHuy());
    }

    @Test
    void ngayChuaCoPhieuThiTatCaBang0() {
        TomTatPhieuHomNay tt = TomTatPhieuHomNay.tu(List.of());
        assertEquals(0, tt.soPhieu());
        assertEquals(0, BigDecimal.ZERO.compareTo(tt.daThu()));
        assertEquals(0, tt.soHuy());
    }

    @Test
    void trangThaiPhieuHuyChiGhiLyDoNguoiHuyXemODuoi() {
        assertEquals("Đã hủy: Thu nhầm hộ", ThuPhiController.trangThai(
                phieu(3, "1250000", true, 0, "Thu nhầm hộ")));
        assertEquals("Chưa in biên lai", ThuPhiController.trangThai(
                phieu(4, "100000", false, 0, null)));
        assertEquals("Đã in 1 lần", ThuPhiController.trangThai(
                phieu(1, "1040000", false, 1, null)));
        assertEquals("Không thu tiền", ThuPhiController.trangThai(
                phieu(2, "0", false, 0, null)));
    }
}
