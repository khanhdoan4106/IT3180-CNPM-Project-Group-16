package com.bluemoon.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

/**
 * Hien thi va doc ngay theo dinh dang dd/MM/yyyy tren giao dien.
 */
public final class NgayUtil {

    private static final DateTimeFormatter DINH_DANG =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private NgayUtil() {
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
            if (chu == null || chu.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(chu.strip(), DINH_DANG);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
    }
}
