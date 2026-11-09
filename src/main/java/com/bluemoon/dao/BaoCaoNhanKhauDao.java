package com.bluemoon.dao;

import com.bluemoon.model.TamVang;
import java.time.LocalDate;
import java.util.List;

/**
 * Du lieu bao cao nhan khau (US-004).
 */
@FunctionalInterface
public interface BaoCaoNhanKhauDao {

    /**
     * Nhan khau dang o, thuoc ho dang o, co dot tam vang bao trum ngay cho
     * truoc; theo ma can ho roi ho ten.
     *
     * @param ngay ngay can xem
     * @return danh sach, rong neu khong co ai tam vang
     */
    List<TamVang> tamVang(LocalDate ngay);
}
