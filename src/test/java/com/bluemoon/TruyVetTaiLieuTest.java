package com.bluemoon;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * Mã yêu cầu (RAW-xxx) trích trong chú thích của mã nguồn và tài liệu phải
 * khớp bảng truy vết {@code docs/requirements/09-traceability-matrix.md}:
 * RAW trích cạnh một story thì bảng truy vết phải nối RAW đó với story ấy.
 */
class TruyVetTaiLieuTest {

    private static final Pattern RAW = Pattern.compile("RAW-\\d{3}");
    private static final Pattern US = Pattern.compile("US-\\d{3}");

    /** RAW -> các story trong bảng truy vết (gộp mọi dòng của RAW đó). */
    private static Map<String, Set<String>> bangTruyVet() throws IOException {
        Map<String, Set<String>> kq = new HashMap<>();
        for (String dong : doc("docs/requirements/09-traceability-matrix.md")) {
            String[] cot = dong.split("\\|");
            if (cot.length < 5) {
                continue;
            }
            Matcher raw = RAW.matcher(cot[1]);
            if (raw.find()) {
                Matcher us = US.matcher(cot[4]);
                while (us.find()) {
                    kq.computeIfAbsent(raw.group(), k -> new HashSet<>())
                            .add(us.group());
                }
            }
        }
        return kq;
    }

    private static List<String> doc(String duongDan) throws IOException {
        return Files.readAllLines(Path.of(duongDan), StandardCharsets.UTF_8);
    }

    private static List<String> lech(String nhan, String dong,
            Map<String, Set<String>> truyVet, String story) {
        List<String> loi = new ArrayList<>();
        Matcher m = RAW.matcher(dong);
        while (m.find()) {
            if (!truyVet.getOrDefault(m.group(), Set.of()).contains(story)) {
                loi.add(nhan + ": " + m.group() + " không nối với " + story
                        + " trong bảng truy vết: " + dong.strip());
            }
        }
        return loi;
    }

    @Test
    void chuThichSchemaSqlTrichDungMaYeuCau() throws IOException {
        Map<String, Set<String>> truyVet = bangTruyVet();
        List<String> loi = new ArrayList<>();
        int soDongKiem = 0;
        for (String dong : doc("database/schema.sql")) {
            Matcher us = US.matcher(dong);
            if (dong.startsWith("--") && us.find() && RAW.matcher(dong).find()
                    && !dong.substring(us.end()).contains("US-")) {
                soDongKiem++;
                loi.addAll(lech("schema.sql", dong, truyVet, us.group()));
            }
        }
        assertTrue(soDongKiem >= 3, "Phải kiểm được các chú thích có RAW");
        assertTrue(loi.isEmpty(), String.join("\n", loi));
    }

    @Test
    void giaDinhDonGiaTrichDungMaYeuCau() throws IOException {
        Map<String, Set<String>> truyVet = bangTruyVet();
        String giaDinh = doc("docs/requirements/11-chot-va-gia-dinh.md")
                .stream().filter(d -> d.startsWith("14. ")).findFirst()
                .orElseThrow();
        assertTrue(RAW.matcher(giaDinh).find());
        List<String> loi = lech("giả định 14", giaDinh, truyVet, "US-005");
        assertTrue(loi.isEmpty(), String.join("\n", loi));
        assertFalse(giaDinh.contains("RAW-004"));
    }
}
