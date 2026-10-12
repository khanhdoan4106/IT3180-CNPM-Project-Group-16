# Hướng dẫn cài đặt môi trường

## Cần cài
1. **JDK 21** (Temurin): https://adoptium.net
2. **Maven 3.9+** (hoặc dùng Maven tích hợp IntelliJ/NetBeans)
3. **MySQL 8** + MySQL Workbench
4. **Scene Builder** (gluonhq.com/products/scene-builder) để sửa file `.fxml`
5. IDE: IntelliJ IDEA Community (khuyến nghị)

Kiểm tra: `java -version` hiện 21, `mvn -v` chạy được.

## Dựng cơ sở dữ liệu
1. Mở MySQL Workbench, chạy `database/schema.sql` rồi `database/seed.sql`.
2. Tạo user riêng (không dùng root cho ứng dụng):
   ```sql
   CREATE USER 'bluemoon'@'localhost' IDENTIFIED BY 'mat_khau_cua_ban';
   GRANT ALL ON bluemoon.* TO 'bluemoon'@'localhost';
   ```
3. Sao chép `config/db.properties.example` thành `config/db.properties`
   và điền user/mật khẩu. File này **không được commit**.

## Chạy
```
mvn javafx:run
```
Đăng nhập thử: `admin` / `Admin@123`.

## Test
```
mvn test
```
Test không cần MySQL (dùng DAO giả trong bộ nhớ).

## Lưu ý Windows
- Mở CMD tại thư mục dự án (gõ `cmd` vào thanh địa chỉ File Explorer).
- Nếu `mvn` không nhận: kiểm tra biến môi trường `JAVA_HOME` và `PATH`.

## Cấu trúc
```
src/main/java/com/bluemoon/
  model/       lớp dữ liệu thuần (không SQL, không in ấn)
  dao/         truy cập MySQL, dùng PreparedStatement
  service/     nghiệp vụ
  controller/  xử lý sự kiện giao diện
  util/        tiện ích (băm mật khẩu, điều hướng)
src/main/resources/com/bluemoon/view/  file FXML
src/main/resources/com/bluemoon/css/   giao diện
```
