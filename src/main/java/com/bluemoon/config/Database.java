package com.bluemoon.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Doc cau hinh MySQL va tao ket noi JDBC.
 *
 * <p>Thu tu uu tien: file {@code config/db.properties} (khong commit),
 * sau do bien moi truong {@code BLUEMOON_DB_URL},
 * {@code BLUEMOON_DB_USER}, {@code BLUEMOON_DB_PASSWORD}.
 */
public final class Database {

    /**
     * Mui gio cua moi phien MySQL: Viet Nam, +07:00 khong doi theo mua nen
     * khong can bang mui gio ten (mysql.time_zone_name) tren may chu.
     */
    public static final String MUI_GIO_PHIEN = "+07:00";

    private static final Path FILE_MAC_DINH =
            Path.of("config", "db.properties");

    private static Path fileCauHinh = FILE_MAC_DINH;
    private static Properties cauHinh;
    private static int soLanDocFile;

    private Database() {
    }

    /**
     * Doc cau hinh ket noi. File va bien moi truong chi duoc doc o lan goi
     * dau, cac lan sau dung ban luu trong bo nho. Doc loi thi khong luu,
     * lan sau thu doc lai.
     *
     * @return ban sao thuoc tinh url, user, password
     */
    public static synchronized Properties docCauHinh() {
        if (cauHinh == null) {
            cauHinh = napCauHinh();
        }
        Properties banSao = new Properties();
        banSao.putAll(cauHinh);
        return banSao;
    }

    private static Properties napCauHinh() {
        Properties props = new Properties();
        if (Files.exists(fileCauHinh)) {
            soLanDocFile++;
            try (InputStream in = Files.newInputStream(fileCauHinh)) {
                props.load(in);
            } catch (IOException e) {
                throw new CauHinhException(
                        "Không đọc được " + fileCauHinh, e);
            }
        }
        ghiDeTuMoiTruong(props, "url", "BLUEMOON_DB_URL");
        ghiDeTuMoiTruong(props, "user", "BLUEMOON_DB_USER");
        ghiDeTuMoiTruong(props, "password", "BLUEMOON_DB_PASSWORD");
        return props;
    }

    /**
     * Xoa cau hinh da nho va doi file cau hinh. Chi dung cho test.
     *
     * @param file file cau hinh thay the, null de dung file mac dinh
     */
    static synchronized void datLaiChoTest(Path file) {
        fileCauHinh = file == null ? FILE_MAC_DINH : file;
        cauHinh = null;
        soLanDocFile = 0;
    }

    static synchronized int soLanDocFileChoTest() {
        return soLanDocFile;
    }

    private static void ghiDeTuMoiTruong(Properties props, String khoa,
            String bienMoiTruong) {
        String giaTri = System.getenv(bienMoiTruong);
        if (giaTri != null && !giaTri.isBlank()) {
            props.setProperty(khoa, giaTri);
        }
    }

    /**
     * Mo ket noi moi toi MySQL. Noi goi chiu trach nhiem dong ket noi.
     *
     * @return ket noi dang mo
     */
    public static Connection getConnection() {
        Properties props = docCauHinh();
        String url = props.getProperty("url");
        if (url == null || url.isBlank()) {
            throw new CauHinhException(
                    "Thiếu cấu hình CSDL. Sao chép config/db.properties.example"
                            + " thành config/db.properties và điền thông tin,"
                            + " rồi tắt và mở lại ứng dụng.");
        }
        try {
            Connection conn = DriverManager.getConnection(url,
                    props.getProperty("user"), props.getProperty("password"));
            try (Statement st = conn.createStatement()) {
                // Cot do CSDL tu dien (DEFAULT CURRENT_TIMESTAMP, NOW()) va
                // CURDATE() theo gio Viet Nam du may chu MySQL dat mui gio
                // khac (vd UTC tren may chu cloud, Docker).
                st.execute("SET time_zone = '" + MUI_GIO_PHIEN + "'");
            } catch (SQLException e) {
                conn.close();
                throw e;
            }
            return conn;
        } catch (SQLException e) {
            throw new CauHinhException("Không kết nối được MySQL", e);
        }
    }
}
