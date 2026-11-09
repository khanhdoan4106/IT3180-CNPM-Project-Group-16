package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.DongHoCsdl;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

/** Dong ho doc gio may chu MySQL that. */
class DongHoCsdlIT {

    @Test
    void gioCsdlGanGioMayKhiCungMotMayVaMuiGioVietNam() {
        CsdlThu.dungLai();
        DongHoCsdl dh = new DongHoCsdl();
        long lech = Math.abs(Duration.between(Instant.now(), dh.instant())
                .toMillis());
        assertTrue(lech < 5_000, "Lệch " + lech + " ms so với giờ máy");
        assertEquals(ZoneId.of("Asia/Ho_Chi_Minh"), dh.getZone());
        assertEquals(LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh")),
                LocalDate.now(dh));
    }
}
