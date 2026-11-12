package com.bluemoon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.NoDongDao.TomTatNo;
import com.bluemoon.model.HoNoDong;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.util.PhienLamViec;
import com.bluemoon.util.PhongChu;
import com.bluemoon.util.ThanhMenu;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Thẻ "Hộ còn nợ" ở Trang chính (US-017): bấm thẻ mở màn Công nợ ở tab
 * "Hộ nợ đọng", menu Công nợ đang chọn; chữ của thẻ và tab nợ đọng. Màn
 * Công nợ được tải không kèm controller để không cần CSDL.
 */
class TheHoConNoTest {

    private static final String VIEW = "/com/bluemoon/view/";
    private static final Function<String, Parent> GOC =
            TrangChinhController.taiManHinh;

    @BeforeAll
    static void khoiDong() throws Exception {
        datNeuChuaCo("glass.platform", "Monocle");
        datNeuChuaCo("monocle.platform", "Headless");
        datNeuChuaCo("prism.order", "sw");
        CompletableFuture<Void> xong = new CompletableFuture<>();
        try {
            Platform.startup(() -> xong.complete(null));
        } catch (IllegalStateException daKhoiDong) {
            xong.complete(null);
        }
        xong.get(30, TimeUnit.SECONDS);
        Platform.setImplicitExit(false);
        chay(() -> {
            PhongChu.nap();
            return null;
        });
    }

    @AfterEach
    void traLai() {
        TrangChinhController.taiManHinh = GOC;
    }

    private static void datNeuChuaCo(String khoa, String giaTri) {
        if (System.getProperty(khoa) == null) {
            System.setProperty(khoa, giaTri);
        }
    }

    private static <T> T chay(Callable<T> viec) throws Exception {
        CompletableFuture<T> kq = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                kq.complete(viec.call());
            } catch (Throwable e) {
                kq.completeExceptionally(e);
            }
        });
        return kq.get(60, TimeUnit.SECONDS);
    }

    /** Tải FXML bỏ controller và trình xử lý sự kiện (như BoCucFxmlTest). */
    private static Parent khongController(String tep) {
        try (InputStream vao = TheHoConNoTest.class
                .getResourceAsStream(VIEW + tep)) {
            String fxml = new String(vao.readAllBytes(),
                    StandardCharsets.UTF_8)
                    .replaceAll("\\s+fx:controller=\"[^\"]*\"", "")
                    .replaceAll("\\s+on[A-Z]\\w*=\"#[^\"]*\"", "");
            return new FXMLLoader().load(new ByteArrayInputStream(
                    fxml.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void bamTheMoCongNoOTabHoNoDong() throws Exception {
        chay(() -> {
            PhienLamViec.xoa();
            FXMLLoader loader = new FXMLLoader(
                    TheHoConNoTest.class.getResource(VIEW
                            + "trang-chinh.fxml"));
            Parent goc = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(goc, 1440, 1024));
            stage.show();
            Map<String, Object> ten = loader.getNamespace();
            VBox the = (VBox) ten.get("hoConNoBox");
            goc.getStylesheets().add(TheHoConNoTest.class
                    .getResource("/com/bluemoon/css/giao-dien.css")
                    .toExternalForm());
            goc.applyCss();
            assertEquals(Cursor.HAND, the.getCursor(), "Con trỏ bàn tay");
            assertNotNull(the.getOnMouseClicked(), "Thẻ bấm được");

            String[] daTai = new String[1];
            TrangChinhController.taiManHinh = tep -> {
                daTai[0] = tep;
                return khongController(tep);
            };
            TrangChinhController c = loader.getController();
            c.moNoDong();

            assertEquals("cong-no.fxml", daTai[0]);
            StackPane noiDung = (StackPane) ten.get("noiDung");
            Parent man = (Parent) noiDung.getChildren().get(0);
            TabPane tp = (TabPane) man.lookup("#"
                    + CongNoController.TAB_PANE);
            assertEquals(CongNoController.TAB_NO_DONG,
                    tp.getSelectionModel().getSelectedItem().getId());
            Button congNo = (Button) ten.get("congNoButton");
            assertTrue(congNo.getStyleClass().contains(ThanhMenu.DANG_CHON));
            assertSame(congNo, new ThanhMenu(congNo).dangChon());
            stage.close();
            return null;
        });
    }

    @Test
    void manCongNoMoRaMacDinhOTabChotCongNo() throws Exception {
        chay(() -> {
            Parent man = khongController("cong-no.fxml");
            TabPane tp = (TabPane) man.lookup("#"
                    + CongNoController.TAB_PANE);
            assertEquals("chotTab",
                    tp.getSelectionModel().getSelectedItem().getId());
            assertEquals(List.of("Chốt công nợ", "Hộ nợ đọng"),
                    tp.getTabs().stream().map(t -> t.getText()).toList());
            return null;
        });
    }

    @Test
    void dongPhuCuaThe() {
        assertEquals("8 hộ nợ từ 3 tháng trở lên",
                TrangChinhController.dongNoDong(new TomTatNo(37, 8)));
        assertEquals("Không hộ nào nợ từ 3 tháng",
                TrangChinhController.dongNoDong(new TomTatNo(5, 0)));
    }

    @Test
    void chuCuaBangNoDong() {
        HoNoDong dangO = new HoNoDong(1, "A0601", "Nguyễn Văn An",
                TrangThaiHo.DANG_O, 4, YearMonth.of(2026, 8),
                new BigDecimal("1260000"));
        HoNoDong daDi = new HoNoDong(2, "A0602", null,
                TrangThaiHo.DA_CHUYEN_DI, 3, YearMonth.of(2026, 9),
                new BigDecimal("740000"));
        assertEquals("A0601", CongNoController.canHo(dangO));
        assertEquals("A0602 (đã chuyển đi)", CongNoController.canHo(daDi));
        assertEquals("2 hộ, tổng nợ 2.000.000 đ",
                CongNoController.dongTongNo(List.of(dangO, daDi)));
    }
}
