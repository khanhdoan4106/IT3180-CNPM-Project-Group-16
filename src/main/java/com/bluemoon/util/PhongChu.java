package com.bluemoon.util;

import java.io.IOException;
import java.io.InputStream;
import javafx.scene.text.Font;

/**
 * Nap phong Be Vietnam Pro nhung trong resources/fonts (docs/design/ui.md).
 *
 * <p>giao-dien.css da khai bao {@code @font-face}, nhung JavaFX khong nap
 * duoc phong qua URL file: co dau cach hoac ky tu ma hoa (vd thu muc
 * "Máy tính") nen nap them bang luong doc o day. Nap trung khong sao.
 */
public final class PhongChu {

    /** Ten ho phong dung trong giao-dien.css. */
    public static final String HO_PHONG = "Be Vietnam Pro";

    private static final String THU_MUC = "/com/bluemoon/fonts/";
    private static final String[] TEP = {
        "BeVietnamPro-Regular.ttf", "BeVietnamPro-Medium.ttf",
        "BeVietnamPro-SemiBold.ttf", "BeVietnamPro-Bold.ttf"};

    private PhongChu() {
    }

    /**
     * Nap cac tep phong. Goi sau khi JavaFX da khoi dong, truoc khi hien
     * man hinh dau tien.
     */
    public static void nap() {
        for (String tep : TEP) {
            try (InputStream vao =
                    PhongChu.class.getResourceAsStream(THU_MUC + tep)) {
                if (vao == null || Font.loadFont(vao, 14) == null) {
                    throw new IllegalStateException(
                            "Không nạp được phông " + tep);
                }
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Không đọc được phông " + tep, e);
            }
        }
    }
}
