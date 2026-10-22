package com.bluemoon.service;

import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TrangThaiHo;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Nghiep vu quan ly nhan khau (US-002): them, sua nhan khau cua ho; moi ho
 * co dung mot chu ho, so CCCD khong trung.
 */
public class NhanKhauService {

    static final String LOI_HAI_CHU_HO =
            "Ho da co chu ho. Moi ho chi co mot chu ho";

    private final NhanKhauDao dao;
    private final HoGiaDinhDao hoDao;
    private final Clock dongHo;

    /**
     * Tao dich vu dung dong ho he thong.
     *
     * @param dao nguon du lieu nhan khau
     * @param hoDao nguon du lieu ho gia dinh
     */
    public NhanKhauService(NhanKhauDao dao, HoGiaDinhDao hoDao) {
        this(dao, hoDao, Clock.systemDefaultZone());
    }

    /**
     * Tao dich vu voi dong ho chi dinh (de test ngay hien tai).
     *
     * @param dao nguon du lieu nhan khau
     * @param hoDao nguon du lieu ho gia dinh
     * @param dongHo dong ho dung de lay ngay hom nay
     */
    public NhanKhauService(NhanKhauDao dao, HoGiaDinhDao hoDao,
            Clock dongHo) {
        this.dao = dao;
        this.hoDao = hoDao;
        this.dongHo = dongHo;
    }

    /**
     * Them nhan khau vao ho dang o (AC-005 - AC-008).
     *
     * @param hoId id ho gia dinh
     * @param tt du lieu tren form
     * @return nhan khau vua them, da co id
     * @throws NghiepVuException neu ho khong hop le, thieu/sai du lieu, ho
     *         da co chu ho hoac trung so CCCD
     */
    public NhanKhau them(int hoId, ThongTinNhanKhau tt) {
        layHoDangO(hoId);
        NhanKhau nk = kiemTra(tt, 0, hoId);
        if (nk.getQuanHe() == QuanHeChuHo.CHU_HO && dao.coChuHo(hoId, 0)) {
            throw new NghiepVuException(LOI_HAI_CHU_HO);
        }
        kiemTraTrungCccd(nk);
        return nk.voiId(dao.them(nk));
    }

    /**
     * Sua thong tin nhan khau cua ho dang o. Chu ho phai giu quan he chu ho
     * de ho luon co dung mot chu ho.
     *
     * @param id khoa chinh nhan khau
     * @param tt du lieu tren form
     * @return nhan khau sau khi sua
     * @throws NghiepVuException neu khong tim thay, ho da chuyen di, du lieu
     *         sai, doi vai tro chu ho hoac trung so CCCD
     */
    public NhanKhau capNhat(int id, ThongTinNhanKhau tt) {
        NhanKhau cu = dao.timTheoId(id).orElseThrow(
                () -> new NghiepVuException("Khong tim thay nhan khau"));
        layHoDangO(cu.getHoId());
        NhanKhau moi = kiemTra(tt, id, cu.getHoId());
        if (cu.getQuanHe() == QuanHeChuHo.CHU_HO
                && moi.getQuanHe() != QuanHeChuHo.CHU_HO) {
            throw new NghiepVuException(
                    "Moi ho phai co mot chu ho, khong doi quan he cua chu ho");
        }
        if (moi.getQuanHe() == QuanHeChuHo.CHU_HO
                && dao.coChuHo(cu.getHoId(), id)) {
            throw new NghiepVuException(LOI_HAI_CHU_HO);
        }
        kiemTraTrungCccd(moi);
        dao.capNhat(moi);
        return moi;
    }

    /**
     * Lay cac thanh vien cua ho, chu ho dung dau.
     *
     * @param hoId id ho gia dinh
     * @return danh sach nhan khau
     */
    public List<NhanKhau> theoHo(int hoId) {
        return dao.theoHo(hoId);
    }

    private void layHoDangO(int hoId) {
        HoGiaDinh ho = hoDao.timTheoId(hoId).orElseThrow(
                () -> new NghiepVuException("Khong tim thay ho gia dinh"));
        if (ho.getTrangThai() != TrangThaiHo.DANG_O) {
            throw new NghiepVuException(
                    "Ho da chuyen di, khong the thay doi nhan khau");
        }
    }

    private NhanKhau kiemTra(ThongTinNhanKhau tt, int id, int hoId) {
        List<String> thieu = new ArrayList<>();
        List<String> sai = new ArrayList<>();
        NhanKhau nk = KiemTraNhanKhau.doc(tt, id, hoId,
                LocalDate.now(dongHo), "", thieu, sai);
        if (!thieu.isEmpty() || !sai.isEmpty()) {
            throw new NghiepVuException(
                    HoGiaDinhService.thongBao(thieu, sai));
        }
        return nk;
    }

    /** AC-008: so CCCD da thuoc nhan khau khac thi canh bao, khong luu. */
    private void kiemTraTrungCccd(NhanKhau nk) {
        if (nk.getSoCccd() != null
                && dao.tonTaiCccd(nk.getSoCccd(), nk.getId())) {
            throw new NghiepVuException(
                    "So CCCD " + nk.getSoCccd() + " da ton tai");
        }
    }
}
