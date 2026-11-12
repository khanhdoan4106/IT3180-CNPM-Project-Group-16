package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
import com.bluemoon.dao.ThongKeDaoImpl;
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
import com.bluemoon.model.ThongKeKhoan;
import com.bluemoon.model.ThuThang;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiKhoanPhi;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.ThongKeService;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Số liệu thu phí trên MySQL thật (US-016, US-019, AC-046, AC-047, AC-052,
 * AC-053): phiếu đã hủy không tính, ranh giới tháng, 6 tháng có tháng
 * trống, khoản bắt buộc theo tháng chốt, khoản tự nguyện đếm hộ.
 */
class ThongKeIT {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T10 = YearMonth.of(2026, 10);
    private static final YearMonth T11 = YearMonth.of(2026, 11);

    private final KhoanPhiDaoImpl khoanPhiDao = new KhoanPhiDaoImpl();
    private final HoGiaDinhDaoImpl hoDao = new HoGiaDinhDaoImpl();
    private final PhieuThuDaoImpl phieuDao = new PhieuThuDaoImpl();
    private final CongNoDaoImpl congNoDao = new CongNoDaoImpl();
    private TaiKhoan admin;
    private CongNoService congNo;
    private ThongKeService thongKe;
    private KhoanPhi dichVu;
    private KhoanPhi quanLy;
    private KhoanPhi quy;
    private KhoanPhi quyTrong;
    private int hoA;
    private int hoB;
    private int hoC;

    @BeforeEach
    void chuanBi() {
        admin = CsdlThu.dungLai();
        Clock ngay12T11 = Clock.fixed(LocalDateTime.of(2026, 11, 12, 9, 0)
                .atZone(VN).toInstant(), VN);
        congNo = new CongNoService(congNoDao, khoanPhiDao, hoDao, ngay12T11);
        thongKe = new ThongKeService(new ThongKeDaoImpl(), khoanPhiDao,
                ngay12T11);
        dichVu = batBuoc("Phí dịch vụ", "5000");
        quanLy = batBuoc("Phí quản lý", "7000");
        quy = tuNguyen("Quỹ vì người nghèo 2026");
        quyTrong = tuNguyen("Quỹ khuyến học 2026");
        hoA = ho("A0601", "75.50", "001182004561");
        hoB = ho("A0602", "60.00", "001182004562");
        hoC = ho("A0603", "50.00", "001182004563");
        congNo.chot(admin, T10);
        congNo.chot(admin, T11);
    }

    private KhoanPhi batBuoc(String ten, String donGia) {
        KhoanPhi k = new KhoanPhi(0, ten, LoaiKhoanPhi.BAT_BUOC,
                CachTinhPhi.THEO_DIEN_TICH, new BigDecimal(donGia),
                LocalDate.of(2026, 1, 1), null, TrangThaiKhoanPhi.DANG_THU);
        return k.voiId(khoanPhiDao.them(k, admin.getId()));
    }

    private KhoanPhi tuNguyen(String ten) {
        KhoanPhi k = new KhoanPhi(0, ten, LoaiKhoanPhi.TU_NGUYEN,
                CachTinhPhi.KHONG_CO_DINH, null, LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 31), TrangThaiKhoanPhi.DANG_THU);
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

    private PhaiNop phaiNop(YearMonth thang, int hoId, KhoanPhi k) {
        return congNo.theoThang(admin, thang).stream()
                .filter(p -> p.hoId() == hoId && p.khoanPhiId() == k.id())
                .findFirst().orElseThrow();
    }

    /** Lập phiếu đóng một khoản phải nộp, thời điểm chỉ định. */
    private int thu(PhaiNop p, LocalDateTime luc) {
        return phieuDao.lapPhieu(new PhieuThuMoi(p.hoId(),
                HinhThucThanhToan.TIEN_MAT, admin.getId(), luc,
                List.of(new PhieuThuMoi.Dong(p.khoanPhiId(), p.id(),
                        p.soTien())))).id();
    }

