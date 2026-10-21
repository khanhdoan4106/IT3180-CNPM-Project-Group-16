package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.service.HoGiaDinhService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.ThongTinHo;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Man hinh quan ly ho gia dinh (US-001). Chi chuyen su kien cho service va
 * hien ket qua, khong chua nghiep vu.
 */
public class HoGiaDinhController {

    private static final DateTimeFormatter DINH_DANG_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String LOI_CSDL =
            "Khong ket noi duoc co so du lieu. Xem docs/setup.md";

    private final HoGiaDinhService service =
            new HoGiaDinhService(new HoGiaDinhDaoImpl());

    /** Ho dang sua; null khi dang them moi. */
    private HoGiaDinh dangSua;

    @FXML
    private TextField timKiemField;
    @FXML
    private CheckBox gomChuyenDiCheck;
    @FXML
    private Label soLuongLabel;
    @FXML
    private TableView<HoGiaDinh> bangHo;
    @FXML
    private TableColumn<HoGiaDinh, String> cotMa;
    @FXML
    private TableColumn<HoGiaDinh, String> cotLoai;
    @FXML
    private TableColumn<HoGiaDinh, Integer> cotTang;
    @FXML
    private TableColumn<HoGiaDinh, String> cotDienTich;
    @FXML
    private TableColumn<HoGiaDinh, String> cotChuHo;
    @FXML
    private TableColumn<HoGiaDinh, String> cotSdt;
    @FXML
    private TableColumn<HoGiaDinh, String> cotNgay;
    @FXML
    private TableColumn<HoGiaDinh, String> cotTrangThai;
    @FXML
    private Label tieuDeFormLabel;
    @FXML
    private TextField maField;
    @FXML
    private ComboBox<LoaiCanHo> loaiCombo;
    @FXML
    private TextField tangField;
    @FXML
    private TextField dienTichField;
    @FXML
    private TextField chuHoField;
    @FXML
    private TextField sdtField;
    @FXML
    private DatePicker ngayPicker;
    @FXML
    private Label thongBaoLabel;
    @FXML
    private Button chuyenDiButton;

    @FXML
    private void initialize() {
        ganCot(cotMa, HoGiaDinh::getMaCanHo);
        ganCot(cotLoai, h -> h.getLoaiCanHo().getNhan());
        ganCot(cotTang, HoGiaDinh::getTang);
        ganCot(cotDienTich, h -> h.getDienTich().stripTrailingZeros()
                .toPlainString());
        ganCot(cotChuHo, HoGiaDinh::getTenChuHo);
        ganCot(cotSdt, h -> h.getSoDienThoai() == null ? ""
                : h.getSoDienThoai());
        ganCot(cotNgay, h -> h.getNgayChuyenDen().format(DINH_DANG_NGAY));
        ganCot(cotTrangThai, h -> h.getTrangThai().getNhan());

        loaiCombo.setItems(FXCollections.observableArrayList(
                LoaiCanHo.values()));
        ngayPicker.setConverter(new BoChuyenNgay());
        ngayPicker.setPromptText("dd/mm/yyyy");

        timKiemField.textProperty().addListener((o, cu, moi) -> taiLai());
        gomChuyenDiCheck.selectedProperty().addListener(
                (o, cu, moi) -> taiLai());
        bangHo.getSelectionModel().selectedItemProperty().addListener(
                (o, cu, moi) -> hienLenForm(moi));
        nhapMoi();
        taiLai();
    }

