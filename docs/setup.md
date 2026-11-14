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

> **Trigger nhật ký:** `schema.sql` tạo hai trigger chặn sửa, xóa bảng
> `nhat_ky` (TS-002). MySQL 8 bật binary log sẵn, nên tạo trigger cần quyền
> `SUPER` hoặc biến `log_bin_trust_function_creators = 1`; nếu không, MySQL
> báo lỗi 1419 "You do not have the SUPER privilege and binary logging is
> enabled". Chạy `schema.sql` bằng root (như bước 2) thì không gặp lỗi này.
> `schema.sql` xóa trigger cũ (`DROP TRIGGER IF EXISTS`) trước khi tạo, nên
> chạy lại được nhiều lần.

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
| `v0.3.0` (Sprint 3) | `v0.4.0` (Sprint 4, chưa phát hành) | Không. Chỉ thêm bảng mới `nhat_ky` và hai trigger `trg_nky_chan_sua`, `trg_nky_chan_xoa` (TS-002) | Không bắt buộc: chạy lại `schema.sql` bằng root, dữ liệu cũ giữ nguyên; nhật ký bắt đầu từ lúc nâng cấp |

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

Mỗi test chạy lại `schema.sql`, kể cả tạo trigger nhật ký, bằng user đã cấu
hình (`config/db.properties` hoặc `BLUEMOON_DB_USER`). User `bluemoon` không
có quyền `SUPER`, nên nếu MySQL bật binary log (mặc định của MySQL 8) thì
root chạy thêm một lần (giữ qua các lần khởi động lại MySQL):

```sql
SET PERSIST log_bin_trust_function_creators = 1;
```

Không muốn đổi biến này thì chạy kiểm thử tích hợp bằng root (đặt
`BLUEMOON_DB_USER`, `BLUEMOON_DB_PASSWORD`), như CI.

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

### Dữ liệu demo đầy đủ (`seed-demo.sql`)

Muốn các màn có nhiều số liệu (biểu đồ 6 tháng, hộ nợ đọng, thống kê, nhật
ký), dựng lại CSDL rồi chạy **ba tệp theo thứ tự**, cùng ngày demo:

```
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed.sql
mysql -u root -p < database/seed-demo.sql
```

(hoặc mở lần lượt ba tệp trong MySQL Workbench và bấm Execute). Dòng cuối in
"Đã nạp dữ liệu demo: 40 hộ, 114 nhân khẩu, … phiếu thu". `seed-demo.sql` chỉ
dùng cho máy demo, chỉ chạy một lần trên CSDL vừa dựng: nếu CSDL đã có đợt
chốt, phiếu thu hoặc hộ demo thì tệp không thêm gì và báo lý do. Tệp được
sinh bằng `python scripts/sinh-seed-demo.py` (không sửa tay); mọi người, số
CCCD, số điện thoại trong đó đều hư cấu.

Sau khi nạp:

- 40 hộ (36 hộ demo: căn hộ khối A, B, C tầng 5 – 28, ki-ốt K02 – K04; một
  hộ đã chuyển đi), 114 nhân khẩu; 6 tháng gần nhất đã chốt công nợ (tháng
  này chỉ chốt và thu khi đã qua ngày 2 của tháng), khoảng 180 phiếu thu,
  80 – 90% hộ đóng đủ mỗi tháng cũ, khoảng một nửa đã đóng tháng này.
- Tài khoản: `admin` / `Admin@123` (Quản trị viên) và `thuquy` /
  `Thuquy@123` (Thủ quỹ "Trần Thị Bình", người thu phần lớn phiếu).
- Quỹ tự nguyện "Quỹ khuyến học" đã thu xong (4 tháng trước đến 2 tháng
  trước), hai quỹ của `seed.sql` đang mở có vài hộ đóng góp; có hộ ghi 0 đ
  ("Không đóng").
- `seed-demo.sql` đặt lại thời điểm ghi của các dòng biến động mẫu trong
  `seed.sql` về đúng ngày sự kiện, để "Biến động nhân khẩu gần đây" xếp
  đúng thứ tự.

Hộ tiêu biểu để trình bày:

