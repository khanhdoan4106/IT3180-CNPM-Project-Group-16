package com.bluemoon.dao;

import com.bluemoon.config.Database;
import com.bluemoon.model.ChiTietPhieuThu;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.PhieuThuMoi;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TongDongGop;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Cai dat JDBC cua {@link PhieuThuDao}. Luon dung PreparedStatement.
 *
 * <p>Thu tu khoa khi lap phieu: dong ho_gia_dinh, cac dong phai_nop, cuoi
 * cung dong bo dem cua nam. Moi giao dich ghi tren ho (thu, sua so tien
 * khoan phai nop) deu khoa theo cung thu tu nen khong tao vong cho
 * (deadlock); khoa bo dem sau cung de giu khoa ngan nhat. Neu van bi MySQL
 * huy (1213, 1205) thi ra {@link DangBiKhoaException}.
 */
public class PhieuThuDaoImpl implements PhieuThuDao {

    /** So bien lai lon nhat trong mot nam (5 chu so). */
    private static final int SO_TOI_DA = 99_999;

    /** Dieu kien "khoan phai nop chua co phieu con hieu luc". */
    private static final String CHUA_THU = "NOT EXISTS (SELECT 1 FROM "
            + "phieu_thu_chi_tiet ct WHERE ct.phai_nop_da_thu = p.id)";
    private static final String CHON_HO = "SELECT h.id, h.ma_can_ho, "
            + "h.loai_can_ho, h.tang, h.dien_tich, h.so_dien_thoai, "
            + "h.ngay_chuyen_den, h.trang_thai, c.ho_ten AS ten_chu_ho, "
            + "(SELECT COUNT(*) FROM nhan_khau n WHERE n.ho_id = h.id "
            + "AND n.trang_thai = 'DANG_O') AS so_thanh_vien "
            + "FROM ho_gia_dinh h LEFT JOIN nhan_khau c "
            + "ON c.ho_id = h.id AND c.quan_he = 'CHU_HO' ";
    private static final String SQL_HO_THEO_MA = CHON_HO
            + "WHERE h.ma_can_ho = ? AND (h.trang_thai = 'DANG_O' OR EXISTS "
            + "(SELECT 1 FROM phai_nop p WHERE p.ho_id = h.id "
            + "AND p.so_tien > 0 AND " + CHUA_THU + ")) "
            + "ORDER BY h.trang_thai = 'DANG_O' DESC, h.ngay_chuyen_den DESC";
    private static final String SQL_HO_THEO_ID = CHON_HO + "WHERE h.id = ?";
    private static final String CHON_NO = "SELECT p.id, p.ho_id, "
            + "h.ma_can_ho, p.khoan_phi_id, k.ten, d.thang, p.so_tien "
            + "FROM phai_nop p JOIN dot_thu d ON d.id = p.dot_thu_id "
            + "JOIN khoan_phi k ON k.id = p.khoan_phi_id "
            + "JOIN ho_gia_dinh h ON h.id = p.ho_id "
            + "WHERE p.so_tien > 0 AND " + CHUA_THU + " ";
    private static final String SQL_NO_CUA_HO = CHON_NO
            + "AND p.ho_id = ? ORDER BY d.thang, k.ten";
    private static final String SQL_NO_DEN_THANG = CHON_NO
            + "AND d.thang <= ? ORDER BY h.ma_can_ho, p.ho_id, d.thang, k.ten";
    private static final String SQL_DONG_GOP = "SELECT ct.khoan_phi_id, "
            + "SUM(ct.so_tien), COUNT(*) FROM phieu_thu_chi_tiet ct "
            + "JOIN phieu_thu pt ON pt.id = ct.phieu_thu_id "
            + "WHERE pt.ho_id = ? AND ct.phai_nop_id IS NULL "
            + "AND ct.con_hieu_luc GROUP BY ct.khoan_phi_id";

