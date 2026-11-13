package com.bluemoon.xuat;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Quy ước chung khi xuất tệp .xlsx (giả định 42): tiền là ô số định dạng
 * {@code #,##0}, ngày là ô ngày định dạng {@code dd/mm/yyyy}, dòng tiêu đề
 * đậm và cố định, độ rộng cột theo nội dung; ghi ra tệp tạm cùng thư mục
 * rồi đổi tên để lỗi giữa chừng không để lại tệp hỏng.
 */
public final class TepExcel {

    /** Định dạng ô tiền. */
    public static final String DINH_DANG_TIEN = "#,##0";
    /** Định dạng ô ngày. */
    public static final String DINH_DANG_NGAY = "dd/mm/yyyy";
    /** Báo khi tệp đích đang bị chương trình khác (vd Excel) giữ. */
    public static final String LOI_DANG_MO = "Tệp đang được mở trong chương"
            + " trình khác. Đóng tệp rồi thử lại.";

    /** Độ rộng cột tối đa, tính theo số ký tự. */
    static final int RONG_TOI_DA = 60;

    private TepExcel() {
    }

    /** Việc ghi nội dung tệp vào một luồng. */
    @FunctionalInterface
    public interface CongViecGhi {
        /**
         * Ghi nội dung.
         *
         * @param ra luồng ghi
         * @throws IOException lỗi ghi
         */
        void ghi(OutputStream ra) throws IOException;
    }

    /**
     * Ghi tệp an toàn: ghi vào tệp tạm cùng thư mục, xong mới đổi tên thành
     * tệp đích (thay tệp cũ nếu có). Lỗi giữa chừng thì xóa tệp tạm, tệp
     * đích giữ nguyên như trước.
     *
     * @param dich tệp đích
     * @param viec việc ghi nội dung
     * @throws LoiGhiTep nếu không ghi được, kèm thông báo cho người dùng
     */
    public static void ghiAnToan(Path dich, CongViecGhi viec) {
        Path thuMuc = dich.toAbsolutePath().getParent();
        Path tam = null;
        try {
            tam = Files.createTempFile(thuMuc, ".bluemoon-", ".tmp");
            try (OutputStream ra = Files.newOutputStream(tam)) {
                viec.ghi(ra);
            }
            try {
                Files.move(tam, dich, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tam, dich, StandardCopyOption.REPLACE_EXISTING);
            }
            tam = null;
        } catch (IOException e) {
            throw new LoiGhiTep(thongBao(e), e);
        } finally {
            if (tam != null) {
                try {
                    Files.deleteIfExists(tam);
                } catch (IOException boQua) {
                    // Khong xoa duoc tep tam thi thoi, tep dich van nguyen.
                }
            }
        }
    }

    /**
     * Thông báo cho người dùng theo lỗi ghi tệp. Tệp bị chương trình khác
     * giữ (Windows báo AccessDenied hoặc "being used by another process")
     * thì nhắc đóng tệp.
     *
     * @param e lỗi
     * @return thông báo tiếng Việt
     */
    static String thongBao(IOException e) {
        if (e instanceof NoSuchFileException) {
            return "Không tìm thấy thư mục để lưu tệp.";
        }
        if (e instanceof FileSystemException) {
            return LOI_DANG_MO;
        }
        return "Không ghi được tệp: " + e.getMessage();
    }

    /** Lỗi ghi tệp, thông báo đã sẵn để hiện cho người dùng. */
    public static final class LoiGhiTep extends RuntimeException {
        private static final long serialVersionUID = 1L;

        LoiGhiTep(String thongBao, Throwable nguyenNhan) {
            super(thongBao, nguyenNhan);
        }
    }

    /** Kiểu ô dùng chung của một workbook. */
    static final class KieuO {
        final CellStyle tieuDe;
        final CellStyle dam;
        final CellStyle tien;
        final CellStyle tienDam;
        final CellStyle ngay;
        final CellStyle tieuDeTrang;

        KieuO(Workbook wb) {
            Font dam = wb.createFont();
            dam.setBold(true);
            Font lon = wb.createFont();
            lon.setBold(true);
            lon.setFontHeightInPoints((short) 14);
            short dinhDangTien = wb.createDataFormat()
                    .getFormat(DINH_DANG_TIEN);
            tieuDe = wb.createCellStyle();
            tieuDe.setFont(dam);
            this.dam = wb.createCellStyle();
            this.dam.setFont(dam);
            tien = wb.createCellStyle();
            tien.setDataFormat(dinhDangTien);
            tienDam = wb.createCellStyle();
            tienDam.setDataFormat(dinhDangTien);
            tienDam.setFont(dam);
            ngay = wb.createCellStyle();
            ngay.setDataFormat(wb.createDataFormat()
                    .getFormat(DINH_DANG_NGAY));
            tieuDeTrang = wb.createCellStyle();
            tieuDeTrang.setFont(lon);
        }
    }

    /** Ô chữ; null thì để trống ô. */
    static void chu(Row r, int cot, String giaTri, CellStyle kieu) {
        if (giaTri == null) {
            return;
        }
        Cell c = r.createCell(cot);
        c.setCellValue(giaTri);
        if (kieu != null) {
            c.setCellStyle(kieu);
        }
    }

    /** Ô tiền (số); null thì để trống ô. */
    static void tien(Row r, int cot, BigDecimal giaTri, CellStyle kieu) {
        if (giaTri == null) {
            return;
        }
        Cell c = r.createCell(cot);
        c.setCellValue(giaTri.doubleValue());
        c.setCellStyle(kieu);
    }

    /** Ô ngày; null thì để trống ô. */
    static void ngay(Row r, int cot, LocalDate giaTri, CellStyle kieu) {
        if (giaTri == null) {
            return;
        }
        Cell c = r.createCell(cot);
        c.setCellValue(giaTri);
        c.setCellStyle(kieu);
    }

    /**
     * Ghi nhận độ dài nội dung của một ô để đặt độ rộng cột.
     *
     * @param doDai độ dài lớn nhất từng cột
     * @param cot cột
     * @param noiDung nội dung hiển thị, null thì bỏ qua
     */
    static void dai(int[] doDai, int cot, String noiDung) {
        if (noiDung != null) {
            doDai[cot] = Math.max(doDai[cot], noiDung.length());
        }
    }

    /**
     * Đặt độ rộng cột theo nội dung dài nhất (đếm ký tự, không cần phông
     * chữ của máy), cố định các dòng tới hết dòng tiêu đề.
     *
     * @param sh sheet
     * @param soCot số cột
     * @param dongTieuDe chỉ số dòng tiêu đề bảng
     * @param doDai độ dài lớn nhất của nội dung từng cột, theo ký tự
     */
    static void hoanTat(Sheet sh, int soCot, int dongTieuDe, int[] doDai) {
        for (int i = 0; i < soCot; i++) {
            int kyTu = Math.min(RONG_TOI_DA, doDai[i] + 2);
            sh.setColumnWidth(i, kyTu * 256);
        }
        sh.createFreezePane(0, dongTieuDe + 1);
    }
}