    /** Lập phiếu đóng góp tự nguyện, thời điểm chỉ định. */
    private int dongGop(int hoId, KhoanPhi k, String soTien,
            LocalDateTime luc) {
        BigDecimal tien = new BigDecimal(soTien);
        return phieuDao.lapPhieu(new PhieuThuMoi(hoId,
                tien.signum() == 0 ? null : HinhThucThanhToan.TIEN_MAT,
                admin.getId(), luc,
                List.of(new PhieuThuMoi.Dong(k.id(), null, tien)))).id();
    }

    private void huy(int phieuId) {
        phieuDao.huy(phieuId, admin.getId(),
                LocalDateTime.of(2026, 11, 12, 10, 0), "Thu nhầm");
    }

    private static void bang(String mong, BigDecimal thuc) {
        assertEquals(0, new BigDecimal(mong).compareTo(thuc),
                "mong " + mong + ", thực " + thuc);
    }

    /** AC-046, AC-047: phiếu đã hủy không tính; tháng trống là 0 đ. */
    @Test
    void phieuDaHuyKhongTinh() {
        bang("0", thongKe.tongThuThang(admin, T11));
        dongGop(hoA, quy, "100000", LocalDateTime.of(2026, 11, 5, 9, 0));
        int bo = dongGop(hoB, quy, "50000",
                LocalDateTime.of(2026, 11, 6, 9, 0));
        int boDv = thu(phaiNop(T11, hoA, dichVu),
                LocalDateTime.of(2026, 11, 7, 9, 0));
        huy(bo);
        huy(boDv);
        bang("100000", thongKe.tongThuThang(admin, T11));
        bang("100000", thongKe.tongQuan(admin).daThu());
        ThongKeKhoan q = thongKe.theoKhoan(admin, quy, null);
        bang("100000", q.tongThu());
        assertEquals(1, q.soHoDaDong());
        ThongKeKhoan dv = thongKe.theoKhoan(admin, dichVu, T11);
        bang("0", dv.tongThu());
        assertEquals(0, dv.soHoDaDong());
    }

    /** Giả định 24: tháng tính theo thời điểm lập phiếu, đúng ranh giới. */
    @Test
    void ranhGioiThang() {
        dongGop(hoA, quy, "1000", LocalDateTime.of(2026, 10, 31, 23, 59,
                59));
        dongGop(hoB, quy, "2000", LocalDateTime.of(2026, 11, 1, 0, 0, 0));
        dongGop(hoC, quy, "4000", LocalDateTime.of(2026, 11, 30, 23, 59,
                59));
        dongGop(hoC, quy, "8000", LocalDateTime.of(2026, 12, 1, 0, 0, 0));
        bang("1000", thongKe.tongThuThang(admin, T10));
        bang("6000", thongKe.tongThuThang(admin, T11));
        bang("8000", thongKe.tongThuThang(admin, YearMonth.of(2026, 12)));
    }

    /** Tiền nợ tháng trước đóng trong tháng này tính vào tháng này. */
    @Test
    void noThangTruocDongThangNayTinhThangNay() {
        PhaiNop dv10 = phaiNop(T10, hoA, dichVu);
        thu(dv10, LocalDateTime.of(2026, 11, 3, 8, 0));
        bang("0", thongKe.tongThuThang(admin, T10));
        bang(dv10.soTien().toPlainString(),
                thongKe.tongThuThang(admin, T11));
    }

    @Test
    void sauThangCoThangTrong() {
        dongGop(hoA, quy, "5000", LocalDateTime.of(2026, 5, 31, 12, 0));
        dongGop(hoA, quy, "6000", LocalDateTime.of(2026, 6, 1, 12, 0));
        dongGop(hoB, quy, "9000", LocalDateTime.of(2026, 9, 15, 12, 0));
        dongGop(hoC, quy, "11000", LocalDateTime.of(2026, 11, 10, 12, 0));
        List<ThuThang> ds = thongKe.tongQuan(admin).sauThang();
        assertEquals(6, ds.size());
        assertEquals(YearMonth.of(2026, 6), ds.get(0).thang());
        assertEquals(T11, ds.get(5).thang());
        String[] mong = {"6000", "0", "0", "9000", "0", "11000"};
        for (int i = 0; i < 6; i++) {
            bang(mong[i], ds.get(i).tong());
        }
    }

