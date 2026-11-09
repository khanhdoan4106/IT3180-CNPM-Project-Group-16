package com.bluemoon.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.config.Database;
import com.bluemoon.dao.HoGiaDinhDaoImpl;
import com.bluemoon.dao.NhanKhauDaoImpl;
import com.bluemoon.dao.XungDotException;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
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

    @Test
    void doiChuHoTuChoiNguoiVuaChuyenDiOMayKhac() throws Exception {
        int hoId = dao.them(ho("A0601"), chuHo("001182004567"), null,
                admin.getId());
        NhanKhauDaoImpl nkDao = new NhanKhauDaoImpl();
        int cuId = nkDao.theoHo(hoId).get(0).getId();
        int moiId = nkDao.them(new NhanKhau(0, hoId, "Hoàng Văn Nam",
                LocalDate.of(1980, 1, 30), GioiTinh.NAM, "001080007890",
                QuanHeChuHo.VO_CHONG, TrangThaiNhanKhau.DANG_O,
                LoaiCuTru.THUONG_TRU, null), admin.getId());
        // May khac vua danh dau nguoi nay chuyen di.
        try (Connection c = Database.getConnection();
                PreparedStatement ps = c.prepareStatement("UPDATE nhan_khau "
                        + "SET trang_thai = 'DA_CHUYEN_DI' WHERE id = ?")) {
            ps.setInt(1, moiId);
            ps.executeUpdate();
        }
        XungDotException e = assertThrows(XungDotException.class,
                () -> nkDao.doiChuHo(cuId, moiId, QuanHeChuHo.KHAC, false,
                        admin.getId()));
        assertEquals(XungDotException.Loai.NHAN_KHAU_DOI, e.getLoai());
        // Hoan tac: chu ho cu van la chu ho.
        assertEquals(QuanHeChuHo.CHU_HO,
                nkDao.timTheoId(cuId).orElseThrow().getQuanHe());
    }
}
