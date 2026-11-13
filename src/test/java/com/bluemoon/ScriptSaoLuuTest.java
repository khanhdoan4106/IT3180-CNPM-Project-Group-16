package com.bluemoon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * Các script sao lưu PowerShell (TS-001): đọc được tiếng Việt trên Windows
 * PowerShell 5.1, gọi mysqldump với đủ tùy chọn của giả định 43 và không
 * nhận mật khẩu qua tham số. Chạy hành vi script: src/test/scripts và
 * SaoLuuIT (MySQL thật).
 */
public class ScriptSaoLuuTest {

    private static final Path SAO_LUU = Path.of("scripts", "sao-luu.ps1");

    /**
     * Danh sách tùy chọn mysqldump trong scripts/sao-luu.ps1 (giữa hai dòng
     * đánh dấu), để SaoLuuIT chạy mysqldump đúng như script.
     *
     * @return các tùy chọn theo thứ tự trong script
     */
    public static List<String> tuyChonMysqldump() {
        String noiDung;
        try {
            noiDung = Files.readString(SAO_LUU, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        int dau = noiDung.indexOf("# TUY-CHON-MYSQLDUMP-BAT-DAU");
        int cuoi = noiDung.indexOf("# TUY-CHON-MYSQLDUMP-KET-THUC");
        assertTrue(dau >= 0 && cuoi > dau,
                "sao-luu.ps1 thiếu dòng đánh dấu danh sách tùy chọn");
        Matcher m = Pattern.compile("^\\s*'(--[^']+)'\\s*$",
                Pattern.MULTILINE).matcher(noiDung.substring(dau, cuoi));
        List<String> kq = new ArrayList<>();
        while (m.find()) {
            kq.add(m.group(1));
        }
        return kq;
    }

    private static List<Path> cacScript() throws IOException {
        List<Path> kq = new ArrayList<>();
        for (Path thuMuc : List.of(Path.of("scripts"),
                Path.of("src", "test", "scripts"))) {
            try (Stream<Path> s = Files.list(thuMuc)) {
                s.filter(p -> p.toString().endsWith(".ps1")).sorted()
                        .forEach(kq::add);
            }
        }
        return kq;
    }

    @Test
    void moiScriptCoBomUtf8DeWindowsPowerShellDocDungTiengViet()
            throws IOException {
        List<Path> script = cacScript();
        assertTrue(script.size() >= 5, "Thiếu script: " + script);
        for (Path p : script) {
            byte[] b = Files.readAllBytes(p);
            // Windows PowerShell 5.1 doc tep khong BOM theo bang ma ANSI:
            // "THÀNH CÔNG" trong log thanh chu loi.
            assertTrue(b.length > 3 && (b[0] & 0xFF) == 0xEF
                    && (b[1] & 0xFF) == 0xBB && (b[2] & 0xFF) == 0xBF,
                    p + " phải lưu UTF-8 có BOM");
        }
    }

    @Test
    void tuyChonMysqldumpTheoGiaDinh43() {
        List<String> tuyChon = tuyChonMysqldump();
        for (String can : List.of("--single-transaction", "--routines",
                "--triggers", "--set-gtid-purged=OFF",
                "--default-character-set=utf8mb4")) {
            assertTrue(tuyChon.contains(can), "Thiếu " + can + ": "
                    + tuyChon);
        }
        // --databases/--all-databases ghi CREATE DATABASE va USE vao tep,
        // khong khoi phuc duoc vao CSDL ten khac.
        for (String cam : List.of("--databases", "-B", "--all-databases",
                "-A", "--skip-triggers")) {
            assertFalse(tuyChon.contains(cam), "Không dùng " + cam);
        }
        assertEquals(tuyChon.size(), tuyChon.stream().distinct().count());
    }

    @Test
    void khongScriptNaoNhanMatKhauQuaThamSo() throws IOException {
        Pattern thamSoMatKhau = Pattern.compile(
                "\\$\\w*(MatKhau|Password|Pwd)\\b|--password",
                Pattern.CASE_INSENSITIVE);
        for (Path p : cacScript()) {
            if (!p.startsWith("scripts")) {
                continue;
            }
            String noiDung = Files.readString(p, StandardCharsets.UTF_8);
            Matcher m = thamSoMatKhau.matcher(noiDung);
            assertFalse(m.find(), () -> p + " có tham số mật khẩu: "
                    + m.group());
        }
    }
}
