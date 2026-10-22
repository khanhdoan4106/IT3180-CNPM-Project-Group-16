package com.bluemoon.service;

import com.bluemoon.dao.DotCuTruDao;
import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.TinhTrangCuTru;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiNhanKhau;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nghiep vu tam tru, tam vang (US-003): ghi nhan tung dot co ngay bat dau va
 * ket thuc, khong cho cac dot cua cung mot nguoi chong ngay, va tinh tinh
 * trang cu tru theo ngay hom nay.
 */
public class DotCuTruService {

    static final String LOI_CHONG_NGAY =
            "Thoi gian bi chong voi mot dot tam tru/tam vang da ghi nhan";

    private static final DateTimeFormatter DINH_DANG =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DotCuTruDao dao;
    private final NhanKhauDao nhanKhauDao;
    private final HoGiaDinhDao hoDao;
    private final Clock dongHo;

    /**
     * Tao dich vu dung dong ho he thong.
     *
     * @param dao nguon du lieu tam tru/tam vang
     * @param nhanKhauDao nguon du lieu nhan khau
     * @param hoDao nguon du lieu ho gia dinh
     */
    public DotCuTruService(DotCuTruDao dao, NhanKhauDao nhanKhauDao,
            HoGiaDinhDao hoDao) {
        this(dao, nhanKhauDao, hoDao, Clock.systemDefaultZone());
    }

    /**
     * Tao dich vu voi dong ho chi dinh (de test ngay hien tai).
     *
     * @param dao nguon du lieu tam tru/tam vang
     * @param nhanKhauDao nguon du lieu nhan khau
     * @param hoDao nguon du lieu ho gia dinh
     * @param dongHo dong ho dung de lay ngay hom nay
     */
    public DotCuTruService(DotCuTruDao dao, NhanKhauDao nhanKhauDao,
            HoGiaDinhDao hoDao, Clock dongHo) {
        this.dao = dao;
        this.nhanKhauDao = nhanKhauDao;
        this.hoDao = hoDao;
        this.dongHo = dongHo;
    }

    /**
     * Ghi nhan mot dot tam tru hoac tam vang (AC-009 - AC-011). Tam tru chi
     * ghi cho nguoi dang ky tam tru (gia han = them dot moi), tam vang chi
     * ghi cho nguoi thuong tru.
     *
     * @param nhanKhauId id nhan khau
     * @param tt du lieu tren form
     * @return dot vua ghi, da co id
     * @throws NghiepVuException neu nhan khau/ho khong con o, thieu ngay,
     *         ngay ket thuc truoc ngay bat dau, sai loai hoac chong ngay
     */
    public DotCuTru ghiNhan(int nhanKhauId, ThongTinDot tt) {
        NhanKhau nk = layDangO(nhanKhauId);
        List<String> thieu = new ArrayList<>();
        List<String> sai = new ArrayList<>();
        DotCuTru dot = KiemTraNhanKhau.docDot(tt, nhanKhauId, "", thieu, sai);
        if (!thieu.isEmpty() || !sai.isEmpty()) {
            throw new NghiepVuException(
                    HoGiaDinhService.thongBao(thieu, sai));
        }
        if (dot.getLoai() == LoaiDotCuTru.TAM_TRU
                && nk.getLoaiCuTru() != LoaiCuTru.TAM_TRU) {
            throw new NghiepVuException("Chi ghi tam tru cho nguoi dang ky"
                    + " tam tru. Nguoi thuong tru vang mat thi ghi tam vang");
        }
        if (dot.getLoai() == LoaiDotCuTru.TAM_VANG
                && nk.getLoaiCuTru() != LoaiCuTru.THUONG_TRU) {
            throw new NghiepVuException(
                    "Chi ghi tam vang cho nguoi thuong tru");
        }
        for (DotCuTru cu : dao.theoNhanKhau(nhanKhauId)) {
            if (dot.chongNgay(cu)) {
                throw new NghiepVuException("Thoi gian bi chong voi dot "
                        + cu.getLoai().getNhan().toLowerCase() + " "
                        + cu.getTuNgay().format(DINH_DANG) + " - "
                        + cu.getDenNgay().format(DINH_DANG));
            }
        }
        try {
            return dot.voiId(dao.them(dot));
        } catch (TrungKhoaException e) {
            throw LoiTrung.doi(e, null, null);
        }
    }

    /**
     * Lay cac dot cua mot nhan khau, moi nhat truoc.
     *
     * @param nhanKhauId id nhan khau
     * @return danh sach dot
     */
    public List<DotCuTru> theoNhanKhau(int nhanKhauId) {
        return dao.theoNhanKhau(nhanKhauId);
    }

    /**
     * Tinh tinh trang cu tru hom nay cua cac thanh vien trong ho: han tam tru
     * (to do khi qua han ma chua gia han) va dang tam vang den ngay nao.
     *
     * @param hoId id ho gia dinh
     * @return tinh trang theo id nhan khau; nguoi khong co trong map thi
     *         dung {@link TinhTrangCuTru#TRONG}
     */
    public Map<Integer, TinhTrangCuTru> tinhTrangTheoHo(int hoId) {
        LocalDate homNay = LocalDate.now(dongHo);
        Map<Integer, List<DotCuTru>> theoNguoi = new HashMap<>();
        for (DotCuTru d : dao.theoHo(hoId)) {
            theoNguoi.computeIfAbsent(d.getNhanKhauId(),
                    k -> new ArrayList<>()).add(d);
        }
        Map<Integer, TinhTrangCuTru> ketQua = new HashMap<>();
        for (NhanKhau nk : nhanKhauDao.theoHo(hoId)) {
            List<DotCuTru> dots = theoNguoi.getOrDefault(nk.getId(),
                    List.of());
            ketQua.put(nk.getId(), tinhTrang(nk, dots, homNay));
        }
        return ketQua;
    }

    /** Tinh tinh trang cua mot nguoi tu cac dot cua nguoi do. */
    static TinhTrangCuTru tinhTrang(NhanKhau nk, List<DotCuTru> dots,
            LocalDate homNay) {
        LocalDate hanTamTru = null;
        LocalDate tamVangDen = null;
        for (DotCuTru d : dots) {
            if (d.getLoai() == LoaiDotCuTru.TAM_TRU) {
                if (hanTamTru == null || d.getDenNgay().isAfter(hanTamTru)) {
                    hanTamTru = d.getDenNgay();
                }
            } else if (d.dangHieuLuc(homNay)) {
                tamVangDen = d.getDenNgay();
            }
        }
        boolean quaHan = nk.getLoaiCuTru() == LoaiCuTru.TAM_TRU
                && nk.getTrangThai() == TrangThaiNhanKhau.DANG_O
                && (hanTamTru == null || hanTamTru.isBefore(homNay));
        return new TinhTrangCuTru(hanTamTru, quaHan, tamVangDen);
    }

    private NhanKhau layDangO(int nhanKhauId) {
        NhanKhau nk = nhanKhauDao.timTheoId(nhanKhauId).orElseThrow(
                () -> new NghiepVuException("Khong tim thay nhan khau"));
        HoGiaDinh ho = hoDao.timTheoId(nk.getHoId()).orElseThrow(
                () -> new NghiepVuException("Khong tim thay ho gia dinh"));
        if (ho.getTrangThai() != TrangThaiHo.DANG_O
                || nk.getTrangThai() != TrangThaiNhanKhau.DANG_O) {
            throw new NghiepVuException(
                    "Nhan khau da chuyen di, khong ghi tam tru/tam vang");
        }
        return nk;
    }
}
