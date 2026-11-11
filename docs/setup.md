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

> **Cẩn thận:** `schema.sql` và `seed.sql` đều bắt đầu bằng `USE bluemoon;`
> nên luôn ghi vào CSDL `bluemoon`, dù Workbench đang chọn CSDL khác. Muốn
> thử trên CSDL khác thì sao chép tệp ra rồi sửa dòng `USE`. Riêng kiểm thử
> tích hợp (mục 4) tự tạo bảng trong CSDL `*test*` bằng cách bỏ dòng này.

### Chạy lại `seed.sql`

`seed.sql` chạy lại bao nhiêu lần cũng được: không sinh dòng trùng và không
sửa dữ liệu đã có (dòng đã có theo mã căn hộ, số CCCD, tên khoản thu thì
được bỏ qua). Hai hệ quả khi chạy trên CSDL đã dùng:

- **Ngày của quỹ mẫu không được làm mới.** Hai quỹ mẫu (Quỹ vì người nghèo
  `<năm>`, Quỹ biển đảo) được tạo với thời gian thu từ đầu tháng chạy seed
  đến cuối tháng sau. Chạy lại seed sang tháng khác không kéo dài thời gian
  này, nên quỹ có thể đã hết hạn và màn Thu phí không còn liệt kê quỹ nào.
- **Dòng cũ giữ nguyên.** Tên không dấu của bản mẫu cũ ("Nguyen Thi Lan",
  "Hoang Van Nam") và ngày tạm trú/tạm vắng cũ không bị đổi.

Muốn quỹ đang mở và dữ liệu mẫu có dấu để demo thì **dựng lại CSDL**
(xem dưới), không chạy lại seed trên CSDL cũ.

### Dựng lại CSDL khi kéo bản mới

`schema.sql` chỉ tạo bảng **chưa có** (`CREATE TABLE IF NOT EXISTS`), không
thêm cột vào bảng đã có, và dự án không có script nâng cấp. Vì vậy, khi bản
mới đổi cấu trúc bảng đã có, CSDL cũ phải dựng lại từ đầu:

| Từ bản | Lên bản | Thay đổi bảng đã có | Cần dựng lại |
|---|---|---|---|
| `v0.1.0` (Sprint 1) | `v0.2.0` (Sprint 2) | Thêm cột `tai_khoan.so_dien_thoai` | Có |
| `v0.2.0` (Sprint 2) | `v0.3.0` (Sprint 3) | Không. Chỉ thêm bảng mới `bo_dem_bien_lai`, `phieu_thu`, `phieu_thu_chi_tiet` | Không bắt buộc: chạy lại `schema.sql` là đủ để chạy ứng dụng, dữ liệu cũ giữ nguyên |

Nâng từ `v0.2.0` giữ được dữ liệu, nhưng **dữ liệu mẫu cũ không được đổi**: tên
không dấu ("Nguyen Thi Lan"), quỹ mẫu có ngày cố định (Quỹ biển đảo hết hạn
30/11/2026, Quỹ vì người nghèo 2026 hết hạn 31/12/2026), họ tên tài khoản
`admin` vẫn là "Quản trị viên". Máy dùng để demo nên dựng lại CSDL.

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
Trên GitHub, workflow `CI` có hai job: `build` (`mvn -B verify`) và `build-mysql`
(service `mysql:8.0`, CSDL `bluemoon_test`, `mvn -B verify -Pmysql`), cả hai chạy
cho mọi Pull Request. Chiến lược kiểm thử, bảng AC → test và các việc còn
kiểm tay: [testing.md](testing.md).

## 5. Chuẩn bị demo

Dựng lại CSDL (mục 2, "Dựng lại CSDL") **vào ngày demo hoặc ngày liền trước**,
không chạy lại `seed.sql` trên CSDL cũ (xem "Chạy lại `seed.sql`"). Dữ liệu
mẫu tính theo ngày chạy seed (`CURDATE()`):

- đợt tạm trú/tạm vắng lệch so với ngày chạy;
- hai quỹ tự nguyện mở **từ ngày đầu tháng này đến ngày cuối tháng sau**, nên
  luôn đang mở khi demo trong khoảng đó;
- tên "Quỹ vì người nghèo `<năm hiện tại>`" lấy năm của ngày chạy seed.

Đăng nhập `admin` / `Admin@123`: lời chào ở góc trên là "Xin chào, Nguyễn Thị
Thu Hương (Quản trị viên)". Họ tên, nơi thường trú và lý do mẫu đều có dấu.

Kiểm tra trước màn hình Nhân khẩu:

| Hộ | Kỳ vọng |
|---|---|
| A0601 | Hoàng Văn Nam đang tạm vắng (còn hạn 25 ngày sau khi seed) |
| K01 | Vũ Văn Toàn tạm trú còn hạn (120 ngày sau khi seed) |
| PH01 | Đỗ Thị Hoa tạm trú đã quá hạn, cột hạn tạm trú tô đỏ |

Màn Khoản thu: có Phí dịch vụ (5.000 đ/m²), Phí quản lý (7.000 đ/m²), "Quỹ vì
người nghèo `<năm>`" (không cố định số tiền) và "Quỹ biển đảo" (50.000 đ mỗi
hộ), hai quỹ sau đang mở.

### Kịch bản thu phí (Sprint 3)

Sau khi dựng CSDL, đăng nhập `admin`:

1. **Công nợ:** chọn tháng trước, bấm Chốt công nợ; làm lại với tháng này.
2. **Thu phí:** nhập `A0601`, Tra cứu. Hộ có 4 khoản chưa đóng (2 tháng ×
   phí dịch vụ 377.500 đ và phí quản lý 528.500 đ, tổng nợ 1.812.000 đ), dải
   thông tin hiện "Căn hộ A0601 · Chủ hộ Nguyễn Thị Lan · Đang ở". Nhập
   100.000 cho "Quỹ vì người nghèo `<năm>`", chọn Tiền mặt, bấm Thu tất cả
   (tổng 1.912.000 đ), xác nhận, In biên lai (chọn "Microsoft Print to PDF"
   nếu không có máy in).
3. **Phiếu thu hôm nay:** dải tóm tắt hiện "Số phiếu 1", "Đã thu hôm nay
   1.912.000 đ". In lại phiếu vừa lập (biên lai ghi "BẢN IN LẠI"), rồi Hủy
   phiếu với lý do; trạng thái thành "Đã hủy: <lý do>" màu đỏ. Tra cứu lại
   `A0601` thấy các khoản trở lại chưa đóng.
4. **Công nợ:** cột "Đã đóng"; In thông báo phí cho tất cả hộ.
5. **Nhân khẩu:** Danh sách tạm vắng hôm nay có Hoàng Văn Nam (A0601).
6. **Tìm không dấu (tùy chọn):** ở Hộ gia đình, tìm "nguyen" ra chủ hộ
   Nguyễn Thị Lan (giả định 40).

Đăng nhập thủ quỹ (tạo ở màn Tài khoản) để thử phân quyền: không có menu
Hộ gia đình, Nhân khẩu, màn Tài khoản chỉ còn tab "Tài khoản của tôi"; hủy
phiếu chỉ hủy được phiếu do mình lập.

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
| Sửa `config/db.properties` khi ứng dụng đang chạy (kể cả vừa tạo file sau khi thấy báo thiếu cấu hình) mà vẫn báo lỗi cũ | Tắt và mở lại ứng dụng: cấu hình chỉ được đọc một lần khi khởi động |
