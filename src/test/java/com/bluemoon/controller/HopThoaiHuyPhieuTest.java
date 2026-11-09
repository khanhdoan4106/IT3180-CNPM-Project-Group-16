package com.bluemoon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.PhieuThu;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Hop thoai huy phieu: khong co hai nut deu bat dau bang "Huy" canh nhau. */
class HopThoaiHuyPhieuTest {

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
    }

    private static void datNeuChuaCo(String khoa, String giaTri) {
        if (System.getProperty(khoa) == null) {
            System.setProperty(khoa, giaTri);
        }
    }

    @Test
    void nutLaHuyPhieuVaDongKhongCoNutHuy() throws Exception {
        PhieuThu p = new PhieuThu(4, 2026, 4, 1, "A0601", "Nguyễn Thị Lan",
                new BigDecimal("100000"), HinhThucThanhToan.CHUYEN_KHOAN, 2,
                "Trần Thị Bình", LocalDateTime.of(2026, 11, 9, 10, 42), 0,
                false, null, null, null, List.of());
        CompletableFuture<Dialog<String>> kq = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                kq.complete(ThuPhiController.taoHopThoaiHuy(p));
            } catch (Throwable t) {
                kq.completeExceptionally(t);
            }
        });
        Dialog<String> d = kq.get(30, TimeUnit.SECONDS);
        List<String> nut = d.getDialogPane().getButtonTypes().stream()
                .map(ButtonType::getText).toList();
        assertEquals(List.of("Hủy phiếu", "Đóng"), nut);
        assertEquals("Hủy phiếu PT-2026-00004", d.getTitle());
        assertTrue(d.getHeaderText().contains("100.000 đ · Chuyển khoản"),
                d.getHeaderText());
    }
}
