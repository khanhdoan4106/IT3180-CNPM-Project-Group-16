package com.bluemoon.service;

import com.bluemoon.dao.HoGiaDinhDao;
import com.bluemoon.dao.NhanKhauDao;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiHo;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Du lieu gia lap trong bo nho cho hai DAO ho gia dinh va nhan khau, de
 * test service ma khong can MySQL.
 */
class KhoGia {

    final Map<Integer, HoGiaDinh> ho = new LinkedHashMap<>();
    final Map<Integer, NhanKhau> nhanKhau = new LinkedHashMap<>();
    final HoGiaDinhDao hoDao = new HoDao();
    final NhanKhauDao nhanKhauDao = new NkDao();
    private int idHo = 1;
    private int idNk = 1;

    private Optional<NhanKhau> chuHo(int hoId) {
        return nhanKhau.values().stream().filter(n -> n.getHoId() == hoId
                && n.getQuanHe() == QuanHeChuHo.CHU_HO).findFirst();
    }

    private class HoDao implements HoGiaDinhDao {

        @Override
        public int them(HoGiaDinh h, NhanKhau chuHo) {
            int id = idHo++;
            ho.put(id, h.voiId(id));
            nhanKhauDao.them(chuHo.voiHoId(id));
            return id;
        }

        @Override
        public void capNhat(HoGiaDinh h) {
            ho.put(h.getId(), h);
        }

        @Override
        public void capNhatTrangThai(int id, TrangThaiHo trangThai) {
            ho.put(id, ho.get(id).voiTrangThai(trangThai));
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
                    int soThanhVien = nhanKhauDao.theoHo(h.getId()).size();
                    kq.add(new TomTatHo(h, ten, soThanhVien));
                }
            }
            return kq;
        }
    }

    private class NkDao implements NhanKhauDao {

        @Override
        public int them(NhanKhau nk) {
            int id = idNk++;
            nhanKhau.put(id, nk.voiId(id));
            return id;
        }

        @Override
        public void capNhat(NhanKhau nk) {
            nhanKhau.put(nk.getId(), nk);
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
                    n -> n.getId() != boQuaId && soCccd.equals(n.getSoCccd()));
        }

        @Override
        public boolean coChuHo(int hoId, int boQuaId) {
            return nhanKhau.values().stream().anyMatch(
                    n -> n.getId() != boQuaId && n.getHoId() == hoId
                            && n.getQuanHe() == QuanHeChuHo.CHU_HO);
        }
    }
}
