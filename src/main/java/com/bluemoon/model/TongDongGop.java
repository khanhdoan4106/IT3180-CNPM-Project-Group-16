package com.bluemoon.model;

import java.math.BigDecimal;

/**
 * Tong dong gop cua mot ho cho mot quy, doc tu phieu thu con hieu luc.
 *
 * @param khoanPhiId quy tu nguyen
 * @param tong tong so tien
 * @param soLan so dong chi tiet, ke ca dong 0 d
 */
public record TongDongGop(int khoanPhiId, BigDecimal tong, int soLan) {
}
