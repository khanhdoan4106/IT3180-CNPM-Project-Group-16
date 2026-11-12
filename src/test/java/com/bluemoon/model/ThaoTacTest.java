package com.bluemoon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Ma thao tac trong enum {@link ThaoTac} khop rang buoc chk_nky_thao_tac
 * trong database/schema.sql, de them ma moi ma quen sua CSDL (hoac nguoc
 * lai) thi trot ngay o day thay vi loi luc ghi nhat ky.
 */
class ThaoTacTest {

    @Test
    void maKhopRangBuocCheckTrongSchema() throws IOException {
        String schema = Files.readString(Path.of("database/schema.sql"),
                StandardCharsets.UTF_8);
        Matcher m = Pattern.compile(
                "chk_nky_thao_tac CHECK \\(thao_tac IN \\(([^)]*)\\)\\)")
                .matcher(schema);
        assertTrue(m.find(), "Không thấy ràng buộc chk_nky_thao_tac");
        Set<String> trongSchema = new LinkedHashSet<>();
        Matcher ma = Pattern.compile("'([A-Z_]+)'").matcher(m.group(1));
        while (ma.find()) {
            trongSchema.add(ma.group(1));
        }
        Set<String> trongEnum = Arrays.stream(ThaoTac.values())
                .map(ThaoTac::getMa)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        assertEquals(trongEnum, trongSchema);
    }

    @Test
    void nhanCoDauVaKhongTrung() {
        Set<String> nhan = Arrays.stream(ThaoTac.values())
                .map(ThaoTac::getNhan).collect(Collectors.toSet());
        assertEquals(ThaoTac.values().length, nhan.size());
        assertEquals("Hủy phiếu thu", ThaoTac.HUY_PHIEU_THU.getNhan());
        for (ThaoTac t : ThaoTac.values()) {
            assertTrue(t.getMa().length() <= 30,
                    t.getMa() + " dài hơn cột VARCHAR(30)");
        }
    }
}
