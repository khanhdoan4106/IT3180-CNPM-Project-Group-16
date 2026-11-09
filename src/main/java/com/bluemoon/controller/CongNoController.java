package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.DongHoCsdl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
import com.bluemoon.model.KetQuaTinhCongNo;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.ThongBaoPhi;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.SoTien;
import com.bluemoon.service.ThongBaoPhiService;
import com.bluemoon.util.PhienLamViec;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
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

    /** So thang gan nhat hien trong o chon thang. */
    private static final int SO_THANG = 12;

    private final CongNoService service = new CongNoService(
            new CongNoDaoImpl(), new KhoanPhiDaoImpl(),
            new HoGiaDinhDaoImpl(), new DongHoCsdl());
    private final ThongBaoPhiService thongBaoService = new ThongBaoPhiService(
            new PhieuThuDaoImpl(), new CongNoDaoImpl());

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
    private TableColumn<PhaiNop, String> cotDaDong;
    @FXML
    private TableColumn<PhaiNop, String> cotGhiChu;
    @FXML
    private MenuButton thongBaoButton;
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
        ganCot(cotDaDong, p -> p.daDong() ? "Đã đóng" : "");
        ganCot(cotGhiChu, CongNoController::ghiChu);
        List<YearMonth> thang = new ArrayList<>();
        YearMonth hienTai;
        try {
            hienTai = service.thangHienTai();
        } catch (CauHinhException | DaoException e) {
            hienTai = YearMonth.now();
            baoLoi(LoiCsdl.thongBao(e));
        }
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
            thongBaoButton.setDisable(!dangXemDaChot);
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    @FXML
    private void onXemTruoc() {
        try {
            KetQuaTinhCongNo kq = service.xemTruoc(PhienLamViec.hienTai(),
                    thangCombo.getValue());
            dangXemDaChot = false;
            suaButton.setDisable(true);
            thongBaoButton.setDisable(true);
            bangKhoan.setItems(FXCollections.observableArrayList(kq.khoan()));
            tomTat(kq.khoan());
            hienBoQua(kq);
            thongBaoLabel.getStyleClass().removeAll("loi", "thanh-cong");
            thongBaoLabel.setText("Đang xem trước, chưa lưu.");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    @FXML
    private void onChot() {
        if (!HopThoai.xacNhan("Chốt công nợ " + tenThang().toLowerCase()
                + "?")) {
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
            baoLoi(LoiCsdl.thongBao(e));
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
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    /** Hop thoai sua so tien; tra ve {so tien, ghi chu} hoac rong. */
    private static Optional<String[]> hoiSoTienMoi(PhaiNop p) {
        Dialog<String[]> d = new Dialog<>();
        // Thuat ngu: "khoan thu" la muc danh muc, cua ho la "khoan phai nop".
        d.setTitle("Sửa khoản phải nộp – " + p.maCanHo());
        d.setHeaderText(p.tenKhoan() + " · giá trị cũ "
                + SoTien.hienThi(p.soTien()));
        ButtonType luu = new ButtonType("Lưu thay đổi",
                ButtonBar.ButtonData.OK_DONE);
        HopThoai.datNut(d, luu);
        // Cung dang SoTien.doc nhan (377.500 đ); CSDL tra ve 377500.00.
        TextField soTien = new TextField(SoTien.hienThi(p.soTien()));
        TextField ghiChu = new TextField(p.ghiChu() == null ? ""
                : p.ghiChu());
        ghiChu.setPromptText("Không bắt buộc");
        GridPane luoi = new GridPane();
        luoi.setHgap(10);
        luoi.setVgap(10);
        luoi.addRow(0, new Label("Số tiền"), soTien);
        luoi.addRow(1, new Label("Ghi chú"), ghiChu);
        if (p.daDong()) {
            // AC-026: khoan da co phieu thu thi khoa o, nhac huy phieu truoc.
            soTien.setDisable(true);
            ghiChu.setDisable(true);
            Label daThu = new Label(CongNoService.LOI_DA_THU);
            daThu.getStyleClass().add("loi");
            daThu.setWrapText(true);
            daThu.setMaxWidth(320);
            luoi.add(daThu, 0, 2, 2, 1);
            d.getDialogPane().lookupButton(luu).setDisable(true);
        }
        d.getDialogPane().setContent(luoi);
        d.setResultConverter(b -> b == luu
                ? new String[] {soTien.getText(), ghiChu.getText()} : null);
        return d.showAndWait();
    }

    /** US-015: thong bao cho ho cua dong dang chon. */
    @FXML
    private void onThongBaoMotHo() {
        PhaiNop p = bangKhoan.getSelectionModel().getSelectedItem();
        if (!dangXemDaChot || p == null) {
            baoLoi("Chọn một dòng của hộ cần in thông báo");
            return;
        }
        try {
            ThongBaoPhi tb = thongBaoService.motHo(PhienLamViec.hienTai(),
                    thangCombo.getValue(), p.hoId());
            inThongBao(List.of(tb));
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    /** US-015: thong bao cho moi ho dang o con khoan can dong. */
    @FXML
    private void onThongBaoTatCa() {
        try {
            List<ThongBaoPhi> ds = thongBaoService.tatCa(
                    PhienLamViec.hienTai(), thangCombo.getValue());
            inThongBao(ds);
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    private void inThongBao(List<ThongBaoPhi> ds) {
        LocalDate homNay = LocalDate.now();
        boolean daIn = InAn.xemVaIn("Thông báo phí " + tenThang().toLowerCase()
                + " (" + ds.size() + " hộ)", () -> ds.stream()
                        .map(tb -> MauIn.thongBao(tb, homNay)).toList());
        if (daIn) {
            baoThanhCong("Đã in thông báo cho " + ds.size() + " hộ");
        }
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
