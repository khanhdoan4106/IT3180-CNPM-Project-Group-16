package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.BienDongDaoImpl;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.NhanKhauDaoImpl;
import com.bluemoon.model.BienDong;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.service.BienDongService;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.HoGiaDinhService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.SoTien;
import com.bluemoon.service.ThongTinDot;
import com.bluemoon.service.ThongTinHo;
import com.bluemoon.service.ThongTinNhanKhau;
import com.bluemoon.util.NgayUtil;
import com.bluemoon.util.PhienLamViec;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    private final HoGiaDinhService service = new HoGiaDinhService(
            new HoGiaDinhDaoImpl(), new NhanKhauDaoImpl());
    private final BienDongService bienDongService =
            new BienDongService(new BienDongDaoImpl());
    private final CongNoService congNoService = new CongNoService(
            new CongNoDaoImpl(), new KhoanPhiDaoImpl(),
            new HoGiaDinhDaoImpl());

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
    private Button lichSuButton;

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
        // Phan chu ho chi co khi them moi; ten o theo nhan tren form.
        String loiNgay = NgayUtil.loiDauTien(
                NgayUtil.loiCuaO(ngayPicker, "Ngày chuyển đến"),
                dangSua != null ? null
                        : NgayUtil.loiCuaO(ngaySinhChuHoPicker, "Ngày sinh"),
                dangSua != null ? null
                        : NgayUtil.loiCuaO(tamTruTuChuHoPicker, "Tạm trú từ"),
                dangSua != null ? null
                        : NgayUtil.loiCuaO(tamTruDenChuHoPicker,
                                "Tạm trú đến"));
        if (loiNgay != null) {
            baoLoi(loiNgay);
            return;
        }
        ThongTinHo tt = new ThongTinHo(maField.getText(),
                loaiCombo.getValue(), tangField.getText(),
                dienTichField.getText(), sdtField.getText(),
                NgayUtil.doc(ngayPicker));
        try {
            HoGiaDinh ho;
            if (dangSua == null) {
                ho = service.them(nguoi(), tt, new ThongTinNhanKhau(
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
                ho = service.capNhat(nguoi(), dangSua.ho().getId(), tt);
            }
            String viec = dangSua == null ? "Đã thêm" : "Đã cập nhật";
            taiLai();
            chonDong(ho.getId());
            baoThanhCong(viec + " hộ " + ho.getMaCanHo());
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
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
        if (!HopThoai.xacNhan("Đánh dấu hộ " + dangSua + " đã chuyển đi?"
                + "\nDữ liệu của hộ vẫn được giữ lại." + canhBaoNo())) {
            return;
        }
        try {
            String ma = dangSua.ho().getMaCanHo();
            service.danhDauChuyenDi(nguoi(), dangSua.ho().getId());
            onNhapMoi();
            taiLai();
            baoThanhCong("Đã đánh dấu hộ " + ma + " chuyển đi");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    private void taiLai() {
        try {
            bangHo.setItems(FXCollections.observableArrayList(
                    service.timKiem(nguoi(), timKiemField.getText(),
                            gomChuyenDiCheck.isSelected())));
            soLuongLabel.setText(bangHo.getItems().size() + " hộ");
        } catch (CauHinhException | DaoException e) {
            bangHo.getItems().clear();
            soLuongLabel.setText("");
            baoLoi(LoiCsdl.thongBao(e));
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
        chuHoLabel.setText("Chủ hộ: "
                + (dong.tenChuHo() == null ? "(chưa có)" : dong.tenChuHo())
                + " - " + dong.soThanhVien() + " thành viên."
                + "\nSửa chủ hộ và thành viên ở màn hình Nhân khẩu.");
        boolean dangO = ho.getTrangThai() == TrangThaiHo.DANG_O;
        tieuDeFormLabel.setText(dangO ? "Sửa hộ " + ho.getMaCanHo()
                : "Hộ " + ho.getMaCanHo() + " (đã chuyển đi)");
        datChoPhepSua(dangO);
        chuyenDiButton.setDisable(!dangO);
        lichSuButton.setDisable(false);
        thongBaoLabel.setText("");
    }

    private void nhapMoi() {
        dangSua = null;
        tieuDeFormLabel.setText("Thêm hộ gia đình");
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
        lichSuButton.setDisable(true);
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

    /** Canh bao no chua dong; no van giu de thu (giả định 21). */
    private String canhBaoNo() {
        try {
            BigDecimal no = congNoService.tongChuaDong(nguoi(),
                    dangSua.ho().getId());
            return no.signum() > 0 ? "\n\nHộ còn nợ " + SoTien.hienThi(no)
                    + " chưa đóng. Khoản nợ vẫn được giữ để thu tiếp." : "";
        } catch (NghiepVuException | CauHinhException | DaoException e) {
            return "";
        }
    }

    /** US-027: lich su bien dong nhan khau cua ho dang chon. */
    @FXML
    private void onLichSu() {
        if (dangSua == null) {
            return;
        }
        try {
            List<BienDong> ds = bienDongService.theoHo(nguoi(),
                    dangSua.ho().getId());
            BangLichSu.hien("Lịch sử biến động – hộ "
                    + dangSua.ho().getMaCanHo(), ds);
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    /** Tai khoan dang dang nhap, truyen cho service kiem tra quyen. */
    private static TaiKhoan nguoi() {
        return PhienLamViec.hienTai();
    }
}
