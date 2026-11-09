package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.BaoCaoNhanKhauDaoImpl;
import com.bluemoon.dao.DaoException;
import com.bluemoon.model.TamVang;
import com.bluemoon.service.BaoCaoNhanKhauService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.util.NgayUtil;
import com.bluemoon.util.PhienLamViec;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.print.PageOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Hop thoai danh sach nhan khau tam vang de cung cap cho cong an phuong
 * (US-004): chon ngay, xem, in. Xuat tep Excel/PDF (AC-014) o Sprint 4.
 */
final class DanhSachTamVang {

    private final BaoCaoNhanKhauService service =
            new BaoCaoNhanKhauService(new BaoCaoNhanKhauDaoImpl());
    private final DatePicker ngayPicker = new DatePicker(LocalDate.now());
    private final TableView<TamVang> bang = new TableView<>();
    private final Label thongBao = new Label();
    private final Button inButton = new Button("In danh sách");
    private List<TamVang> dangXem = List.of();

    private DanhSachTamVang() {
        NgayUtil.ganDinhDang(ngayPicker);
        bang.getColumns().setAll(List.of(
                cot("Họ và tên", 170, TamVang::hoTen),
                cot("Ngày sinh", 95, v -> NgayUtil.hien(v.ngaySinh())),
                cot("Căn hộ", 75, TamVang::maCanHo),
                cot("Từ ngày", 95, v -> NgayUtil.hien(v.tuNgay())),
                cot("Đến ngày", 95, v -> NgayUtil.hien(v.denNgay())),
                cot("Lý do", 180, v -> v.lyDo() == null ? "" : v.lyDo())));
        bang.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bang.setPlaceholder(new Label("Chọn ngày rồi bấm Xem"));
        bang.setPrefSize(760, 360);
        thongBao.setWrapText(true);
        inButton.setDisable(true);
        inButton.setMinWidth(Region.USE_PREF_SIZE);
        inButton.setOnAction(e -> in());
    }

    /** Mo hop thoai. */
    static void mo() {
        new DanhSachTamVang().hien();
    }

    private void hien() {
        Button xem = new Button("Xem");
        xem.setMinWidth(Region.USE_PREF_SIZE);
        xem.setDefaultButton(true);
        xem.setOnAction(e -> xem());
        Region dem = new Region();
        HBox.setHgrow(dem, Priority.ALWAYS);
        Label nhanNgay = new Label("Tạm vắng vào ngày");
        nhanNgay.setMinWidth(Region.USE_PREF_SIZE);
        HBox thanh = new HBox(10, nhanNgay, ngayPicker, xem, dem, inButton);
        thanh.setAlignment(Pos.CENTER_LEFT);
        Dialog<Void> d = new Dialog<>();
        d.setTitle("Danh sách nhân khẩu tạm vắng");
        d.getDialogPane().setContent(new VBox(10, thanh, bang, thongBao));
        d.getDialogPane().getButtonTypes().setAll(HopThoai.DONG);
        d.setResizable(true);
        xem();
        d.showAndWait();
    }

    private void xem() {
        thongBao.getStyleClass().remove("loi");
        thongBao.setText("");
        if (NgayUtil.saiDinhDang(ngayPicker)) {
            baoLoi(NgayUtil.loi("Ngày"));
            return;
        }
        try {
            dangXem = service.tamVang(PhienLamViec.hienTai(),
                    NgayUtil.doc(ngayPicker));
            bang.setItems(FXCollections.observableArrayList(dangXem));
            thongBao.setText(dangXem.size() + " người tạm vắng");
            inButton.setDisable(false);
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    private void in() {
        List<TamVang> ds = dangXem;
        LocalDate ngay = NgayUtil.doc(ngayPicker);
        InAn.xemVaIn("Danh sách tạm vắng", PageOrientation.LANDSCAPE,
                () -> MauIn.danhSachTamVang(ds, ngay));
    }

    private void baoLoi(String noiDung) {
        dangXem = List.of();
        bang.getItems().clear();
        inButton.setDisable(true);
        thongBao.getStyleClass().add("loi");
        thongBao.setText(noiDung);
    }

    private static TableColumn<TamVang, String> cot(String ten, double rong,
            Function<TamVang, String> lay) {
        TableColumn<TamVang, String> c = new TableColumn<>(ten);
        c.setPrefWidth(rong);
        c.setCellValueFactory(
                o -> new SimpleStringProperty(lay.apply(o.getValue())));
        return c;
    }
}
