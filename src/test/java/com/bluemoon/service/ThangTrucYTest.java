package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Thang trục Y theo "Quy tắc biểu đồ" (docs/design/ui.md): từ 0, mốc tròn
 * 1, 2, 2,5, 5 × 10^n, giá trị lớn nhất nằm trong thang, đơn vị theo giá
 * trị lớn nhất, toàn 0 không chia cho 0.
 */
class ThangTrucYTest {

    private static ThangTrucY tinh(String... dong) {
        return ThangTrucY.tinh(List.of(dong).stream().map(BigDecimal::new)
                .toList());
    }

    private static void bang(String mong, BigDecimal thuc) {
        assertEquals(0, new BigDecimal(mong).compareTo(thuc),
                "mong " + mong + ", thực " + thuc);
    }

    /** Mẫu Figma: 198 … 214 triệu → thang 0 – 250, bước 50. */
    @Test
    void mauFigma() {
        ThangTrucY t = tinh("198000000", "205000000", "201000000",
                "214000000", "209000000", "186450000");
        assertEquals(ThangTrucY.TRIEU, t.donVi());
        bang("50", t.buoc());
        bang("250", t.tren());
        assertEquals(186.45, t.doi(new BigDecimal("186450000")), 1e-9);
    }

    @Test
    void duoiMotTrieuDungNghinDong() {
        ThangTrucY t = tinh("999999", "0");
        assertEquals(ThangTrucY.NGHIN, t.donVi());
        bang("1000", t.heSo());
        bang("200", t.buoc());
        bang("1200", t.tren());
        // Dung 1 trieu chuyen sang trieu dong, nhan khong thanh 0,0x.
        ThangTrucY trieu = tinh("1000000");
        assertEquals(ThangTrucY.TRIEU, trieu.donVi());
        bang("0.2", trieu.buoc());
        bang("1.2", trieu.tren());
    }

    @Test
    void toanKhongVanCoThangMacDinh() {
        ThangTrucY t = tinh("0", "0", "0");
        bang("5", t.tren());
        bang("1", t.buoc());
        assertEquals(ThangTrucY.NGHIN, t.donVi());
        assertEquals(t, ThangTrucY.tinh(List.of()));
    }

    @Test
    void cotCaoNhatSatDinhThiThemMotBuoc() {
        ThangTrucY t = tinh("250000000");
        bang("50", t.buoc());
        bang("300", t.tren());
    }

    @Test
    void mocTron() {
        bang("50", ThangTrucY.mocTronToiThieu(new BigDecimal("42.8")));
        bang("2.5", ThangTrucY.mocTronToiThieu(new BigDecimal("2.1")));
        bang("2", ThangTrucY.mocTronToiThieu(new BigDecimal("2")));
        bang("1000", ThangTrucY.mocTronToiThieu(new BigDecimal("501")));
        bang("0.1", ThangTrucY.mocTronToiThieu(new BigDecimal("0.07")));
    }

    /**
     * Với mọi giá trị lớn nhất: bước là mốc tròn, đỉnh là bội của bước,
     * giá trị lớn nhất nằm trong thang và không quá 6 khoảng.
     */
    @ParameterizedTest
    @ValueSource(strings = {"1", "999", "1000", "12345", "250000", "999999",
        "1000000", "1500000", "7300000", "49999999", "186450000",
        "999999999", "123456789012"})
    void moiGiaTriNamTrongThangMocTron(String lonNhat) {
        ThangTrucY t = tinh(lonNhat, "0");
        BigDecimal m = new BigDecimal(lonNhat).divide(t.heSo());
        assertTrue(m.compareTo(t.tren()) < 0, "trong thang");
        BigDecimal soKhoang = t.tren().divide(t.buoc());
        assertEquals(0, soKhoang.remainder(BigDecimal.ONE).signum(),
                "đỉnh là bội của bước");
        assertTrue(soKhoang.intValue() >= 1 && soKhoang.intValue() <= 6,
                "số khoảng " + soKhoang);
        BigDecimal dinhChuan = t.buoc().movePointLeft(
                t.buoc().precision() - t.buoc().scale() - 1)
                .stripTrailingZeros();
        assertTrue(List.of("1", "2", "2.5", "5").contains(
                dinhChuan.toPlainString()), "mốc tròn " + t.buoc());
    }
}
