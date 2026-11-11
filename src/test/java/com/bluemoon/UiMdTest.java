package com.bluemoon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

/** docs/design/ui.md giữ đủ quy tắc biểu đồ và bảng màu theo Figma. */
class UiMdTest {

    private static String doc() throws IOException {
        return Files.readString(Path.of("docs/design/ui.md"),
                StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    @Test
    void mucQuyTacBieuDoCoDuSauQuyTac() throws IOException {
        String ui = doc();
        int dau = ui.indexOf("\n## Quy tắc biểu đồ\n");
        assertTrue(dau >= 0, "Thiếu mục Quy tắc biểu đồ");
        int cuoi = ui.indexOf("\n## ", dau + 1);
        String muc = ui.substring(dau, cuoi);
        for (String y : List.of("**Trục X:**", "**Trục Y:**",
                "bắt đầu từ 0", "lưới ngang mờ", "**Cột đúng tỷ lệ**",
                "**Chú giải**", "từ hai chuỗi", "**Kỳ chưa kết thúc**",
                "chú thích", "**Không dùng hai trục Y.**",
                "Thu phí 6 tháng gần nhất")) {
            assertTrue(muc.contains(y), "Thiếu trong quy tắc biểu đồ: " + y);
        }
    }

    @Test
    void bangMauPhuongAnAChuPhuLa526077() throws IOException {
        String dong = doc().lines().filter(d -> d.startsWith("| chữ phụ"))
                .findFirst().orElseThrow();
        assertEquals("| chữ phụ | `#526077` | `#475569` | `#334155` |", dong);
    }

    @Test
    void dieuHuongNoiVeLogoVaNutDangChon() throws IOException {
        String ui = doc();
        assertTrue(ui.contains("Bấm logo \"BlueMoon\""));
        assertTrue(ui.contains("không có mục \"Trang chủ\" riêng"));
        assertTrue(ui.contains("`-mau-chinh-dam`"));
    }
}
