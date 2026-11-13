package com.bluemoon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.util.PhongChu;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Hộp thoại danh sách tạm vắng (AC-014): nút "Xuất Excel" cạnh "In danh
 * sách", bị vô hiệu khi chưa có danh sách, không nút nào bị cắt chữ ở bề
 * rộng của bảng (760). Dựng nội dung hộp thoại, không gọi CSDL.
 */
class DanhSachTamVangTest {

    @BeforeAll
    static void khoiDong() throws Exception {
        for (String[] kv : new String[][] {{"glass.platform", "Monocle"},
            {"monocle.platform", "Headless"}, {"prism.order", "sw"}}) {
            if (System.getProperty(kv[0]) == null) {
                System.setProperty(kv[0], kv[1]);
            }
        }
        CompletableFuture<Void> xong = new CompletableFuture<>();
        try {
            Platform.startup(() -> xong.complete(null));
        } catch (IllegalStateException daKhoiDong) {
            xong.complete(null);
        }
        xong.get(30, TimeUnit.SECONDS);
        Platform.setImplicitExit(false);
        chay(() -> {
            PhongChu.nap();
            return null;
        });
    }

    private static <T> T chay(Callable<T> viec) throws Exception {
        CompletableFuture<T> kq = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                kq.complete(viec.call());
            } catch (Throwable e) {
                kq.completeExceptionally(e);
            }
        });
        return kq.get(60, TimeUnit.SECONDS);
    }

    private static void gom(Node n, List<Button> nut) {
        if (n instanceof Button b) {
            nut.add(b);
        }
        if (n instanceof javafx.scene.Parent p) {
            p.getChildrenUnmodifiable().forEach(c -> gom(c, nut));
        }
    }

    private static String chuHienThi(Labeled l) {
        for (Node con : l.getChildrenUnmodifiable()) {
            if (con instanceof Text t) {
                return t.getText();
            }
        }
        return l.getText();
    }

    @Test
    void nutXuatExcelCanhNutInVaVoHieuKhiChuaCoDanhSach() throws Exception {
        chay(() -> {
            VBox goc = new DanhSachTamVang().noiDung();
            Scene scene = new Scene(goc, 760, 480);
            scene.getStylesheets().add(DanhSachTamVangTest.class
                    .getResource("/com/bluemoon/css/giao-dien.css")
                    .toExternalForm());
            goc.applyCss();
            goc.layout();
            List<Button> nut = new ArrayList<>();
            gom(goc, nut);
            List<String> ten = nut.stream().map(Button::getText).toList();
            int in = ten.indexOf("In danh sách");
            assertEquals("Xuất Excel", ten.get(in + 1),
                    "Nút Xuất Excel ngay cạnh In danh sách");
            Button xuat = nut.get(in + 1);
            assertTrue(xuat.isDisabled(), "Chưa có danh sách: vô hiệu");
            for (Button b : nut) {
                assertEquals(b.getText(), chuHienThi(b),
                        "Nút bị cắt chữ: " + b.getText());
                assertTrue(b.getWidth() + 0.5 >= b.prefWidth(-1),
                        b.getText());
                assertTrue(b.localToScene(b.getLayoutBounds()).getMaxX()
                        <= 760.5, "Nút nằm trong bề rộng: " + b.getText());
            }
            return null;
        });
    }
}
