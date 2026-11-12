package com.bluemoon.service;

import com.bluemoon.dao.KhoanPhiDao;
import com.bluemoon.dao.ThongKeDao;
import com.bluemoon.dao.ThongKeDao.SoLieuKhoan;
import com.bluemoon.dao.ThongKeDao.TyLeThu;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThongKeKhoan;
import com.bluemoon.model.ThuThang;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Số liệu thu phí: tổng thu tháng và biểu đồ 6 tháng ở Trang chính
 * (US-016, AC-046, AC-047, giả định 24), thống kê theo khoản (US-019,
 * AC-052, AC-053, giả định 45). Quản trị viên và Thủ quỹ đều xem được.
 */
public class ThongKeService {

    /** Số tháng của biểu đồ ở Trang chính, tính cả tháng hiện tại. */
    public static final int SO_THANG_BIEU_DO = 6;

    private final ThongKeDao dao;
    private final KhoanPhiDao khoanPhiDao;
    private final Clock dongHo;

    /**
     * Tạo dịch vụ.
     *
     * @param dao nguồn số liệu thống kê
     * @param khoanPhiDao danh mục khoản thu
     * @param dongHo đồng hồ để biết tháng hiện tại (giờ CSDL khi chạy thật,
     *        {@link com.bluemoon.dao.DongHoCsdl})
     */
    public ThongKeService(ThongKeDao dao, KhoanPhiDao khoanPhiDao,
            Clock dongHo) {
        this.dao = dao;
        this.khoanPhiDao = khoanPhiDao;
        this.dongHo = dongHo;
    }

    /**
     * Tháng hiện tại theo đồng hồ của dịch vụ.
     *
     * @return tháng hiện tại
     */
    public YearMonth thangHienTai() {
        return YearMonth.now(dongHo);
    }

