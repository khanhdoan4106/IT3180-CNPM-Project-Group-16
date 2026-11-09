package com.bluemoon.controller;

import com.bluemoon.model.ChiTietPhieuThu;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.TamVang;
import com.bluemoon.model.ThongBaoPhi;
import com.bluemoon.service.SoTien;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Mau trang in: bien lai (US-013), thong bao phi (US-015), danh sach tam
 * vang (US-004). Moi lan goi tao Node moi de vua xem truoc vua in duoc.
 */
final class MauIn {

    /** Ten don vi in tren dau trang. */
    static final String DON_VI = "BAN QUẢN TRỊ CHUNG CƯ BLUEMOON";

    private static final double RONG_DOC = 600;
    /** Cot so tien du rong cho "1.000.000.000 đ" in dam (so tien toi da). */
    private static final double RONG_SO_TIEN = 160;
    private static final double RONG_NGANG = 960;
    /**
     * Chieu cao toi da cua mot trang ngang (cung don vi voi RONG_NGANG):
     * ti le vung in A4 nam ngang, le mac dinh 0,75 inch (734 x 487 pt).
     */
    private static final double CAO_TRANG_NGANG = RONG_NGANG * 487 / 734;
    private static final String CSS = "/com/bluemoon/css/giao-dien.css";
    /** Cot rong tu muc nay tro len thi cho chu xuong dong. */
    private static final double COT_XUONG_DONG = 150;
    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter NGAY_GIO =
            DateTimeFormatter.ofPattern("HH:mm 'ngày' dd/MM/uuuu");

    private MauIn() {
    }

    /**
     * Bien lai thu tien (AC-039): ma bien lai, ho, khoan, so tien, ngay
     * thu, nguoi thu; ban in lai ghi ro "BẢN IN LẠI" (AC-040).
     *
     * @param p phieu thu
     * @param inLai true neu phieu da duoc in it nhat mot lan
     * @return trang bien lai
     */
    static Node bienLai(PhieuThu p, boolean inLai) {
        VBox trang = trang(RONG_DOC);
        HBox dau = new HBox(nhan(DON_VI, "in-dam", "in-nho"));
        Region dem = new Region();
        HBox.setHgrow(dem, Priority.ALWAYS);
        dau.getChildren().add(dem);
        if (inLai) {
            dau.getChildren().add(nhan("BẢN IN LẠI", "in-ban-in-lai"));
        }
        trang.getChildren().addAll(dau,
                giua(nhan("BIÊN LAI THU TIỀN", "in-tieu-de")),
                giua(nhan("Số: " + p.soBienLai(), "in-dam")));

        GridPane tt = luoiThongTin();
        dongThongTin(tt, 0, "Căn hộ", p.maCanHo());
        dongThongTin(tt, 1, "Chủ hộ", p.tenChuHo() == null ? "–"
                : p.tenChuHo());
        dongThongTin(tt, 2, "Thời điểm thu", p.thoiDiem().format(NGAY_GIO));
        dongThongTin(tt, 3, "Hình thức", p.hinhThuc().getNhan());
        trang.getChildren().add(tt);

        GridPane bang = bang(new String[] {"STT", "Khoản thu", "Số tiền"},
                new double[] {44, 316, RONG_SO_TIEN}, new boolean[] {false, false,
                    true});
        int i = 1;
        for (ChiTietPhieuThu d : p.chiTiet()) {
            dongBang(bang, i, String.valueOf(i), d.moTa(),
                    SoTien.hienThi(d.soTien()));
            i++;
        }
        oTong(bang, i, "Tổng cộng", SoTien.hienThi(p.tongTien()));
        trang.getChildren().addAll(bang,
                nhan("Bằng chữ: " + SoTien.bangChu(p.tongTien()),
                        "in-nghieng"));

        trang.getChildren().add(chuKy(
                "Người nộp tiền", null,
                "Người thu tiền", p.tenNguoiThu()));
        return trang;
    }

