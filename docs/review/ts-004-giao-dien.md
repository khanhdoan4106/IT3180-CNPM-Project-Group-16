# Rà soát giao diện TS-004: dễ dùng cho người lớn tuổi

Story: TS-004 (AC-072, NFR-008). Thiết kế đối chiếu: Figma
"BlueMoon – Thiết kế giao diện", khung "Thành phần"
([ui.md](../design/ui.md#thành-phần)).

Ảnh chụp bằng JavaFX chạy không màn hình (Monocle), không điều khiển màn
hình: `ChupManHinhTest` tải từng FXML như `BoCucFxmlTest` (không CSDL, chữ
động điền bản dài nhất, các khung chỉ hiện lúc chạy được bật lên) ở
1120×645, Trang chính 1440×1024. Ảnh "trước" chụp từ mã của `main`
(`b9f221e`) trước khi sửa CSS/FXML; ảnh "sau" từ nhánh này. Chụp lại:

```
mvn test -Dtest=ChupManHinhTest,ThuMotHoNamBuocTest -DchupAnh=docs/review/ts-004 -DchupTienTo=sau
```

Vì không có CSDL, bảng trong ảnh trống, trừ ảnh Thu phí sau khi tra cứu
(`ThuMotHoNamBuocTest`, dữ liệu giả) và Trang chính có số liệu
(`TrangChinhSoLieuTest`, mục [Trang chính có số liệu](#trang-chính-có-số-liệu)).
Thanh menu trong mọi ảnh dùng tên "Nguyễn Thị Thu Hương", vai trò "Quản trị
viên".

## Tóm tắt

| Hạng mục | Trước | Sau | Kiểm bằng |
|---|---|---|---|
| Cỡ chữ gốc | 14 px; trục và số trên cột biểu đồ 13 px; trang in 12 px | 16 px; không chữ nào trên màn hình dưới 14 px; tiêu đề 24/20, số lớn 34 | `BoCucFxmlTest` (mọi chữ của mọi màn), `DeDocTest#chuGoc16ChuLoiVaChuNutChinhDungMau` |
| Độ cao | Nút ~28, ô nhập ~26, dòng bảng 24 | Nút 44, ô nhập 40, dòng bảng và tiêu đề cột 40 | `DeDocTest#oNhapNutDongBangDuCao`, `BoCucFxmlTest` |
| Tương phản | Chữ đỏ `#DC2626` 4,1:1 trên nền màn hình; "Đã thu hôm nay" `#15803D` 4,1:1 trên nhãn xám; phiếu hủy `#94A3B8` 2,6:1; dòng chọn mặc định chữ trắng trên xanh nhạt | Mọi cặp chữ/nền ≥ 4,5:1 (lỗi `#B91C1C`, thành công `#166534`, phiếu hủy chữ phụ, dòng chọn nền `#DBEAFE` chữ đậm) | `DeDocTest#moiCapChuNenDatAa` (26 cặp đo từ CSS) |
| Tiêu điểm | Vòng mờ mặc định của JavaFX | Viền 2 px màu chính cho ô nhập, danh sách chọn, bảng, nút; vòng ngoài cho nút chính | `DeDocTest#oDangCoTieuDiemCoVienMauChinh` |
| Kiểu thành phần | Nút, tab, bảng, danh sách chọn, hộp thoại kiểu JavaFX mặc định (xám, gradient) | Một bộ kiểu theo Figma trên mọi màn và hộp thoại (`KieuHopThoai` gắn CSS cho mọi cửa sổ mới) | Ảnh dưới đây |
| Thu một hộ | 4 thao tác nhưng mở màn phải bấm vào ô mã phòng; sau tra cứu tiêu điểm vẫn ở ô mã; thu xong tiêu điểm ở nút Thu | 4 thao tác; tiêu điểm tự ở ô mã khi mở màn, sang "Tiền mặt" sau tra cứu, về ô mã (bôi đen) sau khi thu | `ThuMotHoNamBuocTest` |
| Chữ trắng trên nền trắng | Trang chính: tiêu đề tháng, nội dung nhắc chốt, số lớn của hai thẻ, tiêu đề "Thu phí 6 tháng gần nhất" và "Biến động nhân khẩu gần đây" không thấy (xem ảnh) | Hiện đúng màu chữ | `DeDocTest#chuTrongVungCuonTrangChinhLaMauChu` |
| CSS đọc dở | – (phát hiện khi làm) | JavaFX bỏ ngang phần còn lại của tệp CSS khi gặp selector xuống dòng, không báo lỗi; nay mọi selector một dòng | `DeDocTest#javaFxDocHetMoiLuatCss` |

Nguyên nhân dòng "chữ trắng trên nền trắng": vùng cuộn của Trang chính đặt
`-fx-background: transparent`, JavaFX suy màu chữ mặc định của nhãn từ biến
này ra màu trắng. Nhãn có lớp màu riêng (`.phu-de`) không bị. Nay `.root`
đặt thẳng các biến màu chữ của JavaFX (`-fx-text-background-color`, …) về
`-mau-chu`.

## Từng màn

| Màn | Vấn đề (trước) | Đã sửa |
|---|---|---|
| Đăng nhập | Chữ 14, ô nhập thấp, viền mặc định | Chữ 16, ô 40 px bo 8, nút chính 44 px |
| Trang chính | Chữ trắng trên nền trắng (tiêu đề, số liệu, tiêu đề khung); nút "Đăng xuất" kiểu mặc định; lời chào "Xin chào, Nguyễn Thị Thu Hương (Quản trị…" bị cắt ngay ở 1440 px; bảng "Biến động nhân khẩu gần đây" 6 cột trong khung hẹp, chữ 16 px bị cắt hết | Hiện đúng chữ; "Đăng xuất" là nút phụ; nút menu có viền trắng khi có tiêu điểm; người dùng theo Figma (xem [Thanh menu](#thanh-menu)); bảng biến động gọn 4 cột (Ngày, Căn hộ, Họ tên, Biến động), ghi chú và người ghi hiện khi rê chuột lên dòng, khung rộng 560 px |
| Hộ gia đình | Cột bảng hẹp, khi chữ 16 thì cắt tiêu đề ("Mã căn…", "T…"); form phải chật | Cột rộng theo tiêu đề (bảng cuộn ngang khi cần); form 430 px, cột nhãn 156 px, ô ngày/danh sách chọn co được; hàng "Xem lịch sử biến động" / "Đánh dấu chuyển đi" xuống dòng khi chật; "Đánh dấu chuyển đi" là nút nguy hiểm |
| Nhân khẩu | Như Hộ gia đình; ô chọn hộ tô xanh như dòng đang chọn | Như Hộ gia đình (cột nhãn 140 px); ô chọn hộ nền trắng |
| Khoản thu | Cột "Trạng thái" bị cắt khi chữ 16 | Cột có độ rộng tối thiểu 120 |
| Công nợ | Tab, bảng, nút "In thông báo phí" kiểu mặc định; tab "Chốt công nợ" trống ~100 px giữa tiêu đề và bảng (ba nhãn thông báo rỗng vẫn giữ chỗ) | Tab gạch chân, bảng theo bộ thành phần, nút có danh sách cùng kiểu nút phụ; nhãn trạng thái, hộ bị bỏ qua, tóm tắt chỉ hiện và giữ chỗ khi có chữ (`visible`/`managed` gắn với chữ trong FXML) |
| Thu phí | Tab, bảng, nút kiểu mặc định; tên quỹ dài đè cột "Đã đóng" khi chữ 16; tiêu điểm không theo luồng thu | Theo bộ thành phần; tên quỹ xuống dòng; tiêu điểm theo luồng AC-072; chữ hướng dẫn "Gõ mã phòng rồi nhấn Enter (hoặc bấm Tra cứu)" |
| Thống kê | Danh sách chọn kiểu mặc định | Theo bộ thành phần |
| Tài khoản | Bảng tài khoản trống hiện "No content in table"; form Đăng ký: chữ 16 làm ô nhập còn 47 px | Chữ báo trống tiếng Việt; form Đăng ký đặt nhãn trên ô (kiểu "Ô nhập" của Figma) |

### Thanh menu

Theo Figma "Người dùng": hai dòng căn phải trước nút Đăng xuất, tên (15 px
Medium, trắng) trên, vai trò (14 px) dưới, bỏ "Xin chào,". Vai trò dùng
`-mau-dai` (`#EFF6FF`, 4,7:1 trên nền menu) thay màu trắng mờ 85% của Figma
(4,1:1). Tên dài hơn chỗ trống thì rút gọn "…", tooltip trên cả hai dòng
hiện "Tên – Vai trò". Để ở 1120 px vẫn đủ chỗ cho "Nguyễn Thị Thu Hương",
mục menu dùng cỡ của Figma (15 px Medium), đệm ngang 8, cách nhau 8 (giữ quy
tắc nút cách nút ≥ 8 thay vì 4 px của Figma); nút Đăng xuất đệm ngang 14.
Ở 1120 px còn dư khoảng 3 px. Kiểm ở 1120, 1366, 1440 px:
`BoCucFxmlTest#thanhMenuHienDuTenVaVaiTro`; tên dài rút gọn kèm tooltip:
`NguoiDungThanhMenuTest`.

### Trang chính có số liệu

Số liệu giả tháng 12/2026: thu 6 tháng T7…T12 từ 186 đến 214 triệu, tháng 12
chưa kết thúc (cột đậm), 3 biến động gần đây. `TrangChinhSoLieuTest` dựng
màn ở 1440×1024 và 1120×645 rồi kiểm: nhãn T7…T12, số trên đầu cột, số trên
trục Y, tên hai trục "Triệu đồng" và "Tháng (năm 2026)" không chồng nhau,
không ra ngoài biểu đồ, không dưới 14 px; số không đè lên cột; cột T12 đậm;
không nhãn, tiêu đề cột, ô bảng nào bị cắt "…". Ở 1120×645 trang cuộn, phần
dưới biểu đồ nằm dưới mép cửa sổ (ảnh chụp đúng phần thấy được).

| 1440×1024 | 1120×645 |
|---|---|
| ![](ts-004/sau-trang-chinh-so-lieu-1440x1024.png) | ![](ts-004/sau-trang-chinh-so-lieu-1120x645.png) |

### Ảnh trước / sau

| Màn | Trước | Sau |
|---|---|---|
| Đăng nhập | ![](ts-004/truoc-dang-nhap.png) | ![](ts-004/sau-dang-nhap.png) |
| Trang chính (1440×1024) | ![](ts-004/truoc-trang-chinh.png) | ![](ts-004/sau-trang-chinh.png) |
| Hộ gia đình | ![](ts-004/truoc-ho-gia-dinh.png) | ![](ts-004/sau-ho-gia-dinh.png) |
| Nhân khẩu | ![](ts-004/truoc-nhan-khau.png) | ![](ts-004/sau-nhan-khau.png) |
| Khoản thu | ![](ts-004/truoc-khoan-thu.png) | ![](ts-004/sau-khoan-thu.png) |
| Công nợ – Chốt công nợ | ![](ts-004/truoc-cong-no-tab1.png) | ![](ts-004/sau-cong-no-tab1.png) |
| Công nợ – Hộ nợ đọng | ![](ts-004/truoc-cong-no-tab2.png) | ![](ts-004/sau-cong-no-tab2.png) |
| Thu phí – Thu tiền | ![](ts-004/truoc-thu-phi-tab1.png) | ![](ts-004/sau-thu-phi-tab1.png) |
| Thu phí – sau khi tra cứu A0601 (dữ liệu giả) | – | ![](ts-004/sau-thu-phi-da-tra-cuu.png) |
| Thu phí – Phiếu thu hôm nay | ![](ts-004/truoc-thu-phi-tab2.png) | ![](ts-004/sau-thu-phi-tab2.png) |
| Thống kê | ![](ts-004/truoc-thong-ke.png) | ![](ts-004/sau-thong-ke.png) |
| Tài khoản – Tài khoản của tôi | ![](ts-004/truoc-tai-khoan-tab1.png) | ![](ts-004/sau-tai-khoan-tab1.png) |
| Tài khoản – Quản lý tài khoản | ![](ts-004/truoc-tai-khoan-tab2.png) | ![](ts-004/sau-tai-khoan-tab2.png) |
| Tài khoản – Nhật ký thao tác | ![](ts-004/truoc-tai-khoan-tab3.png) | ![](ts-004/sau-tai-khoan-tab3.png) |

## Chữ hiển thị

Rà chữ trên mọi màn: nút là động từ, thông báo lỗi nói phải làm gì, xưng
hô trung tính (không "bạn", không "quý khách"). Đã đổi:

| Màn | Chỗ | Cũ | Mới |
|---|---|---|---|
| Thu phí | Dải thông tin khi chưa tra cứu | Nhập mã phòng rồi bấm Tra cứu | Gõ mã phòng rồi nhấn Enter (hoặc bấm Tra cứu) |
| Thu phí | Nút | Lịch sử thu của hộ | Xem lịch sử thu |
| Thu phí – Phiếu thu hôm nay | Lỗi khi chưa chọn phiếu | Chọn một phiếu trong bảng | Bấm chọn một phiếu trong bảng trước |
| Hộ gia đình | Nút | Lịch sử biến động | Xem lịch sử biến động |
| Nhân khẩu | Nút | Danh sách tạm vắng | Xem danh sách tạm vắng |
| Khoản thu | Lỗi "Sửa đơn giá" khi chưa chọn | Chưa chọn khoản thu | Bấm chọn khoản phí dịch vụ hoặc phí quản lý trong bảng trước |
| Tài khoản – Quản lý | Lỗi khi chưa chọn | Chưa chọn tài khoản | Bấm chọn một tài khoản trong bảng trước |
| Tài khoản – Quản lý | Bảng trống | No content in table | Chưa có tài khoản nào |
| Trang chính | Thẻ số liệu khi lỗi đọc | Chưa đọc được số liệu thu phí / Chưa đọc được số liệu nợ | Chưa đọc được số liệu. Bấm BlueMoon để thử lại. |

Giữ nguyên: thông báo nghiệp vụ do Service trả (vd "Không tìm thấy phòng"
AC-031, "Chưa chọn hình thức thanh toán" AC-038) vì chữ nằm trong AC và
TS-004 không đổi Service; các nút đã là động từ ("Lưu", "Tra cứu", "Thu tất
cả", "Ghi nhận", "Hủy phiếu", "Xuất Excel"…).

## Còn lại

- Kiểm tay AC-072 với người chưa dùng ứng dụng: bảng ghi kết quả ở
  [testing.md](../testing.md#5-việc-còn-kiểm-tay), chưa thử.
- Figma: sửa màu "lỗi" và "thành công" của phương án A theo
  [ui.md](../design/ui.md#phương-án-màu).
- Ở 1120×645 bảng thành viên của màn Nhân khẩu chỉ thấy khoảng hai dòng (dòng
  cao 40); cửa sổ thật phóng to nên không gặp, ghi lại để xét khi có frame
  Figma cho màn này.
- Lỗi nhập liệu dưới từng ô (thiết kế) vẫn là một nhãn thông báo chung mỗi
  màn, ngoài phạm vi TS-004.
