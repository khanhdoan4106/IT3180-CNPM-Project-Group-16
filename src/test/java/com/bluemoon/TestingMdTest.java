package com.bluemoon;

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
 * Mọi tham chiếu {@code Lớp#phương_thức} trong bảng AC → test của
 * docs/testing.md phải trỏ tới test còn tồn tại, để đổi tên hay xóa test
 * không làm bảng nói sai.
 */
class TestingMdTest {

    private static final Pattern LOP = Pattern.compile(
            "^([A-Z][A-Za-z0-9]*(?:Test|IT))(?:#(\\w+))?$");

    @Test
    void moiThamChieuTestTrongBangAcConTonTai() throws IOException {
        String testing = Files.readString(Path.of("docs/testing.md"),
                StandardCharsets.UTF_8);
        List<String> loi = new ArrayList<>();
        int soThamChieu = 0;
        for (String dong : testing.split("\\R")) {
            if (!dong.startsWith("| AC-")) {
                continue;
            }
            String lopCuoi = null;
            Matcher m = Pattern.compile("`([^`]+)`").matcher(dong);
            while (m.find()) {
                String token = m.group(1);
                String lop;
                String phuongThuc;
                Matcher l = LOP.matcher(token);
                if (l.matches()) {
                    lop = l.group(1);
                    phuongThuc = l.group(2);
                    lopCuoi = lop;
                } else if (token.startsWith("#") && lopCuoi != null) {
                    lop = lopCuoi;
                    phuongThuc = token.substring(1);
                } else {
                    continue;
                }
                soThamChieu++;
                String ma = maNguon(lop);
                String nhan = dong.substring(0, 8).strip();
                if (ma == null) {
                    loi.add(nhan + ": không có lớp " + lop);
                } else if (phuongThuc != null && !Pattern.compile(
                        "void\\s+" + phuongThuc + "\\s*\\(").matcher(ma)
                        .find()) {
                    loi.add(nhan + ": " + lop + " không có " + phuongThuc);
                }
            }
        }
        assertTrue(soThamChieu > 100, "Phải kiểm được các tham chiếu test");
        assertTrue(loi.isEmpty(), String.join("\n", loi));
    }

    private static String maNguon(String lop) throws IOException {
        try (Stream<Path> cay = Files.walk(Path.of("src/test/java"))) {
            return cay.filter(p -> p.getFileName().toString()
                    .equals(lop + ".java")).findFirst().map(p -> {
                        try {
                            return Files.readString(p,
                                    StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }).orElse(null);
        }
    }
}
