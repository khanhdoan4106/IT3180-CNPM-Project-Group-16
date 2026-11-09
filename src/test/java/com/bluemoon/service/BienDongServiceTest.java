package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.BienDongDao;
import com.bluemoon.model.BienDong;
import com.bluemoon.model.LoaiBienDong;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Lich su bien dong (US-027): chi Quan tri vien xem, AC-060, AC-062. */
class BienDongServiceTest {

    private static final BienDong MAU = new BienDong(1, 7, "A0601", 3,
            "Nguyễn Thị Lan", LoaiBienDong.CHUYEN_DEN,
            LocalDate.of(2026, 1, 1), "Dữ liệu mẫu", "Quản trị viên",
            LocalDateTime.of(2026, 1, 1, 8, 0));

    private int soLanGoiDao;
    private int soDongYeuCau;
    private BienDongService service;
    private TaiKhoan admin;
    private TaiKhoan thuQuy;

    @BeforeEach
    void chuanBi() {
        soLanGoiDao = 0;
        service = new BienDongService(new BienDongDao() {
            @Override
            public List<BienDong> theoHo(int hoId) {
                soLanGoiDao++;
                return hoId == 7 ? List.of(MAU) : List.of();
            }

            @Override
            public List<BienDong> ganDay(int soDong) {
                soLanGoiDao++;
                soDongYeuCau = soDong;
                return List.of(MAU);
            }
        });
        admin = new TaiKhoan(1, "admin", "x", "Quản trị viên",
                VaiTro.QUAN_TRI, true);
        thuQuy = new TaiKhoan(2, "thuquy", "x", "Thủ quỹ", VaiTro.THU_QUY,
                true);
    }

    @Test
    void quanTriVienXemLichSuCuaHoVaBienDongGanDay() {
        assertEquals(List.of(MAU), service.theoHo(admin, 7));
        assertEquals(List.of(), service.theoHo(admin, 8));
        assertEquals(List.of(MAU), service.ganDay(admin));
        assertEquals(BienDongService.GAN_DAY, soDongYeuCau);
    }

    @Test
    void thuQuyBiChanVaKhongChamTangDuLieu() {
        NghiepVuException e1 = assertThrows(NghiepVuException.class,
                () -> service.theoHo(thuQuy, 7));
        NghiepVuException e2 = assertThrows(NghiepVuException.class,
                () -> service.ganDay(thuQuy));
        assertEquals(PhanQuyen.LOI_KHONG_CO_QUYEN, e1.getMessage());
        assertEquals(PhanQuyen.LOI_KHONG_CO_QUYEN, e2.getMessage());
        assertEquals(0, soLanGoiDao);
    }

    @Test
    void chuaDangNhapBiChan() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.ganDay(null));
        assertEquals(PhanQuyen.LOI_CHUA_DANG_NHAP, e.getMessage());
    }
}
