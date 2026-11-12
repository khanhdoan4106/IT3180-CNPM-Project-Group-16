package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.NhatKyDao;
import com.bluemoon.model.NhatKy;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.ThaoTac;
import com.bluemoon.model.VaiTro;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Xem nhat ky thao tac (TS-002, AC-069): phan quyen va kiem tra ngay. */
class NhatKyServiceTest {

    private static final TaiKhoan QT = new TaiKhoan(1, "admin", "x",
            "Quản trị", VaiTro.QUAN_TRI, true);
    private static final TaiKhoan THU_QUY = new TaiKhoan(2, "thuquy", "x",
            "Thủ quỹ", VaiTro.THU_QUY, true);
    private static final LocalDate NGAY_1 = LocalDate.of(2026, 11, 1);
    private static final LocalDate NGAY_7 = LocalDate.of(2026, 11, 7);

    /** DAO gia: ghi lai tham so cua lan goi cuoi. */
    private static class DaoGia implements NhatKyDao {
        private int soLanGoi;
        private LocalDate tu;
        private LocalDate den;
        private ThaoTac loai;

        @Override
        public List<NhatKy> theoKhoang(LocalDate tuNgay, LocalDate denNgay,
                ThaoTac thaoTac) {
            soLanGoi++;
            tu = tuNgay;
            den = denNgay;
            loai = thaoTac;
            return List.of(new NhatKy(1, LocalDateTime.of(2026, 11, 3, 9, 0),
                    1, "Quản trị", "admin", ThaoTac.THEM_HO, "Hộ A1203",
                    null));
        }
    }

    private DaoGia dao;
    private NhatKyService service;

    @BeforeEach
    void chuanBi() {
        dao = new DaoGia();
        service = new NhatKyService(dao);
    }

    @Test
    void thuQuyBiTuChoi() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.theoKhoang(THU_QUY, NGAY_1, NGAY_7, null));
        assertEquals(PhanQuyen.LOI_KHONG_CO_QUYEN, e.getMessage());
        assertEquals(0, dao.soLanGoi);
    }

    @Test
    void chuaDangNhapHoacBiKhoaBiTuChoi() {
        assertThrows(NghiepVuException.class,
                () -> service.theoKhoang(null, NGAY_1, NGAY_7, null));
        assertThrows(NghiepVuException.class, () -> service.theoKhoang(
                QT.voiHoatDong(false), NGAY_1, NGAY_7, null));
        assertEquals(0, dao.soLanGoi);
    }

    @Test
    void locTheoKhoangNgayVaLoaiThaoTac() {
        List<NhatKy> ds = service.theoKhoang(QT, NGAY_1, NGAY_7,
                ThaoTac.SUA_HO);
        assertEquals(1, ds.size());
        assertEquals(NGAY_1, dao.tu);
        assertEquals(NGAY_7, dao.den);
        assertEquals(ThaoTac.SUA_HO, dao.loai);

        service.theoKhoang(QT, NGAY_7, NGAY_7, null);
        assertEquals(NGAY_7, dao.tu);
        assertNull(dao.loai);
    }

    @Test
    void tuNgaySauDenNgayBaoLoi() {
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.theoKhoang(QT, NGAY_7, NGAY_1, null));
        assertEquals("Từ ngày không được sau đến ngày", e.getMessage());
        assertEquals(0, dao.soLanGoi);
    }

    @Test
    void thieuNgayBaoLoi() {
        assertThrows(NghiepVuException.class,
                () -> service.theoKhoang(QT, null, NGAY_7, null));
        NghiepVuException e = assertThrows(NghiepVuException.class,
                () -> service.theoKhoang(QT, NGAY_1, null, null));
        assertEquals("Vui lòng chọn từ ngày và đến ngày", e.getMessage());
        assertEquals(0, dao.soLanGoi);
    }

    @Test
    void macDinhLaBayNgayGanNhatTinhCaHomNay() {
        assertEquals(LocalDate.of(2026, 11, 6),
                NhatKyService.tuNgayMacDinh(LocalDate.of(2026, 11, 12)));
    }
}
