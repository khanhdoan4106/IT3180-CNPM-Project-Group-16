package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.BaoCaoThuDaoImpl;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.DongHoCsdl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.ThongKeDaoImpl;
import com.bluemoon.model.BaoCaoThu;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.ThongKeKhoan;
import com.bluemoon.service.BaoCaoThuService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.SoTien;
import com.bluemoon.service.ThongKeService;
import com.bluemoon.util.NgayUtil;
import com.bluemoon.util.PhienLamViec;
import com.bluemoon.xuat.BaoCaoThuExcel;
import java.io.File;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

/**
 * Man Thong ke (US-019, AC-052, AC-053): tong thu cua mot khoan theo dot
 * (gia dinh 45). Chi chuyen su kien cho service.
 */
public class ThongKeController {

    static final String CHUA_CHOT = "Chưa có tháng nào được chốt công nợ, nên"
            + " chưa có số liệu cho khoản bắt buộc. Chốt công nợ ở màn"
            + " Công nợ trước.";

    /** So thang gan nhat trong o chon thang cua bao cao thu. */
    static final int SO_THANG_BAO_CAO = 12;

    private final DongHoCsdl dongHo = new DongHoCsdl();
    private final ThongKeService service = new ThongKeService(
            new ThongKeDaoImpl(), new KhoanPhiDaoImpl(), dongHo);
    private final BaoCaoThuService baoCaoService =
            new BaoCaoThuService(new BaoCaoThuDaoImpl(), dongHo);

    @FXML
    private ComboBox<KhoanPhi> khoanCombo;
    @FXML
    private VBox thangBox;
    @FXML
    private ComboBox<YearMonth> thangCombo;
    @FXML
    private VBox khoangBox;
    @FXML
    private Label khoangLabel;
    @FXML
    private Button xemButton;
    @FXML
    private Label thongBaoLabel;
    @FXML
    private VBox ketQuaBox;
    @FXML
    private Label ketQuaTieuDeLabel;
    @FXML
    private Label tongThuLabel;
    @FXML
    private Label phu1Label;
    @FXML
    private Label phu2Label;
    @FXML
    private ComboBox<YearMonth> baoCaoThangCombo;
    @FXML
    private Button xuatExcelButton;
    @FXML
    private Label baoCaoLabel;

