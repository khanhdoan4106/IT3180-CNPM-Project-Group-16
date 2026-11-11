package com.bluemoon;

import com.bluemoon.dao.DongHoCsdl;
import java.util.TimeZone;

/**
 * Lop khoi dong phu. Can thiet de chay file jar khi JavaFX
 * khong nam tren module path.
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Đặt múi giờ mặc định của ứng dụng là Việt Nam để "hôm nay" trên màn
     * hình (ô chọn ngày, ngày ghi biến động) không phụ thuộc cài đặt của
     * máy. Giờ ghi và đọc CSDL không dựa vào múi giờ này (DAO dùng
     * {@code LocalDateTime} trực tiếp).
     */
    static void datMuiGioViet() {
        TimeZone.setDefault(TimeZone.getTimeZone(DongHoCsdl.VN));
    }

    /**
     * Diem vao cua chuong trinh.
     *
     * @param args doi so dong lenh
     */
    public static void main(String[] args) {
        datMuiGioViet();
        App.main(args);
    }
}
