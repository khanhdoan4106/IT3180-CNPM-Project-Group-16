# Rà soát tổng thể sau Sprint 3: dọn mục tồn và hoàn thiện tài liệu

Ngày rà soát: 09/11/2026 · Phạm vi: `main` tại `8a0aaba` (PR #28 đã merge) ·
Giai đoạn 1: chỉ đọc, chạy thử và lập kế hoạch, chưa sửa, chưa commit.

Ngoài phạm vi, không đụng tới: Sprint 4 (thống kê, nhật ký, sao lưu, xuất
Excel/PDF) và mọi nội dung về đóng góp, phân công thành viên.

## Tóm tắt

- `mvn clean verify`: **233/233 test đạt**. `mvn verify -Pmysql` (MySQL 8.0.46
  tạm, cổng 3308): thêm **15/15 kiểm thử tích hợp đạt**. Không đụng CSDL
  `bluemoon` và MySQL80; `mysqld` tạm sẽ tắt và xóa sau khi báo cáo xong.
- Kịch bản demo `setup.md` mục 5, chạy bằng Service trên CSDL dựng từ
  `schema.sql` + `seed.sql`: **13/15 bước đạt, 2 bước hỏng**, cùng một nguyên
  nhân là tên không dấu trong `seed.sql` (mục 2.2).
- `seed.sql` chạy lại 3 lần: số dòng không đổi (idempotent). Có một lỗi
  hẹn giờ: quỹ mẫu có ngày cố định sẽ hết hạn (mục 2.2, F2).
- Bảng mục tồn (mục 1) phân loại từng mục: phần lớn "Làm ngay" đều nhỏ
  (một dòng đến một hàm), 4 mục để Sprint 4, các mục còn lại bỏ vì không còn
  đúng hoặc chỉ là sở thích thiết kế. Có thêm 9 phát hiện mới (F1–F9), trong
  đó F1, F2 cần làm.
- Tài liệu: 3 tệp cần thêm (`CHANGELOG.md`, `docs/testing.md`, báo cáo này),
  khoảng 9 tệp cần sửa. Ba liên kết "hỏng" kiểm tra được đều là liên kết chỉ
  chạy trên GitHub (đúng mục đích); không có liên kết nội bộ hỏng.
- Đề xuất hai PR: **PR mã** (`chore/don-muc-ton`) rồi **PR tài liệu**
  (`docs/hoan-thien-tai-lieu`) tách từ `main` sau khi PR mã merge, vì tài
  liệu trích tên test và dữ liệu mẫu mới.

## Xử lý sau khi duyệt

Scrum Master duyệt kế hoạch ngày 09/11/2026 với ba quyết định: (1) chặn đóng
góp tự nguyện cho hộ đã chuyển đi (giả định 39); (2) chưa gắn tag `v0.3.0`,
gắn ở Sprint Review sau khi PR này và PR chỉnh màn Thu phí theo Figma merge;
(3) quỹ mẫu theo ngày chạy seed, tên không chứa năm cứng, kiểm tên có dấu
bằng Java hoặc `HEX()`, không dựa vào collation `_ci`. Phần mã làm trên nhánh
`chore/don-muc-ton` (PR 1), mỗi mục một commit, kèm test. PR tài liệu
(`docs/hoan-thien-tai-lieu`) làm sau cùng.

| Mục | Xử lý | Test |
|---|---|---|
| S2-N7, S2-G10, S2-G11, S2-G14, S3-G2, F1, F2 | Đã sửa: `seed.sql` tên, nơi thường trú, lý do có dấu; họ tên tài khoản mẫu là tên người; hai quỹ mẫu mở từ đầu tháng này đến cuối tháng sau, tên không chứa năm cứng ("Quỹ vì người nghèo <năm hiện tại>"), kiểm tra chạy lại theo tên; dòng "Chuyển đến" của người tạm trú lấy ngày bắt đầu đợt đầu. Chạy lại 3 lần: số dòng không đổi | `SeedIT` 4 test (số dòng sau 3 lần chạy, tên so sánh trong Java, quỹ đang mở, ba trường hợp tạm trú/tạm vắng) |
| S2-N11, F5 | Đã sửa: `LoiKhongMongDoi` đặt bộ xử lý lỗi toàn cục trong `App.start`, hiện hộp thoại thay vì chỉ in console | `LoiKhongMongDoiTest` 3 test (chọn thông báo); hộp thoại kiểm tay |
| S2-N6 | Đã sửa: hỏi xác nhận khi đổi vai trò và khi khóa tài khoản; thông báo "Đã đổi vai trò của … thành …" | Kiểm tay (Service không đổi) |
| S2-N5, S2-G7 | Đã sửa: "Biến động nhân khẩu gần đây", tooltip "từ đủ 14 tuổi", gợi ý `vd A0601`/`vd admin`, dòng "Đang xem trước" không còn đỏ | `BoCucFxmlTest` |
| S2-N10a | Đã sửa: `LoiTrung.trungCccd` dùng chung ba nơi | Test hiện có của `HoGiaDinhServiceTest`, `NhanKhauServiceTest` |
| S2-G5 | Đã sửa: `doiChuHo` kiểm tra lại dưới khóa (chủ hộ cũ còn là chủ hộ đang ở, người mới còn đang ở), lỗi thì hoàn tác và báo tải lại (`XungDotException.NHAN_KHAU_DOI`) | IT `doiChuHoTuChoiNguoiVuaChuyenDiOMayKhac` |
| S3-G4, S2-G8 | Đã sửa: `SQL_GHI_IN` thêm `AND NOT da_huy`; Javadoc `TEN_HOP_LE` đặt đúng chỗ | IT `daInKhongDemPhieuDaHuy` |
| S3-G3 | Đã sửa theo quyết định 1: `thongTin` không trả quỹ nào cho hộ đã chuyển đi, `thu` từ chối dòng đóng góp (kể cả 0 đ) với "Hộ đã chuyển đi chỉ thu các khoản còn nợ", màn Thu phí hiện đúng lý do; giả định 39 ghi vào `11-chot-va-gia-dinh.md` | `ThuPhiServiceTest.hoDaChuyenDiChiThuKhoanNoKhongGhiDongGop`, IT `hoDaChuyenDiKhongGhiDongGopTuNguyen` |
| F3 | Đã sửa: `CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS` (3 Java, 4 FXML); `javac -Xlint:all` còn 0 cảnh báo | `BoCucFxmlTest`, `MauInTest` |
| S2-N9 (phần còn lại) | Đã sửa: `BienDongServiceTest` | 3 test |
| Để Sprint 4, Bỏ, và các mục tài liệu (S2-N12, N13, G2, S3-G1, S3-G10, F4, F6–F9) | Giữ nguyên theo bảng mục 1; phần tài liệu làm ở PR 2 | – |

Sau khi sửa: 240 test đơn vị và 22 kiểm thử tích hợp (MySQL 8.0.46) đều đạt.
Tài liệu `setup.md` còn nhắc "Quỹ vì người nghèo 2026" và tên cũ; đã cập nhật
ở PR tài liệu (bảng dưới) cùng kịch bản demo.

### Xử lý ở PR tài liệu (`docs/hoan-thien-tai-lieu`)

PR chỉ sửa tài liệu, không đụng mã, mỗi tệp hoặc nhóm tệp một commit. Số test
ghi trong tài liệu (244 đơn vị, 22 tích hợp) là số sau PR chỉnh màn Thu phí
theo Figma.

| Mục | Xử lý |
|---|---|
| F8 | Thêm `CHANGELOG.md` (v0.1.0, v0.2.0, Chưa phát hành gồm Sprint 3 và PR #28, #29, #30; chỉ thay đổi sản phẩm, không ghi tên người) và `docs/testing.md` (chiến lược, cách chạy, bảng AC → test cho Sprint 1–3, việc còn kiểm tay) |
| F6 | `README.md`: trạng thái Sprint 3 "chờ gắn tag", liên kết Release `v0.2.0`, FXML `thu-phi`, CI hai job, mục lục có `docs/review`, `docs/meeting-notes`, `docs/testing.md`, `CHANGELOG.md`; không đụng mục Thành viên |
| S3-G1 | `setup.md`: cảnh báo `USE bluemoon`; mục "Chạy lại `seed.sql`" (an toàn nhiều lần, không làm mới ngày quỹ, muốn quỹ đang mở khi demo thì dựng lại CSDL); bảng nâng cấp từ `v0.2.0` nói rõ dữ liệu mẫu cũ không đổi; kịch bản demo theo dữ liệu mẫu mới |
| F7, S2-N12 | `use-case.md` bỏ "cần chốt lại" và thêm US-026, US-027 vào bảng trạng thái; `erd.md` `DOI_CHU_HO` ghi khi đổi chủ hộ |
| S2-N13 | `ui.md`: mục "Trạng thái so với thiết kế" (cửa sổ, lỗi nhập liệu, phông, màu, bộ lọc khoản thu, hộp thoại, in ấn), ghi phần Sprint 4 (TS-004) |
| S2-G2, F4 | `11-chot-va-gia-dinh.md` giả định 40 (collation `_ci` không phân biệt hoa thường và dấu); giả định 25 ghi khoản 0 đ không tính (khớp giả định 33); giả định 33–38 ghi đã vào `main` |
| S3-G10 | Kiểm tra 5 sơ đồ Mermaid bằng `mermaid.parse`: đều hợp lệ (`workflow.md` cần môi trường có đủ DOM) |
| – | `architecture.md`, `class-diagram.md` thêm test mới và lớp tiện ích của controller; `08-product-backlog.md`, `10-definition-of-done.md`, `workflow.md` (hai job CI, báo cáo rà soát, bước phát hành có `CHANGELOG.md`), mẫu Pull Request |
| F9 | Chưa làm: chưa gắn tag `v0.3.0` (gắn ở Sprint Review); `pom.xml` vẫn `0.3.0-SNAPSHOT` |

Liên kết nội bộ: kiểm 89 liên kết tương đối trong `README.md`, `CHANGELOG.md`,
`docs/**/*.md`, `.github/**/*.md`; 4 liên kết "hỏng" đều chỉ chạy trên GitHub
(hai liên kết Release trong `README.md`, hai `../blob/main/...` trong mẫu
Issue), đúng mục đích.

## 1. Bảng mục tồn

Nguồn: S2 = `sprint-2-ra-soat.md` (Nên sửa N, Ghi nhận G), S3 =
`sprint-3-ra-soat.md` (Ghi nhận G). "Còn đúng?" kiểm tra trên mã `main` hiện
tại.

| Mục | Nội dung | Còn đúng? (file:dòng) | Phân loại |
|---|---|---|---|
| S2-N5 | Chữ chưa thống nhất | Còn 3 chỗ: `trang-chinh.fxml:46` "Biến động dân cư gần đây" (đúng thuật ngữ là "nhân khẩu"); `TaiKhoanController.java:161` "thuquy01 giờ là Quản trị viên"; tooltip "bắt buộc với người từ 14 tuổi" ở `ho-gia-dinh.fxml:130`, `nhan-khau.fxml:162` (Service ghi "từ đủ 14 tuổi") | **Làm ngay** |
| S2-N6 | Đổi vai trò, khóa tài khoản không hỏi xác nhận | Còn: `TaiKhoanController.java:151` (`onDoiVaiTro`), `:170` (`onKhoa`) không gọi `HopThoai.xacNhan`. Cấp quyền Quản trị viên chỉ một cú bấm | **Làm ngay** |
| S2-N7 | Dữ liệu mẫu không dấu | Còn: `seed.sql:10`, `:26-54`, `:59-69`. Hậu quả đã thấy khi chạy kịch bản demo (2.2) | **Làm ngay** (kèm S2-G10, S2-G11, S3-G2) |
| S2-N9 (phần còn lại) | Thiếu test | `BienDongService` vẫn không có test riêng (không có `BienDongServiceTest`), chưa có test Thủ quỹ bị chặn xem lịch sử. Phần còn lại của N9 đã làm ở Sprint 2–3 (SoTien, IT MySQL, CI) | **Làm ngay** (1 tệp test nhỏ) |
| S2-N10 | Trùng lặp | (a) "Số CCCD … đã tồn tại" còn 3 nơi: `HoGiaDinhService.java:120`, `NhanKhauService.java:283`, `LoiTrung.java:32`. (b) `CongNoController.java:301` `tomTat` tự cộng tiền. (c) `baoLoi`/`baoThanhCong` lặp ở 7 controller | (a) **Làm ngay**; (b) **Bỏ**: chỉ hiển thị, tổng lấy từ danh sách đang xem; (c) **Bỏ**: sở thích thiết kế, không sai |
| S2-N11 | Lỗi không lường trước không hiện gì | Còn: `App.java` không đặt `Thread.setDefaultUncaughtExceptionHandler`; lỗi tải FXML hay `RuntimeException` lạ làm nút "không có tác dụng" | **Làm ngay** |
| S2-N12 (phần còn lại) | Tài liệu lệch | Còn: `use-case.md:66-67` ("Sprint 2–3, cần chốt lại"), `erd.md:202` ("`DOI_CHU_HO` để sẵn cho US-026" nhưng US-026 đã làm), `ui.md` (N13), `README.md` (danh sách FXML thiếu `thu-phi`, CI mô tả một job, chưa nhắc `docs/review`, `docs/meeting-notes`) | **Làm ngay** (PR tài liệu) |
| S2-N13 | `ui.md` mô tả Figma, không ghi phần đã làm | Còn: `ui.md:13-14` (1440×1024 và 1120×645; thực tế màn chức năng nằm trong Trang chính), `:19` (lỗi hiện dưới từng ô; thực tế một nhãn chung), bộ lọc Tất cả/Bắt buộc/Tự nguyện ở Khoản thu chưa có, phông và biến màu (Be Vietnam Pro đã làm ở Sprint 2) | Ghi trạng thái thật vào `ui.md`: **Làm ngay** (tài liệu). Làm các phần giao diện còn thiếu: **Để Sprint 4** (TS-004 rà soát giao diện) |
| S2-G1 | Phiên chỉ đọc lại khi mở màn hình | Còn đúng, là giả định 17 | **Bỏ** (đã chấp nhận) |
| S2-G2 | Đăng nhập, mã khoản không phân biệt hoa thường và **dấu** | Còn đúng; mới thấy thêm: `utf8mb4_unicode_ci` coi "Hoàng" = "Hoang", nên so khớp trong SQL và tìm kiếm không phân biệt dấu | Ghi vào giả định (tài liệu): **Làm ngay** |
| S2-G3 | Ngày lấy theo hai nguồn (`LocalDate.now()` và `CURDATE()`) | Còn: `BienDongDaoImpl.java:32` (`CURDATE()`) và `:64` (`LocalDate.now()`). Sau N4, `ThuPhiService` đã dùng giờ CSDL nên chỗ này là chỗ duy nhất còn lệch | **Để Sprint 4** (làm cùng nhật ký thao tác, cũng cần giờ CSDL) |
| S2-G4 | Sửa ngày chuyển đến không đổi lịch sử | Đúng nguyên tắc "lịch sử chỉ thêm" | **Bỏ** |
| S2-G5 | `doiChuHo` không kiểm tra `DANG_O` trong giao dịch | Còn: `NhanKhauDaoImpl.java:44-47` (`SQL_CHU_HO_CU`, `SQL_CHU_HO_MOI`) không có `trang_thai = 'DANG_O'`. `UNIQUE (chu_ho_cua)` vẫn giữ "một chủ hộ" | **Làm ngay** (thêm điều kiện, kiểm số dòng) |
| S2-G6 | "Đã chốt" suy từ danh sách khác rỗng | Còn: `CongNoController.java:138`. Chỉ sai khi tháng được chốt mà không có hộ nào, không gặp trong thực tế | **Bỏ** |
| S2-G7 | "Đang xem trước, chưa lưu." không đặt lại kiểu chữ | Còn: `CongNoController.java:168` dùng `setText` thay vì `baoThongTin`, nên sau một lỗi vẫn đỏ | **Làm ngay** (1 dòng) |
| S2-G8 | Javadoc mồ côi | Còn: `TaiKhoanService.java:21`, javac `-Xlint:all` báo `dangling-doc-comments` | **Làm ngay** (1 dòng) |
| S2-G9 | Không có chức năng `DA_DONG` cho khoản | Đã xử lý ở Sprint 3 (`KhoanPhi.dangMo`, cột "Hết hạn") | **Bỏ** (xong) |
| S2-G10 | Họ tên mẫu "Quản trị viên" → lời chào lặp | Còn: `seed.sql:10` | **Làm ngay** (cùng S2-N7) |
| S2-G11 | Người tạm trú mẫu có dòng "Chuyển đến" từ 2023 | Còn: `seed.sql:91-97` | **Làm ngay** (cùng S2-N7) |
| S2-G12 | DAO giả và SQL thật lệch về khoản đã thu | Đã sửa ở Sprint 3 | **Bỏ** (xong) |
| S2-G13 / S3-G6 | Mỗi lệnh DAO mở một kết nối; đọc lại tệp cấu hình mỗi lần | Còn: `Database.java:61`. Đo 21 ms cho tra cứu + thông tin (AC-032 đạt). Thử tải 12 luồng làm cạn cổng Windows | **Để Sprint 4** (cân nhắc HikariCP cùng thống kê, xuất Excel) |
| S2-G14 | `INSERT IGNORE` nuốt cả lỗi CHECK | Còn; sẽ được kiểm bằng `SeedIT` (số dòng và nội dung sau khi chạy seed) | **Làm ngay** (qua `SeedIT`) |
| S2-G15 | Tag mang version `-SNAPSHOT` | `pom.xml:9` vẫn `0.3.0-SNAPSHOT`, chưa có tag `v0.3.0` | **Để lúc phát hành** (xem 4, câu hỏi 2) |
| S3-G1 | `schema.sql`/`seed.sql` có `USE bluemoon` | Còn: `schema.sql:2-4`, `seed.sql:3`; `setup.md` chưa cảnh báo | **Làm ngay** (1 đoạn trong `setup.md`) |
| S3-G2 | Dữ liệu mẫu không dấu | Trùng S2-N7 | **Làm ngay** |
| S3-G3 | Hộ đã chuyển đi vẫn ghi được đóng góp tự nguyện | Còn: `ThuPhiService.java:113-119`, `ThuPhiController.hienQuy`; `lapPhieu` không chặn | **Làm ngay** nếu Scrum Master đồng ý (câu hỏi 1): chặn ở Service + ẩn khu đóng góp, thêm giả định |
| S3-G4 | `daIn` không kiểm tra phiếu hủy | Còn: `PhieuThuDaoImpl.java:107` (`SQL_GHI_IN`) | **Làm ngay** (thêm `AND da_huy = FALSE`) |
| S3-G5 | Biên lai, thông báo không chia trang theo chiều cao | Còn; 24 khoản vẫn đọc được (thu nhỏ 0,65) | **Bỏ** |
| S3-G7 | Tên chủ hộ trên bản in lại là tên lúc in | Giả định 37 đã chấp nhận | **Bỏ** |
| S3-G8 | `TrungKhoaException` của `lapPhieu` chưa thành thông báo nghiệp vụ | Còn nhưng chưa từng xảy ra (khóa dòng chặn trước) | **Bỏ** |
| S3-G9 | `tatCa` gọi `timHo` từng hộ (N+1) | Còn, vài chục hộ không đáng kể | **Bỏ** |
| S3-G10 | Mermaid `workflow.md` chưa kiểm được trong rà soát Sprint 3 | `workflow.md` không đổi; kiểm lại bằng `mermaid-cli` trong PR tài liệu | **Làm ngay** (kiểm tra, không sửa nếu hợp lệ) |
| S3-G11 | Mục Sprint 2 còn mở | Đã gom vào bảng này | – |

Tổng: **Để Sprint 4** gồm giao diện N13, S2-G3, S2-G13/S3-G6 (kết nối) và
S2-G15 (tag); các mục còn lại là **Làm ngay** hoặc **Bỏ** như bảng.
Không có mục "để sau" nào khác trong mã và tài liệu: tìm
`TODO`/`FIXME`/`XXX`/`HACK` trong `src`, `database`, `docs`, `README.md` không
ra kết quả nào.

## 2. Kết quả kiểm tra tổng thể

### 2.1 Build và kiểm thử

| Kiểm tra | Kết quả |
|---|---|
| `mvn -B clean verify` (Temurin 21.0.12, Maven 3.9.16) | Đạt, 233 test, 0 trượt, 0 bỏ qua |
| `mvn -B verify -Pmysql` (`bm_test_it`, MySQL 8.0.46 tạm) | Đạt, 15 IT: `PhieuThuIT` 9, `CongNoIT` 4, `DongHoCsdlIT` 1, `GiaoDichHoGiaDinhIT` 1 |
| Cảnh báo biên dịch (`javac -Xlint:all`) | 4 cảnh báo: 3× `CONSTRAINED_RESIZE_POLICY` deprecated (`BangLichSu.java:37`, `DanhSachTamVang.java:52`, `ThuPhiController.java:504`; còn dùng trong FXML), 1× `dangling-doc-comments` (`TaiKhoanService.java:21`). Maven in thêm cảnh báo `Unsupported JavaFX configuration` khi chạy trên classpath, đã biết và vô hại (`setup.md`) |
| Mã chết | Quét hàm, lớp, import không được tham chiếu trong `src/main`: **không có lớp hay import thừa**. Chỉ `NhanKhau.voiQuanHe` (`NhanKhau.java:91`) không được `src/main` gọi, nhưng bộ giả `KhoGia` dùng → **Bỏ** |
| TODO bị quên | Không có |

### 2.2 Kịch bản demo (`setup.md` mục 5) và `seed.sql`

CSDL trống → `schema.sql` → `seed.sql`, rồi gọi Service (`CongNoService`,
`ThuPhiService`, `ThongBaoPhiService`, `BaoCaoNhanKhauService`) đúng các
bước:

| Bước | Kết quả |
|---|---|
| 1. Chốt công nợ tháng trước và tháng này | Đạt (tháng này thêm 8 khoản) |
| 2. A0601 có 4 khoản chưa đóng; Quỹ vì người nghèo 2026 đang mở; thu tất cả + 100.000 đ, Tiền mặt | Đạt (`PT-2026-00001`, 1.912.000 đ, 5 dòng) |
| 2. In biên lai lần đầu | Đạt (`soLanIn = 0`) |
| 3. In lại ghi "BẢN IN LẠI"; hủy phiếu có lý do; A0601 lại 4 khoản chưa đóng | Đạt |
| 4. Công nợ có cột "Đã đóng"; in thông báo phí cho tất cả hộ | Đạt (2 dòng đã đóng; 3 hộ còn khoản) |
| 5. Danh sách tạm vắng hôm nay có **Hoàng Văn Nam** (A0601) | **Hỏng**: trả "Hoang Van Nam" |
| 5. Bảng "Chuẩn bị demo": Vũ Văn Toàn (K01) tạm trú còn hạn 120 ngày | Ngày đạt; tên lệch: "Vu Van Toan" |
| 5. Bảng "Chuẩn bị demo": Đỗ Thị Hoa (PH01) tạm trú quá hạn | Ngày đạt; **Hỏng** khi kiểm tên: "Do Thi Hoa" |

**Nguyên nhân duy nhất của các bước hỏng:** tên người trong `seed.sql`
không dấu trong khi `setup.md` ghi có dấu. Lưu ý khi viết test: so sánh
`ho_ten = 'Hoàng Văn Nam'` trong SQL vẫn **khớp** "Hoang Van Nam" vì collation
`utf8mb4_unicode_ci` không phân biệt dấu (chỉ chữ `Đ` bị phân biệt). Kiểm tra
tên có dấu bằng `BINARY`, `HEX()` hoặc so sánh chuỗi trong Java, không dùng
`=` trong SQL (đây cũng là lý do bước kiểm "có dấu" của riêng mình lúc đầu báo
đạt sai).

`seed.sql` đã có `SET NAMES utf8mb4` ở dòng 5, nên đọc UTF-8 đúng dù `mysql`
dùng bảng mã khác (tên khoản thu "Phí dịch vụ" lưu `C3AD`… đúng).

**Chạy lại nhiều lần:** `schema.sql` rồi `seed.sql` ba lần liên tiếp cho cùng
số dòng (tài khoản 1, hộ 4, nhân khẩu 9, đợt tạm trú/tạm vắng 3, khoản thu 4,
biến động 12).

**Đề xuất sửa `seed.sql`** (giữ `SET NAMES utf8mb4`, giữ `INSERT IGNORE`):

1. **F1: tên có dấu.** Viết có dấu toàn bộ tên, nơi thường trú, lý do: "Trần
   Văn Hùng", "Nguyễn Thị Lan", "Hoàng Văn Nam", "Hoàng Minh Châu", "Lê Minh
   Quân", "Phạm Đức Anh", "Phạm Thu Hà", "Vũ Văn Toàn", "Đỗ Thị Hoa", "Xã Hải
   Phong, huyện Hải Hậu, Nam Định", "Phường Trần Lãm, TP Thái Bình, Thái
   Bình", "Thuê nhà làm việc", "Đi học", "Công tác nước ngoài". Khóa chống
   trùng là số CCCD nên không ảnh hưởng idempotent. CSDL đã có dữ liệu cũ
   không tự đổi tên: giữ cách dựng lại CSDL đã ghi ở `setup.md`, không thêm
   `ON DUPLICATE KEY UPDATE` để khỏi ghi đè dữ liệu người dùng đã sửa.
2. **F2: quỹ mẫu hết hạn.** `seed.sql:83-86`: "Quỹ biển đảo"
   (01–30/11/2026) và "Quỹ vì người nghèo 2026" (01/10–31/12/2026) có ngày cố
   định. Sau 30/11, kịch bản demo mất "Quỹ biển đảo" (màn Thu phí chỉ liệt kê
   quỹ đang mở); sau 31/12 mất cả hai. Đổi sang ngày tính theo ngày chạy seed
   (như đợt tạm trú đã làm), ví dụ `tu_ngay = đầu tháng hiện tại`, `den_ngay =
   cuối tháng sau`, và đổi khóa chống trùng sang `NOT EXISTS (… WHERE ten = …)`
   vì `UNIQUE (ten, tu_ngay)` sẽ cho thêm một dòng mới mỗi khi chạy seed ở
   tháng khác.
3. **S2-G10:** họ tên tài khoản mẫu là tên người ("Nguyễn Thị Thu Hương") để
   lời chào không thành "Quản trị viên (Quản trị viên)".
4. **S2-G11:** dòng "Chuyển đến" của người tạm trú lấy ngày bắt đầu đợt tạm
   trú đầu tiên thay cho ngày hộ chuyển đến năm 2023.
5. **S2-G14 và kiểm chứng:** thêm `SeedIT` (chỉ chạy với `-Pmysql`): dựng
   CSDL từ `schema.sql`, chạy `seed.sql` hai lần, kiểm số dòng từng bảng,
   kiểm tên có dấu (so sánh trong Java), kiểm 3 trường hợp tạm trú/tạm vắng
   và hai quỹ đang mở. Các `*IT` đã có hàm bỏ `USE bluemoon` để chạy trên
   CSDL `*test*` (`CsdlThu`), tái sử dụng.

### 2.3 Chữ hiển thị

Đã đọc mọi `text`/`promptText` trong 8 FXML và mọi chuỗi tiếng Việt trong
các controller. Chữ tự nhiên, có dấu; nút hộp thoại tiếng Việt; thuật ngữ
đúng `thuat-ngu.md`. Cần sửa:

| Vị trí | Hiện tại | Đề xuất |
|---|---|---|
| `trang-chinh.fxml:46` | "Biến động dân cư gần đây" | "Biến động nhân khẩu gần đây" (US-027) |
| `TaiKhoanController.java:161` | "thuquy01 giờ là Quản trị viên" | "Đã đổi vai trò của thuquy01 thành Quản trị viên" |
| `ho-gia-dinh.fxml:130`, `nhan-khau.fxml:162` | "bắt buộc với người từ 14 tuổi" | "bắt buộc với người từ đủ 14 tuổi" |
| `thu-phi.fxml:45` | "VD: A0601" | "vd A0601", cùng kiểu `ho-gia-dinh.fxml` ("vd A0601", "vd 75.5") và `tai-khoan.fxml` |
| `dang-nhap.fxml:17` | "vd: admin" | "vd admin" (bỏ dấu hai chấm cho cùng kiểu) |
| `CongNoController.java:168` | "Đang xem trước, chưa lưu." hiện màu đỏ nếu trước đó có lỗi | Đặt lại kiểu chữ về thường khi hiện thông tin (S2-G7) |

Giữ nguyên: "Mã phòng" ở màn Thu phí (đúng AC-030/031, đã có trong
`thuat-ngu.md`).

### 2.4 Phát hiện mới

| # | Phát hiện | Vị trí và cách tái hiện | Đề xuất | Phân loại |
|---|---|---|---|---|
| F1 | Tên không dấu làm hỏng bước 5 của kịch bản demo | Mục 2.2 | Sửa `seed.sql` | Làm ngay |
| F2 | Quỹ mẫu có ngày cố định, hết hạn sau 30/11 và 31/12/2026 | `seed.sql:83-86`; chạy `seed.sql` rồi mở Thu phí sau ngày đó: không còn quỹ nào mở | Ngày theo ngày chạy seed (2.2, mục 2) | Làm ngay |
| F3 | `CONSTRAINED_RESIZE_POLICY` deprecated từ JavaFX 20 | 3 chỗ Java + các FXML (`javac -Xlint:all`) | Đổi sang `CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS` (cùng hành vi); kiểm `BoCucFxmlTest` và ảnh chụp | Làm ngay |
| F4 | Collation `_ci` không phân biệt dấu | 2.2 (so `ho_ten` bằng `=`); tìm "nguyen" ra "Nguyễn" | Không đổi (tiện cho tìm kiếm); ghi vào giả định, và lưu ý khi viết test | Làm ngay (tài liệu) |
| F5 | Ứng dụng không có bộ xử lý lỗi toàn cục | `App.java` | Xem S2-N11 | Làm ngay |
| F6 | README lệch: danh sách FXML thiếu `thu-phi`; "CI chạy `mvn verify`" (nay hai job); không nhắc `docs/review`, `docs/meeting-notes`, `docs/testing.md`; thiếu liên kết Release `v0.2.0`; trạng thái Sprint 3 "Hoàn thành" nhưng chưa có tag `v0.3.0` | `README.md:34-36`, `:62-63`, `:90-96` | Sửa trong PR tài liệu | Làm ngay |
| F7 | `use-case.md` và `erd.md` còn câu cũ | `use-case.md:66-67`, `erd.md:202` | Sửa trong PR tài liệu | Làm ngay |
| F8 | `CHANGELOG.md` và `docs/testing.md` chưa có; mỗi release chỉ có ghi chú trên GitHub (v0.1.0, v0.2.0) | – | Thêm (mục 3) | Làm ngay |
| F9 | Chưa gắn tag `v0.3.0`; `pom.xml` vẫn `0.3.0-SNAPSHOT` | `pom.xml:9`, `git tag` chỉ có `v0.1.0`, `v0.2.0` | Quyết định thời điểm phát hành (câu hỏi 2); ghi bước phát hành vào `workflow.md` | Chờ quyết định |

Liên kết: kiểm 54 liên kết tương đối trong `README.md`, `docs/**/*.md`,
`.github/**/*.md`; 3 liên kết "hỏng" là `../../releases/tag/v0.1.0` (README) và
hai `../blob/main/...` (mẫu Issue), chỉ chạy trên GitHub, đúng mục đích. Các
tệp `docs/design/README.md`, `docs/meeting-notes/README.md`,
`docs/review/*.md` chưa có trong mục lục `README.md` (F6).

## 3. Tài liệu sẽ thêm hoặc sửa

Mọi nội dung dựa trên dữ kiện có thật: `git log`, PR #1–#28, release
`v0.1.0`/`v0.2.0` (ghi chú trên GitHub), báo cáo rà soát, backlog.

**Thêm:**

1. **`CHANGELOG.md`** (thư mục gốc, theo Keep a Changelog): `v0.1.0`
   (29/10/2026), `v0.2.0` (09/11/2026), `Unreleased` (Sprint 3 và các sửa
   sau). Mỗi bản chia Thêm / Đổi / Sửa theo story và AC; lấy từ ghi chú
   release, PR #24–#28 và báo cáo rà soát. Chỉ thay đổi của sản phẩm, không
   ghi tên người.
2. **`docs/testing.md`:** chiến lược (unit test với DAO giả `KhoGia`,
   `KhoThuPhiGia`, `KhoPhieuThuGia`; IT trên MySQL `*IT` với `CsdlThu`; test
   bố cục `BoCucFxmlTest`, `MauInTest` bằng Monocle; kiểm tra tay những gì
   không tự động được: in thật, hộp thoại máy in, nhìn bằng mắt); cách chạy
   từng loại (IntelliJ, `mvn test`, `mvn verify -Pmysql`, tạo CSDL
   `bluemoon_test`, biến môi trường, CI hai job); số test hiện tại; bảng
   **ánh xạ AC → test tự động** cho mọi AC đã làm (Sprint 1–3), có cột "kiểm
   tay" cho AC không tự động được (AC-032, AC-072, in ấn). Bảng AC Sprint 3
   đã có trong `sprint-3-ra-soat.md` mục 3; Sprint 1–2 lập từ tên test và
   `traceability-matrix`.
3. Báo cáo này.

**Sửa:**

| Tệp | Thay đổi |
|---|---|
| `README.md` | Mô tả hiện tại; trạng thái Sprint (Sprint 3 "Hoàn thành, chờ gắn tag `v0.3.0`" hoặc "Hoàn thành `v0.3.0`" sau khi tag); thêm `thu-phi` và `docs/review`, `docs/meeting-notes`, `docs/testing.md`, `CHANGELOG.md` vào cấu trúc và mục lục tài liệu; CI hai job; liên kết Release; không đụng mục Thành viên |
| `docs/setup.md` | Cảnh báo `schema.sql`/`seed.sql` có `USE bluemoon` (S3-G1); ghi chạy lại `seed.sql` an toàn nhiều lần và khi nào phải dựng lại; kịch bản demo mục 5 cho khớp seed mới; trỏ sang `testing.md` |
| `docs/design/use-case.md` | Bỏ "Sprint 2–3, cần chốt lại", thêm US-026, US-027 vào bảng trạng thái (S2-N12) |
| `docs/design/erd.md` | Dòng 202: `DOI_CHU_HO` đã dùng cho US-026 |
| `docs/design/ui.md` | Ghi trạng thái thật theo cột "Đã làm / Sprint 4": kích thước cửa sổ, lỗi nhập liệu một nhãn chung, bộ lọc khoản thu chưa có, nút hộp thoại, in ấn (S2-N13) |
| `docs/design/class-diagram.md`, `architecture.md` | Thêm test mới (`SeedIT`, `BienDongServiceTest`, handler lỗi toàn cục); khớp mã sau PR mã |
| `docs/requirements/08-product-backlog.md` | Ghi Sprint 3 đã merge, tag `v0.3.0` (khi có) |
| `docs/requirements/11-chot-va-gia-dinh.md` | Giả định mới: `_ci` không phân biệt hoa thường và dấu (S2-G2, F4); hộ đã chuyển đi không ghi đóng góp (nếu duyệt, S3-G3) |
| `docs/workflow.md` | CI hai job; báo cáo rà soát trước khi merge; bước phát hành (đặt version `pom.xml`, tag, ghi `CHANGELOG.md`, ghi chú release) |
| `docs/thuat-ngu.md` | Thêm mục nếu có thuật ngữ mới sau sửa chữ |
| `.github/PULL_REQUEST_TEMPLATE.md` | Chỉ nếu cần nhắc cập nhật `CHANGELOG.md` |

Mermaid: kiểm lại cả 5 sơ đồ (`erd.md`, `class-diagram.md` ×2, `use-case.md`,
`workflow.md`) bằng `mermaid-cli` trong PR tài liệu; rà soát Sprint 3 đã xác
nhận 4/5 bằng `mermaid.parse`, `workflow.md` không đổi từ Sprint 2 khi đã
render thành công.

## 4. Đề xuất chia PR

### PR 1: mã (`chore/don-muc-ton`, tách từ `main` ở `8a0aaba`)

Mỗi mục một commit, kèm test, tự build được, qua cả hai job CI:

| # | Commit | Nội dung | Test |
|---|---|---|---|
| 1 | Dữ liệu mẫu có dấu, quỹ theo ngày, họ tên tài khoản, ngày chuyển đến của người tạm trú | `seed.sql` (S2-N7, G10, G11, G14, S3-G2, F1, F2) | `SeedIT` mới |
| 2 | Bộ xử lý lỗi toàn cục | `App.java`, hộp thoại "Có lỗi không mong muốn, vui lòng thử lại" (S2-N11) | Test hàm dựng thông báo; kiểm tay hộp thoại |
| 3 | Hỏi xác nhận đổi vai trò và khóa tài khoản | `TaiKhoanController` (S2-N6) | Kiểm tay (Service không đổi) |
| 4 | Chữ hiển thị | 2.3 và S2-G7 | `BoCucFxmlTest` |
| 5 | Gộp thông báo trùng CCCD | `LoiTrung` (S2-N10a) | `HoGiaDinhServiceTest`, `NhanKhauServiceTest` hiện có |
| 6 | `doiChuHo` chỉ đặt người đang ở làm chủ hộ | `NhanKhauDaoImpl` (S2-G5) | IT mới trong `GiaoDichHoGiaDinhIT` |
| 7 | `daIn` không đếm phiếu đã hủy; bỏ javadoc mồ côi | `PhieuThuDaoImpl.java:107`, `TaiKhoanService.java:21` (S3-G4, S2-G8) | `PhieuThuIT` |
| 8 | Hộ đã chuyển đi không ghi đóng góp tự nguyện | `ThuPhiService`, `ThuPhiController` (S3-G3), **chỉ nếu duyệt** | `ThuPhiServiceTest` |
| 9 | `CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS` | 3 Java + FXML (F3) | `BoCucFxmlTest`, `javac -Xlint:all` không còn cảnh báo |
| 10 | `BienDongServiceTest` | Thủ quỹ bị chặn xem lịch sử (S2-N9) | chính nó |

### PR 2: tài liệu (`docs/hoan-thien-tai-lieu`, tách từ `main` sau khi PR 1 merge)

Mỗi tệp hoặc nhóm tệp một commit: `CHANGELOG.md`, `docs/testing.md`, `README.md`,
`docs/setup.md`, thiết kế (`use-case.md`, `erd.md`, `ui.md`, `class-diagram.md`,
`architecture.md`), yêu cầu (`08`, `11`), `workflow.md`, `thuat-ngu.md`. Tách
sau PR 1 để `testing.md`, `setup.md` trích đúng tên test và dữ liệu mẫu mới.

Cả hai PR: commit đứng tên `Khanh Doan <dqkhanh412006@gmail.com>`, thông
điệp tiếng Việt, không thêm dòng đồng tác giả, không đụng
`config/db.properties`, không force push, không push vào `main`; chờ CI
xanh cả hai job trước khi báo.

### Câu hỏi đã quyết

Xem bảng "Xử lý sau khi duyệt" ở đầu báo cáo: chặn đóng góp cho hộ đã chuyển
đi (giả định 39), chưa gắn tag `v0.3.0` (gắn ở Sprint Review), quỹ mẫu theo
ngày chạy seed.
