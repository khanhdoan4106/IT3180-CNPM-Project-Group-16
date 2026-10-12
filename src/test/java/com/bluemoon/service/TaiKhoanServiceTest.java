package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bluemoon.dao.TaiKhoanDao;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.util.MatKhauUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaiKhoanServiceTest {

    /** DAO gia lap trong bo nho, khong can MySQL. */
    private static class DaoGia implements TaiKhoanDao {
        private final Map<String, TaiKhoan> theoTen = new HashMap<>();
        private String matKhauMoiNhat;

        @Override
        public Optional<TaiKhoan> timTheoTenDangNhap(String ten) {
            return Optional.ofNullable(theoTen.get(ten));
        }

        @Override
        public void capNhatMatKhau(int id, String matKhauBam) {
            matKhauMoiNhat = matKhauBam;
        }
    }

    private DaoGia dao;
    private TaiKhoanService service;
    private TaiKhoan admin;

    @BeforeEach
    void chuanBi() {
        dao = new DaoGia();
        admin = new TaiKhoan(1, "admin", MatKhauUtil.bam("matkhau123"),
                "Quan tri", VaiTro.QUAN_TRI, true);
        dao.theoTen.put("admin", admin);
        dao.theoTen.put("khoa", new TaiKhoan(2, "khoa",
                MatKhauUtil.bam("matkhau123"), "Bi khoa",
                VaiTro.THU_QUY, false));
        service = new TaiKhoanService(dao);
    }

    @Test
    void dangNhapDungThanhCong() {
        assertEquals(1, service.dangNhap("admin", "matkhau123").getId());
    }

    @Test
    void saiMatKhauBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("admin", "sai"));
    }

    @Test
    void khongCoTaiKhoanThongBaoGiongSaiMatKhau() {
        NghiepVuException a = assertThrows(NghiepVuException.class,
                () -> service.dangNhap("khongco", "x"));
        NghiepVuException b = assertThrows(NghiepVuException.class,
                () -> service.dangNhap("admin", "sai"));
        assertEquals(a.getMessage(), b.getMessage());
    }

    @Test
    void taiKhoanBiKhoaKhongDangNhapDuoc() {
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("khoa", "matkhau123"));
    }

    @Test
    void thieuThongTinBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("", "x"));
        assertThrows(NghiepVuException.class,
                () -> service.dangNhap("admin", ""));
    }

    @Test
    void doiMatKhauThanhCongLuuBam() {
        service.doiMatKhau(admin, "matkhau123", "matkhaumoi456");
        assertTrue(MatKhauUtil.kiemTra("matkhaumoi456",
                dao.matKhauMoiNhat));
    }

    @Test
    void doiMatKhauSaiMatKhauCu() {
        assertThrows(NghiepVuException.class,
                () -> service.doiMatKhau(admin, "sai", "matkhaumoi456"));
    }

    @Test
    void matKhauMoiQuaNganBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.doiMatKhau(admin, "matkhau123", "123"));
    }
}
