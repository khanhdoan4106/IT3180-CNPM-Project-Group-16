package com.bluemoon.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class DongHoCsdlTest {

    private static final Instant GIO_CSDL =
            Instant.parse("2026-11-09T17:30:00Z");

    @Test
    void layGioCsdlKhongPhaiGioMayVaHienThiMuiGioVietNam() {
        DongHoCsdl dh = new DongHoCsdl(DongHoCsdl.VN, () -> GIO_CSDL,
                () -> 0L);
        assertEquals(GIO_CSDL, dh.instant());
        // 17:30 UTC = 00:30 ngay 10/11 gio Viet Nam: "hom nay" la ngay 10.
        assertEquals(LocalDate.of(2026, 11, 10), LocalDate.now(dh));
        assertEquals(LocalDateTime.of(2026, 11, 10, 0, 30),
                LocalDateTime.now(dh));
        assertEquals(ZoneId.of("Asia/Ho_Chi_Minh"), dh.getZone());
    }

    @Test
    void docCsdlMotLanRoiCongThoiGianTroiQuaDocLaiSauMotChuKy() {
        AtomicInteger soLanDoc = new AtomicInteger();
        AtomicLong nano = new AtomicLong(1_000);
        DongHoCsdl dh = new DongHoCsdl(DongHoCsdl.VN, () -> {
            soLanDoc.incrementAndGet();
            return GIO_CSDL;
        }, nano::get);
        dh.instant();
        nano.addAndGet(5_000_000_000L);
        assertEquals(GIO_CSDL.plusSeconds(5), dh.instant());
        assertEquals(1, soLanDoc.get());
        nano.addAndGet(DongHoCsdl.CHU_KY_NS);
        dh.instant();
        assertEquals(2, soLanDoc.get());
    }

    @Test
    void clockFixedVanDungDuocChoTest() {
        Clock c = Clock.fixed(GIO_CSDL, DongHoCsdl.VN);
        assertEquals(LocalDate.of(2026, 11, 10), LocalDate.now(c));
    }
}
