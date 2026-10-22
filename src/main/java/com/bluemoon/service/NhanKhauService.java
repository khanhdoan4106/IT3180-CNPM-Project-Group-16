package com.bluemoon.service;

import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiNhanKhau;
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
        try {
            return nk.voiId(dao.them(nk));
        } catch (TrungKhoaException e) {
            throw LoiTrung.doi(e, null, nk.getSoCccd());
        }
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
        NhanKhau cu = layDangO(id);
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
        try {
            dao.capNhat(moi);
        } catch (TrungKhoaException e) {
            throw LoiTrung.doi(e, null, moi.getSoCccd());
        }
        return moi;
    }

    /**
     * Danh dau mot thanh vien da roi ho (chuyen can khac, tach ho...). Khong
     * xoa du lieu; so CCCD cua nguoi nay khai bao lai duoc o ho khac. Chu ho
     * khong chuyen di rieng duoc vi ho phai luon co chu ho.
     *
     * @param id khoa chinh nhan khau
     * @throws NghiepVuException neu khong tim thay, da chuyen di, ho da
     *         chuyen di hoac la chu ho
     */
    public void danhDauChuyenDi(int id) {
        NhanKhau nk = layDangO(id);
        if (nk.getQuanHe() == QuanHeChuHo.CHU_HO) {
            throw new NghiepVuException("Chu ho khong the chuyen di rieng."
                    + " Neu ca ho chuyen di, danh dau o man hinh Ho gia dinh");
        }
        dao.capNhatTrangThai(id, TrangThaiNhanKhau.DA_CHUYEN_DI);
    }

    /**
     * Lay cac thanh vien cua ho, ke ca nguoi da chuyen di; nguoi dang o
     * truoc, chu ho dung dau.
     *
     * @param hoId id ho gia dinh
     * @return danh sach nhan khau
     */
    public List<NhanKhau> theoHo(int hoId) {
        return dao.theoHo(hoId);
    }

    /** Lay nhan khau dang o thuoc ho dang o; nguoc lai bao loi. */
    private NhanKhau layDangO(int id) {
        NhanKhau nk = dao.timTheoId(id).orElseThrow(
                () -> new NghiepVuException("Khong tim thay nhan khau"));
        layHoDangO(nk.getHoId());
        if (nk.getTrangThai() != TrangThaiNhanKhau.DANG_O) {
            throw new NghiepVuException(
                    "Nhan khau da chuyen di, khong the thay doi");
        }
        return nk;
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

    /**
     * AC-008: so CCCD da thuoc nhan khau dang o khac thi bao trung va khong
     * luu (chat hon "canh bao" trong AC, xem 11-chot-va-gia-dinh.md).
     */
    private void kiemTraTrungCccd(NhanKhau nk) {
        if (nk.getSoCccd() != null
                && dao.tonTaiCccd(nk.getSoCccd(), nk.getId())) {
            throw new NghiepVuException(
                    "So CCCD " + nk.getSoCccd() + " da ton tai");
        }
    }
}
