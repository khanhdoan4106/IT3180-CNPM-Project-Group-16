package com.bluemoon.util;

import java.util.Map;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Enter trong ô nhập của một khung chỉ chạy nút của khung đó, không chạy
 * nút mặc định của cả màn hình (nút mặc định làm Enter ở khung này bấm nhầm
 * nút của khung kia).
 *
 * <p>Khai báo trong FXML, trên khung chứa các ô nhập:
 * {@code <properties enterChay="luuButton"/>} với giá trị là {@code fx:id}
 * của nút. {@link #apDung(Map)} gắn xử lý sau khi tải FXML.
 */
public final class PhimEnter {

    /** Khóa thuộc tính FXML chứa fx:id của nút cần chạy. */
    public static final String KHOA = "enterChay";

    private PhimEnter() {
    }

    /**
     * Gắn Enter cho mọi khung có thuộc tính {@link #KHOA}.
     *
     * @param tenMien {@code FXMLLoader.getNamespace()} của màn hình vừa tải
     * @throws IllegalStateException nếu fx:id nút không có hoặc không phải
     *         nút bấm
     */
    public static void apDung(Map<String, Object> tenMien) {
        for (Object o : tenMien.values()) {
            if (o instanceof Node khung
                    && khung.getProperties().get(KHOA) instanceof String id) {
                if (!(tenMien.get(id) instanceof Button nut)) {
                    throw new IllegalStateException("enterChay=\"" + id
                            + "\" không trỏ tới nút nào");
                }
                gan(khung, nut);
            }
        }
    }

    /**
     * Enter ở ô nhập trong {@code khung} bấm {@code nut}. Bỏ qua khi tiêu
     * điểm đang ở một nút (Enter ở nút không làm gì, như trước) và khi sự
     * kiện đã được xử lý (vd chọn mục trong danh sách đang mở).
     *
     * @param khung khung chứa các ô nhập
     * @param nut nút cần bấm
     */
    public static void gan(Node khung, Button nut) {
        khung.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() != KeyCode.ENTER || e.isConsumed()
                    || e.getTarget() instanceof ButtonBase) {
                return;
            }
            Node tieuDiem = khung.getScene() == null ? null
                    : khung.getScene().getFocusOwner();
            if (tieuDiem instanceof ButtonBase) {
                return;
            }
            nut.fire();
            e.consume();
        });
    }
}
