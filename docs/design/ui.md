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
- Góc phải thanh menu (Figma "Người dùng"): tên người dùng (15 px Medium)
  và vai trò (14 px, `-mau-dai`) hai dòng căn phải trước nút Đăng xuất,
  không có "Xin chào". Tên dài quá chỗ trống thì rút gọn "…", tooltip hiện
  đủ "Tên – Vai trò". Mục menu 15 px Medium, đệm ngang 8, cách nhau 8 px để
  ở 1120 px vẫn đủ chỗ cho "Nguyễn Thị Thu Hương" (`BoCucFxmlTest` kiểm ở
  1120, 1366, 1440 px).
- Bấm logo "BlueMoon" (con trỏ bàn tay, gợi ý "Về trang chủ") để hiện lại
  Trang chính; không có mục "Trang chủ" riêng. Nút menu của màn đang mở có
  nền đậm (`-mau-chinh-dam`); ở Trang chính không nút nào được chọn.
- Phím Enter trong ô nhập chỉ chạy nút của khung chứa ô đó (đợt tạm trú →
  "Ghi nhận", đơn giá → "Sửa đơn giá", đổi mật khẩu → "Đổi mật khẩu"…), khai
  báo bằng `<properties enterChay="…"/>` trên khung trong FXML. Chỉ màn đăng
  nhập (một khung duy nhất) dùng nút mặc định của cả màn.
