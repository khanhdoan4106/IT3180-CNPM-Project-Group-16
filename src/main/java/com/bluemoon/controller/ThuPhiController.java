package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.DongHoCsdl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
import com.bluemoon.model.ChiTietPhieuThu;
import com.bluemoon.model.DongGopQuy;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiDongGop;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.SoTien;
import com.bluemoon.service.ThuPhiService;
import com.bluemoon.util.PhienLamViec;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Man hinh thu phi (US-010 den US-014) va ghi nhan dong gop tu nguyen
 * (US-007). Chi chuyen su kien cho service, khong chua nghiep vu.
 */
public class ThuPhiController {

    private static final String CSS = "/com/bluemoon/css/giao-dien.css";
    private static final DateTimeFormatter NGAY_GIO =
            DateTimeFormatter.ofPattern("HH:mm dd/MM/uuuu");

    private final ThuPhiService service = new ThuPhiService(
            new PhieuThuDaoImpl(), new KhoanPhiDaoImpl(), new DongHoCsdl());

    @FXML
    private ToggleGroup hinhThucGroup;
    @FXML
    private Tab phieuTab;
    @FXML
    private TextField maField;
    @FXML
    private ComboBox<TomTatHo> hoCombo;
    @FXML
    private Button lichSuButton;
    @FXML
    private Label hoLabel;
    @FXML
    private Label noLabel;
    @FXML
    private Label khoanTieuDe;
    @FXML
    private Label quyTieuDe;
    @FXML
    private TableView<DongChon> bangNo;
    @FXML
    private TableColumn<DongChon, Boolean> cotChon;
    @FXML
    private TableColumn<DongChon, String> cotThang;
    @FXML
    private TableColumn<DongChon, String> cotKhoan;
    @FXML
    private TableColumn<DongChon, String> cotSoTien;
    @FXML
    private GridPane quyGrid;
    @FXML
    private RadioButton tienMatRadio;
    @FXML
    private RadioButton chuyenKhoanRadio;
    @FXML
    private Label tongLabel;
    @FXML
    private Button thuButton;
    @FXML
    private Label thongBaoLabel;
    @FXML
    private TableView<PhieuThu> bangPhieu;
    @FXML
    private Label thongBaoPhieuLabel;

    /** Ho dang thu, null khi chua tra cuu. */
    private TomTatHo hoDangThu;
    /** O nhap so tien cua tung quy dang mo, theo id quy. */
    private final Map<Integer, TextField> oQuy = new LinkedHashMap<>();

    /** Mot dong khoan chua dong kem o chon. */
    private static final class DongChon {
        private final KhoanNo khoan;
        private final BooleanProperty chon = new SimpleBooleanProperty(true);

        DongChon(KhoanNo khoan) {
            this.khoan = khoan;
        }
    }

    @FXML
    private void initialize() {
        cotChon.setCellValueFactory(o -> o.getValue().chon);
        cotChon.setCellFactory(CheckBoxTableCell.forTableColumn(cotChon));
        cotChon.setEditable(true);
        cotThang.setCellValueFactory(o -> new SimpleStringProperty(
                o.getValue().khoan.thang().getMonthValue() + "/"
                        + o.getValue().khoan.thang().getYear()));
        cotKhoan.setCellValueFactory(o -> new SimpleStringProperty(
                o.getValue().khoan.tenKhoan()));
        cotSoTien.setCellValueFactory(o -> new SimpleStringProperty(
                SoTien.hienThi(o.getValue().khoan.soTien())));
        tienMatRadio.setUserData(HinhThucThanhToan.TIEN_MAT);
        chuyenKhoanRadio.setUserData(HinhThucThanhToan.CHUYEN_KHOAN);
        hoCombo.valueProperty().addListener((o, cu, moi) -> {
            if (moi != null) {
                hienHo(moi.ho().getId());
            }
        });
        cauHinhBangPhieu(bangPhieu);
        phieuTab.setOnSelectionChanged(e -> {
            if (phieuTab.isSelected()) {
                taiPhieuHomNay();
            }
        });
    }

    // ---------------- Thu tien ----------------

