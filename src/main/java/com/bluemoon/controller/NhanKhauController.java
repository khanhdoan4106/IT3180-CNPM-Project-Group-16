package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.NhanKhauDaoImpl;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TomTatHo;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Man hinh quan ly nhan khau (US-002): chon ho dang o, xem thanh vien, them
 * va sua nhan khau. Chi chuyen su kien cho service va hien ket qua.
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
    private void initialize() {
        ganCot(cotHoTen, NhanKhau::getHoTen);
        ganCot(cotNgaySinh, n -> NgayUtil.hien(n.getNgaySinh()));
        ganCot(cotGioiTinh, n -> n.getGioiTinh().getNhan());
        ganCot(cotCccd, n -> n.getSoCccd() == null ? "" : n.getSoCccd());
        ganCot(cotQuanHe, n -> n.getQuanHe().getNhan());

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

    /** Tai lai thanh vien cua ho dang chon va cap nhat so thanh vien. */
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
            soThanhVienLabel.setText(bangNhanKhau.getItems().size()
                    + " thanh vien");
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
        tieuDeFormLabel.setText("Sua nhan khau");
        hoTenField.setText(nk.getHoTen());
        ngaySinhPicker.setValue(nk.getNgaySinh());
        gioiTinhCombo.setValue(nk.getGioiTinh());
        cccdField.setText(nk.getSoCccd() == null ? "" : nk.getSoCccd());
        quanHeCombo.setValue(nk.getQuanHe());
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
        luuButton.setDisable(hoCombo.getValue() == null);
        thongBaoLabel.setText("");
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
