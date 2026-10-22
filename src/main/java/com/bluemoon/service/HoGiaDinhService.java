package com.bluemoon.service;

import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiHo;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Nghiep vu quan ly ho gia dinh (US-001): them, sua, tim kiem, danh dau ho
 * chuyen di. Ho moi duoc them cung chu ho de moi ho co dung mot chu ho
 * (US-002).
 */
public class HoGiaDinhService {

    /** So tang cua toa nha (RAW-001). */
    public static final int SO_TANG = 30;
    /** Dien tich san toa nha, dien tich mot can khong vuot qua (RAW-001). */
    public static final BigDecimal DIEN_TICH_TOI_DA = new BigDecimal("450");

    private static final Pattern MAU_MA = Pattern.compile("[A-Z0-9-]{1,20}");
    private static final Pattern MAU_SDT = Pattern.compile("0\\d{9}");
    private static final String DUOI_CHU_HO = " chu ho";

    private final HoGiaDinhDao dao;
    private final NhanKhauDao nhanKhauDao;
    private final Clock dongHo;

    /**
     * Tao dich vu dung dong ho he thong.
     *
     * @param dao nguon du lieu ho gia dinh
     * @param nhanKhauDao nguon du lieu nhan khau (kiem tra CCCD chu ho)
     */
    public HoGiaDinhService(HoGiaDinhDao dao, NhanKhauDao nhanKhauDao) {
        this(dao, nhanKhauDao, Clock.systemDefaultZone());
    }

    /**
     * Tao dich vu voi dong ho chi dinh (de test ngay hien tai).
     *
     * @param dao nguon du lieu ho gia dinh
     * @param nhanKhauDao nguon du lieu nhan khau (kiem tra CCCD chu ho)
     * @param dongHo dong ho dung de lay ngay hom nay
     */
    public HoGiaDinhService(HoGiaDinhDao dao, NhanKhauDao nhanKhauDao,
            Clock dongHo) {
        this.dao = dao;
        this.nhanKhauDao = nhanKhauDao;
        this.dongHo = dongHo;
    }

    /**
     * Them ho gia dinh moi cung chu ho (AC-001 - AC-004). Quan he trong
     * {@code chuHo} bi bo qua, luon luu la chu ho.
     *
     * @param tt du lieu ho tren form
     * @param chuHo du lieu chu ho tren form
     * @return ho vua them, da co id
     * @throws NghiepVuException neu thieu/sai du lieu, trung ma can ho hoac
     *         trung so CCCD chu ho
     */
    public HoGiaDinh them(ThongTinHo tt, ThongTinNhanKhau chuHo) {
        List<String> thieu = new ArrayList<>();
        List<String> sai = new ArrayList<>();
        HoGiaDinh ho = docHo(tt, 0, TrangThaiHo.DANG_O, thieu, sai);
        ThongTinNhanKhau ttChuHo = new ThongTinNhanKhau(chuHo.hoTen(),
                chuHo.ngaySinh(), chuHo.gioiTinh(), chuHo.soCccd(),
                QuanHeChuHo.CHU_HO);
        NhanKhau nk = KiemTraNhanKhau.doc(ttChuHo, 0, 0,
                LocalDate.now(dongHo), DUOI_CHU_HO, thieu, sai);
        if (!thieu.isEmpty() || !sai.isEmpty()) {
            throw new NghiepVuException(thongBao(thieu, sai));
        }
        kiemTraTrungMa(ho);
        if (nk.getSoCccd() != null
                && nhanKhauDao.tonTaiCccd(nk.getSoCccd(), 0)) {
            throw new NghiepVuException(
                    "So CCCD " + nk.getSoCccd() + " da ton tai");
        }
        return ho.voiId(dao.them(ho, nk));
    }

    /**
     * Sua thong tin ho dang o. Chu ho sua o man hinh nhan khau.
     *
     * @param id khoa chinh ho can sua
     * @param tt du lieu tren form
     * @return ho sau khi sua
     * @throws NghiepVuException neu khong tim thay, ho da chuyen di, du lieu
     *         sai hoac trung ma can ho
     */
    public HoGiaDinh capNhat(int id, ThongTinHo tt) {
        HoGiaDinh cu = layDangO(id);
        List<String> thieu = new ArrayList<>();
        List<String> sai = new ArrayList<>();
        HoGiaDinh moi = docHo(tt, id, cu.getTrangThai(), thieu, sai);
        if (!thieu.isEmpty() || !sai.isEmpty()) {
            throw new NghiepVuException(thongBao(thieu, sai));
        }
        kiemTraTrungMa(moi);
        dao.capNhat(moi);
        return moi;
    }

    /**
     * Danh dau ho va moi thanh vien dang o la da chuyen di. Khong xoa du
     * lieu de giu lich su; can ho co the nhan ho moi, CCCD cua cac thanh
     * vien khai bao lai duoc o ho khac.
     *
     * @param id khoa chinh ho
     * @throws NghiepVuException neu khong tim thay hoac ho da chuyen di
     */
    public void danhDauChuyenDi(int id) {
        layDangO(id);
        dao.danhDauChuyenDi(id);
    }

