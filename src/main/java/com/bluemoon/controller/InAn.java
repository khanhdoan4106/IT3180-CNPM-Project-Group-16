package com.bluemoon.controller;

import java.util.List;
import java.util.function.Supplier;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Window;

/**
 * Xem truoc va in bang PrinterJob cua JavaFX, dung chung cho bien lai
 * (US-013), thong bao phi (US-015) va danh sach tam vang (US-004). Tren
 * Windows chon may in "Microsoft Print to PDF" de luu thanh tep PDF; xuat
 * tep Excel/PDF truc tiep lam o Sprint 4 (gia dinh 27).
 */
final class InAn {

    /** Thong bao khi may tinh khong co may in nao. */
    static final String LOI_KHONG_CO_MAY_IN = "Máy tính chưa cài máy in."
            + " Trên Windows có thể chọn máy in \"Microsoft Print to PDF\""
            + " để lưu thành tệp PDF.";

    private static final String CSS = "/com/bluemoon/css/giao-dien.css";

    private InAn() {
    }

    /**
     * Mo hop thoai xem truoc; nguoi dung bam "In…" thi chon may in va in
     * tung trang, moi trang co cho vua kho giay.
     *
     * @param tieuDe tieu de hop thoai
     * @param taoTrang tao cac trang can in; goi lai moi lan (mot lan de xem,
     *        mot lan de in) vi mot Node chi nam trong mot Scene
     * @return true neu da gui lenh in thanh cong
     */
    static boolean xemVaIn(String tieuDe, Supplier<List<Node>> taoTrang) {
        VBox xem = new VBox(16);
        xem.setPadding(new Insets(16));
        xem.getStyleClass().add("nen-xem-truoc");
        xem.setAlignment(Pos.TOP_CENTER);
        for (Node trang : taoTrang.get()) {
            StackPane giay = new StackPane(trang);
            giay.getStyleClass().add("giay-xem-truoc");
            giay.setMaxWidth(Region.USE_PREF_SIZE);
            xem.getChildren().add(giay);
        }
        ScrollPane cuon = new ScrollPane(xem);
        cuon.setPrefSize(640, 560);
        cuon.setFitToWidth(true);

        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(tieuDe);
        ButtonType in = new ButtonType("In…", ButtonBar.ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().setAll(in, HopThoai.DONG);
        d.getDialogPane().setContent(cuon);
        d.getDialogPane().getStylesheets().add(
                InAn.class.getResource(CSS).toExternalForm());
        d.setResizable(true);
        if (d.showAndWait().orElse(HopThoai.DONG) != in) {
            return false;
        }
        return in(taoTrang.get(), d.getOwner());
    }

    private static boolean in(List<Node> trang, Window chuSoHuu) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            HopThoai.canhBao(LOI_KHONG_CO_MAY_IN);
            return false;
        }
        if (!job.showPrintDialog(chuSoHuu)) {
            job.cancelJob();
            return false;
        }
        PageLayout kho = job.getJobSettings().getPageLayout();
        for (Node t : trang) {
            dungBoCuc(t);
            double rong = t.getBoundsInLocal().getWidth();
            double cao = t.getBoundsInLocal().getHeight();
            double tiLe = Math.min(1, Math.min(
                    kho.getPrintableWidth() / rong,
                    kho.getPrintableHeight() / cao));
            t.getTransforms().add(new Scale(tiLe, tiLe));
            if (!job.printPage(kho, t)) {
                job.cancelJob();
                HopThoai.canhBao("Máy in báo lỗi, chưa in xong. Kiểm tra"
                        + " máy in rồi thử lại.");
                return false;
            }
        }
        return job.endJob();
    }

    /** Dat trang vao Scene tam de CSS va bo cuc duoc tinh truoc khi in. */
    private static void dungBoCuc(Node trang) {
        Scene tam = new Scene(new Group(trang));
        tam.getStylesheets().add(InAn.class.getResource(CSS).toExternalForm());
        trang.applyCss();
        if (trang instanceof Parent p) {
            p.layout();
        }
    }
}
