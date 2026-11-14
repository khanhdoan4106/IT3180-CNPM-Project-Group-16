# Rà soát trước phát hành v1.0.0

Ngày rà soát: 13 – 14/11/2026 · Phạm vi: `main` tại `ff84e59` (Sprint 4: US-016,
US-019 #39, TS-002 #38, US-017 #40, US-018 và AC-014 #41, TS-001 #42, sửa
AC-026 #43, TS-004 #44).

## Tóm tắt

**Khuyến nghị: phát hành.** Không có lỗi chặn phát hành; mục 2 chỉ có việc
cập nhật tài liệu theo số phiên bản (làm luôn trong PR `release/v1.0.0`) và
một số điểm ghi nhận để nhóm quyết.

- `mvn clean verify -Pmysql` (JDK 21, Maven 3.9 của IntelliJ) trên MySQL
  8.0.46 tạm: **366 test đơn vị đạt** (0 lỗi; 3 bỏ qua có chủ ý:
  `ChupManHinhTest` chỉ chạy khi có `-DchupAnh`, hai test của
  `DatabaseConfigTest` tự bỏ qua khi đã đặt biến `BLUEMOON_DB_*`) và **62/62
  kiểm thử tích hợp đạt**.
- CI của `main` tại `ff84e59`: hai job `build` và `build-mysql` **đạt**
  (cũng đạt ở `b9f221e`, `4db58a8`).
- **79/79 AC** (AC-001 → AC-079; US-024, US-025 là Won't, không có AC) có
  dòng trong bảng AC → test của `docs/testing.md` mục 4; mọi tham chiếu
  `Lớp#phương_thức` trong bảng đều tồn tại (`TestingMdTest`). 15 AC đánh dấu
  ◐ (tự động một phần, phần còn lại kiểm tay), AC-032 chỉ kiểm tay.
- Luồng nghiệp vụ chính chạy qua Service trên MySQL thật dựng từ
  `schema.sql` + `seed.sql`, hai vai trò: **10/10 bước đạt** (mục 5).
- Năm điểm dễ sai của Sprint 4 (nhật ký trong giao dịch, nợ đọng, xuất
  Excel, sao lưu, phân quyền) đã đọc mã và đối chiếu test: không thấy lỗi
  (mục 4). Một điểm cần nhóm xác nhận: Thủ quỹ **được** xem Thống kê và xuất
  Excel theo bảng phân quyền đã chốt, khác yêu cầu rà soát (G1).

**Không làm được trong lần này (nói rõ để khỏi tưởng đã kiểm):**

- **Không chạy giao diện thật trên màn hình.** Môi trường rà soát không điều
  khiển màn hình; luồng chính chạy qua Service, giao diện chỉ được kiểm bằng
  JavaFX chạy ngầm (Monocle) trong bộ test (`BoCucFxmlTest`,
  `ThuMotHoNamBuocTest`, `TrangChinhSoLieuTest`…). Nhóm cần chạy ứng dụng
  thật với hai vai trò theo `testing.md` mục 5 trước Sprint Review (mục 6).
- In thật, hai máy thật, lịch sao lưu Task Scheduler: kiểm tay (`testing.md`
  mục 5).

Môi trường thử: `mysqld` riêng cổng 3399, thư mục dữ liệu trong thư mục tạm
của phiên; CSDL `bluemoon` và dịch vụ MySQL80 không bị đụng
(`BLUEMOON_DB_URL/USER/PASSWORD` đều đặt).

## 1. Chặn phát hành

Không có.

Đã chủ động tìm và không thấy: nhật ký ghi ngoài giao dịch hoặc còn lại khi
thao tác hoàn tác, ghi mật khẩu vào nhật ký, nợ đọng bỏ sót hộ đã chuyển đi
hoặc đếm trùng tháng, phiếu đã hủy lọt vào báo cáo Excel, tạo tệp cho tháng
không có phiếu, mật khẩu MySQL trong script hay repo, script khôi phục thử
ghi đè CSDL `bluemoon`, Thủ quỹ mở được nhật ký hoặc quản lý tài khoản.

## 2. Cập nhật tài liệu khi phát hành (làm trong PR `release/v1.0.0`)

| # | Vị trí | Đang ghi | Sửa thành |
|---|---|---|---|
| D1 | `README.md`, bảng Sprint | Sprint 4 – `v1.0.0` "Chưa bắt đầu" | "Hoàn thành", liên kết release |
| D2 | `README.md`, "Phạm vi v1.0" | Không nhắc sao lưu và nhật ký thao tác | Thêm dòng "Vận hành: sao lưu hằng ngày, nhật ký thao tác" |
| D3 | `docs/setup.md`, bảng nâng cấp | "`v0.4.0` (Sprint 4, chưa phát hành)" | `v1.0.0` (Sprint 4) |
| D4 | `docs/testing.md` mục 2 | "350 test đơn vị và 62 kiểm thử tích hợp" (số trước TS-004) | 366 và 62 |
| D5 | `08-product-backlog.md` | Sprint 4 "Đang làm" | Hoàn thành, `v1.0.0` |
| D6 | `CHANGELOG.md` | Mục "[Chưa phát hành]" | "[v1.0.0] – 14/11/2026 – Sprint 4" và liên kết |
| D7 | `pom.xml` | `0.4.0-SNAPSHOT` | `1.0.0` |

Bảng truy vết (`09-traceability-matrix.md`) và giả định 41 – 45
(`11-chot-va-gia-dinh.md`) khớp mã: báo cáo thu lọc `da_huy = FALSE` theo
khoảng thời gian (41), chỉ xuất `.xlsx` qua `TepExcel.ghiAnToan` (42), script
`mysqldump --single-transaction` đọc mật khẩu từ login-path (43), nhật ký ghi
trong cùng giao dịch, trigger chặn sửa/xóa (44), thống kê theo tháng chốt
hoặc khoảng thu của quỹ (45). `ui.md` đã cập nhật ở #44.

## 3. Ghi nhận (không sửa, để nhóm quyết)

### G1. Thủ quỹ xem được Thống kê và xuất báo cáo thu

- **Vị trí:** `ThongKeService` (5 chỗ), `BaoCaoThuService`, `NoDongService`
  cho cả `QUAN_TRI` và `THU_QUY`; menu "Thống kê" hiện với cả hai vai trò.
- **Căn cứ hiện tại:** bảng phân quyền `docs/design/architecture.md` ghi "Có"
  cho Thủ quỹ ở "thống kê theo khoản", "hộ nợ đọng", "xuất báo cáo thu ra
  Excel"; US-018 viết "Là Thủ quỹ, tôi muốn xuất báo cáo thu tiền ra Excel".
- **Khác với yêu cầu rà soát** ("Thủ quỹ không mở được Thống kê"). Nhật ký
  thao tác và quản lý tài khoản thì đúng là chỉ Quản trị viên (Service chặn,
  tab bị gỡ khỏi màn Tài khoản).
- **Đề xuất:** giữ như tài liệu (Thủ quỹ cần báo cáo thu để đối chiếu sổ
  quỹ). Nếu nhóm muốn chặn thì đổi 7 lời gọi `PhanQuyen.yeuCau`, ẩn nút menu
  và sửa bảng phân quyền, US-018 — khoảng 1 giờ kèm test.

### G2. User ứng dụng có `GRANT ALL`, trigger nhật ký chỉ chặn UPDATE/DELETE

- **Vị trí:** `docs/setup.md` bước 3 (`GRANT ALL ON bluemoon.*`);
  `schema.sql` (`trg_nky_chan_sua`, `trg_nky_chan_xoa`).
- **Mô tả:** ứng dụng không có mã nào sửa/xóa nhật ký và trigger chặn câu
  `UPDATE`/`DELETE`, nhưng ai có mật khẩu user `bluemoon` vẫn `DROP TRIGGER`
  hoặc `TRUNCATE nhat_ky` được. Đúng phạm vi AC-069 (ứng dụng không cho
  sửa/xóa); không chống được người cố ý có mật khẩu CSDL.
- **Đề xuất (v2.0 hoặc khi triển khai thật):** user ứng dụng chỉ cấp
  `SELECT, INSERT, UPDATE, DELETE`, chạy `schema.sql` bằng root.

### G3. Hai AC ◐ không có bước tương ứng ở `testing.md` mục 5

AC-035 ("nhãn tổng trên màn hình kiểm tay") và AC-070 ("khởi động ứng dụng
thật kiểm tay") ghi phần kiểm tay ở bảng AC nhưng không có dòng trong mục 5.
Phần nhãn tổng đã có `ThuMotHoNamBuocTest` kiểm "Thu tất cả" và tổng sau tra
cứu; khởi động thật nằm trong bước chạy ứng dụng của nhóm (mục 6). Thêm hai
dòng vào mục 5 khi tiện.

### G4. Biên bản họp các sprint trước

`docs/meeting-notes/` chỉ có `README.md`: chưa có biên bản Sprint 1 – 3. Lần
này chỉ tạo khung Sprint 4 Review và Retrospective (PR phát hành); biên bản
cũ không tự tạo.

## 4. Soát lỗi tinh vi Sprint 4

| Điểm | Đã kiểm | Kết quả |
|---|---|---|
| Nhật ký cùng giao dịch | 8 DAO gọi `NhatKyDaoImpl.ghi` trên cùng `Connection` sau `setAutoCommit(false)`, lỗi thì `rollback`; `NhatKyIT#ghiNhatKyLoiThiThaoTacCungHoanTac`, `#thaoTacXungDotHoacTrungKhoaKhongDeLaiDong` | Đạt |
| Trigger chặn sửa/xóa | `trg_nky_chan_sua`, `trg_nky_chan_xoa` (`SIGNAL 45000`); `NhatKyIT#mysqlTuChoiSuaVaXoaNhatKy`, `#chayLaiSchemaKhongMatNhatKyVaVanChanSua`, `SaoLuuIT#triggerNhatKyVanChanSuaXoaSauKhoiPhuc` | Đạt (xem G2) |
| Không ghi mật khẩu | Tạo tài khoản ghi họ tên, vai trò; đặt lại mật khẩu ghi "Đặt mật khẩu tạm"; tự đổi mật khẩu không ghi; luồng mục 5 khẳng định không có mật khẩu trong nội dung | Đạt |
| Nợ đọng gồm hộ đã chuyển đi | `NoDongDaoImpl` không lọc `trang_thai`; `NoDongIT#hoDaChuyenDiVanCo` | Đạt |
| Số tháng nợ khi tháng này đã/chưa chốt | Đếm `COUNT(DISTINCT dot_thu_id)` của khoản > 0 chưa có phiếu còn hiệu lực; `dot_thu` có `UNIQUE (thang)` nên không đếm trùng khi chốt lại; tháng chưa chốt không có `phai_nop` nên không tính | Đạt |
| Excel: phiếu hủy, tổng cộng, kỳ trống | `pt.da_huy = FALSE`; dòng "Tổng cộng" cộng từng cột (`BaoCaoThuExcelTest#dongTongCongCongTungCotTien`); tháng không có phiếu bị từ chối trước khi mở hộp chọn tệp (`BaoCaoThuServiceTest#thangKhongCoPhieuBiTuChoiTruocKhiTaoTep`) | Đạt |
| Sao lưu: mật khẩu | Không có tham số mật khẩu (`ScriptSaoLuuTest#khongScriptNaoNhanMatKhauQuaThamSo`); repo chỉ có giá trị mẫu `db.properties.example` và mật khẩu giả của CI | Đạt |
| Khôi phục thử không đụng `bluemoon` | Đích mặc định `bluemoon_khoi_phuc_thu`; đích là `bluemoon` hoặc trùng nguồn thì từ chối nếu thiếu `-GhiDe`, có `-GhiDe` vẫn phải gõ lại tên CSDL | Đạt |
| Phân quyền Thủ quỹ | Nhật ký, quản lý tài khoản, tạo khoản thu: Service chặn và giao diện ẩn; Thống kê, Excel: được dùng theo thiết kế | Đạt (xem G1) |

## 5. Luồng nghiệp vụ chính (qua Service, MySQL thật)

CSDL dựng từ `schema.sql` + `seed.sql`; đồng hồ là giờ máy chủ CSDL
(`DongHoCsdl`) như khi chạy thật. Chương trình thử tạm, không đưa vào repo.

| # | Bước | Vai trò | Kết quả |
|---|---|---|---|
| 1 | Đăng nhập `admin` / `Admin@123`; tạo tài khoản Thủ quỹ rồi đăng nhập bằng nó | QTV, TQ | Đạt |
| 2 | Tạo khoản tự nguyện "Quỹ rà soát v1.0.0"; Thủ quỹ thử tạo thì bị từ chối | QTV, TQ | Đạt |
| 3 | Chốt công nợ tháng hiện tại | QTV | Đạt, có khoản phải nộp |
| 4 | Tra cứu "a0601", thu tất cả khoản + 50.000 đ quỹ, Tiền mặt | TQ | Đạt, tổng = nợ + 50.000 đ, hộ hết nợ |
| 5 | In biên lai hai lần | TQ | Đạt, lần 2 là bản in lại |
| 6 | Hủy phiếu (của mình) kèm lý do; thu lại bằng Chuyển khoản | TQ | Đạt, khoản trở lại chưa đóng rồi thu được |
| 7 | Thống kê phí dịch vụ tháng này; tổng quan Trang chính; hộ nợ đọng | TQ, QTV | Đạt |
| 8 | Báo cáo thu tháng, xuất `.xlsx` | QTV, TQ | Đạt, phiếu đã hủy không có, tệp được tạo |
| 9 | Nhật ký hôm nay | QTV | Đạt: có tạo tài khoản, tạo khoản, chốt, 2 lần lập phiếu, hủy phiếu; không có mật khẩu |
| 10 | Thủ quỹ xem nhật ký, danh sách tài khoản | TQ | Bị từ chối, đúng |

## 6. Việc nhóm cần làm trước Sprint Review

1. Chạy ứng dụng thật (`com.bluemoon.Launcher`) với hai vai trò theo đúng
   luồng ở mục 5, trên giao diện: kiểm menu theo vai trò, in biên lai ra máy
   in PDF, xuất Excel và mở tệp bằng Excel thật.
2. Các bước kiểm tay còn lại ở `testing.md` mục 5 (hai máy, lịch sao lưu,
   một người chưa dùng thử thu một hộ cho AC-072).
3. Quyết G1 (Thủ quỹ và Thống kê) trước khi tag.
4. Điền biên bản Sprint 4 Review và Retrospective
   (`docs/meeting-notes/sprint-4-review-retrospective.md`).
