package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.TrungKhoaException;
import com.bluemoon.model.GioiTinh;
import com.bluemoon.model.HoGiaDinh;
import com.bluemoon.model.LoaiCanHo;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.QuanHeChuHo;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.TrangThaiNhanKhau;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Them ho cung chu ho va lich su bien dong trong mot giao dich (AC-078):
 * loi giua chung thi khong con dong nao.
 */
class GiaoDichHoGiaDinhIT {

    private final HoGiaDinhDaoImpl dao = new HoGiaDinhDaoImpl();
    private TaiKhoan admin;

    @BeforeEach
    void chuanBi() {
        admin = CsdlThu.dungLai();
    }

    private static HoGiaDinh ho(String ma) {
        return new HoGiaDinh(0, ma, LoaiCanHo.CAN_HO, 6,
                new BigDecimal("75.50"), null, LocalDate.of(2026, 1, 1),
                TrangThaiHo.DANG_O);
    }

    private static NhanKhau chuHo(String cccd) {
        return new NhanKhau(0, 0, "Nguyễn Thị Lan", LocalDate.of(1982, 8, 25),
                GioiTinh.NU, cccd, QuanHeChuHo.CHU_HO,
                TrangThaiNhanKhau.DANG_O, LoaiCuTru.THUONG_TRU, null);
    }

    @Test
    void loiGiuaGiaoDichThiKhongGhiHoVaLichSu() {
        dao.them(ho("A0601"), chuHo("001182004567"), null, admin.getId());
        // Ho thu hai luu duoc, chu ho trung CCCD bi CSDL tu choi.
        TrungKhoaException e = assertThrows(TrungKhoaException.class,
                () -> dao.them(ho("A0602"), chuHo("001182004567"), null,
                        admin.getId()));
        assertEquals("uq_nk_cccd_dang_o", e.getTenRangBuoc());
        assertEquals(1, CsdlThu.dem("ho_gia_dinh"));
        assertEquals(1, CsdlThu.dem("nhan_khau"));
        assertEquals(1, CsdlThu.dem("bien_dong_nhan_khau"));
    }
}
