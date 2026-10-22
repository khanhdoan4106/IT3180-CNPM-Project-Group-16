package com.bluemoon.controller;

import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.DieuHuong;
import com.bluemoon.util.PhienLamViec;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private Button taiKhoanButton;

    @FXML
    private void initialize() {
        TaiKhoan tk = PhienLamViec.hienTai();
        if (tk != null) {
            chaoLabel.setText("Xin chao, " + tk.getHoTen() + " ("
                    + tk.getVaiTro().getNhan() + ")");
        }
        // US-020: chi Quan tri vien dang ky tai khoan; service cung kiem tra.
        boolean quanTri = tk != null && tk.getVaiTro() == VaiTro.QUAN_TRI;
        taiKhoanButton.setVisible(quanTri);
        taiKhoanButton.setManaged(quanTri);
    }

    @FXML
    private void onMoHoGiaDinh() {
        noiDung.getChildren().setAll(DieuHuong.taiManHinh("ho-gia-dinh.fxml"));
    }

    @FXML
    private void onMoNhanKhau() {
        noiDung.getChildren().setAll(DieuHuong.taiManHinh("nhan-khau.fxml"));
    }

    @FXML
    private void onMoTaiKhoan() {
        noiDung.getChildren().setAll(DieuHuong.taiManHinh("tai-khoan.fxml"));
    }

    @FXML
    private void onDangXuat() {
        PhienLamViec.xoa();
        DieuHuong.hienDangNhap();
    }
}
