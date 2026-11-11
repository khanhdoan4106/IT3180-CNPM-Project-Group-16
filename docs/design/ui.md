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
  hình chức năng 1120×645. Trong ứng dụng, màn chức năng nằm trong khung
  Trang chính (cửa sổ phóng to); bố cục được kiểm ở 1120×645 bằng
  `BoCucFxmlTest` (xem [Trạng thái so với thiết kế](#trạng-thái-so-với-thiết-kế)).
- Menu chỉ hiện mục mà vai trò được dùng (xem
  [Phân quyền](architecture.md#phân-quyền)).
- Bấm logo "BlueMoon" (con trỏ bàn tay, gợi ý "Về trang chủ") để hiện lại
  Trang chính; không có mục "Trang chủ" riêng. Nút menu của màn đang mở có
  nền đậm (`-mau-chinh-dam`); ở Trang chính không nút nào được chọn.
- Phím Enter trong ô nhập chỉ chạy nút của khung chứa ô đó (đợt tạm trú →
  "Ghi nhận", đơn giá → "Sửa đơn giá", đổi mật khẩu → "Đổi mật khẩu"…), khai
  báo bằng `<properties enterChay="…"/>` trên khung trong FXML. Chỉ màn đăng
  nhập (một khung duy nhất) dùng nút mặc định của cả màn.
- Hộp thoại (sửa khoản thu, xác nhận chốt) mở đè lên màn hình hiện tại, nền
  phía sau làm tối; dùng `Dialog<T>` của JavaFX.
- Thiết kế: lỗi nhập liệu hiện ngay dưới ô sai, không dồn vào hộp cảnh báo
  chung. Hiện tại: mỗi màn có một nhãn thông báo chung (đỏ khi lỗi); hiện
  dưới từng ô là việc của Sprint 4.

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
| Thu phí: tab "Thu tiền" và "Phiếu thu hôm nay" | 1120×645 | US-007, US-010 – US-014 | Có trên Figma (frame "Thu phí – Thu tiền – 1120x645", "Thu phí – Phiếu thu hôm nay – 1120x645", thẻ "Đặc tả: Thu phí"); đã có code khớp thiết kế (Sprint 3) |
| Hủy phiếu (hộp thoại) | 480 rộng | US-014, AC-042 | Có trên Figma (frame "Hủy phiếu thu (US-014)"); đã có code khớp thiết kế (Sprint 3) |
| Lịch sử thu của hộ, chi tiết phiếu (hộp thoại) | – | US-014, AC-043 | Đã có code (Sprint 3), chưa có trên Figma |
| Xem trước khi in: biên lai, thông báo phí, danh sách tạm vắng | 640×560 | US-013, US-015, US-004 | Đã có code (Sprint 3) |

Màn hình Hộ gia đình, Nhân khẩu đã có code ở Sprint 1, sẽ đưa về cùng bộ
thành phần khi rà soát giao diện (Sprint 4). Sprint 2 thêm vào hai màn này:
nút "Lịch sử biến động" (US-027), nút "Đặt làm chủ hộ" (US-026) và cảnh báo
nợ khi đánh dấu hộ chuyển đi. Sprint 3 thêm nút "Danh sách tạm vắng" ở màn
Nhân khẩu (US-004), cột "Đã đóng" và nút "In thông báo phí" ở màn Chốt công
nợ (US-015, AC-026).

### Trạng thái so với thiết kế

Phần thiết kế chưa làm hết được ghi rõ để khỏi nhầm với phần đã có; các mục
"Sprint 4" nằm trong TS-004 (rà soát giao diện).

| Nội dung thiết kế | Hiện tại | Xử lý |
|---|---|---|
| Màn chức năng 1120×645, Trang chính 1440×1024 | Màn chức năng nằm trong khung Trang chính (cửa sổ phóng to); đăng nhập 1120×645 | Giữ; bố cục kiểm ở 1120×645 (`BoCucFxmlTest`) |
| Lỗi nhập liệu hiện dưới từng ô | Một nhãn thông báo chung mỗi màn | Sprint 4 |
| Phông Be Vietnam Pro, chữ nội dung 16 px | Đã nhúng Be Vietnam Pro (Sprint 2); cỡ chữ gốc 14 px | Cỡ chữ: Sprint 4 |
| Ba phương án màu (A, B, C) bằng bộ biến `-mau-*` | Một bộ biến (11 biến) theo phương án A; màn Thu phí dùng toàn biến | Chọn phương án ở Sprint Review; thêm B, C nếu cần ở Sprint 4 |
| Khoản thu: bộ lọc Tất cả / Bắt buộc / Tự nguyện | Chưa có | Sprint 4 |
| Sửa khoản thu của hộ: hiện giá trị cũ và tổng mới | Hộp thoại một ô "Số tiền" kèm giá trị cũ ở tiêu đề, khóa ô khi đã có phiếu thu (AC-026); chưa có "tổng mới" | Sprint 4 |
| Nút hộp thoại | Tiếng Việt: "Đồng ý", "Hủy", "Đóng"; hộp thoại Hủy phiếu dùng "Hủy phiếu" và "Đóng" (`HopThoai`) | Đã làm |
| Thu phí (Thu tiền, Phiếu thu hôm nay, Hủy phiếu) | Theo Figma; khác: thanh menu/tab giữ kiểu hiện tại, bảng và nút dùng kiểu JavaFX mặc định, "Tiền mặt" không chọn sẵn (AC-038) | Rà soát nhìn ở Sprint 4 |
| Hộ gia đình, Nhân khẩu | Chưa theo bộ thành phần Figma | Sprint 4 |
| In ấn (biên lai, thông báo, danh sách tạm vắng) | Xem trước 640×560 rồi chọn máy in; danh sách tạm vắng in khổ ngang; xuất Excel/PDF chưa có | Xuất tệp: Sprint 4 (AC-014) |

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
| chữ phụ | `#526077` | `#475569` | `#334155` |
| viền | `#CBD5E1` | `#99D5CC` | `#64748B` |
| lỗi | `#DC2626` | `#DC2626` | `#B91C1C` |
| thành công | `#15803D` | `#15803D` | `#166534` |

Chữ phụ của phương án A là `#526077` (biến `-mau-phu`): tương phản 5,8:1 trên
nền `#F1F5F9` và 6,4:1 trên trắng, đạt WCAG AA (≥ 4,5:1). Màu cũ `#64748B` chỉ
đạt 4,3:1 trên nền nên bị thay. Biến này dùng cho mọi chữ phụ (`.phu-de`,
nhãn phụ, tiêu đề cột bảng); không viết thẳng mã màu xám vào CSS
(`MauPhuTest` kiểm màu, tương phản và màu JavaFX thực vẽ).

Phương án C ưu tiên NFR-008 (người lớn tuổi dùng được): chữ gần đen trên nền
trắng, viền đậm. Nhóm chọn một phương án ở Sprint Review. Code hiện chỉ có
bộ biến của phương án A (`-mau-chinh`, `-mau-chinh-dam`, `-mau-nen`,
`-mau-chu`, `-mau-loi`, `-mau-phu`, `-mau-phu-nhat`, `-mau-thanh-cong`,
`-mau-vien`, `-mau-dai`, `-mau-nhan`); màu "bề mặt" (trắng) viết thẳng
trong CSS.

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

## Quy tắc biểu đồ

Mọi biểu đồ (Trang chính, thống kê ở Sprint 4) theo các quy tắc sau. Mẫu là
biểu đồ "Thu phí 6 tháng gần nhất" ở frame "Trang chính – 1440x1024" trên
Figma.

- **Trục X:** có tên trục (vd "Tháng (năm 2026)") và nhãn ở từng mốc (T6 …
  T11).
- **Trục Y:** có tên trục kèm đơn vị (vd "Triệu đồng"); với biểu đồ cột, thang
  chia đều và bắt đầu từ 0; có vạch chia ở mỗi mốc và lưới ngang mờ.
- **Cột đúng tỷ lệ** so với thang Y (không cắt gốc, không phóng đại); mẫu ghi
  số liệu trên đầu cột.
- **Chú giải** khi có từ hai chuỗi dữ liệu trở lên; một chuỗi thì không cần.
- **Kỳ chưa kết thúc** tô khác các kỳ đã chốt (đậm hơn) kèm chú thích dưới
  biểu đồ (vd "Cột đậm: tháng 11 chưa kết thúc, số liệu tính đến hôm nay").
- **Không dùng hai trục Y.**

## Đặc tả màn hình

Mỗi dòng: thành phần – kiểu – ràng buộc – nguồn. Bảng đầy đủ nằm trong file
Figma (khung "Đặc tả màn hình").

### Khoản thu (US-005, US-006)

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Bộ lọc | Nút chọn | Tất cả / Bắt buộc / Tự nguyện (chưa có, Sprint 4) | – |
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
| Số tiền | Ô số | Không âm; tiêu đề hộp thoại ghi khoản và giá trị cũ (tổng mới của hộ: Sprint 4) | AC-025 |
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

Mục tiêu: thu xong một hộ trong tối đa 5 bước (AC-072): nhập mã phòng và
nhấn Enter (hoặc bấm Tra cứu) → (bỏ chọn khoản không thu, nhập đóng góp, nếu
cần) → chọn hình thức → Thu tất cả → Đồng ý trên hộp xác nhận. Đó là 4 thao
tác, 5 nếu bấm nút Tra cứu thay vì Enter; hộp xác nhận đã tính. In biên lai
là phần sau khi đã lập phiếu: thêm "In biên lai" → "In…" → hộp thoại máy in.

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Mã phòng | Ô nhập + nút Tra cứu (Enter cũng tra cứu) | Không phân biệt hoa thường; không có thì báo "Không tìm thấy phòng" | AC-030, AC-031 |
| Chọn hộ | Danh sách chọn, chỉ hiện khi mã phòng có hộ đã chuyển đi còn nợ | Hộ đang ở đứng đầu | Giả định 21 |
| Thông tin hộ | Dải nền xanh nhạt | Bên trái "Căn hộ A0601 · Chủ hộ … · Đang ở" (hoặc "Đã chuyển đi"), bên phải "Còn nợ … đ" | AC-030 |
| Khoản phải nộp chưa đóng | Tiêu đề "Khoản phải nộp chưa đóng (n)", hai liên kết "Chọn tất cả" / "Bỏ chọn tất cả" bên phải tiêu đề, bảng có ô chọn (Thu, Tháng, Khoản, Số tiền căn phải) | Chọn sẵn tất cả; số tiền là số hiện tại, không sửa ở đây | AC-033, AC-034 |
| Đóng góp tự nguyện | Tiêu đề "Đóng góp tự nguyện (n quỹ đang mở)", dòng gợi ý, khung có viền với hàng tiêu đề "Quỹ / Đã đóng / Lần này" | Cột "Đã đóng" hiện "Chưa thu", "Không đóng" hoặc số tiền (vd "100.000 đ"). Ô gợi ý "Số tiền" khi chưa thu, "Đóng thêm" khi đã có lần ghi. Trống: chưa thu. 0: không đóng (chỉ khi chưa có lần nào). Hộ đã chuyển đi không có quỹ nào (giả định 39) | AC-022 – AC-024, giả định 29, giả định 39 |
| Hình thức | Hai nút chọn | Không chọn sẵn; phiếu có tiền mà chưa chọn thì báo lỗi | AC-036, AC-038 |
| Tổng thu | Nhãn đậm | Cộng ngay khi chọn hoặc nhập | AC-035 |
| Thu tất cả / Thu các khoản đã chọn | Nút chính | Chữ đổi theo lựa chọn; hỏi xác nhận; xong thì hỏi "In biên lai" / "Để sau" | AC-033, AC-037 |
| Dải tóm tắt (tab Phiếu thu hôm nay) | Ba nhãn bo tròn | "Số phiếu n", "Đã thu hôm nay x đ" (cộng các phiếu chưa hủy, chữ xanh), "Đã hủy n phiếu" (chữ đỏ) | AC-043 |
| Phiếu thu hôm nay | Bảng | Số biên lai (đậm), căn hộ, thời điểm, tổng (căn phải), hình thức, người thu, trạng thái. Phiếu hủy chữ xám cả dòng, cột trạng thái "Đã hủy: <lý do>" màu đỏ; người hủy và thời điểm hủy xem ở "Xem chi tiết". Trạng thái khác: "Chưa in biên lai", "Đã in n lần", "Không thu tiền" | AC-043 |
| Hủy phiếu | Nút viền đỏ ở tab Phiếu thu hôm nay; hộp thoại "Hủy phiếu <số>" có ô "Lý do hủy (bắt buộc)", dòng lưu ý, hai nút "Hủy phiếu" (viền đỏ) và "Đóng" | Thủ quỹ: phiếu của mình; trong ngày; lý do bắt buộc. Nút bỏ qua là "Đóng" (không dùng "Hủy" để khỏi nhầm với "Hủy phiếu") | AC-041, AC-042, giả định 28 |

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
  ngang (đặt sẵn trong hộp thoại máy in, đổi được), chia trang theo chiều
  cao thực sau khi bố cục nên họ tên, lý do dài không làm trang bị thu nhỏ;
  có số trang và chỗ ký. Xuất tệp Excel/PDF làm ở
  Sprint 4 (AC-014).

