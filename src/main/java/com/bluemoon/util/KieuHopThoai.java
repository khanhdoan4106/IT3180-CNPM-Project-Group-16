package com.bluemoon.util;

import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.stage.Window;

/**
 * Gan css/giao-dien.css cho moi cua so moi mo (Alert, Dialog, hop thoai
 * xem truoc), de hop thoai cung kieu nut, chu va mau voi man hinh
 * (docs/design/ui.md, muc Thanh phan). Goi mot lan khi khoi dong.
 */
public final class KieuHopThoai {

    /** Duong dan tep kieu dung chung. */
    public static final String CSS = "/com/bluemoon/css/giao-dien.css";

    private static boolean daCai;

    private KieuHopThoai() {
    }

    /** Theo doi danh sach cua so; goi tren luong JavaFX. */
    public static void caiDat() {
        if (daCai) {
            return;
        }
        daCai = true;
        Window.getWindows().forEach(KieuHopThoai::gan);
        Window.getWindows().addListener((ListChangeListener<Window>) c -> {
            while (c.next()) {
                c.getAddedSubList().forEach(KieuHopThoai::gan);
            }
        });
    }

    private static void gan(Window w) {
        ganScene(w.getScene());
        w.sceneProperty().addListener((o, cu, moi) -> ganScene(moi));
    }

    /**
     * Them tep kieu vao scene neu chua co.
     *
     * @param scene scene cua cua so, co the null
     */
    public static void ganScene(Scene scene) {
        if (scene == null) {
            return;
        }
        String url = KieuHopThoai.class.getResource(CSS).toExternalForm();
        if (!scene.getStylesheets().contains(url)) {
            scene.getStylesheets().add(url);
        }
    }
}
