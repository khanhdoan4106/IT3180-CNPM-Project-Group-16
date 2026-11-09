package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.config.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * database/seed.sql trên MySQL that: chạy lại nhiều lần không sinh trùng, tên
 * có dấu (so sánh trong Java, không so bằng SQL vì collation _ci không phân
 * biệt dấu), quỹ mẫu luôn đang mở, ba trường hợp tạm trú/tạm vắng.
 */
class SeedIT {

    @BeforeEach
    void chuanBi() throws SQLException {
        CsdlThu.dungLai();
        try (Connection c = Database.getConnection();
                Statement st = c.createStatement()) {
            // Bo tai khoan do CsdlThu them de kiem tai khoan mau cua seed.
            st.executeUpdate("DELETE FROM tai_khoan");
        }
    }

    private static List<String> cot(String sql, String... tham)
            throws SQLException {
        List<String> kq = new ArrayList<>();
        try (Connection c = Database.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < tham.length; i++) {
                ps.setString(i + 1, tham[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kq.add(rs.getString(1));
                }
            }
        }
        return kq;
    }

    private static List<Integer> soDong() {
        List<Integer> kq = new ArrayList<>();
        for (String b : List.of("tai_khoan", "ho_gia_dinh", "nhan_khau",
                "tam_tru_tam_vang", "khoan_phi", "bien_dong_nhan_khau")) {
            kq.add(CsdlThu.dem(b));
        }
        return kq;
    }

    @Test
    void chayLaiBaLanKhongSinhTrungVaDuSoDong() {
        CsdlThu.chaySeed();
        List<Integer> lanMot = soDong();
        assertEquals(List.of(1, 4, 9, 3, 4, 12), lanMot);
        CsdlThu.chaySeed();
        CsdlThu.chaySeed();
        assertEquals(lanMot, soDong());
    }

    @Test
    void tenCoDauSoSanhTrongJava() throws SQLException {
        CsdlThu.chaySeed();
        assertEquals(List.of("Nguyễn Thị Thu Hương"),
                cot("SELECT ho_ten FROM tai_khoan"));
        List<String> ten = cot("SELECT ho_ten FROM nhan_khau");
        for (String can : List.of("Trần Văn Hùng", "Nguyễn Thị Lan",
                "Hoàng Văn Nam", "Hoàng Minh Châu", "Lê Minh Quân",
                "Phạm Đức Anh", "Phạm Thu Hà", "Vũ Văn Toàn",
                "Đỗ Thị Hoa")) {
            assertTrue(ten.contains(can), "Thiếu \"" + can + "\" trong "
                    + ten);
        }
        assertEquals(List.of("Đi học"), cot("SELECT ly_do FROM "
                + "tam_tru_tam_vang WHERE loai = 'TAM_TRU' AND den_ngay < "
                + "CURDATE()"));
    }

    @Test
    void haiQuyMauLuonDangMoVaTenKhongCungNam() throws SQLException {
        CsdlThu.chaySeed();
        List<String> quy = cot("SELECT ten FROM khoan_phi WHERE loai = "
                + "'TU_NGUYEN' AND CURDATE() BETWEEN tu_ngay AND den_ngay "
                + "ORDER BY ten");
        assertEquals(List.of("Quỹ biển đảo", "Quỹ vì người nghèo "
                + LocalDate.now().getYear()), quy);
    }

    @Test
    void baTruongHopTamTruTamVangVaLichSuTamTru() throws SQLException {
        CsdlThu.chaySeed();
        LocalDate homNay = LocalDate.now();
        assertEquals(homNay.plusDays(120), LocalDate.parse(
                cot(DEN_NGAY, "036093004321").get(0)));
        assertTrue(LocalDate.parse(cot(DEN_NGAY, "034199008765").get(0))
                .isBefore(homNay));
        assertEquals(homNay.plusDays(25), LocalDate.parse(
                cot(DEN_NGAY, "001080007890").get(0)));
        // Dong "Chuyen den" cua nguoi tam tru lay ngay bat dau dot tam tru.
        assertEquals(homNay.minusDays(60), LocalDate.parse(cot("SELECT b.ngay "
                + "FROM bien_dong_nhan_khau b JOIN nhan_khau n ON n.id = "
                + "b.nhan_khau_id WHERE n.so_cccd = ? AND b.loai = "
                + "'CHUYEN_DEN'", "036093004321").get(0)));
    }

    private static final String DEN_NGAY = "SELECT t.den_ngay FROM "
            + "tam_tru_tam_vang t JOIN nhan_khau n ON n.id = t.nhan_khau_id "
            + "WHERE n.so_cccd = ?";
}