    @FXML
    private void onLuu() {
        if (ngaySaiDinhDang()) {
            baoLoi("Ngay chuyen den phai co dang dd/mm/yyyy");
            return;
        }
        ThongTinHo tt = new ThongTinHo(maField.getText(),
                loaiCombo.getValue(), tangField.getText(),
                dienTichField.getText(), chuHoField.getText(),
                sdtField.getText(), docNgay());
        try {
            HoGiaDinh ho = dangSua == null ? service.them(tt)
                    : service.capNhat(dangSua.getId(), tt);
            String viec = dangSua == null ? "Da them" : "Da cap nhat";
            taiLai();
            chonDong(ho.getId());
            baoThanhCong(viec + " ho " + ho.getMaCanHo());
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onNhapMoi() {
        bangHo.getSelectionModel().clearSelection();
        nhapMoi();
    }

    @FXML
    private void onChuyenDi() {
        if (dangSua == null) {
            return;
        }
        Alert hoi = new Alert(Alert.AlertType.CONFIRMATION,
                "Danh dau ho " + dangSua.getMaCanHo() + " ("
                        + dangSua.getTenChuHo() + ") da chuyen di?"
                        + "\nDu lieu cua ho van duoc giu lai.",
                ButtonType.OK, ButtonType.CANCEL);
        hoi.setHeaderText(null);
        if (hoi.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            String ma = dangSua.getMaCanHo();
            service.danhDauChuyenDi(dangSua.getId());
            onNhapMoi();
            taiLai();
            baoThanhCong("Ho " + ma + " da duoc danh dau chuyen di");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    private void taiLai() {
        try {
            bangHo.setItems(FXCollections.observableArrayList(
                    service.timKiem(timKiemField.getText(),
                            gomChuyenDiCheck.isSelected())));
            soLuongLabel.setText(bangHo.getItems().size() + " ho");
        } catch (CauHinhException | DaoException e) {
            bangHo.getItems().clear();
            soLuongLabel.setText("");
            baoLoi(LOI_CSDL);
        }
    }

    private void hienLenForm(HoGiaDinh ho) {
        if (ho == null) {
            return;
        }
        dangSua = ho;
        maField.setText(ho.getMaCanHo());
        loaiCombo.setValue(ho.getLoaiCanHo());
        tangField.setText(String.valueOf(ho.getTang()));
        dienTichField.setText(ho.getDienTich().stripTrailingZeros()
                .toPlainString());
        chuHoField.setText(ho.getTenChuHo());
        sdtField.setText(ho.getSoDienThoai() == null ? ""
                : ho.getSoDienThoai());
        ngayPicker.setValue(ho.getNgayChuyenDen());
        boolean dangO = ho.getTrangThai() == TrangThaiHo.DANG_O;
        tieuDeFormLabel.setText(dangO ? "Sua ho " + ho.getMaCanHo()
                : "Ho " + ho.getMaCanHo() + " (da chuyen di)");
        datChoPhepSua(dangO);
        chuyenDiButton.setDisable(!dangO);
        thongBaoLabel.setText("");
    }

    private void nhapMoi() {
        dangSua = null;
        tieuDeFormLabel.setText("Them ho gia dinh");
        maField.clear();
        loaiCombo.setValue(LoaiCanHo.CAN_HO);
        tangField.clear();
        dienTichField.clear();
        chuHoField.clear();
        sdtField.clear();
        ngayPicker.setValue(LocalDate.now());
        datChoPhepSua(true);
        chuyenDiButton.setDisable(true);
        thongBaoLabel.setText("");
        maField.requestFocus();
    }

    private void datChoPhepSua(boolean choPhep) {
        for (TextField f : new TextField[] {maField, tangField,
                dienTichField, chuHoField, sdtField}) {
            f.setEditable(choPhep);
        }
        loaiCombo.setDisable(!choPhep);
        ngayPicker.setDisable(!choPhep);
    }

    /**
     * Doc ngay tren DatePicker, ke ca khi nguoi dung go tay ma chua nhan
     * Enter. Tra ve null neu de trong hoac sai dinh dang.
     */
    private LocalDate docNgay() {
        String chu = ngayPicker.getEditor().getText();
        if (chu == null || chu.isBlank()) {
            return null;
        }
        LocalDate ngay = ngayPicker.getConverter().fromString(chu.strip());
        ngayPicker.setValue(ngay);
        return ngay;
    }

    private boolean ngaySaiDinhDang() {
        String chu = ngayPicker.getEditor().getText();
        return chu != null && !chu.isBlank()
                && ngayPicker.getConverter().fromString(chu.strip()) == null;
    }

    private void chonDong(int id) {
        for (HoGiaDinh h : bangHo.getItems()) {
            if (h.getId() == id) {
                bangHo.getSelectionModel().select(h);
                bangHo.scrollTo(h);
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

    private static <T> void ganCot(TableColumn<HoGiaDinh, T> cot,
            Function<HoGiaDinh, T> lay) {
        cot.setCellValueFactory(o -> giaTri(lay.apply(o.getValue())));
    }

    private static <T> ObservableValue<T> giaTri(T v) {
        return new SimpleObjectProperty<>(v);
    }

    /** Chuyen doi ngay theo dinh dang dd/MM/yyyy; chuoi sai tra ve null. */
    private static final class BoChuyenNgay
            extends StringConverter<LocalDate> {

        @Override
        public String toString(LocalDate ngay) {
            return ngay == null ? "" : ngay.format(DINH_DANG_NGAY);
        }

        @Override
        public LocalDate fromString(String chu) {
            if (chu == null || chu.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(chu.strip(), DINH_DANG_NGAY);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
    }
}
