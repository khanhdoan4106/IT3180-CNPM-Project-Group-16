package com.bluemoon.service;

import com.bluemoon.dao.CongNoDao;
import com.bluemoon.dao.KhoanPhiDao;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.KhoanPhi;
import com.bluemoon.model.LoaiKhoanPhi;
import com.bluemoon.model.PhaiNop;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Du lieu gia lap trong bo nho cho danh muc khoan thu va cong no, de test
 * service ma khong can MySQL.
 */
class KhoThuPhiGia {

    final Map<Integer, KhoanPhi> khoan = new LinkedHashMap<>();
    final Map<Integer, PhaiNop> phaiNop = new LinkedHashMap<>();
    final Map<Integer, YearMonth> thangCuaKhoan = new LinkedHashMap<>();
    final Set<YearMonth> thangDaChot = new HashSet<>();
    final Set<Integer> daCoPhieuThu = new HashSet<>();
    final KhoanPhiDao khoanPhiDao = new KpDao();
    final CongNoDao congNoDao = new CnDao();
    /** Gia lap may khac vua luu cung ten: lan them tiep theo bi tu choi. */
    boolean trungKhiThem;
    private int idKhoan = 1;
    private int idPhaiNop = 1;

    private class KpDao implements KhoanPhiDao {

        @Override
        public int them(KhoanPhi k) {
            if (trungKhiThem) {
                throw new TrungKhoaException(TRUNG_TEN_DOT, null);
            }
            int id = idKhoan++;
            khoan.put(id, k.voiId(id));
            return id;
        }

        @Override
        public void capNhatDonGia(int id, BigDecimal donGia) {
            khoan.put(id, khoan.get(id).voiDonGia(donGia));
        }

        @Override
        public Optional<KhoanPhi> timTheoId(int id) {
            return Optional.ofNullable(khoan.get(id));
        }

        @Override
        public List<KhoanPhi> danhSach() {
            List<KhoanPhi> ds = new ArrayList<>(khoan.values());
            ds.sort(Comparator.comparing(
                    (KhoanPhi k) -> k.loai() == LoaiKhoanPhi.TU_NGUYEN));
            return ds;
        }

        @Override
        public boolean tonTaiTen(String ten, LocalDate tuNgay) {
            return khoan.values().stream().anyMatch(
                    k -> k.ten().equals(ten) && k.tuNgay().equals(tuNgay));
        }
    }

    private class CnDao implements CongNoDao {

        @Override
        public int chot(YearMonth thang, int nguoiChot, List<PhaiNop> ds) {
            thangDaChot.add(thang);
            int soMoi = 0;
            for (PhaiNop p : ds) {
                boolean daCo = phaiNop.values().stream().anyMatch(
                        q -> thangCuaKhoan.get(q.id()).equals(thang)
                                && q.hoId() == p.hoId()
                                && q.khoanPhiId() == p.khoanPhiId());
                if (!daCo) {
                    int id = idPhaiNop++;
                    phaiNop.put(id, new PhaiNop(id, p.hoId(), p.maCanHo(),
                            p.khoanPhiId(), p.tenKhoan(), p.dienTich(),
                            p.donGia(), p.soTienGoc(), p.soTien(), null, null,
                            null));
                    thangCuaKhoan.put(id, thang);
                    soMoi++;
                }
            }
            return soMoi;
        }

        @Override
        public boolean daChot(YearMonth thang) {
            return thangDaChot.contains(thang);
        }

        @Override
        public List<PhaiNop> theoThang(YearMonth thang) {
            return phaiNop.values().stream()
                    .filter(p -> thangCuaKhoan.get(p.id()).equals(thang))
                    .toList();
        }

        @Override
        public Optional<PhaiNop> timTheoId(int id) {
            return Optional.ofNullable(phaiNop.get(id));
        }

        @Override
        public void suaSoTien(int id, BigDecimal soTien, int nguoiSua,
                String ghiChu) {
            PhaiNop p = phaiNop.get(id);
            phaiNop.put(id, new PhaiNop(id, p.hoId(), p.maCanHo(),
                    p.khoanPhiId(), p.tenKhoan(), p.dienTich(), p.donGia(),
                    p.soTienGoc(), soTien, nguoiSua, LocalDateTime.now(),
                    ghiChu));
        }

        @Override
        public boolean daCoPhieuThu(int id) {
            return daCoPhieuThu.contains(id);
        }

        @Override
        public BigDecimal tongChuaDong(int hoId) {
            return phaiNop.values().stream()
                    .filter(p -> p.hoId() == hoId
                            && !daCoPhieuThu.contains(p.id()))
                    .map(PhaiNop::soTien)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}
