package com.bluemoon.service;

import com.bluemoon.dao.CongNoDao;
import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.dao.KhoanPhiDao;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.KetQuaTinhCongNo;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Chot cong no dau thang (US-009) va sua khoan phai nop cua mot ho truoc
 * khi ho dong (US-008).
 */
public class CongNoService {

    /** Do dai toi da cua ghi chu khi sua (cot ghi_chu VARCHAR(200)). */
    public static final int GHI_CHU_TOI_DA = 200;
    /** Thong bao khi khoan da co phieu thu (AC-026). */
    public static final String LOI_DA_THU =
            "Khoản này đã có phiếu thu. Hủy phiếu thu trước khi sửa giá trị";

    private final CongNoDao dao;
    private final KhoanPhiDao khoanPhiDao;
    private final HoGiaDinhDao hoDao;
    private final Clock dongHo;

    /**
     * Tao dich vu dung dong ho he thong.
     *
     * @param dao nguon du lieu cong no
     * @param khoanPhiDao nguon du lieu danh muc khoan thu
     * @param hoDao nguon du lieu ho gia dinh
     */
    public CongNoService(CongNoDao dao, KhoanPhiDao khoanPhiDao,
            HoGiaDinhDao hoDao) {
        this(dao, khoanPhiDao, hoDao, Clock.systemDefaultZone());
    }

    /**
     * Tao dich vu voi dong ho chi dinh (de test thang hien tai).
     *
     * @param dao nguon du lieu cong no
     * @param khoanPhiDao nguon du lieu danh muc khoan thu
     * @param hoDao nguon du lieu ho gia dinh
     * @param dongHo dong ho dung de lay thang hien tai
     */
    public CongNoService(CongNoDao dao, KhoanPhiDao khoanPhiDao,
            HoGiaDinhDao hoDao, Clock dongHo) {
        this.dao = dao;
        this.khoanPhiDao = khoanPhiDao;
        this.hoDao = hoDao;
        this.dongHo = dongHo;
    }

    /**
     * Thang hien tai, mac dinh cua man hinh chot.
     *
     * @return thang hien tai theo dong ho
     */
    public YearMonth thangHienTai() {
        return YearMonth.now(dongHo);
    }

    /**
     * Thang hien tai da chot chua, de Trang chinh nhac chot.
     *
     * @return true neu thang hien tai chua co dot chot
     */
    public boolean canNhacChot() {
        return !dao.daChot(thangHienTai());
    }

