package com.bluemoon.service;

import com.bluemoon.dao.PhieuThuDao;
import com.bluemoon.dao.XungDotException;
import com.bluemoon.model.ChiTietPhieuThu;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.PhaiNop;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.PhieuThuMoi;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TongDongGop;
import com.bluemoon.model.TrangThaiHo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Phieu thu gia lap trong bo nho, dung chung khoan thu va khoan phai nop
 * voi {@link KhoThuPhiGia} de test service ma khong can MySQL. Kiem tra lai
 * duoi khoa nhu PhieuThuDaoImpl (so tien doi, da thu, quy tac 0 d).
 */
class KhoPhieuThuGia implements PhieuThuDao {

    final Map<Integer, TomTatHo> ho = new LinkedHashMap<>();
    final Map<Integer, PhieuThu> phieu = new LinkedHashMap<>();
    final Map<Integer, String> tenTaiKhoan = new HashMap<>();
    private final Map<Integer, Integer> boDem = new HashMap<>();
    private final KhoThuPhiGia kho;
    /** Gia lap may khac vua doi du lieu: lan lap phieu tiep theo bi tu choi. */
    XungDotException.Loai xungDotKhiLap;
    private int idPhieu = 1;
    private int idDong = 1;

    KhoPhieuThuGia(KhoThuPhiGia kho) {
        this.kho = kho;
    }

    void themTaiKhoan(TaiKhoan tk) {
        tenTaiKhoan.put(tk.getId(), tk.getHoTen());
    }

    int themHo(String ma, TrangThaiHo trangThai, String chuHo) {
        int id = ho.size() + 1;
        ho.put(id, new TomTatHo(new HoGiaDinh(id, ma, LoaiCanHo.CAN_HO, 6,
                new BigDecimal("75.50"), null, LocalDate.of(2026, 1, 1),
                trangThai), chuHo, 2));
        return id;
    }

    /** Them khoan phai nop cua mot thang da chot, tra ve id. */
    int themPhaiNop(int hoId, int khoanId, YearMonth thang, String soTien) {
        int id = kho.phaiNop.size() + 1;
        BigDecimal tien = new BigDecimal(soTien);
        kho.phaiNop.put(id, new PhaiNop(id, hoId,
                ho.get(hoId).ho().getMaCanHo(), khoanId,
                kho.khoan.get(khoanId).ten(), new BigDecimal("75.50"),
                BigDecimal.ONE, tien, tien, null, null, null, false));
        kho.thangCuaKhoan.put(id, thang);
        kho.thangDaChot.add(thang);
        return id;
    }

    @Override
    public List<TomTatHo> timHoTheoMa(String maCanHo) {
        return ho.values().stream()
                .filter(t -> t.ho().getMaCanHo().equalsIgnoreCase(maCanHo)
                        && (t.ho().getTrangThai() == TrangThaiHo.DANG_O
                                || !khoanChuaDong(t.ho().getId()).isEmpty()))
                .sorted(Comparator.comparing(
                        (TomTatHo t) -> t.ho().getTrangThai()
                                != TrangThaiHo.DANG_O))
                .toList();
    }

    @Override
    public Optional<TomTatHo> timHo(int hoId) {
        return Optional.ofNullable(ho.get(hoId));
    }

    @Override
    public List<KhoanNo> khoanChuaDong(int hoId) {
        return chuaDong().stream().filter(k -> k.hoId() == hoId).toList();
    }

    @Override
    public List<KhoanNo> chuaDongDenThang(YearMonth thang) {
        return chuaDong().stream().filter(k -> !k.thang().isAfter(thang))
                .sorted(Comparator.comparing(KhoanNo::maCanHo))
                .toList();
    }

    private List<KhoanNo> chuaDong() {
        return kho.phaiNop.values().stream()
                .filter(p -> p.soTien().signum() > 0
                        && !kho.daCoPhieuThu.contains(p.id()))
                .map(p -> new KhoanNo(p.id(), p.hoId(), p.maCanHo(),
                        p.khoanPhiId(), p.tenKhoan(),
                        kho.thangCuaKhoan.get(p.id()), p.soTien()))
                .sorted(Comparator.comparing(KhoanNo::thang)
                        .thenComparing(KhoanNo::tenKhoan))
                .toList();
    }

    @Override
    public List<TongDongGop> tongDongGop(int hoId) {
        Map<Integer, TongDongGop> tong = new LinkedHashMap<>();
        for (PhieuThu p : phieu.values()) {
            if (p.hoId() != hoId || p.daHuy()) {
                continue;
            }
            for (ChiTietPhieuThu d : p.chiTiet()) {
                if (d.phaiNopId() == null) {
                    TongDongGop cu = tong.getOrDefault(d.khoanPhiId(),
                            new TongDongGop(d.khoanPhiId(), BigDecimal.ZERO,
                                    0));
                    tong.put(d.khoanPhiId(), new TongDongGop(d.khoanPhiId(),
                            cu.tong().add(d.soTien()), cu.soLan() + 1));
                }
            }
        }
        return new ArrayList<>(tong.values());
    }

