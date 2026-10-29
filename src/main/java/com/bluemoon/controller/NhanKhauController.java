package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.DotCuTruDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.NhanKhauDaoImpl;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TinhTrangCuTru;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.service.DotCuTruService;
import com.bluemoon.service.HoGiaDinhService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.NhanKhauService;
import com.bluemoon.service.ThongTinDot;
import com.bluemoon.service.ThongTinNhanKhau;
import com.bluemoon.util.NgayUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Man hinh quan ly nhan khau (US-002, US-003): chon ho dang o, xem thanh
 * vien, them, sua nhan khau, danh dau thanh vien chuyen di, ghi nhan tam
 * tru/tam vang. Chi chuyen su kien cho service va hien ket qua.
 */
public class NhanKhauController {

    private static final String LOI_CSDL =
            "Không kết nối được cơ sở dữ liệu. Xem docs/setup.md";

    private final NhanKhauService service = new NhanKhauService(
            new NhanKhauDaoImpl(), new HoGiaDinhDaoImpl());
    private final HoGiaDinhService hoService = new HoGiaDinhService(
            new HoGiaDinhDaoImpl(), new NhanKhauDaoImpl());
    private final DotCuTruService dotService = new DotCuTruService(
            new DotCuTruDaoImpl(), new NhanKhauDaoImpl(),
            new HoGiaDinhDaoImpl());

    /** Nhan khau dang sua; null khi dang them moi. */
    private NhanKhau dangSua;
    /** Tinh trang cu tru hom nay cua thanh vien ho dang chon, theo id. */
    private Map<Integer, TinhTrangCuTru> tinhTrang = new HashMap<>();

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
    private TableColumn<NhanKhau, String> cotCuTru;
    @FXML
    private TableColumn<NhanKhau, NhanKhau> cotHanTamTru;
    @FXML
    private TableColumn<NhanKhau, String> cotTamVang;
    @FXML
    private TableColumn<NhanKhau, String> cotTrangThai;
    @FXML
    private Label tieuDeDotLabel;
    @FXML
    private TableView<DotCuTru> bangDot;
    @FXML
    private TableColumn<DotCuTru, String> cotDotLoai;
    @FXML
    private TableColumn<DotCuTru, String> cotDotTu;
    @FXML
    private TableColumn<DotCuTru, String> cotDotDen;
    @FXML
    private TableColumn<DotCuTru, String> cotDotLyDo;
    @FXML
    private ComboBox<LoaiDotCuTru> loaiDotCombo;
    @FXML
    private DatePicker dotTuPicker;
    @FXML
    private DatePicker dotDenPicker;
    @FXML
    private TextField lyDoField;
    @FXML
    private Label thongBaoDotLabel;
    @FXML
    private Button ghiDotButton;
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
    private ComboBox<LoaiCuTru> loaiCuTruCombo;
    @FXML
    private GridPane tamTruGrid;
    @FXML
    private TextField noiThuongTruField;
    @FXML
    private Label tamTruTuLabel;
    @FXML
    private DatePicker tamTruTuPicker;
    @FXML
    private Label tamTruDenLabel;
    @FXML
    private DatePicker tamTruDenPicker;
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
        ganCot(cotCuTru, n -> n.getLoaiCuTru().getNhan());
        ganCot(cotHanTamTru, n -> n);
        cotHanTamTru.setCellFactory(c -> new OHanTamTru());
        ganCot(cotTamVang, n -> NgayUtil.hien(tinhTrangCua(n).tamVangDen()));
        ganCot(cotTrangThai, n -> n.getTrangThai().getNhan());
        cotDotLoai.setCellValueFactory(
                o -> giaTri(o.getValue().getLoai().getNhan()));
        cotDotTu.setCellValueFactory(
                o -> giaTri(NgayUtil.hien(o.getValue().getTuNgay())));
        cotDotDen.setCellValueFactory(
                o -> giaTri(NgayUtil.hien(o.getValue().getDenNgay())));
        cotDotLyDo.setCellValueFactory(o -> giaTri(
                o.getValue().getLyDo() == null ? "" : o.getValue().getLyDo()));

