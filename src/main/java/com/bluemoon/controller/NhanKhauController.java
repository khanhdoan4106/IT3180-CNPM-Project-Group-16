package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.NhanKhauDaoImpl;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.service.HoGiaDinhService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.NhanKhauService;
import com.bluemoon.service.ThongTinNhanKhau;
import com.bluemoon.util.NgayUtil;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Man hinh quan ly nhan khau (US-002): chon ho dang o, xem thanh vien, them,
 * sua nhan khau va danh dau thanh vien chuyen di. Chi chuyen su kien cho
 * service va hien ket qua.
 */
public class NhanKhauController {

    private static final String LOI_CSDL =
            "Khong ket noi duoc co so du lieu. Xem docs/setup.md";

    private final NhanKhauService service = new NhanKhauService(
            new NhanKhauDaoImpl(), new HoGiaDinhDaoImpl());
    private final HoGiaDinhService hoService = new HoGiaDinhService(
            new HoGiaDinhDaoImpl(), new NhanKhauDaoImpl());

    /** Nhan khau dang sua; null khi dang them moi. */
    private NhanKhau dangSua;

    @FXML
    private ComboBox<TomTatHo> hoCombo;
    @FXML
    private Label soThanhVienLabel;
    @FXML
    private TableView<NhanKhau> bangNhanKhau;
    @FXML
    private TableColumn<NhanKhau, String> cotHoTen;
    @FXML
    private TableColumn<NhanKhau, String> cotNgaySinh;
    @FXML
    private TableColumn<NhanKhau, String> cotGioiTinh;
    @FXML
    private TableColumn<NhanKhau, String> cotCccd;
    @FXML
    private TableColumn<NhanKhau, String> cotQuanHe;
    @FXML
    private TableColumn<NhanKhau, String> cotTrangThai;
    @FXML
    private Label tieuDeFormLabel;
    @FXML
    private TextField hoTenField;
    @FXML
    private DatePicker ngaySinhPicker;
    @FXML
    private ComboBox<GioiTinh> gioiTinhCombo;
    @FXML
    private TextField cccdField;
    @FXML
    private ComboBox<QuanHeChuHo> quanHeCombo;
    @FXML
    private Label thongBaoLabel;
    @FXML
    private Button luuButton;
    @FXML
    private Button chuyenDiButton;

    @FXML
    private void initialize() {
        ganCot(cotHoTen, NhanKhau::getHoTen);
        ganCot(cotNgaySinh, n -> NgayUtil.hien(n.getNgaySinh()));
        ganCot(cotGioiTinh, n -> n.getGioiTinh().getNhan());
        ganCot(cotCccd, n -> n.getSoCccd() == null ? "" : n.getSoCccd());
        ganCot(cotQuanHe, n -> n.getQuanHe().getNhan());
        ganCot(cotTrangThai, n -> n.getTrangThai().getNhan());

        gioiTinhCombo.setItems(FXCollections.observableArrayList(
                GioiTinh.values()));
        quanHeCombo.setItems(FXCollections.observableArrayList(
                QuanHeChuHo.values()));
        NgayUtil.ganDinhDang(ngaySinhPicker);

        hoCombo.valueProperty().addListener((o, cu, moi) -> {
            nhapMoi();
            taiThanhVien();
        });
        bangNhanKhau.getSelectionModel().selectedItemProperty().addListener(
                (o, cu, moi) -> hienLenForm(moi));
        taiDanhSachHo();
        nhapMoi();
    }

