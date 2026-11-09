package com.bluemoon.service;

import com.bluemoon.dao.BaoCaoNhanKhauDao;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.TamVang;
import com.bluemoon.model.VaiTro;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Bao cao nhan khau cho cong an phuong (US-004). Chi Quan tri vien.
 */
public class BaoCaoNhanKhauService {

    private static final DateTimeFormatter NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    private final BaoCaoNhanKhauDao dao;

    /**
     * Tao dich vu.
     *
     * @param dao nguon du lieu bao cao
     */
    public BaoCaoNhanKhauService(BaoCaoNhanKhauDao dao) {
        this.dao = dao;
    }

    /**
     * Danh sach nhan khau tam vang vao mot ngay (AC-012, AC-013).
     *
     * @param nguoi tai khoan dang dang nhap
     * @param ngay ngay can xem
     * @return danh sach, it nhat mot nguoi
     * @throws NghiepVuException neu chua chon ngay hoac khong co ai tam vang
     */
    public List<TamVang> tamVang(TaiKhoan nguoi, LocalDate ngay) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI);
        if (ngay == null) {
            throw new NghiepVuException("Chưa chọn ngày");
        }
        List<TamVang> ds = dao.tamVang(ngay);
        if (ds.isEmpty()) {
            throw new NghiepVuException("Không có nhân khẩu tạm vắng vào ngày "
                    + ngay.format(NGAY));
        }
        return ds;
    }
}