| Hộ | Dùng để demo |
|---|---|
| C1508 (Tạ Thanh Hiền) | Nợ 5 tháng, đứng đầu tab "Hộ nợ đọng"; chủ hộ đang tạm vắng |
| B0907, A2203 | Nợ 4 và 3 tháng |
| B1104 (đã chuyển đi) | Cả hộ chuyển đi 3 tháng trước khi còn nợ 3 tháng: vẫn có ở "Hộ nợ đọng" kèm "(đã chuyển đi)" và tra cứu thu phí được |
| A0601 | Nợ đúng 2 tháng (1.812.000 đ) như kịch bản thu phí dưới đây; Hoàng Văn Nam tạm vắng |
| B2005 | Phiếu tiền mặt bị hủy ("Chọn nhầm hình thức thanh toán") rồi thu lại bằng chuyển khoản |
| C0806 | Phiếu tháng trước bị hủy ("Thu nhầm hộ"), hộ còn nợ 2 tháng |
| A1702 | Một phiếu đóng bù hai tháng (4 khoản) |
| B1406, C1003 | Đổi chủ hộ: chủ hộ cũ rời hộ (B1406), chủ hộ cũ ở lại làm vợ/chồng (C1003); xem "Lịch sử biến động" |
| A0908, C2101, C2407 | Có người đang tạm vắng |
| A1808, B1907 | Người tạm trú đã quá hạn (cột hạn tô đỏ); C1203, A1702, B1301, C2705 tạm trú còn hạn |
| B1702, C1502, C2205, B1903 | Hộ mới chuyển đến trong 6 tháng (có trong Nhật ký) |

