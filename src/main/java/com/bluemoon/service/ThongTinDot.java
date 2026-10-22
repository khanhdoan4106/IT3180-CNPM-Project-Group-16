package com.bluemoon.service;

import com.bluemoon.model.LoaiDotCuTru;
import java.time.LocalDate;

/**
 * Du lieu nguoi dung nhap cho mot dot tam tru/tam vang (US-003).
 *
 * @param loai tam tru hoac tam vang, null neu chua chon
 * @param tuNgay ngay bat dau, null neu chua chon
 * @param denNgay ngay ket thuc, null neu chua chon
 * @param lyDo ly do, co the de trong
 */
public record ThongTinDot(LoaiDotCuTru loai, LocalDate tuNgay,
        LocalDate denNgay, String lyDo) {
}
