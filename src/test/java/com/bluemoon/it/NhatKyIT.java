package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.Database;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.DaoException;
import com.bluemoon.dao.DotCuTruDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.NhanKhauDaoImpl;
import com.bluemoon.dao.NhatKyDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
import com.bluemoon.dao.TaiKhoanDaoImpl;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.dao.XungDotException;
import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.NhatKy;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.PhieuThuMoi;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.model.VaiTro;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.KhoanPhiService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Nhat ky thao tac tren MySQL that (TS-002, AC-068, AC-069): moi nhom thao
 * tac ghi dung mot dong voi nguoi lam, thao tac, noi dung dung; thao tac
 * loi khong de lai dong nao; MySQL tu choi UPDATE, DELETE tren nhat_ky.
 */
class NhatKyIT {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T11 = YearMonth.of(2026, 11);

    private final NhatKyDaoImpl nhatKyDao = new NhatKyDaoImpl();
    private final HoGiaDinhDaoImpl hoDao = new HoGiaDinhDaoImpl();
    private final NhanKhauDaoImpl nkDao = new NhanKhauDaoImpl();
    private final DotCuTruDaoImpl dotDao = new DotCuTruDaoImpl();
    private final KhoanPhiDaoImpl khoanPhiDao = new KhoanPhiDaoImpl();
    private final CongNoDaoImpl congNoDao = new CongNoDaoImpl();
    private final PhieuThuDaoImpl phieuDao = new PhieuThuDaoImpl();
    private final TaiKhoanDaoImpl tkDao = new TaiKhoanDaoImpl();
    private TaiKhoan admin;
    private TaiKhoan thuQuy;

    @BeforeEach
    void chuanBi() {
        admin = CsdlThu.dungLai();
        int id = tkDao.them(new TaiKhoan(0, "thuquy1", "bam-thu-quy",
                "Trần Văn Quỹ", VaiTro.THU_QUY, true), admin.getId());
        thuQuy = tkDao.timTheoId(id).orElseThrow();
    }

    // ---- tien ich ----

    /** Nhat ky quanh hom nay (gio CSDL), moi nhat truoc. */
    private List<NhatKy> nhatKy() {
        LocalDate homNay = LocalDate.now(VN);
        return nhatKyDao.theoKhoang(homNay.minusDays(1), homNay.plusDays(1),
                null);
    }

    /** Chay thao tac, kiem tra no ghi dung mot dong va tra ve dong do. */
    private NhatKy motDong(Runnable thaoTac) {
        int truoc = CsdlThu.dem("nhat_ky");
        thaoTac.run();
        assertEquals(truoc + 1, CsdlThu.dem("nhat_ky"),
                "Mỗi thao tác phải ghi đúng một dòng nhật ký");
        return nhatKy().get(0);
    }

    private static void kiemTra(NhatKy n, TaiKhoan nguoi, ThaoTac thaoTac,
            String doiTuong, String noiDung) {
        assertEquals(nguoi.getId(), n.taiKhoanId());
        assertEquals(nguoi.getHoTen(), n.hoTen());
        assertEquals(thaoTac, n.thaoTac());
        assertEquals(doiTuong, n.doiTuong());
        assertEquals(noiDung, n.noiDung());
    }

    private static HoGiaDinh ho(String ma, String dienTich) {
        return new HoGiaDinh(0, ma, LoaiCanHo.CAN_HO, 12,
                new BigDecimal(dienTich), null, LocalDate.of(2026, 1, 1),
                TrangThaiHo.DANG_O);
    }

    private static NhanKhau nguoi(String ten, String cccd, QuanHeChuHo qh,
            LoaiCuTru cuTru, String noiThuongTru) {
        return new NhanKhau(0, 0, ten, LocalDate.of(1982, 8, 25),
                GioiTinh.NU, cccd, qh, TrangThaiNhanKhau.DANG_O, cuTru,
                noiThuongTru);
    }

