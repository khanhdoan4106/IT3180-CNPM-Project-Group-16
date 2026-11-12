package com.bluemoon.service;

import com.bluemoon.dao.NoDongDao;
import com.bluemoon.dao.NoDongDao.TomTatNo;
import com.bluemoon.model.HoNoDong;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import java.util.List;

/**
 * Hộ nợ đọng (US-017, AC-048, AC-049, giả định 25): lọc hộ nợ từ 3 tháng
 * trở lên hoặc mọi hộ còn nợ, và tóm tắt cho Trang chính. Quản trị viên và
 * Thủ quỹ đều xem được, như màn Công nợ.
 */
public class NoDongService {

    /** Nợ từ chừng này tháng trở lên là nợ đọng (AC-048, giả định 25). */
    public static final int NGUONG_NO_DONG = 3;

    private final NoDongDao dao;

    /**
     * Tạo dịch vụ.
     *
     * @param dao nguồn dữ liệu nợ
     */
    public NoDongService(NoDongDao dao) {
        this.dao = dao;
    }

    /**
     * Danh sách hộ còn nợ.
     *
     * @param nguoi tài khoản đang đăng nhập
     * @param chiNoDong true: chỉ hộ nợ từ {@link #NGUONG_NO_DONG} tháng trở
     *        lên (AC-048); false: mọi hộ còn nợ
     * @return danh sách hộ, nợ nhiều tháng nhất trước
     * @throws NghiepVuException nếu không có quyền
     */
    public List<HoNoDong> hoNo(TaiKhoan nguoi, boolean chiNoDong) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        return dao.hoNo(chiNoDong ? NGUONG_NO_DONG : 1);
    }

    /**
     * Số hộ còn nợ và số hộ nợ đọng, cho thẻ "Hộ còn nợ" ở Trang chính.
     *
     * @param nguoi tài khoản đang đăng nhập
     * @return tóm tắt
     * @throws NghiepVuException nếu không có quyền
     */
    public TomTatNo tomTat(TaiKhoan nguoi) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        return dao.tomTat(NGUONG_NO_DONG);
    }
}
