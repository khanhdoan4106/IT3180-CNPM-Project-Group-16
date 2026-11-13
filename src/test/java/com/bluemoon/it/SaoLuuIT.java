package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.bluemoon.ScriptSaoLuuTest;
import com.bluemoon.config.Database;
import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.DotCuTruDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.NhanKhauDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
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
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.PhieuThuMoi;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.KhoanPhiService;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Sao lưu và khôi phục trên MySQL thật (TS-001, AC-066, AC-067): chạy
 * mysqldump với đúng các tùy chọn trong scripts/sao-luu.ps1, nạp tệp vào
 * một CSDL mới tên khác rồi so: số dòng mọi bảng, tổng tiền phiếu thu, cột
 * sinh, trigger chặn sửa/xóa nhật ký. Máy không có mysqldump/mysql thì bỏ
 * qua kèm thông báo.
 */
class SaoLuuIT {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T11 = YearMonth.of(2026, 11);
    private static final Pattern URL = Pattern.compile(
            "jdbc:mysql://([^/:?]+)(?::(\\d+))?/([^/?]+)(\\?.*)?");

    private final HoGiaDinhDaoImpl hoDao = new HoGiaDinhDaoImpl();
    private final NhanKhauDaoImpl nkDao = new NhanKhauDaoImpl();
    private final DotCuTruDaoImpl dotDao = new DotCuTruDaoImpl();
    private final KhoanPhiDaoImpl khoanPhiDao = new KhoanPhiDaoImpl();
    private final CongNoDaoImpl congNoDao = new CongNoDaoImpl();
    private final PhieuThuDaoImpl phieuDao = new PhieuThuDaoImpl();

    private TaiKhoan admin;
    private Path mysqldump;
    private Path mysql;
    private Path thuMuc;
    private Path tepTuyChon;
    private String csdlNguon;
    private String csdlKhoiPhuc;
    private String urlKhoiPhuc;
    private Properties cauHinh;

    @BeforeEach
    void chuanBi() throws IOException {
        admin = CsdlThu.dungLai();
        mysqldump = timCongCu("mysqldump");
        mysql = timCongCu("mysql");
        assumeTrue(mysqldump != null && mysql != null,
                "Bo qua SaoLuuIT: khong tim thay mysqldump/mysql trong PATH"
                        + " hay C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin");

        cauHinh = Database.docCauHinh();
        Matcher m = URL.matcher(cauHinh.getProperty("url"));
        assertTrue(m.matches(), "URL JDBC không đúng dạng");
        String host = m.group(1);
        String cong = m.group(2) == null ? "3306" : m.group(2);
        csdlNguon = m.group(3);
        csdlKhoiPhuc = csdlNguon + "_khoi_phuc";
        urlKhoiPhuc = "jdbc:mysql://" + host + ":" + cong + "/"
                + csdlKhoiPhuc + (m.group(4) == null ? "" : m.group(4));

        thuMuc = Files.createTempDirectory("bluemoon-sao-luu-it");
        // Mat khau di qua tep tuy chon nhu script, khong qua dong lenh.
        tepTuyChon = thuMuc.resolve("client.cnf");
        Files.writeString(tepTuyChon, "[client]\nhost=" + host + "\nport="
                + cong + "\nuser=" + giaTri(cauHinh.getProperty("user"))
                + "\npassword=" + giaTri(cauHinh.getProperty("password"))
                + "\n", StandardCharsets.UTF_8);
        thucThi("DROP DATABASE IF EXISTS `" + csdlKhoiPhuc + "`");
    }

    @AfterEach
    void donDep() throws IOException {
        if (csdlKhoiPhuc != null) {
            thucThi("DROP DATABASE IF EXISTS `" + csdlKhoiPhuc + "`");
        }
        if (thuMuc != null) {
            try (var s = Files.list(thuMuc)) {
                for (Path p : s.toList()) {
                    Files.deleteIfExists(p);
                }
            }
            Files.deleteIfExists(thuMuc);
        }
    }

    // ---- tien ich ----