Có `seed-demo.sql` thì kịch bản thu phí dưới đây vẫn chạy được: bước 1 "Chốt
công nợ" chỉ báo không có khoản mới (đã chốt sẵn), tab "Phiếu thu hôm nay"
có thể đã có vài phiếu của Thủ quỹ trong ngày.

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
| "Không kết nối được cơ sở dữ liệu" | Sai `user`/`password` trong `config/db.properties`, hoặc MySQL chưa chạy. Sửa file xong thì tắt và mở lại ứng dụng |
| "Thiếu hoặc sai cấu hình kết nối cơ sở dữ liệu" | Chưa có `config/db.properties` (mục 2, bước 4). Tạo hoặc sửa xong thì tắt và mở lại ứng dụng |
| "Có lỗi khi đọc hoặc lưu dữ liệu" ngay khi đăng nhập, sau khi kéo bản mới | CSDL tạo từ bản cũ, thiếu cột mới: [dựng lại CSDL](#dựng-lại-csdl-khi-kéo-bản-mới) |
| `release version 21 not supported` | Chọn JDK 21 trong Project Structure |
| `'mvn' is not recognized` | Chạy bằng bảng Maven trong IntelliJ |
| Không thấy cửa sổ ứng dụng | Cửa sổ nằm sau IntelliJ, dùng Alt+Tab |
| `CREATE USER` báo user đã tồn tại | Bỏ qua, chạy tiếp `GRANT` |
| Lỗi 1419 "You do not have the SUPER privilege and binary logging is enabled" khi chạy `schema.sql`, `mvn verify -Pmysql` hoặc khôi phục sao lưu | Chạy `schema.sql` bằng root, hoặc cho kiểm thử tích hợp: [mục 4](#4-chạy-test); khôi phục sao lưu: [mục 7](#7-sao-lưu-và-khôi-phục) |
| Sửa `config/db.properties` khi ứng dụng đang chạy (kể cả vừa tạo file sau khi thấy báo thiếu cấu hình) mà vẫn báo lỗi cũ | Tắt và mở lại ứng dụng: cấu hình chỉ được đọc một lần khi khởi động |

## 7. Sao lưu và khôi phục

TS-001, giả định 43. Làm trên **máy đặt MySQL**. Ba script trong `scripts/`:

| Script | Làm gì |
|---|---|
| `sao-luu.ps1` | `mysqldump` CSDL `bluemoon` ra `bluemoon-yyyyMMdd-HHmmss.sql`, ghi một dòng vào `sao-luu.log`, giữ 30 bản mới nhất |
| `dang-ky-lich-sao-luu.ps1` | Đăng ký tác vụ Task Scheduler "BlueMoon – Sao lưu hằng ngày" chạy `sao-luu.ps1` lúc 22:00 mỗi ngày; `-Xoa` để gỡ |
| `khoi-phuc-thu.ps1` | Nạp một tệp sao lưu vào CSDL thử `bluemoon_khoi_phuc_thu`, so số dòng từng bảng và tổng tiền phiếu thu với `bluemoon`, kiểm tra trigger nhật ký |

Chạy script bằng `powershell -ExecutionPolicy Bypass -File …` như các lệnh
dưới đây: cách này chỉ bỏ qua chính sách thực thi cho lần chạy đó, **không
đổi chính sách của máy** (không cần `Set-ExecutionPolicy`). Script chạy được
trên Windows PowerShell 5.1 có sẵn trong Windows và PowerShell 7 (`pwsh`).
Mở PowerShell ở thư mục repo.

### Bước 1. Tài khoản MySQL và login-path

Script **không nhận mật khẩu qua tham số** và không ghi mật khẩu ra log.
Nên dùng một tài khoản MySQL riêng chỉ có quyền đủ để sao lưu (root chạy
trong MySQL Workbench):

```sql
CREATE USER 'bluemoon_sao_luu'@'localhost' IDENTIFIED BY 'mat_khau_cua_ban';
GRANT SELECT, SHOW VIEW, TRIGGER, LOCK TABLES ON bluemoon.* TO 'bluemoon_sao_luu'@'localhost';
GRANT PROCESS ON *.* TO 'bluemoon_sao_luu'@'localhost';
```

Lưu mật khẩu vào login-path `bluemoon-sao-luu` bằng `mysql_config_editor`.
Mật khẩu được hỏi, không gõ trên dòng lệnh, và lưu ở dạng mã hóa trong
`%APPDATA%\MySQL\.mylogin.cnf` của tài khoản Windows đang dùng:

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql_config_editor.exe" set --login-path=bluemoon-sao-luu --host=localhost --user=bluemoon_sao_luu --password
```

Login-path gắn với tài khoản Windows: tạo bằng chính tài khoản sẽ chạy tác
vụ lịch. Không dùng được `mysql_config_editor` thì tạo tệp tùy chọn **ngoài
repo** (vd `D:\bi-mat\bluemoon-sao-luu.cnf`, chỉ tài khoản của bạn đọc được)
và truyền `-TepTuyChon <đường dẫn>` cho mọi script; script từ chối tệp nằm
trong repo:

```ini
[client]
host=localhost
user=bluemoon_sao_luu
password=mat_khau_cua_ban
```

### Bước 2. Chạy thử một lần

```powershell
powershell -ExecutionPolicy Bypass -File scripts\sao-luu.ps1
```

Mặc định ghi vào `%USERPROFILE%\BlueMoon\sao-luu`. Tham số: `-ThuMucDich`
(thư mục khác), `-SoBanGiu` (số bản giữ lại, mặc định 30), `-CoSoDuLieu`
(mặc định `bluemoon`), `-MysqlDump` (khi `mysqldump` không có trong PATH hay
`C:\Program Files\MySQL\MySQL Server 8.0\bin`), `-LoginPath` (mặc định
`bluemoon-sao-luu`), `-TepTuyChon`.

Mỗi lần chạy thêm một dòng vào `sao-luu.log` trong thư mục đích: thời điểm,
kết quả, tên tệp, dung lượng, thời gian chạy, ghi chú hoặc lỗi.

```text
2026-11-13 22:00:01 | THÀNH CÔNG | bluemoon-20261113-220001.sql | 1,2 MB | 0,9 s | xóa 1 bản cũ, giữ 30 bản
2026-11-14 22:00:01 | LỖI | bluemoon-20261114-220001.sql |  | 0,4 s | mysqldump trả mã 2: … Access denied …
```

Cách script giữ an toàn bản sao lưu:

- `mysqldump --single-transaction` đọc một ảnh nhất quán mà không khóa
  bảng, nên sao lưu được khi ứng dụng đang mở; kèm `--routines --triggers`
  (hai trigger của `nhat_ky`), `--set-gtid-purged=OFF` và
  `--default-character-set=utf8mb4`. Không dùng `--databases`, nên tệp
  không có `CREATE DATABASE`/`USE` và khôi phục được vào CSDL tên khác.
- Ghi ra tệp `.tmp` rồi mới đổi tên; chỉ coi là thành công khi `mysqldump`
  trả mã 0 **và** tệp kết thúc bằng dòng `-- Dump completed`. Lỗi thì xóa
  tệp dở, ghi dòng `LỖI` và thoát mã 1.
- Chỉ khi lần này thành công mới xóa bản cũ, giữ đúng `-SoBanGiu` bản mới
  nhất có tên dạng `bluemoon-yyyyMMdd-HHmmss.sql`; tệp khác trong thư mục
  không bị đụng tới.

**Nên chép thư mục sao lưu sang ổ khác hoặc máy khác** (ổ USB, ổ mạng, thư
mục đồng bộ đám mây): bản sao lưu nằm cùng ổ đĩa với MySQL thì hỏng ổ là mất
cả hai. Có thể đặt `-ThuMucDich` thẳng vào một ổ khác.

### Bước 3. Đăng ký lịch hằng ngày

```powershell
powershell -ExecutionPolicy Bypass -File scripts\dang-ky-lich-sao-luu.ps1
```

Tác vụ chạy lúc 22:00 (`-Gio 21:30` để đổi giờ) bằng tài khoản Windows đang
đăng nhập; máy tắt hoặc ngủ đúng giờ thì tác vụ chạy ngay khi máy bật lại.
Mặc định chỉ chạy khi tài khoản đang đăng nhập; thêm `-ChayKhiChuaDangNhap`
để chạy cả khi chưa đăng nhập (không lưu mật khẩu Windows). Các tham số
`-ThuMucDich`, `-SoBanGiu`, `-TepTuyChon`… được chuyển nguyên cho
`sao-luu.ps1`. Chạy lại script để đổi giờ hay tham số; gỡ bằng
`dang-ky-lich-sao-luu.ps1 -Xoa` (tệp sao lưu và log giữ nguyên).

Kiểm tra:

- `sao-luu.log`: mỗi ngày một dòng `THÀNH CÔNG`.
- Task Scheduler (`taskschd.msc`) → Task Scheduler Library → "BlueMoon – Sao
  lưu hằng ngày": cột Last Run Result là `0x0` khi thành công, `0x1` khi
  lỗi; tab History liệt kê từng lần chạy (bấm "Enable All Tasks History" ở
  khung bên phải nếu đang tắt). Chạy thử ngay: chuột phải → Run, hoặc
  `Start-ScheduledTask -TaskName 'BlueMoon – Sao lưu hằng ngày'`.

### Bước 4. Khôi phục thử

Làm định kỳ (vd mỗi tháng) để chắc bản sao lưu dùng được (AC-067). Chạy
ngay sau một lần sao lưu, lúc không ai dùng ứng dụng, để CSDL nguồn chưa
đổi so với tệp:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\sao-luu.ps1
powershell -ExecutionPolicy Bypass -File scripts\khoi-phuc-thu.ps1 -TepSaoLuu "$env:USERPROFILE\BlueMoon\sao-luu\bluemoon-20261113-220001.sql"
```

Script tạo lại CSDL `bluemoon_khoi_phuc_thu`, nạp tệp, rồi in bảng số dòng
từng bảng của `bluemoon` và bản khôi phục, tổng tiền phiếu thu chưa hủy, và
hai trigger `trg_nky_chan_sua`, `trg_nky_chan_xoa`. Dòng cuối là `KẾT QUẢ:
KHỚP` (mã thoát 0) hoặc `KẾT QUẢ: LỆCH` kèm phần lệch (mã thoát 1). Tài
khoản dùng để khôi phục cần quyền tạo CSDL và trigger: dùng login-path hay
tệp tùy chọn của root (`-LoginPath`, `-TepTuyChon`), không phải tài khoản
chỉ sao lưu ở bước 1. Xem xong thì xóa CSDL thử:
`DROP DATABASE bluemoon_khoi_phuc_thu;`.

### Khôi phục thật

Khi CSDL `bluemoon` hỏng hoặc mất dữ liệu:

1. **Dừng ứng dụng BlueMoon ở mọi máy** (tắt hẳn cửa sổ ứng dụng), để không
   máy nào ghi vào CSDL trong lúc khôi phục.
2. Chọn bản sao lưu: xem `sao-luu.log`, lấy bản `THÀNH CÔNG` gần nhất trước
   khi xảy ra sự cố. Nên khôi phục thử bản đó trước (bước 4).
3. Chạy bằng tài khoản root:
   ```powershell
   powershell -ExecutionPolicy Bypass -File scripts\khoi-phuc-thu.ps1 -TepSaoLuu "<tệp .sql>" -CsdlDich bluemoon -GhiDe -TepTuyChon "<tệp tùy chọn của root>"
   ```
   Script hỏi gõ lại `bluemoon` để xác nhận, rồi xóa và tạo lại CSDL
   `bluemoon` từ tệp; thiếu `-GhiDe` thì từ chối. Quyền `GRANT ALL ON
   bluemoon.*` của user ứng dụng vẫn còn sau khi xóa CSDL.
4. Mở lại ứng dụng ở một máy, kiểm tra vài hộ và phiếu thu gần nhất. Dữ liệu
   nhập sau thời điểm sao lưu không có trong bản khôi phục, phải nhập lại.

Không dùng script thì nạp tay: tạo CSDL `bluemoon` với `CHARACTER SET
utf8mb4 COLLATE utf8mb4_unicode_ci`, rồi trong `cmd`:
`mysql -u root -p --default-character-set=utf8mb4 bluemoon < bluemoon-….sql`.

> **Quyền tạo trigger khi khôi phục:** tệp sao lưu tạo lại hai trigger của
> `nhat_ky`. MySQL 8 bật binary log sẵn, nên tài khoản không có `SUPER` gặp
> lỗi 1419 "You do not have the SUPER privilege and binary logging is
> enabled". Khôi phục bằng root, hoặc root chạy một lần
> `SET PERSIST log_bin_trust_function_creators = 1;` (như mục 4). Sao lưu
> thì không cần quyền này, chỉ cần `TRIGGER` để đọc trigger.
