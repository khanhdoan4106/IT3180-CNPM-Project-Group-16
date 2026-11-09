package com.bluemoon.controller;

import com.bluemoon.model.BienDong;
import com.bluemoon.util.NgayUtil;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Hop thoai hien lich su bien dong nhan khau (US-027), dung chung cho man
 * hinh ho gia dinh va trang chinh.
 */
final class BangLichSu {

    private static final DateTimeFormatter GIO =
            DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    private BangLichSu() {
    }

    /**
     * Tao bang lich su.
     *
     * @param ds cac bien dong
     * @param coCanHo true de them cot can ho (xem toan chung cu)
     * @return bang da gan du lieu
     */
    static TableView<BienDong> taoBang(List<BienDong> ds, boolean coCanHo) {
        TableView<BienDong> bang = new TableView<>(
                FXCollections.observableArrayList(ds));
        bang.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);
        bang.setPlaceholder(new Label("Chưa có biến động nào"));
        them(bang, "Ngày", b -> NgayUtil.hien(b.ngay()));
        if (coCanHo) {
            them(bang, "Căn hộ", BienDong::maCanHo);
        }
        them(bang, "Họ tên", BienDong::hoTen);
        them(bang, "Biến động", b -> b.loai().getNhan());
        them(bang, "Ghi chú", b -> b.ghiChu() == null ? "" : b.ghiChu());
        them(bang, "Người ghi", b -> b.nguoiGhi() + " · "
                + b.thoiDiem().format(GIO));
        return bang;
    }

    /**
     * Hien hop thoai lich su.
     *
     * @param tieuDe tieu de hop thoai
     * @param ds cac bien dong, moi nhat truoc
     */
    static void hien(String tieuDe, List<BienDong> ds) {
        Dialog<Void> d = new Dialog<>();
        d.setTitle(tieuDe);
        d.setHeaderText(tieuDe);
        TableView<BienDong> bang = taoBang(ds, false);
        bang.setPrefSize(820, 360);
        d.getDialogPane().setContent(bang);
        d.getDialogPane().getButtonTypes().add(HopThoai.DONG);
        d.showAndWait();
    }

    private static void them(TableView<BienDong> bang, String ten,
            Function<BienDong, String> lay) {
        TableColumn<BienDong, String> cot = new TableColumn<>(ten);
        cot.setCellValueFactory(
                o -> new SimpleStringProperty(lay.apply(o.getValue())));
        bang.getColumns().add(cot);
    }
}
