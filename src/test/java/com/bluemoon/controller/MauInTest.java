package com.bluemoon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.ChiTietPhieuThu;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.ThongBaoPhi;
import com.bluemoon.model.TamVang;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.service.SoTien;
import com.bluemoon.util.PhongChu;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Trang in dung bang chinh {@link MauIn}, bo cuc trong Scene tam nhu
 * {@code InAn.dungBoCuc}, roi do tung o so tien: phai hien du, mot dong,
 * khong bi cat thanh "..." (N2 cua ra soat Sprint 3). Chay headless bang
 * Monocle nhu BoCucFxmlTest.
 */
class MauInTest {

    private static final String CSS = "/com/bluemoon/css/giao-dien.css";
    private static final YearMonth T11 = YearMonth.of(2026, 11);

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

    @ParameterizedTest
    @ValueSource(strings = {"9999999", "10000000", "24000000", "123456789",
        "1000000000"})
    void bienLaiInDuSoTienMotDong(String so) throws Exception {
        BigDecimal tien = new BigDecimal(so);
        PhieuThu p = new PhieuThu(1, 2026, 1, 1, "PH01", "Nguyễn Văn An",
                tien, HinhThucThanhToan.TIEN_MAT, 1, "Quản trị viên",
                LocalDateTime.of(2026, 11, 9, 9, 30), 0, false, null, null,
                null, List.of(new ChiTietPhieuThu(1, 1, "Phí dịch vụ", 1, T11,
                        tien)));
        List<String> loi = trenLuongFx(() -> kiemTra(MauIn.bienLai(p, false),
                SoTien.hienThi(tien)));
        assertTrue(loi.isEmpty(), "Biên lai " + so + ":\n  "
                + String.join("\n  ", loi));
    }

    @ParameterizedTest
    @ValueSource(strings = {"10000000", "123456789", "1000000000"})
    void thongBaoInDuSoTienMotDong(String so) throws Exception {
        BigDecimal tien = new BigDecimal(so);
        TomTatHo ho = new TomTatHo(new HoGiaDinh(1, "PH01", LoaiCanHo.PENTHOUSE,
                30, new BigDecimal("250"), null, LocalDate.of(2026, 1, 1),
                TrangThaiHo.DANG_O), "Nguyễn Văn An", 3);
        ThongBaoPhi tb = new ThongBaoPhi(ho, T11, List.of(new KhoanNo(1, 1,
                "PH01", 1, "Phí dịch vụ", T11, tien)), List.of(new KhoanNo(2,
                1, "PH01", 1, "Phí dịch vụ", T11.minusMonths(1), tien)));
        String tong = SoTien.hienThi(tien.add(tien));
        List<String> loi = trenLuongFx(() -> kiemTra(
                MauIn.thongBao(tb, LocalDate.of(2026, 11, 9)),
                SoTien.hienThi(tien), tong));
        assertTrue(loi.isEmpty(), "Thông báo " + so + ":\n  "
                + String.join("\n  ", loi));
    }

    /** Dung trang trong Scene tam, kiem cac o so tien da cho; tra ve loi. */
    private static List<String> kiemTra(Node trang, String... soTien) {
        Scene scene = new Scene(new Group(trang));
        scene.getStylesheets().add(
                MauInTest.class.getResource(CSS).toExternalForm());
        Parent goc = (Parent) trang;
        goc.applyCss();
        goc.layout();
        goc.applyCss();
        goc.layout();
        List<Label> nhan = new ArrayList<>();
        thuThap(goc, nhan);
        List<String> loi = new ArrayList<>();
        for (String can : soTien) {
            long tim = nhan.stream().filter(l -> can.equals(l.getText()))
                    .count();
            assertTrue(tim > 0, "Không có ô \"" + can + "\" trên trang");
            for (Label l : nhan) {
                if (can.equals(l.getText())) {
                    kiemO(l, loi);
                }
            }
        }
        return loi;
    }

