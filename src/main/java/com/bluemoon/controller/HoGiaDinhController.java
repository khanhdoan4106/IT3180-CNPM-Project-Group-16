package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.NhanKhauDaoImpl;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.service.HoGiaDinhService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.ThongTinDot;
import com.bluemoon.service.ThongTinHo;
import com.bluemoon.service.ThongTinNhanKhau;
import com.bluemoon.util.NgayUtil;
import java.time.LocalDate;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Man hinh quan ly ho gia dinh (US-001). Chi chuyen su kien cho service va
 * hien ket qua, khong chua nghiep vu.
 */
public class HoGiaDinhController {

    private static final String LOI_CSDL =
            "Khong ket noi duoc co so du lieu. Xem docs/setup.md";

    private final HoGiaDinhService service = new HoGiaDinhService(
            new HoGiaDinhDaoImpl(), new NhanKhauDaoImpl());

    /** Ho dang sua; null khi dang them moi. */
    private TomTatHo dangSua;

    @FXML
    private TextField timKiemField;
    @FXML
    private CheckBox gomChuyenDiCheck;
    @FXML
    private Label soLuongLabel;
    @FXML
    private TableView<TomTatHo> bangHo;
    @FXML
    private TableColumn<TomTatHo, String> cotMa;
    @FXML
    private TableColumn<TomTatHo, String> cotLoai;
    @FXML
    private TableColumn<TomTatHo, Integer> cotTang;
    @FXML
    private TableColumn<TomTatHo, String> cotDienTich;
    @FXML
    private TableColumn<TomTatHo, String> cotChuHo;
    @FXML
    private TableColumn<TomTatHo, Integer> cotSoThanhVien;
    @FXML
    private TableColumn<TomTatHo, String> cotSdt;
    @FXML
    private TableColumn<TomTatHo, String> cotNgay;
    @FXML
    private TableColumn<TomTatHo, String> cotTrangThai;
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
    private TextField sdtField;
    @FXML
    private DatePicker ngayPicker;
    @FXML
    private VBox chuHoBox;
    @FXML
    private TextField chuHoField;
    @FXML
    private DatePicker ngaySinhChuHoPicker;
    @FXML
    private ComboBox<GioiTinh> gioiTinhChuHoCombo;
    @FXML
    private TextField cccdChuHoField;
    @FXML
    private ComboBox<LoaiCuTru> loaiCuTruChuHoCombo;
    @FXML
    private GridPane tamTruChuHoGrid;
    @FXML
    private TextField noiThuongTruChuHoField;
    @FXML
    private DatePicker tamTruTuChuHoPicker;
    @FXML
    private DatePicker tamTruDenChuHoPicker;
    @FXML
    private Label chuHoLabel;
    @FXML
    private Label thongBaoLabel;
    @FXML
    private Button chuyenDiButton;

