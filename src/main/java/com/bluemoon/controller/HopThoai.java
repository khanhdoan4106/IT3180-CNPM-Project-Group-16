package com.bluemoon.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * Nut va hop thoai tieng Viet dung chung. JavaFX khong co goi ngon ngu
 * tieng Viet, nen ButtonType.OK/CANCEL/CLOSE hien "OK", "Cancel", "Close";
 * moi Alert/Dialog dung cac nut duoi day thay the.
 */
final class HopThoai {

    /** Nut chap nhan chung. */
    static final ButtonType DONG_Y =
            new ButtonType("Đồng ý", ButtonData.OK_DONE);
    /** Nut bo qua thao tac. */
    static final ButtonType HUY =
            new ButtonType("Hủy", ButtonData.CANCEL_CLOSE);
    /** Nut dong hop thoai chi de xem. */
    static final ButtonType DONG =
            new ButtonType("Đóng", ButtonData.CANCEL_CLOSE);

    private HopThoai() {
    }

    /**
     * Hoi xac nhan truoc mot thao tac.
     *
     * @param noiDung cau hoi
     * @return true neu nguoi dung chon "Đồng ý"
     */
    static boolean xacNhan(String noiDung) {
        Alert hoi = new Alert(Alert.AlertType.CONFIRMATION, noiDung, DONG_Y,
                HUY);
        hoi.setTitle("Xác nhận");
        hoi.setHeaderText(null);
        return hoi.showAndWait().orElse(HUY) == DONG_Y;
    }

    /**
     * Hien canh bao co mot nut "Đóng" va cho nguoi dung dong.
     *
     * @param noiDung noi dung canh bao
     */
    static void canhBao(String noiDung) {
        Alert bao = new Alert(Alert.AlertType.WARNING, noiDung, DONG);
        bao.setTitle("Cảnh báo");
        bao.setHeaderText(null);
        bao.showAndWait();
    }

    /**
     * Dat hai nut cho hop thoai nhap lieu: nut chinh va "Hủy".
     *
     * @param hop hop thoai
     * @param nutChinh nut chinh, vd "Lưu thay đổi"; phai co ButtonData
     *        OK_DONE de hop thoai tra ket qua
     */
    static void datNut(Dialog<?> hop, ButtonType nutChinh) {
        hop.getDialogPane().getButtonTypes().setAll(nutChinh, HUY);
    }
}