    private static String giaTri(String s) {
        String v = s == null ? "" : s;
        return "\"" + v.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static Path timCongCu(String ten) {
        boolean windows = File.separatorChar == '\\';
        String tep = windows ? ten + ".exe" : ten;
        List<String> thuMuc = new ArrayList<>(List.of(
                System.getenv().getOrDefault("PATH", "")
                        .split(File.pathSeparator)));
        thuMuc.add("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin");
        for (String d : thuMuc) {
            if (d.isBlank()) {
                continue;
            }
            Path p = Path.of(d, tep);
            if (Files.isRegularFile(p) && Files.isExecutable(p)) {
                return p;
            }
        }
        return null;
    }

    private static void thucThi(String sql) {
        try (Connection c = Database.getConnection();
                Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException(sql, e);
        }
    }

    private Connection ketNoiKhoiPhuc() throws SQLException {
        return DriverManager.getConnection(urlKhoiPhuc,
                cauHinh.getProperty("user"), cauHinh.getProperty("password"));
    }

    /** Chay mot chuong trinh, tra ve ma thoat; loi ghi vao tep stderr. */
    private int chay(List<String> lenh, Path vao, Path ra)
            throws IOException, InterruptedException {
        Path loi = thuMuc.resolve("stderr.txt");
        ProcessBuilder pb = new ProcessBuilder(lenh)
                .redirectError(loi.toFile());
        if (vao != null) {
            pb.redirectInput(vao.toFile());
        }
        pb.redirectOutput(ra == null ? ProcessBuilder.Redirect.DISCARD
                : ProcessBuilder.Redirect.to(ra.toFile()));
        Process p = pb.start();
        assertTrue(p.waitFor(180, TimeUnit.SECONDS), "Quá thời gian: "
                + lenh.get(0));
        if (p.exitValue() != 0) {
            System.err.println(Files.readString(loi));
        }
        return p.exitValue();
    }

    /** Sao luu nhu scripts/sao-luu.ps1 roi khoi phuc vao CSDL moi. */
    private Path saoLuuRoiKhoiPhuc() throws Exception {
        Path tep = thuMuc.resolve("bluemoon-20261113-220000.sql");
        List<String> lenh = new ArrayList<>();
        lenh.add(mysqldump.toString());
        lenh.add("--defaults-extra-file=" + tepTuyChon);
        lenh.addAll(ScriptSaoLuuTest.tuyChonMysqldump());
        lenh.add(csdlNguon);
        assertEquals(0, chay(lenh, null, tep), "mysqldump lỗi");

        thucThi("CREATE DATABASE `" + csdlKhoiPhuc + "` CHARACTER SET "
                + "utf8mb4 COLLATE utf8mb4_unicode_ci");
        assertEquals(0, chay(List.of(mysql.toString(),
                "--defaults-extra-file=" + tepTuyChon,
                "--default-character-set=utf8mb4", csdlKhoiPhuc), tep, null),
                "Nạp tệp sao lưu lỗi");
        return tep;
    }

    private static HoGiaDinh ho(String ma, String dienTich) {
        return new HoGiaDinh(0, ma, LoaiCanHo.CAN_HO, 12,
                new BigDecimal(dienTich), null, LocalDate.of(2026, 1, 1),
                TrangThaiHo.DANG_O);
    }

    private static NhanKhau nguoi(String ten, String cccd, QuanHeChuHo qh) {
        return new NhanKhau(0, 0, ten, LocalDate.of(1982, 8, 25),
                GioiTinh.NU, cccd, qh, TrangThaiNhanKhau.DANG_O,
                LoaiCuTru.THUONG_TRU, null);
    }

    private PhieuThuMoi phieuMoi(int hoId) {
        List<PhieuThuMoi.Dong> dong = phieuDao.khoanChuaDong(hoId).stream()
                .map((KhoanNo k) -> new PhieuThuMoi.Dong(k.khoanPhiId(),
                        k.phaiNopId(), k.soTien()))
                .toList();
        return new PhieuThuMoi(hoId, HinhThucThanhToan.TIEN_MAT,
                admin.getId(), LocalDateTime.now(VN), dong);
    }

    /**
     * Du lieu du moi bang: ho, nhan khau, dot tam vang, bien dong, khoan
     * thu, cong no, phieu thu da huy va chua huy, nhat ky.
     */
    private void taoDuLieu() {
        for (String ten : List.of(KhoanPhiService.PHI_DICH_VU,
                KhoanPhiService.PHI_QUAN_LY)) {
            khoanPhiDao.them(new KhoanPhi(0, ten, LoaiKhoanPhi.BAT_BUOC,
                    CachTinhPhi.THEO_DIEN_TICH, new BigDecimal(
                            ten.equals(KhoanPhiService.PHI_DICH_VU) ? "5000"
                                    : "7000"),
                    LocalDate.of(2026, 1, 1), null,
                    TrangThaiKhoanPhi.DANG_THU), admin.getId());
        }
        int a1203 = hoDao.them(ho("A1203", "75.50"), nguoi("Nguyễn Thị Lan",
                "001182004567", QuanHeChuHo.CHU_HO), null, admin.getId());
        int b0405 = hoDao.them(ho("B0405", "60.00"), nguoi("Trần Văn Hùng",
                "001080001111", QuanHeChuHo.CHU_HO), null, admin.getId());
        int chong = nkDao.them(nguoi("Hoàng Văn Nam", "001080007890",
                QuanHeChuHo.VO_CHONG).voiHoId(a1203), admin.getId());
        dotDao.them(new DotCuTru(0, chong, LoaiDotCuTru.TAM_VANG,
                LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 31),
                "Công tác"), admin.getId());

        Clock ngay3T12 = Clock.fixed(LocalDateTime.of(2026, 12, 3, 9, 30)
                .atZone(VN).toInstant(), VN);
        new CongNoService(congNoDao, khoanPhiDao, hoDao, ngay3T12)
                .chot(admin, T11);

        // A1203: lap, huy, lap lai; B0405: mot phieu chua huy.
        PhieuThu huy = phieuDao.lapPhieu(phieuMoi(a1203));
        phieuDao.huy(huy.id(), admin.getId(), LocalDateTime.now(VN),
                "Thu nhầm");
        phieuDao.lapPhieu(phieuMoi(a1203));
        phieuDao.lapPhieu(phieuMoi(b0405));
    }