- Hộp thoại (sửa khoản thu, xác nhận chốt) mở đè lên màn hình hiện tại, nền
  phía sau làm tối; dùng `Dialog<T>` của JavaFX. `KieuHopThoai` gắn
  `giao-dien.css` cho mọi cửa sổ mới mở nên hộp thoại cùng kiểu với màn
  hình (xem [Thành phần](#thành-phần)).
- Nhãn thông báo chỉ hiện khi có chữ: `visible` và `managed` gắn với chữ
  (`visible="${!x.text.empty}"` trong FXML, hoặc `setVisible`/`setManaged`
  cùng lúc trong controller), để nhãn rỗng không giữ chỗ trống.
- Thiết kế: lỗi nhập liệu hiện ngay dưới ô sai, không dồn vào hộp cảnh báo
  chung. Hiện tại: mỗi màn có một nhãn thông báo chung (đỏ khi lỗi); hiện
  dưới từng ô là việc của Sprint 4.

## Màn hình

| Màn hình | Kích thước | Story | Trạng thái |
|---|---|---|---|
| Đăng nhập | 1120×645 | US-021 | Đã có code (Sprint 1) |
| Trang chính | 1440×1024 | US-016, US-027, nhắc chốt công nợ | Đã có code: nhắc chốt, biến động gần đây (Sprint 2); tiêu đề "Tổng quan tháng …", thẻ "Đã thu tháng này", "Hộ còn nợ", biểu đồ "Thu phí 6 tháng gần nhất" (Sprint 4) |
| Thống kê theo khoản, báo cáo thu theo tháng | 1120×645 | US-019, US-018 | Đã có code (Sprint 4), mục menu "Thống kê" như Figma; chưa có frame riêng trên Figma |
| Tài khoản của tôi (tab) | 1120×645 | US-023 | Đã có code (Sprint 2) |
| Quản lý tài khoản (tab, chỉ Quản trị viên) | 1120×645 | US-020, US-022 | Đã có code (Sprint 2) |
| Khoản thu (danh mục, tạo khoản tự nguyện) | 1120×645 | US-005, US-006 | Đã có code (Sprint 2) |
| Công nợ: tab "Chốt công nợ" và "Hộ nợ đọng" | 1120×645 | US-009, US-017 | Chốt công nợ: đã có code (Sprint 2); tab "Hộ nợ đọng": đã có code (Sprint 4), chưa có frame riêng trên Figma |
| Sửa khoản thu của hộ (hộp thoại, 2 trạng thái) | 480 rộng | US-008 | Đã có code (Sprint 2) |
| Lịch sử biến động của hộ (hộp thoại) | 820 rộng | US-027 | Đã có code (Sprint 2) |
| Thu phí: tab "Thu tiền" và "Phiếu thu hôm nay" | 1120×645 | US-007, US-010 – US-014 | Có trên Figma (frame "Thu phí – Thu tiền – 1120x645", "Thu phí – Phiếu thu hôm nay – 1120x645", thẻ "Đặc tả: Thu phí"); đã có code khớp thiết kế (Sprint 3) |
| Hủy phiếu (hộp thoại) | 480 rộng | US-014, AC-042 | Có trên Figma (frame "Hủy phiếu thu (US-014)"); đã có code khớp thiết kế (Sprint 3) |
| Lịch sử thu của hộ, chi tiết phiếu (hộp thoại) | – | US-014, AC-043 | Đã có code (Sprint 3), chưa có trên Figma |
| Xem trước khi in: biên lai, thông báo phí, danh sách tạm vắng | 640×560 | US-013, US-015, US-004 | Đã có code (Sprint 3) |

Màn hình Hộ gia đình, Nhân khẩu đã có code ở Sprint 1, đưa về cùng bộ
thành phần ở TS-004. Sprint 2 thêm vào hai màn này:
nút "Xem lịch sử biến động" (US-027), nút "Đặt làm chủ hộ" (US-026) và cảnh báo
nợ khi đánh dấu hộ chuyển đi. Sprint 3 thêm nút "Xem danh sách tạm vắng" ở màn
Nhân khẩu (US-004), cột "Đã đóng" và nút "In thông báo phí" ở màn Chốt công
nợ (US-015, AC-026).

### Trạng thái so với thiết kế

Phần thiết kế chưa làm hết được ghi rõ để khỏi nhầm với phần đã có; các mục
"Sprint 4" nằm trong TS-004 (rà soát giao diện).

| Nội dung thiết kế | Hiện tại | Xử lý |
|---|---|---|
| Màn chức năng 1120×645, Trang chính 1440×1024 | Màn chức năng nằm trong khung Trang chính (cửa sổ phóng to); đăng nhập 1120×645 | Giữ; bố cục kiểm ở 1120×645 (`BoCucFxmlTest`) |
| Lỗi nhập liệu hiện dưới từng ô | Một nhãn thông báo chung mỗi màn | Sprint 4 |
| Phông Be Vietnam Pro, chữ nội dung 16 px | Đã nhúng Be Vietnam Pro (Sprint 2); chữ gốc 16 px, không chữ nào trên màn hình dưới 14 px (TS-004). Nhãn, nút dùng Bold thay Medium/SemiBold vì JavaFX tách Medium, SemiBold thành họ phông riêng | Đã làm |
| Ba phương án màu (A, B, C) bằng bộ biến `-mau-*` | Một bộ biến theo phương án A, riêng "lỗi" và "thành công" lấy màu đậm của phương án C để đạt 4,5:1 trên nền (TS-004) | Chọn phương án ở Sprint Review; thêm B, C nếu cần |
| Bộ thành phần (nút, ô nhập, tab, bảng, hộp thoại) | Cùng một kiểu trên mọi màn theo [Thành phần](#thành-phần); không còn kiểu JavaFX mặc định (TS-004) | Đã làm |
| Khoản thu: bộ lọc Tất cả / Bắt buộc / Tự nguyện | Chưa có | Sprint 4 |
| Sửa khoản thu của hộ: hiện giá trị cũ và tổng mới | Hộp thoại một ô "Số tiền" kèm giá trị cũ ở tiêu đề, khóa ô khi đã có phiếu thu (AC-026); chưa có "tổng mới" | Sprint 4 |
| Nút hộp thoại | Tiếng Việt: "Đồng ý", "Hủy", "Đóng"; hộp thoại Hủy phiếu dùng "Hủy phiếu" và "Đóng" (`HopThoai`) | Đã làm |
| Thu phí (Thu tiền, Phiếu thu hôm nay, Hủy phiếu) | Theo Figma; menu có trạng thái "đang chọn" và logo BlueMoon về Trang chính (#34); "Tiền mặt" không chọn sẵn (AC-038) khớp Figma đã sửa; tab, bảng, nút theo bộ thành phần (TS-004); thu một hộ 4 thao tác (AC-072) | Đã làm |
| Thanh menu chung | Figma đã có mục "Tài khoản của tôi" trong khung chung và mục Công nợ, khớp menu trong code; mục "Thống kê" giữa Thu phí và Tài khoản (Sprint 4) | Đã làm |
| Trang chính (frame "Trang chính – 1440x1024") | Có tiêu đề tháng, thẻ "Đã thu tháng này" và "Hộ còn nợ" (bấm để mở tab "Hộ nợ đọng"), biểu đồ 6 tháng theo quy tắc biểu đồ (`BieuDoThuPhi`), nhắc chốt, biến động gần đây bên phải biểu đồ; nội dung cuộn được khi cửa sổ thấp. Chưa có: thẻ "Hộ đang ở", "Nhân khẩu", khung "Khoản thu mới cập nhật" và dải "Thao tác nhanh" | Xét ở TS-004 |
| Thống kê theo khoản | Chọn khoản; khoản bắt buộc chọn tháng đã chốt (mặc định tháng gần nhất), quỹ tự nguyện hiện "Thu từ … đến …" chỉ đọc; kết quả: tổng đã thu chữ lớn, số phụ. Chưa có frame trên Figma, dựng theo kiểu các màn 1120×645 | Vẽ frame ở TS-004 nếu cần |
| Hộ gia đình, Nhân khẩu | Theo bộ thành phần (TS-004); form bên phải rộng 430, cột nhãn 156/140 px cho chữ 16 px; hàng nút dưới xuống dòng khi chật. Chưa có frame riêng trên Figma | Đã làm |
| In ấn (biên lai, thông báo, danh sách tạm vắng) | Xem trước 640×560 rồi chọn máy in; danh sách tạm vắng in khổ ngang và xuất Excel (AC-014); báo cáo thu tháng xuất Excel ở màn Thống kê (US-018); cần PDF thì in qua máy in PDF | Đã làm |

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
| lỗi | `#B91C1C` | `#DC2626` | `#B91C1C` |
| thành công | `#166534` | `#15803D` | `#166534` |

Chữ phụ của phương án A là `#526077` (biến `-mau-phu`): tương phản 5,8:1 trên
nền `#F1F5F9` và 6,4:1 trên trắng, đạt WCAG AA (≥ 4,5:1). Màu cũ `#64748B` chỉ
đạt 4,3:1 trên nền nên bị thay. Biến này dùng cho mọi chữ phụ (`.phu-de`,
nhãn phụ, tiêu đề cột bảng); không viết thẳng mã màu xám vào CSS
(`MauPhuTest` kiểm màu, tương phản và màu JavaFX thực vẽ).

TS-004 đổi hai màu của phương án A (Figma cần sửa theo): lỗi `#DC2626` →
`#B91C1C` vì `#DC2626` chỉ đạt 4,1:1 trên nền `#F1F5F9` (thông báo lỗi nằm
trên nền màn hình); thành công `#15803D` → `#166534` vì `#15803D` chỉ đạt
4,1:1 trên nhãn tóm tắt `#E2E8F0` ("Đã thu hôm nay"). Mọi cặp chữ/nền đang
dùng đạt ≥ 4,5:1, `DeDocTest#moiCapChuNenDatAa` đo từ tệp CSS: chữ chính,
chữ phụ, chữ trắng trên nút chính và thanh menu, chữ đỏ báo lỗi, chữ xanh
thành công, chữ trên nền thẻ (trắng), dòng chọn, dòng xen kẽ, dải thông
tin.

Phương án C ưu tiên NFR-008 (người lớn tuổi dùng được): chữ gần đen trên nền
trắng, viền đậm. Nhóm chọn một phương án ở Sprint Review. Code hiện chỉ có
bộ biến của phương án A (`-mau-chinh`, `-mau-chinh-dam`, `-mau-nen`,
`-mau-chu`, `-mau-loi`, `-mau-phu`, `-mau-thanh-cong`, `-mau-vien`,
`-mau-dai`, `-mau-nhan`, và từ TS-004: `-mau-chon` nền dòng chọn,
`-mau-xen-ke` dòng xen kẽ, `-mau-nen-loi` nền nút nguy hiểm khi rê chuột);
màu "bề mặt" (trắng) viết thẳng trong CSS. Biến `-mau-phu-nhat` (`#94A3B8`,
2,6:1 trên trắng) đã bỏ: phiếu đã hủy dùng chữ phụ như Figma.

## Chữ

Phông Be Vietnam Pro (thiết kế cho tiếng Việt, dấu rõ ở cỡ nhỏ), nhúng vào
CSS bằng `@font-face`. Chữ gốc 16 px để người lớn tuổi dễ đọc (NFR-008,
TS-004); không chữ nào trên màn hình dưới 14 px, kể cả tiêu đề cột bảng,
nhãn trục và số trên cột biểu đồ, dòng phụ, thông báo lỗi
(`BoCucFxmlTest` đo mọi chữ của từng màn). Trang in giữ chữ 14 px để mẫu in
không đổi.

| Kiểu | Cỡ / độ đậm | Lớp CSS | Dùng cho |
|---|---|---|---|
| Tên ứng dụng | 32 / Bold | `.tieu-de` | "BlueMoon" ở màn Đăng nhập |
| Tiêu đề trang | 24 / Bold | `.tieu-de-trang` | Tiêu đề màn hình, Trang chính |
| Tiêu đề khung | 20 / Bold | `.tieu-de-form`, phần đầu hộp thoại | Tiêu đề khung, form, hộp thoại |
| Số lớn | 34 / Bold | `.so-lon` | Thẻ số liệu |
| Nội dung | 16 / Regular | gốc | Dữ liệu trong bảng, ô nhập, nhãn |
| Nhãn bảng | 15 / Bold | tiêu đề cột | Cột đầu bảng, hàng tiêu đề khung quỹ |
| Chú thích | 14–16 / Regular | `.phu-de`, trục biểu đồ 14 | Ghi chú, gợi ý dưới ô |
| Nút | 16 / Bold | `.button` | Chữ trên nút |

Figma dùng Medium (nhãn) và SemiBold (nút); JavaFX coi hai độ đậm này là họ
phông riêng nên code dùng Bold cùng họ "Be Vietnam Pro".

## Thành phần

Mọi màn dùng chung các kiểu dưới đây (`css/giao-dien.css`), theo khung
"Thành phần" trên Figma; không còn kiểu mặc định của JavaFX. Chỉ đổi lớp
kiểu trong FXML, không đổi nội dung màn hình.

| Thành phần | Kiểu | Lớp |
|---|---|---|
| Nút chính | Nền `-mau-chinh`, chữ trắng đậm, bo 8, cao ≥ 44; rê chuột nền `-mau-chinh-dam`. Nút mặc định của hộp thoại ("Đồng ý", "In biên lai") cùng kiểu | `.nut-chinh`, `:default` |
| Nút phụ | Nền trắng, viền `-mau-vien`, chữ `-mau-chu` đậm, bo 8, cao ≥ 44; mọi nút không có lớp riêng là nút phụ | `.button`, `.nut-phu` |
| Nút nguy hiểm | Nền trắng, viền và chữ `-mau-loi` (Figma "Viền đỏ"): "Hủy phiếu", "Đánh dấu chuyển đi" | `.nut-nguy-hiem` |
| Ô nhập, danh sách chọn, ô ngày | Nền trắng, viền 1 px `-mau-vien`, bo 8, cao ≥ 40, chữ 16; chữ gợi ý `-mau-phu` | `.text-field`, `.combo-box-base` |
| Tiêu điểm | Ô nhập, danh sách chọn, bảng: viền 2 px `-mau-chinh`. Nút phụ: viền 2 px `-mau-chinh`; nút chính, nút nguy hiểm: vòng ngoài cách nút 2 px. Ô chọn, nút chọn: vòng `-mau-chinh-dam`. Trên thanh menu xanh: viền trắng. Vẽ bằng lớp nền nên ô không đổi kích thước khi có tiêu điểm | `:focused` |
| Bảng | Viền `-mau-vien`, bo 6; hàng tiêu đề nền `-mau-nen`, chữ phụ 15 đậm, cao 40; dòng cao 40, xen kẽ trắng / `-mau-xen-ke`, kẻ dưới `-mau-vien`; dòng chọn nền `-mau-chon` có vạch `-mau-chinh` bên trái, chữ giữ `-mau-chu`; dòng trống trắng; bảng trống có chữ báo tiếng Việt (placeholder) | `.table-view` |
| Tab | Không khung; tab đang chọn chữ `-mau-chinh` đậm, gạch dưới 3 px; tab thường chữ phụ; dải tab kẻ dưới `-mau-vien` | `.tab-pane` |
| Hộp thoại | Nền trắng; phần đầu nền `-mau-nen` chữ 20 đậm; nút như trên, nút mặc định là nút chính | `.dialog-pane` |
| Khoảng cách | Hai nút cạnh nhau cách ≥ 8 px (FXML dùng 10) | `BoCucFxmlTest` |

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

### Trang chính (US-016)

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Tiêu đề | Nhãn | "Tổng quan tháng 11/2026", tháng theo giờ CSDL | US-016 |
| Đã thu tháng này | Thẻ số liệu | Tổng phiếu chưa hủy lập trong tháng, dạng "186.450.000 đ"; tháng chưa có phiếu là "0 đ" | AC-046, AC-047, giả định 24 |
| Dòng phụ của thẻ | Nhãn phụ | "72% tổng phải thu" (làm tròn xuống); tháng chưa chốt: "Tháng 11 chưa chốt công nợ" | giả định 24 |
| Hộ còn nợ | Thẻ số liệu, bấm được (con trỏ bàn tay) | Số hộ còn ít nhất một tháng nợ; dòng phụ "8 hộ nợ từ 3 tháng trở lên", không có thì "Không hộ nào nợ từ 3 tháng"; bấm thẻ mở Công nợ ở tab "Hộ nợ đọng" | US-017, giả định 25 |
| Thu phí 6 tháng gần nhất | Biểu đồ cột | Theo [Quy tắc biểu đồ](#quy-tắc-biểu-đồ); đơn vị "Triệu đồng", dưới 1 triệu thì "Nghìn đồng"; trải hai năm thì nhãn "T12/25" | US-016 |
| Biến động nhân khẩu gần đây | Bảng gọn, chỉ Quản trị viên | Ngày, Căn hộ, Họ tên, Biến động; ghi chú và người ghi ở tooltip của dòng; khung rộng 560 px | US-027 |
| Làm mới | – | Đọc lại số liệu khi đăng nhập và khi bấm logo, không hẹn giờ | – |

### Thống kê theo khoản (US-019)

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Khoản thu | Danh sách chọn | Khoản bắt buộc trước, rồi quỹ tự nguyện mới nhất trước | AC-052 |
| Tháng đã chốt | Danh sách chọn | Chỉ với khoản bắt buộc; mặc định tháng gần nhất; chưa chốt tháng nào thì báo rõ và khóa nút "Xem" | giả định 45 |
| Thời gian thu | Nhãn chỉ đọc | Chỉ với quỹ tự nguyện: "Thu từ … đến …" | giả định 45 |
| Tổng đã thu | Chữ lớn | Phiếu chưa hủy; chưa có giao dịch là 0 đ | AC-052, AC-053 |
| Số phụ | Nhãn | Bắt buộc: tổng phải thu, hộ đã đóng / hộ phải đóng (khoản 0 đ không tính); tự nguyện: hộ đã đóng góp, hộ ghi không đóng (0 đ) | giả định 29, 33, 45 |

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

### Hộ nợ đọng (US-017)

Tab "Hộ nợ đọng" của màn Công nợ, cạnh tab "Chốt công nợ".

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Lọc | Nút chọn | "Nợ từ 3 tháng trở lên" (mặc định) / "Tất cả hộ còn nợ"; tải lại khi đổi | AC-048 |
| Danh sách hộ | Bảng | Căn hộ (hộ đã chuyển đi ghi "(đã chuyển đi)"), chủ hộ, số tháng nợ, nợ từ tháng, tổng tiền nợ; nợ nhiều tháng nhất ở trên | AC-049, giả định 21, 25 |
| Dòng tổng | Nhãn đậm | "x hộ, tổng nợ y đ"; không có hộ nào thì bảng báo "Không có hộ nào nợ từ 3 tháng trở lên" | AC-049 |
| Ghi chú | Nhãn phụ | Số tháng nợ tính cả tháng này nếu tháng này đã chốt | giả định 25 |

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

Mục tiêu: thu xong một hộ trong tối đa 5 bước (AC-072). Bước là một thao
tác chủ động của người dùng; gõ mã rồi Enter tính là 1. Luồng chuẩn
(`ThuMotHoNamBuocTest` chạy đúng luồng này trên màn thật):

| Bước | Người dùng làm | Màn hình | Tiêu điểm sau bước | Thao tác |
|---|---|---|---|---|
| – | Mở màn Thu phí | Dải thông tin "Gõ mã phòng rồi nhấn Enter (hoặc bấm Tra cứu)" | Ô mã phòng | 0 |
| 1 | Gõ mã phòng, nhấn Enter | Thông tin hộ, khoản bắt buộc chưa đóng đã chọn sẵn hết, tổng thu, nút "Thu tất cả" | Nút chọn "Tiền mặt" (phòng có hộ đã chuyển đi còn nợ: danh sách chọn hộ) | 1 |
| 2 | Xem khoản (bỏ chọn khoản không thu, nhập đóng góp nếu cần) | Tổng thu cộng ngay | – | 0 (xem) |
| 3 | Chọn "Tiền mặt" hoặc "Chuyển khoản" | – | Nút vừa chọn | 1 |
| 4 | Bấm "Thu tất cả" (hoặc "Thu các khoản đã chọn") | Hộp xác nhận "Lập phiếu thu cho căn hộ …?" | Nút "Đồng ý" | 1 |
| 5 | Bấm "Đồng ý" | Phiếu được lập; hộp "Đã thu tiền" với "In biên lai" / "Để sau" | Nút "In biên lai" | 1 |

Tổng 4 thao tác (5 nếu bấm nút Tra cứu thay cho Enter). In biên lai là phần
sau khi đã lập phiếu, không tính: "In biên lai" → "In…" → hộp thoại máy in.
Đóng hộp "Đã thu tiền" thì tiêu điểm về ô mã phòng, mã cũ được bôi đen để
gõ ngay hộ tiếp theo; mã sai cũng bôi đen để gõ đè.

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Mã phòng | Ô nhập + nút Tra cứu (Enter cũng tra cứu); có tiêu điểm khi mở màn | Không phân biệt hoa thường; không có thì báo "Không tìm thấy phòng" | AC-030, AC-031 |
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
  có số trang và chỗ ký. Nút "Xuất Excel" cạnh "In danh sách" ghi tệp
  `.xlsx` cùng các trường (tên gợi ý `danh-sach-tam-vang-2026-11-12.xlsx`),
  bị vô hiệu khi chưa có danh sách (AC-014). Cần PDF thì in qua máy in PDF.

### Báo cáo thu theo tháng (US-018)

Phần dưới của màn Thống kê.

| Thành phần | Kiểu | Ràng buộc | Nguồn |
|---|---|---|---|
| Tháng | Danh sách chọn | 12 tháng gần nhất, mặc định tháng hiện tại theo giờ CSDL | US-018 |
| Xuất Excel | Nút chính | Tháng chưa có phiếu thu: báo "Tháng 11/2026 chưa có phiếu thu nào, không có dữ liệu để xuất." và không mở hộp chọn tệp; có thì hỏi chỗ lưu (lọc `*.xlsx`, tên gợi ý `bao-cao-thu-2026-11.xlsx`) | AC-050, AC-051 |
| Tệp | `.xlsx` | Tiêu đề "BÁO CÁO THU TIỀN THÁNG 11/2026", "Lập ngày …"; cột STT, mã căn hộ, chủ hộ, mỗi khoản/quỹ một cột (bắt buộc trước, theo tên), tổng, ngày thu, số biên lai; dòng "Tổng cộng" | giả định 41, 42 |
| Tệp đang mở | Nhãn đỏ | "Tệp đang được mở trong chương trình khác. Đóng tệp rồi thử lại." | giả định 42 |