    @FXML
    private void initialize() {
        ganCot(cotMa, t -> t.ho().getMaCanHo());
        ganCot(cotLoai, t -> t.ho().getLoaiCanHo().getNhan());
        ganCot(cotTang, t -> t.ho().getTang());
        ganCot(cotDienTich, t -> t.ho().getDienTich().stripTrailingZeros()
                .toPlainString());
        ganCot(cotChuHo, t -> t.tenChuHo() == null ? "" : t.tenChuHo());
        ganCot(cotSoThanhVien, TomTatHo::soThanhVien);
        ganCot(cotSdt, t -> t.ho().getSoDienThoai() == null ? ""
                : t.ho().getSoDienThoai());
        ganCot(cotNgay, t -> NgayUtil.hien(t.ho().getNgayChuyenDen()));
        ganCot(cotTrangThai, t -> t.ho().getTrangThai().getNhan());

        loaiCombo.setItems(FXCollections.observableArrayList(
                LoaiCanHo.values()));
        gioiTinhChuHoCombo.setItems(FXCollections.observableArrayList(
                GioiTinh.values()));
        loaiCuTruChuHoCombo.setItems(FXCollections.observableArrayList(
                LoaiCuTru.values()));
        for (DatePicker p : new DatePicker[] {ngayPicker,
                ngaySinhChuHoPicker, tamTruTuChuHoPicker,
                tamTruDenChuHoPicker}) {
            NgayUtil.ganDinhDang(p);
        }
        loaiCuTruChuHoCombo.valueProperty().addListener((o, cu, moi) -> {
            boolean tamTru = moi == LoaiCuTru.TAM_TRU;
            tamTruChuHoGrid.setVisible(tamTru);
            tamTruChuHoGrid.setManaged(tamTru);
        });

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
        if (NgayUtil.saiDinhDang(ngayPicker)) {
            baoLoi("Ngay chuyen den phai co dang dd/mm/yyyy");
            return;
        }
        if (dangSua == null) {
            for (DatePicker p : new DatePicker[] {ngaySinhChuHoPicker,
                    tamTruTuChuHoPicker, tamTruDenChuHoPicker}) {
                if (NgayUtil.saiDinhDang(p)) {
                    baoLoi("Ngay phai co dang dd/mm/yyyy");
                    return;
                }
            }
        }
        ThongTinHo tt = new ThongTinHo(maField.getText(),
                loaiCombo.getValue(), tangField.getText(),
                dienTichField.getText(), sdtField.getText(),
                NgayUtil.doc(ngayPicker));
        try {
            HoGiaDinh ho;
            if (dangSua == null) {
                ho = service.them(tt, new ThongTinNhanKhau(
                        chuHoField.getText(),
                        NgayUtil.doc(ngaySinhChuHoPicker),
                        gioiTinhChuHoCombo.getValue(),
                        cccdChuHoField.getText(), null,
                        loaiCuTruChuHoCombo.getValue(),
                        noiThuongTruChuHoField.getText()),
                        new ThongTinDot(LoaiDotCuTru.TAM_TRU,
                                NgayUtil.doc(tamTruTuChuHoPicker),
                                NgayUtil.doc(tamTruDenChuHoPicker), null));
            } else {
                ho = service.capNhat(dangSua.ho().getId(), tt);
            }
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
                "Danh dau ho " + dangSua + " da chuyen di?"
                        + "\nDu lieu cua ho van duoc giu lai.",
                ButtonType.OK, ButtonType.CANCEL);
        hoi.setHeaderText(null);
        if (hoi.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            String ma = dangSua.ho().getMaCanHo();
            service.danhDauChuyenDi(dangSua.ho().getId());
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

    private void hienLenForm(TomTatHo dong) {
        if (dong == null) {
            return;
        }
        dangSua = dong;
        HoGiaDinh ho = dong.ho();
        maField.setText(ho.getMaCanHo());
        loaiCombo.setValue(ho.getLoaiCanHo());
        tangField.setText(String.valueOf(ho.getTang()));
        dienTichField.setText(ho.getDienTich().stripTrailingZeros()
                .toPlainString());
        sdtField.setText(ho.getSoDienThoai() == null ? ""
                : ho.getSoDienThoai());
        ngayPicker.setValue(ho.getNgayChuyenDen());
        hienPhanChuHo(false);
        chuHoLabel.setText("Chu ho: "
                + (dong.tenChuHo() == null ? "(chua co)" : dong.tenChuHo())
                + " - " + dong.soThanhVien() + " thanh vien."
                + "\nSua chu ho va thanh vien o man hinh Nhan khau.");
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
        sdtField.clear();
        ngayPicker.setValue(LocalDate.now());
        chuHoField.clear();
        ngaySinhChuHoPicker.setValue(null);
        gioiTinhChuHoCombo.setValue(null);
        cccdChuHoField.clear();
        loaiCuTruChuHoCombo.setValue(LoaiCuTru.THUONG_TRU);
        noiThuongTruChuHoField.clear();
        tamTruTuChuHoPicker.setValue(null);
        tamTruDenChuHoPicker.setValue(null);
        hienPhanChuHo(true);
        datChoPhepSua(true);
        chuyenDiButton.setDisable(true);
        thongBaoLabel.setText("");
        maField.requestFocus();
    }

    /** Them moi: nhap chu ho tren form; sua: chi hien ten chu ho. */
    private void hienPhanChuHo(boolean nhap) {
        chuHoBox.setVisible(nhap);
        chuHoBox.setManaged(nhap);
        chuHoLabel.setVisible(!nhap);
        chuHoLabel.setManaged(!nhap);
    }

    private void datChoPhepSua(boolean choPhep) {
        for (TextField f : new TextField[] {maField, tangField,
                dienTichField, sdtField}) {
            f.setEditable(choPhep);
        }
        loaiCombo.setDisable(!choPhep);
        ngayPicker.setDisable(!choPhep);
    }

    private void chonDong(int id) {
        for (TomTatHo t : bangHo.getItems()) {
            if (t.ho().getId() == id) {
                bangHo.getSelectionModel().select(t);
                bangHo.scrollTo(t);
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

    private static <T> void ganCot(TableColumn<TomTatHo, T> cot,
            Function<TomTatHo, T> lay) {
        cot.setCellValueFactory(o -> giaTri(lay.apply(o.getValue())));
    }

    private static <T> ObservableValue<T> giaTri(T v) {
        return new SimpleObjectProperty<>(v);
    }
}
