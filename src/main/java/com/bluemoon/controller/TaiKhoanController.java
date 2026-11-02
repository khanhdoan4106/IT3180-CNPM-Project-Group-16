package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.TaiKhoanDaoImpl;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.TaiKhoanService;
import com.bluemoon.util.PhienLamViec;
import java.util.Optional;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Man hinh tai khoan: tai khoan cua toi (US-023) cho moi nguoi dung; quan
 * ly va dang ky tai khoan (US-020, US-022) chi Quan tri vien thay. Chi
 * chuyen su kien cho service, khong chua nghiep vu.
 */
public class TaiKhoanController {

    private static final String LOI_CSDL =
            "Không kết nối được cơ sở dữ liệu. Xem docs/setup.md";

    private final TaiKhoanService service =
            new TaiKhoanService(new TaiKhoanDaoImpl());

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab quanLyTab;
    @FXML
    private Label tenDangNhapLabel;
    @FXML
    private Label vaiTroLabel;
    @FXML
    private TextField hoTenCuaToiField;
    @FXML
    private TextField sdtCuaToiField;
    @FXML
    private PasswordField matKhauCuField;
    @FXML
    private PasswordField matKhauMoiField;
    @FXML
    private PasswordField nhapLaiMoiField;
    @FXML
    private TableView<TaiKhoan> bangTaiKhoan;
    @FXML
    private TableColumn<TaiKhoan, String> cotTen;
    @FXML
    private TableColumn<TaiKhoan, String> cotHoTen;
    @FXML
    private TableColumn<TaiKhoan, String> cotSdt;
    @FXML
    private TableColumn<TaiKhoan, String> cotVaiTro;
    @FXML
    private TableColumn<TaiKhoan, String> cotTrangThai;
    @FXML
    private Button khoaButton;
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
        TaiKhoan tk = PhienLamViec.hienTai();
        if (tk != null) {
            tenDangNhapLabel.setText(tk.getTenDangNhap());
            vaiTroLabel.setText(tk.getVaiTro().getNhan());
            hoTenCuaToiField.setText(tk.getHoTen());
            sdtCuaToiField.setText(tk.getSoDienThoai() == null ? ""
                    : tk.getSoDienThoai());
        }
        // AC-061: chi Quan tri vien quan ly tai khoan; service kiem tra lai.
        if (tk == null || tk.getVaiTro() != VaiTro.QUAN_TRI) {
            tabPane.getTabs().remove(quanLyTab);
            return;
        }
        ganCot(cotTen, TaiKhoan::getTenDangNhap);
        ganCot(cotHoTen, TaiKhoan::getHoTen);
        ganCot(cotSdt, t -> t.getSoDienThoai() == null ? ""
                : t.getSoDienThoai());
        ganCot(cotVaiTro, t -> t.getVaiTro().getNhan());
        ganCot(cotTrangThai, t -> t.isHoatDong() ? "Hoạt động" : "Đã khóa");
        bangTaiKhoan.getSelectionModel().selectedItemProperty().addListener(
                (o, cu, moi) -> khoaButton.setText(
                        moi != null && !moi.isHoatDong() ? "Mở khóa" : "Khóa"));
        vaiTroCombo.setItems(FXCollections.observableArrayList(
                VaiTro.values()));
        nhapMoi();
        taiLai();
    }

    @FXML
    private void onLuuHoTen() {
        try {
            TaiKhoan moi = service.capNhatThongTin(PhienLamViec.hienTai(),
                    hoTenCuaToiField.getText(), sdtCuaToiField.getText());
            PhienLamViec.dat(moi);
            hoTenCuaToiField.setText(moi.getHoTen());
            sdtCuaToiField.setText(moi.getSoDienThoai() == null ? ""
                    : moi.getSoDienThoai());
            baoThanhCong("Đã lưu thông tin cá nhân");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onDoiMatKhau() {
        try {
            service.doiMatKhau(PhienLamViec.hienTai(),
                    matKhauCuField.getText(), matKhauMoiField.getText(),
                    nhapLaiMoiField.getText());
            matKhauCuField.clear();
            matKhauMoiField.clear();
            nhapLaiMoiField.clear();
            baoThanhCong("Đã đổi mật khẩu. Lần đăng nhập sau dùng mật khẩu"
                    + " mới.");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onDoiVaiTro() {
        TaiKhoan t = dangChon();
        if (t == null) {
            return;
        }
        VaiTro moi = t.getVaiTro() == VaiTro.QUAN_TRI ? VaiTro.THU_QUY
                : VaiTro.QUAN_TRI;
        try {
            service.doiVaiTro(PhienLamViec.hienTai(), t.getId(), moi);
            taiLai();
            baoThanhCong(t.getTenDangNhap() + " giờ là " + moi.getNhan());
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onKhoa() {
        TaiKhoan t = dangChon();
        if (t == null) {
            return;
        }
        try {
            TaiKhoan moi = service.datHoatDong(PhienLamViec.hienTai(),
                    t.getId(), !t.isHoatDong());
            taiLai();
            baoThanhCong((moi.isHoatDong() ? "Đã mở khóa " : "Đã khóa ")
                    + moi.getTenDangNhap());
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onDatLaiMatKhau() {
        TaiKhoan t = dangChon();
        if (t == null) {
            return;
        }
        Optional<String[]> nhap = hoiMatKhauTam(t);
        if (nhap.isEmpty()) {
            return;
        }
        try {
            service.datLaiMatKhau(PhienLamViec.hienTai(), t.getId(),
                    nhap.get()[0], nhap.get()[1]);
            baoThanhCong("Đã đặt lại mật khẩu cho " + t.getTenDangNhap()
                    + ". Nhắc người dùng đổi mật khẩu sau khi đăng nhập.");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onDangKy() {
        try {
            TaiKhoan tk = service.dangKy(PhienLamViec.hienTai(),
                    tenDangNhapField.getText(), hoTenField.getText(),
                    matKhauField.getText(), nhapLaiField.getText(),
                    vaiTroCombo.getValue());
            nhapMoi();
            taiLai();
            baoThanhCong("Đã tạo tài khoản " + tk.getTenDangNhap() + " ("
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

    private void taiLai() {
        try {
            bangTaiKhoan.setItems(FXCollections.observableArrayList(
                    service.danhSach(PhienLamViec.hienTai())));
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    private TaiKhoan dangChon() {
        TaiKhoan t = bangTaiKhoan.getSelectionModel().getSelectedItem();
        if (t == null) {
            baoLoi("Chưa chọn tài khoản");
        }
        return t;
    }

    /** Hop thoai nhap mat khau tam; tra ve {mat khau, nhap lai}. */
    private static Optional<String[]> hoiMatKhauTam(TaiKhoan t) {
        Dialog<String[]> d = new Dialog<>();
        d.setTitle("Đặt lại mật khẩu");
        d.setHeaderText("Tài khoản " + t.getTenDangNhap());
        ButtonType luu = new ButtonType("Đặt lại",
                ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(luu, ButtonType.CANCEL);
        PasswordField moi = new PasswordField();
        moi.setPromptText("ít nhất 8 ký tự");
        PasswordField nhapLai = new PasswordField();
        GridPane luoi = new GridPane();
        luoi.setHgap(10);
        luoi.setVgap(10);
        luoi.addRow(0, new Label("Mật khẩu tạm"), moi);
        luoi.addRow(1, new Label("Nhập lại"), nhapLai);
        d.getDialogPane().setContent(luoi);
        d.setResultConverter(b -> b == luu
                ? new String[] {moi.getText(), nhapLai.getText()} : null);
        return d.showAndWait();
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

    private static void ganCot(TableColumn<TaiKhoan, String> cot,
            Function<TaiKhoan, String> lay) {
        cot.setCellValueFactory(
                o -> new SimpleStringProperty(lay.apply(o.getValue())));
    }
}
