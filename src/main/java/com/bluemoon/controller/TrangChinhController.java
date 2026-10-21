package com.bluemoon.controller;

import com.bluemoon.model.TaiKhoan;
import com.bluemoon.util.DieuHuong;
import com.bluemoon.util.PhienLamViec;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * Khung man hinh chinh (layout: dau - than). Cac chuc nang them theo sprint.
 */
public class TrangChinhController {

    @FXML
    private Label chaoLabel;
    @FXML
    private StackPane noiDung;

    @FXML
    private void initialize() {
        TaiKhoan tk = PhienLamViec.hienTai();
        if (tk != null) {
            chaoLabel.setText("Xin chao, " + tk.getHoTen() + " ("
                    + tk.getVaiTro().getNhan() + ")");
        }
    }

    @FXML
    private void onMoHoGiaDinh() {
        noiDung.getChildren().setAll(DieuHuong.taiManHinh("ho-gia-dinh.fxml"));
    }

    @FXML
    private void onDangXuat() {
        PhienLamViec.xoa();
        DieuHuong.hienDangNhap();
    }
}
