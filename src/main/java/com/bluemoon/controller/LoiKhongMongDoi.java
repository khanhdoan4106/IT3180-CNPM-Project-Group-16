package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import com.bluemoon.dao.DaoException;
import com.bluemoon.service.NghiepVuException;
import javafx.application.Platform;

/**
 * Bo xu ly loi toan cuc: loi ma controller khong bat (vd tai FXML, loi bat
 * thuong trong su kien) hien hop thoai thay vi chi in ra console, de nguoi
 * dung khong thay nut "khong co tac dung".
 */
public final class LoiKhongMongDoi {

    /** Thong bao cho loi khong thuoc loai da biet. */
    static final String LOI_CHUNG = "Có lỗi không mong muốn, vui lòng thử"
            + " lại. Nếu lỗi lặp lại, báo người quản trị hệ thống.";

    private LoiKhongMongDoi() {
    }

    /** Dat bo xu ly cho moi luong cua ung dung. Goi mot lan khi khoi dong. */
    public static void caiDat() {
        Thread.setDefaultUncaughtExceptionHandler((luong, loi) -> {
            loi.printStackTrace();
            String noiDung = thongBao(loi);
            if (Platform.isFxApplicationThread()) {
                HopThoai.canhBao(noiDung);
            } else {
                Platform.runLater(() -> HopThoai.canhBao(noiDung));
            }
        });
    }

    /**
     * Noi dung hien cho nguoi dung.
     *
     * @param loi loi khong duoc bat
     * @return thong bao phu hop loai loi
     */
    static String thongBao(Throwable loi) {
        if (loi instanceof NghiepVuException) {
            return loi.getMessage();
        }
        if (loi instanceof CauHinhException || loi instanceof DaoException) {
            return LoiCsdl.thongBao((RuntimeException) loi);
        }
        return LOI_CHUNG;
    }
}
