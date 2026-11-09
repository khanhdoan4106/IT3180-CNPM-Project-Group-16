package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.XungDotException;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.DongGopQuy;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiDongGop;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ThuPhiServiceTest {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T11 = YearMonth.of(2026, 11);
    private static final YearMonth T12 = YearMonth.of(2026, 12);
    private static final HinhThucThanhToan TIEN_MAT =
            HinhThucThanhToan.TIEN_MAT;

    private KhoThuPhiGia kho;
    private KhoPhieuThuGia khoPhieu;
    private ThuPhiService service;
    private TaiKhoan admin;
    private TaiKhoan thuQuy;
    private TaiKhoan thuQuy2;
    private int dichVu;
    private int quanLy;
    private int quyNgheo;
    private int a0601;
    private int dv11;
    private int dv12;
    private int ql12;

    @BeforeEach
    void chuanBi() {
        kho = new KhoThuPhiGia();
        khoPhieu = new KhoPhieuThuGia(kho);
        service = dichVu(LocalDateTime.of(2026, 12, 3, 9, 30));
        admin = new TaiKhoan(1, "admin", "x", "Nguyễn Văn An",
                VaiTro.QUAN_TRI, true);
        thuQuy = new TaiKhoan(2, "thuquy", "x", "Trần Thị Bình",
                VaiTro.THU_QUY, true);
        thuQuy2 = new TaiKhoan(3, "thuquy2", "x", "Lê Văn Cường",
                VaiTro.THU_QUY, true);
        List.of(admin, thuQuy, thuQuy2).forEach(khoPhieu::themTaiKhoan);
        dichVu = themKhoan(KhoanPhiService.PHI_DICH_VU, LoaiKhoanPhi.BAT_BUOC,
                LocalDate.of(2026, 1, 1), null);
        quanLy = themKhoan(KhoanPhiService.PHI_QUAN_LY, LoaiKhoanPhi.BAT_BUOC,
                LocalDate.of(2026, 1, 1), null);
        quyNgheo = themKhoan("Quỹ vì người nghèo 2026",
                LoaiKhoanPhi.TU_NGUYEN, LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 31));
        themKhoan("Quỹ đã hết hạn", LoaiKhoanPhi.TU_NGUYEN,
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));
        a0601 = khoPhieu.themHo("A0601", TrangThaiHo.DANG_O, "Nguyễn Thị Lan");
        dv11 = khoPhieu.themPhaiNop(a0601, dichVu, T11, "377500");
        dv12 = khoPhieu.themPhaiNop(a0601, dichVu, T12, "377500");
        ql12 = khoPhieu.themPhaiNop(a0601, quanLy, T12, "528500");
    }

    private ThuPhiService dichVu(LocalDateTime luc) {
        return new ThuPhiService(khoPhieu, kho.khoanPhiDao,
                Clock.fixed(luc.atZone(VN).toInstant(), VN));
    }

    private int themKhoan(String ten, LoaiKhoanPhi loai, LocalDate tu,
            LocalDate den) {
        boolean batBuoc = loai == LoaiKhoanPhi.BAT_BUOC;
        return kho.khoanPhiDao.them(new KhoanPhi(0, ten, loai,
                batBuoc ? CachTinhPhi.THEO_DIEN_TICH
                        : CachTinhPhi.KHONG_CO_DINH,
                batBuoc ? new BigDecimal("5000") : null, tu, den,
                TrangThaiKhoanPhi.DANG_THU));
    }

    private PhieuThu thuTatCa(TaiKhoan nguoi) {
        return service.thu(nguoi, a0601, List.of(dv11, dv12, ql12), null,
                TIEN_MAT);
    }

    private DongGopQuy quy(int hoId) {
        return service.thongTin(admin, hoId).dongGop().stream()
                .filter(d -> d.quy().id() == quyNgheo).findFirst()
                .orElseThrow();
    }

    private static void loi(String thongBao, Runnable viec) {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                viec::run);
        assertEquals(thongBao, e.getMessage());
    }

    // ---------- US-010: tra cuu ----------

    @Test
    void traCuuTheoMaKhongPhanBietHoaThuong() {
        List<TomTatHo> ds = service.traCuu(thuQuy, "  a0601 ");
        assertEquals(1, ds.size());
        assertEquals("Nguyễn Thị Lan", ds.get(0).tenChuHo());
    }

    @Test
    void traCuuMaKhongTonTaiBaoKhongTimThay() {
        loi(ThuPhiService.LOI_KHONG_TIM_THAY,
                () -> service.traCuu(thuQuy, "Z9999"));
        loi("Chưa nhập mã phòng", () -> service.traCuu(thuQuy, "  "));
    }

    @Test
    void traCuuThayHoDaChuyenDiConNoXepSauHoDangO() {
        int cu = khoPhieu.themHo("B0202", TrangThaiHo.DA_CHUYEN_DI, "Hộ cũ");
        khoPhieu.themPhaiNop(cu, dichVu, T11, "100000");
        khoPhieu.themHo("B0202", TrangThaiHo.DA_CHUYEN_DI, "Hộ hết nợ");
        int moi = khoPhieu.themHo("B0202", TrangThaiHo.DANG_O, "Hộ mới");
        List<TomTatHo> ds = service.traCuu(thuQuy, "B0202");
        assertEquals(List.of(moi, cu),
                ds.stream().map(t -> t.ho().getId()).toList());
    }

    @Test
    void hoDaChuyenDiChiThuKhoanNoKhongGhiDongGop() {
        // Gia dinh 39
        int cu = khoPhieu.themHo("B0202", TrangThaiHo.DA_CHUYEN_DI, "Hộ cũ");
        int no = khoPhieu.themPhaiNop(cu, dichVu, T11, "100000");
        assertTrue(service.thongTin(thuQuy, cu).dongGop().isEmpty());
        for (String nhap : List.of("50.000", "0")) {
            NghiepVuException e = assertThrows(NghiepVuException.class,
                    () -> service.thu(thuQuy, cu, List.of(no),
                            Map.of(quyNgheo, nhap), TIEN_MAT));
            assertEquals("Hộ đã chuyển đi chỉ thu các khoản còn nợ",
                    e.getMessage());
        }
        assertEquals(0, service.homNay(admin).size());
        PhieuThu p = service.thu(thuQuy, cu, List.of(no), Map.of(quyNgheo, ""),
                TIEN_MAT);
        assertEquals(0, new BigDecimal("100000").compareTo(p.tongTien()));
    }

    @Test
    void thongTinGomKhoanChuaDongVaQuyDangMo() {
        ThuPhiService.ThongTinThu tt = service.thongTin(thuQuy, a0601);
        assertEquals(List.of(dv11, dv12, ql12), tt.khoanChuaDong().stream()
                .map(KhoanNo::phaiNopId).toList());
        assertEquals(0, new BigDecimal("1283500").compareTo(tt.tongNo()));
        // Quy het han khong hien (AC-022 "khoan dang mo").
        assertEquals(List.of(quyNgheo), tt.dongGop().stream()
                .map(d -> d.quy().id()).toList());
        assertEquals(TrangThaiDongGop.CHUA_THU, quy(a0601).trangThai());
    }

    // ---------- US-011, US-012: thu tien ----------

    @Test
    void thuTatCaDuaCongNoVe0VaGhiNguoiThu() {
        PhieuThu p = thuTatCa(thuQuy);
        assertEquals("PT-2026-00001", p.soBienLai());
        assertEquals(0, new BigDecimal("1283500").compareTo(p.tongTien()));
        assertEquals(3, p.chiTiet().size());
        assertEquals(TIEN_MAT, p.hinhThuc());
        assertEquals(thuQuy.getId(), p.nguoiThuId());
        assertEquals("Trần Thị Bình", p.tenNguoiThu());
        assertEquals(LocalDateTime.of(2026, 12, 3, 9, 30), p.thoiDiem());
        assertTrue(service.thongTin(thuQuy, a0601).khoanChuaDong().isEmpty());
        assertEquals(0, kho.congNoDao.tongChuaDong(a0601).signum());
    }

    @Test
    void thuMotPhanChiGhiKhoanDuocChon() {
        PhieuThu p = service.thu(thuQuy, a0601, List.of(dv11), Map.of(),
                HinhThucThanhToan.CHUYEN_KHOAN);
        assertEquals(1, p.chiTiet().size());
        assertEquals("Phí dịch vụ tháng 11/2026", p.chiTiet().get(0).moTa());
        assertEquals(List.of(dv12, ql12), service.thongTin(thuQuy, a0601)
                .khoanChuaDong().stream().map(KhoanNo::phaiNopId).toList());
    }

    @Test
    void chuaChonHinhThucThiBaoLoi() {
        loi(ThuPhiService.LOI_CHUA_CHON_HINH_THUC,
                () -> service.thu(thuQuy, a0601, List.of(dv11), null, null));
        assertTrue(khoPhieu.phieu.isEmpty());
    }

    @Test
    void khongChonKhoanNaoThiBaoLoi() {
        loi("Chưa chọn khoản nào để thu", () -> service.thu(thuQuy, a0601,
                List.of(), Map.of(quyNgheo, "  "), TIEN_MAT));
    }

    @Test
    void khoanDaThuKhongThuLaiDuoc() {
        service.thu(thuQuy, a0601, List.of(dv11), null, TIEN_MAT);
        loi(ThuPhiService.LOI_DU_LIEU_DOI, () -> service.thu(thuQuy2, a0601,
                List.of(dv11), null, TIEN_MAT));
    }

    @Test
    void mayKhacVuaThuThiBaoTraCuuLai() {
        khoPhieu.xungDotKhiLap = XungDotException.Loai.DA_THU;
        loi(ThuPhiService.LOI_DU_LIEU_DOI, () -> thuTatCa(thuQuy));
        khoPhieu.xungDotKhiLap = XungDotException.Loai.HET_SO;
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> thuTatCa(thuQuy));
        assertTrue(e.getMessage().startsWith("Đã dùng hết số biên lai"));
    }

    @Test
    void soBienLaiLienTucVaDanhLaiKhiSangNam() {
        assertEquals("PT-2026-00001", service.thu(thuQuy, a0601,
                List.of(dv11), null, TIEN_MAT).soBienLai());
        assertEquals("PT-2026-00002", service.thu(thuQuy, a0601,
                List.of(dv12), null, TIEN_MAT).soBienLai());
        PhieuThu sangNam = dichVu(LocalDateTime.of(2027, 1, 2, 8, 0)).thu(
                thuQuy, a0601, List.of(ql12), null, TIEN_MAT);
        assertEquals("PT-2027-00001", sangNam.soBienLai());
    }

    // ---------- US-007: dong gop tu nguyen ----------

    @Test
    void dongGopNhieuLanCongDon() {
        service.thu(thuQuy, a0601, List.of(),
                Map.of(quyNgheo, "100.000"), TIEN_MAT);
        service.thu(thuQuy, a0601, List.of(),
                Map.of(quyNgheo, "50.000 đ"), TIEN_MAT);
        DongGopQuy q = quy(a0601);
        assertEquals(0, new BigDecimal("150000").compareTo(q.tongDaDong()));
        assertEquals(2, q.soLan());
        assertEquals(TrangThaiDongGop.DA_DONG, q.trangThai());
        loi("Hộ đã đóng Quỹ vì người nghèo 2026, không ghi \"không đóng\""
                + " được", () -> service.thu(thuQuy, a0601, List.of(),
                        Map.of(quyNgheo, "0"), null));
    }

    @Test
    void ghi0DongLaKhongDongVaPhieuKhongCoHinhThuc() {
        PhieuThu p = service.thu(thuQuy, a0601, List.of(),
                Map.of(quyNgheo, "0"), TIEN_MAT);
        assertTrue(p.khongThuTien());
        assertNull(p.hinhThuc());
        assertEquals(TrangThaiDongGop.KHONG_DONG, quy(a0601).trangThai());
        loi("Đã ghi hộ không đóng Quỹ vì người nghèo 2026", () -> service.thu(
                thuQuy, a0601, List.of(), Map.of(quyNgheo, "0"), null));
        // Sau do ho doi y dong tien thi tinh la da dong.
        service.thu(thuQuy, a0601, List.of(), Map.of(quyNgheo, "20.000"),
                TIEN_MAT);
        assertEquals(TrangThaiDongGop.DA_DONG, quy(a0601).trangThai());
    }

    @Test
    void thuChungKhoanBatBuocVaNhieuQuyTrenMotPhieu() {
        int quyBien = themKhoan("Quỹ biển đảo", LoaiKhoanPhi.TU_NGUYEN,
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 12, 31));
        PhieuThu p = service.thu(thuQuy, a0601, List.of(dv11),
                Map.of(quyNgheo, "200.000", quyBien, "50.000"), TIEN_MAT);
        assertEquals(3, p.chiTiet().size());
        assertEquals(0, new BigDecimal("627500").compareTo(p.tongTien()));
    }

    @Test
    void quyKhongMoHoacSoSaiBiTuChoi() {
        loi("Quỹ này không còn mở, không ghi đóng góp được",
                () -> service.thu(thuQuy, a0601, List.of(),
                        Map.of(4, "10.000"), TIEN_MAT));
        assertThrows(NghiepVuException.class, () -> service.thu(thuQuy,
                a0601, List.of(), Map.of(quyNgheo, "-5"), TIEN_MAT));
    }

    // ---------- US-014: huy phieu ----------

    @Test
    void thuQuyHuyPhieuMinhLapKhoanTroVeChuaDong() {
        PhieuThu p = thuTatCa(thuQuy);
        PhieuThu daHuy = service.huy(thuQuy, p.id(), "  Thu nhầm hộ  ");
        assertTrue(daHuy.daHuy());
        assertEquals("PT-2026-00001", daHuy.soBienLai());
        assertEquals("Trần Thị Bình", daHuy.tenNguoiHuy());
        assertEquals("Thu nhầm hộ", daHuy.lyDoHuy());
        assertEquals(3, service.thongTin(thuQuy, a0601).khoanChuaDong()
                .size());
        // Phieu huy van con trong lich su (AC-043); thu lai duoc.
        assertEquals(1, service.lichSu(thuQuy, a0601).size());
        assertEquals("PT-2026-00002", thuTatCa(thuQuy).soBienLai());
    }

    @Test
    void biKhoaKhiThuThiBaoCoNguoiKhacDangThaoTac() {
        khoPhieu.biKhoa = true;
        List<Integer> ids = service.thongTin(admin, a0601).khoanChuaDong()
                .stream().map(KhoanNo::phaiNopId).toList();
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.thu(admin, a0601, ids, null,
                        HinhThucThanhToan.TIEN_MAT));
        assertEquals("Có người khác đang thao tác trên hộ này, vui lòng"
                + " thử lại", e.getMessage());
        assertEquals(0, service.homNay(admin).size());
    }

    @Test
    void thuQuyKhongHuyPhieuNguoiKhacQuanTriThiDuoc() {
        PhieuThu p = thuTatCa(thuQuy);
        loi("Thủ quỹ chỉ hủy được phiếu do mình lập",
                () -> service.huy(thuQuy2, p.id(), "Nhầm"));
        assertTrue(service.huy(admin, p.id(), "Nhầm").daHuy());
    }

    @Test
    void khongHuyPhieuNgayTruoc() {
        PhieuThu p = thuTatCa(thuQuy);
        ThuPhiService homSau = dichVu(LocalDateTime.of(2026, 12, 4, 0, 0, 5));
        loi("Chỉ hủy được phiếu lập trong ngày. Phiếu PT-2026-00001 lập"
                + " ngày 03/12/2026", () -> homSau.huy(admin, p.id(), "Nhầm"));
        assertFalse(khoPhieu.phieu.get(p.id()).daHuy());
    }

    @Test
    void huyCanLyDoVaKhongHuyHaiLan() {
        PhieuThu p = thuTatCa(thuQuy);
        loi("Vui lòng nhập lý do hủy phiếu",
                () -> service.huy(thuQuy, p.id(), "   "));
        assertThrows(NghiepVuException.class,
                () -> service.huy(thuQuy, p.id(), "x".repeat(201)));
        service.huy(thuQuy, p.id(), "Nhầm");
        loi("Phiếu PT-2026-00001 đã bị hủy trước đó",
                () -> service.huy(admin, p.id(), "Lần hai"));
    }

    @Test
    void khoanDaThuKhongSuaDuocChoDenKhiHuyPhieu() {
        CongNoService congNo = new CongNoService(kho.congNoDao,
                kho.khoanPhiDao, new KhoGia().hoDao);
        PhieuThu p = thuTatCa(thuQuy);
        assertTrue(kho.congNoDao.theoThang(T12).stream()
                .allMatch(x -> x.daDong()));
        loi(CongNoService.LOI_DA_THU,
                () -> congNo.suaSoTien(thuQuy, dv12, "300.000", null));
        service.huy(thuQuy, p.id(), "Nhầm");
        congNo.suaSoTien(thuQuy, dv12, "300.000", null);
    }

    // ---------- US-013: in bien lai ----------

    @Test
    void inLanHaiLaBanInLai() {
        PhieuThu p = thuTatCa(thuQuy);
        assertEquals(0, service.choIn(thuQuy, p.id()).soLanIn());
        service.daIn(thuQuy, p.id());
        assertEquals(1, service.choIn(thuQuy, p.id()).soLanIn());
    }

    @Test
    void phieuHuyHoacKhongThuTienKhongIn() {
        PhieuThu khong = service.thu(thuQuy, a0601, List.of(),
                Map.of(quyNgheo, "0"), null);
        loi("Phiếu PT-2026-00001 không thu tiền, không in biên lai",
                () -> service.choIn(thuQuy, khong.id()));
        PhieuThu p = thuTatCa(thuQuy);
        service.huy(thuQuy, p.id(), "Nhầm");
        loi("Phiếu PT-2026-00002 đã hủy, không in biên lai",
                () -> service.choIn(thuQuy, p.id()));
    }

    @Test
    void phieuHomNayGomCaPhieuHuy() {
        PhieuThu p = thuTatCa(thuQuy);
        service.huy(thuQuy, p.id(), "Nhầm");
        thuTatCa(thuQuy);
        assertEquals(2, service.homNay(admin).size());
        assertTrue(dichVu(LocalDateTime.of(2026, 12, 4, 8, 0))
                .homNay(admin).isEmpty());
    }

    @Test
    void canDangNhap() {
        loi(PhanQuyen.LOI_CHUA_DANG_NHAP,
                () -> service.traCuu(null, "A0601"));
        TaiKhoan khoa = new TaiKhoan(4, "khoa", "x", "Bị khóa",
                VaiTro.THU_QUY, false);
        loi(PhanQuyen.LOI_CHUA_DANG_NHAP, () -> thuTatCa(khoa));
    }
}
