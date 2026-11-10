package com.bluemoon.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DatabaseConfigTest {

    @AfterEach
    void khoiPhuc() {
        Database.datLaiChoTest(null);
    }

    @Test
    void docCauHinhKhongNem() {
        assertNotNull(Database.docCauHinh());
    }

    @Test
    void docNhieuLanChiDocFileMotLan(@TempDir Path thuMuc)
            throws IOException {
        Path file = thuMuc.resolve("db.properties");
        Files.writeString(file, "url=jdbc:mysql://x/y\nuser=u\n");
        Database.datLaiChoTest(file);

        Database.docCauHinh();
        Database.docCauHinh();
        Database.docCauHinh();

        assertEquals(1, Database.soLanDocFileChoTest());
        assertEquals("u", Database.docCauHinh().getProperty("user"));
    }

    @Test
    void suaBanSaoKhongLamDoiCauHinhDaNho(@TempDir Path thuMuc)
            throws IOException {
        Path file = thuMuc.resolve("db.properties");
        Files.writeString(file, "user=u\n");
        Database.datLaiChoTest(file);

        Database.docCauHinh().setProperty("user", "khac");

        assertEquals("u", Database.docCauHinh().getProperty("user"));
    }

    @Test
    void thieuCauHinhVanBaoDungLoi(@TempDir Path thuMuc) {
        assumeTrue(System.getenv("BLUEMOON_DB_URL") == null);
        Database.datLaiChoTest(thuMuc.resolve("khong-co.properties"));

        CauHinhException loi = assertThrows(CauHinhException.class,
                Database::getConnection);
        assertEquals("Thiếu cấu hình CSDL. Sao chép config/db.properties.example"
                + " thành config/db.properties và điền thông tin.",
                loi.getMessage());
        // Thieu cau hinh khong bi nho: goi lai van bao dung loi.
        assertThrows(CauHinhException.class, Database::getConnection);
    }

    @Test
    void fileKhongDocDuocBaoDungLoiVaKhongBiNho(@TempDir Path thuMuc) {
        // Mot thu muc o vi tri file cau hinh -> doc that bai (IOException).
        Database.datLaiChoTest(thuMuc);

        CauHinhException loi = assertThrows(CauHinhException.class,
                Database::docCauHinh);
        assertEquals("Không đọc được " + thuMuc, loi.getMessage());
        assertEquals(2, lanDocSauHaiLanLoi());
    }

    private static int lanDocSauHaiLanLoi() {
        assertThrows(CauHinhException.class, Database::docCauHinh);
        return Database.soLanDocFileChoTest();
    }
}