        gioiTinhCombo.setItems(FXCollections.observableArrayList(
                GioiTinh.values()));
        quanHeCombo.setItems(FXCollections.observableArrayList(
                QuanHeChuHo.values()));
        loaiCuTruCombo.setItems(FXCollections.observableArrayList(
                LoaiCuTru.values()));
        loaiDotCombo.setItems(FXCollections.observableArrayList(
                LoaiDotCuTru.values()));
        for (DatePicker p : new DatePicker[] {ngaySinhPicker, tamTruTuPicker,
                tamTruDenPicker, dotTuPicker, dotDenPicker}) {
            NgayUtil.ganDinhDang(p);
        }

        loaiCuTruCombo.valueProperty().addListener(
                (o, cu, moi) -> hienPhanTamTru());
        hoCombo.valueProperty().addListener((o, cu, moi) -> {
            nhapMoi();
            taiThanhVien();
        });
        bangNhanKhau.getSelectionModel().selectedItemProperty().addListener(
                (o, cu, moi) -> {
                    hienLenForm(moi);
                    taiDot(moi);
                });
        taiDanhSachHo();
        nhapMoi();
        taiDot(null);
    }

    @FXML
    private void onLuu() {
        TomTatHo ho = hoCombo.getValue();
        if (ho == null) {
            baoLoi("Vui lòng chọn hộ gia đình");
            return;
        }
        for (DatePicker p : new DatePicker[] {ngaySinhPicker, tamTruTuPicker,
                tamTruDenPicker}) {
            if (NgayUtil.saiDinhDang(p)) {
                baoLoi("Ngày phải có dạng dd/mm/yyyy");
                return;
            }
        }
        ThongTinNhanKhau tt = new ThongTinNhanKhau(hoTenField.getText(),
                NgayUtil.doc(ngaySinhPicker), gioiTinhCombo.getValue(),
                cccdField.getText(), quanHeCombo.getValue(),
                loaiCuTruCombo.getValue(), noiThuongTruField.getText());
        try {
            NhanKhau nk;
            if (dangSua == null) {
                nk = service.them(ho.ho().getId(), tt, new ThongTinDot(
                        LoaiDotCuTru.TAM_TRU, NgayUtil.doc(tamTruTuPicker),
                        NgayUtil.doc(tamTruDenPicker), null));
            } else {
                nk = service.capNhat(dangSua.getId(), tt);
            }
            String viec = dangSua == null ? "Đã thêm" : "Đã cập nhật";
            taiThanhVien();
            chonDong(nk.getId());
            baoThanhCong(viec + " nhân khẩu " + nk.getHoTen());
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
                "Đánh dấu " + dangSua.getHoTen() + " đã rời hộ?"
                        + "\nDữ liệu vẫn được giữ lại.",
                ButtonType.OK, ButtonType.CANCEL);
        hoi.setTitle("Xác nhận");
        hoi.setHeaderText(null);
        if (hoi.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            String ten = dangSua.getHoTen();
            service.danhDauChuyenDi(dangSua.getId());
            onNhapMoi();
            taiThanhVien();
            baoThanhCong(ten + " đã được đánh dấu chuyển đi");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onGhiDot() {
        NhanKhau nk = bangNhanKhau.getSelectionModel().getSelectedItem();
        if (nk == null) {
            return;
        }
        if (NgayUtil.saiDinhDang(dotTuPicker)
                || NgayUtil.saiDinhDang(dotDenPicker)) {
            baoDot("Ngày phải có dạng dd/mm/yyyy", true);
            return;
        }
        ThongTinDot tt = new ThongTinDot(loaiDotCombo.getValue(),
                NgayUtil.doc(dotTuPicker), NgayUtil.doc(dotDenPicker),
                lyDoField.getText());
        try {
            DotCuTru dot = dotService.ghiNhan(nk.getId(), tt);
            taiThanhVien();
            chonDong(nk.getId());
            baoDot("Đã ghi nhận " + dot.getLoai().getNhan().toLowerCase()
                    + " cho " + nk.getHoTen(), false);
        } catch (NghiepVuException e) {
            baoDot(e.getMessage(), true);
        } catch (CauHinhException | DaoException e) {
            baoDot(LOI_CSDL, true);
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
     * Tai lai thanh vien cua ho dang chon cung tinh trang cu tru hom nay; so
     * thanh vien chi dem nguoi dang o (AC-005).
     */
    private void taiThanhVien() {
        TomTatHo ho = hoCombo.getValue();
        if (ho == null) {
            tinhTrang = new HashMap<>();
            bangNhanKhau.getItems().clear();
            soThanhVienLabel.setText("");
            return;
        }
        try {
            tinhTrang = dotService.tinhTrangTheoHo(ho.ho().getId());
            bangNhanKhau.setItems(FXCollections.observableArrayList(
                    service.theoHo(ho.ho().getId())));
            long dangO = bangNhanKhau.getItems().stream().filter(
                    n -> n.getTrangThai() == TrangThaiNhanKhau.DANG_O)
                    .count();
            long quaHan = tinhTrang.values().stream()
                    .filter(TinhTrangCuTru::quaHanTamTru).count();
            soThanhVienLabel.setText(dangO + " thành viên đang ở"
                    + (quaHan > 0 ? ", " + quaHan + " quá hạn tạm trú" : ""));
        } catch (CauHinhException | DaoException e) {
            tinhTrang = new HashMap<>();
            bangNhanKhau.getItems().clear();
            soThanhVienLabel.setText("");
            baoLoi(LOI_CSDL);
        }
    }

    /** Hien cac dot cua nguoi dang chon; chi ghi dot moi cho nguoi dang o. */
    private void taiDot(NhanKhau nk) {
        thongBaoDotLabel.setText("");
        lyDoField.clear();
        dotTuPicker.setValue(null);
        dotDenPicker.setValue(null);
        if (nk == null) {
            tieuDeDotLabel.setText("Tạm trú / tạm vắng");
            bangDot.getItems().clear();
            loaiDotCombo.setValue(null);
            ghiDotButton.setDisable(true);
            return;
        }
        tieuDeDotLabel.setText("Tạm trú / tạm vắng: " + nk.getHoTen());
        loaiDotCombo.setValue(nk.getLoaiCuTru() == LoaiCuTru.TAM_TRU
                ? LoaiDotCuTru.TAM_TRU : LoaiDotCuTru.TAM_VANG);
        ghiDotButton.setDisable(
                nk.getTrangThai() != TrangThaiNhanKhau.DANG_O);
        try {
            bangDot.setItems(FXCollections.observableArrayList(
                    dotService.theoNhanKhau(nk.getId())));
        } catch (CauHinhException | DaoException e) {
            bangDot.getItems().clear();
            baoDot(LOI_CSDL, true);
        }
    }

    private void hienLenForm(NhanKhau nk) {
        if (nk == null) {
            return;
        }
        dangSua = nk;
        boolean dangO = nk.getTrangThai() == TrangThaiNhanKhau.DANG_O;
        tieuDeFormLabel.setText(dangO ? "Sửa nhân khẩu"
                : "Nhân khẩu (đã chuyển đi)");
        hoTenField.setText(nk.getHoTen());
        ngaySinhPicker.setValue(nk.getNgaySinh());
        gioiTinhCombo.setValue(nk.getGioiTinh());
        cccdField.setText(nk.getSoCccd() == null ? "" : nk.getSoCccd());
        quanHeCombo.setValue(nk.getQuanHe());
        loaiCuTruCombo.setValue(nk.getLoaiCuTru());
        noiThuongTruField.setText(nk.getNoiThuongTru() == null ? ""
                : nk.getNoiThuongTru());
        datChoPhepSua(dangO);
        hienPhanTamTru();
        chuyenDiButton.setDisable(!dangO
                || nk.getQuanHe() == QuanHeChuHo.CHU_HO);
        thongBaoLabel.setText("");
    }

    private void nhapMoi() {
        dangSua = null;
        tieuDeFormLabel.setText("Thêm nhân khẩu");
        hoTenField.clear();
        ngaySinhPicker.setValue(null);
        gioiTinhCombo.setValue(null);
        cccdField.clear();
        quanHeCombo.setValue(null);
        loaiCuTruCombo.setValue(LoaiCuTru.THUONG_TRU);
        noiThuongTruField.clear();
        tamTruTuPicker.setValue(null);
        tamTruDenPicker.setValue(null);
        datChoPhepSua(true);
        hienPhanTamTru();
        luuButton.setDisable(hoCombo.getValue() == null);
        chuyenDiButton.setDisable(true);
        thongBaoLabel.setText("");
    }

    /**
     * Phan tam tru chi hien voi nguoi tam tru; ngay cua dot dau tien chi
     * nhap khi them moi (sau do gia han o phan tam tru/tam vang).
     */
    private void hienPhanTamTru() {
        boolean tamTru = loaiCuTruCombo.getValue() == LoaiCuTru.TAM_TRU;
        hien(tamTruGrid, tamTru);
        boolean themMoi = dangSua == null;
        for (Node n : new Node[] {tamTruTuLabel,
                tamTruTuPicker, tamTruDenLabel, tamTruDenPicker}) {
            hien(n, themMoi);
        }
    }

    private void datChoPhepSua(boolean choPhep) {
        hoTenField.setEditable(choPhep);
        cccdField.setEditable(choPhep);
        noiThuongTruField.setEditable(choPhep);
        ngaySinhPicker.setDisable(!choPhep);
        gioiTinhCombo.setDisable(!choPhep);
        quanHeCombo.setDisable(!choPhep);
        loaiCuTruCombo.setDisable(!choPhep || dangSua != null);
        luuButton.setDisable(!choPhep);
    }

    private TinhTrangCuTru tinhTrangCua(NhanKhau nk) {
        return tinhTrang.getOrDefault(nk.getId(), TinhTrangCuTru.TRONG);
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
        datThongBao(thongBaoLabel, noiDung, true);
    }

    private void baoThanhCong(String noiDung) {
        datThongBao(thongBaoLabel, noiDung, false);
    }

    private void baoDot(String noiDung, boolean loi) {
        datThongBao(thongBaoDotLabel, noiDung, loi);
    }

    private static void datThongBao(Label nhan, String noiDung,
            boolean loi) {
        nhan.getStyleClass().removeAll("loi", "thanh-cong");
        nhan.getStyleClass().add(loi ? "loi" : "thanh-cong");
        nhan.setText(noiDung);
    }

    private static void hien(Node n, boolean hien) {
        n.setVisible(hien);
        n.setManaged(hien);
    }

    private static <T> void ganCot(TableColumn<NhanKhau, T> cot,
            Function<NhanKhau, T> lay) {
        cot.setCellValueFactory(o -> giaTri(lay.apply(o.getValue())));
    }

    private static <T> ObservableValue<T> giaTri(T v) {
        return new SimpleObjectProperty<>(v);
    }

    /**
     * O "Han tam tru": ngay ket thuc muon nhat cua cac dot tam tru, to do
     * khi nguoi tam tru da qua han ma chua gia han.
     */
    private final class OHanTamTru extends TableCell<NhanKhau, NhanKhau> {

        @Override
        protected void updateItem(NhanKhau nk, boolean rong) {
            super.updateItem(nk, rong);
            getStyleClass().remove("qua-han");
            if (rong || nk == null) {
                setText(null);
                return;
            }
            TinhTrangCuTru tt = tinhTrangCua(nk);
            if (tt.quaHanTamTru()) {
                getStyleClass().add("qua-han");
                setText(tt.hanTamTru() == null ? "Chưa có hạn"
                        : NgayUtil.hien(tt.hanTamTru()) + " (quá hạn)");
            } else {
                setText(NgayUtil.hien(tt.hanTamTru()));
            }
        }
    }
}