    private int themHoA1203() {
        return hoDao.them(ho("A1203", "75.50"), nguoi("Nguyễn Thị Lan",
                "001182004567", QuanHeChuHo.CHU_HO, LoaiCuTru.THUONG_TRU,
                null), null, admin.getId());
    }

    private void themKhoanBatBuoc() {
        for (String ten : List.of(KhoanPhiService.PHI_DICH_VU,
                KhoanPhiService.PHI_QUAN_LY)) {
            khoanPhiDao.them(new KhoanPhi(0, ten, LoaiKhoanPhi.BAT_BUOC,
                    CachTinhPhi.THEO_DIEN_TICH, new BigDecimal(
                            ten.equals(KhoanPhiService.PHI_DICH_VU) ? "5000"
                                    : "7000"),
                    LocalDate.of(2026, 1, 1), null,
                    TrangThaiKhoanPhi.DANG_THU), admin.getId());
        }
    }

    private CongNoService congNo() {
        Clock ngay3T12 = Clock.fixed(LocalDateTime.of(2026, 12, 3, 9, 30)
                .atZone(VN).toInstant(), VN);
        return new CongNoService(congNoDao, khoanPhiDao, hoDao, ngay3T12);
    }

    // ---- moi nhom thao tac ghi dung mot dong ----

    @Test
    void hoGiaDinhGhiThemSuaChuyenDiVaChiGhiTruongThucSuDoi() {
        NhatKy them = motDong(this::themHoA1203);
        kiemTra(them, admin, ThaoTac.THEM_HO, "Hộ A1203",
                "Chủ hộ Nguyễn Thị Lan; Căn hộ, tầng 12, 75,50 m², "
                        + "chuyển đến 01/01/2026");
        int hoId = hoDao.timKiem("A1203", false).get(0).ho().getId();

        HoGiaDinh cu = hoDao.timTheoId(hoId).orElseThrow();
        HoGiaDinh moi = new HoGiaDinh(hoId, cu.getMaCanHo(),
                cu.getLoaiCanHo(), cu.getTang(), new BigDecimal("80.00"),
                cu.getSoDienThoai(), cu.getNgayChuyenDen(),
                cu.getTrangThai());
        kiemTra(motDong(() -> hoDao.capNhat(moi, admin.getId())), admin,
                ThaoTac.SUA_HO, "Hộ A1203", "Diện tích: 75,50 m² → 80,00 m²");

        // Luu lai y nguyen: khong doi gi thi khong ghi.
        int truoc = CsdlThu.dem("nhat_ky");
        hoDao.capNhat(moi, admin.getId());
        assertEquals(truoc, CsdlThu.dem("nhat_ky"));

        kiemTra(motDong(() -> hoDao.danhDauChuyenDi(hoId, admin.getId())),
                admin, ThaoTac.HO_CHUYEN_DI, "Hộ A1203",
                "Cả hộ chuyển đi, 1 nhân khẩu");
    }

