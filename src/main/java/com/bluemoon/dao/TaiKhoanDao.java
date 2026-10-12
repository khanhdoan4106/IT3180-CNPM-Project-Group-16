package com.bluemoon.dao;

import com.bluemoon.model.TaiKhoan;
import java.util.Optional;

/**
 * Truy cap bang tai_khoan.
 */
public interface TaiKhoanDao {

    /**
     * Tim tai khoan theo ten dang nhap.
     *
     * @param tenDangNhap ten dang nhap
     * @return tai khoan, rong neu khong co
     */
    Optional<TaiKhoan> timTheoTenDangNhap(String tenDangNhap);

    /**
     * Cap nhat mat khau da bam.
     *
     * @param id khoa chinh tai khoan
     * @param matKhauBam mat khau moi da bam
     */
    void capNhatMatKhau(int id, String matKhauBam);
}
