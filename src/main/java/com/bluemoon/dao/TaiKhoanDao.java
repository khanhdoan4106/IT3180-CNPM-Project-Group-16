package com.bluemoon.dao;

import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import java.util.List;
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
     * Cap nhat mat khau da bam khi nguoi dung tu doi mat khau. Khong ghi
     * nhat ky (gia dinh 44).
     *
     * @param id khoa chinh tai khoan
     * @param matKhauBam mat khau moi da bam
     */
    void capNhatMatKhau(int id, String matKhauBam);

    /**
     * Them tai khoan moi va ghi nhat ky (ho ten, vai tro; khong ghi mat
     * khau) trong cung giao dich.
     *
     * @param tk tai khoan can them (id bo qua), mat khau da bam
     * @param nguoiId id Quan tri vien tao, ghi vao nhat ky
     * @return id cua tai khoan vua them
     * @throws TrungKhoaException neu ten dang nhap da ton tai
     */
    int them(TaiKhoan tk, int nguoiId);

    /**
     * Tim tai khoan theo khoa chinh.
     *
     * @param id khoa chinh
     * @return tai khoan, rong neu khong co
     */
    Optional<TaiKhoan> timTheoId(int id);

    /**
     * Tat ca tai khoan, theo ten dang nhap.
     *
     * @return danh sach tai khoan
     */
    List<TaiKhoan> danhSach();

    /**
     * Cap nhat thong tin ca nhan (US-023).
     *
     * @param id khoa chinh
     * @param hoTen ho ten moi
     * @param soDienThoai so dien thoai moi, co the null
     */
    void capNhatThongTin(int id, String hoTen, String soDienThoai);

    /**
     * Doi vai tro (US-022) va ghi nhat ky vai tro cu, moi.
     *
     * @param id khoa chinh
     * @param vaiTro vai tro moi
     * @param nguoiId id Quan tri vien thao tac, ghi vao nhat ky
     */
    void capNhatVaiTro(int id, VaiTro vaiTro, int nguoiId);

    /**
     * Khoa hoac mo khoa tai khoan; khong xoa de giu lich su thu. Ghi nhat
     * ky trong cung giao dich.
     *
     * @param id khoa chinh
     * @param hoatDong false de khoa
     * @param nguoiId id Quan tri vien thao tac, ghi vao nhat ky
     */
    void capNhatHoatDong(int id, boolean hoatDong, int nguoiId);

    /**
     * Quan tri vien dat lai mat khau cho tai khoan khac (RAW-035). Ghi nhat
     * ky viec dat lai, khong ghi mat khau hay ma bam.
     *
     * @param id khoa chinh tai khoan duoc dat lai
     * @param matKhauBam mat khau tam da bam
     * @param nguoiId id Quan tri vien thao tac, ghi vao nhat ky
     */
    void datLaiMatKhau(int id, String matKhauBam, int nguoiId);
}
