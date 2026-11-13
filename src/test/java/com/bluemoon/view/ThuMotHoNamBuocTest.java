package com.bluemoon.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.controller.ThuPhiController;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.service.ThuPhiGia;
import com.bluemoon.util.KieuHopThoai;
import com.bluemoon.util.PhienLamViec;
import com.bluemoon.util.PhimEnter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * AC-072: thu xong một hộ trong tối đa 5 bước trên màn Thu phí. Tải
 * thu-phi.fxml thật cùng controller, dữ liệu giả trong bộ nhớ
 * ({@link ThuPhiGia}), trên cửa sổ ảo của Monocle (không điều khiển màn
 * hình). Mỗi thao tác của người dùng là một sự kiện bàn phím/chuột gửi tới
 * nút hoặc ô như khi bấm thật; phím gõ đi tới ô đang có tiêu điểm, nên
 * tiêu điểm sai chỗ thì test trượt. Hộp xác nhận và hộp "In biên lai" là
 * hộp thoại thật (Alert).
 *
 * <p>Đếm thao tác: gõ mã phòng rồi Enter = 1; bấm chuột = 1. Xem danh sách
 * khoản không tính. Bước sau khi đã lập phiếu ("Để sau"/"In biên lai")
 * không tính.
 */
class ThuMotHoNamBuocTest {

    private Stage stage;
    private int soThaoTac;

    @BeforeAll
    static void khoiDong() throws Exception {
        FxTest.khoiDong();
        FxTest.chay(() -> {
            KieuHopThoai.caiDat();
            return null;
        });
    }

    @AfterEach
    void dong() throws Exception {
        FxTest.chay(() -> {
            for (Window w : List.copyOf(Window.getWindows())) {
                w.hide();
            }
            return null;
        });
        PhienLamViec.xoa();
    }

    private record Man(Map<String, Object> ten, Scene scene) {
        <T> T lay(String id) {
            @SuppressWarnings("unchecked")
            T t = (T) ten.get(id);
            assertNotNull(t, id);
            return t;
        }
    }

    private Man moMan(ThuPhiGia gia) throws Exception {
        PhienLamViec.dat(ThuPhiGia.THU_QUY);
        Man m = FxTest.chay(() -> {
            FXMLLoader loader = new FXMLLoader(ThuMotHoNamBuocTest.class
                    .getResource("/com/bluemoon/view/thu-phi.fxml"));
            loader.setControllerFactory(
                    c -> new ThuPhiController(gia.service()));
            Parent goc = loader.load();
            PhimEnter.apDung(loader.getNamespace());
            Scene scene = new Scene(goc, 1120, 645);
            stage = new Stage();
            stage.setScene(scene);
            stage.show();
            goc.applyCss();
            goc.layout();
            return new Man(loader.getNamespace(), scene);
        });
        // Cho cac viec Platform.runLater cua controller (dat tieu diem).
        FxTest.chay(() -> null);
        return m;
    }

    @Test
    void thuMotHoTrongBonThaoTacTieuDiemDungCho() throws Exception {
        ThuPhiGia gia = ThuPhiGia.moi();
        Man m = moMan(gia);
        TextField ma = m.lay("maField");
        RadioButton tienMat = m.lay("tienMatRadio");
        Button thu = m.lay("thuButton");
        TableView<?> bangNo = m.lay("bangNo");

        // Mo man: tieu diem san o o ma phong.
        assertSame(ma, tieuDiem(m.scene()), "mở màn: tiêu điểm ở ô mã phòng");

        // (1) Go ma phong + Enter (phim di toi o dang co tieu diem).
        FxTest.chay(() -> {
            goPhim(m.scene(), "a0601");
            nhanEnter(m.scene());
            return null;
        });
        soThaoTac++;

        // (2) Xem khoan: da chon san het khoan bat buoc chua dong.
        FxTest.chay(() -> {
            assertEquals(ThuPhiGia.SO_KHOAN, bangNo.getItems().size());
            assertEquals("Thu tất cả", thu.getText(),
                    "mặc định chọn hết khoản bắt buộc chưa đóng");
            assertFalse(thu.isDisabled());
            assertSame(tienMat, tieuDiem(m.scene()),
                    "sau Tra cứu: tiêu điểm ở nhóm hình thức");
            return null;
        });

        // Anh cho bao cao TS-004 (chi khi chay voi -DchupAnh=<thu muc>).
        String thuMuc = System.getProperty("chupAnh");
        if (thuMuc != null) {
            var anh = FxTest.chay(() -> m.scene().snapshot(null));
            java.nio.file.Files.createDirectories(java.nio.file.Path.of(thuMuc));
            ChupManHinhTest.ghiPng(anh, java.nio.file.Path.of(thuMuc,
                    System.getProperty("chupTienTo", "anh")
                            + "-thu-phi-da-tra-cuu.png"));
        }

        // (3) Chon Tien mat.
        FxTest.chay(() -> {
            bam(tienMat);
            return null;
        });
        soThaoTac++;
        FxTest.chay(() -> {
            assertTrue(tienMat.isSelected());
            assertSame(tienMat, tieuDiem(m.scene()));
            return null;
        });

        // (4) Bam "Thu tat ca": hop xac nhan mo (showAndWait chan luong
        // JavaFX nen bam bat dong bo roi cho hop thoai).
        Platform.runLater(() -> bam(thu));
        soThaoTac++;
        Button dongY = cho(() -> nutHopThoai("Đồng ý"));
        FxTest.chay(() -> {
            assertSame(dongY, tieuDiem(dongY.getScene()),
                    "hộp xác nhận: tiêu điểm ở nút Đồng ý");
            assertTrue(dongY.isDefaultButton());
            return null;
        });
        assertEquals(0, gia.phieu().size(), "chưa lập phiếu trước Đồng ý");

        // (5) Bam "Dong y": phieu duoc lap.
        Platform.runLater(() -> bam(dongY));
        soThaoTac++;
        Button deSau = cho(() -> nutHopThoai("Để sau"));
        assertNotNull(cho(() -> nutHopThoai("In biên lai")),
                "sau khi lập phiếu có sẵn nút In biên lai");
        List<PhieuThu> phieu = gia.phieu();
        assertEquals(1, phieu.size(), "đã lập phiếu");
        assertEquals(0, ThuPhiGia.TONG.compareTo(phieu.get(0).tongTien()));
        assertEquals(HinhThucThanhToan.TIEN_MAT, phieu.get(0).hinhThuc());

        assertTrue(soThaoTac <= 5, "AC-072: tối đa 5 thao tác, thực tế "
                + soThaoTac);
        assertEquals(4, soThaoTac);

        // Sau khi lap phieu (khong tinh): "De sau" thi tieu diem ve o ma
        // phong, boi den ma cu de go ho tiep theo.
        Platform.runLater(() -> bam(deSau));
        cho(() -> nutHopThoai("Để sau") == null ? Boolean.TRUE : null);
        FxTest.chay(() -> {
            assertSame(ma, tieuDiem(m.scene()),
                    "xong một hộ: tiêu điểm về ô mã phòng");
            assertEquals("a0601", ma.getSelectedText());
            return null;
        });
    }

