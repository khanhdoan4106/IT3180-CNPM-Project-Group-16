package com.bluemoon.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.util.PhimEnter;
import com.bluemoon.util.PhongChu;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Enter trong ô nhập của một khung chỉ chạy nút của khung đó (PhimEnter).
 * Tải từng màn hình FXML thật cùng controller, trên cửa sổ ảo của Monocle
 * (không mở cửa sổ thật, không gửi phím thật); thay hành động mọi nút bằng
 * bộ ghi nhận rồi gửi sự kiện Enter vào từng ô và xem nút nào chạy.
 */
class PhimEnterTest {

    private static final String THU_MUC_VIEW = "/com/bluemoon/view/";
    private static final String CSS = "/com/bluemoon/css/giao-dien.css";

    @BeforeAll
    static void khoiDongJavaFx() throws Exception {
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
        // Dong cua so cuoi cung khong duoc tat JavaFX giua cac test.
        Platform.setImplicitExit(false);
        trenLuongFx(() -> {
            PhongChu.nap();
            return null;
        });
    }

    private static void datNeuChuaCo(String khoa, String giaTri) {
        if (System.getProperty(khoa) == null) {
            System.setProperty(khoa, giaTri);
        }
    }

    private static <T> T trenLuongFx(Callable<T> viec) throws Exception {
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

    /** Tên nút nào chạy khi nhấn Enter ở từng ô; null = không nút nào. */
    private static void kiem(String tenFile, Map<String, String> mong,
            boolean khongNutMacDinh) throws Exception {
        List<String> sai = trenLuongFx(() -> chay(tenFile, mong,
                khongNutMacDinh));
        assertTrue(sai.isEmpty(), tenFile + ":\n  " + String.join("\n  ", sai));
    }

    private static List<String> chay(String tenFile, Map<String, String> mong,
            boolean khongNutMacDinh) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent goc;
        try (java.io.InputStream vao = new java.io.ByteArrayInputStream(
                boControllerVaHanhDong(tenFile).getBytes(
                        java.nio.charset.StandardCharsets.UTF_8))) {
            goc = loader.load(vao);
        }
        PhimEnter.apDung(loader.getNamespace());
        Scene scene = new Scene(goc, 1120, 645);
        scene.getStylesheets().add(
                PhimEnterTest.class.getResource(CSS).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        goc.applyCss();
        goc.layout();

        // Dua noi dung moi tab vao cay cua so de tim thay nut cua tab do.
        for (Object o : loader.getNamespace().values()) {
            if (o instanceof javafx.scene.control.TabPane tp) {
                for (int i = tp.getTabs().size() - 1; i >= 0; i--) {
                    tp.getSelectionModel().select(i);
                    goc.applyCss();
                    goc.layout();
                }
            }
        }
        List<String> chay = new ArrayList<>();
        List<String> sai = new ArrayList<>();
        for (Node n : goc.lookupAll(".button")) {
            if (n instanceof Button b) {
                b.setDisable(false);
                b.setOnAction(e -> chay.add(b.getText()));
                if (khongNutMacDinh && b.isDefaultButton()) {
                    sai.add("Nút mặc định toàn màn: " + b.getText());
                }
            }
        }
        for (Map.Entry<String, String> e : mong.entrySet()) {
            Node o = (Node) loader.getNamespace().get(e.getKey());
            assertNotNull(o, "Không có ô " + e.getKey());
            Node nhan = o instanceof DatePicker d ? d.getEditor() : o;
            chay.clear();
            Event.fireEvent(nhan, new KeyEvent(KeyEvent.KEY_PRESSED, "\r",
                    "ENTER", KeyCode.ENTER, false, false, false, false));
            List<String> mongDoi = e.getValue() == null ? List.of()
                    : List.of(e.getValue());
            if (!chay.equals(mongDoi)) {
                sai.add("Enter ở " + e.getKey() + " chạy " + chay
                        + ", mong " + mongDoi);
            }
        }
        stage.close();
        return sai;
    }

    private static Map<String, String> tatCa(String nut, String... oNhap) {
        Map<String, String> m = new LinkedHashMap<>();
        for (String o : oNhap) {
            m.put(o, nut);
        }
        return m;
    }

    @Test
    void nhanKhauMoiKhungChiChayNutCuaMinh() throws Exception {
        Map<String, String> mong = new LinkedHashMap<>();
        mong.putAll(tatCa("Ghi nhận", "loaiDotCombo", "dotTuPicker",
                "dotDenPicker", "lyDoField"));
        mong.putAll(tatCa("Lưu", "hoTenField", "ngaySinhPicker",
                "gioiTinhCombo", "cccdField", "quanHeCombo",
                "loaiCuTruCombo", "noiThuongTruField", "tamTruTuPicker",
                "tamTruDenPicker"));
        kiem("nhan-khau.fxml", mong, true);
    }

    @Test
    void hoGiaDinhOTimKiemKhongLuuForm() throws Exception {
        Map<String, String> mong = new LinkedHashMap<>();
        mong.put("timKiemField", null);
        mong.putAll(tatCa("Lưu", "maField", "loaiCombo", "tangField",
                "dienTichField", "sdtField", "ngayPicker", "chuHoField",
                "ngaySinhChuHoPicker", "gioiTinhChuHoCombo",
                "cccdChuHoField", "loaiCuTruChuHoCombo",
                "noiThuongTruChuHoField", "tamTruTuChuHoPicker",
                "tamTruDenChuHoPicker"));
        kiem("ho-gia-dinh.fxml", mong, true);
    }

    @Test
    void khoanThuDonGiaSuaCaiKhacTaoMoi() throws Exception {
        Map<String, String> mong = new LinkedHashMap<>();
        mong.put("donGiaField", "Sửa đơn giá");
        mong.putAll(tatCa("Tạo khoản thu", "tenField", "tuNgayPicker",
                "denNgayPicker", "soTienField"));
        kiem("khoan-thu.fxml", mong, true);
    }

    @Test
    void taiKhoanMoiFormGuiDungNutCuaMinh() throws Exception {
        Map<String, String> mong = new LinkedHashMap<>();
        mong.putAll(tatCa("Lưu thay đổi", "hoTenCuaToiField",
                "sdtCuaToiField"));
        mong.putAll(tatCa("Đổi mật khẩu", "matKhauCuField",
                "matKhauMoiField", "nhapLaiMoiField"));
        mong.putAll(tatCa("Đăng ký", "tenDangNhapField", "hoTenField",
                "matKhauField", "nhapLaiField", "vaiTroCombo"));
        kiem("tai-khoan.fxml", mong, true);
    }

    @Test
    void dangNhapVanGiuNutMacDinh() throws Exception {
        Map<String, String> mong = new LinkedHashMap<>();
        mong.putAll(tatCa("Đăng nhập", "tenDangNhapField"));
        kiem("dang-nhap.fxml", mong, false);
    }

    @Test
    void oTimKiemHoCoHanhDongRieng() throws Exception {
        String xml = docFxml("ho-gia-dinh.fxml");
        int viTri = xml.indexOf("fx:id=\"timKiemField\"");
        String the = xml.substring(viTri, xml.indexOf("/>", viTri));
        assertTrue(the.contains("onAction=\"#onTimKiem\""),
                "Ô tìm kiếm phải có hành động riêng: " + the);
        assertEquals(0, xml.split("defaultButton=\"true\"", -1).length - 1);
    }

    private static String docFxml(String tenFile) throws Exception {
        try (java.io.InputStream vao = PhimEnterTest.class
                .getResourceAsStream(THU_MUC_VIEW + tenFile)) {
            return new String(vao.readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    /**
     * Bỏ fx:controller và on...="#..." để tải không cần CSDL; nút do test tự
     * gắn bộ ghi nhận. Giữ nguyên properties, id và defaultButton.
     */
    private static String boControllerVaHanhDong(String tenFile)
            throws Exception {
        return docFxml(tenFile).replaceAll("fx:controller=\"[^\"]*\"", "")
                .replaceAll("\\bon[A-Z][A-Za-z]*=\"#[^\"]*\"", "");
    }
}
