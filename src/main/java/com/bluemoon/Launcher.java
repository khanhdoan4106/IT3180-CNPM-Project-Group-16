package com.bluemoon;

/**
 * Lop khoi dong phu. Can thiet de chay file jar khi JavaFX
 * khong nam tren module path.
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Diem vao cua chuong trinh.
     *
     * @param args doi so dong lenh
     */
    public static void main(String[] args) {
        App.main(args);
    }
}