    @Test
    void maSaiThiTieuDiemVanOOMaPhong() throws Exception {
        Man m = moMan(ThuPhiGia.moi());
        TextField ma = m.lay("maField");
        FxTest.chay(() -> {
            goPhim(m.scene(), "Z9999");
            nhanEnter(m.scene());
            assertSame(ma, tieuDiem(m.scene()));
            assertEquals("Z9999", ma.getSelectedText(),
                    "mã sai được bôi đen để gõ đè");
            return null;
        });
    }

    // ---------------- thao tac ----------------

    private static Node tieuDiem(Scene s) {
        return s.getFocusOwner();
    }

    /** Go tung ky tu vao o dang co tieu diem. */
    private static void goPhim(Scene s, String chu) {
        for (char c : chu.toCharArray()) {
            Event.fireEvent(s.getFocusOwner(), new KeyEvent(
                    KeyEvent.KEY_TYPED, String.valueOf(c), "",
                    KeyCode.UNDEFINED, false, false, false, false));
        }
    }

    private static void nhanEnter(Scene s) {
        Node o = s.getFocusOwner();
        Event.fireEvent(o, new KeyEvent(KeyEvent.KEY_PRESSED, "\r", "ENTER",
                KeyCode.ENTER, false, false, false, false));
        Event.fireEvent(o, new KeyEvent(KeyEvent.KEY_RELEASED, "", "ENTER",
                KeyCode.ENTER, false, false, false, false));
    }

    /** Bam chuot trai vao giua nut, nhu nguoi dung bam. */
    private static void bam(Node n) {
        assertFalse(n.isDisabled(), "không bấm được nút bị khóa: " + n);
        Bounds b = n.getLayoutBounds();
        double x = b.getCenterX();
        double y = b.getCenterY();
        Event.fireEvent(n, chuot(MouseEvent.MOUSE_PRESSED, n, x, y, true));
        Event.fireEvent(n, chuot(MouseEvent.MOUSE_RELEASED, n, x, y, false));
        Event.fireEvent(n, chuot(MouseEvent.MOUSE_CLICKED, n, x, y, false));
    }

    private static MouseEvent chuot(javafx.event.EventType<MouseEvent> loai,
            Node n, double x, double y, boolean dangGiu) {
        javafx.geometry.Point2D p = n.localToScreen(x, y);
        double sx = p == null ? x : p.getX();
        double sy = p == null ? y : p.getY();
        return new MouseEvent(loai, x, y, sx, sy, MouseButton.PRIMARY, 1,
                false, false, false, false, dangGiu, false, false, false,
                false, true, null);
    }

    /** Nut co chu cho truoc trong mot hop thoai dang mo, null neu chua co. */
    private Button nutHopThoai(String chu) {
        for (Window w : Window.getWindows()) {
            if (w == stage || !w.isShowing() || w.getScene() == null) {
                continue;
            }
            for (Node n : w.getScene().getRoot().lookupAll(".button")) {
                if (n instanceof Button b && chu.equals(b.getText())) {
                    return b;
                }
            }
        }
        return null;
    }

    /** Hoi tren luong JavaFX cho toi khi co ket qua (toi da 10 giay). */
    private static <T> T cho(Callable<T> hoi) throws Exception {
        long het = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < het) {
            T t = FxTest.chay(hoi);
            if (t != null) {
                return t;
            }
            Thread.sleep(50);
        }
        throw new AssertionError("Hết giờ chờ hộp thoại");
    }
}
