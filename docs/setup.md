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

### Dựng lại CSDL khi kéo bản mới

`schema.sql` chỉ tạo bảng **chưa có** (`CREATE TABLE IF NOT EXISTS`), không
thêm cột vào bảng đã có, và dự án không có script nâng cấp. Vì vậy, khi bản
mới đổi cấu trúc bảng đã có, CSDL cũ phải dựng lại từ đầu:

| Từ bản | Lên bản | Thay đổi bảng đã có | Cần dựng lại |
|---|---|---|---|
| `v0.1.0` (Sprint 1) | `v0.2.0` (Sprint 2) | Thêm cột `tai_khoan.so_dien_thoai` | Có |
| `v0.2.0` (Sprint 2) | `v0.3.0` (Sprint 3) | Không. Chỉ thêm bảng mới `bo_dem_bien_lai`, `phieu_thu`, `phieu_thu_chi_tiet` | Không: chạy lại `schema.sql` là đủ, dữ liệu cũ giữ nguyên |

Dấu hiệu chưa dựng lại: đăng nhập báo "Có lỗi khi đọc hoặc lưu dữ liệu"
(CSDL thiếu cột mà ứng dụng đọc).

Cách làm, bằng tài khoản root trong MySQL Workbench:

1. `DROP DATABASE bluemoon;` (xóa **toàn bộ** dữ liệu trong CSDL này, kể
   cả dữ liệu tự nhập khi thử; dữ liệu mẫu sẽ có lại ở bước 2).
2. Chạy lại `database/schema.sql`, rồi `database/seed.sql` (bước 2 ở
   trên).
3. Không cần tạo lại user: quyền `GRANT ALL ON bluemoon.*` ở bước 3 vẫn
   còn sau khi xóa CSDL.

Khi mở Pull Request đổi cấu trúc bảng đã có, thêm một dòng vào bảng trên.

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

Kiểm thử tích hợp với MySQL thật (lớp `*IT`, thử DAO và giao dịch): tạo một
CSDL **riêng có chữ `test` trong tên**, vì mỗi test xóa và tạo lại toàn bộ
bảng của CSDL đó.

```sql
CREATE DATABASE bluemoon_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL ON bluemoon_test.* TO 'bluemoon'@'localhost';
```

Rồi đặt biến môi trường và chạy `mvn verify -Pmysql` (PowerShell):

```powershell
$env:BLUEMOON_DB_URL = "jdbc:mysql://localhost:3306/bluemoon_test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8"
mvn verify -Pmysql
```

Thiếu biến này, hoặc tên CSDL không có chữ `test`, thì các lớp `*IT` tự bỏ
qua. `mvn verify` không có `-Pmysql` không chạy kiểm thử tích hợp.

## 5. Chuẩn bị demo

Ngày của dữ liệu mẫu tạm trú/tạm vắng được tính lúc chạy `seed.sql` (theo
`CURDATE()`). Trong vài ngày trước demo, dựng lại CSDL (mục 2) rồi kiểm tra
màn hình Nhân khẩu:

| Hộ | Kỳ vọng |
|---|---|
| A0601 | Hoàng Văn Nam đang tạm vắng (còn hạn 25 ngày sau khi seed) |
| K01 | Vũ Văn Toàn tạm trú còn hạn (120 ngày sau khi seed) |
| PH01 | Đỗ Thị Hoa tạm trú đã quá hạn, cột hạn tạm trú tô đỏ |

Kịch bản thu phí (Sprint 3), sau khi dựng CSDL:

1. **Công nợ:** chọn tháng trước, bấm Chốt công nợ; làm lại với tháng này.
2. **Thu phí:** nhập `A0601`, Tra cứu; hộ có 4 khoản chưa đóng (2 tháng ×
   phí dịch vụ, phí quản lý). Nhập 100.000 cho "Quỹ vì người nghèo 2026",
   chọn Tiền mặt, bấm Thu tất cả, In biên lai (chọn "Microsoft Print to
   PDF" nếu không có máy in).
3. **Phiếu thu hôm nay:** in lại phiếu vừa lập (biên lai ghi "BẢN IN LẠI"),
   rồi Hủy phiếu với lý do; tra cứu lại `A0601` thấy các khoản trở lại chưa
   đóng.
4. **Công nợ:** cột "Đã đóng"; In thông báo phí cho tất cả hộ.
5. **Nhân khẩu:** Danh sách tạm vắng hôm nay có Hoàng Văn Nam (A0601).

## 6. Lỗi thường gặp

| Hiện tượng | Cách xử lý |
|---|---|
| "Không kết nối được cơ sở dữ liệu" | Sai `user`/`password` trong `config/db.properties`, hoặc MySQL chưa chạy |
| "Thiếu hoặc sai cấu hình kết nối cơ sở dữ liệu" | Chưa có `config/db.properties` (mục 2, bước 4) |
| "Có lỗi khi đọc hoặc lưu dữ liệu" ngay khi đăng nhập, sau khi kéo bản mới | CSDL tạo từ bản cũ, thiếu cột mới: [dựng lại CSDL](#dựng-lại-csdl-khi-kéo-bản-mới) |
| `release version 21 not supported` | Chọn JDK 21 trong Project Structure |
| `'mvn' is not recognized` | Chạy bằng bảng Maven trong IntelliJ |
| Không thấy cửa sổ ứng dụng | Cửa sổ nằm sau IntelliJ, dùng Alt+Tab |
| `CREATE USER` báo user đã tồn tại | Bỏ qua, chạy tiếp `GRANT` |
