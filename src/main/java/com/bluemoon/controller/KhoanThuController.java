package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.KhoanPhiService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.SoTien;
import com.bluemoon.util.NgayUtil;
import com.bluemoon.util.PhienLamViec;
import java.util.function.Function;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Man hinh khoan thu (US-005, US-006). Thu quy chi xem danh muc; Quan tri
 * vien doi don gia va tao khoan tu nguyen. Service kiem tra lai quyen.
 */
public class KhoanThuController {

    private final KhoanPhiService service =
            new KhoanPhiService(new KhoanPhiDaoImpl());

    @FXML
    private TableView<KhoanPhi> bangKhoan;
    @FXML
    private TableColumn<KhoanPhi, String> cotTen;
    @FXML
    private TableColumn<KhoanPhi, String> cotLoai;
    @FXML
    private TableColumn<KhoanPhi, String> cotMucThu;
    @FXML
    private TableColumn<KhoanPhi, String> cotThoiGian;
    @FXML
    private TableColumn<KhoanPhi, String> cotTrangThai;
    @FXML
    private HBox suaDonGiaBox;
    @FXML
    private Label dangChonLabel;
    @FXML
    private TextField donGiaField;
    @FXML
    private VBox taoBox;
    @FXML
    private TextField tenField;
    @FXML
    private DatePicker tuNgayPicker;
    @FXML
    private DatePicker denNgayPicker;
    @FXML
    private TextField soTienField;
    @FXML
    private Label thongBaoLabel;

    @FXML
    private void initialize() {
        ganCot(cotTen, KhoanPhi::ten);
        ganCot(cotLoai, k -> k.loai().getNhan());
        ganCot(cotMucThu, KhoanThuController::mucThu);
        ganCot(cotThoiGian, KhoanThuController::thoiGian);
        ganCot(cotTrangThai, k -> k.trangThai().getNhan());
        TaiKhoan tk = PhienLamViec.hienTai();
        boolean quanTri = tk != null && tk.getVaiTro() == VaiTro.QUAN_TRI;
        for (Node n : new Node[] {suaDonGiaBox, taoBox}) {
            n.setVisible(quanTri);
            n.setManaged(quanTri);
        }
        NgayUtil.ganDinhDang(tuNgayPicker);
        NgayUtil.ganDinhDang(denNgayPicker);
        bangKhoan.getSelectionModel().selectedItemProperty().addListener(
                (o, cu, moi) -> chon(moi));
        taiLai();
    }

    private void chon(KhoanPhi k) {
        if (k == null || k.cachTinh() != CachTinhPhi.THEO_DIEN_TICH) {
            dangChonLabel.setText("Chọn phí dịch vụ hoặc phí quản lý"
                    + " để sửa đơn giá");
            donGiaField.clear();
            return;
        }
        dangChonLabel.setText("Đang chọn: " + k.ten() + ". Phí dịch vụ "
                + "2.500 – 16.500 đ/m², phí quản lý từ 7.000 đ/m².");
        // Cung dang SoTien.doc nhan (5.000 đ); CSDL tra ve 5000.00.
        donGiaField.setText(SoTien.hienThi(k.donGia()));
    }

    @FXML
    private void onSuaDonGia() {
        KhoanPhi k = bangKhoan.getSelectionModel().getSelectedItem();
        if (k == null) {
            baoLoi("Bấm chọn khoản phí dịch vụ hoặc phí quản lý trong bảng trước");
            return;
        }
        try {
            KhoanPhi moi = service.suaDonGia(PhienLamViec.hienTai(), k.id(),
                    donGiaField.getText());
            taiLai();
            baoThanhCong("Đã đổi đơn giá " + moi.ten() + " thành "
                    + SoTien.hienThi(moi.donGia()) + "/m². Các tháng đã chốt"
                    + " giữ nguyên số cũ.");
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    @FXML
    private void onTao() {
        String loiNgay = NgayUtil.loiDauTien(
                NgayUtil.loiCuaO(tuNgayPicker, "Từ ngày"),
                NgayUtil.loiCuaO(denNgayPicker, "Đến ngày"));
        if (loiNgay != null) {
            baoLoi(loiNgay);
            return;
        }
        try {
            KhoanPhi k = service.taoKhoanTuNguyen(PhienLamViec.hienTai(),
                    tenField.getText(), NgayUtil.doc(tuNgayPicker),
                    NgayUtil.doc(denNgayPicker), soTienField.getText());
            onNhapMoi();
            taiLai();
            baoThanhCong("Đã tạo khoản thu " + k.ten());
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    @FXML
    private void onNhapMoi() {
        tenField.clear();
        tuNgayPicker.setValue(null);
        denNgayPicker.setValue(null);
        soTienField.clear();
    }

    private void taiLai() {
        try {
            bangKhoan.setItems(FXCollections.observableArrayList(
                    service.danhSach(PhienLamViec.hienTai())));
        } catch (NghiepVuException e) {
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    private static String mucThu(KhoanPhi k) {
        return switch (k.cachTinh()) {
            case THEO_DIEN_TICH -> SoTien.hienThi(k.donGia()) + "/m²";
            case CO_DINH_THEO_HO -> SoTien.hienThi(k.donGia()) + "/hộ";
            case KHONG_CO_DINH -> "Không cố định";
        };
    }

    private static String thoiGian(KhoanPhi k) {
        if (k.denNgay() == null) {
            return "Hằng tháng";
        }
        return NgayUtil.hien(k.tuNgay()) + " – "
                + NgayUtil.hien(k.denNgay());
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

    private static void ganCot(TableColumn<KhoanPhi, String> cot,
            Function<KhoanPhi, String> lay) {
        cot.setCellValueFactory(
                o -> new SimpleStringProperty(lay.apply(o.getValue())));
    }
}