    /**
     * Tổng quan tháng hiện tại cho Trang chính.
     *
     * @param nguoi tài khoản đang đăng nhập
     * @return tổng thu tháng, tỷ lệ thu (nếu đã chốt) và thu 6 tháng
     * @throws NghiepVuException nếu không có quyền
     */
    public TongQuan tongQuan(TaiKhoan nguoi) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        YearMonth thang = thangHienTai();
        List<ThuThang> sauThang = thuGanNhat(thang);
        BigDecimal daThu = sauThang.get(sauThang.size() - 1).tong();
        Optional<TyLeThu> tyLe = dao.tyLeThu(thang);
        return new TongQuan(thang, daThu, tyLe.isPresent(),
                tyLe.map(ThongKeService::phanTram).orElse(null), sauThang);
    }

    /**
     * Tổng tiền đã thu trong một tháng (giả định 24): phiếu chưa hủy lập
     * trong tháng, kể cả tiền nợ tháng trước đóng trong tháng này. Tháng
     * không có phiếu là 0 đ (AC-047).
     *
     * @param nguoi tài khoản đang đăng nhập
     * @param thang tháng cần xem
     * @return tổng tiền
     * @throws NghiepVuException nếu không có quyền hoặc chưa chọn tháng
     */
    public BigDecimal tongThuThang(TaiKhoan nguoi, YearMonth thang) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        if (thang == null) {
            throw new NghiepVuException("Chưa chọn tháng");
        }
        return dao.thuTheoThang(thang, thang)
                .getOrDefault(thang, BigDecimal.ZERO);
    }

    /** Thu của 6 tháng tới tháng cuối, một truy vấn; tháng trống là 0. */
    private List<ThuThang> thuGanNhat(YearMonth cuoi) {
        YearMonth dau = cuoi.minusMonths(SO_THANG_BIEU_DO - 1L);
        Map<YearMonth, BigDecimal> co = dao.thuTheoThang(dau, cuoi);
        List<ThuThang> kq = new ArrayList<>();
        for (YearMonth t = dau; !t.isAfter(cuoi); t = t.plusMonths(1)) {
            kq.add(new ThuThang(t, co.getOrDefault(t, BigDecimal.ZERO)));
        }
        return kq;
    }

    /**
     * Phần trăm đã thu, làm tròn xuống để chỉ ghi 100% khi thu đủ. Tháng đã
     * chốt mà không có khoản nào phải nộp thì không có tỷ lệ.
     */
    static Integer phanTram(TyLeThu t) {
        if (t.phaiNop().signum() <= 0) {
            return null;
        }
        return t.daThu().multiply(BigDecimal.valueOf(100))
                .divide(t.phaiNop(), 0, RoundingMode.DOWN).intValue();
    }

    /**
     * Các khoản thu để chọn ở màn Thống kê: khoản bắt buộc trước, rồi khoản
     * tự nguyện mới nhất trước.
     *
     * @param nguoi tài khoản đang đăng nhập
     * @return danh sách khoản
     * @throws NghiepVuException nếu không có quyền
     */
    public List<KhoanPhi> cacKhoan(TaiKhoan nguoi) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        return khoanPhiDao.danhSach().stream()
                .sorted(Comparator.comparing(
                        (KhoanPhi k) -> k.loai() != LoaiKhoanPhi.BAT_BUOC)
                        .thenComparing(KhoanPhi::tuNgay,
                                Comparator.reverseOrder())
                        .thenComparing(KhoanPhi::ten))
                .toList();
    }

    /**
     * Các tháng đã chốt công nợ, mới nhất trước (đợt của khoản bắt buộc).
     *
     * @param nguoi tài khoản đang đăng nhập
     * @return danh sách tháng, rỗng nếu chưa chốt tháng nào
     * @throws NghiepVuException nếu không có quyền
     */
    public List<YearMonth> thangDaChot(TaiKhoan nguoi) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        return dao.thangDaChot();
    }

    /**
     * Thống kê một khoản theo đợt (giả định 45). Khoản bắt buộc: đợt là
     * tháng đã chốt. Khoản tự nguyện: đợt là khoảng thời gian thu của khoản,
     * tham số tháng bị bỏ qua. Chưa có giao dịch thì tổng là 0 đ (AC-053).
     *
     * @param nguoi tài khoản đang đăng nhập
     * @param khoan khoản thu
     * @param thang tháng đã chốt (chỉ cần với khoản bắt buộc)
     * @return số liệu của khoản
     * @throws NghiepVuException nếu không có quyền, chưa chọn khoản, hoặc
     *         khoản bắt buộc mà chưa chọn tháng / tháng chưa chốt
     */
    public ThongKeKhoan theoKhoan(TaiKhoan nguoi, KhoanPhi khoan,
            YearMonth thang) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        if (khoan == null) {
            throw new NghiepVuException("Chưa chọn khoản thu");
        }
        if (khoan.loai() == LoaiKhoanPhi.TU_NGUYEN) {
            SoLieuKhoan s = dao.khoanTuNguyen(khoan.id());
            return new ThongKeKhoan(khoan, null, s.tongThu(),
                    BigDecimal.ZERO, 0, s.soHoDaDong(), s.soHoKhongDong());
        }
        if (thang == null) {
            throw new NghiepVuException("Chưa chọn tháng");
        }
        if (!dao.thangDaChot().contains(thang)) {
            throw new NghiepVuException("Tháng " + thang.getMonthValue()
                    + "/" + thang.getYear() + " chưa chốt công nợ");
        }
        SoLieuKhoan s = dao.khoanBatBuoc(khoan.id(), thang);
        return new ThongKeKhoan(khoan, thang, s.tongThu(), s.tongPhaiThu(),
                s.soHoPhaiDong(), s.soHoDaDong(), 0);
    }

    /**
     * Tổng quan tháng hiện tại.
     *
     * @param thang tháng hiện tại
     * @param daThu tổng đã thu trong tháng
     * @param daChot tháng đã chốt công nợ hay chưa
     * @param phanTram phần trăm đã thu của tổng phải thu; null nếu chưa
     *        chốt hoặc không có khoản phải nộp
     * @param sauThang thu của 6 tháng gần nhất, cũ trước, tháng hiện tại cuối
     */
    public record TongQuan(YearMonth thang, BigDecimal daThu, boolean daChot,
            Integer phanTram, List<ThuThang> sauThang) {
    }
}
