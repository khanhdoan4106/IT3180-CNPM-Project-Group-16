package com.bluemoon.controller;

import com.bluemoon.model.ThuThang;
import com.bluemoon.service.ThangTrucY;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * Biểu đồ cột "Thu phí 6 tháng gần nhất" ở Trang chính (US-016), theo mục
 * "Quy tắc biểu đồ" trong docs/design/ui.md: trục X có tên và nhãn từng
 * tháng; trục Y có tên kèm đơn vị, thang từ 0 chia theo mốc tròn
 * ({@link ThangTrucY}), lưới ngang mờ; số liệu trên đầu cột; cột tháng chưa
 * kết thúc đậm hơn; một chuỗi nên không có chú giải; một trục Y.
 */
public class BieuDoThuPhi extends BarChart<String, Number> {

    /** Lớp CSS của cột tháng đã kết thúc. */
    public static final String COT_DA_QUA = "cot-da-qua";
    /** Lớp CSS của cột tháng hiện tại (chưa kết thúc), tô đậm hơn. */
    public static final String COT_HIEN_TAI = "cot-hien-tai";
    /** Lớp CSS của nhãn số liệu trên đầu cột. */
    public static final String NHAN_COT = "nhan-cot";

    private static final double CACH_DAU_COT = 4;

    private final List<Text> nhan = new ArrayList<>();
    private ThangTrucY thang = ThangTrucY.tinh(List.of());

    /** Biểu đồ rỗng (FXML tạo), có sẵn tên trục và thang mặc định. */
    public BieuDoThuPhi() {
        super(new CategoryAxis(), new NumberAxis());
        getStyleClass().add("bieu-do-thu");
        setLegendVisible(false);
        setAnimated(false);
        setVerticalGridLinesVisible(false);
        setHorizontalGridLinesVisible(true);
        setAlternativeRowFillVisible(false);
        setBarGap(0);
        setCategoryGap(28);
        NumberAxis y = (NumberAxis) getYAxis();
        y.setAutoRanging(false);
        y.setMinorTickVisible(false);
        y.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number n) {
                return dinhDang(n.doubleValue());
            }

            @Override
            public Number fromString(String s) {
                throw new UnsupportedOperationException();
            }
        });
        datThang(thang);
        getXAxis().setLabel("Tháng");
    }

    /**
     * Vẽ số liệu thu của các tháng.
     *
     * @param duLieu thu từng tháng, cũ trước, tháng hiện tại cuối
     * @param hienTai tháng hiện tại (cột đậm)
     */
    public void hien(List<ThuThang> duLieu, YearMonth hienTai) {
        thang = ThangTrucY.tinh(duLieu.stream().map(ThuThang::tong)
                .toList());
        datThang(thang);
        boolean haiNam = duLieu.stream().map(t -> t.thang().getYear())
                .distinct().count() > 1;
        getXAxis().setLabel(haiNam || duLieu.isEmpty() ? "Tháng"
                : "Tháng (năm " + duLieu.get(0).thang().getYear() + ")");

        getPlotChildren().removeAll(nhan);
        nhan.clear();
        XYChart.Series<String, Number> chuoi = new XYChart.Series<>();
        for (ThuThang t : duLieu) {
            chuoi.getData().add(new XYChart.Data<>(nhanThang(t.thang(),
                    haiNam), thang.doi(t.tong())));
        }
        getData().setAll(List.of(chuoi));
        for (int i = 0; i < duLieu.size(); i++) {
            XYChart.Data<String, Number> d = chuoi.getData().get(i);
            boolean dangChay = duLieu.get(i).thang().equals(hienTai);
            d.getNode().getStyleClass().add(dangChay ? COT_HIEN_TAI
                    : COT_DA_QUA);
            Text t = new Text(dinhDang(d.getYValue().doubleValue()));
            t.getStyleClass().add(NHAN_COT);
            nhan.add(t);
        }
        getPlotChildren().addAll(nhan);
        requestChartLayout();
    }

    /**
     * Thang trục Y đang dùng.
     *
     * @return thang trục Y
     */
    public ThangTrucY getThang() {
        return thang;
    }

    private void datThang(ThangTrucY t) {
        NumberAxis y = (NumberAxis) getYAxis();
        y.setLowerBound(0);
        y.setUpperBound(t.tren().doubleValue());
        y.setTickUnit(t.buoc().doubleValue());
        y.setLabel(t.donVi());
    }

    /** Nhãn tháng: "T11"; biểu đồ trải hai năm thì "T12/25". */
    static String nhanThang(YearMonth t, boolean haiNam) {
        return "T" + t.getMonthValue()
                + (haiNam ? String.format("/%02d", t.getYear() % 100) : "");
    }

    /**
     * Số theo kiểu Việt Nam: từ 100 trở lên làm tròn số nguyên (186), nhỏ
     * hơn thì giữ một chữ số lẻ (2,5).
     */
    static String dinhDang(double so) {
        DecimalFormatSymbols vn = DecimalFormatSymbols.getInstance(
                Locale.forLanguageTag("vi-VN"));
        DecimalFormat f = new DecimalFormat(
                Math.abs(so) >= 100 ? "#,##0" : "#,##0.#", vn);
        return f.format(BigDecimal.valueOf(so));
    }

    @Override
    protected void layoutPlotChildren() {
        super.layoutPlotChildren();
        if (getData().isEmpty()) {
            return;
        }
        List<XYChart.Data<String, Number>> ds = getData().get(0).getData();
        for (int i = 0; i < ds.size() && i < nhan.size(); i++) {
            Node cot = ds.get(i).getNode();
            if (cot == null) {
                continue;
            }
            Bounds b = cot.getBoundsInParent();
            Text t = nhan.get(i);
            double rong = t.getLayoutBounds().getWidth();
            t.setLayoutX(b.getMinX() + (b.getWidth() - rong) / 2);
            t.setLayoutY(b.getMinY() - CACH_DAU_COT
                    - t.getLayoutBounds().getMaxY());
        }
    }
}
