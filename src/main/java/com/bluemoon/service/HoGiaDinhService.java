package com.bluemoon.service;

import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.TrangThaiHo;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Nghiep vu quan ly ho gia dinh (US-001): them, sua, tim kiem, danh dau ho
 * chuyen di.
 */
public class HoGiaDinhService {

    /** So tang cua toa nha (RAW-001). */
    public static final int SO_TANG = 30;
    /** Dien tich san toa nha, dien tich mot can khong vuot qua (RAW-001). */
    public static final BigDecimal DIEN_TICH_TOI_DA = new BigDecimal("450");

    private static final Pattern MAU_MA = Pattern.compile("[A-Z0-9-]{1,20}");
    private static final Pattern MAU_SDT = Pattern.compile("0\\d{9}");
    private static final int TEN_TOI_DA = 100;

    private final HoGiaDinhDao dao;
    private final Clock dongHo;

    /**
     * Tao dich vu dung dong ho he thong.
     *
     * @param dao nguon du lieu ho gia dinh
     */
    public HoGiaDinhService(HoGiaDinhDao dao) {
        this(dao, Clock.systemDefaultZone());
    }

    /**
     * Tao dich vu voi dong ho chi dinh (de test ngay hien tai).
     *
     * @param dao nguon du lieu ho gia dinh
     * @param dongHo dong ho dung de lay ngay hom nay
     */
    public HoGiaDinhService(HoGiaDinhDao dao, Clock dongHo) {
        this.dao = dao;
        this.dongHo = dongHo;
    }

    /**
     * Them ho gia dinh moi (AC-001 - AC-004).
     *
     * @param tt du lieu tren form
     * @return ho vua them, da co id
     * @throws NghiepVuException neu thieu/sai du lieu hoac trung ma can ho
     */
    public HoGiaDinh them(ThongTinHo tt) {
        HoGiaDinh ho = kiemTra(tt, 0, TrangThaiHo.DANG_O);
        return ho.voiId(dao.them(ho));
    }

    /**
     * Sua thong tin ho dang o.
     *
     * @param id khoa chinh ho can sua
     * @param tt du lieu tren form
     * @return ho sau khi sua
     * @throws NghiepVuException neu khong tim thay, ho da chuyen di, du lieu
     *         sai hoac trung ma can ho
     */
    public HoGiaDinh capNhat(int id, ThongTinHo tt) {
        HoGiaDinh cu = layDangO(id);
        HoGiaDinh moi = kiemTra(tt, id, cu.getTrangThai());
        dao.capNhat(moi);
        return moi;
    }

    /**
     * Danh dau ho da chuyen di. Khong xoa du lieu de giu lich su; can ho
     * co the nhan ho moi.
     *
     * @param id khoa chinh ho
     * @throws NghiepVuException neu khong tim thay hoac ho da chuyen di
     */
    public void danhDauChuyenDi(int id) {
        layDangO(id);
        dao.capNhatTrangThai(id, TrangThaiHo.DA_CHUYEN_DI);
    }

    /**
     * Tim ho theo ma can ho hoac ten chu ho (RAW-027).
     *
     * @param tuKhoa tu khoa, co the rong
     * @param gomDaChuyenDi true de lay ca ho da chuyen di
     * @return danh sach ho
     */
    public List<HoGiaDinh> timKiem(String tuKhoa, boolean gomDaChuyenDi) {
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
     * Kiem tra toan bo form, gom moi loi thanh mot thong bao, roi kiem tra
     * trung ma can ho.
     */
    private HoGiaDinh kiemTra(ThongTinHo tt, int id, TrangThaiHo trangThai) {
        List<String> thieu = new ArrayList<>();
        List<String> sai = new ArrayList<>();

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
        String ten = chuanHoa(tt.tenChuHo()).replaceAll("\\s+", " ");
        if (ten.isEmpty()) {
            thieu.add("Ten chu ho");
        } else if (ten.length() > TEN_TOI_DA) {
            sai.add("Ten chu ho toi da " + TEN_TOI_DA + " ky tu");
        }
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

        if (!thieu.isEmpty() || !sai.isEmpty()) {
            throw new NghiepVuException(thongBao(thieu, sai));
        }
        if (dao.tonTaiMaDangO(ma, id)) {
            throw new NghiepVuException(
                    "Ma can ho " + ma + " da co ho dang o");
        }
        return new HoGiaDinh(id, ma, tt.loaiCanHo(), tang, dienTich, ten,
                sdt.isEmpty() ? null : sdt, ngay, trangThai);
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

    private static String thongBao(List<String> thieu, List<String> sai) {
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
