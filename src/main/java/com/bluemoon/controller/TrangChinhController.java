package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.BienDongDaoImpl;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.DongHoCsdl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.NoDongDao.TomTatNo;
import com.bluemoon.dao.NoDongDaoImpl;
import com.bluemoon.dao.TaiKhoanDaoImpl;
import com.bluemoon.dao.ThongKeDaoImpl;
import com.bluemoon.model.BienDong;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.BienDongService;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.NghiepVuException;
import com.bluemoon.service.NoDongService;
import com.bluemoon.service.SoTien;
import com.bluemoon.service.TaiKhoanService;
import com.bluemoon.service.ThongKeService;
import com.bluemoon.util.DieuHuong;
import com.bluemoon.util.PhienLamViec;
import com.bluemoon.util.ThanhMenu;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
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
    /**
     * Tai man hinh chuc nang theo ten tep FXML. Test thay bang ham tai FXML
     * khong controller de khong dung CSDL.
     */
    static Function<String, Parent> taiManHinh = DieuHuong::taiManHinh;

    /** Gio CSDL (gia dinh 38), dung chung cho nhac chot va so lieu thu. */
    private final DongHoCsdl dongHo = new DongHoCsdl();

    @FXML
    private Label tenNguoiDungLabel;
    @FXML
    private Label vaiTroLabel;
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
    private Button thongKeButton;
    @FXML
    private Button taiKhoanButton;
    @FXML
    private HBox nhacChotBox;
    @FXML
    private Label nhacChotLabel;
    @FXML
    private VBox bienDongBox;
    @FXML
    private Label tieuDeLabel;
    @FXML
    private Label daThuLabel;
    @FXML
    private Label tyLeLabel;
    @FXML
    private BieuDoThuPhi bieuDo;
    @FXML
    private Label chuThichLabel;
    @FXML
    private Label soHoNoLabel;
    @FXML
    private Label noDongLabel;

    /**
     * Ten va vai tro nguoi dung tren thanh menu (Figma "Nguoi dung"): hai
     * dong can phai, khong "Xin chao". Ten qua dai thi rut gon "...", tooltip
     * tren ca hai dong hien du "Ten – Vai tro".
     *
     * @param ten nhan ten
     * @param vaiTro nhan vai tro
     * @param tk tai khoan dang nhap, null thi de trong
     */
    static void hienNguoiDung(Label ten, Label vaiTro, TaiKhoan tk) {
        if (tk == null) {
            ten.setText("");
            vaiTro.setText("");
            ten.setTooltip(null);
            vaiTro.setTooltip(null);
            return;
        }
        ten.setText(tk.getHoTen());
        vaiTro.setText(tk.getVaiTro().getNhan());
        Tooltip du = new Tooltip(tk.getHoTen() + " – "
                + tk.getVaiTro().getNhan());
        ten.setTooltip(du);
        vaiTro.setTooltip(du);
    }

    /** Nut menu cua man hinh dang mo (nen dam), khong nut nao o Trang chinh. */
    private ThanhMenu menu;
    /** Noi dung Trang chinh luc moi vao, de hien lai khi bam logo. */
    private List<Node> trangChu;

    @FXML
    private void initialize() {
        TaiKhoan tk = PhienLamViec.hienTai();
        hienNguoiDung(tenNguoiDungLabel, vaiTroLabel, tk);
        // AC-060: Thu quy khong dung ho gia dinh, nhan khau. Muc Tai khoan
        // ai cung thay (US-023); tab quan ly chi Quan tri vien thay.
        boolean quanTri = tk != null && tk.getVaiTro() == VaiTro.QUAN_TRI;
        for (Node n : new Node[] {hoGiaDinhButton, nhanKhauButton}) {
            n.setVisible(quanTri);
            n.setManaged(quanTri);
        }
        menu = new ThanhMenu(hoGiaDinhButton, nhanKhauButton, khoanThuButton,
                congNoButton, thuPhiButton, thongKeButton, taiKhoanButton);
        trangChu = List.copyOf(noiDung.getChildren());
        hienTongQuan();
        nhacChotCongNo();
        if (quanTri) {
            hienBienDongGanDay();
        }
    }

    /**
     * Bam logo: hien lai Trang chinh, bo chon moi nut menu, lam moi so lieu
     * thu, nhac chot va bien dong gan day. Khong dung luong nen hay hen gio:
     * so lieu chi doc lai khi vao Trang chinh.
     */
    @FXML
    private void onVeTrangChu() {
        noiDung.getChildren().setAll(trangChu);
        menu.boChonTatCa();
        hienTongQuan();
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
            TableView<BienDong> bang = BangLichSu.taoBangGon(ds);
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

    /**
     * US-016: tong thu thang hien tai (gia dinh 24), ty le thu cua cong no
     * thang nay va bieu do 6 thang. Chua dang nhap (test) thi bo qua.
     */
    private void hienTongQuan() {
        TaiKhoan tk = PhienLamViec.hienTai();
        if (tk == null) {
            return;
        }
        try {
            ThongKeService.TongQuan tq = new ThongKeService(
                    new ThongKeDaoImpl(), new KhoanPhiDaoImpl(), dongHo)
                    .tongQuan(tk);
            YearMonth t = tq.thang();
            tieuDeLabel.setText("Tổng quan tháng " + t.getMonthValue() + "/"
                    + t.getYear());
            daThuLabel.setText(SoTien.hienThi(tq.daThu()));
            tyLeLabel.setText(dongTyLe(tq));
            bieuDo.hien(tq.sauThang(), t);
            chuThichLabel.setText("Cột đậm: tháng " + t.getMonthValue()
                    + " chưa kết thúc, số liệu tính đến hôm nay.");
        } catch (NghiepVuException | CauHinhException | DaoException e) {
            daThuLabel.setText("–");
            tyLeLabel.setText("Chưa đọc được số liệu. Bấm BlueMoon để thử lại.");
        }
        try {
            TomTatNo no = new NoDongService(new NoDongDaoImpl()).tomTat(tk);
            soHoNoLabel.setText(String.valueOf(no.soHoConNo()));
            noDongLabel.setText(dongNoDong(no));
        } catch (NghiepVuException | CauHinhException | DaoException e) {
            soHoNoLabel.setText("–");
            noDongLabel.setText("Chưa đọc được số liệu. Bấm BlueMoon để thử lại.");
        }
    }

    /** Dong phu cua the "Ho con no" (US-017). */
    static String dongNoDong(TomTatNo no) {
        return no.soHoNoDong() == 0
                ? "Không hộ nào nợ từ " + NoDongService.NGUONG_NO_DONG
                        + " tháng"
                : no.soHoNoDong() + " hộ nợ từ " + NoDongService.NGUONG_NO_DONG
                        + " tháng trở lên";
    }

    /** Dong phu cua the "Da thu thang nay". */
    static String dongTyLe(ThongKeService.TongQuan tq) {
        int thang = tq.thang().getMonthValue();
        if (!tq.daChot()) {
            return "Tháng " + thang + " chưa chốt công nợ";
        }
        if (tq.phanTram() == null) {
            return "Tháng " + thang + " không có khoản phải thu";
        }
        return tq.phanTram() + "% tổng phải thu";
    }

    /** Thay lich tu chay: nhac khi da sang thang moi ma chua chot. */
    private void nhacChotCongNo() {
        try {
            CongNoService congNo = new CongNoService(new CongNoDaoImpl(),
                    new KhoanPhiDaoImpl(), new HoGiaDinhDaoImpl(), dongHo);
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

    /**
     * Bam the "Ho con no": mo man Cong no o tab "Ho no dong", menu Cong no
     * o trang thai dang chon.
     */
    @FXML
    private void onMoNoDong() {
        if (lamMoiPhien()) {
            moNoDong();
        }
    }

    /** Mo man Cong no o tab "Ho no dong" (khong kiem tra lai phien). */
    void moNoDong() {
        CongNoController.chonTabNoDong(hien("cong-no.fxml", congNoButton));
    }

    @FXML
    private void onMoThongKe() {
        mo("thong-ke.fxml", thongKeButton);
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
        if (lamMoiPhien()) {
            hien(tenFile, nut);
        }
    }

    /**
     * Doc lai tai khoan tu CSDL truoc khi mo man hinh.
     *
     * @return true neu duoc mo tiep; false neu da dang xuat hoac dung lai
     *         Trang chinh vi doi vai tro
     */
    private boolean lamMoiPhien() {
        TaiKhoan cu = PhienLamViec.hienTai();
        try {
            TaiKhoan moi = taiKhoanService.lamMoiPhien(cu);
            PhienLamViec.dat(moi);
            if (cu.getVaiTro() != moi.getVaiTro()) {
                DieuHuong.hienTrangChinh();
                return false;
            }
        } catch (NghiepVuException e) {
            HopThoai.canhBao(e.getMessage());
            onDangXuat();
            return false;
        } catch (CauHinhException | DaoException e) {
            // Mat ket noi: van mo man hinh, man hinh se bao loi CSDL.
        }
        return true;
    }

    /** Dat man hinh vao phan than va chon nut menu cua no. */
    private Parent hien(String tenFile, Button nut) {
        Parent man = taiManHinh.apply(tenFile);
        noiDung.getChildren().setAll(man);
        menu.chon(nut);
        return man;
    }

    @FXML
    private void onDangXuat() {
        PhienLamViec.xoa();
        DieuHuong.hienDangNhap();
    }
}
