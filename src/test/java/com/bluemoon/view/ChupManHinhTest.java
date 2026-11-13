package com.bluemoon.view;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Chụp PNG từng màn hình FXML bằng Monocle (không mở cửa sổ thật, không
 * điều khiển màn hình) cho báo cáo rà soát giao diện TS-004
 * (docs/review/ts-004-giao-dien.md). Chỉ chạy khi có thuộc tính
 * {@code chupAnh} (thư mục lưu ảnh), vd:
 *
 * <pre>mvn test -Dtest=ChupManHinhTest -DchupAnh=docs/review/ts-004
 *     -DchupTienTo=sau</pre>
 *
 * <p>Tải màn hình như {@link BoCucFxmlTest} (bỏ controller, điền chữ động
 * dài nhất, hiện các khung chỉ hiện lúc chạy), ở 1120×645; Trang chính
 * 1440×1024. Màn có tab thì chụp mỗi tab một ảnh.
 */
@EnabledIfSystemProperty(named = "chupAnh", matches = ".+")
class ChupManHinhTest {

    private static final String THU_MUC_VIEW = "/com/bluemoon/view/";
    private static final String CSS = "/com/bluemoon/css/giao-dien.css";

    @BeforeAll
    static void khoiDong() throws Exception {
        FxTest.khoiDong();
    }

    @ParameterizedTest
    @ValueSource(strings = {"dang-nhap", "trang-chinh", "ho-gia-dinh",
            "nhan-khau", "khoan-thu", "cong-no", "thu-phi", "thong-ke",
            "tai-khoan"})
    void chup(String man) throws Exception {
        Path thuMuc = Path.of(System.getProperty("chupAnh"));
        String tienTo = System.getProperty("chupTienTo", "anh");
        Files.createDirectories(thuMuc);
        List<WritableImage> anh = FxTest.chay(() -> chupMan(man));
        for (int i = 0; i < anh.size(); i++) {
            String ten = tienTo + "-" + man
                    + (anh.size() > 1 ? "-tab" + (i + 1) : "") + ".png";
            ghiPng(anh.get(i), thuMuc.resolve(ten));
        }
    }

    private static List<WritableImage> chupMan(String man) throws Exception {
        String tenFile = man + ".fxml";
        boolean trangChinh = man.equals("trang-chinh");
        double rong = trangChinh ? 1440 : 1120;
        double cao = trangChinh ? 1024 : 645;
        FXMLLoader loader = new FXMLLoader(
                ChupManHinhTest.class.getResource(THU_MUC_VIEW + tenFile));
        Parent goc;
        try (InputStream vao = new ByteArrayInputStream(
                boController(tenFile).getBytes(StandardCharsets.UTF_8))) {
            goc = loader.load(vao);
        }
        BoCucFxmlTest.CHU_DONG.forEach((id, chu) -> {
            if (loader.getNamespace().get(id) instanceof Labeled l) {
                l.setText(chu);
            }
        });
        BoCucFxmlTest.HIEN_LUC_CHAY.forEach(id -> {
            if (loader.getNamespace().get(id) instanceof Node n) {
                n.setVisible(true);
                n.setManaged(true);
            }
        });
        Scene scene = new Scene(goc, rong, cao);
        scene.getStylesheets().add(
                ChupManHinhTest.class.getResource(CSS).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        List<WritableImage> ketQua = new ArrayList<>();
        TabPane tp = timTabPane(goc);
        if (tp == null) {
            dung(goc);
            ketQua.add(scene.snapshot(null));
        } else {
            for (Tab tab : tp.getTabs()) {
                tp.getSelectionModel().select(tab);
                dung(goc);
                ketQua.add(scene.snapshot(null));
            }
        }
        stage.close();
        return ketQua;
    }

    private static void dung(Parent goc) {
        goc.applyCss();
        goc.layout();
        goc.applyCss();
        goc.layout();
    }

    private static TabPane timTabPane(Node n) {
        if (n instanceof TabPane tp) {
            return tp;
        }
        if (n instanceof Parent p) {
            for (Node con : p.getChildrenUnmodifiable()) {
                TabPane tp = timTabPane(con);
                if (tp != null) {
                    return tp;
                }
            }
        }
        return null;
    }

    private static String boController(String tenFile) throws Exception {
        URL url = ChupManHinhTest.class.getResource(THU_MUC_VIEW + tenFile);
        try (InputStream vao = url.openStream()) {
            return new String(vao.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("\\s+fx:controller=\"[^\"]*\"", "")
                    .replaceAll("\\s+on[A-Z]\\w*=\"#[^\"]*\"", "");
        }
    }

    /** Ghi ảnh JavaFX ra PNG không cần javafx-swing. */
    static void ghiPng(WritableImage anh, Path tep) throws Exception {
        int w = (int) anh.getWidth();
        int h = (int) anh.getHeight();
        BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        PixelReader r = anh.getPixelReader();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                b.setRGB(x, y, r.getArgb(x, y));
            }
        }
        ImageIO.write(b, "png", tep.toFile());
    }
}
