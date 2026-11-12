package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.config.Database;
import com.bluemoon.dao.BaoCaoThuDaoImpl;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
import com.bluemoon.model.BaoCaoThu;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.PhieuThuMoi;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.service.BaoCaoThuService;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.NghiepVuException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Báo cáo thu trên MySQL thật (US-018, AC-050, AC-051, giả định 41): phiếu
 * đã hủy không có, ranh giới tháng, chủ hộ, số biên lai, phiếu thu nhiều
 * tháng của một khoản cộng vào một cột.
 */
class BaoCaoThuIT {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T10 = YearMonth.of(2026, 10);
    private static final YearMonth T11 = YearMonth.of(2026, 11);

    @TempDir
    Path thuMuc;

    private final KhoanPhiDaoImpl khoanPhiDao = new KhoanPhiDaoImpl();
    private final HoGiaDinhDaoImpl hoDao = new HoGiaDinhDaoImpl();
    private final PhieuThuDaoImpl phieuDao = new PhieuThuDaoImpl();
    private TaiKhoan admin;
    private CongNoService congNo;
    private BaoCaoThuService baoCao;
    private KhoanPhi dichVu;
    private KhoanPhi quanLy;
    private KhoanPhi quy;
    private KhoanPhi khuyenHoc;
    private int hoA;
    private int hoB;

    @BeforeEach
    void chuanBi() {
        admin = CsdlThu.dungLai();
        Clock ngay12T11 = Clock.fixed(LocalDateTime.of(2026, 11, 12, 9, 0)
                .atZone(VN).toInstant(), VN);
        congNo = new CongNoService(new CongNoDaoImpl(), khoanPhiDao, hoDao,
                ngay12T11);
        baoCao = new BaoCaoThuService(new BaoCaoThuDaoImpl(), ngay12T11);
        dichVu = khoan("Phí dịch vụ", LoaiKhoanPhi.BAT_BUOC, "5000");
        quanLy = khoan("Phí quản lý", LoaiKhoanPhi.BAT_BUOC, "7000");
        quy = khoan("Quỹ vì người nghèo 2026", LoaiKhoanPhi.TU_NGUYEN, null);
        khuyenHoc = khoan("Quỹ khuyến học 2026", LoaiKhoanPhi.TU_NGUYEN,
                null);
        // Ho dau tien co them mot thanh vien, de id nhan khau cua chu ho
        // cac ho sau khac id ho (chu ho phai lay theo quan he, khong theo
        // id trung nhau).
        int hoDau = ho("A0501", "50.00", "001182004560");
        themThanhVien(hoDau);
        hoA = ho("A0601", "75.50", "001182004561");
        hoB = ho("A0602", "60.00", "001182004562");
        congNo.chot(admin, T10);
        congNo.chot(admin, T11);
        // PT-2026-00001: A dong phi dich vu T10 va T11, phi quan ly T11.
        lap(hoA, LocalDateTime.of(2026, 11, 3, 9, 0),
                dong(hoA, T10, dichVu), dong(hoA, T11, dichVu),
                dong(hoA, T11, quanLy));
        // PT-2026-00002: 23:59:59 ngay cuoi thang 11, tinh vao thang 11.
        lap(hoB, LocalDateTime.of(2026, 11, 30, 23, 59, 59),
                gop(quy, "50000"));
        // PT-2026-00003: 00:00:00 ngay dau thang 12, khong thuoc thang 11.
        lap(hoB, LocalDateTime.of(2026, 12, 1, 0, 0, 0), gop(quy, "70000"));
        // PT-2026-00004: 23:59:59 ngay cuoi thang 10.
        lap(hoA, LocalDateTime.of(2026, 10, 31, 23, 59, 59),
                gop(quy, "10000"));
        // PT-2026-00005: da huy, quy khuyen hoc chi co o phieu nay.
        int huy = lap(hoB, LocalDateTime.of(2026, 11, 4, 8, 0),
                gop(khuyenHoc, "90000"));
        phieuDao.huy(huy, admin.getId(), LocalDateTime.of(2026, 11, 4, 9, 0),
                "Thu nhầm");
    }

    private KhoanPhi khoan(String ten, LoaiKhoanPhi loai, String donGia) {
        boolean batBuoc = loai == LoaiKhoanPhi.BAT_BUOC;
        KhoanPhi k = new KhoanPhi(0, ten, loai, batBuoc
                ? CachTinhPhi.THEO_DIEN_TICH : CachTinhPhi.KHONG_CO_DINH,
                donGia == null ? null : new BigDecimal(donGia),
                batBuoc ? LocalDate.of(2026, 1, 1) : LocalDate.of(2026, 10, 1),
                batBuoc ? null : LocalDate.of(2026, 12, 31),
                TrangThaiKhoanPhi.DANG_THU);
        return k.voiId(khoanPhiDao.them(k, admin.getId()));
    }