    @Override
    public PhieuThu lapPhieu(PhieuThuMoi moi) {
        if (xungDotKhiLap != null) {
            XungDotException.Loai loai = xungDotKhiLap;
            xungDotKhiLap = null;
            throw new XungDotException(loai, "gia lap");
        }
        for (PhieuThuMoi.Dong d : moi.dong()) {
            if (d.phaiNopId() != null) {
                PhaiNop p = kho.phaiNop.get(d.phaiNopId());
                if (kho.daCoPhieuThu.contains(p.id())) {
                    throw new XungDotException(XungDotException.Loai.DA_THU,
                            "");
                }
                if (p.soTien().compareTo(d.soTien()) != 0) {
                    throw new XungDotException(
                            XungDotException.Loai.SO_TIEN_DOI, "");
                }
            } else if (d.soTien().signum() == 0 && tongDongGop(moi.hoId())
                    .stream().anyMatch(t -> t.khoanPhiId() == d.khoanPhiId())) {
                throw new XungDotException(
                        XungDotException.Loai.DA_CO_DONG_GOP, "");
            }
        }
        int nam = moi.thoiDiem().getYear();
        int so = boDem.merge(nam, 1, Integer::sum);
        int id = idPhieu++;
        List<ChiTietPhieuThu> dong = new ArrayList<>();
        for (PhieuThuMoi.Dong d : moi.dong()) {
            YearMonth thang = d.phaiNopId() == null ? null
                    : kho.thangCuaKhoan.get(d.phaiNopId());
            dong.add(new ChiTietPhieuThu(idDong++, d.khoanPhiId(),
                    kho.khoan.get(d.khoanPhiId()).ten(), d.phaiNopId(), thang,
                    d.soTien()));
            if (d.phaiNopId() != null) {
                kho.daCoPhieuThu.add(d.phaiNopId());
            }
        }
        TomTatHo h = ho.get(moi.hoId());
        phieu.put(id, new PhieuThu(id, nam, so, moi.hoId(),
                h.ho().getMaCanHo(), h.tenChuHo(), moi.tongTien(),
                moi.hinhThuc(), moi.nguoiThu(),
                tenTaiKhoan.get(moi.nguoiThu()), moi.thoiDiem(), 0, false,
                null, null, null, List.copyOf(dong)));
        return phieu.get(id);
    }

    @Override
    public void huy(int id, int nguoiHuy, LocalDateTime luc, String lyDo) {
        PhieuThu p = phieu.get(id);
        if (p.daHuy()) {
            throw new XungDotException(XungDotException.Loai.DA_HUY, "");
        }
        phieu.put(id, new PhieuThu(p.id(), p.nam(), p.so(), p.hoId(),
                p.maCanHo(), p.tenChuHo(), p.tongTien(), p.hinhThuc(),
                p.nguoiThuId(), p.tenNguoiThu(), p.thoiDiem(), p.soLanIn(),
                true, tenTaiKhoan.get(nguoiHuy), luc, lyDo, p.chiTiet()));
        p.chiTiet().stream().filter(d -> d.phaiNopId() != null)
                .forEach(d -> kho.daCoPhieuThu.remove(d.phaiNopId()));
    }

    @Override
    public Optional<PhieuThu> timTheoId(int id) {
        return Optional.ofNullable(phieu.get(id));
    }

    @Override
    public List<PhieuThu> theoHo(int hoId) {
        return phieu.values().stream().filter(p -> p.hoId() == hoId)
                .sorted(Comparator.comparing(PhieuThu::id).reversed())
                .toList();
    }

    @Override
    public List<PhieuThu> trongNgay(LocalDate ngay) {
        return phieu.values().stream()
                .filter(p -> p.thoiDiem().toLocalDate().equals(ngay))
                .sorted(Comparator.comparing(PhieuThu::id).reversed())
                .toList();
    }

    @Override
    public void ghiLanIn(int id) {
        PhieuThu p = phieu.get(id);
        phieu.put(id, new PhieuThu(p.id(), p.nam(), p.so(), p.hoId(),
                p.maCanHo(), p.tenChuHo(), p.tongTien(), p.hinhThuc(),
                p.nguoiThuId(), p.tenNguoiThu(), p.thoiDiem(),
                p.soLanIn() + 1, p.daHuy(), p.tenNguoiHuy(), p.thoiDiemHuy(),
                p.lyDoHuy(), p.chiTiet()));
    }
}
