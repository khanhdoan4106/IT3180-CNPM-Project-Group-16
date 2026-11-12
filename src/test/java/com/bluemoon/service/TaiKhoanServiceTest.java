package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.MatKhauUtil;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaiKhoanServiceTest {

    /** DAO gia lap trong bo nho, khong can MySQL. */
    private static class DaoGia implements TaiKhoanDao {
        private final Map<String, TaiKhoan> theoTen = new HashMap<>();
        private String matKhauMoiNhat;
        /** Gia lap may khac vua dang ky cung ten truoc khi INSERT. */
        private boolean trungKhiThem;

        @Override
        public Optional<TaiKhoan> timTheoTenDangNhap(String ten) {
            return Optional.ofNullable(theoTen.get(ten));
        }

        @Override
        public void capNhatMatKhau(int id, String matKhauBam) {
            matKhauMoiNhat = matKhauBam;
        }

        @Override
        public void datLaiMatKhau(int id, String matKhauBam, int nguoiId) {
            matKhauMoiNhat = matKhauBam;
        }

        @Override
        public Optional<TaiKhoan> timTheoId(int id) {
            return theoTen.values().stream().filter(t -> t.getId() == id)
                    .findFirst();
        }

        @Override
        public List<TaiKhoan> danhSach() {
            return theoTen.values().stream().sorted(Comparator.comparing(
                    TaiKhoan::getTenDangNhap)).toList();
        }

        @Override
        public void capNhatThongTin(int id, String hoTen, String sdt) {
            TaiKhoan t = timTheoId(id).orElseThrow();
            theoTen.put(t.getTenDangNhap(), t.voiThongTin(hoTen, sdt));
        }

        @Override
        public void capNhatVaiTro(int id, VaiTro vaiTro, int nguoiId) {
            TaiKhoan t = timTheoId(id).orElseThrow();
            theoTen.put(t.getTenDangNhap(), new TaiKhoan(id,
                    t.getTenDangNhap(), t.getMatKhauBam(), t.getHoTen(),
                    vaiTro, t.isHoatDong()));
        }

        @Override
        public void capNhatHoatDong(int id, boolean hoatDong,
                int nguoiId) {
            TaiKhoan t = timTheoId(id).orElseThrow();
            theoTen.put(t.getTenDangNhap(), new TaiKhoan(id,
                    t.getTenDangNhap(), t.getMatKhauBam(), t.getHoTen(),
                    t.getVaiTro(), hoatDong));
        }

        @Override
        public int them(TaiKhoan tk, int nguoiId) {
            if (trungKhiThem) {
                throw new TrungKhoaException("ten_dang_nhap", null);
            }
            int id = theoTen.size() + 1;
            theoTen.put(tk.getTenDangNhap(), new TaiKhoan(id,
                    tk.getTenDangNhap(), tk.getMatKhauBam(), tk.getHoTen(),
                    tk.getVaiTro(), tk.isHoatDong()));
            return id;
        }
    }

    /** Bam mot lan cho ca lop; BCrypt do kho 12 mat ~0.3 giay moi lan. */
    private static final String BAM_MAU = MatKhauUtil.bam("matkhau123");

    private DaoGia dao;
    private TaiKhoanService service;
    private TaiKhoan admin;
    private TaiKhoan thuQuy;

    @BeforeEach
    void chuanBi() {
        dao = new DaoGia();
        admin = new TaiKhoan(1, "admin", BAM_MAU,
                "Quan tri", VaiTro.QUAN_TRI, true);
        dao.theoTen.put("admin", admin);
        thuQuy = new TaiKhoan(3, "thuquy", BAM_MAU,
                "Thu quy", VaiTro.THU_QUY, true);
        dao.theoTen.put("thuquy", thuQuy);
        dao.theoTen.put("khoa", new TaiKhoan(2, "khoa",
                BAM_MAU, "Bi khoa",
                VaiTro.THU_QUY, false));
        service = new TaiKhoanService(dao);
    }

    @Test
    void dangNhapDungThanhCong() {
        assertEquals(1, service.dangNhap("admin", "matkhau123").getId());
    }

    @Test
    void saiMatKhauBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("admin", "sai"));
    }

    @Test
    void khongCoTaiKhoanThongBaoGiongSaiMatKhau() {
        NghiepVuException a = assertThrows(NghiepVuException.class,
                () -> service.dangNhap("khongco", "x"));
        NghiepVuException b = assertThrows(NghiepVuException.class,
                () -> service.dangNhap("admin", "sai"));
        assertEquals(a.getMessage(), b.getMessage());
    }

    @Test
    void taiKhoanBiKhoaKhongDangNhapDuoc() {
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("khoa", "matkhau123"));
    }

    @Test
    void thieuThongTinBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("", "x"));
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("admin", ""));
    }

    @Test
    void doiMatKhauThanhCongLuuBam() {
        service.doiMatKhau(admin, "matkhau123", "matkhaumoi456",
                "matkhaumoi456");
        assertTrue(MatKhauUtil.kiemTra("matkhaumoi456",
                dao.matKhauMoiNhat));
    }

    @Test
    void doiMatKhauSaiMatKhauCu() {
        assertThrows(NghiepVuException.class,
                () -> service.doiMatKhau(admin, "sai", "matkhaumoi456",
                        "matkhaumoi456"));
    }

    @Test
    void matKhauMoiQuaNganBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.doiMatKhau(admin, "matkhau123", "123", "123"));
    }

    @Test
    void matKhauQuaDaiDangNhapBaoSaiThongTin() {
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("admin", "a".repeat(100)));
    }

    // US-020: dang ky tai khoan

    @Test
    void dangKyHopLeTaoTaiKhoan() { // AC-054
        TaiKhoan moi = service.dangKy(admin, "  ketoan01 ", "Nguyen Van A",
                "matkhau123", "matkhau123", VaiTro.THU_QUY);
        assertEquals("ketoan01", moi.getTenDangNhap());
        assertEquals(VaiTro.THU_QUY, moi.getVaiTro());
        assertTrue(moi.isHoatDong());
        assertEquals(moi.getId(), service.dangNhap("ketoan01",
                "matkhau123").getId());
    }

    @Test
    void dangKyLuuMatKhauDaBam() {
        service.dangKy(admin, "ketoan01", "Nguyen Van A", "matkhau123",
                "matkhau123", VaiTro.QUAN_TRI);
        String bam = dao.theoTen.get("ketoan01").getMatKhauBam();
        assertFalse(bam.contains("matkhau123"));
        assertTrue(MatKhauUtil.kiemTra("matkhau123", bam));
    }

    @Test
    void dangKyTrungTenBaoTrung() { // AC-055
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.dangKy(admin, "admin", "Nguoi khac",
                        "matkhau123", "matkhau123", VaiTro.QUAN_TRI));
        assertTrue(e.getMessage().contains("đã tồn tại"));
    }

    @Test
    void dangKyTrungLucLuuBaoTrung() { // AC-055, hai may cung luu
        dao.trungKhiThem = true;
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.dangKy(admin, "ketoan01", "Nguyen Van A",
                        "matkhau123", "matkhau123", VaiTro.THU_QUY));
        assertTrue(e.getMessage().contains("đã tồn tại"));
    }

    @Test
    void dangKyMatKhauNganBaoLoi() { // AC-056
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.dangKy(admin, "ketoan01", "Nguyen Van A",
                        "1234567", "1234567", VaiTro.THU_QUY));
        assertTrue(e.getMessage().contains("ít nhất "
                + TaiKhoanService.MAT_KHAU_TOI_THIEU));
        assertFalse(dao.theoTen.containsKey("ketoan01"));
    }

    @Test
    void dangKyMatKhauQuaDaiBaoLoi() {
        assertThrows(NghiepVuException.class,
                () -> service.dangKy(admin, "ketoan01", "Nguyen Van A",
                        "a".repeat(80), "a".repeat(80), VaiTro.THU_QUY));
    }

    @Test
    void dangKyNhapLaiKhongKhopBaoLoi() {
        assertThrows(NghiepVuException.class,
                () -> service.dangKy(admin, "ketoan01", "Nguyen Van A",
                        "matkhau123", "matkhau124", VaiTro.THU_QUY));
    }

    @Test
    void dangKyTenDangNhapSaiDangBaoLoi() {
        for (String ten : new String[] {"", "ab", "co dau cach",
                "ti\u1ebfng", "a".repeat(51)}) {
            assertThrows(NghiepVuException.class,
                    () -> service.dangKy(admin, ten, "Nguyen Van A",
                            "matkhau123", "matkhau123", VaiTro.THU_QUY),
                    ten);
        }
    }

    @Test
    void dangKyThieuHoTenHoacVaiTroBaoLoi() {
        assertThrows(NghiepVuException.class,
                () -> service.dangKy(admin, "ketoan01", "  ",
                        "matkhau123", "matkhau123", VaiTro.THU_QUY));
        assertThrows(NghiepVuException.class,
                () -> service.dangKy(admin, "ketoan01", "Nguyen Van A",
                        "matkhau123", "matkhau123", null));
    }

    @Test
    void chiQuanTriDuocDangKy() {
        assertThrows(NghiepVuException.class,
                () -> service.dangKy(thuQuy, "ketoan01", "Nguyen Van A",
                        "matkhau123", "matkhau123", VaiTro.THU_QUY));
        assertThrows(NghiepVuException.class,
                () -> service.dangKy(null, "ketoan01", "Nguyen Van A",
                        "matkhau123", "matkhau123", VaiTro.THU_QUY));
        assertFalse(dao.theoTen.containsKey("ketoan01"));
    }

    @Test
    void quanTriVienBiKhoaKhongDangKyDuoc() {
        // PhanQuyen: tai khoan trong phien da bi khoa thi khong thao tac.
        TaiKhoan biKhoa = new TaiKhoan(9, "qtcu", "x", "QT cũ",
                VaiTro.QUAN_TRI, false);
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.dangKy(biKhoa, "ketoan01", "Nguyen Van A",
                        "matkhau123", "matkhau123", VaiTro.THU_QUY));
        assertEquals(PhanQuyen.LOI_CHUA_DANG_NHAP, e.getMessage());
        assertFalse(dao.theoTen.containsKey("ketoan01"));
    }

    // US-023: tai khoan cua toi

    @Test
    void doiMatKhauCoNhapLai() { // AC-063
        service.doiMatKhau(admin, "matkhau123", "matkhaumoi456",
                "matkhaumoi456");
        assertTrue(MatKhauUtil.kiemTra("matkhaumoi456",
                dao.matKhauMoiNhat));
    }

    @Test
    void doiMatKhauNhapLaiKhongKhopHoacTrungCu() {
        assertThrows(NghiepVuException.class, () -> service.doiMatKhau(
                admin, "matkhau123", "matkhaumoi456", "matkhaumoi457"));
        assertThrows(NghiepVuException.class, () -> service.doiMatKhau(
                admin, "matkhau123", "matkhau123", "matkhau123"));
        assertThrows(NghiepVuException.class, () -> service.doiMatKhau(
                admin, "sai", "matkhaumoi456", "matkhaumoi456")); // AC-064
    }

    @Test
    void capNhatThongTinCaNhan() { // AC-065
        TaiKhoan moi = service.capNhatThongTin(thuQuy, "  Trần   Thị Lan ",
                "0912.345.678");
        assertEquals("Trần Thị Lan", moi.getHoTen());
        assertEquals("0912345678", moi.getSoDienThoai());
        TaiKhoan luu = dao.theoTen.get("thuquy");
        assertEquals("Trần Thị Lan", luu.getHoTen());
        assertEquals("0912345678", luu.getSoDienThoai());
        assertEquals(VaiTro.THU_QUY, moi.getVaiTro());
        assertEquals(null, service.capNhatThongTin(thuQuy, "Lan", "")
                .getSoDienThoai());
        assertThrows(NghiepVuException.class,
                () -> service.capNhatThongTin(thuQuy, " ", ""));
        assertThrows(NghiepVuException.class,
                () -> service.capNhatThongTin(thuQuy, "Lan", "12345"));
    }

    // US-022: quan ly tai khoan

    @Test
    void chiQuanTriVienXemDanhSach() { // AC-061
        assertEquals(3, service.danhSach(admin).size());
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.danhSach(thuQuy));
        assertEquals(PhanQuyen.LOI_KHONG_CO_QUYEN, e.getMessage());
    }

    @Test
    void doiVaiTroVaKhoaTaiKhoanKhac() {
        assertEquals(VaiTro.QUAN_TRI,
                service.doiVaiTro(admin, 3, VaiTro.QUAN_TRI).getVaiTro());
        assertFalse(service.datHoatDong(admin, 3, false).isHoatDong());
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("thuquy", "matkhau123"));
        assertTrue(service.datHoatDong(admin, 3, true).isHoatDong());
    }

    @Test
    void khongTuKhoaHayTuHaQuyenMinh() {
        assertThrows(NghiepVuException.class,
                () -> service.datHoatDong(admin, 1, false));
        assertThrows(NghiepVuException.class,
                () -> service.doiVaiTro(admin, 1, VaiTro.THU_QUY));
        assertTrue(dao.theoTen.get("admin").isHoatDong());
    }

    @Test
    void thuQuyKhongQuanLyDuocTaiKhoan() {
        assertThrows(NghiepVuException.class,
                () -> service.datHoatDong(thuQuy, 1, false));
        assertThrows(NghiepVuException.class,
                () -> service.doiVaiTro(thuQuy, 3, VaiTro.QUAN_TRI));
    }

    @Test
    void datLaiMatKhauChoNguoiQuen() { // RAW-035
        service.datLaiMatKhau(admin, 3, "tamthoi123", "tamthoi123");
        assertTrue(MatKhauUtil.kiemTra("tamthoi123", dao.matKhauMoiNhat));
        assertThrows(NghiepVuException.class, () -> service.datLaiMatKhau(
                admin, 3, "tamthoi123", "tamthoi124"));
        assertThrows(NghiepVuException.class, () -> service.datLaiMatKhau(
                admin, 99, "tamthoi123", "tamthoi123"));
    }

    @Test
    void lamMoiPhienThayKhoaVaDoiVaiTroNgay() {
        service.doiVaiTro(admin, 3, VaiTro.QUAN_TRI);
        assertEquals(VaiTro.QUAN_TRI,
                service.lamMoiPhien(thuQuy).getVaiTro());
        service.datHoatDong(admin, 3, false);
        assertThrows(NghiepVuException.class,
                () -> service.lamMoiPhien(thuQuy));
        assertThrows(NghiepVuException.class,
                () -> service.lamMoiPhien(null));
    }
}
