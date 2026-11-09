# Thiết kế giao diện

Bản thiết kế trên Figma:
[BlueMoon – Thiết kế giao diện](https://www.figma.com/design/zIpAYc9R9HeO2NaKChHirY).
File gồm bộ thành phần dùng chung (nút, ô nhập, mục menu, thẻ số liệu, dòng
đặc tả), các màn hình, 3 phương án màu và bảng đặc tả cho từng màn hình.

## Bố cục và điều hướng

- Toàn ứng dụng dùng bố cục **đầu – thân**: thanh menu trên cùng (logo, các
  mục chức năng, người dùng, Đăng xuất), màn hình chức năng ở phần thân.
  Trang chính thêm phần **đuôi** chứa thao tác nhanh.
- Kích thước theo Bộ bài tập: Trang chính 1440×1024; đăng nhập và các màn
  hình chức năng 1120×645.
- Menu chỉ hiện mục mà vai trò được dùng (xem
  [Phân quyền](architecture.md#phân-quyền)).
- Hộp thoại (sửa khoản thu, xác nhận chốt) mở đè lên màn hình hiện tại, nền
  phía sau làm tối; dùng `Dialog<T>` của JavaFX.
- Lỗi nhập liệu hiện ngay dưới ô sai, không dồn vào hộp cảnh báo chung.

## Màn hình

| Màn hình | Kích thước | Story | Trạng thái |
|---|---|---|---|
| Đăng nhập | 1120×645 | US-021 | Đã có code (Sprint 1) |
| Trang chính | 1440×1024 | US-016, US-027, nhắc chốt công nợ | Đã có code: nhắc chốt, biến động gần đây (Sprint 2) |
| Tài khoản của tôi (tab) | 1120×645 | US-023 | Đã có code (Sprint 2) |
| Quản lý tài khoản (tab, chỉ Quản trị viên) | 1120×645 | US-020, US-022 | Đã có code (Sprint 2) |
| Khoản thu (danh mục, tạo khoản tự nguyện) | 1120×645 | US-005, US-006 | Đã có code (Sprint 2) |
| Chốt công nợ | 1120×645 | US-009 | Đã có code (Sprint 2) |
| Sửa khoản thu của hộ (hộp thoại, 2 trạng thái) | 480 rộng | US-008 | Đã có code (Sprint 2) |
| Lịch sử biến động của hộ (hộp thoại) | 820 rộng | US-027 | Đã có code (Sprint 2) |
| Thu phí: tab "Thu tiền" và "Phiếu thu hôm nay" | 1120×645 | US-007, US-010 – US-014 | Đã có code (Sprint 3), chưa có trên Figma |
| Hủy phiếu, lịch sử thu của hộ, chi tiết phiếu (hộp thoại) | – | US-014, AC-043 | Đã có code (Sprint 3) |
| Xem trước khi in: biên lai, thông báo phí, danh sách tạm vắng | 640×560 | US-013, US-015, US-004 | Đã có code (Sprint 3) |

Màn hình Hộ gia đình, Nhân khẩu đã có code ở Sprint 1, sẽ đưa về cùng bộ
thành phần khi rà soát giao diện (Sprint 4). Sprint 2 thêm vào hai màn này:
nút "Lịch sử biến động" (US-027), nút "Đặt làm chủ hộ" (US-026) và cảnh báo
nợ khi đánh dấu hộ chuyển đi. Sprint 3 thêm nút "Danh sách tạm vắng" ở màn
Nhân khẩu (US-004), cột "Đã đóng" và nút "In thông báo phí" ở màn Chốt công
nợ (US-015, AC-026).

## Phương án màu

Màu khai báo thành biến; mỗi phương án là một chế độ của cùng bộ biến, trong
code tương ứng biến `-mau-*` của `css/giao-dien.css`.

| Biến | A – Xanh dương | B – Xanh ngọc | C – Tương phản cao |
|---|---|---|---|
| màu chính | `#2563EB` | `#0F766E` | `#1E3A8A` |
| màu chính đậm | `#1D4ED8` | `#134E4A` | `#172554` |
| nền | `#F1F5F9` | `#F0FDFA` | `#F8FAFC` |
| bề mặt | `#FFFFFF` | `#FFFFFF` | `#FFFFFF` |
| chữ | `#0F172A` | `#134E4A` | `#020617` |
| chữ phụ | `#64748B` | `#475569` | `#334155` |
| viền | `#CBD5E1` | `#99D5CC` | `#64748B` |
| lỗi | `#DC2626` | `#DC2626` | `#B91C1C` |
| thành công | `#15803D` | `#15803D` | `#166534` |

Phương án C ưu tiên NFR-008 (người lớn tuổi dùng được): chữ gần đen trên nền
trắng, viền đậm. Nhóm chọn một phương án ở Sprint Review.

## Chữ

Phông Be Vietnam Pro (thiết kế cho tiếng Việt, dấu rõ ở cỡ nhỏ), nhúng vào
CSS bằng `@font-face`. Chữ nội dung 16 px để người lớn tuổi dễ đọc
(NFR-008).

| Kiểu | Cỡ / độ đậm | Dùng cho |
|---|---|---|
| Tiêu đề | 28 / Bold | Tiêu đề trang |
| Tiêu đề nhỏ | 20 / SemiBold | Tiêu đề khung, hộp thoại |
| Nội dung | 16 / Regular | Dữ liệu trong bảng, ô nhập |
| Nhãn | 15 / Medium | Nhãn ô nhập, cột đầu bảng |
| Chú thích | 14 / Regular | Ghi chú, gợi ý dưới ô |
| Nút | 16 / SemiBold | Chữ trên nút |

## Đặc tả màn hình

Mỗi dòng: thành phần – kiểu – ràng buộc – nguồn. Bảng đầy đủ nằm trong file
Figma (khung "Đặc tả màn hình").

### Khoản thu (US-005, US-006)

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Bộ lọc | Nút chọn | Tất cả / Bắt buộc / Tự nguyện | – |
| Danh mục khoản thu | Bảng | Tên, loại, mức thu, trạng thái | AC-019 |
| Sửa đơn giá | Nút phụ | Phí dịch vụ 2.500–16.500 đ/m²; phí quản lý mặc định 7.000 đ/m² | AC-015 – AC-017 |
| Tên khoản thu | Ô nhập | Bắt buộc, không trùng trong cùng đợt | AC-020 |
| Từ ngày, Đến ngày | Ô ngày | Bắt buộc; đến ngày không trước từ ngày | AC-019 |
| Số tiền mỗi hộ | Ô số | Không bắt buộc | AC-021 |
| Tạo khoản thu | Nút chính | Khoản mới hiện ngay trong danh mục | AC-019 |

### Chốt công nợ (US-009)

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Chọn tháng | Danh sách chọn | Mặc định tháng hiện tại | AC-027 |
| Xem trước | Nút phụ | Tính thử, chưa lưu | AC-027 |
| Thông báo đã chốt | Nhãn xanh | Chốt lại chỉ thêm hộ/khoản còn thiếu | AC-028 |
| Bảng xem trước | Bảng | Diện tích × đơn giá từng phòng | AC-018, AC-027 |
| Chốt công nợ | Nút chính | Hỏi xác nhận trước khi lưu | AC-027 |

### Sửa khoản thu của hộ (US-008)

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Phí dịch vụ, Phí quản lý | Ô số | Không âm; hiện giá trị cũ và tổng mới | AC-025 |
| Ghi chú | Ô nhập | Không bắt buộc, lưu cùng lần sửa | – |
| Cảnh báo đã thu | Nhãn đỏ | Đã có phiếu thu thì khóa ô, yêu cầu hủy phiếu trước | AC-026 |

### Tài khoản (US-022, US-023)

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Họ tên | Ô nhập | Bắt buộc, tối đa 100 ký tự | AC-065 |
| Mật khẩu hiện tại | Ô nhập ẩn | Sai thì từ chối, báo dưới ô | AC-064 |
| Mật khẩu mới, Nhập lại | Ô nhập ẩn | Hai ô phải khớp | AC-063 |
| Danh sách tài khoản | Bảng | Tên đăng nhập, họ tên, vai trò, trạng thái; chỉ Quản trị viên thấy | AC-061 |
| Khóa | Nút phụ | Không xóa, giữ lịch sử thu | – |

### Thu phí (US-007, US-010 – US-014)

Mục tiêu: thu xong một hộ trong tối đa 5 bước (AC-072): nhập mã phòng →
Tra cứu → (bỏ chọn khoản không thu, nhập đóng góp) → chọn hình thức → Thu.

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Mã phòng | Ô nhập + nút Tra cứu (Enter cũng tra cứu) | Không phân biệt hoa thường; không có thì báo "Không tìm thấy phòng" | AC-030, AC-031 |
| Chọn hộ | Danh sách chọn, chỉ hiện khi mã phòng có hộ đã chuyển đi còn nợ | Hộ đang ở đứng đầu | Giả định 21 |
| Thông tin hộ | Nhãn | Căn hộ, chủ hộ, trạng thái, tổng còn nợ | AC-030 |
| Khoản phải nộp chưa đóng | Bảng có ô chọn | Chọn sẵn tất cả; số tiền là số hiện tại, không sửa ở đây | AC-033, AC-034 |
| Đóng góp tự nguyện | Mỗi quỹ đang mở một dòng: tên, tình trạng, ô số tiền | Trống: chưa thu. 0: không đóng (chỉ khi chưa có lần nào). Đã đóng thì ghi "Đã đóng 100.000 đ", ô gợi ý "Đóng thêm" | AC-022 – AC-024, giả định 29 |
| Hình thức | Hai nút chọn | Không chọn sẵn; phiếu có tiền mà chưa chọn thì báo lỗi | AC-036, AC-038 |
| Tổng thu | Nhãn đậm | Cộng ngay khi chọn hoặc nhập | AC-035 |
| Thu tất cả / Thu các khoản đã chọn | Nút chính | Chữ đổi theo lựa chọn; hỏi xác nhận; xong thì hỏi "In biên lai" / "Để sau" | AC-033, AC-037 |
| Phiếu thu hôm nay | Bảng | Số biên lai, căn hộ, thời điểm, tổng, hình thức, người thu, trạng thái; phiếu hủy chữ xám | AC-043 |
| Hủy phiếu | Nút viền đỏ + hộp thoại lý do | Thủ quỹ: phiếu của mình; trong ngày; lý do bắt buộc | AC-041, AC-042, giả định 28 |

### Biên lai (US-013)

Khổ dọc, in qua hộp thoại xem trước rồi chọn máy in (trên Windows có thể
chọn "Microsoft Print to PDF"). Gồm: đơn vị, "BIÊN LAI THU TIỀN", số biên
lai, căn hộ, chủ hộ, thời điểm thu, hình thức, bảng khoản (khoản bắt buộc
kèm tháng), tổng cộng, số tiền bằng chữ, chỗ ký người nộp và người thu (in
sẵn tên người thu). Từ lần in thứ hai có khung "BẢN IN LẠI" (AC-040). Phiếu
"Không thu tiền" và phiếu đã hủy không in.

### Thông báo phí (US-015) và danh sách tạm vắng (US-004)

- Thông báo phí: in cho hộ đang chọn hoặc cho mọi hộ đang ở còn khoản cần
  đóng, mỗi hộ một trang; liệt kê khoản của tháng, khoản các tháng trước
  chưa đóng, tổng và số tiền bằng chữ. Tháng chưa chốt thì không in.
- Danh sách tạm vắng: chọn ngày (mặc định hôm nay), xem trên bảng rồi in khổ
  ngang 18 người một trang, có số trang và chỗ ký. Xuất tệp Excel/PDF làm ở
  Sprint 4 (AC-014).

