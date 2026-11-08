package com.bluemoon.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

/**
 * Hien thi va doc ngay theo dinh dang dd/MM/yyyy tren giao dien.
 */
public final class NgayUtil {

    /**
     * STRICT: ngay khong ton tai (31/02) la sai, khong bi doi ngam thanh
     * ngay cuoi thang nhu kieu SMART mac dinh. STRICT can "uuuu" (nam) thay
     * cho "yyyy" (nam cua ky nguyen).
     */
    private static final DateTimeFormatter DINH_DANG = DateTimeFormatter
            .ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private NgayUtil() {
    }

    /**
     * Thong bao khi o ngay co chu nhung doc khong duoc: sai dang, hoac
     * dung dang nhung la ngay khong co (31/02).
     *
     * @param truong ten o ngay dung nhu nhan tren man hinh, vd "Ngày sinh"
     * @return thong bao loi
     */
    public static String loi(String truong) {
        return truong + " không hợp lệ: nhập ngày có thật theo dạng"
                + " ngày/tháng/năm, ví dụ 08/11/2026";
    }

    /**
     * Thong bao loi cua mot o ngay neu o do co chu nhung doc khong duoc.
     *
     * @param o o chon ngay
     * @param truong ten o nhu nhan tren man hinh
     * @return thong bao loi, null neu o hop le hoac de trong
     */
    public static String loiCuaO(DatePicker o, String truong) {
        return saiDinhDang(o) ? loi(truong) : null;
    }

    /**
     * Loi dau tien trong danh sach (theo thu tu cac o tren form).
     *
     * @param cacLoi ket qua {@link #loiCuaO} cua tung o
     * @return loi dau tien khac null, hoac null neu moi o deu hop le
     */
    public static String loiDauTien(String... cacLoi) {
        for (String l : cacLoi) {
            if (l != null) {
                return l;
            }
        }
        return null;
    }

    /**
     * Doc chuoi ngay dd/MM/yyyy.
     *
     * @param chu chuoi nguoi dung go, co the null
     * @return ngay, hoac null neu de trong, sai dinh dang hay khong ton tai
     */
    public static LocalDate docChuoi(String chu) {
        if (chu == null || chu.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(chu.strip(), DINH_DANG);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Dinh dang ngay de hien thi.
     *
     * @param ngay ngay, co the null
     * @return chuoi dd/MM/yyyy, rong neu null
     */
    public static String hien(LocalDate ngay) {
        return ngay == null ? "" : ngay.format(DINH_DANG);
    }

    /**
     * Cho DatePicker dung dinh dang dd/MM/yyyy.
     *
     * @param picker o chon ngay
     */
    public static void ganDinhDang(DatePicker picker) {
        picker.setConverter(new BoChuyenNgay());
        picker.setPromptText("dd/mm/yyyy");
    }

    /**
     * Doc ngay tren DatePicker, ke ca khi nguoi dung go tay ma chua nhan
     * Enter. Tra ve null neu de trong hoac sai dinh dang.
     *
     * @param picker o chon ngay
     * @return ngay da chon, hoac null
     */
    public static LocalDate doc(DatePicker picker) {
        String chu = picker.getEditor().getText();
        if (chu == null || chu.isBlank()) {
            return null;
        }
        LocalDate ngay = picker.getConverter().fromString(chu.strip());
        picker.setValue(ngay);
        return ngay;
    }

    /**
     * Kiem tra o ngay co chu nhung khong doc duoc.
     *
     * @param picker o chon ngay
     * @return true neu sai dinh dang
     */
    public static boolean saiDinhDang(DatePicker picker) {
        String chu = picker.getEditor().getText();
        return chu != null && !chu.isBlank()
                && picker.getConverter().fromString(chu.strip()) == null;
    }

    /** Chuyen doi ngay theo dinh dang dd/MM/yyyy; chuoi sai tra ve null. */
    private static final class BoChuyenNgay
            extends StringConverter<LocalDate> {

        @Override
        public String toString(LocalDate ngay) {
            return hien(ngay);
        }

        @Override
        public LocalDate fromString(String chu) {
            return docChuoi(chu);
        }
    }
}
