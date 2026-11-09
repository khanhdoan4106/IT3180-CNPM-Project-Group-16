package com.bluemoon.service;

import com.bluemoon.dao.CongNoDao;
import com.bluemoon.dao.PhieuThuDao;
import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThongBaoPhi;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.VaiTro;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In thong bao thu phi hang thang cho tung ho (US-015). Quan tri vien va
 * Thu quy deu dung duoc.
 */
public class ThongBaoPhiService {

    private final PhieuThuDao dao;
    private final CongNoDao congNoDao;

    /**
     * Tao dich vu.
     *
     * @param dao nguon du lieu khoan chua dong
     * @param congNoDao de biet thang da chot chua
     */
    public ThongBaoPhiService(PhieuThuDao dao, CongNoDao congNoDao) {
        this.dao = dao;
        this.congNoDao = congNoDao;
    }

    /**
     * Thong bao cua mot ho (AC-044, AC-045).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param thang thang thong bao
     * @param hoId khoa chinh ho
     * @return thong bao
     * @throws NghiepVuException neu thang chua chot hoac ho khong co khoan
     *         can dong
     */
    public ThongBaoPhi motHo(TaiKhoan nguoi, YearMonth thang, int hoId) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        canDaChot(thang);
        TomTatHo ho = dao.timHo(hoId).orElseThrow(
                () -> new NghiepVuException("Không tìm thấy hộ"));
        List<KhoanNo> no = dao.khoanChuaDong(hoId).stream()
                .filter(k -> !k.thang().isAfter(thang)).toList();
        if (no.isEmpty()) {
            throw new NghiepVuException("Hộ " + ho.ho().getMaCanHo()
                    + " không có khoản cần đóng đến " + tenThang(thang));
        }
        return tao(ho, thang, no);
    }

    /**
     * Thong bao cho moi ho dang o con khoan can dong den het thang, de in
     * mot lan phat cho ca toa nha.
     *
     * @param nguoi tai khoan dang dang nhap
     * @param thang thang thong bao
     * @return cac thong bao, theo ma can ho
     * @throws NghiepVuException neu thang chua chot hoac khong ho nao con
     *         khoan can dong
     */
    public List<ThongBaoPhi> tatCa(TaiKhoan nguoi, YearMonth thang) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        canDaChot(thang);
        Map<Integer, List<KhoanNo>> theoHo = new LinkedHashMap<>();
        for (KhoanNo k : dao.chuaDongDenThang(thang)) {
            theoHo.computeIfAbsent(k.hoId(), id -> new ArrayList<>()).add(k);
        }
        List<ThongBaoPhi> kq = new ArrayList<>();
        for (Map.Entry<Integer, List<KhoanNo>> e : theoHo.entrySet()) {
            TomTatHo ho = dao.timHo(e.getKey()).orElse(null);
            // Ho da chuyen di khong con o toa nha de phat thong bao.
            if (ho != null && ho.ho().getTrangThai() == TrangThaiHo.DANG_O) {
                kq.add(tao(ho, thang, e.getValue()));
            }
        }
        if (kq.isEmpty()) {
            throw new NghiepVuException("Không hộ nào còn khoản cần đóng đến "
                    + tenThang(thang));
        }
        return kq;
    }

    private void canDaChot(YearMonth thang) {
        if (thang == null) {
            throw new NghiepVuException("Chưa chọn tháng");
        }
        if (!congNoDao.daChot(thang)) {
            throw new NghiepVuException(tenThang(thang)
                    + " chưa chốt công nợ, chưa in thông báo được");
        }
    }

    private static ThongBaoPhi tao(TomTatHo ho, YearMonth thang,
            List<KhoanNo> no) {
        return new ThongBaoPhi(ho, thang,
                no.stream().filter(k -> k.thang().equals(thang)).toList(),
                no.stream().filter(k -> k.thang().isBefore(thang)).toList());
    }

    private static String tenThang(YearMonth t) {
        return "tháng " + t.getMonthValue() + "/" + t.getYear();
    }
}
