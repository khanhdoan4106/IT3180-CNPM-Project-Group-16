package com.bluemoon;

import com.bluemoon.util.DieuHuong;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Ung dung JavaFX BlueMoon. Hien man hinh dang nhap khi khoi dong.
 */
public class App extends Application {

    private static final String TIEU_DE = "BlueMoon - Quan ly chung cu";

    @Override
    public void start(Stage stage) {
        stage.setTitle(TIEU_DE);
        DieuHuong.khoiTao(stage);
        DieuHuong.hienDangNhap();
        stage.show();
    }

    /**
     * Diem vao JavaFX.
     *
     * @param args doi so dong lenh
     */
    public static void main(String[] args) {
        launch(args);
    }
}
