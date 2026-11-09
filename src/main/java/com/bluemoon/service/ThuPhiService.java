package com.bluemoon.service;

import com.bluemoon.dao.KhoanPhiDao;
import com.bluemoon.dao.PhieuThuDao;
import com.bluemoon.dao.XungDotException;
import com.bluemoon.model.DongGopQuy;
import com.bluemoon.model.HinhThucThanhToan;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.PhieuThuMoi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TongDongGop;
import com.bluemoon.model.TrangThaiDongGop;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Thu phi tai quay (US-010 den US-014) va ghi nhan dong gop tu nguyen
 * (US-007). Quan tri vien va Thu quy deu dung duoc.
 */
public class ThuPhiService {

    /** Do dai toi da cua ly do huy (cot ly_do_huy VARCHAR(200)). */
    public static final int LY_DO_TOI_DA = 200;
    /** AC-031. */
    public static final String LOI_KHONG_TIM_THAY = "Không tìm thấy phòng";
    /** AC-038. */
    public static final String LOI_CHUA_CHON_HINH_THUC =
            "Chưa chọn hình thức thanh toán";
    /** Khoan vua duoc thu o may khac, hoac vua bi sua. */
    public static final String LOI_DU_LIEU_DOI = "Khoản phải nộp của hộ vừa"
            + " thay đổi (có thể vừa được thu hoặc sửa ở máy khác)."
            + " Tra cứu lại rồi thu tiếp";

    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private final PhieuThuDao dao;
    private final KhoanPhiDao khoanPhiDao;
    private final Clock dongHo;

    /**
     * Tao dich vu dung dong ho he thong.
     *
     * @param dao nguon du lieu phieu thu
     * @param khoanPhiDao nguon du lieu danh muc khoan thu
     */
    public ThuPhiService(PhieuThuDao dao, KhoanPhiDao khoanPhiDao) {
        this(dao, khoanPhiDao, Clock.systemDefaultZone());
    }

    /**
     * Tao dich vu voi dong ho chi dinh (de test ngay lap, ngay huy).
     *
     * @param dao nguon du lieu phieu thu
     * @param khoanPhiDao nguon du lieu danh muc khoan thu
     * @param dongHo dong ho cho thoi diem lap va huy phieu
     */
    public ThuPhiService(PhieuThuDao dao, KhoanPhiDao khoanPhiDao,
            Clock dongHo) {
        this.dao = dao;
        this.khoanPhiDao = khoanPhiDao;
        this.dongHo = dongHo;
    }

