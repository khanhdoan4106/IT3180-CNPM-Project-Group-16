package com.bluemoon.controller;

import com.bluemoon.config.CauHinhException;
import java.sql.SQLException;

/**
 * Thong bao cho nguoi dung khi loi o tang CSDL, tach mat ket noi voi loi
 * du lieu de nguoi ho tro biet can xem dau.
 */
final class LoiCsdl {

    /** Khong mo duoc, hoac mat, ket noi toi MySQL. */
    static final String MAT_KET_NOI = "Không kết nối được cơ sở dữ liệu."
            + " Kiểm tra MySQL đã chạy chưa, mạng, và user, mật khẩu trong"
            + " config/db.properties. Sửa file xong thì tắt và mở lại ứng"
            + " dụng (cấu hình chỉ được đọc một lần khi khởi động). Nếu vẫn"
            + " lỗi, báo người quản trị hệ thống.";
    /** Thieu hoac khong doc duoc config/db.properties. */
    static final String SAI_CAU_HINH = "Thiếu hoặc sai cấu hình kết nối cơ"
            + " sở dữ liệu (config/db.properties). Sửa file xong thì tắt và"
            + " mở lại ứng dụng (cấu hình chỉ được đọc một lần khi khởi"
            + " động). Nếu không tự sửa được, báo người quản trị hệ thống.";
    /** Co ket noi nhung cau lenh bi CSDL tu choi. */
    static final String LOI_DU_LIEU = "Có lỗi khi đọc hoặc lưu dữ liệu."
            + " Thử lại; nếu vẫn lỗi, báo người quản trị hệ thống.";

    /** SQLState lop 08: loi ket noi (vd 08S01 khi mat ket noi). */
    private static final String LOP_KET_NOI = "08";

    private LoiCsdl() {
    }

    /**
     * Thong bao ung voi loi.
     *
     * @param e loi tu tang config hoac DAO
     * @return thong bao hien cho nguoi dung
     */
    static String thongBao(RuntimeException e) {
        if (e instanceof CauHinhException) {
            return coLoiSql(e) ? MAT_KET_NOI : SAI_CAU_HINH;
        }
        return matKetNoi(e) ? MAT_KET_NOI : LOI_DU_LIEU;
    }

    private static boolean coLoiSql(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException) {
                return true;
            }
        }
        return false;
    }

    private static boolean matKetNoi(Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && sql.getSQLState() != null
                    && sql.getSQLState().startsWith(LOP_KET_NOI)) {
                return true;
            }
        }
        return false;
    }
}