    @FXML
    private void onTraCuu() {
        xoaThongBao(thongBaoLabel);
        try {
            List<TomTatHo> ds = service.traCuu(PhienLamViec.hienTai(),
                    maField.getText());
            boolean nhieuHo = ds.size() > 1;
            hoCombo.setVisible(nhieuHo);
            hoCombo.setManaged(nhieuHo);
            hoCombo.setItems(FXCollections.observableArrayList(ds));
            if (nhieuHo) {
                baoThanhCong(thongBaoLabel, "Phòng " + ds.get(0).ho()
                        .getMaCanHo() + " có hộ đã chuyển đi còn nợ: chọn"
                        + " hộ cần thu ở ô bên cạnh.");
            }
            hoCombo.setValue(null);
            hoCombo.setValue(ds.get(0));
        } catch (NghiepVuException e) {
            xoaHo();
            baoLoi(thongBaoLabel, e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(thongBaoLabel, LoiCsdl.thongBao(e));
        }
    }

    /** Hien khoan chua dong va quy dang mo cua ho. */
    private void hienHo(int hoId) {
        try {
            ThuPhiService.ThongTinThu tt = service.thongTin(
                    PhienLamViec.hienTai(), hoId);
            hoDangThu = tt.ho();
            String chuHo = tt.ho().tenChuHo() == null ? ""
                    : " · Chủ hộ " + tt.ho().tenChuHo();
            String trangThai = tt.ho().ho().getTrangThai() == TrangThaiHo.DANG_O
                    ? "Đang ở" : "Đã chuyển đi";
            hoLabel.setText("Căn hộ " + tt.ho().ho().getMaCanHo() + chuHo
                    + " · " + trangThai);
            noLabel.setText("Còn nợ " + SoTien.hienThi(tt.tongNo()));
            khoanTieuDe.setText("Khoản phải nộp chưa đóng ("
                    + tt.khoanChuaDong().size() + ")");
            bangNo.setItems(FXCollections.observableArrayList(
                    tt.khoanChuaDong().stream().map(DongChon::new).toList()));
            bangNo.getItems().forEach(d -> d.chon.addListener(
                    (o, cu, moi) -> capNhatTong()));
            hienQuy(tt.dongGop(), tt.ho().ho().getTrangThai()
                    != TrangThaiHo.DANG_O);
            lichSuButton.setDisable(false);
            capNhatTong();
        } catch (NghiepVuException e) {
            baoLoi(thongBaoLabel, e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(thongBaoLabel, LoiCsdl.thongBao(e));
        }
    }

    private void hienQuy(List<DongGopQuy> dsQuy, boolean daChuyenDi) {
        quyGrid.getChildren().clear();
        oQuy.clear();
        quyTieuDe.setText("Đóng góp tự nguyện (" + dsQuy.size()
                + " quỹ đang mở)");
        if (dsQuy.isEmpty()) {
            Label khongCo = new Label(daChuyenDi
                    ? ThuPhiService.LOI_HO_DA_CHUYEN_DI
                    : "Không có quỹ nào đang mở");
            khongCo.setWrapText(true);
            quyGrid.add(khongCo, 0, 0);
            return;
        }
        int hang = 0;
        for (DongGopQuy q : dsQuy) {
            if (hang > 0) {
                quyGrid.add(new Separator(), 0, hang * 2 - 1, 3, 1);
            }
            Label ten = new Label(q.quy().ten());
            ten.setWrapText(true);
            Label daDong = new Label(tinhTrang(q));
            daDong.getStyleClass().add("phu-de");
            TextField o = new TextField();
            o.setPromptText(q.trangThai() == TrangThaiDongGop.CHUA_THU
                    ? "Số tiền" : "Đóng thêm");
            o.textProperty().addListener((ob, cu, moi) -> capNhatTong());
            oQuy.put(q.quy().id(), o);
            GridPane.setMargin(o, new Insets(8, 0, 8, 0));
            quyGrid.add(ten, 0, hang * 2);
            quyGrid.add(daDong, 1, hang * 2);
            quyGrid.add(o, 2, hang * 2);
            hang++;
        }
    }

    private static String tinhTrang(DongGopQuy q) {
        return q.trangThai() == TrangThaiDongGop.DA_DONG
                ? SoTien.hienThi(q.tongDaDong())
                : q.trangThai().getNhan();
    }

    /** Cong tong de nguoi thu doi chieu truoc khi bam Thu. */
    private void capNhatTong() {
        BigDecimal tong = BigDecimal.ZERO;
        boolean tatCa = !bangNo.getItems().isEmpty();
        boolean coChon = false;
        for (DongChon d : bangNo.getItems()) {
            if (d.chon.get()) {
                tong = tong.add(d.khoan.soTien());
                coChon = true;
            } else {
                tatCa = false;
            }
        }
        boolean soSai = false;
        for (TextField o : oQuy.values()) {
            if (o.getText() == null || o.getText().isBlank()) {
                continue;
            }
            coChon = true;
            try {
                tong = tong.add(SoTien.doc(o.getText(), "Số tiền"));
            } catch (NghiepVuException e) {
                soSai = true;
            }
        }
        tongLabel.setText(soSai ? "Tổng thu: số tiền chưa hợp lệ"
                : "Tổng thu: " + SoTien.hienThi(tong));
        thuButton.setText(tatCa && oQuy.values().stream()
                .allMatch(o -> o.getText().isBlank())
                ? "Thu tất cả" : "Thu các khoản đã chọn");
        thuButton.setDisable(hoDangThu == null || !coChon);
    }

    @FXML
    private void onChonTatCa() {
        bangNo.getItems().forEach(d -> d.chon.set(true));
    }

    @FXML
    private void onBoChonTatCa() {
        bangNo.getItems().forEach(d -> d.chon.set(false));
    }

    @FXML
    private void onThu() {
        if (hoDangThu == null) {
            return;
        }
        List<Integer> chon = bangNo.getItems().stream()
                .filter(d -> d.chon.get()).map(d -> d.khoan.phaiNopId())
                .toList();
        Map<Integer, String> dongGop = new LinkedHashMap<>();
        oQuy.forEach((id, o) -> dongGop.put(id, o.getText()));
        HinhThucThanhToan hinhThuc = hinhThucGroup.getSelectedToggle() == null
                ? null : (HinhThucThanhToan) hinhThucGroup
                        .getSelectedToggle().getUserData();
        String ma = hoDangThu.ho().getMaCanHo();
        if (!HopThoai.xacNhan("Lập phiếu thu cho căn hộ " + ma + "?\n"
                + tongLabel.getText()
                + (hinhThuc == null ? "" : " · " + hinhThuc.getNhan()))) {
            return;
        }
        try {
            PhieuThu p = service.thu(PhienLamViec.hienTai(),
                    hoDangThu.ho().getId(), chon, dongGop, hinhThuc);
            hienHo(hoDangThu.ho().getId());
            hinhThucGroup.selectToggle(null);
            baoThanhCong(thongBaoLabel, "Đã lập phiếu " + p.soBienLai()
                    + (p.khongThuTien() ? " (không thu tiền)"
                            : " · " + SoTien.hienThi(p.tongTien())));
            if (!p.khongThuTien() && hoiInNgay(p)) {
                inBienLai(p.id(), thongBaoLabel);
            }
        } catch (NghiepVuException e) {
            baoLoi(thongBaoLabel, e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(thongBaoLabel, LoiCsdl.thongBao(e));
        }
    }

    private static boolean hoiInNgay(PhieuThu p) {
        ButtonType in = new ButtonType("In biên lai",
                ButtonBar.ButtonData.OK_DONE);
        ButtonType deSau = new ButtonType("Để sau",
                ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Đã lập phiếu " + p.soBienLai() + ", tổng "
                        + SoTien.hienThi(p.tongTien()) + ".", in, deSau);
        a.setTitle("Đã thu tiền");
        a.setHeaderText(null);
        return a.showAndWait().orElse(deSau) == in;
    }

    @FXML
    private void onLichSu() {
        if (hoDangThu == null) {
            return;
        }
        try {
            List<PhieuThu> ds = service.lichSu(PhienLamViec.hienTai(),
                    hoDangThu.ho().getId());
            TableView<PhieuThu> bang = new TableView<>(
                    FXCollections.observableArrayList(ds));
            cauHinhBangPhieu(bang);
            bang.setPlaceholder(new Label("Hộ chưa có phiếu thu"));
            bang.setPrefSize(900, 380);
            Dialog<Void> d = new Dialog<>();
            d.setTitle("Lịch sử thu – căn hộ " + hoDangThu.ho().getMaCanHo());
            d.getDialogPane().setContent(bang);
            d.getDialogPane().getButtonTypes().setAll(HopThoai.DONG);
            d.setResizable(true);
            d.showAndWait();
        } catch (NghiepVuException e) {
            baoLoi(thongBaoLabel, e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(thongBaoLabel, LoiCsdl.thongBao(e));
        }
    }

    private void xoaHo() {
        hoDangThu = null;
        hoLabel.setText("Nhập mã phòng rồi bấm Tra cứu");
        noLabel.setText("");
        khoanTieuDe.setText("Khoản phải nộp chưa đóng (0)");
        quyTieuDe.setText("Đóng góp tự nguyện (0 quỹ đang mở)");
        bangNo.getItems().clear();
        quyGrid.getChildren().clear();
        oQuy.clear();
        hoCombo.setVisible(false);
        hoCombo.setManaged(false);
        lichSuButton.setDisable(true);
        capNhatTong();
    }

    // ---------------- Phieu thu hom nay ----------------

    private void taiPhieuHomNay() {
        try {
            bangPhieu.setItems(FXCollections.observableArrayList(
                    service.homNay(PhienLamViec.hienTai())));
        } catch (NghiepVuException e) {
            baoLoi(thongBaoPhieuLabel, e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(thongBaoPhieuLabel, LoiCsdl.thongBao(e));
        }
    }

    @FXML
    private void onLamMoiPhieu() {
        xoaThongBao(thongBaoPhieuLabel);
        taiPhieuHomNay();
    }

    private PhieuThu phieuDangChon() {
        PhieuThu p = bangPhieu.getSelectionModel().getSelectedItem();
        if (p == null) {
            baoLoi(thongBaoPhieuLabel, "Chọn một phiếu trong bảng");
        }
        return p;
    }

    @FXML
    private void onXemChiTiet() {
        PhieuThu p = phieuDangChon();
        if (p == null) {
            return;
        }
        VBox noiDung = new VBox(6);
        noiDung.getChildren().add(new Label("Căn hộ " + p.maCanHo()
                + (p.tenChuHo() == null ? "" : " · " + p.tenChuHo())));
        noiDung.getChildren().add(new Label("Lập lúc "
                + p.thoiDiem().format(NGAY_GIO) + " · " + p.tenNguoiThu()));
        for (ChiTietPhieuThu d : p.chiTiet()) {
            noiDung.getChildren().add(new Label("• " + d.moTa() + ": "
                    + SoTien.hienThi(d.soTien())));
        }
        noiDung.getChildren().add(new Label("Tổng: "
                + SoTien.hienThi(p.tongTien())
                + (p.hinhThuc() == null ? "" : " · " + p.hinhThuc())));
        if (p.daHuy()) {
            Label huy = new Label("Đã hủy lúc "
                    + p.thoiDiemHuy().format(NGAY_GIO) + " bởi "
                    + p.tenNguoiHuy() + ". Lý do: " + p.lyDoHuy());
            huy.setWrapText(true);
            huy.getStyleClass().add("loi");
            noiDung.getChildren().add(huy);
        }
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Phiếu " + p.soBienLai());
        d.getDialogPane().setContent(noiDung);
        d.getDialogPane().setPrefWidth(480);
        d.getDialogPane().getButtonTypes().setAll(HopThoai.DONG);
        d.showAndWait();
    }

    @FXML
    private void onInBienLai() {
        PhieuThu p = phieuDangChon();
        if (p != null) {
            inBienLai(p.id(), thongBaoPhieuLabel);
            taiPhieuHomNay();
        }
    }

    /** In bien lai; lan thu hai tro di ghi "BAN IN LAI" (AC-040). */
    private void inBienLai(int id, Label baoTai) {
        try {
            PhieuThu p = service.choIn(PhienLamViec.hienTai(), id);
            boolean inLai = p.soLanIn() > 0;
            if (InAn.xemVaIn("Biên lai " + p.soBienLai(),
                    () -> List.of(MauIn.bienLai(p, inLai)))) {
                service.daIn(PhienLamViec.hienTai(), id);
                baoThanhCong(baoTai, "Đã in biên lai " + p.soBienLai()
                        + (inLai ? " (bản in lại)" : ""));
            }
        } catch (NghiepVuException e) {
            baoLoi(baoTai, e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(baoTai, LoiCsdl.thongBao(e));
        }
    }

    @FXML
    private void onHuyPhieu() {
        PhieuThu p = phieuDangChon();
        if (p == null) {
            return;
        }
        String lyDo = hoiLyDoHuy(p);
        if (lyDo == null) {
            return;
        }
        try {
            PhieuThu daHuy = service.huy(PhienLamViec.hienTai(), p.id(),
                    lyDo);
            taiPhieuHomNay();
            if (hoDangThu != null && hoDangThu.ho().getId() == daHuy.hoId()) {
                hienHo(daHuy.hoId());
            }
            baoThanhCong(thongBaoPhieuLabel, "Đã hủy phiếu "
                    + daHuy.soBienLai() + ". Các khoản của phiếu trở lại"
                    + " chưa đóng.");
        } catch (NghiepVuException e) {
            baoLoi(thongBaoPhieuLabel, e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(thongBaoPhieuLabel, LoiCsdl.thongBao(e));
        }
    }

    /** Hop thoai nhap ly do huy; tra ve null neu dong ma khong huy. */
    private static String hoiLyDoHuy(PhieuThu p) {
        Dialog<String> d = new Dialog<>();
        d.setTitle("Hủy phiếu " + p.soBienLai());
        d.setHeaderText("Căn hộ " + p.maCanHo() + " · "
                + SoTien.hienThi(p.tongTien())
                + (p.hinhThuc() == null ? "" : " · " + p.hinhThuc().getNhan())
                + ". Phiếu hủy vẫn được lưu kèm lý do.");
        d.getDialogPane().getStylesheets().add(
                ThuPhiController.class.getResource(CSS).toExternalForm());
        ButtonType huy = new ButtonType("Hủy phiếu",
                ButtonBar.ButtonData.OK_DONE);
        HopThoai.datNutChinhVaDong(d, huy);
        d.getDialogPane().lookupButton(huy).getStyleClass().add("nut-phu");
        Label nhan = new Label("Lý do hủy (bắt buộc)");
        nhan.getStyleClass().add("tieu-de-form");
        TextField lyDo = new TextField();
        lyDo.setPromptText("vd thu nhầm hộ");
        Label luuY = new Label("Chỉ hủy được phiếu lập trong ngày. Thủ quỹ"
                + " chỉ hủy phiếu do mình lập. Các khoản trong phiếu sẽ trở"
                + " lại chưa đóng.");
        luuY.getStyleClass().add("phu-de");
        luuY.setWrapText(true);
        luuY.setPrefWidth(420);
        d.getDialogPane().setContent(new VBox(8, nhan, lyDo, luuY));
        d.setResultConverter(b -> b == huy ? lyDo.getText() : null);
        return d.showAndWait().orElse(null);
    }

    /** Cot chung cho bang phieu hom nay va lich su thu cua ho. */
    private static void cauHinhBangPhieu(TableView<PhieuThu> bang) {
        bang.getColumns().setAll(List.of(
                cot("Số biên lai", 120, PhieuThu::soBienLai),
                cot("Căn hộ", 70, PhieuThu::maCanHo),
                cot("Thời điểm", 125, p -> p.thoiDiem().format(NGAY_GIO)),
                cot("Tổng tiền", 110, p -> SoTien.hienThi(p.tongTien())),
                cot("Hình thức", 105, p -> p.hinhThuc() == null ? "–"
                        : p.hinhThuc().getNhan()),
                cot("Người thu", 130, PhieuThu::tenNguoiThu),
                cot("Trạng thái", 320, ThuPhiController::trangThai)));
        bang.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        bang.setRowFactory(t -> new TableRow<>() {
            @Override
            protected void updateItem(PhieuThu p, boolean trong) {
                super.updateItem(p, trong);
                getStyleClass().remove("phieu-da-huy");
                if (!trong && p != null && p.daHuy()) {
                    getStyleClass().add("phieu-da-huy");
                }
            }
        });
    }

    private static TableColumn<PhieuThu, String> cot(String ten, double rong,
            Function<PhieuThu, String> lay) {
        TableColumn<PhieuThu, String> c = new TableColumn<>(ten);
        c.setPrefWidth(rong);
        c.setCellValueFactory(
                o -> new SimpleStringProperty(lay.apply(o.getValue())));
        return c;
    }

    private static String trangThai(PhieuThu p) {
        if (p.daHuy()) {
            return "Đã hủy (" + p.tenNguoiHuy() + "): " + p.lyDoHuy();
        }
        if (p.khongThuTien()) {
            return "Không thu tiền";
        }
        return p.soLanIn() == 0 ? "Chưa in biên lai"
                : "Đã in " + p.soLanIn() + " lần";
    }

    private static void xoaThongBao(Label l) {
        l.setText("");
    }

    private static void baoLoi(Label l, String noiDung) {
        l.getStyleClass().removeAll("loi", "thanh-cong");
        l.getStyleClass().add("loi");
        l.setText(noiDung);
    }

    private static void baoThanhCong(Label l, String noiDung) {
        l.getStyleClass().removeAll("loi", "thanh-cong");
        l.getStyleClass().add("thanh-cong");
        l.setText(noiDung);
    }
}
