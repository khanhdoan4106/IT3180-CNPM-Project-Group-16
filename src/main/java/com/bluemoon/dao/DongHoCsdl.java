package com.bluemoon.dao;

import com.bluemoon.config.Database;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Dong ho theo gio may chu MySQL ({@code SELECT NOW()}), khong theo gio may
 * thu ngan (gia dinh 38): chinh lich may thu ngan khong lam doi thoi diem
 * lap phieu, nam cua so bien lai hay dieu kien "huy trong ngay".
 *
 * <p>De khong mo ket noi moi cho moi lan hoi gio, gio CSDL doc mot lan roi
 * cong them thoi gian troi qua do bang {@link System#nanoTime()} (khong bi
 * anh huong khi doi lich may), doc lai sau {@link #CHU_KY_NS}. Mui gio hien
 * thi la {@code Asia/Ho_Chi_Minh}.
 */
public class DongHoCsdl extends Clock {

    /** Mui gio hien thi. */
    public static final ZoneId VN = ZoneId.of("Asia/Ho_Chi_Minh");
    /** Doc lai gio CSDL sau moi 30 giay. */
    static final long CHU_KY_NS = 30_000_000_000L;

    private static final String SQL = "SELECT UNIX_TIMESTAMP(NOW(3))";

    private final ZoneId mui;
    private final Supplier<Instant> docCsdl;
    private final LongSupplier nanoTime;
    private Instant moc;
    private long nanoMoc;

    /** Dong ho doc gio tu CSDL that, mui gio Viet Nam. */
    public DongHoCsdl() {
        this(VN, DongHoCsdl::docTuCsdl, System::nanoTime);
    }

    /**
     * Tao dong ho voi nguon gio va dong ho don dieu chi dinh (cho test).
     *
     * @param mui mui gio hien thi
     * @param docCsdl doc gio hien tai cua CSDL
     * @param nanoTime dong ho don dieu, don vi nano giay
     */
    DongHoCsdl(ZoneId mui, Supplier<Instant> docCsdl, LongSupplier nanoTime) {
        this.mui = mui;
        this.docCsdl = docCsdl;
        this.nanoTime = nanoTime;
    }

    @Override
    public ZoneId getZone() {
        return mui;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new DongHoCsdl(zone, docCsdl, nanoTime);
    }

    @Override
    public synchronized Instant instant() {
        long bayGio = nanoTime.getAsLong();
        if (moc == null || bayGio - nanoMoc >= CHU_KY_NS || bayGio < nanoMoc) {
            moc = docCsdl.get();
            nanoMoc = bayGio;
        }
        return moc.plusNanos(bayGio - nanoMoc);
    }

    private static Instant docTuCsdl() {
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL);
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            BigDecimal giay = rs.getBigDecimal(1);
            long nguyen = giay.longValue();
            int nano = giay.subtract(BigDecimal.valueOf(nguyen))
                    .movePointRight(9).intValue();
            return Instant.ofEpochSecond(nguyen, nano);
        } catch (SQLException e) {
            throw new DaoException("Loi doc gio may chu CSDL", e);
        }
    }
}
