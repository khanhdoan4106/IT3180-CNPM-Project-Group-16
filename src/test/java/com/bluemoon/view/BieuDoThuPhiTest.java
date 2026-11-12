package com.bluemoon.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.controller.BieuDoThuPhi;
import com.bluemoon.model.ThuThang;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Biểu đồ "Thu phí 6 tháng gần nhất" (US-016) theo "Quy tắc biểu đồ" trong
 * docs/design/ui.md, vẽ thật bằng Monocle: tên hai trục, trục Y từ 0, cột
 * tháng hiện tại khác kiểu, số liệu trên đầu cột, không chú giải.
 */
class BieuDoThuPhiTest {

    private static final YearMonth T11 = YearMonth.of(2026, 11);

    @BeforeAll
    static void khoiDong() throws Exception {
        FxTest.khoiDong();
    }

    private static List<ThuThang> sauThang(YearMonth cuoi, long... trieu) {
        List<ThuThang> ds = new ArrayList<>();
        for (int i = 0; i < trieu.length; i++) {
            ds.add(new ThuThang(cuoi.minusMonths(trieu.length - 1L - i),
                    BigDecimal.valueOf(trieu[i])));
        }
        return ds;
    }

    private record Ve(BieuDoThuPhi bieuDo, Stage stage) {
        List<XYChart.Data<String, Number>> cot() {
            return bieuDo.getData().get(0).getData();
        }

        List<Text> nhan() {
            List<Text> ds = new ArrayList<>();
            for (Node n : bieuDo.lookupAll("." + BieuDoThuPhi.NHAN_COT)) {
                ds.add((Text) n);
            }
            return ds;
        }
    }

    private static Ve ve(List<ThuThang> duLieu, YearMonth hienTai) {
        BieuDoThuPhi b = new BieuDoThuPhi();
        b.hien(duLieu, hienTai);
        Scene scene = new Scene(b, 800, 460);
        scene.getStylesheets().add(BieuDoThuPhiTest.class
                .getResource("/com/bluemoon/css/giao-dien.css")
                .toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        b.applyCss();
        b.layout();
        return new Ve(b, stage);
    }

    @Test
    void coTenHaiTrucVaNhanThang() throws Exception {
        FxTest.chay(() -> {
            Ve v = ve(sauThang(T11, 198_000_000, 205_000_000, 201_000_000,
                    214_000_000, 209_000_000, 186_450_000), T11);
            assertEquals("Tháng (năm 2026)", v.bieuDo().getXAxis().getLabel());
            assertEquals("Triệu đồng", v.bieuDo().getYAxis().getLabel());
            assertEquals(List.of("T6", "T7", "T8", "T9", "T10", "T11"),
                    v.cot().stream().map(XYChart.Data::getXValue).toList());
            assertFalse(v.bieuDo().isLegendVisible(), "Một chuỗi");
            assertEquals(1, v.bieuDo().getData().size());
            v.stage().close();
            return null;
        });
    }

    @Test
    void haiNamThiNhanCoNam() throws Exception {
        FxTest.chay(() -> {
            YearMonth t3 = YearMonth.of(2027, 3);
            Ve v = ve(sauThang(t3, 1, 2, 3, 4, 5, 6), t3);
            assertEquals("Tháng", v.bieuDo().getXAxis().getLabel());
            assertEquals(List.of("T10/26", "T11/26", "T12/26", "T1/27",
                    "T2/27", "T3/27"), v.cot().stream()
                    .map(XYChart.Data::getXValue).toList());
            v.stage().close();
            return null;
        });
    }

    @Test
    void trucYBatDauTuKhongTheoMocTronCoLuoiNgang() throws Exception {
        FxTest.chay(() -> {
            Ve v = ve(sauThang(T11, 198_000_000, 205_000_000, 201_000_000,
                    214_000_000, 209_000_000, 186_450_000), T11);
            NumberAxis y = (NumberAxis) v.bieuDo().getYAxis();
            assertFalse(y.isAutoRanging());
            assertEquals(0, y.getLowerBound());
            assertEquals(250, y.getUpperBound());
            assertEquals(50, y.getTickUnit());
            assertTrue(v.bieuDo().isHorizontalGridLinesVisible());
            assertFalse(v.bieuDo().getVerticalGridLinesVisible());
            // Cot dung ty le: cot 0 dong cao 0, cot 214 cao nhat.
            assertEquals(214.0, v.cot().get(3).getYValue().doubleValue());
            v.stage().close();
            return null;
        });
    }

    @Test
    void cotThangHienTaiKhacKieu() throws Exception {
        FxTest.chay(() -> {
            Ve v = ve(sauThang(T11, 10, 20, 30, 40, 50, 60), T11);
            List<XYChart.Data<String, Number>> cot = v.cot();
            for (int i = 0; i < 5; i++) {
                assertTrue(cot.get(i).getNode().getStyleClass()
                        .contains(BieuDoThuPhi.COT_DA_QUA));
                assertFalse(cot.get(i).getNode().getStyleClass()
                        .contains(BieuDoThuPhi.COT_HIEN_TAI));
            }
            Node hienTai = cot.get(5).getNode();
            assertTrue(hienTai.getStyleClass()
                    .contains(BieuDoThuPhi.COT_HIEN_TAI));
            // Mau CSS khac nhau that (dam hon).
            assertFalse(nen(cot.get(0).getNode()).equals(nen(hienTai)));
            v.stage().close();
            return null;
        });
    }

    @Test
    void soLieuTrenDauCot() throws Exception {
        FxTest.chay(() -> {
            Ve v = ve(sauThang(T11, 198_000_000, 205_000_000, 0,
                    214_000_000, 209_000_000, 186_450_000), T11);
            List<Text> nhan = v.nhan();
            assertEquals(List.of("198", "205", "0", "214", "209", "186"),
                    nhan.stream().map(Text::getText).toList());
            for (int i = 0; i < nhan.size(); i++) {
                Bounds cot = v.cot().get(i).getNode().getBoundsInParent();
                Bounds chu = nhan.get(i).getBoundsInParent();
                assertTrue(chu.getMaxY() <= cot.getMinY() + 0.5,
                        "nhãn nằm trên đầu cột " + i);
                assertEquals(cot.getCenterX(), chu.getCenterX(), 1.0);
            }
            v.stage().close();
            return null;
        });
    }

    @Test
    void toanKhongVanVeTruc() throws Exception {
        FxTest.chay(() -> {
            Ve v = ve(sauThang(T11, 0, 0, 0, 0, 0, 0), T11);
            NumberAxis y = (NumberAxis) v.bieuDo().getYAxis();
            assertEquals(0, y.getLowerBound());
            assertEquals(5, y.getUpperBound());
            assertEquals("Nghìn đồng", y.getLabel());
            assertEquals(6, v.nhan().size());
            v.stage().close();
            return null;
        });
    }

    private static Object nen(Node n) {
        n.applyCss();
        return ((javafx.scene.layout.Region) n).getBackground().getFills()
                .get(0).getFill();
    }
}
