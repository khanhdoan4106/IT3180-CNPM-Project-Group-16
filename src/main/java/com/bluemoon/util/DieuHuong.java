package com.bluemoon.util;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Chuyen man hinh. Moi man hinh la mot file FXML trong resources/view.
 */
public final class DieuHuong {

    private static final String THU_MUC_VIEW = "/com/bluemoon/view/";
    private static final String CSS = "/com/bluemoon/css/giao-dien.css";

    private static Stage stage;

    private DieuHuong() {
    }

    /**
     * Gan cua so chinh.
     *
     * @param cuaSo stage cua ung dung
     */
    public static void khoiTao(Stage cuaSo) {
        stage = cuaSo;
    }

    /** Hien man hinh dang nhap (1120x645 theo Figma). */
    public static void hienDangNhap() {
        hien("dang-nhap.fxml", 1120, 645, false);
    }

    /** Hien man hinh chinh (1440x1024 theo Figma). */
    public static void hienTrangChinh() {
        hien("trang-chinh.fxml", 1440, 1024, true);
    }

    private static void hien(String tenFile, double rong, double cao,
            boolean phongTo) {
        try {
            URL url = DieuHuong.class.getResource(THU_MUC_VIEW + tenFile);
            Parent goc = FXMLLoader.load(url);
            Scene scene = new Scene(goc, rong, cao);
            scene.getStylesheets().add(
                    DieuHuong.class.getResource(CSS).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(phongTo);
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Khong tai duoc man hinh " + tenFile, e);
        }
    }
}
