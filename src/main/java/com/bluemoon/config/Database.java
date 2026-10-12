package com.bluemoon.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Doc cau hinh MySQL va tao ket noi JDBC.
 *
 * <p>Thu tu uu tien: file {@code config/db.properties} (khong commit),
 * sau do bien moi truong {@code BLUEMOON_DB_URL},
 * {@code BLUEMOON_DB_USER}, {@code BLUEMOON_DB_PASSWORD}.
 */
public final class Database {

    private static final Path FILE_CAU_HINH =
            Path.of("config", "db.properties");

    private Database() {
    }

    /**
     * Doc cau hinh ket noi.
     *
     * @return thuoc tinh url, user, password
     */
    public static Properties docCauHinh() {
        Properties props = new Properties();
        if (Files.exists(FILE_CAU_HINH)) {
            try (InputStream in = Files.newInputStream(FILE_CAU_HINH)) {
                props.load(in);
            } catch (IOException e) {
                throw new CauHinhException(
                        "Khong doc duoc " + FILE_CAU_HINH, e);
            }
        }
        ghiDeTuMoiTruong(props, "url", "BLUEMOON_DB_URL");
        ghiDeTuMoiTruong(props, "user", "BLUEMOON_DB_USER");
        ghiDeTuMoiTruong(props, "password", "BLUEMOON_DB_PASSWORD");
        return props;
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
                    "Thieu cau hinh CSDL. Sao chep config/db.properties.example"
                            + " thanh config/db.properties va dien thong tin.");
        }
        try {
            return DriverManager.getConnection(url,
                    props.getProperty("user"), props.getProperty("password"));
        } catch (SQLException e) {
            throw new CauHinhException("Khong ket noi duoc MySQL", e);
        }
    }
}
