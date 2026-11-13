package com.bluemoon.service;

import com.bluemoon.model.CachTinhPhi;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.TaiKhoan;
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

/**
 * Dữ liệu thu phí giả trong bộ nhớ cho test giao diện ở gói khác (vd
 * {@code ThuMotHoNamBuocTest}): hộ A0601 (chủ hộ Nguyễn Thị Lan) nợ phí dịch
 * vụ tháng 11, 12 và phí quản lý tháng 12; một quỹ tự nguyện đang mở.
 * Không cần MySQL.
 */
public final class ThuPhiGia {

    /** Thủ quỹ đăng nhập trong test. */
    public static final TaiKhoan THU_QUY = new TaiKhoan(2, "thuquy", "x",
            "Trần Thị Bình", VaiTro.THU_QUY, true);
    /** Mã căn hộ có nợ. */
    public static final String MA = "A0601";
    /** Số khoản bắt buộc chưa đóng của A0601. */
    public static final int SO_KHOAN = 3;
    /** Tổng các khoản bắt buộc chưa đóng. */
    public static final BigDecimal TONG = new BigDecimal("1283500");

    private final KhoPhieuThuGia khoPhieu;
    private final ThuPhiService service;

    private ThuPhiGia() {
        KhoThuPhiGia kho = new KhoThuPhiGia();
        khoPhieu = new KhoPhieuThuGia(kho);
        ZoneId vn = ZoneId.of("Asia/Ho_Chi_Minh");
        service = new ThuPhiService(khoPhieu, kho.khoanPhiDao, Clock.fixed(
                LocalDateTime.of(2026, 12, 3, 9, 30).atZone(vn).toInstant(),
                vn));
        khoPhieu.themTaiKhoan(THU_QUY);
        int dichVu = them(kho, KhoanPhiService.PHI_DICH_VU,
                LoaiKhoanPhi.BAT_BUOC, LocalDate.of(2026, 1, 1), null);
        int quanLy = them(kho, KhoanPhiService.PHI_QUAN_LY,
                LoaiKhoanPhi.BAT_BUOC, LocalDate.of(2026, 1, 1), null);
        them(kho, "Quỹ vì người nghèo 2026", LoaiKhoanPhi.TU_NGUYEN,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 31));
        int a0601 = khoPhieu.themHo(MA, TrangThaiHo.DANG_O, "Nguyễn Thị Lan");
        khoPhieu.themPhaiNop(a0601, dichVu, YearMonth.of(2026, 11), "377500");
        khoPhieu.themPhaiNop(a0601, dichVu, YearMonth.of(2026, 12), "377500");
        khoPhieu.themPhaiNop(a0601, quanLy, YearMonth.of(2026, 12), "528500");
    }

    /**
     * Tạo bộ dữ liệu mới.
     *
     * @return dữ liệu giả
     */
    public static ThuPhiGia moi() {
        return new ThuPhiGia();
    }

    /**
     * Dịch vụ thu phí chạy trên dữ liệu giả, đồng hồ cố định 03/12/2026.
     *
     * @return dịch vụ
     */
    public ThuPhiService service() {
        return service;
    }

    /**
     * Các phiếu đã lập.
     *
     * @return phiếu theo thứ tự lập
     */
    public List<PhieuThu> phieu() {
        return List.copyOf(khoPhieu.phieu.values());
    }

    private static int them(KhoThuPhiGia kho, String ten, LoaiKhoanPhi loai,
            LocalDate tu, LocalDate den) {
        boolean batBuoc = loai == LoaiKhoanPhi.BAT_BUOC;
        return kho.khoanPhiDao.them(new KhoanPhi(0, ten, loai,
                batBuoc ? CachTinhPhi.THEO_DIEN_TICH
                        : CachTinhPhi.KHONG_CO_DINH,
                batBuoc ? new BigDecimal("5000") : null, tu, den,
                TrangThaiKhoanPhi.DANG_THU), 1);
    }
}
