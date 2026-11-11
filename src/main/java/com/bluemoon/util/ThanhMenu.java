package com.bluemoon.util;

import java.util.List;
import javafx.scene.control.Button;

/**
 * Trạng thái "đang chọn" của các nút trên thanh menu: nút của màn hình đang
 * mở có nền đậm ({@code -mau-chinh-dam}, xem css/giao-dien.css), về trang chủ
 * thì không nút nào được chọn.
 */
public final class ThanhMenu {

    /** Lớp CSS của nút đang chọn. */
    public static final String DANG_CHON = "dang-chon";

    private final List<Button> nut;

    /**
     * Tạo trạng thái cho một nhóm nút menu.
     *
     * @param nut các nút menu (không gồm logo, Đăng xuất)
     */
    public ThanhMenu(Button... nut) {
        this.nut = List.of(nut);
    }

    /**
     * Chọn một nút, bỏ chọn các nút khác.
     *
     * @param chon nút của màn hình vừa mở, phải thuộc nhóm
     */
    public void chon(Button chon) {
        if (!nut.contains(chon)) {
            throw new IllegalArgumentException("Không phải nút menu");
        }
        boChonTatCa();
        chon.getStyleClass().add(DANG_CHON);
    }

    /** Bỏ chọn tất cả (khi về trang chủ). */
    public void boChonTatCa() {
        nut.forEach(b -> b.getStyleClass().remove(DANG_CHON));
    }

    /**
     * Nút đang chọn.
     *
     * @return nút đang chọn, null nếu đang ở trang chủ
     */
    public Button dangChon() {
        return nut.stream()
                .filter(b -> b.getStyleClass().contains(DANG_CHON))
                .findFirst().orElse(null);
    }
}
