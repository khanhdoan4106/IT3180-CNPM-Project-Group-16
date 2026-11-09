package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SoTienTest {

    private static void bang(String moi, BigDecimal soTien) {
        assertEquals(0, new BigDecimal(moi).compareTo(soTien), moi);
    }

    @Test
    void docCacDangNguoiDungNhap() {
        bang("350000", SoTien.doc("350000", "Số tiền"));
        bang("350000", SoTien.doc("350.000", "Số tiền"));
        bang("350000", SoTien.doc("350 000", "Số tiền"));
        bang("350000", SoTien.doc(" 350.000 đ ", "Số tiền"));
        assertNull(SoTien.doc("  ", "Số tiền"));
        assertNull(SoTien.doc(null, "Số tiền"));
    }

    @Test
    void tuChoiSoAmSoLeVaChuoiSai() {
        for (String sai : new String[] {"-5000", "5000.5", "5.00", "abc",
                "35.00.000"}) {
            assertThrows(NghiepVuException.class,
                    () -> SoTien.doc(sai, "Số tiền"), sai);
        }
    }

    @Test
    void chanSoLonHonMotTy() {
        bang("1000000000", SoTien.doc("1.000.000.000", "Số tiền"));
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> SoTien.doc("1.000.000.001", "Số tiền"));
        assertEquals("Số tiền tối đa 1.000.000.000 đ", e.getMessage());
        assertThrows(NghiepVuException.class,
                () -> SoTien.doc("99999999999999999999", "Số tiền"));
    }

    @Test
    void giaTriCsdlHienThiRoiDocLaiDuoc() {
        // Loi 2: CSDL tra ve DECIMAL(12,2) = 5000.00; dien san bang
        // toPlainString thi chinh SoTien.doc tu choi.
        assertThrows(NghiepVuException.class,
                () -> SoTien.doc(new BigDecimal("5000.00").toPlainString(),
                        "Đơn giá"));
        for (String csdl : new String[] {"5000.00", "377500.00",
                "1260000.00", "0.00"}) {
            BigDecimal v = new BigDecimal(csdl);
            bang(csdl, SoTien.doc(SoTien.hienThi(v), "Số tiền"));
        }
        assertEquals("1.260.000 đ",
                SoTien.hienThi(new BigDecimal("1260000.00")));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
        "0|Không đồng",
        "5|Năm đồng",
        "11|Mười một đồng",
        "15|Mười lăm đồng",
        "21|Hai mươi mốt đồng",
        "25|Hai mươi lăm đồng",
        "105|Một trăm linh năm đồng",
        "377500|Ba trăm bảy mươi bảy nghìn năm trăm đồng",
        "1283500|Một triệu hai trăm tám mươi ba nghìn năm trăm đồng",
        "1000050|Một triệu không trăm năm mươi đồng",
        "1005000|Một triệu không trăm linh năm nghìn đồng",
        "1812000|Một triệu tám trăm mười hai nghìn đồng",
        "2000000000|Hai tỷ đồng",
        "50000|Năm mươi nghìn đồng"})
    void docSoTienBangChu(String so, String chu) {
        assertEquals(chu, SoTien.bangChu(new BigDecimal(so)));
    }
}