    /**
     * Thong bao thu phi cua mot ho (AC-044).
     *
     * @param tb thong bao
     * @param ngayIn ngay in
     * @return trang thong bao
     */
    static Node thongBao(ThongBaoPhi tb, LocalDate ngayIn) {
        VBox trang = trang(RONG_DOC);
        YearMonth t = tb.thang();
        trang.getChildren().addAll(nhan(DON_VI, "in-dam", "in-nho"),
                giua(nhan("THÔNG BÁO THU PHÍ THÁNG " + t.getMonthValue() + "/"
                        + t.getYear(), "in-tieu-de")));
        String chuHo = tb.ho().tenChuHo() == null ? ""
                : " – chủ hộ " + tb.ho().tenChuHo();
        trang.getChildren().add(nhan("Kính gửi: Hộ gia đình căn hộ "
                + tb.ho().ho().getMaCanHo() + chuHo, "in-dam"));
        trang.getChildren().add(nhan("Ban quản trị xin thông báo các khoản"
                + " hộ cần đóng như sau:"));

        GridPane bang = bang(new String[] {"STT", "Khoản", "Số tiền"},
                new double[] {44, 316, RONG_SO_TIEN}, new boolean[] {false, false,
                    true});
        int i = 1;
        for (KhoanNo k : tb.khoanThang()) {
            dongBang(bang, i, String.valueOf(i), k.moTa(),
                    SoTien.hienThi(k.soTien()));
            i++;
        }
        for (KhoanNo k : tb.noCu()) {
            dongBang(bang, i, String.valueOf(i), k.moTa() + " (chưa đóng)",
                    SoTien.hienThi(k.soTien()));
            i++;
        }
        oTong(bang, i, "Tổng cần đóng", SoTien.hienThi(tb.tong()));
        trang.getChildren().addAll(bang,
                nhan("Bằng chữ: " + SoTien.bangChu(tb.tong()), "in-nghieng"),
                nhan("Hộ vui lòng đóng tại văn phòng Ban quản trị, bằng tiền"
                        + " mặt hoặc chuyển khoản. Nếu đã đóng, xin bỏ qua"
                        + " thông báo này."),
                phai(nhan("Ngày " + ngayIn.format(NGAY), "in-nghieng")),
                phai(nhan("TM. BAN QUẢN TRỊ", "in-dam")));
        return trang;
    }

    /**
     * Danh sach nhan khau tam vang (AC-012), chia trang ngang. Moi trang chi
     * them dong khi trang van vua khung giay ngang sau khi bo cuc (ho ten,
     * ly do dai xuong dong lam hang cao hon), nen khong bi thu nho qua muc.
     * Goi tren luong JavaFX.
     *
     * @param ds danh sach
     * @param ngay ngay bao cao
     * @return cac trang
     */
    static List<Node> danhSachTamVang(List<TamVang> ds, LocalDate ngay) {
        List<Node> trangIn = new ArrayList<>();
        List<Integer> moc = chiaTrang(ds, ngay);
        for (int t = 0; t < moc.size() - 1; t++) {
            trangIn.add(trangTamVang(ds.subList(moc.get(t), moc.get(t + 1)),
                    moc.get(t), ds.size(), t + 1, moc.size() - 1, ngay,
                    t == moc.size() - 2));
        }
        return trangIn;
    }

    /**
     * Cac moc chia trang: trang t gom dong tu moc[t] den moc[t+1] (khong
     * gom). Them dong cho den khi trang do thuc te cao qua khung giay ngang.
     */
    private static List<Integer> chiaTrang(List<TamVang> ds, LocalDate ngay) {
        List<Integer> moc = new ArrayList<>();
        int dau = 0;
        moc.add(0);
        while (dau < ds.size()) {
            int cuoi = dau + 1;
            while (cuoi < ds.size() && vuaKhungGiay(ds, dau, cuoi + 1, ngay)) {
                cuoi++;
            }
            moc.add(cuoi);
            dau = cuoi;
        }
        return moc;
    }

    /** Trang gom dong [dau, cuoi) co vua chieu cao khung giay ngang khong. */
    private static boolean vuaKhungGiay(List<TamVang> ds, int dau, int cuoi,
            LocalDate ngay) {
        VBox thu = trangTamVang(ds.subList(dau, cuoi), dau, ds.size(), 99, 99,
                ngay, cuoi == ds.size());
        Scene tam = new Scene(new Group(thu));
        tam.getStylesheets().add(
                MauIn.class.getResource(CSS).toExternalForm());
        thu.applyCss();
        thu.autosize();
        thu.layout();
        return thu.getBoundsInLocal().getHeight() <= CAO_TRANG_NGANG;
    }

    private static VBox trangTamVang(List<TamVang> dong, int dau, int tong,
            int trang, int soTrang, LocalDate ngay, boolean cuoiCung) {
        VBox v = trang(RONG_NGANG);
        v.getChildren().addAll(nhan(DON_VI, "in-dam", "in-nho"),
                giua(nhan("DANH SÁCH NHÂN KHẨU TẠM VẮNG", "in-tieu-de")),
                giua(nhan("Tính đến ngày " + ngay.format(NGAY)
                        + " · Tổng số: " + tong + " người", "in-nghieng")));
        GridPane bang = bang(new String[] {"STT", "Họ và tên",
            "Ngày sinh", "Số CCCD", "Căn hộ", "Từ ngày", "Đến ngày",
            "Lý do"}, new double[] {44, 170, 100, 120, 70, 100, 100, 176},
                new boolean[8]);
        for (int i = 0; i < dong.size(); i++) {
            TamVang t = dong.get(i);
            dongBang(bang, i + 1, String.valueOf(dau + i + 1), t.hoTen(),
                    t.ngaySinh().format(NGAY),
                    t.soCccd() == null ? "" : t.soCccd(), t.maCanHo(),
                    t.tuNgay().format(NGAY), t.denNgay().format(NGAY),
                    t.lyDo() == null ? "" : t.lyDo());
        }
        v.getChildren().addAll(bang, phai(nhan("Trang " + trang + "/"
                + soTrang, "in-nho")));
        if (cuoiCung) {
            v.getChildren().add(chuKy("Người lập danh sách", null,
                    "TM. Ban quản trị", null));
        }
        return v;
    }

