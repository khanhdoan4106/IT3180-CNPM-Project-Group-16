# Rà soát Sprint 1–2 và chuẩn bị Sprint 3

Ngày rà soát: 08/11/2026 · Phạm vi: `main` tại `f912c32` · Giai đoạn 1: chỉ
đọc và báo cáo, chưa sửa mã.

## Tóm tắt

- Build và test: `mvn -q clean verify` thành công, **141/141 test đạt**.
- CSDL: `schema.sql` và `seed.sql` chạy sạch trên MySQL 8.0.46 trống, số dòng
  đúng, tiếng Việt đúng UTF-8, chạy `seed.sql` lần hai không đổi gì.
- Kết quả: **3 Lỗi**, **13 Nên sửa**, **15 Ghi nhận**. Cả 3 lỗi đều chỉ
  thấy khi chạy với MySQL thật, vì test hiện chỉ dùng DAO giả.
- Kiến nghị: sửa 3 Lỗi và các mục Nên sửa N1–N4 trước khi tạo tag `v0.2.0`
  (Sprint 2 kết thúc 29/11, còn 3 tuần). Phần còn lại làm đầu Sprint 3.

## Xử lý sau khi duyệt

Scrum Master duyệt báo cáo ngày 08/11/2026. Các mục được chọn đã sửa trên
nhánh `fix/sprint-2-ra-soat`, mỗi mục một commit, kèm test tái hiện.

| Phát hiện | Xử lý | Test |
|---|---|---|
| L1 | Đã sửa: chỉ tính hộ chuyển đến không sau cuối tháng và khoản có hiệu lực trong tháng; không có gì để tính thì báo rõ tháng. Không giới hạn số tháng được chốt; chốt bù chỉ tính hộ còn đang ở lúc chốt (giả định 32) | 4 test `CongNoServiceTest`, `CongNoIT` |
| L2 | Đã sửa: điền sẵn bằng `SoTien.hienThi` | `SoTienTest`, `CongNoIT` |
| L3 | Đã sửa: `dd/MM/uuuu` + `ResolverStyle.STRICT` | `NgayUtilTest` |
| N1, N2 | Đã sửa: `LoiCsdl` tách mất kết nối / thiếu cấu hình / lỗi dữ liệu; `SoTien.doc` chặn số trên 1.000.000.000 đ | `LoiCsdlTest`, `SoTienTest`, `KhoanPhiServiceTest`, `CongNoIT` |
| N3 | Đã sửa: `canNhacChot`, `chot`, `dangKy` gọi `PhanQuyen`; `doiMatKhau` 3 tham số thành `private`; ngoại lệ ghi trong `architecture.md` | `CongNoServiceTest`, `TaiKhoanServiceTest` |
| N4 | Đã sửa: lớp `HopThoai` ("Đồng ý", "Hủy", "Đóng") cho mọi hộp thoại | Kiểm tra tay |
| N5 | Một phần: đã sửa các chữ ở hộp thoại và thông báo vừa đụng tới (mục 3, dòng 1–4, 8–9). Còn lại: "bắt buộc từ đủ 14 tuổi", "Biến động nhân khẩu gần đây", "Đã đổi vai trò…" | – |
| N8 | Đã sửa theo quyết định: chỉ hướng dẫn dựng lại CSDL trong `docs/setup.md`, không viết script nâng cấp | Đã thử đăng nhập trên CSDL `v0.1.0` |
| N9 | Một phần: thêm khung kiểm thử tích hợp `mvn verify -Pmysql` (`GiaoDichHoGiaDinhIT` cho AC-078, `CongNoIT`) và các unit test trên. Chưa thêm CI với MySQL | – |
| N12 | Một phần: backlog, README, traceability (thêm US-026). Còn `use-case.md`, `erd.md`, `ui.md` | – |
| N6, N7, N10, N11, N13, G1–G15 | Chưa làm, để Sprint 3 | – |

Sau khi sửa: 161 test đơn vị và 5 kiểm thử tích hợp (MySQL 8.0.46) đều
đạt.

## 1. Build và cơ sở dữ liệu

Kết quả lúc rà soát, trên `main` tại `f912c32`:

