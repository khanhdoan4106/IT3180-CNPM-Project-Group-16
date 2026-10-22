# Hướng dẫn cài đặt môi trường

## Cần cài
1. **IntelliJ IDEA** (bản Community hoặc Ultimate). Sinh viên có thể dùng
   bản Ultimate miễn phí qua JetBrains Educational Pack. IntelliJ có sẵn
   Maven, nên **không cần cài Maven riêng** nếu chỉ làm trong IntelliJ.
2. **JDK 21** (Eclipse Temurin). Cài ngay trong IntelliJ:
   File → Project Structure → Project → SDK → Add SDK → Download JDK →
   Version 21, Vendor Eclipse Temurin.
3. **MySQL 8.0** + MySQL Workbench (MySQL Installer cho Windows, chọn
   *Custom*, thêm *MySQL Server 8.0* và *MySQL Workbench*).
4. **Scene Builder** (gluonhq.com/products/scene-builder) để sửa file `.fxml`.
5. **Git**.

## Dựng cơ sở dữ liệu
1. Trong MySQL Installer, đặt mật khẩu root và ghi nhớ.
2. Mở MySQL Workbench, kết nối `Local instance MySQL80` bằng tài khoản root.
3. Chạy `database/schema.sql`, rồi `database/seed.sql`
   (File → Open SQL Script → bấm biểu tượng tia sét).
4. Tạo user riêng cho ứng dụng, **mỗi người tự đặt mật khẩu của mình**:
   ```sql
   CREATE USER 'bluemoon'@'localhost' IDENTIFIED BY 'mat_khau_cua_ban';
   GRANT ALL ON bluemoon.* TO 'bluemoon'@'localhost';
   ```
   Nếu báo user đã tồn tại thì chỉ cần chạy lệnh `GRANT`. Muốn đổi mật khẩu:
   `ALTER USER 'bluemoon'@'localhost' IDENTIFIED BY 'mat_khau_moi';`
5. Sao chép `config/db.properties.example` thành `config/db.properties`
   và điền `user`, `password`. File này **không được commit** (đã có trong
   `.gitignore`).

**Khi `schema.sql` đổi cấu trúc bảng đã có** (vd US-002 bỏ cột
`ten_chu_ho` khỏi `ho_gia_dinh`), `CREATE TABLE IF NOT EXISTS` không sửa
bảng cũ. Dữ liệu dev chỉ là dữ liệu mẫu nên dựng lại bằng tài khoản root:
`DROP DATABASE bluemoon;` rồi chạy lại bước 3. Quyền `GRANT` ở bước 4 vẫn
còn, không cần chạy lại.

## Mở dự án và chạy
1. IntelliJ: **Open** → chọn thư mục repo (thư mục có `pom.xml`) →
   **Trust Project**. Chờ tải thư viện Maven xong (thanh tiến trình góc dưới
   bên phải).
2. Chọn JDK 21 nếu IntelliJ hỏi hoặc ô SDK đang trống.
3. **Chạy ứng dụng:** mở `src/main/java/com/bluemoon/Launcher.java`, bấm
   mũi tên xanh cạnh `main` → *Run 'Launcher.main()'*.
   Chạy lớp `Launcher`, không chạy trực tiếp `App`, vì JavaFX cần lớp khởi
   động riêng khi chạy từ classpath.
4. Đăng nhập thử: `admin` / `Admin@123`.

Cửa sổ ứng dụng có thể nằm sau IntelliJ; kiểm tra thanh tác vụ Windows.
Cảnh báo `Unsupported JavaFX configuration: classes were loaded from
'unnamed module'` là bình thường, không phải lỗi.

## Chạy test
Trong IntelliJ: bảng **Maven** (bên phải) → bluemoon → Lifecycle → **test**
(bấm đúp). Kết quả mong đợi: `BUILD SUCCESS`, 84 test pass. Test không cần
MySQL (dùng DAO giả trong bộ nhớ).

## Chuẩn bị demo
Ngày của các đợt tạm trú/tạm vắng mẫu được tính **lúc chạy `seed.sql`**
(theo `CURDATE()`), không phải lúc mở ứng dụng. Vì vậy, trong vòng vài
ngày trước buổi demo:

1. Đăng nhập MySQL bằng root, chạy `DROP DATABASE IF EXISTS bluemoon;`.
2. Chạy lại `database/schema.sql`, rồi `database/seed.sql`.
3. Chạy `Launcher`, đăng nhập `admin` / `Admin@123`, mở màn hình
   **Nhân khẩu** và kiểm tra dữ liệu mẫu đúng như sau:
   - Hộ **A0601**: Hoàng Văn Nam đang tạm vắng (cột "Tam vang den" có ngày).
   - Hộ **K01**: Vũ Văn Toàn tạm trú còn hạn.
   - Hộ **PH01**: Đỗ Thị Hoa tạm trú đã quá hạn (cột "Han tam tru" tô đỏ).

Seed chạy cách buổi demo quá 25 ngày thì Hoàng Văn Nam sẽ hết tạm vắng;
quá 120 ngày thì Vũ Văn Toàn cũng quá hạn tạm trú.

## Dùng dòng lệnh (không bắt buộc)
Cần cài Maven 3.9+ và đặt trong PATH, `JAVA_HOME` trỏ tới JDK 21:
```
mvn test
mvn javafx:run
```

## Lỗi thường gặp
| Hiện tượng | Cách xử lý |
|---|---|
| `'mvn' is not recognized` | Dùng IntelliJ (có sẵn Maven) hoặc cài Maven và thêm vào PATH |
| Không thấy cửa sổ ứng dụng | Tìm trên thanh tác vụ Windows, hoặc Alt+Tab |
| `Khong ket noi duoc co so du lieu` | Sai `user`/`password` trong `config/db.properties`, hoặc MySQL chưa chạy |
| Ô SDK trống, `release version 21 not supported` | Chọn JDK 21 trong Project Structure |
| Báo user đã tồn tại khi `CREATE USER` | Bỏ qua, chạy tiếp lệnh `GRANT` |

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