    /**
     * Gio in dinh dang ngan, de hien tren bang.
     *
     * @param luc thoi diem
     * @return vd "09:30"
     */
    static String gio(LocalDateTime luc) {
        return luc.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static VBox trang(double rong) {
        VBox v = new VBox(12);
        v.getStyleClass().add("trang-in");
        v.setPrefWidth(rong);
        v.setMinWidth(rong);
        v.setMaxWidth(rong);
        return v;
    }

    private static Label nhan(String chu, String... lop) {
        Label l = new Label(chu);
        l.getStyleClass().addAll(lop);
        l.setWrapText(true);
        return l;
    }

    private static Node giua(Label l) {
        HBox h = new HBox(l);
        h.setAlignment(Pos.CENTER);
        return h;
    }

    private static Node phai(Label l) {
        HBox h = new HBox(l);
        h.setAlignment(Pos.CENTER_RIGHT);
        return h;
    }

    private static GridPane luoiThongTin() {
        GridPane g = new GridPane();
        g.setHgap(12);
        g.setVgap(4);
        g.getColumnConstraints().addAll(new ColumnConstraints(110),
                new ColumnConstraints());
        return g;
    }

    private static void dongThongTin(GridPane g, int hang, String nhan,
            String giaTri) {
        g.addRow(hang, nhan(nhan + ":"), nhan(giaTri, "in-dam"));
    }

    private static GridPane bang(String[] dau, double[] rong,
            boolean[] canPhai) {
        GridPane g = new GridPane();
        for (int c = 0; c < dau.length; c++) {
            ColumnConstraints cc = new ColumnConstraints(rong[c]);
            if (canPhai[c]) {
                cc.setHalignment(HPos.RIGHT);
            }
            g.getColumnConstraints().add(cc);
            g.add(o(dau[c], "in-o-dau", canPhai[c]), c, 0);
        }
        return g;
    }

    private static void dongBang(GridPane g, int hang, String... o) {
        for (int c = 0; c < o.length; c++) {
            ColumnConstraints cot = g.getColumnConstraints().get(c);
            boolean soTien = cot.getHalignment() == HPos.RIGHT;
            Label l = o(o[c], "in-o", soTien);
            // Ngay, so, ma ngan khong xuong dong; chi cot rong (ten, ly do)
            // moi xuong dong khi qua dai.
            l.setWrapText(!soTien && cot.getPrefWidth() >= COT_XUONG_DONG);
            g.add(l, c, hang);
        }
    }

    private static void oTong(GridPane g, int hang, String nhan,
            String soTien) {
        int soCot = g.getColumnConstraints().size();
        Label l = o(nhan, "in-o-dau", false);
        g.add(l, 0, hang, soCot - 1, 1);
        g.add(o(soTien, "in-o-dau", true), soCot - 1, hang);
    }

    private static Label o(String chu, String lop, boolean canPhai) {
        Label l = nhan(chu, lop);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setMaxHeight(Double.MAX_VALUE);
        if (canPhai) {
            // So tien khong bao gio xuong dong hay bi cat thanh "...".
            l.setWrapText(false);
            l.setMinWidth(Region.USE_PREF_SIZE);
        }
        l.setAlignment(canPhai ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        GridPane.setFillWidth(l, true);
        return l;
    }

    private static Node chuKy(String trai, String tenTrai, String phai,
            String tenPhai) {
        HBox h = new HBox(cotKy(trai, tenTrai), cotKy(phai, tenPhai));
        h.setSpacing(40);
        return h;
    }

    private static Node cotKy(String chucDanh, String ten) {
        VBox v = new VBox(4, nhan(chucDanh, "in-dam"),
                nhan("(Ký, ghi rõ họ tên)", "in-nghieng", "in-nho"));
        v.setAlignment(Pos.TOP_CENTER);
        v.setPrefHeight(110);
        Region dem = new Region();
        VBox.setVgrow(dem, Priority.ALWAYS);
        v.getChildren().add(dem);
        if (ten != null) {
            v.getChildren().add(nhan(ten));
        }
        HBox.setHgrow(v, Priority.ALWAYS);
        v.setMaxWidth(Double.MAX_VALUE);
        return v;
    }
}
