package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.DongHoCsdl;
import com.bluemoon.dao.NhatKyDaoImpl;
import com.bluemoon.dao.TaiKhoanDaoImpl;
import com.bluemoon.model.NhatKy;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.NhatKyService;
import com.bluemoon.service.TaiKhoanService;
import com.bluemoon.util.NgayUtil;
import com.bluemoon.util.PhienLamViec;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

/**
 * Man hinh tai khoan: tai khoan cua toi (US-023) cho moi nguoi dung; quan
 * ly va dang ky tai khoan (US-020, US-022) va nhat ky thao tac (TS-002) chi
 * Quan tri vien thay. Chi chuyen su kien cho service, khong chua nghiep vu.
 */
public class TaiKhoanController {

    private static final DateTimeFormatter THOI_DIEM =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    /** Muc dau cua o loc thao tac: khong loc. */
    private static final String MOI_THAO_TAC = "Tất cả thao tác";

    private final TaiKhoanService service =
            new TaiKhoanService(new TaiKhoanDaoImpl());
    private final NhatKyService nhatKyService =
            new NhatKyService(new NhatKyDaoImpl());
    private boolean daXemNhatKy;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab quanLyTab;
    @FXML
    private Tab nhatKyTab;
    @FXML
    private DatePicker tuNgayPicker;
    @FXML
    private DatePicker denNgayPicker;
    @FXML
    private ComboBox<String> thaoTacCombo;
    @FXML
    private Label soDongNhatKyLabel;
    @FXML
    private TableView<NhatKy> bangNhatKy;
    @FXML
    private TableColumn<NhatKy, String> cotThoiDiem;
    @FXML
    private TableColumn<NhatKy, String> cotNguoiLam;
    @FXML
    private TableColumn<NhatKy, String> cotThaoTac;
    @FXML
    private TableColumn<NhatKy, String> cotDoiTuong;
    @FXML
    private TableColumn<NhatKy, String> cotNoiDung;
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
        // AC-061, TS-002: chi Quan tri vien quan ly tai khoan va xem nhat
        // ky; service kiem tra lai.
        if (tk == null || tk.getVaiTro() != VaiTro.QUAN_TRI) {
            tabPane.getTabs().removeAll(quanLyTab, nhatKyTab);
            return;
        }
        ganNhatKy();
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

    /** Cot, o loc cua tab nhat ky; chi doc CSDL khi mo tab lan dau. */
    private void ganNhatKy() {
        ganCotNhatKy(cotThoiDiem, n -> n.thoiDiem().format(THOI_DIEM));
        ganCotNhatKy(cotNguoiLam,
                n -> n.hoTen() + " (" + n.tenDangNhap() + ")");
        ganCotNhatKy(cotThaoTac, n -> n.thaoTac().getNhan());
        ganCotNhatKy(cotDoiTuong, NhatKy::doiTuong);
        ganCotNhatKy(cotNoiDung,
                n -> n.noiDung() == null ? "" : n.noiDung());
        NgayUtil.ganDinhDang(tuNgayPicker);
        NgayUtil.ganDinhDang(denNgayPicker);
        LocalDate homNay = LocalDate.now(DongHoCsdl.VN);
        tuNgayPicker.setValue(NhatKyService.tuNgayMacDinh(homNay));
        denNgayPicker.setValue(homNay);
        List<String> loai = new ArrayList<>();
        loai.add(MOI_THAO_TAC);
        for (ThaoTac t : ThaoTac.values()) {
            loai.add(t.getNhan());
        }
        thaoTacCombo.setItems(FXCollections.observableArrayList(loai));
        thaoTacCombo.getSelectionModel().selectFirst();
        tabPane.getSelectionModel().selectedItemProperty().addListener(
                (o, cu, moi) -> {
                    if (moi == nhatKyTab && !daXemNhatKy) {
                        onXemNhatKy();
                    }
                });
    }

    @FXML
    private void onXemNhatKy() {
        String loi = NgayUtil.loiDauTien(
                NgayUtil.loiCuaO(tuNgayPicker, "Từ ngày"),
                NgayUtil.loiCuaO(denNgayPicker, "Đến ngày"));
        if (loi != null) {
            baoLoi(loi);
            return;
        }
        int chon = thaoTacCombo.getSelectionModel().getSelectedIndex();
        ThaoTac thaoTac = chon <= 0 ? null : ThaoTac.values()[chon - 1];
        try {
            List<NhatKy> ds = nhatKyService.theoKhoang(
                    PhienLamViec.hienTai(), NgayUtil.doc(tuNgayPicker),
                    NgayUtil.doc(denNgayPicker), thaoTac);
            daXemNhatKy = true;
            bangNhatKy.setItems(FXCollections.observableArrayList(ds));
            soDongNhatKyLabel.setText(ds.size() + " thao tác");
            thongBaoLabel.setText("");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    /** Cot chu cua bang nhat ky; o bi cat thi re chuot xem du noi dung. */
    private static void ganCotNhatKy(TableColumn<NhatKy, String> cot,
            Function<NhatKy, String> lay) {
        cot.setCellValueFactory(
                o -> new SimpleStringProperty(lay.apply(o.getValue())));
        cot.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String chu, boolean rong) {
                super.updateItem(chu, rong);
                setText(rong ? null : chu);
                setTooltip(rong || chu == null || chu.isEmpty() ? null
                        : new Tooltip(chu));
            }
        });
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
            baoLoi(LoiCsdl.thongBao(e));
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
            baoLoi(LoiCsdl.thongBao(e));
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
        if (!HopThoai.xacNhan("Đổi vai trò của " + t.getTenDangNhap()
                + " từ " + t.getVaiTro().getNhan() + " thành "
                + moi.getNhan() + "?")) {
            return;
        }
        try {
            service.doiVaiTro(PhienLamViec.hienTai(), t.getId(), moi);
            taiLai();
            baoThanhCong("Đã đổi vai trò của " + t.getTenDangNhap()
                    + " thành " + moi.getNhan());
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    @FXML
    private void onKhoa() {
        TaiKhoan t = dangChon();
        if (t == null) {
            return;
        }
        if (t.isHoatDong() && !HopThoai.xacNhan("Khóa tài khoản "
                + t.getTenDangNhap() + "? Người này sẽ không đăng nhập được"
                + " cho đến khi mở khóa.")) {
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
            baoLoi(LoiCsdl.thongBao(e));
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
            baoLoi(LoiCsdl.thongBao(e));
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
            baoLoi(LoiCsdl.thongBao(e));
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
            baoLoi(LoiCsdl.thongBao(e));
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
        HopThoai.datNut(d, luu);
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