    @Test
    void nhanKhauGhiThemSuaChuyenDiDoiChuHoVaDotCuTru() {
        int hoId = themHoA1203();
        int chuHoId = nkDao.theoHo(hoId).get(0).getId();
        NhanKhau chong = nguoi("Hoàng Văn Nam", "001080007890",
                QuanHeChuHo.VO_CHONG, LoaiCuTru.THUONG_TRU, null)
                .voiHoId(hoId);
        int[] id = new int[1];
        kiemTra(motDong(() -> id[0] = nkDao.them(chong, admin.getId())),
                admin, ThaoTac.THEM_NHAN_KHAU,
                "Nhân khẩu Hoàng Văn Nam, hộ A1203",
                "Quan hệ với chủ hộ: Vợ/chồng; Thường trú");

        NhanKhau sua = new NhanKhau(id[0], hoId, "Hoàng Văn Nam",
                LocalDate.of(1980, 1, 30), GioiTinh.NAM, "001080007890",
                QuanHeChuHo.VO_CHONG, TrangThaiNhanKhau.DANG_O,
                LoaiCuTru.THUONG_TRU, null);
        kiemTra(motDong(() -> nkDao.capNhat(sua, admin.getId())), admin,
                ThaoTac.SUA_NHAN_KHAU, "Nhân khẩu Hoàng Văn Nam, hộ A1203",
                "Ngày sinh: 25/08/1982 → 30/01/1980; Giới tính: Nữ → Nam");

        DotCuTru vang = new DotCuTru(0, id[0], LoaiDotCuTru.TAM_VANG,
                LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 31),
                "Công tác");
        kiemTra(motDong(() -> dotDao.them(vang, admin.getId())), admin,
                ThaoTac.GHI_TAM_TRU_TAM_VANG,
                "Nhân khẩu Hoàng Văn Nam, hộ A1203",
                "Tạm vắng 01/12/2026 – 31/12/2026. Lý do: Công tác");

        NhanKhau tamTru = nguoi("Lê Thị Hoa", "001190001234",
                QuanHeChuHo.KHAC, LoaiCuTru.TAM_TRU, "Nam Định")
                .voiHoId(hoId);
        DotCuTru dau = new DotCuTru(0, 0, LoaiDotCuTru.TAM_TRU,
                LocalDate.of(2026, 11, 1), LocalDate.of(2027, 4, 30), null);
        int[] hoaId = new int[1];
        kiemTra(motDong(() -> hoaId[0] = nkDao.themKemDot(tamTru, dau,
                admin.getId())), admin, ThaoTac.THEM_NHAN_KHAU,
                "Nhân khẩu Lê Thị Hoa, hộ A1203",
                "Quan hệ với chủ hộ: Khác; Tạm trú 01/11/2026 – 30/04/2027");

        kiemTra(motDong(() -> nkDao.capNhatTrangThai(hoaId[0],
                TrangThaiNhanKhau.DA_CHUYEN_DI, admin.getId())), admin,
                ThaoTac.NHAN_KHAU_CHUYEN_DI, "Nhân khẩu Lê Thị Hoa, hộ A1203",
                "Rời hộ");

