package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.TaiKhoanDaoImpl;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.TaiKhoanService;
import com.bluemoon.util.PhienLamViec;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * US-020: Quan tri vien dang ky tai khoan moi. Chi chuyen su kien cho
 * service, khong chua nghiep vu.
 */
public class TaiKhoanController {

    private static final String LOI_CSDL =
            "Khong ket noi duoc co so du lieu. Xem docs/setup.md";

    private final TaiKhoanService service =
            new TaiKhoanService(new TaiKhoanDaoImpl());

    @FXML
    private TextField tenDangNhapField;
    @FXML
    private TextField hoTenField;
    @FXML
    private PasswordField matKhauField;
    @FXML
    private PasswordField nhapLaiField;
    @FXML
    private ComboBox<VaiTro> vaiTroCombo;
    @FXML
    private Label thongBaoLabel;

    @FXML
    private void initialize() {
        vaiTroCombo.setItems(FXCollections.observableArrayList(
                VaiTro.values()));
        nhapMoi();
    }

    @FXML
    private void onDangKy() {
        try {
            TaiKhoan tk = service.dangKy(PhienLamViec.hienTai(),
                    tenDangNhapField.getText(), hoTenField.getText(),
                    matKhauField.getText(), nhapLaiField.getText(),
                    vaiTroCombo.getValue());
            nhapMoi();
            baoThanhCong("Da tao tai khoan " + tk.getTenDangNhap() + " ("
                    + tk.getVaiTro().getNhan() + ")");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onNhapMoi() {
        nhapMoi();
        thongBaoLabel.setText("");
    }

    private void nhapMoi() {
        tenDangNhapField.clear();
        hoTenField.clear();
        matKhauField.clear();
        nhapLaiField.clear();
        vaiTroCombo.setValue(VaiTro.THU_QUY);
    }

    private void baoLoi(String noiDung) {
        thongBaoLabel.getStyleClass().removeAll("loi", "thanh-cong");
        thongBaoLabel.getStyleClass().add("loi");
        thongBaoLabel.setText(noiDung);
    }

    private void baoThanhCong(String noiDung) {
        thongBaoLabel.getStyleClass().removeAll("loi", "thanh-cong");
        thongBaoLabel.getStyleClass().add("thanh-cong");
        thongBaoLabel.setText(noiDung);
    }
}