    private static final String SQL_KHOA_HO =
            "SELECT id FROM ho_gia_dinh WHERE id = ? FOR UPDATE";
    private static final String SQL_KHOA_PHAI_NOP =
            "SELECT ho_id, so_tien FROM phai_nop WHERE id = ? FOR UPDATE";
    private static final String SQL_DA_THU =
            "SELECT 1 FROM phieu_thu_chi_tiet WHERE phai_nop_da_thu = ?";
    private static final String SQL_CO_DONG_GOP = "SELECT 1 FROM "
            + "phieu_thu_chi_tiet ct JOIN phieu_thu pt "
            + "ON pt.id = ct.phieu_thu_id WHERE pt.ho_id = ? "
            + "AND ct.khoan_phi_id = ? AND ct.phai_nop_id IS NULL "
            + "AND ct.con_hieu_luc LIMIT 1";
    private static final String SQL_TAO_BO_DEM = "INSERT INTO bo_dem_bien_lai "
            + "(nam, so_cuoi) VALUES (?, 0) ON DUPLICATE KEY UPDATE nam = nam";
    private static final String SQL_KHOA_BO_DEM =
            "SELECT so_cuoi FROM bo_dem_bien_lai WHERE nam = ? FOR UPDATE";
    private static final String SQL_TANG_BO_DEM =
            "UPDATE bo_dem_bien_lai SET so_cuoi = ? WHERE nam = ?";
    private static final String SQL_THEM_PHIEU = "INSERT INTO phieu_thu "
            + "(nam, so, ho_id, tong_tien, hinh_thuc, nguoi_thu, thoi_diem) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_THEM_DONG = "INSERT INTO "
            + "phieu_thu_chi_tiet (phieu_thu_id, khoan_phi_id, phai_nop_id, "
            + "so_tien) VALUES (?, ?, ?, ?)";

    private static final String SQL_KHOA_PHIEU =
            "SELECT da_huy FROM phieu_thu WHERE id = ? FOR UPDATE";
    private static final String SQL_HUY_PHIEU = "UPDATE phieu_thu SET "
            + "da_huy = TRUE, nguoi_huy = ?, thoi_diem_huy = ?, ly_do_huy = ? "
            + "WHERE id = ?";
    private static final String SQL_HUY_DONG = "UPDATE phieu_thu_chi_tiet "
            + "SET con_hieu_luc = FALSE WHERE phieu_thu_id = ?";
    private static final String SQL_GHI_IN =
            "UPDATE phieu_thu SET so_lan_in = so_lan_in + 1 WHERE id = ? "
            + "AND NOT da_huy";

    private static final String CHON_PHIEU = "SELECT pt.id, pt.nam, pt.so, "
            + "pt.ho_id, h.ma_can_ho, c.ho_ten AS ten_chu_ho, pt.tong_tien, "
            + "pt.hinh_thuc, pt.nguoi_thu, tt.ho_ten AS ten_nguoi_thu, "
            + "pt.thoi_diem, pt.so_lan_in, pt.da_huy, th.ho_ten AS "
            + "ten_nguoi_huy, pt.thoi_diem_huy, pt.ly_do_huy FROM phieu_thu pt "
            + "JOIN ho_gia_dinh h ON h.id = pt.ho_id "
            + "LEFT JOIN nhan_khau c ON c.ho_id = h.id "
            + "AND c.quan_he = 'CHU_HO' "
            + "JOIN tai_khoan tt ON tt.id = pt.nguoi_thu "
            + "LEFT JOIN tai_khoan th ON th.id = pt.nguoi_huy ";
    private static final String SQL_PHIEU_THEO_ID = CHON_PHIEU
            + "WHERE pt.id = ?";
    private static final String SQL_PHIEU_THEO_HO = CHON_PHIEU
            + "WHERE pt.ho_id = ? ORDER BY pt.thoi_diem DESC, pt.id DESC";
    private static final String SQL_PHIEU_TRONG_NGAY = CHON_PHIEU
            + "WHERE pt.thoi_diem >= ? AND pt.thoi_diem < ? "
            + "ORDER BY pt.thoi_diem DESC, pt.id DESC";
    private static final String CHON_DONG = "SELECT ct.id, ct.phieu_thu_id, "
            + "ct.khoan_phi_id, k.ten, ct.phai_nop_id, d.thang, ct.so_tien "
            + "FROM phieu_thu_chi_tiet ct "
            + "JOIN khoan_phi k ON k.id = ct.khoan_phi_id "
            + "LEFT JOIN phai_nop p ON p.id = ct.phai_nop_id "
            + "LEFT JOIN dot_thu d ON d.id = p.dot_thu_id "
            + "WHERE ct.phieu_thu_id IN (";

