# Nhật ký thay đổi

Ghi các thay đổi của sản phẩm theo từng bản phát hành, mới nhất ở trên. Khuôn
dạng theo [Keep a Changelog](https://keepachangelog.com/vi/1.1.0/). Mã story
(US-xxx) và tiêu chí chấp nhận (AC-xxx) xem ở
[Product Backlog](docs/requirements/08-product-backlog.md) và
[User Story](docs/requirements/07-user-stories.md); số `#` là Pull Request.

## [Chưa phát hành]

### Thêm

- **Nhật ký thao tác (TS-002, AC-068, AC-069):** mọi thao tác thay đổi dữ
  liệu quan trọng (lập/hủy phiếu thu, sửa số tiền phải nộp, tạo khoản thu tự
  nguyện, sửa đơn giá, chốt công nợ, thêm/sửa/chuyển đi hộ và nhân khẩu, đổi
  chủ hộ, ghi tạm trú/tạm vắng, tạo tài khoản, đổi vai trò, khóa/mở khóa,
  đặt lại mật khẩu) được ghi cùng người làm, thời điểm, đối tượng và nội
  dung thay đổi, trong cùng giao dịch với thao tác (giả định 44). Sửa hộ,
  nhân khẩu chỉ ghi trường thật sự đổi, vd "Diện tích: 75,50 m² → 80,00 m²".
  Không ghi mật khẩu, đăng nhập hay việc tự đổi mật khẩu.
- **Xem nhật ký:** tab "Nhật ký thao tác" trong màn Tài khoản, chỉ Quản trị
  viên thấy; lọc theo khoảng ngày (mặc định 7 ngày gần nhất) và loại thao
  tác, mới nhất ở trên, chỉ đọc.
- **Tổng thu tháng ở Trang chính (US-016, AC-046, AC-047):** tiêu đề "Tổng
  quan tháng …", thẻ "Đã thu tháng này" (tổng phiếu chưa hủy lập trong
  tháng, giả định 24; tháng chưa có phiếu là 0 đ) kèm tỷ lệ đã thu của công
  nợ tháng này, hoặc "chưa chốt công nợ". Biểu đồ cột "Thu phí 6 tháng gần
  nhất" theo quy tắc biểu đồ trong `ui.md`: tên hai trục, trục Y từ 0 chia
  theo mốc tròn, đơn vị triệu hoặc nghìn đồng, số trên đầu cột, cột tháng
  chưa kết thúc tô đậm kèm chú thích. Số liệu đọc lại khi đăng nhập và khi
  bấm logo.
- **Thống kê theo khoản (US-019, AC-052, AC-053):** mục menu "Thống kê"
  giữa Thu phí và Tài khoản. Chọn khoản bắt buộc và tháng đã chốt, hoặc một
  quỹ tự nguyện (trong thời gian thu của quỹ, giả định 45); xem tổng đã thu,
  tổng phải thu và số hộ đã đóng, hoặc số hộ đã đóng góp và số hộ ghi không
  đóng. Chưa có giao dịch thì tổng là 0 đ.

- **Hộ nợ đọng (US-017, AC-048, AC-049):** màn Công nợ chia hai tab, "Chốt
  công nợ" (giữ nguyên chức năng) và "Hộ nợ đọng": lọc "Nợ từ 3 tháng trở
  lên" hoặc "Tất cả hộ còn nợ"; bảng căn hộ, chủ hộ, số tháng nợ, nợ từ
  tháng, tổng tiền nợ, kèm dòng tổng. Số tháng nợ là số tháng chốt còn khoản
  chưa đóng (giả định 25); khoản đã sửa về 0 đ không phải nợ; phiếu đã hủy
  thì khoản trở lại nợ; tính cả hộ đã chuyển đi. Trang chính có thêm thẻ
  "Hộ còn nợ" (số hộ còn nợ, số hộ nợ từ 3 tháng trở lên), bấm để mở tab
  này.

- **Báo cáo thu ra Excel (US-018, AC-050, AC-051):** màn Thống kê có thêm
  phần "Báo cáo thu theo tháng": chọn tháng, "Xuất Excel" ra tệp `.xlsx`
  theo mẫu sổ thu (giả định 41): mỗi phiếu thu chưa hủy một dòng, mỗi
  khoản/quỹ một cột, tổng, ngày thu, số biên lai, dòng "Tổng cộng". Tiền
  là ô số, ngày là ô ngày nên Excel cộng, lọc được. Tháng chưa có phiếu
  thì báo ngay, không tạo tệp.
