package com.bluemoon.service;

import com.bluemoon.dao.DotCuTruDao;
import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiNhanKhau;
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
        public int them(HoGiaDinh h, NhanKhau chuHo, DotCuTru dotTamTru) {
            kiemTraTrung();
            int id = idHo++;
            ho.put(id, h.voiId(id));
            int chuHoId = nhanKhauDao.them(chuHo.voiHoId(id));
            if (dotTamTru != null) {
                themDot(dotTamTru.voiNhanKhauId(chuHoId));
            }
            return id;
        }

        @Override
        public void capNhat(HoGiaDinh h) {
            kiemTraTrung();
            ho.put(h.getId(), h);
        }

        @Override
        public void danhDauChuyenDi(int id) {
            ho.put(id, ho.get(id).voiTrangThai(TrangThaiHo.DA_CHUYEN_DI));
            for (NhanKhau n : nhanKhauDao.theoHo(id)) {
                nhanKhauDao.capNhatTrangThai(n.getId(),
                        TrangThaiNhanKhau.DA_CHUYEN_DI);
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
        public int them(NhanKhau nk) {
            kiemTraTrung();
            int id = idNk++;
            nhanKhau.put(id, nk.voiId(id));
            return id;
        }

        @Override
        public int themKemDot(NhanKhau nk, DotCuTru d) {
            int id = them(nk);
            themDot(d.voiNhanKhauId(id));
            return id;
        }

        @Override
        public void capNhat(NhanKhau nk) {
            kiemTraTrung();
            TrangThaiNhanKhau cu = nhanKhau.get(nk.getId()).getTrangThai();
            nhanKhau.put(nk.getId(), nk.voiTrangThai(cu));
        }

        @Override
        public void capNhatTrangThai(int id, TrangThaiNhanKhau trangThai) {
            nhanKhau.put(id, nhanKhau.get(id).voiTrangThai(trangThai));
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
        public int them(DotCuTru d) {
            kiemTraTrung();
            boolean chong = dot.values().stream().anyMatch(
                    c -> c.getNhanKhauId() == d.getNhanKhauId()
                            && c.chongNgay(d));
            if (chong) {
                throw new TrungKhoaException(CHONG_NGAY, null);
            }
            return themDot(d);
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