        kiemTra(motDong(() -> nkDao.doiChuHo(chuHoId, id[0],
                QuanHeChuHo.VO_CHONG, false, admin.getId())), admin,
                ThaoTac.DOI_CHU_HO, "Hộ A1203",
                "Chủ hộ: Nguyễn Thị Lan → Hoàng Văn Nam; Nguyễn Thị Lan ở "
                        + "lại, quan hệ với chủ hộ: Vợ/chồng");
    }

    @Test
    void khoanThuCongNoVaPhieuThuGhiDungNguoiLam() {
        themKhoanBatBuoc();
        int dichVu = khoanPhiDao.danhSach().stream()
                .filter(k -> k.ten().equals(KhoanPhiService.PHI_DICH_VU))
                .findFirst().orElseThrow().id();
        kiemTra(motDong(() -> khoanPhiDao.them(new KhoanPhi(0,
                "Quỹ khuyến học 2026", LoaiKhoanPhi.TU_NGUYEN,
                CachTinhPhi.CO_DINH_THEO_HO, new BigDecimal("50000"),
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30),
                TrangThaiKhoanPhi.DANG_THU), admin.getId())), admin,
                ThaoTac.TAO_KHOAN_TU_NGUYEN, "Khoản thu Quỹ khuyến học 2026",
                "Thu từ 01/11/2026 đến 30/11/2026; 50.000 đ/hộ");
        kiemTra(motDong(() -> khoanPhiDao.capNhatDonGia(dichVu,
                new BigDecimal("6000"), admin.getId())), admin,
                ThaoTac.SUA_DON_GIA, "Khoản thu Phí dịch vụ",
                "Đơn giá: 5.000 đ/m² → 6.000 đ/m²");

        int hoId = themHoA1203();
        // Thu quy chot: nguoi lam la thu quy, khong phai admin.
        kiemTra(motDong(() -> congNo().chot(thuQuy, T11)), thuQuy,
                ThaoTac.CHOT_CONG_NO, "Tháng 11/2026",
                "Thêm 2 khoản phải nộp cho 1 hộ");
        kiemTra(motDong(() -> congNo().chot(thuQuy, T11)), thuQuy,
                ThaoTac.CHOT_CONG_NO, "Tháng 11/2026",
                "Không có khoản mới (các hộ đã đủ khoản của tháng)");

        PhaiNop dv = congNoDao.theoThang(T11).stream()
                .filter(p -> p.khoanPhiId() == dichVu).findFirst()
                .orElseThrow();
        kiemTra(motDong(() -> assertTrue(congNoDao.suaSoTien(dv.id(),
                new BigDecimal("400000"), thuQuy.getId(), "Giảm cho hộ"))),
                thuQuy, ThaoTac.SUA_SO_TIEN_PHAI_NOP, "Hộ A1203",
                "Phí dịch vụ tháng 11/2026: 453.000 đ → 400.000 đ. "
                        + "Lý do: Giảm cho hộ");

        PhieuThu[] p = new PhieuThu[1];
        NhatKy lap = motDong(() -> p[0] = phieuDao.lapPhieu(phieuMoi(hoId,
                thuQuy.getId())));
        kiemTra(lap, thuQuy, ThaoTac.LAP_PHIEU_THU,
                "Phiếu " + p[0].soBienLai(),
                "Hộ A1203; 2 khoản, tổng 928.500 đ, tiền mặt");

        kiemTra(motDong(() -> phieuDao.huy(p[0].id(), admin.getId(),
                LocalDateTime.now(VN), "Thu nhầm hộ")), admin,
                ThaoTac.HUY_PHIEU_THU, "Phiếu " + p[0].soBienLai(),
                "Hộ A1203; lý do: Thu nhầm hộ");
    }

    private PhieuThuMoi phieuMoi(int hoId, int nguoiThu) {
        List<PhieuThuMoi.Dong> dong = phieuDao.khoanChuaDong(hoId).stream()
                .map((KhoanNo k) -> new PhieuThuMoi.Dong(k.khoanPhiId(),
                        k.phaiNopId(), k.soTien()))
                .toList();
        return new PhieuThuMoi(hoId, HinhThucThanhToan.TIEN_MAT, nguoiThu,
                LocalDateTime.now(VN), dong);
    }

    @Test
    void taiKhoanGhiTaoDoiVaiTroKhoaMoKhoaDatLaiKhongGhiMatKhau() {
        NhatKy tao = nhatKy().get(0);
        kiemTra(tao, admin, ThaoTac.TAO_TAI_KHOAN, "Tài khoản thuquy1",
                "Họ tên: Trần Văn Quỹ; vai trò: Thủ quỹ");
        int id = thuQuy.getId();
        kiemTra(motDong(() -> tkDao.capNhatVaiTro(id, VaiTro.QUAN_TRI,
                admin.getId())), admin, ThaoTac.DOI_VAI_TRO,
                "Tài khoản thuquy1", "Vai trò: Thủ quỹ → Quản trị viên");
        kiemTra(motDong(() -> tkDao.capNhatHoatDong(id, false,
                admin.getId())), admin, ThaoTac.KHOA_TAI_KHOAN,
                "Tài khoản thuquy1", "Trạng thái: Hoạt động → Đã khóa");
        kiemTra(motDong(() -> tkDao.capNhatHoatDong(id, true,
                admin.getId())), admin, ThaoTac.MO_KHOA_TAI_KHOAN,
                "Tài khoản thuquy1", "Trạng thái: Đã khóa → Hoạt động");
        String bam = "$2a$10$maBamKhongDuocLoRaNhatKy0123456789abcdefghij";
        kiemTra(motDong(() -> tkDao.datLaiMatKhau(id, bam, admin.getId())),
                admin, ThaoTac.DAT_LAI_MAT_KHAU, "Tài khoản thuquy1",
                "Đặt mật khẩu tạm");
        assertEquals(bam, tkDao.timTheoId(id).orElseThrow().getMatKhauBam());

        // Tu doi mat khau khong ghi nhat ky (gia dinh 44).
        int truoc = CsdlThu.dem("nhat_ky");
        tkDao.capNhatMatKhau(id, "bam-moi-cua-chinh-minh");
        assertEquals(truoc, CsdlThu.dem("nhat_ky"));
        for (NhatKy n : nhatKy()) {
            String chu = n.doiTuong() + " " + n.noiDung();
            assertFalse(chu.contains("bam"), "Nhật ký lộ mã băm: " + chu);
        }
    }

    // ---- thao tac loi khong de lai dong ----

    @Test
    void thaoTacXungDotHoacTrungKhoaKhongDeLaiDong() {
        themKhoanBatBuoc();
        int hoId = themHoA1203();
        congNo().chot(admin, T11);
        PhieuThuMoi dung = phieuMoi(hoId, admin.getId());
        PhieuThuMoi.Dong d = dung.dong().get(0);
        // So tien vua bi doi o may khac: lap phieu bi tu choi.
        PhieuThuMoi sai = new PhieuThuMoi(hoId, HinhThucThanhToan.TIEN_MAT,
                admin.getId(), dung.thoiDiem(), List.of(new PhieuThuMoi.Dong(
                        d.khoanPhiId(), d.phaiNopId(), BigDecimal.ONE)));
        int truoc = CsdlThu.dem("nhat_ky");
        XungDotException e = assertThrows(XungDotException.class,
                () -> phieuDao.lapPhieu(sai));
        assertEquals(XungDotException.Loai.SO_TIEN_DOI, e.getLoai());
        assertEquals(truoc, CsdlThu.dem("nhat_ky"));

        PhieuThu p = phieuDao.lapPhieu(dung);
        phieuDao.huy(p.id(), admin.getId(), LocalDateTime.now(VN), "Nhầm");
        int sauHuy = CsdlThu.dem("nhat_ky");
        assertThrows(XungDotException.class, () -> phieuDao.huy(p.id(),
                admin.getId(), LocalDateTime.now(VN), "Hủy lần hai"));
        assertEquals(sauHuy, CsdlThu.dem("nhat_ky"));

        assertThrows(TrungKhoaException.class, () -> hoDao.them(
                ho("A1204", "60.00"), nguoi("Người Trùng", "001182004567",
                        QuanHeChuHo.CHU_HO, LoaiCuTru.THUONG_TRU, null),
                null, admin.getId()));
        assertEquals(sauHuy, CsdlThu.dem("nhat_ky"));
    }

    @Test
    void ghiNhatKyLoiThiThaoTacCungHoanTac() {
        // Nguoi lam khong ton tai: chi dong nhat ky dung nguoiId nen chi no
        // vi pham khoa ngoai; thao tac phai hoan tac theo, khong luu ngam
        // ma thieu nhat ky.
        int hoId = themHoA1203();
        HoGiaDinh cu = hoDao.timTheoId(hoId).orElseThrow();
        HoGiaDinh moi = new HoGiaDinh(hoId, cu.getMaCanHo(),
                cu.getLoaiCanHo(), cu.getTang(), new BigDecimal("80.00"),
                cu.getSoDienThoai(), cu.getNgayChuyenDen(),
                cu.getTrangThai());
        int truoc = CsdlThu.dem("nhat_ky");
        assertThrows(DaoException.class, () -> hoDao.capNhat(moi, 9999));
        assertEquals(0, new BigDecimal("75.50").compareTo(
                hoDao.timTheoId(hoId).orElseThrow().getDienTich()));
        assertThrows(DaoException.class, () -> khoanPhiDao.them(
                new KhoanPhi(0, "Quỹ khuyến học 2026", LoaiKhoanPhi.TU_NGUYEN,
                        CachTinhPhi.KHONG_CO_DINH, null,
                        LocalDate.of(2026, 11, 1),
                        LocalDate.of(2026, 11, 30),
                        TrangThaiKhoanPhi.DANG_THU), 9999));
        assertEquals(0, CsdlThu.dem("khoan_phi"));
        assertEquals(truoc, CsdlThu.dem("nhat_ky"));
    }

    // ---- chi them: MySQL chan UPDATE, DELETE ----

    @Test
    void mysqlTuChoiSuaVaXoaNhatKy() throws SQLException {
        int truoc = CsdlThu.dem("nhat_ky");
        try (Connection c = Database.getConnection();
                Statement st = c.createStatement()) {
            for (String lenh : List.of(
                    "UPDATE nhat_ky SET noi_dung = 'sửa lén'",
                    "DELETE FROM nhat_ky")) {
                SQLException e = assertThrows(SQLException.class,
                        () -> st.executeUpdate(lenh), lenh);
                assertEquals("45000", e.getSQLState());
                assertEquals("Nhật ký không được sửa hoặc xóa",
                        e.getMessage());
            }
        }
        assertEquals(truoc, CsdlThu.dem("nhat_ky"));
        assertEquals("Họ tên: Trần Văn Quỹ; vai trò: Thủ quỹ",
                nhatKy().get(0).noiDung());
    }

    @Test
    void chayLaiSchemaKhongMatNhatKyVaVanChanSua() throws SQLException {
        int truoc = CsdlThu.dem("nhat_ky");
        CsdlThu.chayLaiSchema();
        CsdlThu.chayLaiSchema();
        assertEquals(truoc, CsdlThu.dem("nhat_ky"));
        try (Connection c = Database.getConnection();
                Statement st = c.createStatement()) {
            assertThrows(SQLException.class,
                    () -> st.executeUpdate("DELETE FROM nhat_ky"));
        }
    }

    // ---- thoi diem va loc ----

    @Test
    void thoiDiemLaGioVietNam() {
        // May chu MySQL tren CI chay UTC; phien dat +07:00 nen gio ghi la
        // gio Viet Nam.
        LocalDateTime luc = nhatKy().get(0).thoiDiem();
        Duration lech = Duration.between(LocalDateTime.now(VN), luc).abs();
        assertTrue(lech.toMinutes() < 5, "Thời điểm " + luc
                + " lệch giờ Việt Nam " + lech);
    }

    @Test
    void locTheoKhoangNgayVaLoaiThaoTacMoiNhatTruoc() {
        themHoA1203();
        LocalDate homNay = nhatKy().get(0).thoiDiem().toLocalDate();
        List<NhatKy> hom = nhatKyDao.theoKhoang(homNay, homNay, null);
        assertEquals(2, hom.size());
        assertEquals(ThaoTac.THEM_HO, hom.get(0).thaoTac());
        assertEquals(ThaoTac.TAO_TAI_KHOAN, hom.get(1).thaoTac());

        List<NhatKy> chiThemHo = nhatKyDao.theoKhoang(homNay, homNay,
                ThaoTac.THEM_HO);
        assertEquals(1, chiThemHo.size());
        assertNull(nhatKyDao.theoKhoang(homNay, homNay, ThaoTac.SUA_HO)
                .stream().findFirst().orElse(null));
        assertTrue(nhatKyDao.theoKhoang(homNay.plusDays(1),
                homNay.plusDays(7), null).isEmpty());
        assertTrue(nhatKyDao.theoKhoang(homNay.minusDays(7),
                homNay.minusDays(1), null).isEmpty());
    }
}