- **Xuất danh sách tạm vắng (US-004, AC-014):** nút "Xuất Excel" cạnh "In
  danh sách", cùng các trường như bản in.
- Ghi tệp an toàn: ghi tệp tạm rồi đổi tên, lỗi giữa chừng không để lại
  tệp hỏng; tệp đang mở trong Excel thì nhắc đóng tệp rồi thử lại.
- **Sao lưu dữ liệu (TS-001, AC-066, AC-067, giả định 43):** script
  `scripts/sao-luu.ps1` sao lưu CSDL bằng `mysqldump --single-transaction`
  (kèm trigger, không khóa bảng) ra tệp `bluemoon-yyyyMMdd-HHmmss.sql`, ghi
  mỗi lần chạy một dòng `sao-luu.log` (thời điểm, THÀNH CÔNG/LỖI, tệp, dung
  lượng, thời gian), giữ 30 bản mới nhất và chỉ xóa bản cũ khi lần này
  thành công; tệp dở hoặc bị cắt bị xóa, lỗi thì Task Scheduler ghi lần chạy
  lỗi. `scripts/dang-ky-lich-sao-luu.ps1` đăng ký tác vụ "BlueMoon – Sao lưu
  hằng ngày" lúc 22:00, chạy bù khi máy bật lại. `scripts/khoi-phuc-thu.ps1`
  khôi phục vào CSDL thử và so số dòng từng bảng, tổng tiền phiếu thu,
  trigger nhật ký với CSDL gốc. Mật khẩu MySQL chỉ ở login-path
  (`mysql_config_editor`) hoặc tệp tùy chọn ngoài repo. Hướng dẫn sao lưu,
  khôi phục thử và khôi phục thật: `setup.md` mục 7.

### Đổi

