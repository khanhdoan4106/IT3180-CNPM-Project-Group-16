package com.bluemoon.service;

import com.bluemoon.dao.BaoCaoThuDao;
import com.bluemoon.model.BaoCaoThu;
import com.bluemoon.model.TaiKhoan;
import com.bluemoon.model.VaiTro;
import com.bluemoon.xuat.BaoCaoThuExcel;
import com.bluemoon.xuat.TepExcel;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Báo cáo thu tiền theo tháng ra Excel (US-018, AC-050, AC-051, giả định
 * 41, 42). Quản trị viên và Thủ quỹ đều xuất được. Xuất tệp không ghi nhật
 * ký (không thuộc giả định 44).
 */
public class BaoCaoThuService {

    private final BaoCaoThuDao dao;
    private final Clock dongHo;

    /**
     * Tạo dịch vụ.
     *
     * @param dao nguồn dữ liệu báo cáo
     * @param dongHo đồng hồ cho tháng mặc định và ngày lập (giờ CSDL khi
     *        chạy thật, {@link com.bluemoon.dao.DongHoCsdl})
     */
    public BaoCaoThuService(BaoCaoThuDao dao, Clock dongHo) {
        this.dao = dao;
        this.dongHo = dongHo;
    }

    /**
     * Tháng hiện tại theo đồng hồ của dịch vụ (tháng mặc định của màn hình).
     *
     * @return tháng hiện tại
     */
    public YearMonth thangHienTai() {
        return YearMonth.now(dongHo);
    }

    /**
     * Dữ liệu báo cáo của một tháng. Gọi trước khi hỏi chỗ lưu tệp: tháng
     * chưa có phiếu thì báo ngay (AC-051).
     *
     * @param nguoi tài khoản đang đăng nhập
     * @param thang tháng báo cáo
     * @return báo cáo, ít nhất một dòng
     * @throws NghiepVuException nếu không có quyền, chưa chọn tháng, hoặc
     *         tháng chưa có phiếu thu nào
     */
    public BaoCaoThu baoCao(TaiKhoan nguoi, YearMonth thang) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        if (thang == null) {
            throw new NghiepVuException("Chưa chọn tháng");
        }
        BaoCaoThu bc = dao.theoThang(thang);
        if (bc.dong().isEmpty()) {
            throw new NghiepVuException("Tháng " + thang.getMonthValue()
                    + "/" + thang.getYear() + " chưa có phiếu thu nào, không"
                    + " có dữ liệu để xuất.");
        }
        return bc;
    }

    /**
     * Ghi báo cáo ra tệp .xlsx (ghi tệp tạm rồi đổi tên), ngày lập là hôm
     * nay theo đồng hồ của dịch vụ.
     *
     * @param nguoi tài khoản đang đăng nhập
     * @param bc báo cáo từ {@link #baoCao}
     * @param dich tệp đích
     * @throws NghiepVuException nếu không có quyền, báo cáo rỗng hoặc không
     *         ghi được tệp
     */
    public void xuat(TaiKhoan nguoi, BaoCaoThu bc, Path dich) {
        PhanQuyen.yeuCau(nguoi, VaiTro.QUAN_TRI, VaiTro.THU_QUY);
        if (bc == null || bc.dong().isEmpty()) {
            throw new NghiepVuException("Không có dữ liệu để xuất.");
        }
        LocalDate homNay = LocalDate.now(dongHo);
        try {
            TepExcel.ghiAnToan(dich,
                    ra -> BaoCaoThuExcel.ghi(bc, homNay, ra));
        } catch (TepExcel.LoiGhiTep e) {
            throw new NghiepVuException(e.getMessage());
        }
    }
}
