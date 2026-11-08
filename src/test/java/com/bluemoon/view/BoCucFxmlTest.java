package com.bluemoon.view;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tai tung man hinh FXML o kich thuoc 1120x645 (Figma, docs/design/ui.md),
 * khong mo cua so, roi kiem tra khong nut hay nhan nao bi cat chu thanh
 * "...". Chay headless bang Monocle nen chay duoc tren CI khong co man hinh.
 *
 * <p>Bo fx:controller va cac thuoc tinh on...="#..." truoc khi tai de khong
 * can CSDL; chu dong (loi chao, nut Khoa/Mo khoa) dien tay ban dai nhat.
 */
class BoCucFxmlTest {

    private static final String THU_MUC_VIEW = "/com/bluemoon/view/";
    private static final String CSS = "/com/bluemoon/css/giao-dien.css";
    private static final double RONG = 1120;
    private static final double CAO = 645;

    /** Chu do controller dat luc chay, lay ban dai nhat co the gap. */
    private static final Map<String, String> CHU_DONG = Map.of(
            "chaoLabel", "Xin chào, Nguyễn Thị Thu Hương (Quản trị viên)",
            "khoaButton", "Mở khóa");

    @BeforeAll
    static void khoiDongJavaFx() throws Exception {
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
    }

    private static void datNeuChuaCo(String khoa, String giaTri) {
        if (System.getProperty(khoa) == null) {
            System.setProperty(khoa, giaTri);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"dang-nhap.fxml", "trang-chinh.fxml",
            "ho-gia-dinh.fxml", "nhan-khau.fxml", "khoan-thu.fxml",
            "cong-no.fxml", "tai-khoan.fxml"})
    void khongCoChuBiCat(String tenFile) throws Exception {
        List<String> loi = trenLuongFx(() -> kiemTra(tenFile));
        assertTrue(loi.isEmpty(), tenFile + " ở " + (int) RONG + "×"
                + (int) CAO + " có chữ bị cắt:\n  "
                + String.join("\n  ", loi));
    }

    private static List<String> kiemTra(String tenFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                BoCucFxmlTest.class.getResource(THU_MUC_VIEW + tenFile));
        Parent goc;
        try (InputStream vao = new ByteArrayInputStream(
                boController(tenFile).getBytes(StandardCharsets.UTF_8))) {
            goc = loader.load(vao);
        }
        CHU_DONG.forEach((id, chu) -> {
            if (loader.getNamespace().get(id) instanceof Labeled l) {
                l.setText(chu);
            }
        });
        Scene scene = new Scene(goc, RONG, CAO);
        scene.getStylesheets().add(
                BoCucFxmlTest.class.getResource(CSS).toExternalForm());

        List<String> loi = new ArrayList<>();
        List<TabPane> cacTabPane = new ArrayList<>();
        timTabPane(goc, cacTabPane);
        if (cacTabPane.isEmpty()) {
            dungBoCuc(goc);
            thuThapLoi(goc, "", loi);
        }
        for (TabPane tp : cacTabPane) {
            for (Tab tab : tp.getTabs()) {
                tp.getSelectionModel().select(tab);
                dungBoCuc(goc);
                thuThapLoi(goc, "[" + tab.getText() + "] ", loi);
            }
        }
        return loi;
    }

    /** Bo controller va trinh xu ly su kien: chi kiem bo cuc. */
    private static String boController(String tenFile) throws IOException {
        URL url = BoCucFxmlTest.class.getResource(THU_MUC_VIEW + tenFile);
        try (InputStream vao = url.openStream()) {
            String fxml = new String(vao.readAllBytes(),
                    StandardCharsets.UTF_8);
            return fxml.replaceAll("\\s+fx:controller=\"[^\"]*\"", "")
                    .replaceAll("\\s+on[A-Z]\\w*=\"#[^\"]*\"", "");
        }
    }

    private static void dungBoCuc(Parent goc) {
        goc.applyCss();
        goc.layout();
        // Lan hai: skin moi tao o lan dau (tab vua chon) can them mot luot.
        goc.applyCss();
        goc.layout();
    }

    private static void timTabPane(Node node, List<TabPane> ketQua) {
        if (node instanceof TabPane tp) {
            ketQua.add(tp);
        }
        if (node instanceof Parent p) {
            for (Node con : p.getChildrenUnmodifiable()) {
                timTabPane(con, ketQua);
            }
        }
    }

    private static void thuThapLoi(Node node, String tienTo,
            List<String> loi) {
        if (!node.isVisible()) {
            return;
        }
        if (node instanceof Labeled l && l.getText() != null
                && !l.getText().isEmpty()) {
            String hienThi = chuHienThi(l);
            if (hienThi != null && !hienThi.equals(l.getText())) {
                loi.add(tienTo + moTa(l) + " hiện \"" + hienThi + "\"");
            } else if (l instanceof Button b
                    && b.getWidth() + 0.5 < b.prefWidth(-1)) {
                loi.add(tienTo + moTa(b) + " rộng " + Math.round(b.getWidth())
                        + " < cần " + Math.round(b.prefWidth(-1)));
            }
        }
        if (node instanceof Parent p) {
            for (Node con : p.getChildrenUnmodifiable()) {
                thuThapLoi(con, tienTo, loi);
            }
        }
    }

    /** Chu skin dang ve (da cat neu khong du cho), null neu chua co skin. */
    private static String chuHienThi(Labeled l) {
        for (Node con : l.getChildrenUnmodifiable()) {
            if (con instanceof Text t) {
                return t.getText();
            }
        }
        return null;
    }

    private static String moTa(Labeled l) {
        return l.getClass().getSimpleName() + " \"" + l.getText() + "\"";
    }

    private interface CongViec<T> {
        T chay() throws Exception;
    }

    private static <T> T trenLuongFx(CongViec<T> viec) throws Exception {
        CompletableFuture<T> ketQua = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                ketQua.complete(viec.chay());
            } catch (Throwable t) {
                ketQua.completeExceptionally(t);
            }
        });
        return ketQua.get(30, TimeUnit.SECONDS);
    }
}
