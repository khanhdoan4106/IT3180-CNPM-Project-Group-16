package com.bluemoon.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.util.PhienLamViec;
import com.bluemoon.util.ThanhMenu;
import java.util.List;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Thanh menu của Trang chính theo Figma: bấm logo "BlueMoon" (tay, gợi ý
 * "Về trang chủ") để về Trang chính, nút của màn đang mở có nền đậm. Tải
 * trang-chinh.fxml thật cùng controller khi chưa đăng nhập (không đụng CSDL).
 */
class TrangChinhMenuTest {

    private static final Color DAM = Color.web("#1d4ed8");

    @BeforeAll
    static void khoiDong() throws Exception {
        FxTest.khoiDong();
    }

    private record Man(Map<String, Object> ten, Scene scene, Stage stage) {
        Button nut(String id) {
            return (Button) ten.get(id);
        }

        StackPane noiDung() {
            return (StackPane) ten.get("noiDung");
        }
    }

    private static Man tai() throws Exception {
        PhienLamViec.xoa();
        FXMLLoader loader = new FXMLLoader(TrangChinhMenuTest.class
                .getResource("/com/bluemoon/view/trang-chinh.fxml"));
        Parent goc = loader.load();
        Scene scene = new Scene(goc, 1440, 1024);
        scene.getStylesheets().add(TrangChinhMenuTest.class
                .getResource("/com/bluemoon/css/giao-dien.css")
                .toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        goc.applyCss();
        goc.layout();
        return new Man(loader.getNamespace(), scene, stage);
    }

    private static Color nen(Button b) {
        b.applyCss();
        if (b.getBackground() == null || b.getBackground().getFills()
                .isEmpty()) {
            return Color.TRANSPARENT;
        }
        BackgroundFill f = b.getBackground().getFills().get(0);
        return (Color) f.getFill();
    }

    @Test
    void logoLaNutTayCoGoiYVeTrangChuVaKhongCoMucTrangChu() throws Exception {
        FxTest.chay(() -> {
            Man m = tai();
            Button logo = m.nut("logoButton");
            assertEquals("BlueMoon", logo.getText());
            assertEquals(Cursor.HAND, logo.getCursor());
            assertEquals("Về trang chủ", logo.getTooltip().getText());
            for (Node n : m.scene().getRoot().lookupAll(".button")) {
                assertFalse(n instanceof Button b
                        && b.getText().equals("Trang chủ"),
                        "Không thêm mục Trang chủ");
            }
            m.stage().close();
            return null;
        });
    }

    @Test
    void bamLogoHienLaiTrangChinhVaBoChonMenu() throws Exception {
        FxTest.chay(() -> {
            Man m = tai();
            List<Node> trangChu = List.copyOf(m.noiDung().getChildren());
            // Gia lap dang mo man Thu phi (controller them lop dang-chon).
            Label manKhac = new Label("man hinh khac");
            m.noiDung().getChildren().setAll(manKhac);
            m.nut("thuPhiButton").getStyleClass().add(ThanhMenu.DANG_CHON);

            m.nut("logoButton").fire();

            assertEquals(trangChu, m.noiDung().getChildren(),
                    "Bấm logo phải hiện lại nội dung Trang chính");
            for (String id : List.of("hoGiaDinhButton", "nhanKhauButton",
                    "khoanThuButton", "congNoButton", "thuPhiButton",
                    "thongKeButton", "taiKhoanButton")) {
                assertFalse(m.nut(id).getStyleClass()
                        .contains(ThanhMenu.DANG_CHON), id);
            }
            m.stage().close();
            return null;
        });
    }

    @Test
    void mucThongKeNamGiuaThuPhiVaTaiKhoan() throws Exception {
        FxTest.chay(() -> {
            Man m = tai();
            Button thongKe = m.nut("thongKeButton");
            assertEquals("Thống kê", thongKe.getText());
            List<Node> menu = thongKe.getParent().getChildrenUnmodifiable();
            int i = menu.indexOf(thongKe);
            assertSame(m.nut("thuPhiButton"), menu.get(i - 1));
            assertSame(m.nut("taiKhoanButton"), menu.get(i + 1));
            assertTrue(thongKe.getStyleClass().contains("nut-menu"));
            m.stage().close();
            return null;
        });
    }

    @Test
    void nutCuaManDangMoCoNenDamVaChiMotNut() throws Exception {
        FxTest.chay(() -> {
            Man m = tai();
            Button thuPhi = m.nut("thuPhiButton");
            Button congNo = m.nut("congNoButton");
            ThanhMenu menu = new ThanhMenu(m.nut("hoGiaDinhButton"),
                    m.nut("nhanKhauButton"), m.nut("khoanThuButton"),
                    congNo, thuPhi, m.nut("taiKhoanButton"));
            assertNull(menu.dangChon());
            assertEquals(Color.TRANSPARENT, nen(thuPhi));

            menu.chon(thuPhi);
            assertSame(thuPhi, menu.dangChon());
            assertEquals(DAM, nen(thuPhi), "Nền -mau-chinh-dam");
            assertEquals(Color.TRANSPARENT, nen(congNo));

            menu.chon(congNo);
            assertSame(congNo, menu.dangChon());
            assertEquals(Color.TRANSPARENT, nen(thuPhi));
            assertEquals(DAM, nen(congNo));

            menu.boChonTatCa();
            assertNull(menu.dangChon());
            assertEquals(Color.TRANSPARENT, nen(congNo));

            assertThrows(IllegalArgumentException.class,
                    () -> menu.chon(m.nut("logoButton")));
            m.stage().close();
            return null;
        });
    }
}
