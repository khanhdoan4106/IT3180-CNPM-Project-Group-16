package com.bluemoon.service;

import com.bluemoon.model.DotCuTru;
import com.bluemoon.model.LoaiCuTru;
import com.bluemoon.model.LoaiDotCuTru;
import com.bluemoon.model.NhanKhau;
import com.bluemoon.model.TrangThaiNhanKhau;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Kiem tra du lieu form nhan khau va dot tam tru/tam vang, dung chung cho
 * man hinh nhan khau va phan chu ho tren form them ho.
 */
final class KiemTraNhanKhau {

    /** CCCD gom 12 chu so. */
    private static final Pattern MAU_CCCD = Pattern.compile("\\d{12}");
    /** Luat Can cuoc 2023: tu du 14 tuoi bat buoc co the can cuoc. */
    static final int TUOI_BAT_BUOC_CCCD = 14;
    private static final int TEN_TOI_DA = 100;
    /** Do dai toi da cua noi thuong tru va ly do (VARCHAR(200)). */
    private static final int DIA_CHI_TOI_DA = 200;

    private KiemTraNhanKhau() {
    }

    /**
     * Doc va kiem tra form; loi duoc them vao {@code thieu}/{@code sai}.
     *
     * @param tt du lieu form
     * @param id khoa chinh nhan khau, 0 neu them moi
     * @param hoId id ho, 0 neu ho chua luu
     * @param homNay ngay hien tai
     * @param duoi chuoi them sau ten truong trong thong bao, vd " chu ho"
     * @param thieu danh sach truong con thieu
     * @param sai danh sach loi sai dinh dang
     * @return nhan khau, chi dung duoc khi khong co loi
     */
    static NhanKhau doc(ThongTinNhanKhau tt, int id, int hoId,
            LocalDate homNay, String duoi, List<String> thieu,
            List<String> sai) {
        String ten = chuanHoa(tt.hoTen()).replaceAll("\\s+", " ");
        if (ten.isEmpty()) {
            thieu.add("Họ tên" + duoi);
        } else if (ten.length() > TEN_TOI_DA) {
            sai.add("Họ tên" + duoi + " tối đa " + TEN_TOI_DA + " ký tự");
        }
        LocalDate ngaySinh = tt.ngaySinh();
        if (ngaySinh == null) {
            thieu.add("Ngày sinh" + duoi);
        } else if (ngaySinh.isAfter(homNay)) {
            sai.add("Ngày sinh" + duoi + " không được sau hôm nay");
        }
        if (tt.gioiTinh() == null) {
            thieu.add("Giới tính" + duoi);
        }
        String cccd = chuanHoa(tt.soCccd()).replaceAll("[\\s.]", "");
        if (cccd.isEmpty()) {
            if (ngaySinh != null && !ngaySinh.isAfter(homNay)
                    && !ngaySinh.plusYears(TUOI_BAT_BUOC_CCCD)
                            .isAfter(homNay)) {
                thieu.add("Số CCCD" + duoi + " (bắt buộc từ đủ "
                        + TUOI_BAT_BUOC_CCCD + " tuổi)");
            }
        } else if (!MAU_CCCD.matcher(cccd).matches()) {
            sai.add("Số CCCD" + duoi + " gồm đúng 12 chữ số");
        }
        if (tt.quanHe() == null) {
            thieu.add("Quan hệ với chủ hộ");
        }
        String noiThuongTru = null;
        if (tt.loaiCuTru() == null) {
            thieu.add("Loại cư trú" + duoi);
        } else if (tt.loaiCuTru() == LoaiCuTru.TAM_TRU) {
            noiThuongTru = chuanHoa(tt.noiThuongTru())
                    .replaceAll("\\s+", " ");
            if (noiThuongTru.isEmpty()) {
                thieu.add("Nơi thường trú" + duoi);
            } else if (noiThuongTru.length() > DIA_CHI_TOI_DA) {
                sai.add("Nơi thường trú" + duoi + " tối đa " + DIA_CHI_TOI_DA
                        + " ký tự");
            }
        }
        return new NhanKhau(id, hoId, ten, ngaySinh, tt.gioiTinh(),
                cccd.isEmpty() ? null : cccd, tt.quanHe(),
                TrangThaiNhanKhau.DANG_O, tt.loaiCuTru(), noiThuongTru);
    }

    /**
     * Doc va kiem tra mot dot tam tru/tam vang (AC-010, AC-011); loi duoc
     * them vao {@code thieu}/{@code sai}.
     *
     * @param tt du lieu form
     * @param nhanKhauId id nhan khau, 0 neu nhan khau chua luu
     * @param duoi chuoi them sau ten truong trong thong bao
     * @param thieu danh sach truong con thieu
     * @param sai danh sach loi sai
     * @return dot, chi dung duoc khi khong co loi
     */
    static DotCuTru docDot(ThongTinDot tt, int nhanKhauId, String duoi,
            List<String> thieu, List<String> sai) {
        if (tt.loai() == null) {
            thieu.add("Loại (tạm trú/tạm vắng)");
        }
        if (tt.tuNgay() == null) {
            thieu.add("Ngày bắt đầu" + duoi);
        }
        if (tt.denNgay() == null) {
            thieu.add("Ngày kết thúc" + duoi);
        }
        if (tt.tuNgay() != null && tt.denNgay() != null
                && tt.denNgay().isBefore(tt.tuNgay())) {
            sai.add("Ngày kết thúc" + duoi
                    + " không được trước ngày bắt đầu");
        }
        String lyDo = chuanHoa(tt.lyDo()).replaceAll("\\s+", " ");
        if (lyDo.length() > DIA_CHI_TOI_DA) {
            sai.add("Lý do tối đa " + DIA_CHI_TOI_DA + " ký tự");
        }
        return new DotCuTru(0, nhanKhauId, tt.loai(), tt.tuNgay(),
                tt.denNgay(), lyDo.isEmpty() ? null : lyDo);
    }

    /**
     * Doc dot tam tru dau tien cua nguoi dang ky tam tru (loai luon la tam
     * tru); khong co dot thi bao thieu ngay.
     *
     * @param tt du lieu dot, co the null
     * @param duoi chuoi them sau ten truong trong thong bao
     * @param thieu danh sach truong con thieu
     * @param sai danh sach loi sai
     * @return dot tam tru, chi dung duoc khi khong co loi
     */
    static DotCuTru docDotTamTruDauTien(ThongTinDot tt, String duoi,
            List<String> thieu, List<String> sai) {
        ThongTinDot dot = tt == null
                ? new ThongTinDot(LoaiDotCuTru.TAM_TRU, null, null, null)
                : new ThongTinDot(LoaiDotCuTru.TAM_TRU, tt.tuNgay(),
                        tt.denNgay(), tt.lyDo());
        return docDot(dot, 0, " tạm trú" + duoi, thieu, sai);
    }

    private static String chuanHoa(String s) {
        return s == null ? "" : s.strip();
    }
}
