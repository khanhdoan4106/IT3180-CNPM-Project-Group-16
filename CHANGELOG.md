# Nhật ký thay đổi

Ghi các thay đổi của sản phẩm theo từng bản phát hành, mới nhất ở trên. Khuôn
dạng theo [Keep a Changelog](https://keepachangelog.com/vi/1.1.0/). Mã story
(US-xxx) và tiêu chí chấp nhận (AC-xxx) xem ở
[Product Backlog](docs/requirements/08-product-backlog.md) và
[User Story](docs/requirements/07-user-stories.md); số `#` là Pull Request.

## [Chưa phát hành]

## [v0.3.0] – 11/11/2026 – Sprint 3

Hoàn thành thu phí từ tra cứu đến biên lai. Gồm các Pull Request #28 – #34.

### Thêm

- **Tra cứu phí (US-010, AC-030 – AC-032):** nhập mã phòng để xem hộ và các
  khoản chưa đóng; không phân biệt hoa thường; hộ đã chuyển đi còn nợ vẫn
  tra cứu được (#28).
- **Thu phí (US-011, US-012, AC-033 – AC-038):** "Thu tất cả" hoặc chỉ các
  khoản đã chọn trong một phiếu thu, tổng tự cộng, chọn Tiền mặt / Chuyển
  khoản, tự lưu người thu và thời điểm (#28).
- **Đóng góp tự nguyện (US-007, AC-022 – AC-024):** đóng bao nhiêu ghi bấy
  nhiêu cho nhiều quỹ trên cùng màn hình; đóng nhiều lần cho một quỹ được
  cộng dồn; ghi 0 đ là "không đóng", khác "chưa thu" (#28).
- **Biên lai (US-013, AC-039, AC-040):** số biên lai `PT-<năm>-<5 chữ số>`
  đánh lại mỗi năm, số tiền bằng chữ, chỗ ký; từ lần in thứ hai có khung
  "BẢN IN LẠI" (#28).
- **Hủy phiếu (US-014, AC-041 – AC-043):** hủy phiếu thu nhầm trong ngày kèm
  lý do; các khoản trở lại chưa đóng; phiếu hủy vẫn được lưu cùng người hủy.
  Thủ quỹ chỉ hủy phiếu do mình lập (#28).
- **Thông báo phí (US-015, AC-044, AC-045):** in cho một hộ hoặc cho mọi hộ
  đang ở còn khoản cần đóng, mỗi hộ một trang (#28).
- **Danh sách tạm vắng (US-004, AC-012, AC-013):** xem và in theo ngày chọn.
  Xuất tệp Excel/PDF (AC-014) làm ở Sprint 4 (#28).
- **Khóa sửa khoản đã thu (US-008, AC-026):** khoản đã có phiếu thu thì
  không sửa số tiền cho đến khi hủy phiếu (#28).
- **Giờ máy chủ CSDL:** thời điểm lập và hủy phiếu, năm của số biên lai, điều
  kiện "hủy trong ngày" lấy theo giờ máy chủ MySQL, không theo giờ máy của
  người thu (giả định 38) (#28).
- **Báo lỗi không lường trước:** lỗi không xử lý hiện hộp thoại "Có lỗi
  không mong muốn", không chỉ in ra console (#29).
- **Kiểm thử tích hợp trên MySQL của CI:** thêm job `build-mysql` chạy
  `mvn verify -Pmysql` cho mọi Pull Request (#28).

### Đổi

- **Màn Thu phí theo thiết kế Figma (#30):** dải thông tin hộ ("Căn hộ …
  · Chủ hộ … · Đang ở", "Còn nợ …"), tiêu đề có số lượng, liên kết "Chọn tất
  cả" / "Bỏ chọn tất cả", khung quỹ có cột "Quỹ / Đã đóng / Lần này"; tab
  Phiếu thu hôm nay có dải tóm tắt (số phiếu, đã thu, đã hủy) và trạng thái
  phiếu hủy dạng "Đã hủy: <lý do>" màu đỏ.
- **Hộp thoại hủy phiếu (#30):** hai nút "Hủy phiếu" và "Đóng" thay cho hai
  nút cùng bắt đầu bằng "Hủy".
- **Hộ đã chuyển đi chỉ thu các khoản còn nợ (#29):** không ghi đóng góp tự
  nguyện cho hộ đã chuyển đi, màn Thu phí không liệt kê quỹ cho hộ này
  (giả định 39).
- **Hỏi xác nhận (#29):** đổi vai trò và khóa tài khoản đều hỏi trước;
  thông báo "Đã đổi vai trò của … thành …".
- **Chữ hiển thị (#29):** "Biến động nhân khẩu gần đây", "từ đủ 14 tuổi", gợi
  ý nhập "vd A0601" thống nhất.
- **Dữ liệu mẫu (#29):** tên, nơi thường trú, lý do có dấu; hai quỹ mẫu mở từ
  đầu tháng chạy `seed.sql` đến cuối tháng sau, tên "Quỹ vì người nghèo
  <năm hiện tại>"; chạy lại `seed.sql` không sinh dòng trùng.
- **Biên lai và thông báo (#28):** cột tiền không bị cắt với số lớn (đến
  1.000.000.000 đ); danh sách tạm vắng in khổ ngang, chia trang theo chiều
  cao thực.
- **Cấu hình CSDL chỉ đọc một lần (#32):** `config/db.properties` và biến
  môi trường được đọc khi ứng dụng kết nối lần đầu rồi giữ nguyên. Sửa
  `config/db.properties` khi ứng dụng đang chạy, kể cả tạo file sau khi thấy
  báo thiếu cấu hình, không có tác dụng: tắt và mở lại ứng dụng. Thứ tự ưu
  tiên và các thông báo lỗi giữ nguyên.
- **Thông báo lỗi cấu hình (#34):** thông báo "Không kết nối được cơ sở dữ
  liệu" và "Thiếu hoặc sai cấu hình" nói rõ: sửa `config/db.properties` xong
  thì tắt và mở lại ứng dụng; thông báo mất kết nối gợi ý kiểm tra user, mật
  khẩu.
- **Logo và menu (#34):** bấm logo "BlueMoon" (con trỏ bàn tay, gợi ý "Về
  trang chủ") để về Trang chính; nút menu của màn đang mở có nền đậm.
- **Chữ phụ (#34):** màu `#526077` (tương phản 5,8:1 trên nền, đạt WCAG AA);
  tiêu đề cột bảng dùng cùng màu.
- **Chính sách đổi kích thước cột bảng (#29):** chuyển sang kiểu không
  deprecated của JavaFX 20+, hành vi không đổi.

### Sửa

- **Phím Enter bấm nhầm nút (#34):** Enter trong ô của khung đợt tạm trú/tạm
  vắng không còn lưu form nhân khẩu, Enter ở ô đơn giá không còn tạo khoản
  thu mới, Enter ở ô tìm kiếm hộ không còn bấm "Lưu". Mỗi khung chỉ chạy nút
  của mình; Enter ở màn Tài khoản gửi đúng form (họ tên, số điện thoại → "Lưu
  thay đổi"; mật khẩu → "Đổi mật khẩu"; đăng ký → "Đăng ký").
- **Thu phí (#34):** chưa chọn "Tiền mặt/Chuyển khoản" thì báo ngay, không
  còn hỏi xác nhận rồi mới báo lỗi.
- **Giờ lưu theo múi giờ máy (#34):** máy chạy ứng dụng không đặt múi giờ
  Việt Nam làm giờ lập phiếu bị dịch (phiếu 23:30 ngày 31/12 bị từ chối vì
  rơi sang năm sau). Giờ ghi và đọc CSDL không còn phụ thuộc múi giờ máy.
- Sửa số tiền khoản phải nộp ở máy này đồng thời với thu phí ở máy khác không
  còn gây deadlock; khi MySQL hủy giao dịch vì khóa, người dùng thấy "Có người
  khác đang thao tác trên hộ này, vui lòng thử lại" (#28).
- Đổi chủ hộ chỉ chọn người đang ở; nếu người đó vừa chuyển đi ở máy khác thì
  hoàn tác và báo tải lại (#29).
- Số lần in của biên lai không đếm phiếu đã hủy (#29).

### Cơ sở dữ liệu

Chỉ thêm bảng mới `bo_dem_bien_lai`, `phieu_thu`, `phieu_thu_chi_tiet`; không
đổi bảng đã có. Nâng từ `v0.2.0` chỉ cần chạy lại `schema.sql`, dữ liệu cũ
giữ nguyên ([setup.md](docs/setup.md), mục 2).

### Kiểm thử

269 test đơn vị và 29 kiểm thử tích hợp trên MySQL 8.0
([testing.md](docs/testing.md)).

## [v0.2.0] – 09/11/2026 – Sprint 2

32/32 Story Point. Riêng AC-026 của US-008 cần bảng phiếu thu nên làm cùng
Sprint 3.

### Thêm

- **Khoản thu (US-005, AC-015 – AC-018):** phí dịch vụ và phí quản lý tự tính
  theo diện tích × đơn giá; sửa đơn giá trong khung cho phép (#26).
- **Khoản thu tự nguyện (US-006, AC-019 – AC-021):** tạo quỹ theo từng đợt (#26).
- **Sửa khoản phải nộp (US-008, AC-025):** sửa số tiền của một hộ trước khi
  hộ đóng (#26).
- **Chốt công nợ (US-009, AC-027 – AC-029):** chốt tháng cho mọi hộ đang ở;
  chốt lại chỉ bổ sung phần thiếu (#24, #26, #27).
- **Phân quyền (US-022, AC-060 – AC-062):** Quản trị viên / Thủ quỹ, ẩn menu
  và kiểm tra lại ở Service (#26, #27).
- **Tài khoản (US-023, AC-063 – AC-065):** đổi mật khẩu, cập nhật họ tên và
  số điện thoại; Quản trị viên đổi vai trò, khóa, đặt lại mật khẩu (#26).
- **Đổi chủ hộ (US-026, AC-073 – AC-075):** chủ hộ mất, chuyển đi, tách hộ
  (#26).
- **Lịch sử biến động nhân khẩu (US-027, AC-076 – AC-079):** theo hộ và danh
  sách gần đây ở Trang chính (#24, #26).
- Kiểm thử tích hợp trên MySQL thật: `mvn verify -Pmysql` (#27).

### Đổi

- Chốt công nợ chỉ tính hộ đã chuyển đến và khoản có hiệu lực trong tháng.
- Ngày không tồn tại (31/02) bị từ chối; ô số tiền hiển thị `5.000`.
- Thông báo lỗi tách rõ mất kết nối, thiếu cấu hình và lỗi dữ liệu.
- Nút hộp thoại tiếng Việt; nhúng phông Be Vietnam Pro; form cuộn được ở cửa
  sổ 1120×645 (#27).

### Cơ sở dữ liệu

Thêm cột `tai_khoan.so_dien_thoai` và các bảng khoản thu, công nợ, lịch sử
biến động. **Phải dựng lại CSDL** khi nâng từ `v0.1.0`.

### Kiểm thử

169 test JUnit 5, gồm 7 test bố cục FXML chạy không cần màn hình.

## [v0.1.0] – 29/10/2026 – Sprint 1

24/24 Story Point.

### Thêm

- **Nền tảng (TS-003, AC-070, AC-071):** ứng dụng desktop JavaFX dùng CSDL
  MySQL tập trung (#8).
- **Đăng nhập (US-021, AC-057 – AC-059):** mật khẩu băm BCrypt (#8).
- **Đăng ký tài khoản (US-020, AC-054 – AC-056):** do Quản trị viên tạo (#18).
- **Hộ gia đình (US-001, AC-001 – AC-004):** thêm, sửa, tìm kiếm, đánh dấu
  chuyển đi (#11).
- **Nhân khẩu (US-002, AC-005 – AC-008):** quan hệ với chủ hộ, trạng thái cư
  trú, CCCD bắt buộc từ đủ 14 tuổi (#12, #14).
- **Tạm trú, tạm vắng (US-003, AC-009 – AC-011):** có ngày bắt đầu, kết thúc
  (#15).

### Kiểm thử

97 test JUnit 5; CI chạy `mvn verify` cho mỗi Pull Request.

[Chưa phát hành]: https://github.com/khanhdoan4106/IT3180-CNPM-Project-Group-16/compare/v0.3.0...main
[v0.3.0]: https://github.com/khanhdoan4106/IT3180-CNPM-Project-Group-16/releases/tag/v0.3.0
[v0.2.0]: https://github.com/khanhdoan4106/IT3180-CNPM-Project-Group-16/releases/tag/v0.2.0
[v0.1.0]: https://github.com/khanhdoan4106/IT3180-CNPM-Project-Group-16/releases/tag/v0.1.0