    /**
     * Tim ho theo ma can ho (AC-030, AC-031): ho dang o va ho da chuyen di
     * con no (gia dinh 21).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param maNhap ma can ho tren form
     * @return cac ho, ho dang o dung dau; it nhat mot ho
     * @throws NghiepVuException neu chua nhap hoac khong tim thay
     */
    public List<TomTatHo> traCuu(TaiKhoan nguoi, String maNhap) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        String ma = maNhap == null ? "" : maNhap.strip();
        if (ma.isEmpty()) {
            throw new NghiepVuException("Chưa nhập mã phòng");
        }
        List<TomTatHo> ho = dao.timHoTheoMa(ma);
        if (ho.isEmpty()) {
            throw new NghiepVuException(LOI_KHONG_TIM_THAY);
        }
        return ho;
    }

    /**
     * Thong tin thu phi cua mot ho: khoan chua dong va cac quy tu nguyen
     * dang mo kem tinh trang dong gop (AC-030, AC-022, AC-023).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param hoId khoa chinh ho
     * @return thong tin thu phi
     * @throws NghiepVuException neu khong co quyen hoac khong co ho
     */
    public ThongTinThu thongTin(TaiKhoan nguoi, int hoId) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        TomTatHo ho = dao.timHo(hoId).orElseThrow(
                () -> new NghiepVuException(LOI_KHONG_TIM_THAY));
        List<KhoanNo> no = dao.khoanChuaDong(hoId);
        return new ThongTinThu(ho, no, dongGop(hoId));
    }

    /**
     * Thong tin thu phi cua mot ho.
     *
     * @param ho ho kem ten chu ho
     * @param khoanChuaDong khoan phai nop chua dong, theo thang
     * @param dongGop quy tu nguyen dang mo
     */
    public record ThongTinThu(TomTatHo ho, List<KhoanNo> khoanChuaDong,
            List<DongGopQuy> dongGop) {

        /**
         * Tong cac khoan chua dong.
         *
         * @return tong no
         */
        public BigDecimal tongNo() {
            return khoanChuaDong.stream().map(KhoanNo::soTien)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    /** Quy tu nguyen dang mo hom nay kem tinh trang cua ho. */
    private List<DongGopQuy> dongGop(int hoId) {
        LocalDate homNay = LocalDate.now(dongHo);
        Map<Integer, TongDongGop> daDong = dao.tongDongGop(hoId).stream()
                .collect(Collectors.toMap(TongDongGop::khoanPhiId,
                        Function.identity()));
        List<DongGopQuy> kq = new ArrayList<>();
        for (KhoanPhi k : khoanPhiDao.danhSach()) {
            if (k.loai() == LoaiKhoanPhi.TU_NGUYEN && k.dangMo(homNay)) {
                TongDongGop t = daDong.get(k.id());
                kq.add(t == null ? new DongGopQuy(k, BigDecimal.ZERO, 0)
                        : new DongGopQuy(k, t.tong(), t.soLan()));
            }
        }
        return kq;
    }

    /**
     * Lap phieu thu (US-011, US-012, US-007). Khoan bat buoc thu du so tien
     * hien tai (gia dinh 23); dong gop tu nguyen nhap so bat ky, bo trong la
     * khong ghi, 0 d la "khong dong" va chi ghi khi ho chua co lan nao
     * (gia dinh 29). Phieu tong 0 d khong can hinh thuc thanh toan (gia
     * dinh 30).
     *
     * @param nguoi tai khoan dang dang nhap, ghi la nguoi thu (AC-037)
     * @param hoId ho nop tien
     * @param phaiNopIds cac khoan phai nop duoc chon (AC-033, AC-034)
     * @param dongGopNhap so tien nhap cho tung quy: id quy -&gt; chuoi tren
     *        form, co the null
     * @param hinhThuc hinh thuc thanh toan (AC-036), co the null
     * @return phieu da luu, co so bien lai
     * @throws NghiepVuException neu khong co quyen hoac du lieu sai
     */
    public PhieuThu thu(TaiKhoan nguoi, int hoId,
            Collection<Integer> phaiNopIds, Map<Integer, String> dongGopNhap,
            HinhThucThanhToan hinhThuc) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        ThongTinThu tt = thongTin(nguoi, hoId);
        List<PhieuThuMoi.Dong> dong = new ArrayList<>();
        Set<Integer> chon = new LinkedHashSet<>(
                phaiNopIds == null ? List.of() : phaiNopIds);
        for (KhoanNo k : tt.khoanChuaDong()) {
            if (chon.remove(k.phaiNopId())) {
                dong.add(new PhieuThuMoi.Dong(k.khoanPhiId(), k.phaiNopId(),
                        k.soTien()));
            }
        }
        if (!chon.isEmpty()) {
            throw new NghiepVuException(LOI_DU_LIEU_DOI);
        }
        dong.addAll(dongGopMoi(tt, dongGopNhap));
        if (dong.isEmpty()) {
            throw new NghiepVuException("Chưa chọn khoản nào để thu");
        }
        BigDecimal tong = dong.stream().map(PhieuThuMoi.Dong::soTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (tong.signum() > 0 && hinhThuc == null) {
            throw new NghiepVuException(LOI_CHUA_CHON_HINH_THUC);
        }
        PhieuThuMoi moi = new PhieuThuMoi(hoId,
                tong.signum() == 0 ? null : hinhThuc, nguoi.getId(),
                LocalDateTime.now(dongHo).truncatedTo(ChronoUnit.SECONDS),
                dong);
        try {
            return dao.lapPhieu(moi);
        } catch (XungDotException e) {
            throw new NghiepVuException(thongBao(e));
        }
    }

    private static List<PhieuThuMoi.Dong> dongGopMoi(ThongTinThu tt,
            Map<Integer, String> nhap) {
        List<PhieuThuMoi.Dong> dong = new ArrayList<>();
        if (nhap == null) {
            return dong;
        }
        for (Map.Entry<Integer, String> e : nhap.entrySet()) {
            if (e.getValue() == null || e.getValue().isBlank()) {
                continue;
            }
            DongGopQuy q = tt.dongGop().stream()
                    .filter(d -> d.quy().id() == e.getKey()).findFirst()
                    .orElseThrow(() -> new NghiepVuException(
                            "Quỹ này không còn mở, không ghi đóng góp được"));
            BigDecimal soTien = SoTien.doc(e.getValue(), q.quy().ten());
            if (soTien.signum() == 0
                    && q.trangThai() != TrangThaiDongGop.CHUA_THU) {
                throw new NghiepVuException(q.trangThai()
                        == TrangThaiDongGop.DA_DONG
                        ? "Hộ đã đóng " + q.quy().ten()
                                + ", không ghi \"không đóng\" được"
                        : "Đã ghi hộ không đóng " + q.quy().ten());
            }
            dong.add(new PhieuThuMoi.Dong(q.quy().id(), null, soTien));
        }
        return dong;
    }

    /**
     * Huy phieu thu (US-014, gia dinh 28): Thu quy chi huy phieu minh lap,
     * Quan tri vien huy moi phieu; chi trong ngay lap; bat buoc ly do. Phieu
     * va so bien lai duoc giu lai (AC-043).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param id khoa chinh phieu
     * @param lyDoNhap ly do huy
     * @return phieu sau khi huy
     * @throws NghiepVuException neu khong duoc huy
     */
    public PhieuThu huy(TaiKhoan nguoi, int id, String lyDoNhap) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        PhieuThu p = dao.timTheoId(id).orElseThrow(
                () -> new NghiepVuException("Không tìm thấy phiếu thu"));
        if (p.daHuy()) {
            throw new NghiepVuException("Phiếu " + p.soBienLai()
                    + " đã bị hủy trước đó");
        }
        if (nguoi.getVaiTro() == VaiTro.THU_QUY
                && p.nguoiThuId() != nguoi.getId()) {
            throw new NghiepVuException(
                    "Thủ quỹ chỉ hủy được phiếu do mình lập");
        }
        LocalDateTime bayGio = LocalDateTime.now(dongHo)
                .truncatedTo(ChronoUnit.SECONDS);
        if (!p.thoiDiem().toLocalDate().equals(bayGio.toLocalDate())) {
            throw new NghiepVuException("Chỉ hủy được phiếu lập trong ngày."
                    + " Phiếu " + p.soBienLai() + " lập ngày "
                    + p.thoiDiem().toLocalDate().format(NGAY));
        }
        String lyDo = lyDoNhap == null ? "" : lyDoNhap.strip();
        if (lyDo.isEmpty()) {
            throw new NghiepVuException("Vui lòng nhập lý do hủy phiếu");
        }
        if (lyDo.length() > LY_DO_TOI_DA) {
            throw new NghiepVuException(
                    "Lý do tối đa " + LY_DO_TOI_DA + " ký tự");
        }
        try {
            dao.huy(id, nguoi.getId(), bayGio, lyDo);
        } catch (XungDotException e) {
            throw new NghiepVuException(thongBao(e));
        }
        return dao.timTheoId(id).orElseThrow();
    }

    /**
     * Lay phieu de in bien lai (US-013): phieu da huy hoac khong thu tien
     * thi khong in (gia dinh 30).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param id khoa chinh phieu
     * @return phieu; {@code soLanIn() > 0} nghia la ban in lai (AC-040)
     * @throws NghiepVuException neu khong in duoc
     */
    public PhieuThu choIn(TaiKhoan nguoi, int id) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        PhieuThu p = dao.timTheoId(id).orElseThrow(
                () -> new NghiepVuException("Không tìm thấy phiếu thu"));
        if (p.daHuy()) {
            throw new NghiepVuException("Phiếu " + p.soBienLai()
                    + " đã hủy, không in biên lai");
        }
        if (p.khongThuTien()) {
            throw new NghiepVuException("Phiếu " + p.soBienLai()
                    + " không thu tiền, không in biên lai");
        }
        return p;
    }

    /**
     * Ghi da in bien lai them mot lan, sau khi may in nhan lenh.
     *
     * @param nguoi tai khoan dang dang nhap
     * @param id khoa chinh phieu
     */
    public void daIn(TaiKhoan nguoi, int id) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        dao.ghiLanIn(id);
    }

    /**
     * Phieu lap hom nay, ke ca phieu da huy, de in lai hoac huy.
     *
     * @param nguoi tai khoan dang dang nhap
     * @return danh sach phieu, moi nhat truoc
     */
    public List<PhieuThu> homNay(TaiKhoan nguoi) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        return dao.trongNgay(LocalDate.now(dongHo));
    }

    /**
     * Lich su phieu thu cua ho, ke ca phieu da huy kem nguoi huy (AC-043).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param hoId khoa chinh ho
     * @return danh sach phieu, moi nhat truoc
     */
    public List<PhieuThu> lichSu(TaiKhoan nguoi, int hoId) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        return dao.theoHo(hoId);
    }

    private static String thongBao(XungDotException e) {
        return switch (e.getLoai()) {
            case DA_THU, SO_TIEN_DOI, KHONG_TIM_THAY -> LOI_DU_LIEU_DOI;
            case DA_CO_DONG_GOP -> "Hộ vừa có đóng góp cho quỹ này ở máy"
                    + " khác, không ghi \"không đóng\" được. Tra cứu lại";
            case HET_SO -> "Đã dùng hết số biên lai của năm. Báo người"
                    + " quản trị hệ thống";
            case DA_HUY -> "Phiếu vừa được hủy ở máy khác";
        };
    }
}
