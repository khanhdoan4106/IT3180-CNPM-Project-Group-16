package com.bluemoon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.PhongChu;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * TS-004: tên và vai trò người dùng trên thanh menu (Figma "Người dùng"):
 * hai dòng, không "Xin chào"; tên dài quá thì rút gọn "…" và tooltip trên
 * cả hai dòng hiện đủ "Tên – Vai trò". Bố cục ở 1120/1366/1440 px với tên
 * mẫu kiểm ở {@code BoCucFxmlTest#thanhMenuHienDuTenVaVaiTro}.
 */
class NguoiDungThanhMenuTest {

    @BeforeAll
    static void khoiDong() throws Exception {
        datNeuChuaCo("glass.platform", "Monocle");
        datNeuChuaCo("monocle.platform", "Headless");
        datNeuChuaCo("prism.order", "sw");
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

    private static void datNeuChuaCo(String khoa, String giaTri) {
        if (System.getProperty(khoa) == null) {
            System.setProperty(khoa, giaTri);
        }
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
        return kq.get(30, TimeUnit.SECONDS);
    }

    @Test
    void tenDaiRutGonKemTooltipDayDu() throws Exception {
        String ten = "Tôn Nữ Thị Phương Thảo Nguyễn Hoàng Bảo Ngọc";
        chay(() -> {
            Label tenLabel = new Label();
            tenLabel.getStyleClass().add("ten-nguoi-dung");
            Label vaiTro = new Label();
            vaiTro.getStyleClass().add("vai-tro");
            VBox box = new VBox(tenLabel, vaiTro);
            box.getStyleClass().add("thanh-dau");
            box.setMaxWidth(165);
            Scene s = new Scene(box, 165, 80);
            s.getStylesheets().add(NguoiDungThanhMenuTest.class.getResource(
                    "/com/bluemoon/css/giao-dien.css").toExternalForm());
            Stage st = new Stage();
            st.setScene(s);
            st.show();

            TrangChinhController.hienNguoiDung(tenLabel, vaiTro,
                    new TaiKhoan(1, "admin", "x", ten, VaiTro.QUAN_TRI, true));
            box.applyCss();
            box.layout();

            assertEquals(ten, tenLabel.getText(), "không thêm \"Xin chào\"");
            assertEquals("Quản trị viên", vaiTro.getText());
            assertTrue(hienThi(tenLabel).endsWith("…")
                    || hienThi(tenLabel).endsWith("..."),
                    "tên dài bị rút gọn: " + hienThi(tenLabel));
            assertEquals(ten + " – Quản trị viên",
                    tenLabel.getTooltip().getText());
            assertSame(tenLabel.getTooltip(), vaiTro.getTooltip());
            assertEquals(15.0, tenLabel.getFont().getSize(), 0.01);
            assertEquals(14.0, vaiTro.getFont().getSize(), 0.01);

            TrangChinhController.hienNguoiDung(tenLabel, vaiTro, null);
            assertEquals("", tenLabel.getText());
            assertNull(tenLabel.getTooltip());
            st.close();
            return null;
        });
    }

    private static String hienThi(Label l) {
        for (Node n : l.getChildrenUnmodifiable()) {
            if (n instanceof Text t) {
                return t.getText();
            }
        }
        return l.getText();
    }
}