    private int ho(String ma, String dienTich, String cccd) {
        return hoDao.them(new HoGiaDinh(0, ma, LoaiCanHo.CAN_HO, 6,
                new BigDecimal(dienTich), null, LocalDate.of(2026, 1, 1),
                TrangThaiHo.DANG_O),
                new NhanKhau(0, 0, "Chủ hộ " + ma, LocalDate.of(1980, 1, 1),
                        GioiTinh.NU, cccd, QuanHeChuHo.CHU_HO,
                        TrangThaiNhanKhau.DANG_O, LoaiCuTru.THUONG_TRU, null),
                null, admin.getId());
    }

    private static void themThanhVien(int hoId) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement("INSERT INTO "
                        + "nhan_khau (ho_id, ho_ten, ngay_sinh, gioi_tinh, "
                        + "quan_he) VALUES (?, 'Thành viên A0501', "
                        + "'2015-01-01', 'NAM', 'CON')")) {
            ps.setInt(1, hoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private PhieuThuMoi.Dong dong(int hoId, YearMonth thang, KhoanPhi k) {
        PhaiNop p = congNo.theoThang(admin, thang).stream()
                .filter(x -> x.hoId() == hoId && x.khoanPhiId() == k.id())
                .findFirst().orElseThrow();
        return new PhieuThuMoi.Dong(k.id(), p.id(), p.soTien());
    }

    private static PhieuThuMoi.Dong gop(KhoanPhi k, String soTien) {
        return new PhieuThuMoi.Dong(k.id(), null, new BigDecimal(soTien));
    }

    private int lap(int hoId, LocalDateTime luc, PhieuThuMoi.Dong... d) {
        return phieuDao.lapPhieu(new PhieuThuMoi(hoId,
                HinhThucThanhToan.TIEN_MAT, admin.getId(), luc, List.of(d)))
                .id();
    }

    private static void bang(String mong, BigDecimal thuc) {
        assertEquals(0, new BigDecimal(mong).compareTo(thuc),
                "mong " + mong + ", thực " + thuc);
    }

    /** Phiếu đã hủy không có; ranh giới tháng; thứ tự theo thời điểm. */
    @Test
    void phieuTrongThangDungRanhGioiKhongCoPhieuHuy() {
        BaoCaoThu bc = baoCao.baoCao(admin, T11);
        assertEquals(List.of("PT-2026-00001", "PT-2026-00002"),
                bc.dong().stream().map(BaoCaoThu.Dong::soBienLai).toList());
        // Quy khuyen hoc chi co trong phieu da huy: khong thanh cot.
        assertEquals(List.of("Phí dịch vụ", "Phí quản lý",
                "Quỹ vì người nghèo 2026"),
                bc.cot().stream().map(BaoCaoThu.Cot::ten).toList());
        BaoCaoThu t10 = baoCao.baoCao(admin, T10);
        assertEquals(List.of("PT-2026-00004"), t10.dong().stream()
                .map(BaoCaoThu.Dong::soBienLai).toList());
        // 00:00:00 ngay dau thang 12 thuoc thang 12.
        BaoCaoThu t12 = baoCao.baoCao(admin, YearMonth.of(2026, 12));
        assertEquals(List.of("PT-2026-00003"), t12.dong().stream()
                .map(BaoCaoThu.Dong::soBienLai).toList());
    }

    /** Chủ hộ, số biên lai, phiếu thu hai tháng phí dịch vụ một cột. */
    @Test
    void chuHoSoBienLaiVaCongCungKhoan() {
        BaoCaoThu.Dong a = baoCao.baoCao(admin, T11).dong().get(0);
        assertEquals("A0601", a.maCanHo());
        assertEquals("Chủ hộ A0601", a.chuHo());
        assertEquals("PT-2026-00001", a.soBienLai());
        bang("755000", a.tien().get(dichVu.id())); // 75,5 x 5.000 x 2
        bang("528500", a.tien().get(quanLy.id()));
        assertNull(a.tien().get(quy.id()));
        bang("1283500", a.tong());
        assertEquals(LocalDateTime.of(2026, 11, 3, 9, 0), a.thoiDiem());
    }

    /** AC-051: tháng chưa có phiếu (chỉ có phiếu đã hủy cũng vậy). */
    @Test
    void thangKhongCoPhieuBiTuChoi() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> baoCao.baoCao(admin, YearMonth.of(2026, 9)));
        assertEquals("Tháng 9/2026 chưa có phiếu thu nào, không có dữ liệu"
                + " để xuất.", e.getMessage());
    }

    /** AC-050: tệp ghi từ CSDL thật có hai dòng phiếu và dòng Tổng cộng. */
    @Test
    void xuatTepTuCsdl() throws IOException {
        Path tep = thuMuc.resolve("bao-cao-thu-2026-11.xlsx");
        baoCao.xuat(admin, baoCao.baoCao(admin, T11), tep);
        try (XSSFWorkbook wb = new XSSFWorkbook(Files.newInputStream(tep))) {
            Sheet sh = wb.getSheetAt(0);
            assertEquals("Chủ hộ A0602",
                    sh.getRow(5).getCell(2).getStringCellValue());
            assertEquals(50000, sh.getRow(5).getCell(5)
                    .getNumericCellValue());
            assertEquals("Tổng cộng",
                    sh.getRow(6).getCell(0).getStringCellValue());
            assertEquals(1333500, sh.getRow(6).getCell(6)
                    .getNumericCellValue());
        }
    }
}
