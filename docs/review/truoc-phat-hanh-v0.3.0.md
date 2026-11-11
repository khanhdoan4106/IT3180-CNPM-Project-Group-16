# Rà soát trước phát hành v0.3.0

Ngày rà soát: 11/11/2026 · Phạm vi: `main` tại `9a53d11` (Sprint 1–3, PR #24 – #33) ·
Giai đoạn 1: chỉ đọc, chạy thử và báo cáo, chưa sửa mã, chưa commit.

## Tóm tắt

**Khuyến nghị: sửa mục 1–2 rồi phát hành.** Mục 1 trống; mục 2 có 4 việc nhỏ,
tổng khoảng nửa ngày, gói trong một PR. Nếu Sprint Review gấp, phát hành ngay
cũng được: không có lỗi sai tiền, sai quyền hay hỏng dữ liệu.

- `mvn clean verify` (Temurin 21.0.12, Maven 3.9.16 của IntelliJ): **248/248
  test đơn vị đạt**, không cảnh báo biên dịch. `mvn verify -Pmysql` trên MySQL
  8.0.46 tạm: thêm **22/22 kiểm thử tích hợp đạt** (CongNoIT 4, DongHoCsdlIT 1,
  GiaoDichHoGiaDinhIT 2, PhieuThuIT 11, SeedIT 4). Khớp số trong CHANGELOG,
  `docs/releases/v0.3.0.md`, `docs/testing.md` và log CI.
- Kịch bản demo `setup.md` mục 5, chạy bằng Service trên CSDL trống dựng từ
  `schema.sql` + `seed.sql`, hai vai trò: **31/31 bước đạt**, số liệu đúng như
  tài liệu (A0601: 4 khoản, nợ 1.812.000 đ; thu 1.912.000 đ). `seed.sql` chạy
  lần hai không đổi số dòng.
- Tiền, thu trùng, hủy phiếu, số biên lai, phân quyền ở Service, BCrypt (cost
  12) và SQL (mọi truy vấn là `PreparedStatement`, chỉ nối chuỗi dấu `?`) đều
  đúng.
- Điều đáng sửa nhất là phím Enter bấm nhầm nút ở ba màn (N1) và thông báo
  lỗi cấu hình chưa nói phải mở lại ứng dụng sau khi sửa, hệ quả của #32 (N3).

**Không làm được trong lần này (nói rõ để khỏi tưởng đã kiểm):**

- **Bộ bài tập gốc không có trong repo hay thư mục dự án.** Phần đối chiếu đề
  dựa vào `docs/requirements/01-wish-list.md` (RAW-001 – RAW-040, trích từ đề)
  và `11-chot-va-gia-dinh.md`. Mục 3 của báo cáo ghi hệ quả.
- **Không thao tác GUI thật** (đúng quy tắc): hộp thoại in, trang in xem trước,
  Tab/Enter trên cửa sổ thật, hai máy thật (AC-032, AC-070, AC-071 và các bước
  kiểm tay ở `testing.md` mục 5). Phần phím Enter kiểm bằng JavaFX chạy ngầm
  (Monocle, cửa sổ ảo, sự kiện phím tạo trong chương trình, mục N1).
- Thứ tự Tab thật trên màn hình chưa kiểm; chỉ đọc thứ tự khai báo trong FXML.
- Figma: so được frame Thu phí (tab Thu tiền) và Biên lai bằng ảnh chụp; các
  frame còn lại chỉ đối chiếu qua bảng "Trạng thái so với thiết kế" của `ui.md`.

Môi trường thử: `mysqld` riêng cổng 3308, thư mục dữ liệu trong thư mục tạm của
phiên; CSDL `bluemoon` và MySQL80 không bị đụng (biến môi trường
`BLUEMOON_DB_URL/USER/PASSWORD` đều được đặt để không đọc `config/db.properties`).

## Xử lý sau khi duyệt

Scrum Master duyệt báo cáo ngày 11/11/2026 và yêu cầu làm cả mục 2 lẫn mục 3
(trừ G4) trong một PR `fix/truoc-phat-hanh-v0.3.0`, kèm bốn việc theo bản
Figma đã cập nhật. Mỗi mục một commit, kèm test; mỗi test đều được chạy với
mã cũ hoặc mã cố ý sai để chứng minh nó trượt.

| Phát hiện | Xử lý | Test (bản cũ hoặc sai trượt, bản mới đạt) |
|---|---|---|
| N1 | Bỏ nút mặc định toàn màn ở Nhân khẩu, Hộ gia đình, Khoản thu; mỗi khung khai báo `enterChay` trong FXML, `DieuHuong` gắn qua `PhimEnter`; ô tìm kiếm hộ có `onAction` riêng (`d0cc3f7`) | `PhimEnterTest`: bản cũ trượt 4/5 |
| N2 | `ThuPhiService.kiemTraHinhThuc` dùng chung; `onThu` gọi trước hộp xác nhận (`0e65015`) | `ThuPhiServiceTest#kiemTraHinhThucChoManHinhTruocKhiXacNhan`: bỏ kiểm tra thì 2 test trượt. Thứ tự "trước hộp xác nhận" nằm ở `onThu`, không có test tự động (hộp thoại modal) |
| N3 | Hai thông báo hiện cho người dùng và thông báo `CauHinhException` thêm "tắt và mở lại ứng dụng"; `MAT_KET_NOI` gợi ý user, mật khẩu; sửa mục lỗi thường gặp ở `setup.md` (`fbcbddf`) | `LoiCsdlTest#thongBaoCauHinhNoiRoPhaiMoLaiUngDung`, `DatabaseConfigTest`: bản cũ trượt 2 |
| N4 | Sửa chú thích giả định 14 và `schema.sql` (`0f474f0`) | `TruyVetTaiLieuTest` (RAW khớp bảng truy vết): bản cũ trượt 2/2 |
| G1 | Enter ở màn Tài khoản: họ tên, số điện thoại → "Lưu thay đổi"; mật khẩu → "Đổi mật khẩu"; đăng ký → "Đăng ký" (`531f360`) | `PhimEnterTest#taiKhoanMoiFormGuiDungNutCuaMinh`: bản cũ trượt |
| G2 | DAO ghi và đọc `DATETIME` bằng `LocalDateTime` (`setObject`/`getObject`), không qua `Timestamp`; `Launcher` đặt múi giờ mặc định `Asia/Ho_Chi_Minh`; bỏ câu "máy nên đặt múi giờ" ở `sprint-3-ra-soat.md` (`ec7634f`) | `MuiGioIT` (JVM UTC, New York, Kiritimati, Việt Nam: lập phiếu 23:30 ngày 31/12, hủy 23:45, giờ biến động, giờ sửa khoản): bản cũ trượt 5/7, trong đó `chk_pt_nam` từ chối; `LauncherTest` |
| G3 | Thay `docCauHinhKhongNem` (chỉ `assertNotNull`, đọc file thật của máy) bằng `thieuFileKhongNemMaTraCauHinhRong` (`c299296`) | Mã cố ý sai (ném lỗi khi thiếu file): test mới trượt |
| G4 | Giữ nguyên (ô tích đầu cột bảng Thu phí, Sprint 4) | – |
| Figma 5, 6 | Logo là nút (tay, gợi ý "Về trang chủ"), bấm về Trang chính và làm mới; `ThanhMenu` đặt nền `-mau-chinh-dam` cho nút của màn đang mở, về trang chủ bỏ chọn (`c4b657a`) | `TrangChinhMenuTest`: 3 bản sai (không thêm lớp, không hiện lại, không bỏ chọn) đều trượt |
| Figma 7 | `-mau-phu` đổi `#64748b` → `#526077` (5,8:1 trên `#F1F5F9`); tiêu đề cột bảng dùng cùng màu; bảng màu phương án A ở `ui.md` (`8e611e6`) | `MauPhuTest`: CSS cũ trượt 2/3 |
| Figma 8 | Thêm mục "Quy tắc biểu đồ" và ghi logo, nút đang chọn, phím Enter vào `ui.md` (`def207b`) | `UiMdTest`: bản cũ trượt 2/3 |

Số test sau khi sửa: **269 test đơn vị** (trước 248) và **29 kiểm thử tích hợp**
(trước 22). CHANGELOG, `docs/releases/v0.3.0.md` và `docs/testing.md` đã cập
nhật cùng PR; `testing.md` có thêm `TestingMdTest` kiểm mọi tham chiếu test
trong bảng AC → test còn tồn tại (bắt được tham chiếu `docCauHinhKhongNem`
trở thành cũ sau G3).

Ghi nhận thêm khi làm: nạp `cursor="HAND"` trong FXML gây `LoadException`
(không đổi được thành `Cursor`), nên con trỏ bàn tay của logo đặt bằng CSS
`-fx-cursor: hand`; `.phieu-da-huy` vẫn dùng `-mau-phu-nhat` (`#94a3b8`, 2,5:1
trên trắng) vì đó là dòng cố ý mờ đi, ngoài phạm vi mục 7.

## 1. Chặn phát hành

Không có.

Đã chủ động tìm và không thấy: sai làm tròn tiền (diện tích × đơn giá làm tròn
`HALF_UP` đến đồng, số tiền nhập chỉ nhận số nguyên đến 1 tỷ), thu trùng
(khóa dòng, ràng buộc `uq_ptct_phai_nop_da_thu`), hủy phiếu không khôi phục
công nợ, thủ quỹ làm được việc của quản trị viên, mật khẩu lưu rõ, SQL ghép
chuỗi, ràng buộc CSDL lệch kiểm tra Service, tài liệu cài đặt sai.

## 2. Nên sửa trước khi phát hành

### N1. Phím Enter bấm nhầm nút ở ba màn

- **Vị trí:** `nhan-khau.fxml:193` (nút mặc định "Lưu") với các ô của khung đợt
  tạm trú/tạm vắng (`loaiDotCombo`, `dotTuPicker`, `dotDenPicker` dòng 106 – 116,
  `lyDoField`); `khoan-thu.fxml:85` (nút mặc định "Tạo khoản thu") với
  `donGiaField` dòng 53; `ho-gia-dinh.fxml:162` (nút mặc định "Lưu") với
  `timKiemField` dòng 31.
- **Mô tả:** các ô này không có `onAction`, nên Enter chuyển cho nút mặc định
  của cả cảnh: nhập xong "Lý do" của đợt tạm vắng mà bấm Enter thì **lưu form
  nhân khẩu** thay vì "Ghi nhận" đợt, đợt không được ghi và thông báo hiện ở
  nhãn của form kia; sửa đơn giá rồi Enter thì **tạo khoản thu tự nguyện**;
  gõ ô tìm kiếm hộ rồi Enter thì bấm "Lưu".
- **Cách tái hiện (đã chạy):** chương trình thử dựng từng FXML ở cửa sổ ảo
  Monocle, bắn Enter vào từng ô, ghi nút nào chạy. Kết quả: mọi ô của
  `nhan-khau.fxml` (kể cả 4 ô đợt) và `ho-gia-dinh.fxml` (kể cả ô tìm kiếm) và
  `donGiaField` đều chạy nút mặc định "Lưu"/"Tạo khoản thu"; đối chứng
  `dang-nhap.fxml` chạy đúng nút "Đăng nhập". Không có dữ liệu bị hỏng vì
  Service từ chối form thiếu, nhưng người dùng tưởng đã ghi đợt.
- **Đề xuất:** gắn `onAction` đúng việc cho ô đợt (`onGhiDot`), `donGiaField`
  (`onSuaDonGia`), `timKiemField` (tìm, không lưu). Thêm test Monocle kiểu
  `BoCucFxmlTest` kiểm "Enter ở ô X chạy hành động Y".
- **Công sức:** 1 – 2 giờ.

### N2. Thu phí báo thiếu hình thức thanh toán sau khi đã hỏi xác nhận

- **Vị trí:** `ThuPhiController.java:299-334` (`onThu`: xác nhận ở dòng 313,
  gọi Service ở dòng 319); `ThuPhiService.java:209-211`.
- **Mô tả:** chưa chọn "Tiền mặt/Chuyển khoản" mà bấm Thu: hộp "Lập phiếu thu
  cho căn hộ …?" hiện trước, người thu bấm Đồng ý rồi mới thấy lỗi "Chưa chọn
  hình thức thanh toán" ở nhãn dưới. Đúng AC-038 nhưng thứ tự gây khó chịu ở
  thao tác dùng nhiều nhất của Thủ quỹ.
- **Đề xuất:** kiểm trước khi hỏi xác nhận (tổng > 0 và chưa chọn hình thức
  thì báo lỗi ngay, đặt tiêu điểm vào nhóm nút chọn). Một dòng điều kiện.
- **Công sức:** 30 phút, kèm một test controller-free (tách hàm kiểm).

### N3. Thông báo lỗi cấu hình chưa nói phải mở lại ứng dụng (hệ quả của #32)

- **Vị trí:** `LoiCsdl.java:13-18` (`MAT_KET_NOI`, `SAI_CAU_HINH`);
  `Database.java:97` (thông báo `CauHinhException`);
  `DatabaseConfigTest#thieuCauHinhVanBaoDungLoi` (so khớp nguyên văn thông báo).
- **Mô tả:** từ #32 cấu hình chỉ đọc một lần. Người dùng thấy "Thiếu hoặc sai
  cấu hình … Báo người quản trị hệ thống", sửa `config/db.properties` rồi bấm
  lại: **vẫn lỗi cũ** cho tới khi tắt mở ứng dụng. Chỉ `setup.md` mục 6 và
  CHANGELOG nói điều này; người đang đứng trước màn hình lỗi không đọc chúng.
  Sai mật khẩu MySQL trong file còn rơi vào thông báo "Kiểm tra máy chủ MySQL
  và mạng", không gợi ý xem lại user/password.
- **Cách tái hiện:** chạy ứng dụng không có `config/db.properties`, đăng nhập
  (báo thiếu cấu hình), tạo file đúng, đăng nhập lại: vẫn báo thiếu
  (`DatabaseConfigTest#thieuCauHinhVanBaoDungLoi` khẳng định chính hành vi này).
- **Đề xuất:** thêm câu "Sửa xong, tắt và mở lại ứng dụng." vào hai thông báo;
  thêm "kiểm tra user, mật khẩu" vào `MAT_KET_NOI`. Cập nhật test so khớp và
  mục lỗi thường gặp ở `setup.md` cho cùng chữ.
- **Công sức:** 20 phút.

### N4. Hai chú thích tài liệu trích sai mã yêu cầu

- **Vị trí:** `docs/requirements/11-chot-va-gia-dinh.md:26` (giả định 14 ghi căn
  cứ "RAW-004, RAW-005", đúng là RAW-007, RAW-008 về khoảng đơn giá);
  `database/schema.sql:19` (bảng `ho_gia_dinh` ghi "RAW-001, RAW-010, RAW-030,
  RAW-033"; RAW-010 là quỹ tự nguyện, RAW-030 là thu hộ điện nước, RAW-033 là
  nhật ký, không liên quan bảng này; đúng là RAW-001).
- **Đề xuất:** sửa hai chú thích. Không đổi hành vi.
- **Công sức:** 10 phút.

## 3. Làm tốt hơn

Xếp theo giá trị so với công sức.

### G1. Thu tiền và đổi mật khẩu bằng bàn phím

- **Vị trí:** `thu-phi.fxml:178` (nút Thu không phải nút mặc định, ô "Lần này"
  tạo bằng mã ở `ThuPhiController.java:237-241` không có hành động Enter);
  `tai-khoan.fxml` (ba form: họ tên và số điện thoại, đổi mật khẩu, tạo tài
  khoản, đều không có Enter); `cong-no.fxml`.
- **Mô tả:** chạy Monocle cho thấy Enter ở các ô này không làm gì. Dùng được
  bằng Tab rồi Space, nhưng Enter không gửi form, khác `dang-nhap` và các form
  có nút mặc định. Với Thủ quỹ thu hàng chục phiếu mỗi ngày, Enter ở ô tiền
  quỹ để thu (vẫn qua hộp xác nhận) tiết kiệm đáng kể.
- **Đề xuất:** đặt "Thu" làm nút mặc định của tab Thu tiền, thêm `onAction`
  cho ô cuối của mỗi form tài khoản. Sau khi làm, kiểm lại ma trận Enter bằng
  chương trình Monocle ở N1.
- **Công sức:** 1 giờ.

### G2. Múi giờ máy chạy ứng dụng ≠ Việt Nam làm lệch giờ lưu

- **Vị trí:** 4 chỗ `Timestamp.valueOf(LocalDateTime)` trong DAO (vd
  `PhieuThuDaoImpl.java:355, 405`); `ThuPhiService.java:212-215`;
  `docs/setup.md` không nhắc yêu cầu múi giờ. Đã nêu một phần ở `sprint-3-ra-soat.md`
  (mục "Xử lý sau khi duyệt").
- **Cách tái hiện (đã chạy):** ghi `2026-12-31 23:30` với URL
  `serverTimezone=Asia/Ho_Chi_Minh`: JVM `Asia/Ho_Chi_Minh` lưu đúng; JVM `UTC`
  lưu `2027-01-01 06:30`; JVM `America/New_York` lưu `2027-01-01 11:30`.
- **Hậu quả:** trong cùng ứng dụng giờ đọc lại vẫn khớp, nên phiếu hiện bình
  thường. Nhưng cột `thoi_diem` trong CSDL lệch so với giờ thật (báo cáo viết
  thẳng bằng SQL lệch), và phiếu lập trong những giờ cuối năm có `nam` (Java)
  khác `YEAR(thoi_diem)` nên ràng buộc `chk_pt_nam` từ chối, thu thất bại.
  Máy Windows đặt múi giờ Việt Nam (mặc định ở Việt Nam) không gặp.
- **Đề xuất:** `TimeZone.setDefault(DongHoCsdl.VN)` ở `Launcher.main`, hoặc ghi
  bằng `Timestamp.from(ldt.atZone(VN).toInstant())`; thêm một dòng vào mục
  chuẩn bị demo của `setup.md`. Máy dùng để demo ở nước ngoài, hoặc máy ảo đặt
  UTC, là tình huống thật.
- **Công sức:** 30 phút, kèm một IT chạy với `-Duser.timezone=UTC`.

### G3. Test yếu: `DatabaseConfigTest#docCauHinhKhongNem`

- **Vị trí:** `DatabaseConfigTest.java:22-24`.
- **Mô tả:** chỉ `assertNotNull(Database.docCauHinh())`, đọc `config/db.properties`
  thật của máy chạy test nên kết quả phụ thuộc máy. Là test duy nhất trong 248
  test không kiểm được gì có thể sai (quét toàn bộ test: các test còn lại đều
  có khẳng định, hoặc gọi hàm trợ giúp `loi(...)` có khẳng định bên trong).
- **Đề xuất:** xóa, hoặc đọc từ file tạm có `url`, `user` rồi kiểm giá trị (đã
  có test tương tự). Nhớ cập nhật số test (248 → 247) trong CHANGELOG,
  `docs/releases/v0.3.0.md`, `testing.md` nếu xóa.
- **Công sức:** 10 phút.

### G4. Bảng Thu phí thiếu ô tích chọn tất cả ở đầu cột (Figma có)

- **Vị trí:** `thu-phi.fxml:85-107`; frame Figma "Thu phí – Thu tiền – 1120x645".
- **Mô tả:** Figma có ô tích ở tiêu đề cột "Thu"; code chỉ có hai liên kết
  "Chọn tất cả / Bỏ chọn tất cả" (chức năng tương đương). Thuần hình thức, nêu để
  biết: không đề xuất làm riêng.
- **Công sức:** để Sprint 4 (TS-004).

## 4. Ghi nhận / để Sprint 4

- **AC-014** (xuất Excel/PDF danh sách tạm vắng) và toàn bộ US-016 – US-019:
  Sprint 4, đúng như backlog và CHANGELOG.
- **Kiểm tay còn lại** (`testing.md` mục 5): in thật, hai máy thật, AC-032
  (tài liệu ghi 21 ms mỗi lần tra cứu; không đo lại).
- **Phiên cũ sau khi bị khóa:** tài khoản bị khóa hoặc đổi vai trò vẫn dùng
  tiếp được màn đang mở (Service chỉ kiểm đối tượng phiên; đã chạy: sau khi
  khóa, tra cứu bằng phiên cũ vẫn được). Bị chặn khi mở màn mới
  (`lamMoiPhien` báo "Tài khoản đã bị khóa"). Đúng giả định 17, nêu để biết.
- **Phí quản lý dưới 7.000 đ/m² bị chặn hẳn** (giả định 14), trong khi AC-016
  viết "mặc định 7.000, BQT có thể thay đổi". Quy tắc đã được nhóm chốt và có
  test; chỉ rủi ro nếu đề gốc cho đổi xuống. Không đối chiếu được vì thiếu Bộ
  bài tập trong repo.
- **Figma:** Thu phí (tab Thu tiền) và Biên lai khớp: dải thông tin hộ, "Chọn
  tất cả / Bỏ chọn tất cả", khung quỹ 3 cột "Quỹ / Đã đóng / Lần này", nút
  chuyển "Thu tất cả" khi chọn đủ, khung "BẢN IN LẠI", STT, bằng chữ, hai chỗ ký
  (tên người thu in sẵn). Menu Figma có "Thống kê" (Sprint 4). Lệch đã biết và
  đã ghi ở `ui.md`: lỗi hiện dưới từng ô, cỡ chữ 16 px, ba phương án màu, bộ lọc
  khoản thu (Sprint 4).
- **Liên kết và Mermaid:** 29 tệp Markdown, không liên kết nội bộ hỏng; ba liên
  kết `../../releases/tag/vX` trong README chỉ chạy trên GitHub (quy ước cũ), mục
  `v0.3.0` chỉ hết 404 sau khi tạo release. Cả 5 khối Mermaid qua bộ phân tích
  cú pháp `mermaid@10.9.1`; chưa kiểm bằng trình render của GitHub.
- **Mã:** không còn lớp không ai dùng (8 Controller chỉ được FXML gọi), không
  TODO, không `System.out`; `printStackTrace` duy nhất ở `LoiKhongMongDoi.java:25`
  là cố ý (báo lỗi không lường trước).
- **Job `build-mysql`:** `DatabaseConfigTest#thieuCauHinhVanBaoDungLoi` bị bỏ
  qua (`assumeTrue` biến `BLUEMOON_DB_URL` chưa đặt); đúng thiết kế, nên "Skipped: 1"
  trong log CI không phải lỗi.
- **Máy phát triển:** `mvn` không có trong PATH, `java` mặc định là bản 25;
  chạy build cần `JAVA_HOME` JDK 21 và Maven của IntelliJ. `setup.md` mục 6 đã
  có dòng cho `'mvn' is not recognized`; không có dòng nào cho JDK mặc định
  khác 21 (build vẫn qua nhờ `release 21`).

## Bảng AC → kết quả đối chiếu

Cột "Bằng chứng trong repo" lấy từ `docs/testing.md` mục 4. Toàn bộ 122 tham
chiếu `Lớp#phương_thức` trong bảng đó đều tồn tại trong mã test. "Rà soát này"
ghi bước đã chạy lại qua Service trên MySQL tạm, hoặc xác nhận test có thật và
qua. Không có AC nào "Hoàn thành" bị phát hiện là chưa đạt; các dòng ◐ là phần
chỉ kiểm tay được.

| AC | Nội dung | Bằng chứng trong repo | Rà soát này |
|---|---|---|---|
| AC-001 | tạo hộ khi nhập đủ | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-002 | lưu đúng diện tích | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-003 | thiếu thông tin thì báo, không lưu | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-004 | trùng mã căn hộ | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-005 | thêm nhân khẩu | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-006 | quan hệ với chủ hộ | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-007 | chỉ một chủ hộ | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-008 | cảnh báo trùng CCCD | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-009 | ghi nhận tạm trú, tạm vắng | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-010 | thiếu ngày thì báo lỗi | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-011 | ngày kết thúc trước ngày bắt đầu | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-012 | danh sách tạm vắng | Đạt (test tự động, in thật kiểm tay) | xem danh sách tạm vắng hôm nay: ra đúng Hoàng Văn Nam (A0601) |
| AC-013 | không có dữ liệu | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-015 | phí dịch vụ theo diện tích × đơn giá | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-016 | phí quản lý mặc định 7.000 đ/m² | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-017 | đơn giá ngoài khoảng 2.500–16.500 | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-018 | tổng tiền chính xác | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-019 | tạo khoản tự nguyện | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-020 | trùng tên trong cùng đợt | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-021 | không nhập số tiền cố định vẫn lưu | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-022 | đóng số tiền bất kỳ | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-023 | 0 đ là "không đóng" | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-024 | nhiều quỹ một màn | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-025 | sửa khoản chưa thu | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-026 | khoản đã thu thì từ chối sửa | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-027 | chốt công nợ tháng | Đạt (test tự động) | chốt tháng trước và tháng này: 8 khoản mỗi tháng, số tiền = diện tích × đơn giá |
| AC-028 | chốt lại không tạo trùng | Đạt (test tự động) | chốt lại cùng tháng: bổ sung 0 khoản |
| AC-029 | hộ chưa có diện tích | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-030 | hiển thị hộ và khoản chưa đóng | Đạt (test tự động) | tra cứu "a0601": đúng hộ, 4 khoản, nợ 1.812.000 đ |
| AC-031 | không tìm thấy phòng | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-032 | dưới 3 giây | Kiểm tay: đo 21 ms mỗi lần tra cứu | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-033 | thu tất cả, công nợ về 0 | Đạt (test tự động) | thu tất cả: một phiếu, nợ về 0; thu lại cùng khoản bị chặn |
| AC-034 | chỉ thu khoản đã chọn | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-035 | tổng tự cộng, có ngày thu | Đạt phần tự động; nhãn tổng trên màn hình kiểm tay (chưa chạy trong lần rà soát này) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-036 | lưu hình thức thanh toán | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-037 | lưu người thu và thời điểm | Đạt (test tự động) | phiếu ghi đúng tên tài khoản thu |
| AC-038 | chưa chọn hình thức thì báo lỗi | Đạt (test tự động) | không chọn hình thức thì báo lỗi |
| AC-039 | nội dung biên lai | Đạt phần tự động; trang in xem tận mắt (chưa chạy trong lần rà soát này) | số biên lai PT-2026-00001, 00002, 00003, không cấp lại số của phiếu hủy |
| AC-040 | "bản in lại" | Đạt phần tự động; khung "BẢN IN LẠI" trên trang in kiểm tay (chưa chạy trong lần rà soát này) | lần in thứ hai: soLanIn = 1 (bản in lại) |
| AC-041 | hủy trong ngày, khôi phục công nợ | Đạt (test tự động) | hủy phiếu: 4 khoản và quỹ trở lại chưa đóng |
| AC-042 | phiếu ngày trước thì từ chối | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-043 | phiếu hủy giữ vết kèm người hủy | Đạt (test tự động) | phiếu hủy còn lưu, có người hủy |
| AC-044 | thông báo liệt kê khoản | Đạt (test tự động) | thông báo phí tất cả hộ: 3 hộ, số tiền đúng |
| AC-045 | không có khoản thì báo | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-054 | đăng ký hợp lệ | Đạt (test tự động) | Quản trị viên tạo tài khoản Thủ quỹ |
| AC-055 | trùng tên đăng nhập | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-056 | mật khẩu quá ngắn | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-057 | đăng nhập đúng | Đạt (test tự động) | admin / Admin@123 đăng nhập được (băm BCrypt của seed) |
| AC-058 | sai mật khẩu | Đạt (test tự động) | sai mật khẩu bị từ chối |
| AC-059 | chưa đăng nhập thì yêu cầu | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-060 | Thủ quỹ chỉ thu phí và tra cứu | Đạt phần tự động; ẩn menu kiểm tay (chưa chạy trong lần rà soát này) | Thủ quỹ bị chặn ở 5 chức năng của Quản trị viên |
| AC-061 | Quản trị viên dùng mọi chức năng | Đạt phần tự động; ẩn menu kiểm tay (chưa chạy trong lần rà soát này) | Quản trị viên tạo tài khoản được |
| AC-062 | không có quyền thì không thấy dữ liệu | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-063 | đổi mật khẩu | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-064 | mật khẩu cũ sai | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-065 | sửa thông tin cá nhân | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-070 | ứng dụng kết nối MySQL | Đạt phần tự động; khởi động ứng dụng thật kiểm tay (chưa chạy trong lần rà soát này) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-071 | hai máy thấy cùng dữ liệu | Đạt phần tự động; hai máy thật kiểm tay (chưa chạy trong lần rà soát này) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-072 | tối đa 5 bước, chữ rõ | Đạt phần tự động; số bước thao tác kiểm tay: lập phiếu 4–5 thao tác (chưa chạy trong lần rà soát này) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-073 | đổi chủ hộ khi còn người khác | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-074 | hộ chỉ còn một người | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-075 | người được chọn đã chuyển đi | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-076 | ghi biến động khi lưu thành công | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-077 | tạm trú, tạm vắng vào lịch sử | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-078 | lỗi giữa chừng không ghi lịch sử | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |
| AC-079 | xem lịch sử của hộ | Đạt (test tự động) | test có thật, qua trong `mvn clean verify`/`-Pmysql` |

## Khuyến nghị và kế hoạch PR

**Sửa mục 1–2 rồi phát hành.** Mục 1 không có việc; mục 2 gói vào một PR nhỏ.

**PR `fix/truoc-phat-hanh-v0.3.0`** (tách từ `main`, khoảng nửa ngày), mỗi mục
một commit, mỗi commit tự build và qua test:

1. N1: `onAction` đúng việc cho ô đợt, đơn giá, tìm kiếm + test Monocle phím Enter.
2. N2: kiểm hình thức thanh toán trước hộp xác nhận.
3. N3: câu "tắt và mở lại ứng dụng" trong hai thông báo + cập nhật test và `setup.md`.
4. N4: sửa hai chú thích mã yêu cầu.
5. Nếu thêm test mới (N1) hoặc xóa G3: cập nhật số test trong `CHANGELOG.md`,
   `docs/releases/v0.3.0.md`, `docs/testing.md` cùng PR; CHANGELOG ghi N1–N3
   vào mục `[Chưa phát hành]` hoặc `[v0.3.0]` tùy bạn quyết (tag chưa tạo nên
   sửa thẳng vào `[v0.3.0]` được).

Chờ CI xanh `build` và `build-mysql`, rồi tạo release `v0.3.0` như cũ. G1 – G3
có thể vào cùng PR nếu muốn (thêm khoảng 2 giờ) hoặc để đầu Sprint 4. PR 2
(`chore/0.4.0-snapshot`) vẫn chờ bạn báo đã tạo release.

Dừng tại đây, chờ duyệt.