    @Override
    public List<TomTatHo> timHoTheoMa(String maCanHo) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL_HO_THEO_MA)) {
            ps.setString(1, maCanHo);
            List<TomTatHo> kq = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(docHo(rs));
                }
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi tim ho theo ma can ho", e);
        }
    }

    @Override
    public Optional<TomTatHo> timHo(int hoId) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(SQL_HO_THEO_ID)) {
            ps.setInt(1, hoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(docHo(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Loi tim ho", e);
        }
    }

    private static TomTatHo docHo(ResultSet rs) throws SQLException {
        return new TomTatHo(HoGiaDinhDaoImpl.docDong(rs),
                rs.getString("ten_chu_ho"), rs.getInt("so_thanh_vien"));
    }

    @Override
    public List<KhoanNo> khoanChuaDong(int hoId) {
        return docNo(SQL_NO_CUA_HO, ps -> ps.setInt(1, hoId));
    }

    @Override
    public List<KhoanNo> chuaDongDenThang(YearMonth thang) {
        return docNo(SQL_NO_DEN_THANG,
                ps -> ps.setDate(1, Date.valueOf(thang.atDay(1))));
    }

    /** Gan tham so cho cau lenh. */
    @FunctionalInterface
    private interface ThamSo {
        void gan(PreparedStatement ps) throws SQLException;
    }

    private static List<KhoanNo> docNo(String sql, ThamSo thamSo) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            thamSo.gan(ps);
            List<KhoanNo> kq = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(new KhoanNo(rs.getInt("id"), rs.getInt("ho_id"),
                            rs.getString("ma_can_ho"),
                            rs.getInt("khoan_phi_id"), rs.getString("ten"),
                            YearMonth.from(rs.getDate("thang").toLocalDate()),
                            rs.getBigDecimal("so_tien")));
                }
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi lay khoan chua dong", e);
        }
    }

    @Override
    public List<TongDongGop> tongDongGop(int hoId) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_DONG_GOP)) {
            ps.setInt(1, hoId);
            List<TongDongGop> kq = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(new TongDongGop(rs.getInt(1), rs.getBigDecimal(2),
                            rs.getInt(3)));
                }
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi lay dong gop cua ho", e);
        }
    }

    @Override
    public PhieuThu lapPhieu(PhieuThuMoi phieu) {
        int id;
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                khoaHo(conn, phieu.hoId());
                for (PhieuThuMoi.Dong d : phieu.dong()) {
                    if (d.phaiNopId() != null) {
                        kiemTraPhaiNop(conn, phieu.hoId(), d);
                    } else if (d.soTien().signum() == 0) {
                        kiemTraGhiKhongDong(conn, phieu.hoId(),
                                d.khoanPhiId());
                    }
                }
                int nam = phieu.thoiDiem().getYear();
                int so = capSo(conn, nam);
                id = themPhieu(conn, phieu, nam, so);
                themDong(conn, id, phieu.dong());
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw DangBiKhoaException.boc("Loi lap phieu thu", e);
        }
        return timTheoId(id).orElseThrow();
    }

    private static void khoaHo(Connection conn, int hoId)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_KHOA_HO)) {
            ps.setInt(1, hoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new XungDotException(
                            XungDotException.Loai.KHONG_TIM_THAY,
                            "ho " + hoId);
                }
            }
        }
    }

    private static void kiemTraPhaiNop(Connection conn, int hoId,
            PhieuThuMoi.Dong d) throws SQLException {
        try (PreparedStatement ps =
                conn.prepareStatement(SQL_KHOA_PHAI_NOP)) {
            ps.setInt(1, d.phaiNopId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getInt("ho_id") != hoId) {
                    throw new XungDotException(
                            XungDotException.Loai.KHONG_TIM_THAY,
                            "phai nop " + d.phaiNopId());
                }
                if (rs.getBigDecimal("so_tien").compareTo(d.soTien()) != 0) {
                    throw new XungDotException(
                            XungDotException.Loai.SO_TIEN_DOI,
                            "phai nop " + d.phaiNopId());
                }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(SQL_DA_THU)) {
            ps.setInt(1, d.phaiNopId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new XungDotException(XungDotException.Loai.DA_THU,
                            "phai nop " + d.phaiNopId());
                }
            }
        }
    }

    private static void kiemTraGhiKhongDong(Connection conn, int hoId,
            int khoanPhiId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_CO_DONG_GOP)) {
            ps.setInt(1, hoId);
            ps.setInt(2, khoanPhiId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new XungDotException(
                            XungDotException.Loai.DA_CO_DONG_GOP,
                            "quy " + khoanPhiId);
                }
            }
        }
    }

    /** Cap so tiep theo cua nam tu bo dem (gia dinh 31). */
    private static int capSo(Connection conn, int nam) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_TAO_BO_DEM)) {
            ps.setInt(1, nam);
            ps.executeUpdate();
        }
        int so;
        try (PreparedStatement ps = conn.prepareStatement(SQL_KHOA_BO_DEM)) {
            ps.setInt(1, nam);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                so = rs.getInt(1) + 1;
            }
        }
        if (so > SO_TOI_DA) {
            throw new XungDotException(XungDotException.Loai.HET_SO,
                    "nam " + nam);
        }
        try (PreparedStatement ps = conn.prepareStatement(SQL_TANG_BO_DEM)) {
            ps.setInt(1, so);
            ps.setInt(2, nam);
            ps.executeUpdate();
        }
        return so;
    }

    private static int themPhieu(Connection conn, PhieuThuMoi phieu, int nam,
            int so) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_THEM_PHIEU,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, nam);
            ps.setInt(2, so);
            ps.setInt(3, phieu.hoId());
            ps.setBigDecimal(4, phieu.tongTien());
            if (phieu.hinhThuc() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, phieu.hinhThuc().name());
            }
            ps.setInt(6, phieu.nguoiThu());
            ps.setObject(7, phieu.thoiDiem());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static void themDong(Connection conn, int phieuId,
            List<PhieuThuMoi.Dong> dong) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_THEM_DONG)) {
            for (PhieuThuMoi.Dong d : dong) {
                ps.setInt(1, phieuId);
                ps.setInt(2, d.khoanPhiId());
                if (d.phaiNopId() == null) {
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(3, d.phaiNopId());
                }
                ps.setBigDecimal(4, d.soTien());
                ps.executeUpdate();
            }
        }
    }

    @Override
    public void huy(int id, int nguoiHuy, LocalDateTime luc, String lyDo) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps =
                        conn.prepareStatement(SQL_KHOA_PHIEU)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new XungDotException(
                                    XungDotException.Loai.KHONG_TIM_THAY,
                                    "phieu " + id);
                        }
                        if (rs.getBoolean(1)) {
                            throw new XungDotException(
                                    XungDotException.Loai.DA_HUY,
                                    "phieu " + id);
                        }
                    }
                }
                try (PreparedStatement ps =
                        conn.prepareStatement(SQL_HUY_PHIEU)) {
                    ps.setInt(1, nguoiHuy);
                    ps.setObject(2, luc);
                    ps.setString(3, lyDo);
                    ps.setInt(4, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps =
                        conn.prepareStatement(SQL_HUY_DONG)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw DangBiKhoaException.boc("Loi huy phieu thu", e);
        }
    }

    @Override
    public Optional<PhieuThu> timTheoId(int id) {
        List<PhieuThu> ds = docPhieu(SQL_PHIEU_THEO_ID, ps -> ps.setInt(1, id));
        return ds.isEmpty() ? Optional.empty() : Optional.of(ds.get(0));
    }

    @Override
    public List<PhieuThu> theoHo(int hoId) {
        return docPhieu(SQL_PHIEU_THEO_HO, ps -> ps.setInt(1, hoId));
    }

    @Override
    public List<PhieuThu> trongNgay(LocalDate ngay) {
        return docPhieu(SQL_PHIEU_TRONG_NGAY, ps -> {
            ps.setObject(1, ngay.atStartOfDay());
            ps.setObject(2, ngay.plusDays(1).atStartOfDay());
        });
    }

    @Override
    public void ghiLanIn(int id) {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_GHI_IN)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Loi ghi lan in bien lai", e);
        }
    }

    /** Doc phieu roi doc chi tiet cua tat ca trong mot cau lenh. */
    private static List<PhieuThu> docPhieu(String sql, ThamSo thamSo) {
        try (Connection conn = Database.getConnection()) {
            List<PhieuThu> dau = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                thamSo.gan(ps);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        dau.add(docDauPhieu(rs));
                    }
                }
            }
            if (dau.isEmpty()) {
                return dau;
            }
            Map<Integer, List<ChiTietPhieuThu>> dong = docDong(conn, dau);
            List<PhieuThu> kq = new ArrayList<>();
            for (PhieuThu p : dau) {
                kq.add(new PhieuThu(p.id(), p.nam(), p.so(), p.hoId(),
                        p.maCanHo(), p.tenChuHo(), p.tongTien(), p.hinhThuc(),
                        p.nguoiThuId(), p.tenNguoiThu(), p.thoiDiem(),
                        p.soLanIn(), p.daHuy(), p.tenNguoiHuy(),
                        p.thoiDiemHuy(), p.lyDoHuy(), List.copyOf(
                                dong.getOrDefault(p.id(), List.of()))));
            }
            return kq;
        } catch (SQLException e) {
            throw new DaoException("Loi doc phieu thu", e);
        }
    }

    private static PhieuThu docDauPhieu(ResultSet rs) throws SQLException {
        String hinhThuc = rs.getString("hinh_thuc");
        LocalDateTime huy = rs.getObject("thoi_diem_huy", LocalDateTime.class);
        return new PhieuThu(rs.getInt("id"), rs.getInt("nam"),
                rs.getInt("so"), rs.getInt("ho_id"),
                rs.getString("ma_can_ho"), rs.getString("ten_chu_ho"),
                rs.getBigDecimal("tong_tien"),
                hinhThuc == null ? null : HinhThucThanhToan.valueOf(hinhThuc),
                rs.getInt("nguoi_thu"), rs.getString("ten_nguoi_thu"),
                rs.getObject("thoi_diem", LocalDateTime.class),
                rs.getInt("so_lan_in"), rs.getBoolean("da_huy"),
                rs.getString("ten_nguoi_huy"),
                huy,
                rs.getString("ly_do_huy"), Collections.emptyList());
    }

    private static Map<Integer, List<ChiTietPhieuThu>> docDong(
            Connection conn, List<PhieuThu> phieu) throws SQLException {
        String sql = CHON_DONG + String.join(", ",
                Collections.nCopies(phieu.size(), "?"))
                + ") ORDER BY ct.phieu_thu_id, ct.phai_nop_id IS NULL, "
                + "d.thang, k.ten, ct.id";
        Map<Integer, List<ChiTietPhieuThu>> kq = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < phieu.size(); i++) {
                ps.setInt(i + 1, phieu.get(i).id());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int phaiNop = rs.getInt("phai_nop_id");
                    Integer phaiNopId = rs.wasNull() ? null : phaiNop;
                    Date thang = rs.getDate("thang");
                    BigDecimal soTien = rs.getBigDecimal("so_tien");
                    kq.computeIfAbsent(rs.getInt("phieu_thu_id"),
                            k -> new ArrayList<>()).add(new ChiTietPhieuThu(
                                    rs.getInt("id"), rs.getInt("khoan_phi_id"),
                                    rs.getString("ten"), phaiNopId,
                                    thang == null ? null
                                            : YearMonth.from(
                                                    thang.toLocalDate()),
                                    soTien));
                }
            }
        }
        return kq;
    }
}