    private static Map<String, Long> soDong(Connection c) throws SQLException {
        Map<String, Long> kq = new LinkedHashMap<>();
        List<String> bang = new ArrayList<>();
        try (Statement st = c.createStatement();
                ResultSet rs = st.executeQuery("SELECT table_name FROM "
                        + "information_schema.tables WHERE table_schema = "
                        + "DATABASE() AND table_type = 'BASE TABLE' "
                        + "ORDER BY table_name")) {
            while (rs.next()) {
                bang.add(rs.getString(1));
            }
        }
        for (String b : bang) {
            kq.put(b, motSo(c, "SELECT COUNT(*) FROM `" + b + "`"));
        }
        return kq;
    }

    private static long motSo(Connection c, String sql) throws SQLException {
        try (Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private static List<String> cacDong(Connection c, String sql)
            throws SQLException {
        List<String> kq = new ArrayList<>();
        try (Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            int n = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= n; i++) {
                    sb.append(i > 1 ? "|" : "").append(rs.getString(i));
                }
                kq.add(sb.toString());
            }
        }
        return kq;
    }

    private static String dongCuoi(Path tep) throws IOException {
        try (RandomAccessFile f = new RandomAccessFile(tep.toFile(), "r")) {
            long n = Math.min(512, f.length());
            byte[] b = new byte[(int) n];
            f.seek(f.length() - n);
            f.readFully(b);
            String[] dong = new String(b, StandardCharsets.UTF_8).strip()
                    .split("\\R");
            return dong[dong.length - 1];
        }
    }

    // ---- kiem thu ----

    @Test
    void khoiPhucVaoCsdlMoiDuSoDongMoiBangVaTongTien() throws Exception {
        taoDuLieu();
        Path tep = saoLuuRoiKhoiPhuc();

        assertTrue(dongCuoi(tep).startsWith("-- Dump completed"),
                "Tệp sao lưu phải kết thúc bằng dòng -- Dump completed");
        String noiDung = Files.readString(tep, StandardCharsets.UTF_8);
        // Khong co --databases: tep khong tao CSDL, khong USE ten CSDL nguon.
        assertFalse(noiDung.contains("CREATE DATABASE"));
        assertFalse(Pattern.compile("^USE ", Pattern.MULTILINE)
                .matcher(noiDung).find());
        assertTrue(noiDung.contains("Nguyễn Thị Lan"),
                "Tiếng Việt trong tệp sao lưu phải đúng UTF-8");

        try (Connection nguon = Database.getConnection();
                Connection kp = ketNoiKhoiPhuc()) {
            Map<String, Long> soNguon = soDong(nguon);
            for (Map.Entry<String, Long> e : soNguon.entrySet()) {
                assertTrue(e.getValue() > 0, "Dữ liệu thử phải có ở mọi "
                        + "bảng, bảng " + e.getKey() + " trống");
            }
            assertEquals(soNguon, soDong(kp));

            String tien = "SELECT COALESCE(SUM(tong_tien), 0), "
                    + "COALESCE(SUM(IF(da_huy, 0, tong_tien)), 0), "
                    + "SUM(da_huy) FROM phieu_thu";
            List<String> tienNguon = cacDong(nguon, tien);
            assertEquals(tienNguon, cacDong(kp, tien));
            assertTrue(tienNguon.get(0).endsWith("|1"),
                    "Phải có một phiếu đã hủy: " + tienNguon);
            for (String sql : List.of(
                    "SELECT ho_ten FROM nhan_khau ORDER BY id",
                    "SELECT id, nam, so, tong_tien, thoi_diem, ly_do_huy "
                            + "FROM phieu_thu ORDER BY id",
                    "SELECT so_cuoi FROM bo_dem_bien_lai")) {
                assertEquals(cacDong(nguon, sql), cacDong(kp, sql), sql);
            }
        }
    }

