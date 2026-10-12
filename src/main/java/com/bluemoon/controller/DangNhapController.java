package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.TaiKhoanDaoImpl;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.TaiKhoanService;
import com.bluemoon.util.DieuHuong;
import com.bluemoon.util.PhienLamViec;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Man hinh dang nhap. Chi chuyen su kien cho service, khong chua nghiep vu.
 */
public class DangNhapController {

    private final TaiKhoanService service =
            new TaiKhoanService(new TaiKhoanDaoImpl());

    @FXML
    private TextField tenDangNhapField;
    @FXML
    private PasswordField matKhauField;
    @FXML
    private Label thongBaoLabel;

    @FXML
    private void onDangNhap() {
        try {
            TaiKhoan tk = service.dangNhap(tenDangNhapField.getText(),
                    matKhauField.getText());
            PhienLamViec.dat(tk);
            DieuHuong.hienTrangChinh();
        } catch (NghiepVuException e) {
            thongBaoLabel.setText(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            thongBaoLabel.setText(
                    "Khong ket noi duoc co so du lieu. Xem docs/setup.md");
        }
    }
}