    @FXML
    private void onLuu() {
        TomTatHo ho = hoCombo.getValue();
        if (ho == null) {
            baoLoi("Vui long chon ho gia dinh");
            return;
        }
        if (NgayUtil.saiDinhDang(ngaySinhPicker)) {
            baoLoi("Ngay sinh phai co dang dd/mm/yyyy");
            return;
        }
        ThongTinNhanKhau tt = new ThongTinNhanKhau(hoTenField.getText(),
                NgayUtil.doc(ngaySinhPicker), gioiTinhCombo.getValue(),
                cccdField.getText(), quanHeCombo.getValue());
        try {
            NhanKhau nk = dangSua == null
                    ? service.them(ho.ho().getId(), tt)
                    : service.capNhat(dangSua.getId(), tt);
            String viec = dangSua == null ? "Da them" : "Da cap nhat";
            taiThanhVien();
            chonDong(nk.getId());
            baoThanhCong(viec + " nhan khau " + nk.getHoTen());
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onChuyenDi() {
        if (dangSua == null) {
            return;
        }
        Alert hoi = new Alert(Alert.AlertType.CONFIRMATION,
                "Danh dau " + dangSua.getHoTen() + " da roi ho?"
                        + "\nDu lieu van duoc giu lai.",
                ButtonType.OK, ButtonType.CANCEL);
        hoi.setHeaderText(null);
        if (hoi.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            String ten = dangSua.getHoTen();
            service.danhDauChuyenDi(dangSua.getId());
            onNhapMoi();
            taiThanhVien();
            baoThanhCong(ten + " da duoc danh dau chuyen di");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onNhapMoi() {
        bangNhanKhau.getSelectionModel().clearSelection();
        nhapMoi();
    }

    private void taiDanhSachHo() {
        try {
            hoCombo.setItems(FXCollections.observableArrayList(
                    hoService.timKiem("", false)));
        } catch (CauHinhException | DaoException e) {
            hoCombo.getItems().clear();
            baoLoi(LOI_CSDL);
        }
    }

    /**
     * Tai lai thanh vien cua ho dang chon; so thanh vien chi dem nguoi dang
     * o (AC-005).
     */
    private void taiThanhVien() {
        TomTatHo ho = hoCombo.getValue();
        if (ho == null) {
            bangNhanKhau.getItems().clear();
            soThanhVienLabel.setText("");
            return;
        }
        try {
            bangNhanKhau.setItems(FXCollections.observableArrayList(
                    service.theoHo(ho.ho().getId())));
            long dangO = bangNhanKhau.getItems().stream().filter(
                    n -> n.getTrangThai() == TrangThaiNhanKhau.DANG_O)
                    .count();
            soThanhVienLabel.setText(dangO + " thanh vien dang o");
        } catch (CauHinhException | DaoException e) {
            bangNhanKhau.getItems().clear();
            soThanhVienLabel.setText("");
            baoLoi(LOI_CSDL);
        }
    }

    private void hienLenForm(NhanKhau nk) {
        if (nk == null) {
            return;
        }
        dangSua = nk;
        boolean dangO = nk.getTrangThai() == TrangThaiNhanKhau.DANG_O;
        tieuDeFormLabel.setText(dangO ? "Sua nhan khau"
                : "Nhan khau (da chuyen di)");
        hoTenField.setText(nk.getHoTen());
        ngaySinhPicker.setValue(nk.getNgaySinh());
        gioiTinhCombo.setValue(nk.getGioiTinh());
        cccdField.setText(nk.getSoCccd() == null ? "" : nk.getSoCccd());
        quanHeCombo.setValue(nk.getQuanHe());
        datChoPhepSua(dangO);
        chuyenDiButton.setDisable(!dangO
                || nk.getQuanHe() == QuanHeChuHo.CHU_HO);
        thongBaoLabel.setText("");
    }

    private void nhapMoi() {
        dangSua = null;
        tieuDeFormLabel.setText("Them nhan khau");
        hoTenField.clear();
        ngaySinhPicker.setValue(null);
        gioiTinhCombo.setValue(null);
        cccdField.clear();
        quanHeCombo.setValue(null);
        datChoPhepSua(true);
        luuButton.setDisable(hoCombo.getValue() == null);
        chuyenDiButton.setDisable(true);
        thongBaoLabel.setText("");
    }

    private void datChoPhepSua(boolean choPhep) {
        hoTenField.setEditable(choPhep);
        cccdField.setEditable(choPhep);
        ngaySinhPicker.setDisable(!choPhep);
        gioiTinhCombo.setDisable(!choPhep);
        quanHeCombo.setDisable(!choPhep);
        luuButton.setDisable(!choPhep);
    }

    private void chonDong(int id) {
        for (NhanKhau n : bangNhanKhau.getItems()) {
            if (n.getId() == id) {
                bangNhanKhau.getSelectionModel().select(n);
                bangNhanKhau.scrollTo(n);
                return;
            }
        }
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

    private static <T> void ganCot(TableColumn<NhanKhau, T> cot,
            Function<NhanKhau, T> lay) {
        cot.setCellValueFactory(o -> giaTri(lay.apply(o.getValue())));
    }

    private static <T> ObservableValue<T> giaTri(T v) {
        return new SimpleObjectProperty<>(v);
    }
}
