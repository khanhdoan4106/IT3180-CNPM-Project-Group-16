package com.bluemoon.model;

import java.math.BigDecimal;
import java.text.Collator;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Báo cáo thu tiền một tháng theo mẫu sổ thu (US-018, giả định 41, RAW-031):
 * mỗi phiếu thu chưa hủy lập trong tháng một dòng, mỗi khoản/quỹ có tiền
 * trong các phiếu của tháng một cột.
 *
 * @param thang tháng báo cáo
 * @param cot các khoản/quỹ làm cột: khoản bắt buộc trước, quỹ tự nguyện
 *        sau, theo tên
 * @param dong các phiếu, theo thời điểm lập
 */
public record BaoCaoThu(YearMonth thang, List<Cot> cot, List<Dong> dong) {

    /** Khoản bắt buộc trước, quỹ tự nguyện sau; cùng loại theo tên. */
    public static final Comparator<Cot> THU_TU_COT = Comparator
            .comparing((Cot c) -> c.loai() != LoaiKhoanPhi.BAT_BUOC)
            .thenComparing(Cot::ten,
                    Collator.getInstance(Locale.forLanguageTag("vi-VN")))
            .thenComparingInt(Cot::khoanPhiId);

    /**
     * Gom các dòng chi tiết phiếu thu thành báo cáo: mỗi phiếu một dòng
     * (giữ thứ tự gặp), mỗi khoản một cột; một phiếu thu nhiều tháng của
     * cùng một khoản thì cộng vào cùng cột.
     *
     * @param thang tháng báo cáo
     * @param chiTiet các dòng chi tiết, đã xếp theo thời điểm phiếu
     * @return báo cáo
     */
    public static BaoCaoThu gom(YearMonth thang, List<ChiTiet> chiTiet) {
        Map<Integer, Cot> cot = new LinkedHashMap<>();
        Map<Integer, Dong> dong = new LinkedHashMap<>();
        for (ChiTiet c : chiTiet) {
            cot.putIfAbsent(c.khoanPhiId(),
                    new Cot(c.khoanPhiId(), c.tenKhoan(), c.loai()));
            Dong d = dong.computeIfAbsent(c.phieuId(), id -> new Dong(
                    c.maCanHo(), c.chuHo(), new LinkedHashMap<>(),
                    c.tongPhieu(), c.thoiDiem(), c.soBienLai()));
            d.tien().merge(c.khoanPhiId(), c.soTien(), BigDecimal::add);
        }
        List<Cot> dsCot = new ArrayList<>(cot.values());
        dsCot.sort(THU_TU_COT);
        return new BaoCaoThu(thang, dsCot, List.copyOf(dong.values()));
    }

    /**
     * Một dòng chi tiết của phiếu thu, như đọc từ CSDL.
     *
     * @param phieuId id phiếu
     * @param maCanHo mã căn hộ
     * @param chuHo chủ hộ, có thể null
     * @param tongPhieu tổng tiền phiếu
     * @param thoiDiem thời điểm lập phiếu
     * @param soBienLai số biên lai
     * @param khoanPhiId id khoản của dòng
     * @param tenKhoan tên khoản
     * @param loai loại khoản
     * @param soTien số tiền của dòng
     */
    public record ChiTiet(int phieuId, String maCanHo, String chuHo,
            BigDecimal tongPhieu, LocalDateTime thoiDiem, String soBienLai,
            int khoanPhiId, String tenKhoan, LoaiKhoanPhi loai,
            BigDecimal soTien) {
    }

    /**
     * Một cột khoản/quỹ.
     *
     * @param khoanPhiId id khoản
     * @param ten tên khoản, làm tiêu đề cột
     * @param loai loại khoản (bắt buộc hay tự nguyện)
     */
    public record Cot(int khoanPhiId, String ten, LoaiKhoanPhi loai) {
    }

    /**
     * Một phiếu thu.
     *
     * @param maCanHo mã căn hộ
     * @param chuHo họ tên chủ hộ, null nếu hộ không còn chủ hộ
     * @param tien số tiền theo id khoản; khoản có trong phiếu mới có mặt
     *        (đóng góp 0 đ có mặt với giá trị 0), phiếu thu nhiều tháng của
     *        cùng một khoản thì đã cộng lại
     * @param tong tổng tiền phiếu
     * @param thoiDiem thời điểm lập phiếu
     * @param soBienLai số biên lai, vd "PT-2026-00012"
     */
    public record Dong(String maCanHo, String chuHo,
            Map<Integer, BigDecimal> tien, BigDecimal tong,
            LocalDateTime thoiDiem, String soBienLai) {
    }
}