- **Giao diện dễ dùng cho người lớn tuổi (TS-004, AC-072, NFR-008):**
  chữ gốc 16 px (trước 14), không chữ nào trên màn hình dưới 14 px; nút cao
  44, ô nhập 40, dòng bảng 40; mọi cặp chữ/nền đạt tương phản ≥ 4,5:1 (màu
  lỗi đổi `#DC2626` → `#B91C1C`, thành công `#15803D` → `#166534`, phiếu đã
  hủy dùng chữ phụ); ô, nút, bảng đang có tiêu điểm có viền màu chính. Nút
  chính/phụ/nguy hiểm, ô nhập, danh sách chọn, bảng (tiêu đề, dòng chọn,
  dòng xen kẽ, dòng trống), tab và hộp thoại cùng một kiểu theo Figma trên
  mọi màn, không còn kiểu JavaFX mặc định
  ([ui.md](docs/design/ui.md#thành-phần)). Form Hộ gia đình, Nhân khẩu,
  Đăng ký tài khoản và vài cột bảng sắp lại để chữ 16 px không bị cắt ở
  1120×645.
- **Thu một hộ trong 4 thao tác (AC-072):** mở màn Thu phí là gõ được mã
  phòng ngay; tra cứu xong tiêu điểm sang "Tiền mặt"; thu xong (hoặc mã sai)
  tiêu điểm về ô mã phòng, mã cũ bôi đen để gõ hộ tiếp theo. Bảng các bước:
  [ui.md](docs/design/ui.md#thu-phí-us-007-us-010--us-014).
- Chữ hiển thị: nút là động từ ("Xem lịch sử thu", "Xem lịch sử biến
  động", "Xem danh sách tạm vắng"), thông báo khi chưa chọn dòng nói rõ
  phải bấm chọn gì, Thu phí hướng dẫn "Gõ mã phòng rồi nhấn Enter (hoặc
  bấm Tra cứu)". Bảng các chữ đã đổi:
  [ts-004-giao-dien.md](docs/review/ts-004-giao-dien.md#chữ-hiển-thị).

### Thư viện

- Thêm Apache POI `poi-ooxml` 5.5.1 (ghi `.xlsx`, giả định 42) và
  `log4j-to-jul` 2.24.3 (runtime) để log của POI đi qua
  `java.util.logging`, không in cảnh báo log4j ra console
  ([architecture.md](docs/design/architecture.md#quy-ước)).

### Sửa

- **Không sửa được số tiền của khoản vừa được thu ở máy khác (AC-026):**
  khi một máy đang sửa số tiền phải nộp và máy khác lập phiếu thu đúng khoản
  đó cùng lúc, trước đây việc sửa vẫn có thể thành công, làm phiếu thu lệch
  số tiền phải nộp. Nay kiểm tra "đã thu" đọc bản mới nhất dưới khóa
  (`FOR SHARE`) và từ chối sửa. Rà mọi giao dịch DAO theo quy tắc "quyết định
  dưới khóa thì đọc có khóa":
  [doc-co-khoa-trong-giao-dich.md](docs/review/doc-co-khoa-trong-giao-dich.md).
- Tab "Nhật ký thao tác": "Rê chuột lên một ô…" đổi thành "Di chuột lên một
  ô…".
- **Trang chính: chữ trắng trên nền trắng.** Tiêu đề tháng, nội dung nhắc
  chốt, số lớn của hai thẻ và tiêu đề hai khung không thấy được vì vùng cuộn
  đặt nền trong suốt và JavaFX suy màu chữ mặc định ra trắng. Nay màu chữ
  mặc định luôn là `-mau-chu`.
- Bảng tài khoản trống hiện "No content in table" (chữ mặc định của
  JavaFX); nay là "Chưa có tài khoản nào".

### Cơ sở dữ liệu

Thêm bảng `nhat_ky` và hai trigger chặn `UPDATE`, `DELETE` trên bảng này;
không đổi bảng đã có. Nâng từ `v0.3.0` chỉ cần chạy lại `schema.sql` bằng
root, dữ liệu cũ giữ nguyên. Khi MySQL bật binary log, tạo trigger cần quyền
`SUPER` hoặc `log_bin_trust_function_creators = 1`
([setup.md](docs/setup.md), mục 2 và mục 4).

### Kiểm thử

359 test đơn vị và 62 kiểm thử tích hợp trên MySQL 8.0 (thêm `NhatKyIT`,
`NhatKyServiceTest`, `ThaoTacTest`, `NoiDungNhatKyTest`, `ThongKeIT`,
`ThongKeServiceTest`, `ThangTrucYTest`, `BieuDoThuPhiTest`, `DongTyLeTest`,
`NoDongIT`, `NoDongServiceTest`, `TheHoConNoTest`, `BaoCaoThuIT`,
`BaoCaoThuServiceTest`, `BaoCaoThuExcelTest`, `TamVangExcelTest`,
`TepExcelTest`, `DanhSachTamVangTest`, `ScriptSaoLuuTest`, `SaoLuuIT`,
`SuaSoTienDocKhoaIT`, `ThuMotHoNamBuocTest`, `DeDocTest`, `ChupManHinhTest`).
`ThuMotHoNamBuocTest` thu một hộ trên màn Thu phí thật bằng phím và chuột
giả lập, đếm thao tác và kiểm tiêu điểm sau mỗi bước; `BoCucFxmlTest` kiểm
thêm cỡ chữ ≥ 14 px, độ cao nút và ô, khoảng cách giữa nút; `ChupManHinhTest`
chụp PNG các màn cho báo cáo rà soát (chỉ chạy khi có `-DchupAnh`).
Script sao lưu có thêm kiểm thử bằng `mysqldump` giả
(`src/test/scripts/kiem-thu-sao-luu.ps1`) và CI chạy thật sao lưu, khôi phục
thử, sao lưu với mật khẩu sai trên MySQL của job `build-mysql`.

## [v0.3.0] – 12/11/2026 – Sprint 3

Hoàn thành thu phí từ tra cứu đến biên lai. Gồm các Pull Request #28 – #35.

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
  rơi sang năm sau). Cột do MySQL tự điền (giờ biến động, giờ sửa khoản) lệch
  7 giờ khi máy chủ MySQL chạy giờ UTC (Docker, máy chủ đám mây). Giờ ghi và
  đọc CSDL không còn phụ thuộc múi giờ máy chạy ứng dụng hay máy chủ MySQL:
  mỗi phiên MySQL đặt giờ Việt Nam (`+07:00`).
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

269 test đơn vị và 30 kiểm thử tích hợp trên MySQL 8.0
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