| Kiểm tra | Kết quả |
|---|---|
| `mvn -q clean verify` (Temurin 21, Maven 3.9.11) | Đạt. 141 test: TaiKhoan 28, NhanKhau 36, HoGiaDinh 30, DotCuTru 15, CongNo 13, KhoanPhi 11, MatKhauUtil 5, TrungKhoaException 2, DatabaseConfig 1 |
| CI trên `main` (GitHub Actions) | Ba lần chạy gần nhất (PR #24–#26) đều xanh |
| `schema.sql` rồi `seed.sql` trên CSDL trống | Không lỗi. Kiểm tra trên một máy chủ MySQL 8.0.46 tạm, dữ liệu riêng, cổng 3307, để không đụng CSDL `bluemoon` đang dùng |
| Số dòng | hộ 4, nhân khẩu 9, khoản thu 4, biến động 12 (đúng kỳ vọng); thêm đợt tạm trú/tạm vắng 3, tài khoản 1 |
| Mã hóa | Mọi bảng `utf8mb4_unicode_ci`. "Phí dịch vụ" lưu `5068C3AD...`, "Dữ liệu mẫu" lưu `44E1BBAF...`, đúng UTF-8 |
| Chạy `seed.sql` lần hai | Số dòng giữ nguyên, idempotent |
| Chạy DAO thật trên CSDL tạm | Dùng để xác nhận L1, L2, N2 và Ghi nhận G2 bên dưới |

Lưu ý: tên người trong `seed.sql` đang viết không dấu, nên phép thử mã hóa
trên chỉ kiểm được tên khoản thu và ghi chú. Xem N7.

## 2. Phát hiện

### Lỗi

#### L1. Chốt công nợ tháng cũ tính cả hộ chưa ở và khoản chưa áp dụng

- **Vị trí:** `src/main/java/com/bluemoon/service/CongNoService.java:99`,
  `:105`, `:111`
- **Mô tả:** Màn hình cho chọn và chốt 12 tháng gần nhất. Service chỉ chặn
  tháng tương lai, rồi lấy **mọi hộ đang ở hôm nay** và **mọi khoản bắt
  buộc đang thu hôm nay**. Không xét ngày chuyển đến của hộ, cũng không xét
  ngày bắt đầu của khoản thu.
- **Đã tái hiện trên MySQL:** thêm hộ B0909 chuyển đến ngày 05/11/2026, rồi
  chốt tháng 6/2025. Kết quả: B0909 có khoản phải nộp tháng 6/2025, và phí
  dịch vụ, phí quản lý (bắt đầu 01/01/2026) cũng được tính cho tháng 6/2025.
  Hộ bị ghi nợ một tháng mình chưa ở. Lỗi này ảnh hưởng thẳng tới tra cứu
  nợ và thu tiền ở Sprint 3.
- **Cách sửa:**
  - Chỉ tính hộ có `ngay_chuyen_den` không sau ngày cuối tháng (giả định 15:
    hộ đến giữa tháng vẫn tính đủ tháng).
  - Chỉ tính khoản có `tu_ngay` không sau ngày cuối tháng.
  - Hộ đã chuyển đi không có ngày chuyển đi nên không chốt bù đúng được cho
    tháng cũ. Vì vậy giới hạn chốt ở tháng hiện tại và tháng trước (để chốt
    bù khi quên). Các tháng cũ hơn chỉ xem.
  - Thêm test cho cả ba điều kiện.

#### L2. Giá trị điền sẵn khi sửa đơn giá và sửa khoản phải nộp bị chính hệ thống từ chối

- **Vị trí:**
  - `src/main/java/com/bluemoon/controller/KhoanThuController.java:100`
  - `src/main/java/com/bluemoon/controller/CongNoController.java:216`
  - `src/main/java/com/bluemoon/service/SoTien.java:15`
- **Mô tả:** Cột `DECIMAL(12,2)`/`DECIMAL(14,2)` trả về `5000.00`,
  `377500.00`. Controller điền nguyên chuỗi đó vào ô nhập bằng
  `toPlainString()`, còn `SoTien.doc` chỉ nhận số nguyên hoặc số nhóm bằng
  dấu chấm. Người dùng sửa một chữ số rồi Lưu, ví dụ `6000.00`, thì bị báo
  "Đơn giá phải là số nguyên không âm". Bấm Lưu không đổi gì cũng ra lỗi
  định dạng, thay vì "Số tiền không thay đổi".
- **Đã tái hiện trên MySQL:** DAO trả `DON_GIA_DB=5000.00`. Gọi
  `suaDonGia(..., "5000.00")` và `suaSoTien(..., "377500.00")` đều bị từ
  chối. Test không bắt được lỗi này vì DAO giả lưu `BigDecimal("5000")`.
- **Cách sửa:** Thêm `SoTien.nhap(BigDecimal)` trả về `5.000` (đúng dạng
  `SoTien.doc` nhận) và dùng ở hai chỗ điền sẵn. Thêm `SoTienTest` cho số
  có scale 2.

#### L3. Ngày không tồn tại bị đổi ngầm thành ngày khác

- **Vị trí:** `src/main/java/com/bluemoon/util/NgayUtil.java:15`
- **Mô tả:** `DateTimeFormatter.ofPattern("dd/MM/yyyy")` mặc định dùng
  `ResolverStyle.SMART`. Gõ tay `31/02/2026` sẽ lưu `28/02/2026`, gõ
  `31/04/2026` sẽ lưu `30/04/2026`, không báo gì (đã chạy thử). Lỗi này áp
  dụng cho mọi ô ngày: ngày sinh (quyết định quy tắc CCCD từ đủ 14 tuổi),
  ngày tạm trú/tạm vắng, ngày chuyển đến, thời gian khoản tự nguyện.
- **Cách sửa:** Dùng
  `DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)`.
  Khi đó ngày sai trả `null` và `saiDinhDang` báo lỗi như hiện có. Thêm
  test cho `BoChuyenNgay` (lớp này không cần khởi động JavaFX).

### Nên sửa

#### N1. Mọi lỗi CSDL đều báo "Không kết nối được", kèm đường dẫn tài liệu cho lập trình viên

- **Vị trí:** hằng `LOI_CSDL` ở `HoGiaDinhController.java:56` và ở
  `NhanKhauController`, `KhoanThuController`, `CongNoController`,
  `TaiKhoanController`, `DangNhapController`
- **Mô tả:**
  - Câu "Không kết nối được cơ sở dữ liệu. Xem docs/setup.md" hiện cho cả
    `CauHinhException` (mất kết nối thật) lẫn mọi `DaoException`, ví dụ số
    vượt khoảng (N2) hay thiếu cột (N8). Người dùng và người hỗ trợ bị dẫn
    sai hướng.
  - "docs/setup.md" vô nghĩa với Ban quản trị và Thủ quỹ.
  - Cùng hằng và cặp `baoLoi`/`baoThanhCong` đang lặp lại ở 5 controller.
- **Cách sửa:** Gom vào một lớp dùng chung trong `controller`, tách hai thông
  báo:
  - `CauHinhException`: "Không kết nối được cơ sở dữ liệu. Kiểm tra mạng
    hoặc báo người quản trị hệ thống."
  - `DaoException`: "Không lưu được dữ liệu do lỗi cơ sở dữ liệu. Thử lại
    hoặc báo người quản trị hệ thống."

#### N2. Số tiền và đơn giá không có giới hạn trên

- **Vị trí:** `SoTien.java:15`, `KhoanPhiService.java:75`, `:132`,
  `CongNoService.java:200`
- **Mô tả:** Mẫu `\d+` nhận số bao nhiêu chữ số cũng được. Phí quản lý chỉ
  có mức tối thiểu, nên `99999999999999` qua được Service. Đến MySQL thì vượt
  `DECIMAL(12,2)` và thành `DaoException` (đã chạy thử), rồi hiện "Không kết
  nối được CSDL". Tương tự với số tiền mỗi hộ của khoản tự nguyện và sửa
  khoản phải nộp (`DECIMAL(14,2)`).
- **Cách sửa:** Đặt mức trần nghiệp vụ trong `SoTien.doc`, ví dụ tối đa
  1.000.000.000 đ, kèm thông báo "… tối đa 1.000.000.000 đ". Thêm test.

#### N3. Một số hàm public của Service không qua `PhanQuyen.yeuCau`

- **Vị trí và mô tả:**
  - `CongNoService.java:79` `canNhacChot()` đọc CSDL mà không nhận
    `TaiKhoan`. Nên nhận `nguoi` và cho cả hai vai trò.
  - `TaiKhoanService.java:81` `dangKy` tự so vai trò, bỏ qua kiểm tra
    `isHoatDong()` mà `PhanQuyen` có. Thông báo riêng "Chỉ Quản trị viên mới
    được đăng ký tài khoản" cũng khác thông báo chung. Nên đổi sang
    `PhanQuyen.yeuCau(nguoiTao, QUAN_TRI)`.
  - `TaiKhoanService.java:121` `doiMatKhau` 3 tham số là public, không kiểm
    tra quyền, không kiểm tra nhập lại. Chỉ test và bản 4 tham số gọi hàm
    này. Nên đổi thành `private`, chuyển test sang bản 4 tham số.
- **Ngoại lệ hợp lý, cần ghi vào `architecture.md`:** `dangNhap`,
  `lamMoiPhien` (chưa có phiên) và `thangHienTai` (không đọc dữ liệu).
- **Tài liệu:** `docs/design/class-diagram.md:79` đang ghi "Mọi hàm của
  Service … kiểm tra quyền bằng `PhanQuyen.yeuCau`". Câu này sai cho tới
  khi sửa xong mục này.

#### N4. Nút trên hộp thoại hiện tiếng Anh

- **Vị trí:** `HoGiaDinhController.java:239`, `NhanKhauController.java:291`,
  `CongNoController.java:162` (`ButtonType.OK`, `ButtonType.CANCEL`),
  `BangLichSu.java:65` (`ButtonType.CLOSE`), các `Dialog` và `ChoiceDialog`
  trong `CongNoController` và `TaiKhoanController`
- **Mô tả:** JavaFX 21 không có gói ngôn ngữ tiếng Việt (chỉ có de, es, fr,
  it, ja, ko, pt_BR, sv, zh). Vì vậy các nút hiện "OK", "Cancel", "Close"
  giữa giao diện tiếng Việt. Điều này trái quy ước "chữ hiển thị tiếng Việt
  có dấu" và NFR-008.
- **Cách sửa:** Khai báo một lần các `ButtonType` tiếng Việt ("Đồng ý",
  "Hủy", "Đóng") trong lớp dùng chung ở N1 và dùng ở mọi hộp thoại.

#### N5. Chữ hiển thị chưa thống nhất thuật ngữ

Chi tiết và chữ đề xuất ở [mục 3](#3-giao-diện-và-chữ-hiển-thị).

#### N6. Đổi vai trò và khóa tài khoản không hỏi xác nhận

- **Vị trí:** `TaiKhoanController.java:154` (`onDoiVaiTro`), `:173`
  (`onKhoa`)
- **Mô tả:** Một cú bấm nhầm đổi ngay Thủ quỹ thành Quản trị viên (cấp quyền
  quản lý cư dân và tài khoản), hoặc khóa người đang làm việc. Trong khi đó
  các thao tác ít rủi ro hơn như chuyển đi, chốt công nợ lại có hỏi xác
  nhận.
- **Cách sửa:** Thêm hộp xác nhận. Ví dụ: "Đổi vai trò của thuquy01 từ Thủ
  quỹ thành Quản trị viên?", "Khóa tài khoản thuquy01? Người này sẽ bị đăng
  xuất ở lần thao tác tiếp theo."

#### N7. Dữ liệu mẫu viết không dấu, lệch với `docs/setup.md`

- **Vị trí:** `database/seed.sql:26–40`, `:50–55`, `:66–69`;
  `docs/setup.md:54–56`
- **Mô tả:** `seed.sql` có "Tran Van Hung", "Hoang Van Nam", "Thue nha lam
  viec". Trong khi đó bảng kiểm tra demo trong `setup.md` mong thấy "Hoàng
  Văn Nam", "Vũ Văn Toàn", "Đỗ Thị Hoa". Tên không dấu cũng làm phép thử mã
  hóa (NFR-008) mất tác dụng với dữ liệu nhân khẩu.
- **Cách sửa:** Viết có dấu toàn bộ tên, địa chỉ, lý do. Không ảnh hưởng
  idempotent vì khóa chống trùng là số CCCD. CSDL đã có thì phải dựng lại
  mới thấy tên mới.

#### N8. CSDL dựng từ Sprint 1 không chạy được với bản Sprint 2

- **Vị trí:** `database/schema.sql:15`; `docs/setup.md:25`
- **Mô tả:** Cột `tai_khoan.so_dien_thoai` được thêm vào câu `CREATE TABLE
  IF NOT EXISTS` sẵn có. Trên CSDL cũ, chạy lại `schema.sql` không thêm cột.
  Câu đăng nhập đọc cột này nên lỗi, và màn hình đăng nhập báo "Không kết
  nối được cơ sở dữ liệu" (xem N1). `setup.md` có hướng dẫn `DROP DATABASE`
  nhưng không ai biết phải làm khi kéo bản mới về.
- **Cách sửa:** Ghi rõ "Bản này đổi cấu trúc bảng, cần dựng lại CSDL" trong
  release notes `v0.2.0`. Thêm một dòng vào mục "Lỗi thường gặp" của
  `setup.md`. Từ Sprint 3, mỗi lần đổi bảng đã có thì kèm script nâng cấp,
  ví dụ `database/nang-cap/0.3.0.sql`.

#### N9. Thiếu test cho các nhánh quan trọng

- **Unit test còn thiếu:**
  - `BienDongService` không có test riêng, chưa có test Thủ quỹ bị chặn xem
    lịch sử.
  - `SoTien` không có test: số có `đ`, dấu cách, scale 2 (L2), số quá lớn
    (N2).
  - Chưa có test chốt tháng cũ và hộ chuyển đến sau tháng chốt (L1).
  - Chưa có test ngày không tồn tại (L3).
  - `KhoanPhiService`: chưa có test Thủ quỹ xem danh mục, sửa đơn giá khoản
    không theo diện tích, khoản không tồn tại.
- **Tầng DAO:** chưa có test nào chạy với MySQL. Cả ba Lỗi ở trên đều nằm
  ngoài tầm DAO giả. AC-078 (lỗi giữa chừng thì không ghi lịch sử) hiện chỉ
  kiểm được bằng đọc mã.
- **Cách sửa:** Thêm unit test cho các mục trên. Thêm một bộ test tích hợp
  nhỏ (`*IT`, chạy bằng `mvn verify -Pmysql`, đọc `BLUEMOON_DB_*`, tự bỏ qua
  khi không có biến) cho mỗi DAO. Trên CI dùng service container `mysql:8`.

#### N10. Trùng lặp đáng gộp

- Thông báo "Số CCCD … đã tồn tại" viết ba lần: `HoGiaDinhService.java:120`,
  `NhanKhauService.java:283`, `LoiTrung.java:32`. Gộp vào một hàm của
  `LoiTrung` để thông báo luôn giống nhau (đúng mục đích ghi ở Javadoc của
  `LoiTrung`).
- `CongNoController.java:231` `tomTat` tự cộng tiền và đếm hộ, trùng
  `KetQuaTinhCongNo.tongTien()`/`soHo()`. Đây cũng là tính toán nghiệp vụ
  nằm ở Controller. Nên dùng lại hàm của model.
- `LOI_CSDL`, `baoLoi`, `baoThanhCong` lặp ở 5 controller: gộp cùng N1.

#### N11. Lỗi không lường trước không hiện gì cho người dùng

- **Vị trí:** `src/main/java/com/bluemoon/App.java:15`
- **Mô tả:** Controller đã bắt `NghiepVuException`, `CauHinhException`,
  `DaoException`. Các lỗi khác thì chỉ in ra console, người dùng thấy nút
  bấm "không có tác dụng". Ví dụ: `IllegalStateException` khi tải FXML,
  `NghiepVuException` trong `taiLai()` của màn Hộ gia đình và Nhân khẩu (hai
  hàm này không bắt loại lỗi đó).
- **Cách sửa:** Đặt `Thread.setDefaultUncaughtExceptionHandler` trong
  `App.start` để hiện hộp thoại "Có lỗi không mong muốn, vui lòng thử lại".

#### N12. Tài liệu lệch với mã và trạng thái thực

Danh sách ở [mục 4](#4-tài-liệu).

#### N13. `ui.md` mô tả thiết kế Figma nhưng không ghi phần nào đã làm

- **Vị trí:** `docs/design/ui.md:13`, `:19`, `:62`, `:84`, `:106`
- **Mô tả và cách sửa:** Mỗi điểm dưới đây cần chọn hoặc làm ngay, hoặc ghi
  rõ "Sprint 4 (TS-004)":
  - Màn chức năng không phải cửa sổ 1120×645 mà nằm trong Trang chính (cửa
    sổ phóng to).
  - Lỗi nhập liệu dồn vào một nhãn chung, không hiện dưới từng ô.
  - Phông Segoe UI 14 px, không phải Be Vietnam Pro 16 px. CSS mới có 5
    biến màu trên 9.
  - Bộ lọc Tất cả/Bắt buộc/Tự nguyện ở màn Khoản thu chưa có.
  - Hộp thoại sửa khoản phải nộp có một ô, chưa có "tổng mới", chưa khóa ô
    khi đã thu (AC-026).

  Đề xuất: làm bộ lọc khoản thu ngay (nhỏ), các mục còn lại ghi Sprint 4.

### Ghi nhận

| # | Vị trí | Nội dung | Đề xuất |
|---|---|---|---|
| G1 | `TrangChinhController.java:140` | Phiên chỉ được đọc lại khi mở màn hình. Tài khoản vừa bị khóa vẫn thao tác được trên màn hình đang mở cho tới lần chuyển màn hình | Chấp nhận theo giả định 17; ghi rõ trong giả định |
| G2 | `tai_khoan` (collation `_ci`) | Đăng nhập không phân biệt hoa thường: `ADMIN` vào được tài khoản `admin` (đã chạy thử). Đăng ký `Admin` cũng bị báo trùng, nên dữ liệu vẫn nhất quán | Chấp nhận, ghi vào giả định |
| G3 | `BienDongDaoImpl.java:64` và `:32` | Một chỗ lấy ngày theo máy chạy ứng dụng (`LocalDate.now()`), chỗ kia theo máy chủ MySQL (`CURDATE()`). Nếu hai máy lệch múi giờ thì sát nửa đêm có thể lệch một ngày; cũng không test được bằng `Clock` | Truyền ngày từ Service (đã có `Clock`) xuống DAO khi tiện sửa |
| G4 | `HoGiaDinhDaoImpl.java:34` | Sửa ngày chuyển đến của hộ không đổi dòng `CHUYEN_DEN` trong lịch sử | Đúng nguyên tắc "lịch sử chỉ thêm"; chấp nhận |
| G5 | `NhanKhauDaoImpl.java:265` | `doiChuHo` khóa hai dòng nhưng không kiểm tra lại trạng thái trong giao dịch. Hai máy thao tác cùng lúc có thể đặt người vừa chuyển đi làm chủ hộ. Bất biến "một chủ hộ" vẫn được `UNIQUE (chu_ho_cua)` giữ | Thêm điều kiện `trang_thai = 'DANG_O'` vào câu UPDATE khi sửa mã gần đó |
| G6 | `CongNoController.java:122` | "Đã chốt" được suy từ danh sách khoản khác rỗng. Tháng có `dot_thu` nhưng 0 khoản (không có hộ nào) sẽ hiện "chưa chốt" | Dùng `daChot` |
| G7 | `CongNoController.java:150` | "Đang xem trước, chưa lưu." không đặt lại kiểu chữ, nên sau một lỗi sẽ hiện màu đỏ | Sửa cùng N1 |
| G8 | `TaiKhoanService.java:21` | Javadoc mồ côi (hai khối `/** */` liền nhau, khối đầu thuộc `TEN_HOP_LE`) | Chuyển xuống đúng chỗ |
| G9 | `khoan_phi.trang_thai` | Không có chức năng đặt `DA_DONG`. Khoản tự nguyện hết hạn vẫn hiện "Đang thu" | Xử lý ở Sprint 3 (AC-022 "khoản đang mở"), xem 5.4 |
| G10 | `TrangChinhController.java:59`, `seed.sql:10` | Tài khoản mẫu có họ tên "Quản trị viên" nên lời chào thành "Xin chào, Quản trị viên (Quản trị viên)" | Đặt họ tên mẫu là tên người, sửa cùng N7 |
| G11 | `seed.sql:91–97` | Người tạm trú mẫu có dòng "Chuyển đến" từ ngày hộ đến (2023), trong khi đợt tạm trú bắt đầu 60 ngày trước | Lấy ngày bắt đầu đợt đầu tiên cho người tạm trú |
| G12 | `KhoThuPhiGia.java:134` | DAO giả đã trừ khoản có phiếu thu, còn SQL thật (`CongNoDaoImpl.java:50`) chưa trừ. `CongNoServiceTest` đạt nhưng không phản ánh hành vi thật | Sửa SQL ở Sprint 3 (5.4) |
| G13 | `Database.java:61` | Mỗi lần gọi DAO mở một kết nối mới và đọc lại file cấu hình. Ô tìm kiếm truy vấn theo từng phím | Đủ nhanh với quy mô một tòa nhà (AC-032); chưa cần pool |
| G14 | `seed.sql` (`INSERT IGNORE`) | `IGNORE` bỏ qua cả lỗi CHECK, không chỉ lỗi trùng; dữ liệu mẫu sai sẽ bị bỏ qua mà không báo | Chấp nhận; kiểm số dòng sau khi sửa seed |
| G15 | `pom.xml`, tag `v0.1.0` | Tag `v0.1.0` mang version `0.1.0-SNAPSHOT` | Giữ cách làm cũ cho nhất quán, hoặc đặt `0.2.0` trước khi tag rồi nâng `0.3.0-SNAPSHOT` |

### Đối chiếu ràng buộc `schema.sql` với Service

| Ràng buộc CSDL | Kiểm tra ở Service | Khớp |
|---|---|---|
| `uq_ho_ma_dang_o` | `kiemTraTrungMa` + `LoiTrung`: cùng câu "Mã căn hộ … đã có hộ đang ở" | Có |
| `chk_ho_dien_tich`, `chk_ho_tang` | Diện tích > 0 và ≤ 450, tối đa 2 số lẻ; tầng 1–30 | Có (Service chặt hơn) |
| `uq_nk_cccd_dang_o` | `tonTaiCccd` + `LoiTrung`: cùng câu, viết ở 3 nơi | Có (N10) |
| `uq_nk_chu_ho` | `coChuHo` + `LoiTrung` → `LOI_HAI_CHU_HO` | Có |
| `chk_ttv_ngay` | `docDot`: "Ngày kết thúc … không được trước ngày bắt đầu" | Có |
| Chống chồng ngày (không có ràng buộc CSDL) | Service + `SELECT … FOR UPDATE` trong DAO, cùng `LOI_CHONG_NGAY` | Có |
| `uq_kp_ten_dot` | `tonTaiTen` + bắt `TRUNG_TEN_DOT`, cùng câu | Có |
| `chk_kp_don_gia`, `chk_kp_bat_buoc`, `chk_kp_ngay` | Số tiền 0 hoặc trống → `KHONG_CO_DINH`, `don_gia` null; chỉ tạo khoản tự nguyện; bắt buộc có ngày kết thúc | Có |
| `uq_dt_thang`, `chk_dt_ngay_dau_thang` | `thang.atDay(1)` + `ON DUPLICATE KEY` | Có |
| `uq_pn_dot_ho_khoan`, `chk_pn_so_tien` | `NOT EXISTS` trong giao dịch có khóa dòng `dot_thu`; `SoTien.doc` không nhận số âm | Có |
| Độ dài `VARCHAR` (họ tên 100, mã 20, địa chỉ 200, ghi chú 200, tên đăng nhập 50) | Kiểm tra đủ; ghi chú biến động được cắt ở 200 | Có |
| `DECIMAL(12,2)` / `DECIMAL(14,2)` | Không có giới hạn trên | **Không** (N2) |
| AC-029 "hộ chưa có diện tích" | `dien_tich NOT NULL CHECK > 0` nên nhánh này không xảy ra; Service vẫn giữ nhánh phòng hờ | Có (đã ghi trong `erd.md`) |

### Giao dịch và SQL

- Thêm hộ, thêm nhân khẩu (kèm đợt tạm trú), ghi đợt, chuyển đi (hộ và
  thành viên), đổi chủ hộ, chốt công nợ đều chạy trong **một giao dịch**.
- Mỗi chỗ đều có `setAutoCommit(false)`, `rollback()` khi gặp
  `SQLException | RuntimeException`, và `try-with-resources` đóng kết nối.
  Lịch sử biến động ghi trên cùng kết nối.
- `ON DELETE CASCADE` không dùng; không có `DELETE` nào trong mã.
- **SQL:** mọi câu lệnh dùng `PreparedStatement`. Không có `createStatement`
  hay câu SQL ghép từ dữ liệu người dùng. Từ khóa `LIKE` được thoát `%`,
  `_`, `\`.
- **Mã chết:** không còn `TODO`, `FIXME`, `System.out`, `printStackTrace`.
  Chỉ có `daCoPhieuThu` trả `false` có chủ đích chờ Sprint 3. Các overload
  3 tham số của `them` vẫn được test dùng.

## 3. Giao diện và chữ hiển thị

| Vị trí | Hiện tại | Đề xuất | Lý do |
|---|---|---|---|
| `CongNoController.java:210` | "Sửa khoản thu – A0601" | "Sửa khoản phải nộp – A0601" | Thuật ngữ: khoản thu là mục danh mục, khoản phải nộp là của hộ |
| `NhanKhauController.java:289`, `:302` | Hỏi "Đánh dấu X đã rời hộ?", báo "X đã được đánh dấu chuyển đi" | Hỏi "Đánh dấu X đã chuyển đi khỏi hộ?", báo "Đã đánh dấu X chuyển đi" | Một thao tác dùng một tên; thuật ngữ "Đã chuyển đi" |
| `NhanKhauController.java:256–258` | Tiêu đề "Đặt X làm chủ hộ A0601", nhãn "Chủ hộ hiện tại:" đứng trước danh sách quan hệ | "Đặt X làm chủ hộ căn A0601" / "Quan hệ của <chủ hộ cũ> với chủ hộ mới:" | Nhãn hiện tại đọc như đang hỏi tên chủ hộ |
| `KhoanThuController.java:128` | "Ngày không đúng định dạng dd/mm/yyyy" | "Ngày phải có dạng dd/mm/yyyy" | Các màn khác đã dùng câu này |
| `ho-gia-dinh.fxml:116`, `nhan-khau.fxml:146` | "bắt buộc từ 14 tuổi" | "bắt buộc từ đủ 14 tuổi" | Đúng quy tắc trong Service và giả định 5 |
| `trang-chinh.fxml:44` | "Biến động dân cư gần đây" | "Biến động nhân khẩu gần đây" | Cùng tên chức năng US-027 |
| `TaiKhoanController.java:164` | "thuquy01 giờ là Quản trị viên" | "Đã đổi vai trò của thuquy01 thành Quản trị viên" | Cùng mẫu "Đã …" của các thông báo khác |
| Mọi hộp thoại | "OK", "Cancel", "Close" | "Đồng ý", "Hủy", "Đóng" | N4 |
| Mọi màn hình | "… Xem docs/setup.md" | Hai câu ở N1 | N1 |

Phần còn lại đã ổn: tiêu đề, nhãn bắt buộc có `*`, chỗ nhắc định dạng,
thông báo trống của bảng, các câu xác nhận chốt và chuyển đi. Thuật ngữ
"Thủ quỹ", "khoản thu", "chốt công nợ", "khoản phải nộp" dùng đúng
`docs/thuat-ngu.md`, trừ các dòng trong bảng trên.

## 4. Tài liệu

### Trạng thái story Sprint 2 so với mã

| Story | AC | Đánh giá | Trạng thái đề xuất |
|---|---|---|---|
| US-005 | AC-015–018 có test; AC-017 chặn hẳn (giả định 14) | Đủ, sau khi sửa L2 | Hoàn thành |
| US-006 | AC-019–021 có test | Đủ | Hoàn thành |
| US-008 | AC-025 có test (L2 ảnh hưởng); AC-026 mới có khung, `daCoPhieuThu` luôn `false` | **Thiếu AC-026** | "Đang làm – AC-026 xong cùng phiếu thu ở Sprint 3" |
| US-009 | AC-027 (L1), AC-028, AC-029 có test | Đủ, sau khi sửa L1 | Hoàn thành |
| US-022 | AC-060–062: ẩn menu + kiểm tra ở Service, test Thủ quỹ bị chặn ở mỗi Service trừ `BienDongService` | Đủ, nên bổ sung N3 và N9 | Hoàn thành |
| US-023 | AC-063–065 có test | Đủ | Hoàn thành |
| US-026 | AC-073–075 có test | Đủ | Hoàn thành |
| US-027 | AC-076, 077, 079 có test; AC-078 chỉ kiểm bằng đọc mã | Đủ, nên thêm test tích hợp (N9) | Hoàn thành |

Sprint 1 (US-001–003, 020, 021, TS-003) vẫn khớp với mã.

### Tài liệu cần cập nhật

| File:dòng | Hiện tại | Sửa thành |
|---|---|---|
| `README.md:34` | Sprint 2 "Chưa bắt đầu"; nội dung thiếu đổi chủ hộ, lịch sử biến động | "Hoàn thành", thêm hai chức năng, link Release `v0.2.0` |
| `README.md:91` | Danh sách FXML thiếu `khoan-thu`, `cong-no` | Bổ sung |
| `08-product-backlog.md:19`, `:37–38`, `:46–50`, `:84–85` | Sprint 2 và 8 story "Chưa bắt đầu" | Theo bảng trạng thái ở trên |
| `09-traceability-matrix.md` | Không có dòng US-026 (AC-073–075) | Thêm dòng (nguồn: giả định 26, Bộ bài tập ch.7) |
| `use-case.md:66` | US-005 và US-009 chỉ nối với Thủ quỹ | Đổi đơn giá (US-005) nối Ban quản trị; chốt công nợ (US-009), sửa khoản phải nộp (US-008) nối cả hai vai trò, đúng bảng quyền ở `architecture.md` |
| `use-case.md:89–90` | Bảng trạng thái thiếu US-026, US-027; cột "Đã xong (Sprint 1)" | Thêm, và đánh dấu Sprint 2 đã xong |
| `use-case.md:73` | "cần chốt lại … (Sprint 2–3)" | Chốt cùng thiết kế Sprint 3 |
| `erd.md:157` | "`DOI_CHU_HO` để sẵn cho US-026" | US-026 đã làm; thêm đổi chủ hộ vào danh sách thao tác ghi lịch sử ở dòng 122 |
| `class-diagram.md:79` | "Mọi hàm của Service … `PhanQuyen.yeuCau`" | Đúng sau N3; ghi các ngoại lệ |
| `architecture.md` (Phân quyền) | Không nêu hàm nào không cần phiên | Ghi `dangNhap`, `lamMoiPhien` |
| `ui.md` | Xem N13 | Thêm cột "Đã làm / Sprint 4" |
| `setup.md` | Thiếu dòng "Đăng nhập báo không kết nối được sau khi kéo bản mới" | Thêm (N8) |

### Mermaid và liên kết

- Năm sơ đồ Mermaid (`class-diagram.md` ×2, `erd.md`, `use-case.md`,
  `workflow.md`) đều render thành công bằng mermaid-cli 11.4.
- Mọi liên kết nội bộ và anchor trong `README.md`, `docs/`, `.github/` trỏ
  đúng.
- Ba liên kết dạng `../../releases/tag/v0.1.0` và `../blob/main/...` chỉ
  hoạt động trên GitHub, đúng mục đích.
- `docs/setup.md` làm theo được từ máy mới (đã chạy lại các bước CSDL). Chỗ
  vướng duy nhất là N8 với người đã có CSDL Sprint 1.

## 5. Chuẩn bị Sprint 3

Mục này đã cập nhật theo các quyết định của Scrum Master ngày 08/11/2026,
ghi thành giả định 27–31 trong
[11-chot-va-gia-dinh.md](../requirements/11-chot-va-gia-dinh.md). Đây vẫn
là thiết kế, chưa có mã Sprint 3.

### 5.1 Căn cứ

- **`erd.md` (mục "Bảng dự kiến"):**
  - Chi tiết phiếu trỏ tới `phai_nop` (khoản bắt buộc, đóng đủ) hoặc
    `khoan_phi` (khoản tự nguyện, số bất kỳ kể cả 0 đ).
  - "Đã đóng" nghĩa là có dòng chi tiết thuộc phiếu chưa hủy.
  - Khi lập phiếu, khóa các dòng liên quan bằng `SELECT … FOR UPDATE`.
- **Giả định có từ trước:**
  - 21: tra cứu tìm được cả hộ đã chuyển đi còn nợ.
  - 23: khoản bắt buộc đóng đủ; ghi 0 đ là "không đóng", khác "chưa thu".
  - 24: tổng thu tháng tính theo ngày thu trên phiếu chưa hủy.
  - 25: nợ quá 3 tháng tính theo tháng chốt còn khoản chưa đóng.
- **Giả định mới (đã chốt):**
  - 27: Sprint 3 giữ 28 SP, gồm US-004 và US-015; in bằng `PrinterJob`;
    Excel/PDF (cả AC-014) để Sprint 4.
  - 28: Thủ quỹ hủy phiếu mình lập, Quản trị viên hủy mọi phiếu; chỉ trong
    ngày lập; bắt buộc lý do; phiếu hủy giữ lại.
  - 29: đóng góp nhiều lần cho một quỹ, tổng là cộng các lần; 0 đ chỉ ghi
    khi hộ chưa có lần nào cho quỹ đó.
  - 30: phiếu chỉ gồm khoản 0 đ vẫn lưu và có số, ghi "Không thu tiền",
    không in.
  - 31: số `PT-<năm>-<5 chữ số>`, đánh lại mỗi năm, liên tục, cấp từ bảng
    đếm theo năm; phiếu hủy giữ số.

### 5.2 Schema đề xuất

Đã chạy thử trên MySQL 8.0.46 (sau `schema.sql` và `seed.sql` hiện tại):
tạo bảng không lỗi, cấp số ra `PT-2026-00001`, giao dịch hoàn tác không làm
nhảy số. Các ràng buộc chặn đúng: thu trùng một khoản phải nộp, phiếu có
tiền mà thiếu hình thức, năm lệch ngày lập, trùng số biên lai, hủy thiếu
người hủy hoặc lý do. Hủy xong thì khoản đó thu lại được.

```sql
-- Bộ đếm số biên lai theo năm (giả định 31): khóa dòng trong cùng giao
-- dịch lưu phiếu nên số liên tục, không nhảy khi hoàn tác.
CREATE TABLE IF NOT EXISTS bo_dem_bien_lai (
    nam     SMALLINT NOT NULL PRIMARY KEY,
    so_cuoi INT      NOT NULL DEFAULT 0,
    CONSTRAINT chk_bdbl_so_cuoi CHECK (so_cuoi BETWEEN 0 AND 99999)
);

-- US-010 – US-014, US-007: phiếu thu. Số biên lai PT-<nam>-<so 5 chữ số>.
-- Hủy chỉ đổi trạng thái, phiếu giữ số (AC-043, giả định 28, 31).
CREATE TABLE IF NOT EXISTS phieu_thu (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nam           SMALLINT       NOT NULL,
    so            INT            NOT NULL,
    ho_id         INT            NOT NULL,
    tong_tien     DECIMAL(14, 2) NOT NULL,
    hinh_thuc     ENUM('TIEN_MAT', 'CHUYEN_KHOAN') NULL,
    nguoi_thu     INT            NOT NULL,
    thoi_diem     DATETIME       NOT NULL,
    so_lan_in     INT            NOT NULL DEFAULT 0,
    da_huy        BOOLEAN        NOT NULL DEFAULT FALSE,
    nguoi_huy     INT            NULL,
    thoi_diem_huy DATETIME       NULL,
    ly_do_huy     VARCHAR(200)   NULL,
    CONSTRAINT fk_pt_ho FOREIGN KEY (ho_id) REFERENCES ho_gia_dinh (id),
    CONSTRAINT fk_pt_nguoi_thu FOREIGN KEY (nguoi_thu)
        REFERENCES tai_khoan (id),
    CONSTRAINT fk_pt_nguoi_huy FOREIGN KEY (nguoi_huy)
        REFERENCES tai_khoan (id),
    CONSTRAINT uq_pt_so_bien_lai UNIQUE (nam, so),
    CONSTRAINT chk_pt_so CHECK (so BETWEEN 1 AND 99999),
    CONSTRAINT chk_pt_nam CHECK (nam = YEAR(thoi_diem)),
    CONSTRAINT chk_pt_tong_tien CHECK (tong_tien >= 0),
    CONSTRAINT chk_pt_hinh_thuc CHECK (tong_tien = 0 OR hinh_thuc IS NOT NULL),
    CONSTRAINT chk_pt_huy CHECK (
        (da_huy = FALSE AND nguoi_huy IS NULL AND thoi_diem_huy IS NULL
            AND ly_do_huy IS NULL)
        OR (da_huy = TRUE AND nguoi_huy IS NOT NULL
            AND thoi_diem_huy IS NOT NULL AND ly_do_huy IS NOT NULL
            AND ly_do_huy <> '')),
    INDEX idx_pt_ho (ho_id, thoi_diem),
    INDEX idx_pt_thoi_diem (thoi_diem)
);

-- Mỗi dòng một khoản trong phiếu: khoản phải nộp (phai_nop_id) hoặc một
-- lần đóng góp tự nguyện (phai_nop_id NULL). con_hieu_luc chép da_huy của
-- phiếu (đổi trong cùng giao dịch hủy) để cột sinh chặn thu trùng một
-- khoản phải nộp ở tầng CSDL, như cccd_dang_o chép trang_thai.
CREATE TABLE IF NOT EXISTS phieu_thu_chi_tiet (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    phieu_thu_id    INT            NOT NULL,
    khoan_phi_id    INT            NOT NULL,
    phai_nop_id     INT            NULL,
    so_tien         DECIMAL(14, 2) NOT NULL,
    con_hieu_luc    BOOLEAN        NOT NULL DEFAULT TRUE,
    phai_nop_da_thu INT AS (IF(con_hieu_luc, phai_nop_id, NULL)) STORED,
    CONSTRAINT fk_ptct_phieu FOREIGN KEY (phieu_thu_id)
        REFERENCES phieu_thu (id),
    CONSTRAINT fk_ptct_khoan FOREIGN KEY (khoan_phi_id)
        REFERENCES khoan_phi (id),
    CONSTRAINT fk_ptct_phai_nop FOREIGN KEY (phai_nop_id)
        REFERENCES phai_nop (id),
    CONSTRAINT uq_ptct_phai_nop_da_thu UNIQUE (phai_nop_da_thu),
    CONSTRAINT chk_ptct_so_tien CHECK (so_tien >= 0),
    INDEX idx_ptct_khoan (khoan_phi_id)
);
```

**Lý do từng quyết định:**

- **`nam` + `so` thay cho một cột số (giả định 31).** Mỗi năm đánh lại từ
  00001 nên khóa duy nhất là `(nam, so)`. Hiển thị bằng
  `String.format("PT-%d-%05d", nam, so)`, không lưu chuỗi để khỏi lệch với
  hai cột. `chk_pt_nam` giữ năm khớp ngày lập.
- **Bảng đếm `bo_dem_bien_lai` thay `AUTO_INCREMENT`.** Trong giao dịch lưu
  phiếu: `INSERT … ON DUPLICATE KEY UPDATE` để có dòng của năm, rồi
  `SELECT so_cuoi … FOR UPDATE`, rồi tăng `so_cuoi`. Hoàn tác thì số trả
  lại, nên không có khoảng trống; hai máy lập phiếu cùng lúc thì chạy nối
  tiếp. Đến 99.999 thì báo hết số của năm.
- **Thứ tự khóa:** dòng `ho_gia_dinh` trước, bộ đếm sau cùng. Một quy tắc
  khóa cho cả khoản bắt buộc lẫn đóng góp tự nguyện (kiểm tra "0 đ chỉ khi
  chưa có lần nào", giả định 29) tránh được deadlock. Khóa bộ đếm cuối cùng
  để giữ khóa ngắn nhất.
- **`thoi_diem` do Service truyền xuống** (từ `Clock`, như các Service
  khác), không để CSDL tự điền. Như vậy năm của số biên lai, điều kiện "hủy
  trong ngày" (AC-042) và tổng thu tháng (giả định 24) cùng dùng một đồng
  hồ, và test được.
- **`hinh_thuc` được NULL chỉ khi `tong_tien = 0`.** Phiếu "Không thu tiền"
  (giả định 30) không có hình thức thanh toán; phiếu có tiền vẫn bắt buộc
  (AC-038).
- **`phai_nop_da_thu` UNIQUE:** CSDL chặn hai phiếu còn hiệu lực cùng thu
  một khoản phải nộp, kể cả khi Service có lỗi. Không có ràng buộc tương tự
  cho đóng góp tự nguyện, vì được đóng nhiều lần (giả định 29).
- **`khoan_phi_id` luôn có**, kể cả với khoản bắt buộc, để thống kê theo
  khoản (US-019) và tổng thu (US-016) khỏi phải nối qua `phai_nop`.
- **Hủy (giả định 28):** một giao dịch đặt `da_huy`, người hủy, thời điểm,
  lý do, và đặt `con_hieu_luc = FALSE` cho các dòng chi tiết. Phiếu và số
  giữ nguyên. `chk_pt_huy` bắt buộc đủ bốn cột hủy và lý do không rỗng.
  Quyền (Thủ quỹ chỉ phiếu của mình) và "trong ngày" kiểm tra ở Service.
- **`so_lan_in`:** in lại thì ghi "Bản in lại" (AC-040). Phiếu 0 đ không in
  nên luôn bằng 0.

### 5.3 Mã cần thêm

| Tầng | Thành phần | Nội dung |
|---|---|---|
| Model | `PhieuThu` (kèm `soBienLai()` dạng `PT-2026-00001`), `ChiTietPhieuThu`, `HinhThucThanhToan` (enum có nhãn), `TraCuuHo` (hộ, chủ hộ, khoản chưa đóng, quỹ đang mở và trạng thái chưa thu/không đóng/đã đóng của hộ) | Record như `PhaiNop`, `KhoanPhi` |
| DAO | `PhieuThuDao` / `PhieuThuDaoImpl` | `lapPhieu` (một giao dịch: khóa hộ, kiểm tra lại khoản chưa thu và quy tắc 0 đ, cấp số từ bộ đếm, ghi phiếu và chi tiết), `huy`, `timTheoId`, `theoHo`, `ghiLanIn`, `khoanChuaDong(hoId)`, `dongGopCuaHo(hoId)`, `timHoTheoMa(ma)` (gồm hộ đã chuyển đi còn nợ, giả định 21) |
| Service | `ThuPhiService` (Quản trị viên và Thủ quỹ) | `traCuu` (AC-030, 031), `thu(hoId, phaiNopIds, dongGop, hinhThuc)` (AC-033–038, AC-022–024, giả định 29–30), `huyPhieu(id, lyDo)` (AC-041–043, giả định 28), `bienLai(id)` (AC-039, 040; phiếu 0 đ báo không in) |
| Service | `BaoCaoNhanKhauService` (US-004), `ThongBaoPhiService` (US-015) | Dữ liệu danh sách tạm vắng (AC-012, 013) và thông báo phí của hộ (AC-044, 045) |
| In ấn | `util/InAn` dùng chung cho US-004, US-013, US-015 | Nhận một `Node` (bố cục FXML: `bien-lai.fxml`, `thong-bao-phi.fxml`, `danh-sach-tam-vang.fxml`), chọn máy in bằng `PrinterJob.showPrintDialog`, co theo khổ giấy. "Microsoft Print to PDF" dùng tạm cho bản PDF; xuất file thật ở Sprint 4 |
| Controller / FXML | `ThuPhiController` + `thu-phi.fxml` | Ô mã phòng → danh sách khoản (chọn sẵn tất cả) và quỹ đang mở → chọn hình thức → "Thu". Mục tiêu tối đa 5 bước (AC-072). Danh sách phiếu trong ngày của hộ để in lại, hủy |
| Menu | `trang-chinh.fxml` | Nút "Thu phí" cho cả hai vai trò; "Báo cáo nhân khẩu" cho Quản trị viên |
| Test | `ThuPhiServiceTest` (mở rộng `KhoThuPhiGia`), `PhieuThuIT` | Thu tất cả, thu một phần, thu trùng, đóng góp nhiều lần, 0 đ khi đã có lần trước, phiếu 0 đ, số liên tục và đánh lại khi sang năm, hủy (người lập, người khác, Quản trị viên, hôm trước, thiếu lý do), in lại |
| Tài liệu | `schema.sql`, `erd.md`, `class-diagram.md`, `architecture.md`, `ui.md`, `use-case.md`, `setup.md` (bảng "Dựng lại CSDL" nếu đổi bảng đã có) | Cùng PR với mã |

### 5.4 Phần Sprint 2 phải sửa theo

| Vị trí | Thay đổi | Lý do |
|---|---|---|
| `CongNoDaoImpl.daCoPhieuThu` | `SELECT 1 FROM phieu_thu_chi_tiet WHERE phai_nop_da_thu = ?` | Bật AC-026 |
| `CongNoDaoImpl` `SQL_TONG_NO` | Trừ khoản có `phai_nop_da_thu` | Cảnh báo nợ khi chuyển đi đúng; khớp DAO giả (G12) |
| `CongNoService.suaSoTien` + `CongNoDaoImpl` `SQL_SUA` | Gộp kiểm tra và cập nhật vào một câu: `UPDATE … WHERE id = ? AND NOT EXISTS (chi tiết còn hiệu lực)`, số dòng 0 thì báo `LOI_DA_THU` | Hiện kiểm tra và sửa là hai lần gọi riêng; thu tiền xen giữa sẽ sửa được khoản đã thu |
| `CongNoController.hoiSoTienMoi` | Khoản đã thu thì khóa ô và hiện nhãn đỏ (ui.md, AC-026) | Đúng đặc tả |
| `KhoanPhi` | Thêm `dangMo(LocalDate)`: `DANG_THU` và hôm nay trong `[tu_ngay, den_ngay]`; màn Khoản thu hiện "Hết hạn" theo ngày | AC-022 "khoản đang mở"; G9 |
| `PhaiNop` / `CongNoDao.theoThang` | Thêm cột "Đã đóng" (có phiếu chưa hủy) | Màn Công nợ cho thấy khoản nào sửa được |
| `08-product-backlog.md` | US-008 chuyển "Hoàn thành" khi AC-026 xong; US-004 ghi AC-014 làm ở Sprint 4 | Mục 4, giả định 27 |

### 5.5 Quyết định đã chốt

| # | Câu hỏi | Quyết định | Giả định |
|---|---|---|---|
| 1 | Phạm vi Sprint 3 | Giữ US-004, US-015 (28 SP). US-004, US-013, US-015 in chung bằng `PrinterJob`; Excel/PDF để Sprint 4 | 27 |
| 2 | Ai được hủy phiếu | Thủ quỹ: phiếu do mình lập. Quản trị viên: mọi phiếu. Chỉ trong ngày lập, bắt buộc lý do; phiếu hủy giữ lại kèm người hủy, thời điểm, lý do; khoản phải nộp trở về chưa đóng | 28 |
| 3 | Đóng góp tự nguyện | Nhiều lần cho một quỹ, tổng là cộng các lần. 0 đ chỉ khi hộ chưa có lần nào cho quỹ đó ("đã hỏi, không đóng"); đóng hơn 0 sau đó thì tính đã đóng | 29 |
| 4 | Phiếu chỉ gồm 0 đ | Lưu và có số, ghi "Không thu tiền", không in biên lai | 30 |
| 5 | Số biên lai | `PT-<năm>-<5 chữ số>`, đánh lại mỗi năm, liên tục, cấp từ bảng đếm theo năm khóa `FOR UPDATE` trong giao dịch lưu phiếu, không dùng `AUTO_INCREMENT`; phiếu hủy giữ số | 31 |

Hệ quả cần lưu ý khi lập kế hoạch Sprint 3: US-004 chỉ "Hoàn thành" phần in
trực tiếp ở Sprint 3; AC-014 (xuất file Excel/PDF) chuyển sang Sprint 4 cùng
US-018.

### 5.6 Việc cần làm trước khi đóng Sprint 2 (đến 29/11)

1. ~~Duyệt báo cáo này.~~ Đã duyệt 08/11/2026.
2. ~~Sửa L1–L3, N1–N4 kèm test, cập nhật tài liệu.~~ Đã làm trên nhánh
   `fix/sprint-2-ra-soat`; mở Pull Request, chờ CI xanh và một thành viên
   review.
3. Sprint Review và Retrospective; biên bản vào `docs/meeting-notes/`.
4. Sau khi Pull Request được merge: GitHub → Releases → tạo tag `v0.2.0`
   trên `main`. Release notes ghi rõ phải dựng lại CSDL (`docs/setup.md`,
   mục "Dựng lại CSDL khi kéo bản mới").
5. Pull Request riêng nâng `pom.xml` lên `0.3.0-SNAPSHOT` sau khi đã có tag
   (G15).