    private static void kiemO(Label l, List<String> loi) {
        String hienThi = null;
        for (Node con : l.getChildrenUnmodifiable()) {
            if (con instanceof Text t) {
                hienThi = t.getText();
            }
        }
        if (!l.getText().equals(hienThi)) {
            loi.add("\"" + l.getText() + "\" bị cắt thành \"" + hienThi
                    + "\"");
        }
        Text mau = new Text("0");
        mau.setFont(l.getFont());
        double mot = mau.getLayoutBounds().getHeight()
                + l.getInsets().getTop() + l.getInsets().getBottom();
        if (l.getHeight() > mot * 1.5) {
            loi.add("\"" + l.getText() + "\" xuống dòng: cao "
                    + Math.round(l.getHeight()) + " > một dòng "
                    + Math.round(mot));
        }
    }

    private static void thuThap(Node node, List<Label> kq) {
        if (node instanceof Label l && l.getText() != null) {
            kq.add(l);
        }
        if (node instanceof Parent p) {
            for (Node con : p.getChildrenUnmodifiable()) {
                thuThap(con, kq);
            }
        }
    }

    /** Moi trang ngang vua khung giay A4 nam ngang, du moi dong khong sot. */
    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void danhSachTamVangChiaTrangTheoChieuCaoThuc(boolean dai)
            throws Exception {
        List<TamVang> ds = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            ds.add(new TamVang(dai ? "Nguyễn Thị Hoàng Thanh Thảo " + i
                    : "Võ Thị Mai " + i, LocalDate.of(1990, 1, 1), "001099001"
                    + (100 + i), "A06" + (10 + i % 20), LocalDate.of(2026, 11,
                            1), LocalDate.of(2026, 12, 1), dai
                                    ? "Đi công tác dài ngày tại thành phố Hồ"
                                    + " Chí Minh theo quyết định của cơ quan,"
                                    + " có xác nhận của công ty" : "Về quê"));
        }
        double cao = 960 * 487.0 / 734;
        List<double[]> kq = trenLuongFx(() -> {
            List<double[]> r = new ArrayList<>();
            List<Node> trang = MauIn.danhSachTamVang(ds,
                    LocalDate.of(2026, 11, 9));
            for (Node n : trang) {
                Scene tam = new Scene(new Group(n));
                tam.getStylesheets().add(
                        MauInTest.class.getResource(CSS).toExternalForm());
                ((javafx.scene.layout.Region) n).applyCss();
                ((javafx.scene.layout.Region) n).autosize();
                ((Parent) n).layout();
                List<Label> nhan = new ArrayList<>();
                thuThap((Parent) n, nhan);
                long dong = nhan.stream().filter(l -> l.getText()
                        .startsWith(dai ? "Nguyễn Thị Hoàng" : "Võ Thị"))
                        .count();
                r.add(new double[] {n.getBoundsInLocal().getHeight(), dong});
            }
            return r;
        });
        double tong = 0;
        for (double[] t : kq) {
            assertTrue(t[0] <= cao + 0.5, "Trang cao " + t[0] + " > " + cao);
            tong += t[1];
        }
        assertEquals(45, (int) tong, "Mọi dòng phải có mặt, không sót");
        assertTrue(kq.size() > 1);
        // Ten ngan, ly do ngan: nhieu dong hon ban dai.
        if (!dai) {
            assertTrue(kq.get(0)[1] >= 12, "Dòng ngắn nên chứa ≥ 12/trang: "
                    + kq.get(0)[1]);
        }
    }

    private interface CongViec<T> {
        T chay() throws Exception;
    }

    private static <T> T trenLuongFx(CongViec<T> viec) throws Exception {
        CompletableFuture<T> ketQua = new CompletableFuture<>();
        Platform.runLater(() -> {
            try {
                ketQua.complete(viec.chay());
            } catch (Throwable t) {
                ketQua.completeExceptionally(t);
            }
        });
        return ketQua.get(30, TimeUnit.SECONDS);
    }

    @Test
    void soTienLonNhatDuocKiemLaMucTranNghiepVu() {
        assertEquals(new BigDecimal("1000000000"), SoTien.TOI_DA);
    }
}
