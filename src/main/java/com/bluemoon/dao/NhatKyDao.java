package com.bluemoon.dao;

import com.bluemoon.model.NhatKy;
import com.bluemoon.model.ThaoTac;
import java.time.LocalDate;
import java.util.List;

/**
 * Doc nhat ky thao tac (TS-002). Viec ghi nam trong giao dich cua tung
 * thao tac ({@link NhatKyDaoImpl#ghi}); bang chi them, trigger CSDL chan
 * UPDATE va DELETE.
 */
public interface NhatKyDao {

    /**
     * Cac dong ghi trong khoang ngay (tinh ca hai dau), moi nhat truoc.
     *
     * @param tuNgay ngay dau
     * @param denNgay ngay cuoi
     * @param thaoTac chi lay loai nay; null la moi loai
     * @return danh sach nhat ky
     */
    List<NhatKy> theoKhoang(LocalDate tuNgay, LocalDate denNgay,
            ThaoTac thaoTac);
}
