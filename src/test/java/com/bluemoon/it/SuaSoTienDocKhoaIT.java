package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.CongNoDaoImpl;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.KhoanPhiDaoImpl;
import com.bluemoon.dao.PhieuThuDaoImpl;
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
import com.bluemoon.service.CongNoService;
import com.bluemoon.service.KhoanPhiService;
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
 * AC-026 khi hai máy cùng thao tác: máy B bắt đầu sửa số tiền một khoản
 * phải nộp (câu đọc đầu tiên của giao dịch đã chụp snapshot REPEATABLE
 * READ), máy A lập phiếu thu khoản đó và commit, rồi B mới khóa hộ, khóa
 * khoản và kiểm tra "đã thu". Kiểm tra phải thấy phiếu A vừa lập (đọc có
 * khóa), nên B không sửa được. Điều phối bằng điểm chèn
 * {@code CongNoDaoImpl.sauKhiDocHo}, chạy tuần tự, không phụ thuộc may rủi.
 */
class SuaSoTienDocKhoaIT {

    private static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final YearMonth T11 = YearMonth.of(2026, 11);

    private final KhoanPhiDaoImpl khoanPhiDao = new KhoanPhiDaoImpl();
    private final HoGiaDinhDaoImpl hoDao = new HoGiaDinhDaoImpl();
    private final PhieuThuDaoImpl phieuDao = new PhieuThuDaoImpl();
    private final CongNoDaoImpl congNoDao = new CongNoDaoImpl();
    private TaiKhoan admin;
    private int hoId;
    private PhaiNop khoan;

    @BeforeEach
    void chuanBi() {
        admin = CsdlThu.dungLai();
        khoanPhiDao.them(new KhoanPhi(0, KhoanPhiService.PHI_DICH_VU,
                LoaiKhoanPhi.BAT_BUOC, CachTinhPhi.THEO_DIEN_TICH,
                new BigDecimal("5000"), LocalDate.of(2026, 1, 1), null,
                TrangThaiKhoanPhi.DANG_THU), admin.getId());
        hoId = hoDao.them(new HoGiaDinh(0, "A0601", LoaiCanHo.CAN_HO, 6,
                new BigDecimal("75.50"), null, LocalDate.of(2026, 1, 1),
                TrangThaiHo.DANG_O),
                new NhanKhau(0, 0, "Nguyễn Thị Lan", LocalDate.of(1982, 8, 25),
                        GioiTinh.NU, "001182004567", QuanHeChuHo.CHU_HO,
                        TrangThaiNhanKhau.DANG_O, LoaiCuTru.THUONG_TRU, null),
                null, admin.getId());
        Clock ngay3T12 = Clock.fixed(LocalDate.of(2026, 12, 3)
                .atStartOfDay(VN).toInstant(), VN);
        new CongNoService(congNoDao, khoanPhiDao, hoDao, ngay3T12)
                .chot(admin, T11);
        khoan = congNoDao.theoThang(T11).get(0);
    }

    /** Máy A: lập phiếu thu khoản đang xét trên kết nối riêng, commit. */
    private void mayALapPhieu() {
        phieuDao.lapPhieu(new PhieuThuMoi(hoId, HinhThucThanhToan.TIEN_MAT,
                admin.getId(), LocalDateTime.now(VN), List.of(
                        new PhieuThuMoi.Dong(khoan.khoanPhiId(), khoan.id(),
                                khoan.soTien()))));
    }

    @Test
    void phieuLapGiuaChungThiKhongSuaDuocKhoanDaThu() {
        int[] soLanChen = new int[1];
        CongNoDaoImpl mayB = new CongNoDaoImpl() {
            @Override
            protected void sauKhiDocHo(int phaiNopId) {
                assertEquals(khoan.id(), phaiNopId);
                soLanChen[0]++;
                mayALapPhieu();
            }
        };
        int nhatKyTruoc = CsdlThu.dem("nhat_ky");

        boolean daSua = mayB.suaSoTien(khoan.id(), new BigDecimal("1"),
                admin.getId(), "Sửa sau khi máy khác đã thu");

        assertEquals(1, soLanChen[0], "Điểm chèn phải chạy đúng một lần");
        assertTrue(congNoDao.daCoPhieuThu(khoan.id()),
                "Máy A phải lập phiếu xong");
        assertFalse(daSua, "Khoản vừa được thu ở máy khác không được sửa");
        PhaiNop sau = congNoDao.timTheoId(khoan.id()).orElseThrow();
        assertEquals(0, khoan.soTien().compareTo(sau.soTien()),
                "Số tiền phải giữ nguyên " + khoan.soTien() + ", là "
                        + sau.soTien());
        assertFalse(sau.daSua());
        // Chi them dong nhat ky lap phieu cua may A, khong co dong sua.
        assertEquals(nhatKyTruoc + 1, CsdlThu.dem("nhat_ky"));
    }

    @Test
    void khongCoPhieuGiuaChungThiVanSuaDuoc() {
        CongNoDaoImpl mayB = new CongNoDaoImpl() {
            @Override
            protected void sauKhiDocHo(int phaiNopId) {
                // Khong may nao thu: doc co khoa khong chan viec sua.
            }
        };
        assertTrue(mayB.suaSoTien(khoan.id(), new BigDecimal("350000"),
                admin.getId(), "Giảm theo biên bản"));
        assertEquals(0, new BigDecimal("350000").compareTo(
                congNoDao.timTheoId(khoan.id()).orElseThrow().soTien()));
    }
}