    /**
     * AC-052: khoản bắt buộc theo tháng chốt; khoản sửa về 0 đ không phải
     * đóng; tiền tháng khác không lẫn. Tỷ lệ thu tháng hiện tại.
     */
    @Test
    void khoanBatBuocTheoThangChot() {
        PhaiNop dvA11 = phaiNop(T11, hoA, dichVu);
        thu(dvA11, LocalDateTime.of(2026, 11, 5, 9, 0));
        thu(phaiNop(T10, hoA, dichVu), LocalDateTime.of(2026, 11, 5, 9, 1));
        congNoDao.suaSoTien(phaiNop(T11, hoC, dichVu).id(), BigDecimal.ZERO,
                admin.getId(), "Miễn phí");

        ThongKeKhoan t11 = thongKe.theoKhoan(admin, dichVu, T11);
        bang("377500", t11.tongThu());
        // A 75,5 x 5.000 + B 60 x 5.000; C da sua ve 0 d.
        bang("677500", t11.tongPhaiThu());
        assertEquals(2, t11.soHoPhaiDong());
        assertEquals(1, t11.soHoDaDong());

        ThongKeKhoan t10 = thongKe.theoKhoan(admin, dichVu, T10);
        bang("377500", t10.tongThu());
        bang("927500", t10.tongPhaiThu());
        assertEquals(3, t10.soHoPhaiDong());
        assertEquals(1, t10.soHoDaDong());

        // Ty le T11: 377.500 / (DV A, B + QL A, B, C) = 19,1% -> 19%.
        ThongKeService.TongQuan tq = thongKe.tongQuan(admin);
        assertEquals(true, tq.daChot());
        assertEquals(19, tq.phanTram());
        assertEquals(List.of(T11, T10), thongKe.thangDaChot(admin));
    }

    /** AC-053: khoản, đợt chưa có giao dịch thì tổng là 0 đ. */
    @Test
    void chuaCoGiaoDichLaKhong() {
        ThongKeKhoan q = thongKe.theoKhoan(admin, quyTrong, null);
        bang("0", q.tongThu());
        assertEquals(0, q.soHoDaDong());
        assertEquals(0, q.soHoKhongDong());
        ThongKeKhoan ql = thongKe.theoKhoan(admin, quanLy, T10);
        bang("0", ql.tongThu());
        assertEquals(3, ql.soHoPhaiDong());
        assertEquals(0, ql.soHoDaDong());
    }

    /**
     * Giả định 29: hộ đóng nhiều lần cộng dồn; chỉ có lần 0 đ là "không
     * đóng"; 0 đ rồi đóng thêm là "đã đóng"; phiếu hủy không tính.
     */
    @Test
    void tuNguyenDemDungHoDongVaHoGhiKhong() {
        dongGop(hoA, quy, "100000", LocalDateTime.of(2026, 10, 2, 9, 0));
        dongGop(hoA, quy, "50000", LocalDateTime.of(2026, 11, 2, 9, 0));
        huy(dongGop(hoA, quy, "999000",
                LocalDateTime.of(2026, 11, 3, 9, 0)));
        dongGop(hoB, quy, "0", LocalDateTime.of(2026, 10, 3, 9, 0));
        dongGop(hoC, quy, "0", LocalDateTime.of(2026, 10, 4, 9, 0));
        dongGop(hoC, quy, "30000", LocalDateTime.of(2026, 11, 4, 9, 0));
        // Dong gop quy khac khong lan vao.
        dongGop(hoB, quyTrong, "70000", LocalDateTime.of(2026, 11, 4, 9, 0));

        ThongKeKhoan q = thongKe.theoKhoan(admin, quy, null);
        bang("180000", q.tongThu());
        assertEquals(2, q.soHoDaDong());
        assertEquals(1, q.soHoKhongDong());
    }
}
