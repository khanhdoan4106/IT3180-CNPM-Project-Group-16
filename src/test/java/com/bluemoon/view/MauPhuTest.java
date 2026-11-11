package com.bluemoon.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Chữ phụ (-mau-phu) đạt tương phản WCAG AA (≥ 4,5:1) trên nền #F1F5F9 và
 * trên nền trắng, theo Figma: #526077 (5,8:1). Kiểm cả màu thật mà JavaFX
 * vẽ cho chữ phụ và tiêu đề cột bảng.
 */
class MauPhuTest {

    private static final String CSS = "/com/bluemoon/css/giao-dien.css";
    private static final Color NEN = Color.web("#f1f5f9");

    @BeforeAll
    static void khoiDong() throws Exception {
        FxTest.khoiDong();
    }

    private static String docCss() throws Exception {
        try (var vao = MauPhuTest.class.getResourceAsStream(CSS)) {
            return new String(vao.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static double sang(Color c) {
        double[] k = {c.getRed(), c.getGreen(), c.getBlue()};
        for (int i = 0; i < 3; i++) {
            k[i] = k[i] <= 0.03928 ? k[i] / 12.92
                    : Math.pow((k[i] + 0.055) / 1.055, 2.4);
        }
        return 0.2126 * k[0] + 0.7152 * k[1] + 0.0722 * k[2];
    }

    static double tuongPhan(Color a, Color b) {
        double x = sang(a);
        double y = sang(b);
        return (Math.max(x, y) + 0.05) / (Math.min(x, y) + 0.05);
    }

    @Test
    void mauPhuLa526077VaKhongConMau64748b() throws Exception {
        String css = docCss();
        Matcher m = Pattern.compile("-mau-phu:\s*(#[0-9a-fA-F]{6});")
                .matcher(css);
        assertTrue(m.find());
        assertEquals("#526077", m.group(1).toLowerCase());
        assertFalse(css.toLowerCase().contains("#64748b"),
                "Không còn chỗ nào viết thẳng #64748b");
    }

    @Test
    void chuPhuDatTuongPhanAaTrenNenVaTrenNenTrang() throws Exception {
        Color phu = Color.web("#526077");
        assertTrue(tuongPhan(phu, NEN) >= 5.7, "trên #F1F5F9: "
                + tuongPhan(phu, NEN));
        assertTrue(tuongPhan(phu, Color.WHITE) >= 4.5);
        // Mau cu khong dat AA tren nen: bang chung doi mau la can thiet.
        assertTrue(tuongPhan(Color.web("#64748b"), NEN) < 4.5);
    }

    @Test
    void javaFxVeChuPhuVaTieuDeCotBangBangMauPhu() throws Exception {
        Color[] mau = FxTest.chay(() -> {
            Label phu = new Label("Chữ phụ");
            phu.getStyleClass().add("phu-de");
            TableView<String> bang = new TableView<>();
            TableColumn<String, String> cot = new TableColumn<>("Họ tên");
            bang.getColumns().add(cot);
            VBox goc = new VBox(phu, bang);
            Scene scene = new Scene(goc, 400, 300);
            scene.getStylesheets().add(
                    MauPhuTest.class.getResource(CSS).toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
            goc.applyCss();
            goc.layout();
            Node nhan = bang.lookup(".column-header .label");
            Color c1 = (Color) phu.getTextFill();
            Color c2 = (Color) ((Label) nhan).getTextFill();
            stage.close();
            return new Color[] {c1, c2};
        });
        assertEquals(Color.web("#526077"), mau[0], ".phu-de");
        assertEquals(Color.web("#526077"), mau[1], "tiêu đề cột bảng");
    }
}