    /**
     * Tim ho theo ma can ho hoac ten chu ho (RAW-027).
     *
     * @param tuKhoa tu khoa, co the rong
     * @param gomDaChuyenDi true de lay ca ho da chuyen di
     * @return danh sach ho kem ten chu ho va so thanh vien
     */
    public List<TomTatHo> timKiem(String tuKhoa, boolean gomDaChuyenDi) {
        return dao.timKiem(tuKhoa == null ? "" : tuKhoa.trim(),
                gomDaChuyenDi);
    }

    private HoGiaDinh layDangO(int id) {
        HoGiaDinh ho = dao.timTheoId(id).orElseThrow(
                () -> new NghiepVuException("Khong tim thay ho gia dinh"));
        if (ho.getTrangThai() != TrangThaiHo.DANG_O) {
            throw new NghiepVuException(
                    "Ho da chuyen di, khong the thay doi");
        }
        return ho;
    }

    /**
     * Doc va kiem tra form ho; loi duoc gom vao {@code thieu}/{@code sai}
     * de bao mot lan cung cac loi cua chu ho.
     */
    private HoGiaDinh docHo(ThongTinHo tt, int id, TrangThaiHo trangThai,
            List<String> thieu, List<String> sai) {
        String ma = chuanHoa(tt.maCanHo()).toUpperCase();
        if (ma.isEmpty()) {
            thieu.add("Ma can ho");
        } else if (!MAU_MA.matcher(ma).matches()) {
            sai.add("Ma can ho chi gom chu, so, dau '-' (toi da 20 ky tu)");
        }
        if (tt.loaiCanHo() == null) {
            thieu.add("Loai can ho");
        }
        Integer tang = docTang(tt.tang(), thieu, sai);
        BigDecimal dienTich = docDienTich(tt.dienTich(), thieu, sai);
        String sdt = chuanHoa(tt.soDienThoai()).replaceAll("[\\s.]", "");
        if (!sdt.isEmpty() && !MAU_SDT.matcher(sdt).matches()) {
            sai.add("So dien thoai gom 10 chu so, bat dau bang 0");
        }
        LocalDate ngay = tt.ngayChuyenDen();
        if (ngay == null) {
            thieu.add("Ngay chuyen den");
        } else if (ngay.isAfter(LocalDate.now(dongHo))) {
            sai.add("Ngay chuyen den khong duoc sau hom nay");
        }
        return new HoGiaDinh(id, ma, tt.loaiCanHo(), tang == null ? 0 : tang,
                dienTich, sdt.isEmpty() ? null : sdt, ngay, trangThai);
    }

    private void kiemTraTrungMa(HoGiaDinh ho) {
        if (dao.tonTaiMaDangO(ho.getMaCanHo(), ho.getId())) {
            throw new NghiepVuException(
                    "Ma can ho " + ho.getMaCanHo() + " da co ho dang o");
        }
    }

    private static Integer docTang(String giaTri, List<String> thieu,
            List<String> sai) {
        String s = chuanHoa(giaTri);
        if (s.isEmpty()) {
            thieu.add("Tang");
            return null;
        }
        try {
            int tang = Integer.parseInt(s);
            if (tang < 1 || tang > SO_TANG) {
                sai.add("Tang phai tu 1 den " + SO_TANG);
                return null;
            }
            return tang;
        } catch (NumberFormatException e) {
            sai.add("Tang phai la so nguyen");
            return null;
        }
    }

    private static BigDecimal docDienTich(String giaTri, List<String> thieu,
            List<String> sai) {
        String s = chuanHoa(giaTri).replace(',', '.');
        if (s.isEmpty()) {
            thieu.add("Dien tich");
            return null;
        }
        try {
            BigDecimal dt = new BigDecimal(s);
            if (dt.signum() <= 0 || dt.compareTo(DIEN_TICH_TOI_DA) > 0) {
                sai.add("Dien tich phai lon hon 0 va khong qua "
                        + DIEN_TICH_TOI_DA + " m2");
                return null;
            }
            if (dt.stripTrailingZeros().scale() > 2) {
                sai.add("Dien tich toi da 2 chu so thap phan");
                return null;
            }
            return dt;
        } catch (NumberFormatException e) {
            sai.add("Dien tich phai la so (vd 75.5)");
            return null;
        }
    }

    /** Gom loi thanh mot thong bao: cac truong thieu, roi tung loi sai. */
    static String thongBao(List<String> thieu, List<String> sai) {
        StringBuilder sb = new StringBuilder();
        if (!thieu.isEmpty()) {
            sb.append("Vui long nhap: ").append(String.join(", ", thieu));
        }
        for (String loi : sai) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(loi);
        }
        return sb.toString();
    }

    private static String chuanHoa(String s) {
        return s == null ? "" : s.strip();
    }
}
