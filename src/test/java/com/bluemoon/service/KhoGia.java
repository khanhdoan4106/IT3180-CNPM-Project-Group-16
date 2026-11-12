package com.bluemoon.service;

import com.bluemoon.dao.DotCuTruDao;
import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiBienDong;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiNhanKhau;
import com.bluemoon.model.VaiTro;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Du lieu gia lap trong bo nho cho cac DAO ho gia dinh, nhan khau va dot
 * tam tru/tam vang, de test service ma khong can MySQL.
 */
class KhoGia {

    final Map<Integer, HoGiaDinh> ho = new LinkedHashMap<>();
    final Map<Integer, NhanKhau> nhanKhau = new LinkedHashMap<>();
    final HoGiaDinhDao hoDao = new HoDao();
    final NhanKhauDao nhanKhauDao = new NkDao();
    final Map<Integer, DotCuTru> dot = new LinkedHashMap<>();
    final DotCuTruDao dotDao = new DotDao();
    private int idHo = 1;
    private int idNk = 1;
    private int idDot = 1;
    /**
     * Gia lap may khac vua luu cung gia tri: lan ghi them/sua tiep theo bi
     * CSDL tu choi voi rang buoc nay. Null la binh thuong.
     */
    String trungKhiGhi;

    /** Quan tri vien dung chung cho cac test. */
    static final TaiKhoan QT = new TaiKhoan(1, "admin", "x", "Quản trị",
            VaiTro.QUAN_TRI, true);
    /** Thu quy dung chung cho cac test. */
    static final TaiKhoan TQ = new TaiKhoan(2, "thuquy", "x", "Thủ quỹ",
            VaiTro.THU_QUY, true);
    /** Lich su bien dong da ghi, dang "LOAI:hoId:nhanKhauId:nguoiGhi". */
    final List<String> lichSu = new ArrayList<>();

    private void ghi(int hoId, int nkId, LoaiBienDong loai, int nguoiGhi) {
        lichSu.add(loai.name() + ":" + hoId + ":" + nkId + ":" + nguoiGhi);
    }

    private void kiemTraTrung() {
        if (trungKhiGhi != null) {
            throw new TrungKhoaException(trungKhiGhi, null);
        }
    }

    private Optional<NhanKhau> chuHo(int hoId) {
        return nhanKhau.values().stream().filter(n -> n.getHoId() == hoId
                && n.getQuanHe() == QuanHeChuHo.CHU_HO).findFirst();
    }

    private static boolean dangO(NhanKhau n) {
        return n.getTrangThai() == TrangThaiNhanKhau.DANG_O;
    }

    private class HoDao implements HoGiaDinhDao {

        @Override
        public int them(HoGiaDinh h, NhanKhau chuHo, DotCuTru dotTamTru,
                int nguoiGhi) {
            kiemTraTrung();
            int id = idHo++;
            ho.put(id, h.voiId(id));
            int chuHoId = nhanKhauDao.them(chuHo.voiHoId(id), nguoiGhi);
            if (dotTamTru != null) {
                DotCuTru d = dotTamTru.voiNhanKhauId(chuHoId);
                themDot(d);
                ghi(id, chuHoId, LoaiBienDong.valueOf(d.getLoai().name()),
                        nguoiGhi);
            }
            return id;
        }

        @Override
        public void capNhat(HoGiaDinh h, int nguoiId) {
            kiemTraTrung();
            ho.put(h.getId(), h);
        }

        @Override
        public void danhDauChuyenDi(int id, int nguoiGhi) {
            ho.put(id, ho.get(id).voiTrangThai(TrangThaiHo.DA_CHUYEN_DI));
            for (NhanKhau n : nhanKhauDao.theoHo(id)) {
                nhanKhauDao.capNhatTrangThai(n.getId(),
                        TrangThaiNhanKhau.DA_CHUYEN_DI, nguoiGhi);
            }
        }

        @Override
        public Optional<HoGiaDinh> timTheoId(int id) {
            return Optional.ofNullable(ho.get(id));
        }

        @Override
        public boolean tonTaiMaDangO(String maCanHo, int boQuaId) {
            return ho.values().stream().anyMatch(h -> h.getId() != boQuaId
                    && h.getTrangThai() == TrangThaiHo.DANG_O
                    && h.getMaCanHo().equals(maCanHo));
        }

        @Override
        public List<TomTatHo> timKiem(String tuKhoa, boolean gomDaChuyenDi) {
            List<TomTatHo> kq = new ArrayList<>();
            for (HoGiaDinh h : ho.values()) {
                String ten = chuHo(h.getId()).map(NhanKhau::getHoTen)
                        .orElse(null);
                boolean khop = h.getMaCanHo().contains(tuKhoa)
                        || (ten != null && ten.contains(tuKhoa));
                if (khop && (gomDaChuyenDi
                        || h.getTrangThai() == TrangThaiHo.DANG_O)) {
                    int soThanhVien = (int) nhanKhauDao.theoHo(h.getId())
                            .stream().filter(KhoGia::dangO).count();
                    kq.add(new TomTatHo(h, ten, soThanhVien));
                }
            }
            return kq;
        }
    }

