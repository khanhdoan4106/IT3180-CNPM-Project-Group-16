package com.bluemoon.view;

import com.bluemoon.util.PhongChu;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

/** Khởi động JavaFX chạy ngầm (Monocle) và chạy việc trên luồng JavaFX. */
final class FxTest {

    private FxTest() {
    }

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
        // Dong cua so cuoi cung khong duoc tat JavaFX giua cac test.
        Platform.setImplicitExit(false);
        chay(() -> {
            PhongChu.nap();
            return null;
        });
    }

    static <T> T chay(Callable<T> viec) throws Exception {
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

    private static void datNeuChuaCo(String khoa, String giaTri) {
        if (System.getProperty(khoa) == null) {
            System.setProperty(khoa, giaTri);
        }
    }
}
