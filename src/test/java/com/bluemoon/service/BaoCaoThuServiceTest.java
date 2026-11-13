package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.model.BaoCaoThu;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Báo cáo thu ra Excel (US-018, AC-050, AC-051): phân quyền, tháng chưa có
 * phiếu bị từ chối trước khi tạo tệp, tệp ghi ra đọc lại được.
 */
class BaoCaoThuServiceTest {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T10 = YearMonth.of(2026, 10);
    private static final YearMonth T11 = YearMonth.of(2026, 11);
    private static final TaiKhoan QT = new TaiKhoan(1, "admin", "x",
            "Quản trị", VaiTro.QUAN_TRI, true);
    private static final TaiKhoan THU_QUY = new TaiKhoan(2, "thuquy", "x",
            "Thủ quỹ", VaiTro.THU_QUY, true);

    @TempDir
    Path thuMuc;

    private final List<YearMonth> daHoi = new ArrayList<>();
    private BaoCaoThuService service;

    /** Chỉ tháng 11 có một phiếu. */
    private BaoCaoThu theoThang(YearMonth thang) {
        daHoi.add(thang);
        if (!thang.equals(T11)) {
            return new BaoCaoThu(thang, List.of(), List.of());
        }
        return BaoCaoThu.gom(thang, List.of(new BaoCaoThu.ChiTiet(1,
                "A0601", "Nguyễn Thị Lan", new BigDecimal("377500"),
                LocalDateTime.of(2026, 11, 3, 9, 0), "PT-2026-00001", 1,
                "Phí dịch vụ", LoaiKhoanPhi.BAT_BUOC,
                new BigDecimal("377500"))));
    }

    @BeforeEach
    void chuanBi() {
        Clock ngay12T11 = Clock.fixed(LocalDateTime.of(2026, 11, 12, 9, 0)
                .atZone(VN).toInstant(), VN);
        service = new BaoCaoThuService(this::theoThang, ngay12T11);
    }

    private long soTep() throws IOException {
        try (Stream<Path> ds = Files.list(thuMuc)) {
            return ds.count();
        }
    }

    @Test
    void thangMacDinhLaThangHienTaiTheoDongHo() {
        assertEquals(T11, service.thangHienTai());
    }

    /** AC-051: tháng chưa có phiếu bị từ chối, chưa tạo tệp nào. */
    @Test
    void thangKhongCoPhieuBiTuChoiTruocKhiTaoTep() throws IOException {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.baoCao(QT, T10));
        assertEquals("Tháng 10/2026 chưa có phiếu thu nào, không có dữ liệu"
                + " để xuất.", e.getMessage());
        Path tep = thuMuc.resolve("a.xlsx");
        e = assertThrows(NghiepVuException.class, () -> service.xuat(QT,
                new BaoCaoThu(T10, List.of(), List.of()), tep));
        assertEquals("Không có dữ liệu để xuất.", e.getMessage());
        assertEquals(0, soTep(), "Không tạo tệp nào");
    }

    /** AC-050: tệp ghi ra có dữ liệu của tháng, ngày lập theo đồng hồ. */
    @Test
    void xuatRaTepDocLaiDuoc() throws IOException {
        BaoCaoThu bc = service.baoCao(THU_QUY, T11);
        Path tep = thuMuc.resolve("bao-cao-thu-2026-11.xlsx");
        service.xuat(THU_QUY, bc, tep);
        try (XSSFWorkbook wb = new XSSFWorkbook(Files.newInputStream(tep))) {
            var sh = wb.getSheetAt(0);
            assertEquals("Lập ngày 12/11/2026",
                    sh.getRow(1).getCell(0).getStringCellValue());
            assertEquals("A0601", sh.getRow(4).getCell(1)
                    .getStringCellValue());
            assertEquals(377500, sh.getRow(4).getCell(3)
                    .getNumericCellValue());
        }
        assertEquals(1, soTep());
    }

    @Test
    void loiGhiTepThanhThongBaoNghiepVu() {
        BaoCaoThu bc = service.baoCao(QT, T11);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.xuat(QT, bc,
                        thuMuc.resolve("khong-co").resolve("a.xlsx")));
        assertEquals("Không tìm thấy thư mục để lưu tệp.", e.getMessage());
    }

    @Test
    void caHaiVaiTroDuocXuatChuaDangNhapBiTuChoi() {
        service.baoCao(QT, T11);
        service.baoCao(THU_QUY, T11);
        BaoCaoThu bc = service.baoCao(QT, T11);
        Path tep = thuMuc.resolve("a.xlsx");
        for (Runnable r : List.<Runnable>of(
                () -> service.baoCao(null, T11),
                () -> service.baoCao(QT.voiHoatDong(false), T11),
                () -> service.xuat(null, bc, tep))) {
            NghiepVuException e = assertThrows(NghiepVuException.class,
                    r::run);
            assertEquals(PhanQuyen.LOI_CHUA_DANG_NHAP, e.getMessage());
        }
        assertFalse(Files.exists(tep));
        assertEquals(3, daHoi.size(), "Bị từ chối thì không đọc dữ liệu");
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.baoCao(QT, null));
        assertTrue(e.getMessage().contains("tháng"));
    }
}
