package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.model.KetQuaTinhCongNo;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.SoTien;
import com.bluemoon.util.PhienLamViec;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

/**
 * Man hinh chot cong no (US-009) va sua khoan phai nop (US-008). Chi
 * chuyen su kien cho service, khong chua nghiep vu.
 */
public class CongNoController {

    private static final String LOI_CSDL =
            "Không kết nối được cơ sở dữ liệu. Xem docs/setup.md";
    /** So thang gan nhat hien trong o chon thang. */
    private static final int SO_THANG = 12;

    private final CongNoService service = new CongNoService(
            new CongNoDaoImpl(), new KhoanPhiDaoImpl(),
            new HoGiaDinhDaoImpl());

    @FXML
    private ComboBox<YearMonth> thangCombo;
    @FXML
    private Label trangThaiLabel;
    @FXML
    private Label boQuaLabel;
    @FXML
    private Label tomTatLabel;
    @FXML
    private TableView<PhaiNop> bangKhoan;
    @FXML
    private TableColumn<PhaiNop, String> cotCanHo;
    @FXML
    private TableColumn<PhaiNop, String> cotKhoan;
    @FXML
    private TableColumn<PhaiNop, String> cotDienTich;
    @FXML
    private TableColumn<PhaiNop, String> cotDonGia;
    @FXML
    private TableColumn<PhaiNop, String> cotSoTien;
    @FXML
    private TableColumn<PhaiNop, String> cotGhiChu;
    @FXML
    private Button suaButton;
    @FXML
    private Label thongBaoLabel;

    /** True khi bang dang hien khoan da luu (sua duoc). */
    private boolean dangXemDaChot;