    @Test
    void cotSinhVanDungVaVanLaCotSinhSauKhoiPhuc() throws Exception {
        taoDuLieu();
        saoLuuRoiKhoiPhuc();
        try (Connection nguon = Database.getConnection();
                Connection kp = ketNoiKhoiPhuc()) {
            for (String sql : List.of(
                    "SELECT id, phai_nop_id, con_hieu_luc, phai_nop_da_thu "
                            + "FROM phieu_thu_chi_tiet ORDER BY id",
                    "SELECT id, quan_he, chu_ho_cua, cccd_dang_o "
                            + "FROM nhan_khau ORDER BY id",
                    "SELECT id, ma_dang_o FROM ho_gia_dinh ORDER BY id")) {
                assertEquals(cacDong(nguon, sql), cacDong(kp, sql), sql);
            }
            // Phieu huy: dong co phai_nop_id nhung phai_nop_da_thu NULL.
            assertTrue(cacDong(kp, "SELECT phai_nop_da_thu FROM "
                    + "phieu_thu_chi_tiet WHERE con_hieu_luc = FALSE")
                    .stream().allMatch("null"::equals));
            assertEquals(2, motSo(kp, "SELECT COUNT(chu_ho_cua) "
                    + "FROM nhan_khau"));

            String cotSinh = "SELECT CONCAT(table_name, '.', column_name, "
                    + "' ', extra) FROM information_schema.columns WHERE "
                    + "table_schema = DATABASE() AND extra LIKE '%GENERATED%' "
                    + "ORDER BY 1";
            List<String> sinhKp = cacDong(kp, cotSinh);
            assertEquals(cacDong(nguon, cotSinh), sinhKp);
            assertTrue(sinhKp.contains(
                    "phieu_thu_chi_tiet.phai_nop_da_thu STORED GENERATED"));
            assertTrue(sinhKp.contains("nhan_khau.chu_ho_cua STORED GENERATED"));

            // Van tinh lai khi doi cot nguon, va UNIQUE tren cot sinh van
            // chan thu trung, chu ho thu hai.
            try (Statement st = kp.createStatement()) {
                int id = (int) motSo(kp, "SELECT MIN(id) FROM "
                        + "phieu_thu_chi_tiet WHERE con_hieu_luc = TRUE "
                        + "AND phai_nop_id IS NOT NULL");
                st.executeUpdate("UPDATE phieu_thu_chi_tiet SET "
                        + "con_hieu_luc = FALSE WHERE id = " + id);
                assertEquals(List.of("null"), cacDong(kp, "SELECT "
                        + "phai_nop_da_thu FROM phieu_thu_chi_tiet WHERE id = "
                        + id));
                SQLException trung = assertThrows(SQLException.class,
                        () -> st.executeUpdate("UPDATE nhan_khau SET quan_he "
                                + "= 'CHU_HO' WHERE quan_he = 'VO_CHONG'"));
                assertEquals(1062, trung.getErrorCode());
            }
        }
    }

    @Test
    void triggerNhatKyVanChanSuaXoaSauKhoiPhuc() throws Exception {
        taoDuLieu();
        saoLuuRoiKhoiPhuc();
        try (Connection kp = ketNoiKhoiPhuc();
                Statement st = kp.createStatement()) {
            assertEquals(List.of("nhat_ky.trg_nky_chan_sua",
                    "nhat_ky.trg_nky_chan_xoa"), cacDong(kp,
                    "SELECT CONCAT(event_object_table, '.', trigger_name) "
                            + "FROM information_schema.triggers WHERE "
                            + "trigger_schema = DATABASE() ORDER BY 1"));
            long truoc = motSo(kp, "SELECT COUNT(*) FROM nhat_ky");
            assertTrue(truoc > 0);
            for (String lenh : List.of(
                    "UPDATE nhat_ky SET noi_dung = 'sửa lén'",
                    "DELETE FROM nhat_ky")) {
                SQLException e = assertThrows(SQLException.class,
                        () -> st.executeUpdate(lenh), lenh);
                assertEquals("45000", e.getSQLState());
                assertEquals("Nhật ký không được sửa hoặc xóa",
                        e.getMessage());
            }
            assertEquals(truoc, motSo(kp, "SELECT COUNT(*) FROM nhat_ky"));
            assertNotNull(cacDong(kp, "SELECT noi_dung FROM nhat_ky"));
        }
    }
}