    private class NkDao implements NhanKhauDao {

        @Override
        public int them(NhanKhau nk, int nguoiGhi) {
            kiemTraTrung();
            int id = idNk++;
            nhanKhau.put(id, nk.voiId(id));
            ghi(nk.getHoId(), id, LoaiBienDong.CHUYEN_DEN, nguoiGhi);
            return id;
        }

        @Override
        public int themKemDot(NhanKhau nk, DotCuTru d, int nguoiGhi) {
            int id = them(nk, nguoiGhi);
            themDot(d.voiNhanKhauId(id));
            ghi(nk.getHoId(), id, LoaiBienDong.TAM_TRU, nguoiGhi);
            return id;
        }

        @Override
        public void capNhat(NhanKhau nk, int nguoiId) {
            kiemTraTrung();
            TrangThaiNhanKhau cu = nhanKhau.get(nk.getId()).getTrangThai();
            nhanKhau.put(nk.getId(), nk.voiTrangThai(cu));
        }

        @Override
        public void capNhatTrangThai(int id, TrangThaiNhanKhau trangThai,
                int nguoiGhi) {
            NhanKhau cu = nhanKhau.get(id);
            nhanKhau.put(id, cu.voiTrangThai(trangThai));
            if (dangO(cu) && trangThai == TrangThaiNhanKhau.DA_CHUYEN_DI) {
                ghi(cu.getHoId(), id, LoaiBienDong.CHUYEN_DI, nguoiGhi);
            }
        }

        @Override
        public void doiChuHo(int cuId, int moiId, QuanHeChuHo quanHeCu,
                boolean cuChuyenDi, int nguoiGhi) {
            NhanKhau cu = nhanKhau.get(cuId);
            NhanKhau moi = nhanKhau.get(moiId);
            nhanKhau.put(cuId, cu.voiQuanHe(quanHeCu).voiTrangThai(
                    cuChuyenDi ? TrangThaiNhanKhau.DA_CHUYEN_DI
                            : TrangThaiNhanKhau.DANG_O));
            nhanKhau.put(moiId, moi.voiQuanHe(QuanHeChuHo.CHU_HO));
            ghi(cu.getHoId(), moiId, LoaiBienDong.DOI_CHU_HO, nguoiGhi);
            if (cuChuyenDi) {
                ghi(cu.getHoId(), cuId, LoaiBienDong.CHUYEN_DI, nguoiGhi);
            }
        }

        @Override
        public Optional<NhanKhau> timTheoId(int id) {
            return Optional.ofNullable(nhanKhau.get(id));
        }

        @Override
        public List<NhanKhau> theoHo(int hoId) {
            return nhanKhau.values().stream()
                    .filter(n -> n.getHoId() == hoId).toList();
        }

        @Override
        public boolean tonTaiCccd(String soCccd, int boQuaId) {
            return nhanKhau.values().stream().anyMatch(
                    n -> n.getId() != boQuaId && dangO(n)
                            && soCccd.equals(n.getSoCccd()));
        }

        @Override
        public boolean coChuHo(int hoId, int boQuaId) {
            return nhanKhau.values().stream().anyMatch(
                    n -> n.getId() != boQuaId && n.getHoId() == hoId
                            && n.getQuanHe() == QuanHeChuHo.CHU_HO);
        }
    }

    private int themDot(DotCuTru d) {
        int id = idDot++;
        dot.put(id, d.voiId(id));
        return id;
    }

    private class DotDao implements DotCuTruDao {

        @Override
        public int them(DotCuTru d, int nguoiGhi) {
            kiemTraTrung();
            boolean chong = dot.values().stream().anyMatch(
                    c -> c.getNhanKhauId() == d.getNhanKhauId()
                            && c.chongNgay(d));
            if (chong) {
                throw new TrungKhoaException(CHONG_NGAY, null);
            }
            int id = themDot(d);
            ghi(nhanKhau.get(d.getNhanKhauId()).getHoId(), d.getNhanKhauId(),
                    LoaiBienDong.valueOf(d.getLoai().name()), nguoiGhi);
            return id;
        }

        @Override
        public List<DotCuTru> theoNhanKhau(int nhanKhauId) {
            return dot.values().stream()
                    .filter(d -> d.getNhanKhauId() == nhanKhauId)
                    .sorted(Comparator.comparing(DotCuTru::getTuNgay)
                            .reversed())
                    .toList();
        }

        @Override
        public List<DotCuTru> theoHo(int hoId) {
            return dot.values().stream()
                    .filter(d -> nhanKhau.get(d.getNhanKhauId())
                            .getHoId() == hoId)
                    .toList();
        }
    }
}