    @FXML
    private void initialize() {
        ganCot(cotCanHo, PhaiNop::maCanHo);
        ganCot(cotKhoan, PhaiNop::tenKhoan);
        ganCot(cotDienTich, p -> p.dienTich().stripTrailingZeros()
                .toPlainString() + " m²");
        ganCot(cotDonGia, p -> SoTien.hienThi(p.donGia()));
        ganCot(cotSoTien, p -> SoTien.hienThi(p.soTien()));
        ganCot(cotGhiChu, CongNoController::ghiChu);
        List<YearMonth> thang = new ArrayList<>();
        YearMonth hienTai = service.thangHienTai();
        for (int i = 0; i < SO_THANG; i++) {
            thang.add(hienTai.minusMonths(i));
        }
        thangCombo.setItems(FXCollections.observableArrayList(thang));
        thangCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(YearMonth t) {
                return t == null ? "" : "Tháng " + t.getMonthValue() + "/"
                        + t.getYear();
            }

            @Override
            public YearMonth fromString(String s) {
                return null;
            }
        });
        thangCombo.setValue(hienTai);
        boQuaLabel.managedProperty().bind(
                boQuaLabel.textProperty().isNotEmpty());
        thangCombo.valueProperty().addListener((o, cu, moi) -> hienThang());
        hienThang();
    }

    /** Hien khoan da chot cua thang, hoac goi y xem truoc neu chua chot. */
    private void hienThang() {
        boQuaLabel.setText("");
        thongBaoLabel.setText("");
        try {
            List<PhaiNop> ds = service.theoThang(PhienLamViec.hienTai(),
                    thangCombo.getValue());
            dangXemDaChot = !ds.isEmpty();
            bangKhoan.setItems(FXCollections.observableArrayList(ds));
            if (dangXemDaChot) {
                trangThaiLabel.setText(tenThang() + " đã chốt. Chốt lại chỉ"
                        + " thêm hộ hoặc khoản còn thiếu.");
                tomTat(ds);
            } else {
                trangThaiLabel.setText(tenThang() + " chưa chốt.");
                tomTatLabel.setText("");
            }
            suaButton.setDisable(!dangXemDaChot);
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onXemTruoc() {
        try {
            KetQuaTinhCongNo kq = service.xemTruoc(PhienLamViec.hienTai(),
                    thangCombo.getValue());
            dangXemDaChot = false;
            suaButton.setDisable(true);
            bangKhoan.setItems(FXCollections.observableArrayList(kq.khoan()));
            tomTat(kq.khoan());
            hienBoQua(kq);
            thongBaoLabel.setText("Đang xem trước, chưa lưu.");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onChot() {
        Alert hoi = new Alert(Alert.AlertType.CONFIRMATION,
                "Chốt công nợ " + tenThang().toLowerCase() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        hoi.setTitle("Xác nhận");
        hoi.setHeaderText(null);
        if (hoi.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            CongNoService.KetQuaChot kq = service.chot(
                    PhienLamViec.hienTai(), thangCombo.getValue());
            hienThang();
            hienBoQua(kq.tinh());
            baoThanhCong(kq.lanDau()
                    ? "Đã chốt " + kq.soMoi() + " khoản phải nộp"
                    : "Đã bổ sung " + kq.soMoi() + " khoản còn thiếu");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    @FXML
    private void onSua() {
        PhaiNop p = bangKhoan.getSelectionModel().getSelectedItem();
        if (!dangXemDaChot || p == null) {
            baoLoi("Chọn một khoản đã chốt để sửa");
            return;
        }
        Optional<String[]> nhap = hoiSoTienMoi(p);
        if (nhap.isEmpty()) {
            return;
        }
        try {
            PhaiNop moi = service.suaSoTien(PhienLamViec.hienTai(), p.id(),
                    nhap.get()[0], nhap.get()[1]);
            hienThang();
            baoThanhCong("Đã sửa " + moi.tenKhoan() + " của " + moi.maCanHo()
                    + " thành " + SoTien.hienThi(moi.soTien()));
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LOI_CSDL);
        }
    }

    /** Hop thoai sua so tien; tra ve {so tien, ghi chu} hoac rong. */
    private static Optional<String[]> hoiSoTienMoi(PhaiNop p) {
        Dialog<String[]> d = new Dialog<>();
        d.setTitle("Sửa khoản thu – " + p.maCanHo());
        d.setHeaderText(p.tenKhoan() + " · giá trị cũ "
                + SoTien.hienThi(p.soTien()));
        ButtonType luu = new ButtonType("Lưu thay đổi",
                ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(luu, ButtonType.CANCEL);
        TextField soTien = new TextField(p.soTien().toPlainString());
        TextField ghiChu = new TextField(p.ghiChu() == null ? ""
                : p.ghiChu());
        ghiChu.setPromptText("Không bắt buộc");
        GridPane luoi = new GridPane();
        luoi.setHgap(10);
        luoi.setVgap(10);
        luoi.addRow(0, new Label("Số tiền (đ)"), soTien);
        luoi.addRow(1, new Label("Ghi chú"), ghiChu);
        d.getDialogPane().setContent(luoi);
        d.setResultConverter(b -> b == luu
                ? new String[] {soTien.getText(), ghiChu.getText()} : null);
        return d.showAndWait();
    }

    private void tomTat(List<PhaiNop> ds) {
        long soHo = ds.stream().mapToInt(PhaiNop::hoId).distinct().count();
        tomTatLabel.setText("Số hộ: " + soHo + " · Tổng phải thu: "
                + SoTien.hienThi(ds.stream().map(PhaiNop::soTien)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)));
    }

    private void hienBoQua(KetQuaTinhCongNo kq) {
        boQuaLabel.setText(kq.hoBoQua().isEmpty() ? ""
                : kq.hoBoQua().size() + " hộ chưa có diện tích bị bỏ qua: "
                        + String.join(", ", kq.hoBoQua())
                        + ". Cập nhật ở mục Hộ gia đình.");
    }

    private String tenThang() {
        YearMonth t = thangCombo.getValue();
        return "Tháng " + t.getMonthValue() + "/" + t.getYear();
    }

    private static String ghiChu(PhaiNop p) {
        if (p.id() == 0 || !p.daSua()) {
            return "";
        }
        String goc = "Gốc " + SoTien.hienThi(p.soTienGoc());
        return p.ghiChu() == null ? goc : goc + " · " + p.ghiChu();
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

    private static void ganCot(TableColumn<PhaiNop, String> cot,
            Function<PhaiNop, String> lay) {
        cot.setCellValueFactory(
                o -> new SimpleStringProperty(lay.apply(o.getValue())));
    }
}
