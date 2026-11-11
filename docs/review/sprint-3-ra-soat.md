# Rà soát Sprint 3 (PR #28)

Ngày rà soát: 09/11/2026 · Phạm vi: nhánh `feature/sprint-3-thu-phi` tại
`6e4d14c` (10 commit sau `v0.2.0`, PR #28) · Giai đoạn 1: chỉ đọc, chạy thử
và báo cáo, chưa sửa mã.

## Tóm tắt

- Build và test: `mvn clean verify` (Temurin 21.0.12, Maven 3.9.16) thành
  công, **215/215 test đơn vị đạt**. `mvn verify -Pmysql` trên MySQL 8.0.46
  tạm: thêm **13/13 kiểm thử tích hợp đạt**. CI của PR (`build`) xanh.
- CSDL: `schema.sql` + `seed.sql` chạy sạch trên CSDL trống; chạy lại
  `seed.sql` không đổi gì; chạy `schema.sql` trên CSDL đang ở `v0.2.0` thêm
  đúng 3 bảng mới và **giữ nguyên dữ liệu cũ** (checksum 8 bảng cũ không đổi).
- Kết quả: **0 Lỗi**, **6 Nên sửa**, **11 Ghi nhận**. Không tìm thấy lỗi
  sai nghiệp vụ, sai hoặc mất tiền, sai quyền, hỏng dữ liệu. Số biên lai,
  chống thu trùng, hủy phiếu và khôi phục công nợ đều đúng, kể cả khi chạy
  12 luồng thu, hủy đồng thời (mục 2.2).
- Điểm đáng sửa trước khi merge: **N1** (deadlock giữa sửa khoản phải nộp và
  thu phí, chạy thử tái hiện được; thu hoặc sửa bị hủy giữa chừng và báo lỗi
  CSDL chung chung, không mất dữ liệu) và **N2** (số tiền lớn bị cắt hoặc
  xuống dòng trên biên lai, thông báo phí). Các mục còn lại làm trong PR này
  hoặc đầu Sprint 4 tùy Scrum Master.
- Không đụng CSDL `bluemoon` và dịch vụ MySQL80: mọi phép thử chạy trên một
  `mysqld` riêng (cổng 3308, thư mục dữ liệu trong thư mục tạm), đã tắt và
  xóa sau khi rà soát.

## Xử lý sau khi duyệt

Scrum Master duyệt báo cáo ngày 09/11/2026. Các mục được chọn đã sửa trên
nhánh `feature/sprint-3-thu-phi` (PR #28), mỗi mục một commit, kèm test.

| Phát hiện | Xử lý | Test |
|---|---|---|
| N1 | Đã sửa theo yêu cầu: `suaSoTien` chạy trong một giao dịch, khóa `ho_gia_dinh` rồi `phai_nop` (cùng thứ tự với lập phiếu), kiểm tra chưa thu rồi `UPDATE`. Lỗi MySQL 1213 và 1205 đổi thành `DangBiKhoaException` ở DAO (áp dụng cho lập phiếu, hủy phiếu, sửa số tiền); Service hiện "Có người khác đang thao tác trên hộ này, vui lòng thử lại" | `DangBiKhoaExceptionTest`, 2 test Service (thu, sửa), IT `nhieuLuongVuaSuaVuaThuCungMotKhoanKhongDeadlock` |
| N2 | Đã sửa: cột số tiền rộng 160, không xuống dòng, trang rộng 600; đủ cho "1.000.000.000 đ" ở dòng khoản và dòng tổng của biên lai, thông báo phí | `MauInTest` (9 test, đo chữ skin đang vẽ và chiều cao một dòng; bản cũ trượt 6) |
| N3 | Đã sửa: in danh sách tạm vắng đặt sẵn khổ ngang (`PageOrientation.LANDSCAPE`, đổi được trong hộp thoại máy in); chia trang bằng cách thêm dòng cho đến khi trang đo sau layout vượt khung giấy ngang, thay cho 18 dòng cố định | `MauInTest` (2 test: mọi trang vừa khung, không sót dòng; bản cũ trượt). Hướng giấy trong hộp thoại máy in không kiểm tự động được (cần máy in) |
| N4 | Đã sửa theo yêu cầu: `DongHoCsdl extends Clock` (đọc `SELECT NOW()` một lần, cộng thời gian trôi qua bằng `nanoTime`, đọc lại sau 30 giây, múi giờ Asia/Ho_Chi_Minh) dùng cho `ThuPhiService`, `CongNoService` ở các màn hình; test vẫn dùng `Clock.fixed`. Thay giả định 38; cập nhật `erd.md`, `architecture.md`, `class-diagram.md` | `DongHoCsdlTest`, IT `DongHoCsdlIT` |
| N5 | Đã sửa: job `build-mysql` (service `mysql:8.0`, CSDL `bluemoon_test`, `mvn -B verify -Pmysql`); job `build` giữ nguyên. Ghi vào `docs/setup.md` | Job `build-mysql` trên GitHub Actions |
| N6 | Đã sửa: giữ "Mã phòng" ở màn Thu phí, thêm dòng "Mã phòng" và các thuật ngữ mới vào `thuat-ngu.md`; `ui.md` đếm 5 bước có hộp xác nhận và nêu khổ ngang; menu "Cho tất cả hộ đang ở …"; bỏ "(Sprint 3)" ở `thuat-ngu.md`; `erd.md` khớp N1 | Kiểm tra tay, `BoCucFxmlTest` |
| G1 – G11 | Giữ nguyên theo yêu cầu | – |

Sau khi sửa: 233 test đơn vị và 15 kiểm thử tích hợp (MySQL 8.0.46) đều đạt.
Lưu ý còn lại của N4 (thời điểm ghi vào cột `DATETIME` bằng
`Timestamp.valueOf` lệch khi máy không ở múi giờ Việt Nam) đã được xử lý ở
mục G2 của [rà soát trước phát hành v0.3.0](truoc-phat-hanh-v0.3.0.md): DAO
ghi và đọc `DATETIME` bằng `LocalDateTime`, không còn cần đặt múi giờ máy.

## 1. Build và cơ sở dữ liệu

| Kiểm tra | Kết quả |
|---|---|
| `mvn -B clean verify` | Đạt. 215 test: CongNo 18, DotCuTru 15, HoGiaDinh 30, KhoanPhi 12, NhanKhau 36, SoTien 18, TaiKhoan 29, ThongBaoPhi 4, ThuPhi 24, BaoCaoNhanKhau 3, BoCucFxml 8, NgayUtil 6, MatKhauUtil 5, LoiCsdl 4, TrungKhoaException 2, DatabaseConfig 1. 0 trượt, 0 bỏ qua |
| `mvn -B verify -Pmysql` (`BLUEMOON_DB_URL` → `bm_test_it` trên server tạm) | Đạt. 13 IT: `PhieuThuIT` 8, `CongNoIT` 4, `GiaoDichHoGiaDinhIT` 1 |
| CI GitHub (`gh pr checks 28`) | `build` pass, 32 giây. CI chạy `mvn verify` không `-Pmysql`, nên 13 IT không chạy trên CI (xem N5) |
| `schema.sql` rồi `seed.sql` trên CSDL trống | Không lỗi. 11 bảng. Hộ 4, nhân khẩu 9, khoản thu 4, tài khoản 1, đợt tạm trú/tạm vắng 3. Mọi bảng `utf8mb4_unicode_ci` |
| Chạy `seed.sql` lần hai | Số dòng giữ nguyên |
| Nâng cấp `v0.2.0` → nhánh này | Dựng CSDL bằng `schema.sql`/`seed.sql` của commit `371d696`, thêm 1 đợt chốt, 5 khoản phải nộp, sửa 1 khoản; chạy `schema.sql` mới: thêm `bo_dem_bien_lai`, `phieu_thu`, `phieu_thu_chi_tiet`; `CHECKSUM TABLE` của 8 bảng cũ **trước và sau giống hệt nhau**; chạy `schema.sql` lần nữa không lỗi. Khớp với bảng nâng cấp trong `docs/setup.md` |

## 2. Phát hiện

### Lỗi

Không có. Đã kiểm tra riêng các nhóm sau và thấy đúng (chi tiết ở mục 3 và 4):
số biên lai, thu trùng, đóng góp tự nguyện, hủy phiếu, tra cứu hộ đã chuyển
đi, thông báo phí, số tiền bằng chữ, phân quyền.

### Nên sửa

#### N1. Sửa khoản phải nộp (AC-026) và thu phí có thể deadlock

- **Vị trí:** `CongNoDaoImpl.java:58-60` (`SQL_SUA`), `:166-181`
  (`suaSoTien`); lập phiếu ở `PhieuThuDaoImpl.java:225-252`. Câu trong
  `PhieuThuDaoImpl.java:33-36` ("không tạo vòng chờ") và `erd.md:223-226` chỉ
  đúng giữa các lần thu với nhau.
- **Mô tả:** `suaSoTien` chạy một câu `UPDATE phai_nop … WHERE id = ? AND
  NOT EXISTS (SELECT 1 FROM phieu_thu_chi_tiet … WHERE phai_nop_da_thu = ?)`.
  Trong `REPEATABLE READ`, truy vấn con của câu `UPDATE` khóa chia sẻ (S)
  khoảng trống trên chỉ mục `uq_ptct_phai_nop_da_thu`, rồi mới xin khóa X
  của dòng `phai_nop`. `lapPhieu` làm ngược lại: đã giữ X của dòng `phai_nop`
  (`SELECT … FOR UPDATE`) rồi xin chèn vào chỉ mục đó. Hai bên chờ nhau.
- **Tình huống:** hai người cùng thao tác trên **cùng một hộ** trong vài
  mili giây: một người sửa số tiền một khoản chưa thu (màn Công nợ), người
  kia đang thu đúng khoản đó (màn Thu phí). Phía bị MySQL chọn làm nạn nhân
  nhận `DaoException` và thấy "Không lưu được dữ liệu do lỗi cơ sở dữ
  liệu"; giao dịch hoàn tác nên không sai dữ liệu. Nếu nạn nhân là bên thu,
  thủ quỹ đã nhận tiền mà chưa có phiếu và phải thu lại.
- **Cách tái hiện (đã chạy):** 8 luồng × 120 vòng, mỗi vòng ngẫu nhiên thu,
  hủy hoặc sửa số tiền trên 4 hộ. Ba lần chạy: 3, 4 và 3 deadlock trên
  khoảng 400 thao tác mỗi lần, nạn nhân là câu `UPDATE` (`Deadlock found when trying
  to get lock`). `SHOW ENGINE INNODB STATUS` ghi đúng chu trình trên. Chạy
  chỉ thu và hủy (không sửa) 8 × 120 vòng: **0 deadlock**. Phiếu đầu tiên
  của năm mới (bộ đếm chưa có dòng) với 10 luồng: **0 deadlock**.
- **Đề xuất sửa:** đưa `suaSoTien` vào một giao dịch khóa cùng thứ tự với
  `lapPhieu` (khóa `phai_nop` trước, không giữ gì trên chỉ mục phiếu):
  1. `SELECT so_tien FROM phai_nop WHERE id = ? FOR UPDATE`
  2. `SELECT 1 FROM phieu_thu_chi_tiet WHERE phai_nop_da_thu = ?` (đọc
     không khóa; nếu có dòng thì hoàn tác, trả `false`)
  3. `UPDATE phai_nop SET … WHERE id = ?`, rồi `COMMIT`.
  Đã thử trong phần mềm thử tải (không sửa mã dự án): cùng cấu hình 8 × 100
  vòng, 111 lần sửa thành công, **0 deadlock**; bất biến dữ liệu vẫn đúng.
  Thêm một IT chạy song song `suaSoTien` và `lapPhieu` cùng một khoản lặp
  nhiều lần, yêu cầu không có `DaoException`. Sửa câu trong
  `PhieuThuDaoImpl` và `erd.md` cho đúng phạm vi.

#### N2. Số tiền lớn bị cắt hoặc xuống dòng trên biên lai và thông báo phí

- **Vị trí:** `MauIn.java:78-80` và `:118-120` (cột "Số tiền" rộng 110),
  `:251-260` (`dongBang`, chỉ cột ≥ 150 mới cho xuống dòng), `:262-268`
  (`oTong`)
- **Mô tả:** cột số tiền hẹp. Đã dựng trang in bằng chính `MauIn` trong cùng
  điều kiện với `InAn.dungBoCuc` (nhóm `Group`, không ép kích thước):
  - Dòng khoản **123.456.789 đ** bị cắt thành `123.456.78…`;
    **1.000.000.000 đ** thành `1.000.000.0…`. Số tiền in sai trên chứng từ.
  - Ô "Tổng cộng" cho xuống dòng: **24.000.000 đ** in thành `24.000.000` rồi
    `đ` ở dòng dưới; **1.000.000.000 đ** vỡ giữa các chữ số (`1.000.000.0` /
    `00 đ`). Từ 10.000.000 đ trở xuống in đủ.
- **Khi nào gặp:** tổng một phiếu từ khoảng 20 triệu đồng (hộ penthouse nợ
  nhiều tháng, hoặc thu một lần nhiều tháng); một dòng đóng góp từ khoảng 100
  triệu đồng thì bị cắt. Hiếm, nhưng là số tiền trên giấy tờ.
- **Đề xuất sửa:** cột "Số tiền" rộng 150, `setWrapText(false)` và
  `setMinWidth(Region.USE_PREF_SIZE)` cho ô số tiền và ô tổng (số tiền tối
  đa 1.000.000.000 đ, 13 ký tự). Thêm test `BoCucFxmlTest`-kiểu: dựng
  `MauIn.bienLai` với 10.000.000, 123.456.789 và 1.000.000.000 đ, kiểm tra
  không nhãn nào bị rút gọn thành "…" hay xuống dòng.

#### N3. In danh sách tạm vắng: không đặt khổ ngang, chia trang theo số dòng

- **Vị trí:** `MauIn.java:38` (`DONG_MOI_TRANG = 18`), `:150-183`;
  `InAn.java:79-106`; `ui.md:156-157` ("in khổ ngang 18 người một trang")
- **Mô tả:**
  - Trang rộng 960 nhưng `InAn.in` lấy khổ giấy mà người dùng chọn trong
    hộp thoại in, mặc định **dọc**. Trang bị thu nhỏ theo chiều rộng còn
    khoảng một nửa (vùng in A4 dọc, lề mặc định, khoảng 490 pt; chữ 14 px
    thành chừng 7 pt). Tài liệu nói "khổ ngang" nhưng mã không đặt.
  - 18 dòng/trang tính theo số dòng, không theo chiều cao. Họ tên quá khoảng
    20 ký tự và lý do dài xuống dòng (đã dựng thử: 6 người có tên thật cao
    569 px; 18 người có tên và lý do dài cao tới 1.410 px), nên trang vượt
    khổ giấy và bị thu nhỏ thêm. Dữ liệu không mất, chỉ khó đọc.
  - Danh sách này dùng để gửi công an phường (US-004), nên cần đọc rõ.
- **Đề xuất sửa:**
  - Đặt khổ ngang mặc định cho danh sách:
    `job.getJobSettings().setPageLayout(printer.createPageLayout(Paper.A4,
    PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT))` trước
    `showPrintDialog`, cho `InAn.xemVaIn` nhận tham số hướng giấy.
  - Giảm còn khoảng 12 dòng/trang hoặc chia trang theo chiều cao đo được
    của từng hàng.
  - Cập nhật `ui.md` cho khớp.

#### N4. "Hủy trong ngày" và thời điểm phiếu dùng đồng hồ máy khách (giả định 38)

- **Vị trí:** `ThuPhiService.java:203` (thời điểm lập phiếu, năm số biên
  lai), `:264-270` (xét "trong ngày"). So sánh: `CongNoDaoImpl.java:59`
  (`ngay_sua = NOW()`) và `dot_thu.ngay_chot DEFAULT CURRENT_TIMESTAMP`
  dùng đồng hồ máy chủ MySQL.
- **Mô tả:** AC-042 ("giao dịch của ngày trước thì từ chối hủy") là một
  biện pháp kiểm soát tiền. Hiện chỉ cần chỉnh ngày hệ thống của máy tính
  thu ngân lùi về ngày lập phiếu là hủy được phiếu cũ; hai máy lệch giờ thì
  phiếu mới có số lớn hơn nhưng ngày sớm hơn, và đồng hồ lệch qua giao thừa
  thì phiếu mang năm sai trong số biên lai. Giả định 38 đã ghi nhận và chấp nhận đặt "đúng
  giờ máy", nhưng chưa nêu hệ quả kiểm soát này; cùng một ứng dụng cũng đang
  trộn hai nguồn giờ (máy khách và máy chủ MySQL).
- **Đề xuất sửa:** Scrum Master duyệt một trong hai:
  - (a) Đổi giả định 38: lấy thời điểm bằng `SELECT NOW()` trong giao dịch
    lập phiếu và kiểm tra "trong ngày" ngay trong `UPDATE`/`SELECT … FOR
    UPDATE` của `huy` (`DATE(thoi_diem) = CURDATE()`), dùng một đồng hồ
    cho mọi máy. Test vẫn giả lập được bằng cách truyền thời điểm.
  - (b) Giữ như hiện tại, ghi rõ trong giả định 38 rằng đây là rủi ro đã
    chấp nhận (nhóm nhỏ, tin cậy lẫn nhau), nên có đối chiếu sổ quỹ cuối
    ngày bù lại.
  Khuyến nghị (a) vì thay đổi nhỏ và loại được cả hai rủi ro.

#### N5. Các IT chạy trên MySQL không chạy trên CI, nên N1 lọt qua

- **Vị trí:** `.github/workflows/ci.yml`, `docs/setup.md` mục 4
- **Mô tả:** CI chạy `mvn -B verify`, bỏ qua `*IT`. Mọi test của Sprint 3
  dùng DAO giả (`KhoPhieuThuGia`) nên không thấy được khóa, deadlock, ràng
  buộc CSDL thật. Đây là mục N9 của Sprint 2 ("chưa thêm CI với MySQL"), nay
  quan trọng hơn vì Sprint 3 có tiền và khóa dòng. Cả deadlock ở N1 và các
  bất biến (số liên tục, không thu trùng) chỉ kiểm chứng được trên MySQL.
- **Đề xuất sửa:** thêm vào `ci.yml` một job (hoặc bước) dùng service
  container `mysql:8.0`, tạo CSDL `bluemoon_test`, đặt `BLUEMOON_DB_URL`,
  `BLUEMOON_DB_USER`, `BLUEMOON_DB_PASSWORD` rồi chạy `mvn -B verify
  -Pmysql`. Thêm test cho N1 và test dựng trang in cho N2 trước.

#### N6. Chữ hiển thị và tài liệu còn lệch nhau

- **Chữ:**
  - `thu-phi.fxml:43` "Mã phòng" và `ThuPhiService.java:42` "Không tìm thấy
    phòng" (đúng chữ AC-030, AC-031) trong khi mọi màn khác, thông báo và
    `thuat-ngu.md` dùng "mã căn hộ" / "căn hộ". Cùng một màn, dòng đầu ghi
    "Mã phòng", dòng thông tin ghi "Căn hộ A0601". Nên đổi sang "Mã căn hộ"
    và "Không tìm thấy căn hộ", cập nhật AC-030, AC-031 cho khớp (hoặc ghi
    "mã phòng" vào `thuat-ngu.md` như tên gọi thông tục).
  - `cong-no.fxml:79` mục menu "Cho tất cả hộ còn khoản cần đóng" nhưng
    chỉ gồm hộ **đang ở** (giả định 35). Nên ghi "Cho tất cả hộ đang ở còn
    khoản cần đóng".
- **Tài liệu:**
  - `thuat-ngu.md:18` còn ghi "`phieu_thu` (Sprint 3)" như chưa làm; thiếu
    các thuật ngữ mới: Đóng góp tự nguyện, Quỹ, Số biên lai, Thông báo phí.
  - `ui.md:126-127` ghi "5 bước … nhập mã phòng → Tra cứu → … → chọn hình
    thức → Thu" nhưng thiếu hộp thoại xác nhận "Lập phiếu thu cho căn hộ
    …?" (`ThuPhiController.java:284`). Đếm thực tế: nhập mã + Enter (1) →
    chọn hình thức (2) → Thu tất cả (3) → Đồng ý (4): **lập phiếu 4 thao
    tác, 5 nếu bấm nút Tra cứu thay vì Enter**, đạt AC-072. Nếu tính cả in
    biên lai thì thêm "In biên lai" → "In…" → hộp thoại máy in = 7 thao tác.
    Nên ghi đúng và nêu rõ AC-072 tính đến lúc lập phiếu.
  - `erd.md:223-226` và `PhieuThuDaoImpl.java:33-36`: xem N1.
  - `erd.md:186` ("khoản vừa được thu ở máy khác cũng không bị sửa") đúng về
    kết quả nhưng bỏ qua rủi ro deadlock ở N1; cập nhật sau khi sửa.

### Ghi nhận

#### G1. `schema.sql` và `seed.sql` có sẵn `USE bluemoon`

`schema.sql:2-4`, `seed.sql:3`. Chạy hai file này trên một máy chủ đang có
CSDL `bluemoon` thật sẽ đụng vào CSDL thật (chỉ `CREATE … IF NOT EXISTS`
nên không xóa, nhưng `seed.sql` chèn dữ liệu mẫu). Để rà soát an toàn phải
dựng máy chủ riêng. Các `*IT` đã tự bỏ `CREATE DATABASE`/`USE` nên an toàn.
Nên thêm một dòng cảnh báo vào `docs/setup.md` mục 2.

#### G2. Dữ liệu mẫu vẫn viết không dấu

`seed.sql:29` ("Nguyen Thi Lan"…). Hiện lên cả biên lai và thông báo phí
(ảnh chụp). Là mục N7 của Sprint 2 chưa làm; nên làm trước Sprint Review để
bản demo in ra đúng tiếng Việt.

#### G3. Hộ đã chuyển đi vẫn ghi được đóng góp tự nguyện

`ThuPhiService.thongTin` (`:113-119`) và `ThuPhiController.hienQuy` hiện các
quỹ đang mở cả cho hộ đã chuyển đi (tra cứu được vì còn nợ, giả định 21);
`lapPhieu` không chặn. Giả định 21 chỉ nói thu nợ. Cân nhắc ẩn khu đóng góp
khi `trang_thai = DA_CHUYEN_DI`, hoặc ghi vào giả định 21 là được phép.

#### G4. `daIn` không kiểm tra phiếu

`ThuPhiService.java:317-320` và `PhieuThuDaoImpl.java:443-451`: `UPDATE
… so_lan_in + 1 WHERE id = ?` chạy cả khi phiếu đã hủy trong lúc đang xem
trước, và không báo gì khi 0 dòng bị đổi. Hậu quả chỉ là bộ đếm số lần in;
nên thêm `AND da_huy = FALSE`.

#### G5. Biên lai và thông báo phí không chia trang

`InAn.in` chỉ thu nhỏ trang vừa khổ giấy (`:94-97`). Biên lai 24 khoản cao
1.168 px, thông báo 24 khoản cao 1.089 px: thu nhỏ khoảng 0,65 lần, vẫn đọc
được. Quá khoảng 40 khoản (nợ hơn 20 tháng) sẽ nhỏ khó đọc. Chấp nhận
được cho quy mô hiện tại; nếu muốn, chia trang theo chiều cao như N3.

#### G6. Mỗi lệnh DAO mở một kết nối mới

`Database.getConnection()` không có connection pool. Một lần thu gọi khoảng
6–8 kết nối (`traCuu`, `thongTin` 3 lệnh, `lapPhieu`, `timTheoId` …). Đo
`traCuu` + `thongTin` trên MySQL cục bộ: **21 ms/lần** (30 lần), đạt AC-032
(< 3 giây) rất rộng. Thử tải 12 luồng, vài nghìn thao tác liền làm **cạn
cổng tạm của Windows** (khoảng 19.000 kết nối ở trạng thái `TIME_WAIT`,
`Address already in use: connect`) nên phải chờ 2 phút mới chạy tiếp. Với
2–3 máy dùng tay thì không gặp; nếu sau này có chức năng chạy hàng loạt
(Sprint 4, thống kê, xuất Excel) nên cân nhắc HikariCP.

#### G7. Tên chủ hộ trên biên lai in lại là tên lúc in (giả định 37)

`PhieuThu.tenChuHo` lấy qua `JOIN nhan_khau … quan_he = 'CHU_HO'` mỗi lần
đọc. Nếu đổi chủ hộ (US-026) giữa hai lần in, "BẢN IN LẠI" ghi tên khác bản
gốc. Giả định 37 đã nêu và chấp nhận. Chỉ nên lưu tên người nộp vào phiếu
nếu sau này cần đối chiếu chứng từ chặt hơn.

#### G8. `TrungKhoaException` của `lapPhieu` chưa đổi sang thông báo nghiệp vụ

`PhieuThuDaoImpl.java:249` bọc lỗi trùng (MySQL 1062) thành
`TrungKhoaException`, nhưng `ThuPhiService.thongBao` chỉ xử lý
`XungDotException`. Chỉ xảy ra nếu chốt chặn của CSDL (`uq_pt_so_bien_lai`,
`uq_ptct_phai_nop_da_thu`) bắt được thứ mà khóa dòng đã bỏ sót; kiểm tra
bằng thử tải thì chưa lần nào gặp. Người dùng sẽ thấy thông báo lỗi CSDL
chung. Chấp nhận được; có thể ánh xạ về `LOI_DU_LIEU_DOI` cho gọn.

#### G9. `ThongBaoPhiService.tatCa` gọi `timHo` cho từng hộ

`ThongBaoPhiService.java:79-85`: N+1 truy vấn. Với vài chục hộ không đáng
kể; chỉ ghi nhận.

#### G10. Mermaid

Dùng `mermaid` 11.4.1 (`mermaid.parse` trong jsdom): `erd.md` (erDiagram),
`class-diagram.md` (flowchart và classDiagram), `use-case.md` đều hợp lệ.
`workflow.md` không kiểm được bằng công cụ này (lỗi `Element is not
defined` của môi trường jsdom, không phải lỗi cú pháp rõ ràng); tệp này
không đổi trong Sprint 3.

#### G11. Chưa rà lại các mục Sprint 2 còn mở

N5 (chữ "bắt buộc từ đủ 14 tuổi"…), N6, N10, N11, N13 và G1–G15 của Sprint 2
không thuộc phạm vi PR #28 nên không xem lại.

## 3. Đối chiếu AC → mã và test

Ký hiệu: ✓ đúng và có test; ◐ đúng nhưng test chưa đủ; ✗ chưa có test hoặc
làm chưa đúng.

| AC | Mã | Test | Kết luận |
|---|---|---|---|
| AC-012 danh sách tạm vắng | `BaoCaoNhanKhauService.tamVang` (`:37`), `BaoCaoNhanKhauDaoImpl` (`SQL_TAM_VANG`), `MauIn.danhSachTamVang`, `DanhSachTamVang` | `BaoCaoNhanKhauServiceTest.danhSachTamVangTheoNgay`, `PhieuThuIT.thongBaoPhiVaDanhSachTamVang` | ✓ nội dung đúng (họ tên, căn hộ, thời gian + thêm CCCD, lý do). In: xem N3 |
| AC-013 không có dữ liệu | cùng service: "Không có nhân khẩu tạm vắng vào ngày …" | `khongCoAiTamVangThiBaoKhongCoDuLieu` | ✓ |
| AC-022 đóng số tiền bất kỳ | `ThuPhiService.dongGopMoi` | `dongGopNhieuLanCongDon`; IT `dongGopNhieuLanVaGhiKhongDong` | ✓ |
| AC-023 0 đ = "không đóng" | `dongGopMoi`, `DongGopQuy.trangThai`, `PhieuThuDaoImpl.kiemTraGhiKhongDong` | `ghi0DongLaKhongDongVaPhieuKhongCoHinhThuc`; IT cùng tên trên | ✓ |
| AC-024 nhiều quỹ một màn | `thu` + `dongGopMoi` | `thuChungKhoanBatBuocVaNhieuQuyTrenMotPhieu` | ✓ |
| AC-025 sửa khoản chưa thu | `CongNoService.suaSoTien` | `CongNoServiceTest.suaSoTienGiuSoGocVaNguoiSua` | ✓ (Sprint 2) |
| AC-026 khoản đã thu thì từ chối | `CongNoService.java:228-243`, `CongNoDaoImpl.SQL_SUA`, khóa ô ở `CongNoController` | `CongNoServiceTest` (`:196`), `ThuPhiServiceTest.khoanDaThuKhongSuaDuocChoDenKhiHuyPhieu` | ◐ đúng, nhưng deadlock khi chạy song song với thu (N1); chưa có IT song song |
| AC-030 hiển thị hộ và khoản chưa đóng | `traCuu`, `thongTin`, `SQL_NO_CUA_HO` | `traCuuTheoMaKhongPhanBietHoaThuong`, `thongTinGomKhoanChuaDongVaQuyDangMo` | ✓ |
| AC-031 không tìm thấy | `LOI_KHONG_TIM_THAY` | `traCuuMaKhongTonTaiBaoKhongTimThay` | ✓ (chữ: N6) |
| AC-032 dưới 3 giây | – | không có test; đo tay 21 ms/lần | ◐ đạt, chưa có test tự động |
| AC-033 thu tất cả, công nợ về 0 | `thu` | `thuTatCaDuaCongNoVe0VaGhiNguoiThu`; IT `thuTatCaRoiHuyKhoiPhucCongNo` | ✓ |
| AC-034 chỉ thu khoản đã chọn | `thu` | `thuMotPhanChiGhiKhoanDuocChon` | ✓ |
| AC-035 tổng tự cộng, có ngày thu | `PhieuThuMoi.tongTien`, `phieu_thu.thoi_diem`, `capNhatTong` (controller) | `thuTatCaDuaCongNoVe0…` kiểm tổng; nhãn tổng trên màn hình chưa có test | ◐ |
| AC-036 lưu hình thức | `PhieuThuMoi`, `phieu_thu.hinh_thuc` | `thuTatCaDuaCongNoVe0…` | ✓ |
| AC-037 lưu người thu và thời điểm | `thu` (`nguoi.getId()`, `LocalDateTime.now(dongHo)`) | cùng test | ✓ (đồng hồ: N4) |
| AC-038 chưa chọn hình thức báo lỗi | `LOI_CHUA_CHON_HINH_THUC`, `chk_pt_hinh_thuc` | `chuaChonHinhThucThiBaoLoi` | ✓ |
| AC-039 nội dung biên lai | `MauIn.bienLai`, `InAn` | `SoTienTest` (chữ); **không có test dựng trang** | ◐ nội dung đúng (đã chụp); số lớn bị cắt (N2); chưa có test `MauIn` |
| AC-040 "bản in lại" | `choIn`/`daIn`, `MauIn.bienLai(p, inLai)` | `inLanHaiLaBanInLai`, IT `inBienLaiDemSoLanIn` | ◐ đếm lần in đúng, dòng "BẢN IN LẠI" đã chụp nhưng chưa có test hiển thị |
| AC-041 hủy trong ngày, khôi phục công nợ | `huy`, `PhieuThuDaoImpl.huy` | `thuQuyHuyPhieuMinhLapKhoanTroVeChuaDong`; IT `thuTatCaRoiHuyKhoiPhucCongNo` | ✓ |
| AC-042 hủy phiếu ngày trước thì từ chối | `huy` (`:264-270`) | `khongHuyPhieuNgayTruoc` | ✓ (đồng hồ: N4) |
| AC-043 phiếu hủy giữ vết kèm người hủy | `da_huy`, `nguoi_huy`, `lichSu`, `chk_pt_huy` | `phieuHomNayGomCaPhieuHuy`, `huyCanLyDoVaKhongHuyHaiLan`, `thuQuyKhongHuyPhieuNguoiKhacQuanTriThiDuoc` | ✓ |
| AC-044 thông báo liệt kê khoản | `ThongBaoPhiService.motHo`, `tatCa`, `MauIn.thongBao` | `thongBaoLietKeKhoanThangVaNoCu`, `thangChuaChotChuaInDuoc` | ✓ |
| AC-045 không có khoản thì báo | `motHo` | `hoKhongCoKhoanCanDongThiBao` | ✓ |
| AC-072 ≤ 5 bước, chữ rõ | `thu-phi.fxml`, `ThuPhiController` | kiểm tay | ◐ lập phiếu 4–5 thao tác, đạt; in biên lai thêm 3 (N6) |

## 4. Các điểm cần soi kỹ

### Số biên lai

- **Liên tục, không nhảy khi hoàn tác.** `capSo` (`PhieuThuDaoImpl.java:
  313-336`) chạy trong cùng giao dịch lưu phiếu: tạo dòng của năm
  (`INSERT … ON DUPLICATE KEY UPDATE nam = nam`), `SELECT … FOR UPDATE`,
  tăng một. Đúng. Test: `phieuGiaLapHoanTacKhongLamNhaySo` (IT) và thử tải:
  sau 204, 217 phiếu (có hủy) trong 12 luồng, `COUNT(*) - MAX(so) = 0` (không
  trùng, không hụt), `bo_dem_bien_lai.so_cuoi` bằng số phiếu.
- **Đánh lại từ đầu khi sang năm.** `soBienLaiLienTucVaDanhLaiKhiSangNam`
  (đơn vị) đạt. Thêm phép thử: đồng hồ đặt 01/01/2027, bộ đếm chưa có dòng
  2027, 10 luồng thu cùng lúc: dãy 1–17 liên tục, không deadlock.
- **Phiếu hủy giữ số.** `huy` chỉ đổi `da_huy` và `con_hieu_luc`; số không
  cấp lại, `UNIQUE (nam, so)` bảo vệ. Đúng.
- Giới hạn 99.999 phiếu/năm khớp `chk_pt_so`, `chk_bdbl_so_cuoi` và
  `SO_TOI_DA`; hết số thì báo "Đã dùng hết số biên lai của năm".

### Thu trùng và deadlock

- **Hai máy cùng thu một khoản:** chỉ một phiếu thành công, máy kia được báo
  tra cứu lại (`haiMayCungThuMotKhoanChiMotPhieuThanhCong`; thử tải: 100–300
  lần "vừa thay đổi" mỗi lượt, không lần nào tạo hai dòng hiệu lực cho một
  khoản). `UNIQUE (phai_nop_da_thu)` là chốt chặn cuối.
- **Thứ tự khóa hộ → `phai_nop` → bộ đếm:** giữa các lần **thu** với nhau
  không thể deadlock: mọi giao dịch khóa `ho_gia_dinh` trước, nên các thao
  tác trên cùng một hộ được tuần tự hóa; khác hộ thì chỉ gặp nhau ở bộ đếm,
  khóa cuối cùng. Thử 8 luồng × 120 vòng, 10 luồng đầu năm: 0 deadlock.
  **Thu và hủy** cũng không deadlock (`huy` khóa dòng phiếu rồi đổi
  `con_hieu_luc`, không cần khóa `phai_nop` hay hộ).
- **Có deadlock ở một chỗ:** sửa khoản phải nộp với thu (N1).

### Đóng góp tự nguyện

- Quy tắc 0 đ (giả định 29) kiểm hai lớp: `dongGopMoi` (trả thông báo "Hộ
  đã đóng …", "Đã ghi hộ không đóng …") và `kiemTraGhiKhongDong` dưới khóa
  hộ. Ba trạng thái tính từ tổng và số lần của các dòng `con_hieu_luc`:
  chưa thu (0 lần), không đóng (chỉ có 0 đ), đã đóng (tổng > 0). Thử tải
  (thu, hủy ngẫu nhiên có đóng góp 0 đ và 50.000 đ): tổng phiếu luôn bằng
  tổng dòng, không khoản nào có hai dòng hiệu lực; các lần ghi 0 đ sai bị
  từ chối bằng thông báo nghiệp vụ.
- **Hủy phiếu có đóng góp:** `SQL_HUY_DONG` đặt `con_hieu_luc = FALSE` cho
  mọi dòng; `SQL_DONG_GOP` chỉ cộng dòng còn hiệu lực. Hộ quay về "chưa
  thu" hoặc "không đóng" (nếu còn dòng 0 đ của phiếu khác) đúng giả định 29.
  Thử tải: 0 dòng còn hiệu lực của phiếu đã hủy, 0 dòng hết hiệu lực của
  phiếu chưa hủy.

### Hủy phiếu

- Thủ quỹ chỉ hủy phiếu mình lập, Quản trị viên hủy mọi phiếu
  (`ThuPhiService.java:259-263`, test `thuQuyKhongHuyPhieuNguoiKhac…`).
- Chỉ hủy trong ngày (`:264-270`), bắt buộc lý do ≤ 200 ký tự (`:271-278`,
  khớp `VARCHAR(200)` và `chk_pt_huy`). Hủy hai lần: báo "đã bị hủy trước
  đó", và ở DAO báo "vừa được hủy ở máy khác" khi hai máy cùng hủy.
- Sau hủy khoản về chưa đóng và sửa được: `khoanDaThuKhongSuaDuocChoDenKhiHuyPhieu`.

### Tra cứu hộ đã chuyển đi còn nợ và nhiều hộ cùng mã

`SQL_HO_THEO_MA` (`PhieuThuDaoImpl.java:52-56`) lấy hộ đang ở và hộ đã chuyển
đi còn khoản `so_tien > 0` chưa thu; hộ đang ở đứng đầu, sau đó theo
`ngay_chuyen_den` giảm dần. `ma_dang_o` đảm bảo chỉ một hộ đang ở cho một
mã. So khớp mã không phân biệt hoa thường nhờ collation `unicode_ci`.
Test: `traCuuThayHoDaChuyenDiConNoXepSauHoDangO`, IT
`traCuuThayHoDaChuyenDiConNo`. Màn hình hiện ô chọn hộ khi có nhiều hộ.

### Thông báo phí

Tháng chưa chốt thì báo "chưa chốt công nợ" (`canDaChot`). Nợ tháng này và
nợ tháng trước tách đúng (`tao`: `thang.equals` / `thang.isBefore`); chụp ảnh
xác nhận ba nhóm. "In tất cả" bỏ hộ đã chuyển đi (`:82`), in từng hộ vẫn
được. Khoản 0 đ không đưa vào (giả định 33). Đạt `ThongBaoPhiServiceTest`.

### Số tiền bằng chữ

Chạy `SoTien.bangChu` với các giá trị yêu cầu và thêm vài giá trị:

| Số | Kết quả |
|---|---|
| 1.000.000.001 | Một tỷ không trăm linh một đồng |
| 10.000 | Mười nghìn đồng |
| 100.010 | Một trăm nghìn không trăm mười đồng |
| 1.021.000 | Một triệu không trăm hai mươi mốt nghìn đồng |
| 15.000.000 | Mười lăm triệu đồng |
| 1.001 / 1.015 | Một nghìn không trăm linh một / mười lăm đồng |
| 105 / 25 / 21 / 11 | Một trăm linh năm / Hai mươi lăm / Hai mươi mốt / Mười một đồng |
| 1.100.000 / 1.000.100 | Một triệu một trăm nghìn / Một triệu một trăm đồng |
| 2.208.000 / 24.000.000 | Hai triệu hai trăm linh tám nghìn / Hai mươi bốn triệu đồng |
| 123.456.789 | Một trăm hai mươi ba triệu bốn trăm năm mươi sáu nghìn bảy trăm tám mươi chín đồng |

Tất cả đúng tiếng Việt thông dụng ("linh", "mốt", "lăm", "mười"). Cách đọc
hai chữ số cuối "bốn" thay vì "tư" (24 → "hai mươi bốn") cũng chấp nhận được.

## 5. Mã nguồn

- **Phân quyền:** mọi hàm public mới của `ThuPhiService` (8 hàm),
  `ThongBaoPhiService` (2), `BaoCaoNhanKhauService` (1) đều gọi
  `PhanQuyen.yeuCau` đúng vai trò, khớp bảng trong `architecture.md`:
  thu phí và thông báo cho cả hai vai trò, báo cáo tạm vắng chỉ Quản trị
  viên, hủy phiếu có thêm điều kiện của Thủ quỹ. `CongNoService.suaSoTien`
  vẫn gọi `PhanQuyen` ở dòng đầu. Không có ngoại lệ mới cho `architecture.md`.
- **Giao dịch:** `lapPhieu` và `huy` mở `Connection` bằng try-with-resources,
  `setAutoCommit(false)`, hoàn tác ở cả `SQLException` và `RuntimeException`
  (kể cả `XungDotException`). Không có chỗ nối chuỗi từ dữ liệu người dùng
  vào SQL; các `IN (?, …)` tạo placeholder theo số phiếu. Chỉ dùng
  `PreparedStatement`.
- **Ràng buộc `schema.sql` ↔ Service:** `ly_do_huy VARCHAR(200)` ↔
  `LY_DO_TOI_DA`; `so BETWEEN 1 AND 99999` ↔ `SO_TOI_DA`; `chk_pt_hinh_thuc`
  ↔ `thu` (phiếu 0 đ lưu `hinh_thuc` null); `chk_pt_huy` ↔ `huy` (đặt đủ bốn
  cột); `chk_pt_nam` ↔ `thoiDiem.getYear()`; `chk_pn_so_tien` ↔ `SoTien.doc`;
  `chk_ptct_so_tien >= 0` ↔ số đọc từ `SoTien.doc` (không âm). Khớp.
- **In ấn (`InAn`, `MauIn`):**
  - Máy không có máy in: `PrinterJob.createPrinterJob()` trả `null` thì hiện
    `LOI_KHONG_CO_MAY_IN` và không tăng số lần in (`InAn.java:80-84`). Đúng.
  - Bấm Hủy ở hộp thoại máy in: `showPrintDialog` trả `false`, gọi
    `cancelJob` và trả `false`, nên `daIn` không chạy
    (`ThuPhiController.java:433-435`). Đúng. Đóng hộp thoại xem trước cũng
    không tăng (giả định 37).
  - Lỗi giữa chừng: `printPage` lỗi thì hủy job và báo; không tăng số lần in.
  - Danh sách dài: phân trang theo số dòng, chưa theo chiều cao, không đặt
    khổ ngang (N3); biên lai, thông báo không phân trang (G5).
  - Cột số tiền: N2.
- **Controller:** không chứa nghiệp vụ, chỉ cộng tổng để hiển thị
  (`capNhatTong`) và chọn nhãn; mọi lỗi `DaoException`/`CauHinhException` đi
  qua `LoiCsdl.thongBao` (`ThuPhiController`, `CongNoController`,
  `DanhSachTamVang`). `MauIn.bienLai` gọi `p.hinhThuc().getNhan()` không kiểm
  `null`; an toàn vì `choIn` đã chặn phiếu 0 đ, ghi nhận.

## 6. Giao diện và chữ hiển thị

Đã chụp ảnh headless (Monocle, `prism.order=sw`, phông Be Vietnam Pro) vào
thư mục tạm **ngoài repo**: màn Thu phí trống, sau tra cứu, tab "Phiếu thu
hôm nay"; biên lai (lần đầu, in lại, 24 khoản, số tiền lớn); thông báo phí
(thường, 24 khoản); danh sách tạm vắng (2 trang, tên dài, lý do dài). Dữ
liệu: seed + chốt công nợ hai tháng trên CSDL tạm. Nhận xét:

- Bố cục màn Thu phí khớp `ui.md` (tra cứu, bảng khoản có ô chọn, cột đóng
  góp, hình thức, tổng, nút Thu); không nhãn nào bị cắt ở 1120×645. Nút
  chính đổi chữ "Thu tất cả" / "Thu các khoản đã chọn" đúng.
- Biên lai và thông báo đọc rõ, đủ trường theo AC-039, AC-044; "BẢN IN
  LẠI" hiện đúng. Số tiền lớn: N2.
- Chữ tự nhiên, có dấu, nút tiếng Việt ("Đồng ý", "Hủy", "Đóng", "In
  biên lai", "Để sau"). Chỗ cần chỉnh: N6.
- Tên người trong dữ liệu mẫu không dấu: G2.

## 7. Tài liệu

| Tài liệu | Khớp mã? |
|---|---|
| `erd.md` | Khớp `schema.sql` (cột, khóa, cột sinh, CHECK). Cần sửa câu về thứ tự khóa và AC-026 sau N1 |
| `class-diagram.md` | Khớp: `ThuPhiService`, `ThongBaoPhiService`, `BaoCaoNhanKhauService`, `PhieuThu`, `ChiTietPhieuThu`; Mermaid hợp lệ. Chữ ký bỏ tham số `nguoi` (quy ước chung của sơ đồ) |
| `architecture.md` | Bảng phân quyền và bảng chức năng khớp mã |
| `ui.md` | Cần sửa số bước AC-072 (thiếu hộp xác nhận) và "khổ ngang" (N3, N6) |
| `08-product-backlog.md`, README | Khớp (US-004 hoàn thành phần xem/in, AC-014 sang Sprint 4) |
| `setup.md` | Bảng nâng cấp v0.2.0 → v0.3.0 đã kiểm chứng đúng. Kịch bản thu phí chạy đúng trên dữ liệu mẫu: A0601 có 4 khoản (2 tháng × 2), hai quỹ đang mở, Hoàng Văn Nam tạm vắng. Thêm cảnh báo G1 |
| `thuat-ngu.md` | Lệch: N6 |

### Nhận xét giả định 33–38

| # | Nhận xét | Đề xuất |
|---|---|---|
| 33 Khoản 0 đ không phải nợ | Hợp lý, khớp mã (`so_tien > 0` ở `CHON_NO`, `SQL_HO_THEO_MA`). Chưa mâu thuẫn, nhưng giả định 25 (nợ quá 3 tháng, US-017, Sprint 4) đếm "khoản bắt buộc chưa đóng" | Ghi thêm vào 25 rằng khoản 0 đ không tính khi Sprint 4 làm US-017, để hai bên khớp |
| 34 Không đủ thì không thu | Hợp lý, được thử tải kiểm chứng (nhiều lần "vừa thay đổi", không phiếu thiếu khoản) | Giữ |
| 35 Thông báo phí | Hợp lý; khớp mã | Đổi chữ menu (N6) |
| 36 Danh sách tạm vắng theo ngày chọn | Hợp lý; khớp `SQL_TAM_VANG` (người và hộ đang ở, đợt chứa ngày chọn). Cho phép chọn ngày tương lai, hữu ích để lập kế hoạch | Giữ |
| 37 Nội dung biên lai | Chấp nhận được; hệ quả ở G7 (tên chủ hộ có thể đổi giữa các lần in) | Ghi hệ quả vào giả định, không cần đổi mã |
| 38 Thời điểm theo đồng hồ máy lập phiếu | Có rủi ro kiểm soát (N4): đổi ngày máy là hủy được phiếu cũ, hai máy lệch giờ có thể sai năm số biên lai lúc giao thừa, và trộn hai nguồn giờ với `NOW()` | Duyệt đổi sang đồng hồ máy chủ MySQL (N4a), hoặc ghi rõ rủi ro chấp nhận (N4b) |

Không giả định nào mâu thuẫn với AC hay giả định cũ (21–32). Giả định 29 là
cụ thể hóa của 23 chứ không trái; 30 và 31 nhất quán (phiếu 0 đ vẫn có số).

## 8. Đề xuất xử lý

Thứ tự đề xuất, mỗi mục một commit kèm test tái hiện:

1. **N1** (kèm IT song song và sửa lại câu trong `PhieuThuDaoImpl`, `erd.md`).
2. **N2** (kèm test dựng trang in).
3. **N3**, **N6** (chữ, `ui.md`, `thuat-ngu.md`).
4. **N5** (CI chạy `-Pmysql`) để các IT ở N1 chạy được trên GitHub.
5. **N4**: cần Scrum Master quyết (a) hoặc (b).
6. Ghi nhận làm khi tiện: G1 (cảnh báo `setup.md`), G2 (dữ liệu mẫu có dấu),
   G4 (`AND da_huy = FALSE`).

Chờ Scrum Master duyệt trước khi sửa. Chưa commit, chưa push.