    @FXML
    private void initialize() {
        khoanCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(KhoanPhi k) {
                return k == null ? "" : tenKhoan(k);
            }

            @Override
            public KhoanPhi fromString(String s) {
                return null;
            }
        });
        thangCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(YearMonth t) {
                return t == null ? "" : tenThang(t);
            }

            @Override
            public YearMonth fromString(String s) {
                return null;
            }
        });
        khoiTaoBaoCao();
        try {
            List<KhoanPhi> khoan = service.cacKhoan(PhienLamViec.hienTai());
            List<YearMonth> thang =
                    service.thangDaChot(PhienLamViec.hienTai());
            khoanCombo.setItems(FXCollections.observableArrayList(khoan));
            thangCombo.setItems(FXCollections.observableArrayList(thang));
            if (!thang.isEmpty()) {
                thangCombo.setValue(thang.get(0));
            }
            if (khoan.isEmpty()) {
                xemButton.setDisable(true);
                baoLoi("Chưa có khoản thu nào.");
                return;
            }
            khoanCombo.valueProperty().addListener((o, cu, moi) -> doiKhoan());
            thangCombo.valueProperty().addListener((o, cu, moi) -> anKetQua());
            khoanCombo.setValue(khoan.get(0));
        } catch (NghiepVuException e) {
            xemButton.setDisable(true);
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            xemButton.setDisable(true);
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    /**
     * US-018: o chon thang cua bao cao thu, mac dinh thang hien tai theo
     * gio CSDL.
     */
    private void khoiTaoBaoCao() {
        baoCaoThangCombo.setConverter(thangCombo.getConverter());
        YearMonth hienTai;
        try {
            hienTai = baoCaoService.thangHienTai();
        } catch (CauHinhException | DaoException e) {
            xuatExcelButton.setDisable(true);
            baoLoiBaoCao(LoiCsdl.thongBao(e));
            return;
        }
        List<YearMonth> ds = new ArrayList<>();
        for (int i = 0; i < SO_THANG_BAO_CAO; i++) {
            ds.add(hienTai.minusMonths(i));
        }
        baoCaoThangCombo.setItems(FXCollections.observableArrayList(ds));
        baoCaoThangCombo.setValue(hienTai);
    }

    /**
     * Xuat bao cao thu (AC-050). Thang chua co phieu thi bao ngay, khong
     * mo hop chon tep (AC-051).
     */
    @FXML
    private void onXuatExcel() {
        baoCaoLabel.getStyleClass().removeAll("loi", "thanh-cong");
        baoCaoLabel.setText("");
        try {
            BaoCaoThu bc = baoCaoService.baoCao(PhienLamViec.hienTai(),
                    baoCaoThangCombo.getValue());
            FileChooser chon = new FileChooser();
            chon.setTitle("Lưu báo cáo thu");
            chon.setInitialFileName(BaoCaoThuExcel.tenTep(bc.thang()));
            chon.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Tệp Excel (*.xlsx)", "*.xlsx"));
            File tep = chon.showSaveDialog(
                    xuatExcelButton.getScene().getWindow());
            if (tep == null) {
                return;
            }
            baoCaoService.xuat(PhienLamViec.hienTai(), bc, tep.toPath());
            baoCaoLabel.getStyleClass().add("thanh-cong");
            baoCaoLabel.setText("Đã xuất " + bc.dong().size()
                    + " phiếu thu ra " + tep.getName());
        } catch (NghiepVuException e) {
            baoLoiBaoCao(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            baoLoiBaoCao(LoiCsdl.thongBao(e));
        }
    }

    private void baoLoiBaoCao(String noiDung) {
        baoCaoLabel.getStyleClass().removeAll("loi", "thanh-cong");
        baoCaoLabel.getStyleClass().add("loi");
        baoCaoLabel.setText(noiDung);
    }

    /** Doi khoan: bat buoc thi chon thang, tu nguyen thi hien khoang thu. */
    private void doiKhoan() {
        anKetQua();
        KhoanPhi k = khoanCombo.getValue();
        boolean batBuoc = k != null && k.loai() == LoaiKhoanPhi.BAT_BUOC;
        hien(thangBox, batBuoc);
        hien(khoangBox, !batBuoc);
        thongBaoLabel.setText("");
        if (!batBuoc && k != null) {
            khoangLabel.setText(khoangThu(k));
        }
        boolean thieuThang = batBuoc && thangCombo.getItems().isEmpty();
        xemButton.setDisable(thieuThang);
        if (thieuThang) {
            baoLoi(CHUA_CHOT);
        }
    }

    @FXML
    private void onXem() {
        thongBaoLabel.setText("");
        try {
            ThongKeKhoan tk = service.theoKhoan(PhienLamViec.hienTai(),
                    khoanCombo.getValue(), thangCombo.getValue());
            hienKetQua(tk);
        } catch (NghiepVuException e) {
            anKetQua();
            baoLoi(e.getMessage());
        } catch (CauHinhException | DaoException e) {
            anKetQua();
            baoLoi(LoiCsdl.thongBao(e));
        }
    }

    private void hienKetQua(ThongKeKhoan tk) {
        tongThuLabel.setText(SoTien.hienThi(tk.tongThu()));
        if (tk.batBuoc()) {
            ketQuaTieuDeLabel.setText(tk.khoan().ten() + " – "
                    + tenThang(tk.thang()).toLowerCase());
            phu1Label.setText("Tổng phải thu: "
                    + SoTien.hienThi(tk.tongPhaiThu()));
            phu2Label.setText("Hộ đã đóng: " + tk.soHoDaDong() + "/"
                    + tk.soHoPhaiDong() + " hộ");
        } else {
            ketQuaTieuDeLabel.setText(tk.khoan().ten() + " – "
                    + khoangThu(tk.khoan()).toLowerCase());
            phu1Label.setText("Hộ đã đóng góp: " + tk.soHoDaDong() + " hộ");
            phu2Label.setText("Hộ ghi không đóng (0 đ): "
                    + tk.soHoKhongDong() + " hộ");
        }
        hien(ketQuaBox, true);
    }

    private void anKetQua() {
        hien(ketQuaBox, false);
    }

    static String tenKhoan(KhoanPhi k) {
        return k.ten() + (k.loai() == LoaiKhoanPhi.BAT_BUOC ? " (bắt buộc)"
                : " (tự nguyện)");
    }

    static String tenThang(YearMonth t) {
        return "Tháng " + t.getMonthValue() + "/" + t.getYear();
    }

    /** "Thu từ 01/10/2026 đến 31/12/2026"; khoản chưa có ngày kết thúc. */
    static String khoangThu(KhoanPhi k) {
        return "Thu từ " + NgayUtil.hien(k.tuNgay()) + (k.denNgay() == null
                ? ", chưa có ngày kết thúc"
                : " đến " + NgayUtil.hien(k.denNgay()));
    }

    private static void hien(Node n, boolean co) {
        n.setVisible(co);
        n.setManaged(co);
    }

    private void baoLoi(String noiDung) {
        thongBaoLabel.getStyleClass().remove("loi");
        thongBaoLabel.getStyleClass().add("loi");
        thongBaoLabel.setText(noiDung);
    }
}