    /**
     * Tinh thu cong no mot thang, chua luu (AC-027): moi ho dang o nhan
     * moi khoan bat buoc dang thu, so tien = dien tich x don gia, lam tron
     * den dong. Ho chua co dien tich bi bo qua (AC-029).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param thang thang can tinh
     * @return cac khoan phai nop va cac ho bi bo qua
     * @throws NghiepVuException neu khong co quyen, chua chon thang, thang
     *         o tuong lai hoac chua co khoan bat buoc nao
     */
    public KetQuaTinhCongNo xemTruoc(TaiKhoan nguoi, YearMonth thang) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        if (thang == null) {
            throw new NghiepVuException("Chưa chọn tháng");
        }
        if (thang.isAfter(thangHienTai())) {
            throw new NghiepVuException("Chưa đến tháng "
                    + thang.getMonthValue() + "/" + thang.getYear()
                    + ", chưa chốt được");
        }
        List<KhoanPhi> batBuoc = khoanPhiDao.danhSach().stream()
                .filter(KhoanPhi::tinhCongNo).toList();
        if (batBuoc.isEmpty()) {
            throw new NghiepVuException("Chưa có khoản thu bắt buộc nào");
        }
        List<PhaiNop> khoan = new ArrayList<>();
        List<String> boQua = new ArrayList<>();
        for (TomTatHo t : hoDao.timKiem("", false)) {
            HoGiaDinh ho = t.ho();
            BigDecimal dt = ho.getDienTich();
            if (dt == null || dt.signum() <= 0) {
                boQua.add(ho.getMaCanHo());
                continue;
            }
            for (KhoanPhi k : batBuoc) {
                BigDecimal tien = dt.multiply(k.donGia())
                        .setScale(0, RoundingMode.HALF_UP);
                khoan.add(new PhaiNop(0, ho.getId(), ho.getMaCanHo(), k.id(),
                        k.ten(), dt, k.donGia(), tien, tien, null, null,
                        null));
            }
        }
        return new KetQuaTinhCongNo(thang, khoan, boQua);
    }

    /**
     * Chot cong no mot thang (AC-027, AC-028). Chot lai cung thang chi them
     * ho hoac khoan con thieu; khoan da co, ke ca da sua, giu nguyen.
     *
     * @param nguoi tai khoan dang dang nhap
     * @param thang thang chot
     * @return ket qua da tinh; {@code soMoi} trong {@link KetQuaChot}
     * @throws NghiepVuException nhu {@link #xemTruoc}
     */
    public KetQuaChot chot(TaiKhoan nguoi, YearMonth thang) {
        KetQuaTinhCongNo tinh = xemTruoc(nguoi, thang);
        boolean lanDau = !dao.daChot(thang);
        int soMoi = dao.chot(thang, nguoi.getId(), tinh.khoan());
        return new KetQuaChot(tinh, soMoi, lanDau);
    }

    /**
     * Ket qua mot lan chot.
     *
     * @param tinh ket qua tinh (ca cac ho bi bo qua)
     * @param soMoi so khoan moi duoc them lan nay
     * @param lanDau true neu day la lan chot dau tien cua thang
     */
    public record KetQuaChot(KetQuaTinhCongNo tinh, int soMoi,
            boolean lanDau) {
    }

    /**
     * Cac khoan phai nop da chot cua mot thang.
     *
     * @param nguoi tai khoan dang dang nhap
     * @param thang thang can xem
     * @return danh sach, rong neu chua chot
     */
    public List<PhaiNop> theoThang(TaiKhoan nguoi, YearMonth thang) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        return dao.theoThang(thang);
    }

    /**
     * Tong no chua dong cua mot ho, de canh bao khi danh dau ho chuyen di
     * (no van duoc giu de thu tiep).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param hoId khoa chinh ho
     * @return tong no, 0 neu khong no
     */
    public BigDecimal tongChuaDong(TaiKhoan nguoi, int hoId) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        return dao.tongChuaDong(hoId);
    }

    /**
     * Sua so tien mot khoan phai nop truoc khi ho dong (AC-025, AC-026).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param id khoa chinh khoan phai nop
     * @param soTienNhap so tien moi tren form
     * @param ghiChuNhap ly do sua, co the bo trong
     * @return khoan sau khi sua
     * @throws NghiepVuException neu khong co quyen, khong tim thay, da co
     *         phieu thu, so tien sai hoac khong doi
     */
    public PhaiNop suaSoTien(TaiKhoan nguoi, int id, String soTienNhap,
            String ghiChuNhap) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        PhaiNop p = dao.timTheoId(id).orElseThrow(
                () -> new NghiepVuException("Không tìm thấy khoản phải nộp"));
        if (dao.daCoPhieuThu(id)) {
            throw new NghiepVuException(LOI_DA_THU);
        }
        BigDecimal soTien = SoTien.doc(soTienNhap, "Số tiền");
        if (soTien == null) {
            throw new NghiepVuException("Chưa nhập số tiền");
        }
        if (soTien.compareTo(p.soTien()) == 0) {
            throw new NghiepVuException("Số tiền không thay đổi");
        }
        String ghiChu = ghiChuNhap == null || ghiChuNhap.isBlank() ? null
                : ghiChuNhap.strip();
        if (ghiChu != null && ghiChu.length() > GHI_CHU_TOI_DA) {
            throw new NghiepVuException(
                    "Ghi chú tối đa " + GHI_CHU_TOI_DA + " ký tự");
        }
        dao.suaSoTien(id, soTien, nguoi.getId(), ghiChu);
        return dao.timTheoId(id).orElseThrow();
    }
}
