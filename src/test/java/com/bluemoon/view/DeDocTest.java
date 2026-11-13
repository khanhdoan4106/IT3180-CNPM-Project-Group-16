package com.bluemoon.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.css.CssParser;
import javafx.css.Stylesheet;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * TS-004, NFR-008 (người lớn tuổi dùng được): đo từ css/giao-dien.css và
 * từ cái JavaFX thực vẽ. Tương phản mọi cặp chữ/nền đang dùng ≥ 4,5:1
 * (WCAG AA); chữ gốc 16 px; ô nhập, nút, dòng bảng đủ cao; ô đang có tiêu
 * điểm có viền màu chính. Bố cục từng màn (cỡ chữ ≥ 14, khoảng cách nút)
 * kiểm ở {@link BoCucFxmlTest}.
 */
class DeDocTest {

    private static final String CSS = "/com/bluemoon/css/giao-dien.css";
    private static final double AA = 4.5;

    @BeforeAll
    static void khoiDong() throws Exception {
        FxTest.khoiDong();
    }

    private static String docCss() throws Exception {
        try (var vao = DeDocTest.class.getResourceAsStream(CSS)) {
            return new String(vao.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /** Bien -mau-* khai bao trong .root. */
    private static Map<String, Color> bienMau() throws Exception {
        Map<String, Color> m = new LinkedHashMap<>();
        m.put("white", Color.WHITE);
        Matcher k = Pattern.compile("(-mau-[a-z-]+):\\s*(#[0-9a-fA-F]{6});")
                .matcher(docCss());
        while (k.find()) {
            m.put(k.group(1), Color.web(k.group(2)));
        }
        return m;
    }

    @Test
    void javaFxDocHetMoiLuatCss() throws Exception {
        // JavaFX bo ngang phan con lai cua tep khi gap selector viet tren
        // nhieu dong ma khong bao loi: so luat phai bang so khoi { }.
        String css = docCss().replaceAll("(?s)/\\*.*?\\*/", "");
        int khoi = css.length() - css.replace("{", "").length();
        int fontFace = css.split("@font-face", -1).length - 1;
        Stylesheet s = new CssParser().parse(
                DeDocTest.class.getResource(CSS));
        assertEquals(khoi - fontFace, s.getRules().size(),
                "CSS bị đọc dở: kiểm tra selector xuống dòng");
    }

    /**
     * Cặp chữ/nền đang dùng: chữ chính, chữ phụ, chữ trên nút chính, chữ đỏ
     * báo lỗi, chữ trên nền thẻ (trắng), tab, dải thông tin, nhãn tóm tắt,
     * dòng chọn, thanh menu, chú thích.
     */
    @Test
    void moiCapChuNenDatAa() throws Exception {
        Map<String, Color> mau = bienMau();
        String[][] cap = {
            {"-mau-chu", "-mau-nen"}, {"-mau-chu", "white"},
            {"-mau-chu", "-mau-chon"}, {"-mau-chu", "-mau-xen-ke"},
            {"-mau-chu", "-mau-dai"}, {"-mau-chu", "-mau-nhan"},
            {"-mau-phu", "-mau-nen"}, {"-mau-phu", "white"},
            {"-mau-phu", "-mau-nhan"}, {"-mau-phu", "-mau-chon"},
            {"-mau-phu", "-mau-xen-ke"},
            {"white", "-mau-chinh"}, {"white", "-mau-chinh-dam"},
            {"white", "-mau-chu"}, {"-mau-dai", "-mau-chinh"},
            {"-mau-loi", "white"}, {"-mau-loi", "-mau-nen"},
            {"-mau-loi", "-mau-nen-loi"}, {"-mau-loi", "-mau-nhan"},
            {"-mau-loi", "-mau-chon"},
            {"-mau-thanh-cong", "white"}, {"-mau-thanh-cong", "-mau-nen"},
            {"-mau-thanh-cong", "-mau-nhan"},
            {"-mau-chinh", "white"}, {"-mau-chinh", "-mau-nen"},
            {"-mau-chinh-dam", "-mau-dai"}, {"-mau-chinh-dam", "white"},
        };
        List<String> sai = new ArrayList<>();
        for (String[] c : cap) {
            Color chu = mau.get(c[0]);
            Color nen = mau.get(c[1]);
            assertNotNull(chu, c[0]);
            assertNotNull(nen, c[1]);
            double tp = MauPhuTest.tuongPhan(chu, nen);
            if (tp < AA) {
                sai.add(String.format("%s trên %s: %.2f:1", c[0], c[1], tp));
            }
        }
        assertTrue(sai.isEmpty(), "Dưới 4,5:1: " + sai);
    }

    @Test
    void chuGoc16ChuLoiVaChuNutChinhDungMau() throws Exception {
        Map<String, Color> mau = bienMau();
        Object[] kq = FxTest.chay(() -> {
            Label thuong = new Label("Nội dung");
            Label loi = new Label("Không tìm thấy phòng");
            loi.getStyleClass().add("loi");
            Button chinh = new Button("Thu tất cả");
            chinh.getStyleClass().add("nut-chinh");
            Scene s = canh(new VBox(thuong, loi, chinh));
            Object[] r = {thuong.getFont().getSize(), loi.getTextFill(),
                chinh.getTextFill(), nen(chinh).get(0).getFill()};
            ((Stage) s.getWindow()).close();
            return r;
        });
        assertEquals(16.0, (double) kq[0], 0.01, "chữ gốc 16 px");
        assertEquals(mau.get("-mau-loi"), kq[1]);
        assertEquals(Color.WHITE, kq[2]);
        assertEquals(mau.get("-mau-chinh"), kq[3]);
    }

    /**
     * Vùng cuộn Trang chính đặt {@code -fx-background: transparent}; JavaFX
     * tính màu chữ mặc định từ nền đó ra màu trắng, nên tiêu đề tháng, số
     * lớn của thẻ và tiêu đề khung từng là chữ trắng trên nền trắng.
     */
    @Test
    void chuTrongVungCuonTrangChinhLaMauChu() throws Exception {
        Color chu = bienMau().get("-mau-chu");
        Object[] kq = FxTest.chay(() -> {
            Label tieuDe = new Label("Tổng quan tháng 12/2026");
            tieuDe.getStyleClass().add("tieu-de-trang");
            Label so = new Label("186.450.000 đ");
            so.getStyleClass().add("so-lon");
            VBox the = new VBox(so);
            the.getStyleClass().add("the");
            javafx.scene.control.ScrollPane cuon =
                    new javafx.scene.control.ScrollPane(new VBox(tieuDe, the));
            cuon.getStyleClass().add("cuon-trang");
            Scene s = canh(cuon);
            Object[] r = {tieuDe.getTextFill(), so.getTextFill()};
            ((Stage) s.getWindow()).close();
            return r;
        });
        assertEquals(chu, kq[0], "tiêu đề trang trong vùng cuộn");
        assertEquals(chu, kq[1], "số lớn trên thẻ trong vùng cuộn");
    }

    @Test
    void oNhapNutDongBangDuCao() throws Exception {
        double[] cao = FxTest.chay(() -> {
            TextField o = new TextField();
            ComboBox<String> chon = new ComboBox<>();
            DatePicker ngay = new DatePicker();
            Button phu = new Button("Nhập mới");
            Button chinh = new Button("Lưu");
            chinh.getStyleClass().add("nut-chinh");
            TableView<Dong> bang = new TableView<>(
                    FXCollections.observableArrayList(new Dong("A0601")));
            TableColumn<Dong, String> cot = new TableColumn<>("Căn hộ");
            cot.setCellValueFactory(new PropertyValueFactory<>("ma"));
            bang.getColumns().add(cot);
            Scene s = canh(new VBox(o, chon, ngay, phu, chinh, bang));
            TableRow<?> dong = (TableRow<?>) bang.lookup(".table-row-cell");
            double tieuDe = ((Region) bang.lookup(
                    ".column-header-background")).getHeight();
            double[] r = {o.getHeight(), chon.getHeight(), ngay.getHeight(),
                phu.getHeight(), chinh.getHeight(), dong.getHeight(),
                tieuDe};
            ((Stage) s.getWindow()).close();
            return r;
        });
        String[] ten = {"ô nhập", "danh sách chọn", "ô ngày", "nút phụ",
            "nút chính", "dòng bảng", "tiêu đề cột"};
        double[] toiThieu = {36, 36, 36, 36, 40, 36, 36};
        for (int i = 0; i < cao.length; i++) {
            assertTrue(cao[i] >= toiThieu[i], ten[i] + " cao " + cao[i]
                    + " < " + toiThieu[i]);
        }
    }

    @Test
    void oDangCoTieuDiemCoVienMauChinh() throws Exception {
        Color chinh = bienMau().get("-mau-chinh");
        Color dam = bienMau().get("-mau-chinh-dam");
        Object[] kq = FxTest.chay(() -> {
            TextField o = new TextField();
            TextField khac = new TextField();
            Button phu = new Button("Tra cứu");
            Button nutChinh = new Button("Thu tất cả");
            nutChinh.getStyleClass().add("nut-chinh");
            Scene s = canh(new VBox(o, khac, phu, nutChinh));
            Object[] r = new Object[6];
            r[0] = vienTieuDiem(o);
            r[1] = vienTieuDiem(khac);
            o.requestFocus();
            r[2] = vienTieuDiem(o);
            phu.requestFocus();
            r[3] = vienTieuDiem(phu);
            nutChinh.requestFocus();
            r[4] = vienTieuDiem(nutChinh);
            r[5] = nen(o).get(1).getInsets().getTop();
            ((Stage) s.getWindow()).close();
            return r;
        });
        assertTrue(!chinh.equals(kq[1]), "ô không có tiêu điểm không viền"
                + " màu chính");
        assertEquals(chinh, kq[2], "ô nhập có tiêu điểm: viền -mau-chinh");
        assertEquals(chinh, kq[3], "nút có tiêu điểm: viền -mau-chinh");
        assertEquals(dam, kq[4], "nút chính có tiêu điểm: vòng ngoài");
        assertEquals(1.0, (double) kq[5], 0.01, "ô thường viền 1 px");
    }

    /** Mau lop nen ngoai cung (vien) sau khi ap CSS. */
    private static Object vienTieuDiem(Region r) {
        r.applyCss();
        return nen(r).get(0).getFill();
    }

    private static List<BackgroundFill> nen(Region r) {
        r.applyCss();
        return r.getBackground().getFills();
    }

    private static Scene canh(Node goc) {
        VBox v = new VBox(8, goc);
        Scene s = new Scene(v, 400, 600);
        s.getStylesheets().add(DeDocTest.class.getResource(CSS)
                .toExternalForm());
        Stage st = new Stage();
        st.setScene(s);
        st.show();
        v.applyCss();
        v.layout();
        v.applyCss();
        v.layout();
        return s;
    }

    /** Mot dong bang mau. */
    public static final class Dong {
        private final String ma;

        Dong(String ma) {
            this.ma = ma;
        }

        public String getMa() {
            return ma;
        }
    }
}
