package com.bluemoon.xuat;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Ghi tệp an toàn (giả định 42): tệp tạm rồi đổi tên, lỗi không để rác. */
class TepExcelTest {

    @TempDir
    Path thuMuc;

    private long soTep() throws IOException {
        try (Stream<Path> ds = Files.list(thuMuc)) {
            return ds.count();
        }
    }

    @Test
    void ghiXongThiCoTepDichVaKhongConTepTam() throws IOException {
        Path dich = thuMuc.resolve("bao-cao.xlsx");
        TepExcel.ghiAnToan(dich, ra -> ra.write(
                "noi dung".getBytes(StandardCharsets.UTF_8)));
        assertEquals("noi dung", Files.readString(dich));
        assertEquals(1, soTep(), "Chỉ còn tệp đích");
    }

    @Test
    void loiGiuaChungKhongDeLaiTepDich() throws IOException {
        Path dich = thuMuc.resolve("bao-cao.xlsx");
        TepExcel.LoiGhiTep e = assertThrows(TepExcel.LoiGhiTep.class,
                () -> TepExcel.ghiAnToan(dich, ra -> {
                    ra.write(new byte[] {1, 2, 3});
                    throw new IOException("đĩa đầy");
                }));
        assertTrue(e.getMessage().startsWith("Không ghi được tệp"));
        assertFalse(Files.exists(dich), "Không có tệp đích dở dang");
        assertEquals(0, soTep(), "Tệp tạm đã xóa");
    }

    @Test
    void loiGiuaChungGiuNguyenTepCu() throws IOException {
        Path dich = thuMuc.resolve("bao-cao.xlsx");
        Files.write(dich, new byte[] {9, 9});
        assertThrows(TepExcel.LoiGhiTep.class,
                () -> TepExcel.ghiAnToan(dich, ra -> {
                    ra.write(1);
                    throw new IOException("hỏng");
                }));
        assertArrayEquals(new byte[] {9, 9}, Files.readAllBytes(dich));
        assertEquals(1, soTep());
    }

    @Test
    void ghiDeTepCu() throws IOException {
        Path dich = thuMuc.resolve("bao-cao.xlsx");
        Files.write(dich, new byte[] {9, 9});
        TepExcel.ghiAnToan(dich, ra -> ra.write(7));
        assertArrayEquals(new byte[] {7}, Files.readAllBytes(dich));
    }

    @Test
    void thuMucKhongTonTaiBaoRo() {
        Path dich = thuMuc.resolve("khong-co").resolve("a.xlsx");
        TepExcel.LoiGhiTep e = assertThrows(TepExcel.LoiGhiTep.class,
                () -> TepExcel.ghiAnToan(dich, ra -> ra.write(1)));
        assertEquals("Không tìm thấy thư mục để lưu tệp.", e.getMessage());
    }

    /** Tệp bị Excel giữ: Windows báo AccessDenied hoặc FileSystemException. */
    @Test
    void tepDangMoBaoDongTep() {
        assertEquals(TepExcel.LOI_DANG_MO, TepExcel.thongBao(
                new AccessDeniedException("a.xlsx")));
        assertEquals(TepExcel.LOI_DANG_MO, TepExcel.thongBao(
                new FileSystemException("a.xlsx", null, "The process cannot"
                        + " access the file because it is being used by"
                        + " another process")));
        assertEquals("Tệp đang được mở trong chương trình khác. Đóng tệp"
                + " rồi thử lại.", TepExcel.LOI_DANG_MO);
        assertEquals("Không tìm thấy thư mục để lưu tệp.",
                TepExcel.thongBao(new NoSuchFileException("x")));
    }
}
