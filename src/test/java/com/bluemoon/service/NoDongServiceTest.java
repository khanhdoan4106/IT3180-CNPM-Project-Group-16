package com.bluemoon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.bluemoon.dao.NoDongDao;
import com.bluemoon.model.HoNoDong;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TrangThaiHo;
import com.bluemoon.model.VaiTro;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Hộ nợ đọng (US-017, AC-048): phân quyền, ngưỡng 3 tháng, tóm tắt. */
class NoDongServiceTest {

    private static final TaiKhoan QT = new TaiKhoan(1, "admin", "x",
            "Quản trị", VaiTro.QUAN_TRI, true);
    private static final TaiKhoan THU_QUY = new TaiKhoan(2, "thuquy", "x",
            "Thủ quỹ", VaiTro.THU_QUY, true);

    private static HoNoDong ho(String ma, int soThang) {
        return new HoNoDong(soThang, ma, "Chủ hộ " + ma, TrangThaiHo.DANG_O,
                soThang, YearMonth.of(2026, 12 - soThang),
                BigDecimal.valueOf(100_000L * soThang));
    }

    /** DAO giả: lọc theo số tháng tối thiểu như truy vấn HAVING thật. */
    private static class DaoGia implements NoDongDao {
        private final List<HoNoDong> ds = new ArrayList<>(List.of(
                ho("A0101", 1), ho("A0102", 2), ho("A0103", 3),
                ho("A0104", 5)));
        private final List<Integer> nguongDaGoi = new ArrayList<>();

        @Override
        public List<HoNoDong> hoNo(int soThangToiThieu) {
            nguongDaGoi.add(soThangToiThieu);
            return ds.stream().filter(h -> h.soThangNo() >= soThangToiThieu)
                    .toList();
        }

        @Override
        public TomTatNo tomTat(int nguong) {
            nguongDaGoi.add(nguong);
            return new TomTatNo(ds.size(), (int) ds.stream()
                    .filter(h -> h.soThangNo() >= nguong).count());
        }
    }

    private DaoGia dao;
    private NoDongService service;

    @BeforeEach
    void chuanBi() {
        dao = new DaoGia();
        service = new NoDongService(dao);
    }

    /** AC-048: 2 tháng không vào, 3 tháng trở lên thì vào. */
    @Test
    void nguongBaThang() {
        assertEquals(3, NoDongService.NGUONG_NO_DONG);
        List<String> ma = service.hoNo(QT, true).stream()
                .map(HoNoDong::maCanHo).toList();
        assertEquals(List.of("A0103", "A0104"), ma);
        assertEquals(List.of(3), dao.nguongDaGoi);
    }

    @Test
    void tatCaHoConNoTuMotThang() {
        assertEquals(4, service.hoNo(THU_QUY, false).size());
        assertEquals(List.of(1), dao.nguongDaGoi);
    }

    @Test
    void tomTatDemHoConNoVaHoNoDong() {
        NoDongDao.TomTatNo t = service.tomTat(THU_QUY);
        assertEquals(4, t.soHoConNo());
        assertEquals(2, t.soHoNoDong());
        assertEquals(List.of(3), dao.nguongDaGoi);
    }

    @Test
    void caHaiVaiTroDeuXemDuocChuaDangNhapBiTuChoi() {
        service.hoNo(QT, true);
        service.hoNo(THU_QUY, true);
        for (Runnable r : List.<Runnable>of(
                () -> service.hoNo(null, true),
                () -> service.hoNo(QT.voiHoatDong(false), false),
                () -> service.tomTat(null))) {
            NghiepVuException e = assertThrows(NghiepVuException.class,
                    r::run);
            assertEquals(PhanQuyen.LOI_CHUA_DANG_NHAP, e.getMessage());
        }
        assertEquals(List.of(3, 3), dao.nguongDaGoi);
    }
}
