package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.BienDongDaoImpl;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.DongHoCsdl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.TaiKhoanDaoImpl;
import com.bluemoon.model.BienDong;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.BienDongService;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.TaiKhoanService;
import com.bluemoon.util.DieuHuong;
import com.bluemoon.util.PhienLamViec;
import com.bluemoon.util.ThanhMenu;
import java.time.YearMonth;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Khung man hinh chinh (layout: dau - than). Menu chi hien chuc nang vai
 * tro duoc dung (US-022); service van kiem tra lai quyen.
 */
public class TrangChinhController {

    private final TaiKhoanService taiKhoanService =
            new TaiKhoanService(new TaiKhoanDaoImpl());

    @FXML
    private Label chaoLabel;
    @FXML
    private StackPane noiDung;
    @FXML
    private Button hoGiaDinhButton;
    @FXML
    private Button nhanKhauButton;
    @FXML
    private Button khoanThuButton;
    @FXML
    private Button congNoButton;
    @FXML
    private Button thuPhiButton;
    @FXML
    private Button taiKhoanButton;
    @FXML
    private HBox nhacChotBox;
    @FXML
    private Label nhacChotLabel;
    @FXML
    private VBox bienDongBox;

    /** Nut menu cua man hinh dang mo (nen dam), khong nut nao o Trang chinh. */
    private ThanhMenu menu;
    /** Noi dung Trang chinh luc moi vao, de hien lai khi bam logo. */
    private List<Node> trangChu;

    @FXML
    private void initialize() {
        TaiKhoan tk = PhienLamViec.hienTai();
        if (tk != null) {
            chaoLabel.setText("Xin chào, " + tk.getHoTen() + " ("
                    + tk.getVaiTro().getNhan() + ")");
            // Ten dai co the bi rut gon "..." tren thanh menu: hien du
            // noi dung khi re chuot.
            chaoLabel.setTooltip(new Tooltip(chaoLabel.getText()));
        }
        // AC-060: Thu quy khong dung ho gia dinh, nhan khau. Muc Tai khoan
        // ai cung thay (US-023); tab quan ly chi Quan tri vien thay.
        boolean quanTri = tk != null && tk.getVaiTro() == VaiTro.QUAN_TRI;
        for (Node n : new Node[] {hoGiaDinhButton, nhanKhauButton}) {
            n.setVisible(quanTri);
            n.setManaged(quanTri);
        }
        menu = new ThanhMenu(hoGiaDinhButton, nhanKhauButton, khoanThuButton,
                congNoButton, thuPhiButton, taiKhoanButton);
        trangChu = List.copyOf(noiDung.getChildren());
        nhacChotCongNo();
        if (quanTri) {
            hienBienDongGanDay();
        }
    }

    /**
     * Bam logo: hien lai Trang chinh, bo chon moi nut menu, lam moi nhac chot
     * va bien dong gan day.
     */
    @FXML
    private void onVeTrangChu() {
        noiDung.getChildren().setAll(trangChu);
        menu.boChonTatCa();
        nhacChotCongNo();
        TaiKhoan tk = PhienLamViec.hienTai();
        if (tk != null && tk.getVaiTro() == VaiTro.QUAN_TRI) {
            hienBienDongGanDay();
        }
    }

    /** US-027: vai bien dong moi nhat cho Quan tri vien nam nhanh. */
    private void hienBienDongGanDay() {
        try {
            List<BienDong> ds = new BienDongService(new BienDongDaoImpl())
                    .ganDay(PhienLamViec.hienTai());
            TableView<BienDong> bang = BangLichSu.taoBang(ds, true);
            bang.setPrefHeight(200);
            // Giu tieu de (phan tu dau), bo bang cu khi lam moi.
            if (bienDongBox.getChildren().size() > 1) {
                bienDongBox.getChildren().remove(1,
                        bienDongBox.getChildren().size());
            }
            bienDongBox.getChildren().add(bang);
            bienDongBox.setVisible(true);
            bienDongBox.setManaged(true);
        } catch (NghiepVuException | CauHinhException | DaoException e) {
            // Khong hien duoc thi bo qua; man hinh chuc nang se bao loi.
        }
    }

    /** Thay lich tu chay: nhac khi da sang thang moi ma chua chot. */
    private void nhacChotCongNo() {
        try {
            CongNoService congNo = new CongNoService(new CongNoDaoImpl(),
                    new KhoanPhiDaoImpl(), new HoGiaDinhDaoImpl(),
                    new DongHoCsdl());
            if (congNo.canNhacChot(PhienLamViec.hienTai())) {
                YearMonth t = congNo.thangHienTai();
                nhacChotLabel.setText("Tháng " + t.getMonthValue() + "/"
                        + t.getYear() + " chưa chốt công nợ. Chốt để tạo"
                        + " khoản phải nộp cho các hộ đang ở.");
                nhacChotBox.setVisible(true);
                nhacChotBox.setManaged(true);
            } else {
                nhacChotBox.setVisible(false);
                nhacChotBox.setManaged(false);
            }
        } catch (NghiepVuException | CauHinhException | DaoException e) {
            // Khong nhac duoc thi bo qua; man hinh chuc nang se bao loi.
        }
    }

    @FXML
    private void onMoHoGiaDinh() {
        mo("ho-gia-dinh.fxml", hoGiaDinhButton);
    }

    @FXML
    private void onMoNhanKhau() {
        mo("nhan-khau.fxml", nhanKhauButton);
    }

    @FXML
    private void onMoKhoanThu() {
        mo("khoan-thu.fxml", khoanThuButton);
    }

    @FXML
    private void onMoCongNo() {
        mo("cong-no.fxml", congNoButton);
    }

    @FXML
    private void onMoThuPhi() {
        mo("thu-phi.fxml", thuPhiButton);
    }

    @FXML
    private void onMoTaiKhoan() {
        mo("tai-khoan.fxml", taiKhoanButton);
    }

    /**
     * Mo man hinh chuc nang sau khi doc lai tai khoan tu CSDL: bi khoa thi
     * dang xuat, doi vai tro thi dung lai menu theo vai tro moi.
     */
    private void mo(String tenFile, Button nut) {
        TaiKhoan cu = PhienLamViec.hienTai();
        try {
            TaiKhoan moi = taiKhoanService.lamMoiPhien(cu);
            PhienLamViec.dat(moi);
            if (cu.getVaiTro() != moi.getVaiTro()) {
                DieuHuong.hienTrangChinh();
                return;
            }
        } catch (NghiepVuException e) {
            HopThoai.canhBao(e.getMessage());
            onDangXuat();
            return;
        } catch (CauHinhException | DaoException e) {
            // Mat ket noi: van mo man hinh, man hinh se bao loi CSDL.
        }
        noiDung.getChildren().setAll(DieuHuong.taiManHinh(tenFile));
        menu.chon(nut);
    }

    @FXML
    private void onDangXuat() {
        PhienLamViec.xoa();
        DieuHuong.hienDangNhap();
    }
}
