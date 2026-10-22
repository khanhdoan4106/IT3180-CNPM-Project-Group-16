package com.bluemoon.service;

import com.bluemoon.model.NhanKhau;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Kiem tra du lieu form nhan khau, dung chung cho man hinh nhan khau va
 * phan chu ho tren form them ho.
 */
final class KiemTraNhanKhau {

    /** CCCD gom 12 chu so. */
    private static final Pattern MAU_CCCD = Pattern.compile("\\d{12}");
    private static final int TEN_TOI_DA = 100;

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
            thieu.add("Ho ten" + duoi);
        } else if (ten.length() > TEN_TOI_DA) {
            sai.add("Ho ten" + duoi + " toi da " + TEN_TOI_DA + " ky tu");
        }
        LocalDate ngaySinh = tt.ngaySinh();
        if (ngaySinh == null) {
            thieu.add("Ngay sinh" + duoi);
        } else if (ngaySinh.isAfter(homNay)) {
            sai.add("Ngay sinh" + duoi + " khong duoc sau hom nay");
        }
        if (tt.gioiTinh() == null) {
            thieu.add("Gioi tinh" + duoi);
        }
        String cccd = chuanHoa(tt.soCccd()).replaceAll("[\\s.]", "");
        if (!cccd.isEmpty() && !MAU_CCCD.matcher(cccd).matches()) {
            sai.add("So CCCD" + duoi + " gom dung 12 chu so");
        }
        if (tt.quanHe() == null) {
            thieu.add("Quan he voi chu ho");
        }
        return new NhanKhau(id, hoId, ten, ngaySinh, tt.gioiTinh(),
                cccd.isEmpty() ? null : cccd, tt.quanHe());
    }

    private static String chuanHoa(String s) {
        return s == null ? "" : s.strip();
    }
}
