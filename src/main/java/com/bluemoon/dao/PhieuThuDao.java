package com.bluemoon.dao;

import com.bluemoon.model.KhoanNo;
import com.bluemoon.model.PhieuThu;
import com.bluemoon.model.PhieuThuMoi;
import com.bluemoon.model.TomTatHo;
import com.bluemoon.model.TongDongGop;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Truy cap phieu thu va du lieu tra cuu de thu phi (US-010 den US-015,
 * US-007).
 */
public interface PhieuThuDao {

    /**
     * Ho co ma can ho cho truoc de thu phi: ho dang o, va ho da chuyen di
     * con khoan chua dong (gia dinh 21). Ho dang o dung dau.
     *
     * @param maCanHo ma can ho, khong phan biet hoa thuong
     * @return cac ho tim thay, rong neu khong co
     */
    List<TomTatHo> timHoTheoMa(String maCanHo);

    /**
     * Thong tin mot ho kem ten chu ho.
     *
     * @param hoId khoa chinh ho
     * @return ho, rong neu khong co
     */
    Optional<TomTatHo> timHo(int hoId);

    /**
     * Khoan phai nop lon hon 0 d chua co phieu thu con hieu luc cua mot ho,
     * theo thang roi ten khoan.
     *
     * @param hoId khoa chinh ho
     * @return danh sach, rong neu khong no
     */
    List<KhoanNo> khoanChuaDong(int hoId);

    /**
     * Moi khoan chua dong cua cac thang den het thang cho truoc, moi ho
     * (US-015), theo ma can ho, thang, ten khoan.
     *
     * @param thang thang cuoi
     * @return danh sach
     */
    List<KhoanNo> chuaDongDenThang(YearMonth thang);

    /**
     * Tong dong gop tu nguyen cua ho theo tung quy, tren phieu con hieu luc.
     *
     * @param hoId khoa chinh ho
     * @return moi quy da co it nhat mot lan ghi
     */
    List<TongDongGop> tongDongGop(int hoId);

    /**
     * Lap phieu trong mot giao dich: khoa dong ho, kiem tra lai tung khoan
     * phai nop (chua thu, so tien khong doi) va quy tac 0 d, cap so bien lai
     * tu bo dem cua nam, ghi phieu va chi tiet.
     *
     * @param phieu du lieu phieu
     * @return phieu da luu
     * @throws XungDotException neu du lieu vua bi doi o may khac
     */
    PhieuThu lapPhieu(PhieuThuMoi phieu);

    /**
     * Huy phieu trong mot giao dich: danh dau phieu, ghi nguoi huy, ly do,
     * va cho cac dong het hieu luc de khoan phai nop tro ve chua dong.
     *
     * @param id khoa chinh phieu
     * @param nguoiHuy tai khoan huy
     * @param luc thoi diem huy
     * @param lyDo ly do
     * @throws XungDotException neu khong co phieu hoac phieu da huy
     */
    void huy(int id, int nguoiHuy, LocalDateTime luc, String lyDo);

    /**
     * Tim phieu kem chi tiet.
     *
     * @param id khoa chinh
     * @return phieu, rong neu khong co
     */
    Optional<PhieuThu> timTheoId(int id);

    /**
     * Moi phieu cua mot ho, ke ca phieu da huy, moi nhat truoc.
     *
     * @param hoId khoa chinh ho
     * @return danh sach phieu
     */
    List<PhieuThu> theoHo(int hoId);

    /**
     * Moi phieu lap trong mot ngay, ke ca phieu da huy, moi nhat truoc.
     *
     * @param ngay ngay lap
     * @return danh sach phieu
     */
    List<PhieuThu> trongNgay(LocalDate ngay);

    /**
     * Tang so lan in bien lai them 1 (AC-040).
     *
     * @param id khoa chinh phieu
     */
    void ghiLanIn(int id);
}
