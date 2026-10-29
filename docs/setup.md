# Cài đặt và chạy

## 1. Cài phần mềm

| Phần mềm | Ghi chú |
|---|---|
| IntelliJ IDEA (Community hoặc Ultimate) | Có sẵn Maven, không cần cài Maven riêng |
| JDK 21 (Eclipse Temurin) | IntelliJ: File → Project Structure → SDK → Download JDK → 21, Temurin |
| MySQL 8.0 + MySQL Workbench | MySQL Installer cho Windows, chọn Custom |
| Git | |
| Scene Builder (tùy chọn) | Sửa file `.fxml` bằng giao diện kéo thả |

## 2. Dựng cơ sở dữ liệu

1. Mở MySQL Workbench, kết nối bằng tài khoản root.
2. Mở và chạy `database/schema.sql`, rồi `database/seed.sql` (biểu tượng tia sét).
3. Tạo user cho ứng dụng, mỗi người tự đặt mật khẩu:
   ```sql
   CREATE USER 'bluemoon'@'localhost' IDENTIFIED BY 'mat_khau_cua_ban';
   GRANT ALL ON bluemoon.* TO 'bluemoon'@'localhost';
   ```
4. Sao chép `config/db.properties.example` thành `config/db.properties`,
   điền `user` và `password`. File này nằm trong `.gitignore`, không commit.

Khi `schema.sql` thay đổi cấu trúc bảng đã có, dựng lại CSDL bằng root:
`DROP DATABASE bluemoon;` rồi chạy lại bước 2 (quyền ở bước 3 vẫn giữ).

## 3. Chạy ứng dụng

1. IntelliJ → Open → chọn thư mục repo → Trust Project, chờ tải thư viện Maven.
2. Mở `src/main/java/com/bluemoon/Launcher.java` → Run `Launcher.main()`.
   Chạy `Launcher`, không chạy `App`: JavaFX cần lớp khởi động riêng khi
   chạy từ classpath.
3. Đăng nhập `admin` / `Admin@123`.

Cảnh báo `Unsupported JavaFX configuration: classes were loaded from
'unnamed module'` là bình thường.

## 4. Chạy test

IntelliJ → Maven → bluemoon → Lifecycle → `test`. Kết quả mong đợi:
`BUILD SUCCESS`. Test dùng DAO giả trong bộ nhớ, không cần MySQL.

Dòng lệnh (cần Maven 3.9+ trong PATH): `mvn test`, chạy app: `mvn javafx:run`.

## 5. Chuẩn bị demo

Ngày của dữ liệu mẫu tạm trú/tạm vắng được tính lúc chạy `seed.sql` (theo
`CURDATE()`). Trong vài ngày trước demo, dựng lại CSDL (mục 2) rồi kiểm tra
màn hình Nhân khẩu:

| Hộ | Kỳ vọng |
|---|---|
| A0601 | Hoàng Văn Nam đang tạm vắng (còn hạn 25 ngày sau khi seed) |
| K01 | Vũ Văn Toàn tạm trú còn hạn (120 ngày sau khi seed) |
| PH01 | Đỗ Thị Hoa tạm trú đã quá hạn, cột hạn tạm trú tô đỏ |

## 6. Lỗi thường gặp

| Hiện tượng | Cách xử lý |
|---|---|
| Báo không kết nối được cơ sở dữ liệu | Sai `user`/`password` trong `config/db.properties`, hoặc MySQL chưa chạy |
| `release version 21 not supported` | Chọn JDK 21 trong Project Structure |
| `'mvn' is not recognized` | Chạy bằng bảng Maven trong IntelliJ |
| Không thấy cửa sổ ứng dụng | Cửa sổ nằm sau IntelliJ, dùng Alt+Tab |
| `CREATE USER` báo user đã tồn tại | Bỏ qua, chạy tiếp `GRANT` |
