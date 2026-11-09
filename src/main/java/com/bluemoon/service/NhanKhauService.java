package com.bluemoon.service;

import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.model.VaiTro;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Nghiep vu quan ly nhan khau (US-002): them, sua nhan khau cua ho; moi ho
 * co dung mot chu ho, so CCCD khong trung. Nguoi tam tru (US-003) duoc them
 * cung dot tam tru dau tien.
 */
public class NhanKhauService {

    static final String LOI_HAI_CHU_HO =
            "Hộ đã có chủ hộ. Mỗi hộ chỉ có một chủ hộ";

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
     * Them nhan khau thuong tru vao ho dang o (AC-005 - AC-008).
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien (AC-060)
     * @param hoId id ho gia dinh
     * @param tt du lieu tren form
     * @return nhan khau vua them, da co id
     * @throws NghiepVuException neu ho khong hop le, thieu/sai du lieu, ho
     *         da co chu ho hoac trung so CCCD
     */
    public NhanKhau them(TaiKhoan nguoi, int hoId, ThongTinNhanKhau tt) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        return them(nguoi, hoId, tt, null);
    }

    /**
     * Them nhan khau vao ho dang o. Nguoi dang ky tam tru duoc luu cung dot
     * tam tru dau tien trong mot giao dich (US-003), de khong co nguoi tam
     * tru nao khong co thoi han.
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien (AC-060)
     * @param hoId id ho gia dinh
     * @param tt du lieu tren form
     * @param dotTamTru dot tam tru dau tien (loai bi bo qua, luon la tam
     *        tru); bat buoc voi nguoi tam tru, bo qua voi nguoi thuong tru
     * @return nhan khau vua them, da co id
     * @throws NghiepVuException neu ho khong hop le, thieu/sai du lieu, ho
     *         da co chu ho hoac trung so CCCD
     */
    public NhanKhau them(TaiKhoan nguoi, int hoId, ThongTinNhanKhau tt,
            ThongTinDot dotTamTru) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        layHoDangO(hoId);
        List<String> thieu = new ArrayList<>();
        List<String> sai = new ArrayList<>();
        NhanKhau nk = KiemTraNhanKhau.doc(tt, 0, hoId, LocalDate.now(dongHo),
                "", thieu, sai);
        DotCuTru dot = nk.getLoaiCuTru() == LoaiCuTru.TAM_TRU
                ? KiemTraNhanKhau.docDotTamTruDauTien(dotTamTru, "", thieu,
                        sai)
                : null;
        nemNeuLoi(thieu, sai);
        if (nk.getQuanHe() == QuanHeChuHo.CHU_HO && dao.coChuHo(hoId, 0)) {
            throw new NghiepVuException(LOI_HAI_CHU_HO);
        }
        kiemTraTrungCccd(nk);
        try {
            return nk.voiId(dot == null ? dao.them(nk, nguoi.getId())
                    : dao.themKemDot(nk, dot, nguoi.getId()));
        } catch (TrungKhoaException e) {
            throw LoiTrung.doi(e, null, nk.getSoCccd());
        }
    }

    /**
     * Sua thong tin nhan khau cua ho dang o. Chu ho phai giu quan he chu ho
     * de ho luon co dung mot chu ho. Loai cu tru khong doi o day (loai trong
     * {@code tt} bi bo qua); nguoi tam tru sua duoc noi thuong tru.
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien (AC-060)
     * @param id khoa chinh nhan khau
     * @param tt du lieu tren form
     * @return nhan khau sau khi sua
     * @throws NghiepVuException neu khong tim thay, ho da chuyen di, du lieu
     *         sai, doi vai tro chu ho hoac trung so CCCD
     */
    public NhanKhau capNhat(TaiKhoan nguoi, int id, ThongTinNhanKhau tt) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        NhanKhau cu = layDangO(id);
        NhanKhau moi = kiemTra(new ThongTinNhanKhau(tt.hoTen(),
                tt.ngaySinh(), tt.gioiTinh(), tt.soCccd(), tt.quanHe(),
                cu.getLoaiCuTru(), tt.noiThuongTru()), id, cu.getHoId());
        if (cu.getQuanHe() == QuanHeChuHo.CHU_HO
                && moi.getQuanHe() != QuanHeChuHo.CHU_HO) {
            throw new NghiepVuException(
                    "Mỗi hộ phải có một chủ hộ, không đổi quan hệ của chủ hộ");
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
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien (AC-060)
     * @param id khoa chinh nhan khau
     * @throws NghiepVuException neu khong tim thay, da chuyen di, ho da
     *         chuyen di hoac la chu ho
     */
    public void danhDauChuyenDi(TaiKhoan nguoi, int id) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        NhanKhau nk = layDangO(id);
        if (nk.getQuanHe() == QuanHeChuHo.CHU_HO) {
            throw new NghiepVuException("Chủ hộ không thể chuyển đi riêng."
                    + " Nếu cả hộ chuyển đi, đánh dấu ở màn hình Hộ gia đình");
        }
        dao.capNhatTrangThai(id, TrangThaiNhanKhau.DA_CHUYEN_DI,
                nguoi.getId());
    }

    /**
     * Lay cac thanh vien cua ho, ke ca nguoi da chuyen di; nguoi dang o
     * truoc, chu ho dung dau.
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien (AC-060)
     * @param hoId id ho gia dinh
     * @return danh sach nhan khau
     */
    public List<NhanKhau> theoHo(TaiKhoan nguoi, int hoId) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        return dao.theoHo(hoId);
    }

    /**
     * Doi chu ho (US-026, AC-073 - AC-075). Chu ho cu doi sang quan he
     * {@code quanHeCu}, hoac roi ho neu {@code quanHeCu} la null (chu ho mat,
     * chuyen di, tach ho); thanh vien duoc chon thanh chu ho. Ho luon co
     * dung mot chu ho.
     *
     * @param nguoi tai khoan dang dang nhap, phai la Quan tri vien (AC-060)
     * @param hoId ho dang o
     * @param chuHoMoiId thanh vien dang o duoc chon lam chu ho
     * @param quanHeCu quan he moi cua chu ho cu voi chu ho moi; null neu chu
     *        ho cu roi ho
     * @throws NghiepVuException neu khong co quyen, ho khong con thanh vien
     *         nao khac, thanh vien da chuyen di, khong thuoc ho hoac quan he
     *         moi la chu ho
     */
    public void doiChuHo(TaiKhoan nguoi, int hoId, int chuHoMoiId,
            QuanHeChuHo quanHeCu) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        layHoDangO(hoId);
        List<NhanKhau> dangO = dao.theoHo(hoId).stream()
                .filter(n -> n.getTrangThai() == TrangThaiNhanKhau.DANG_O)
                .toList();
        NhanKhau cu = dangO.stream()
                .filter(n -> n.getQuanHe() == QuanHeChuHo.CHU_HO)
                .findFirst().orElseThrow(() -> new NghiepVuException(
                        "Hộ chưa có chủ hộ đang ở"));
        if (dangO.size() < 2) {
            throw new NghiepVuException("Hộ không còn thành viên nào khác"
                    + " đang ở. Nếu cả hộ chuyển đi, đánh dấu ở màn hình"
                    + " Hộ gia đình");
        }
        NhanKhau moi = dao.timTheoId(chuHoMoiId).orElseThrow(
                () -> new NghiepVuException("Không tìm thấy nhân khẩu"));
        if (moi.getHoId() != hoId) {
            throw new NghiepVuException("Người được chọn không thuộc hộ này");
        }
        if (moi.getTrangThai() != TrangThaiNhanKhau.DANG_O) {
            throw new NghiepVuException(moi.getHoTen()
                    + " đã chuyển đi, không làm chủ hộ được");
        }
        if (moi.getId() == cu.getId()) {
            throw new NghiepVuException(moi.getHoTen() + " đang là chủ hộ");
        }
        if (quanHeCu == QuanHeChuHo.CHU_HO) {
            throw new NghiepVuException(
                    "Chọn quan hệ khác chủ hộ cho chủ hộ cũ");
        }
        try {
            dao.doiChuHo(cu.getId(), moi.getId(),
                    quanHeCu == null ? QuanHeChuHo.KHAC : quanHeCu,
                    quanHeCu == null, nguoi.getId());
        } catch (TrungKhoaException e) {
            throw LoiTrung.doi(e, null, null);
        }
    }

    /** Lay nhan khau dang o thuoc ho dang o; nguoc lai bao loi. */
    private NhanKhau layDangO(int id) {
        NhanKhau nk = dao.timTheoId(id).orElseThrow(
                () -> new NghiepVuException("Không tìm thấy nhân khẩu"));
        layHoDangO(nk.getHoId());
        if (nk.getTrangThai() != TrangThaiNhanKhau.DANG_O) {
            throw new NghiepVuException(
                    "Nhân khẩu đã chuyển đi, không thể thay đổi");
        }
        return nk;
    }

    private void layHoDangO(int hoId) {
        HoGiaDinh ho = hoDao.timTheoId(hoId).orElseThrow(
                () -> new NghiepVuException("Không tìm thấy hộ gia đình"));
        if (ho.getTrangThai() != TrangThaiHo.DANG_O) {
            throw new NghiepVuException(
                    "Hộ đã chuyển đi, không thể thay đổi nhân khẩu");
        }
    }

    private NhanKhau kiemTra(ThongTinNhanKhau tt, int id, int hoId) {
        List<String> thieu = new ArrayList<>();
        List<String> sai = new ArrayList<>();
        NhanKhau nk = KiemTraNhanKhau.doc(tt, id, hoId,
                LocalDate.now(dongHo), "", thieu, sai);
        nemNeuLoi(thieu, sai);
        return nk;
    }

    private static void nemNeuLoi(List<String> thieu, List<String> sai) {
        if (!thieu.isEmpty() || !sai.isEmpty()) {
            throw new NghiepVuException(
                    HoGiaDinhService.thongBao(thieu, sai));
        }
    }

    /**
     * AC-008: so CCCD da thuoc nhan khau dang o khac thi bao trung va khong
     * luu (chat hon "canh bao" trong AC, xem 11-chot-va-gia-dinh.md).
     */
    private void kiemTraTrungCccd(NhanKhau nk) {
        if (nk.getSoCccd() != null
                && dao.tonTaiCccd(nk.getSoCccd(), nk.getId())) {
            throw LoiTrung.trungCccd(nk.getSoCccd());
        }
    }
}
