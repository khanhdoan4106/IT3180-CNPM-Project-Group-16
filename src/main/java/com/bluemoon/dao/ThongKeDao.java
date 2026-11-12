package com.bluemoon.dao;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Số liệu thu phí cho Trang chính (US-016) và màn Thống kê (US-019). Mọi
 * con số chỉ tính phiếu thu chưa hủy.
 */
public interface ThongKeDao {

    /**
     * Tổng tiền thu theo tháng, tính theo thời điểm lập phiếu (giả định
     * 24), trong một truy vấn GROUP BY.
     *
     * @param tuThang tháng đầu
     * @param denThang tháng cuối (tính cả tháng này)
     * @return tháng có phiếu -&gt; tổng tiền; tháng không có phiếu thì
     *         không có trong bảng
     */
    Map<YearMonth, BigDecimal> thuTheoThang(YearMonth tuThang,
            YearMonth denThang);

    /**
     * Số đã thu và tổng phải nộp của các khoản phải nộp lớn hơn 0 đ thuộc
     * đợt chốt của một tháng.
     *
     * @param thang tháng
     * @return rỗng nếu tháng chưa chốt công nợ
     */
    Optional<TyLeThu> tyLeThu(YearMonth thang);

    /**
     * Các tháng đã chốt công nợ, mới nhất trước.
     *
     * @return danh sách tháng
     */
    List<YearMonth> thangDaChot();

    /**
     * Thống kê một khoản bắt buộc trong đợt chốt của một tháng.
     *
     * @param khoanPhiId id khoản bắt buộc
     * @param thang tháng đã chốt
     * @return số liệu thô
     */
    SoLieuKhoan khoanBatBuoc(int khoanPhiId, YearMonth thang);

    /**
     * Thống kê đóng góp cho một khoản tự nguyện (các dòng không gắn khoản
     * phải nộp).
     *
     * @param khoanPhiId id khoản tự nguyện
     * @return số liệu thô
     */
    SoLieuKhoan khoanTuNguyen(int khoanPhiId);

    /**
     * Số đã thu và tổng phải nộp của một tháng đã chốt.
     *
     * @param daThu tổng đã thu của các khoản phải nộp
     * @param phaiNop tổng phải nộp (khoản lớn hơn 0 đ)
     */
    record TyLeThu(BigDecimal daThu, BigDecimal phaiNop) {
    }

    /**
     * Số liệu của một khoản.
     *
     * @param tongThu tổng đã thu
     * @param tongPhaiThu tổng phải thu (0 với khoản tự nguyện)
     * @param soHoPhaiDong số hộ phải đóng (0 với khoản tự nguyện)
     * @param soHoDaDong số hộ đã đóng
     * @param soHoKhongDong số hộ ghi "không đóng" (0 với khoản bắt buộc)
     */
    record SoLieuKhoan(BigDecimal tongThu, BigDecimal tongPhaiThu,
            int soHoPhaiDong, int soHoDaDong, int soHoKhongDong) {
    }
}
