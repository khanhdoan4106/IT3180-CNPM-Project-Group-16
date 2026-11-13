# Kiến trúc

Ứng dụng desktop JavaFX, dữ liệu tập trung ở MySQL (RAW-024, NFR-007).

```
View (FXML + CSS) -> Controller -> Service -> DAO -> MySQL
                                      |
                                    Model
```

| Tầng | Trách nhiệm | Không được làm |
|---|---|---|
| View (FXML) | Bố cục, kiểu dáng | Chứa logic nghiệp vụ |
| Controller | Nhận sự kiện, gọi service, hiển thị kết quả/lỗi | Gọi SQL, tính toán nghiệp vụ |
| Service | Quy tắc nghiệp vụ, kiểm tra hợp lệ | Biết về JavaFX |
| DAO | SQL qua PreparedStatement | Chứa quy tắc nghiệp vụ |
| Model | Dữ liệu thuần | Truy cập CSDL, in ấn |

## Quy ước
- Mỗi chức năng: `HoGiaDinh` (model), `HoGiaDinhDao`/`HoGiaDinhDaoImpl`,
  `HoGiaDinhService`, `HoGiaDinhController`, `ho-gia-dinh.fxml`.
- Ngoại lệ: "khoản thu" trên giao diện ứng với `KhoanPhi`, `KhoanPhiDao`,
  `KhoanPhiService` (bảng `khoan_phi`); controller và FXML vẫn là
  `KhoanThuController`, `khoan-thu.fxml` (đúng [thuật ngữ](../thuat-ngu.md)).
- Service nhận DAO qua constructor để test bằng DAO giả (xem
  `TaiKhoanServiceTest`).
- Luôn viết test cho service trước khi sửa/refactor.
- SQL luôn dùng `PreparedStatement`, không nối chuỗi từ dữ liệu người dùng.
- Không xóa cứng dữ liệu nghiệp vụ; đổi trạng thái để giữ lịch sử.
- Xuất tệp (gói `xuat`): dựng tệp `.xlsx` bằng Apache POI từ dữ liệu, tách
  khỏi giao diện nên test được không cần JavaFX (`BaoCaoThuExcel`,
  `TamVangExcel`); `TepExcel.ghiAnToan` ghi ra tệp tạm cùng thư mục rồi
  đổi tên, lỗi giữa chừng không để lại tệp hỏng. Tiền là ô số `#,##0`,
  ngày là ô ngày `dd/mm/yyyy` (giả định 42). Xuất tệp không ghi nhật ký.
- POI ghi log qua `log4j-api`; không có cài đặt log4j thì log4j in dòng
  "ERROR Log4j API could not find a logging provider" ra console (đã thử:
  bỏ phụ thuộc dưới đây thì dòng này hiện khi chạy test). Dự án thêm
  `log4j-to-jul` (phạm vi runtime, cùng bản `log4j-api` 2.24.3 mà POI
  5.5.1 dùng) để chuyển log sang `java.util.logging` có sẵn trong JDK:
  một phụ thuộc nhỏ, không cần tệp cấu hình, log gỡ lỗi của POI không
  hiện ở mức mặc định. Không chọn `log4j-core` (nặng, cần cấu hình) hay
  đặt thuộc tính hệ thống (phải nhớ đặt ở cả ứng dụng lẫn test).

## Phân quyền

Kiểm tra ở hai lớp (NFR-002, NFR-009):

1. **Controller** ẩn mục menu và nút mà vai trò không được dùng.
2. **Service** kiểm tra lại vai trò của người đang đăng nhập trước mỗi thao
   tác, ném `NghiepVuException` nếu không có quyền. Ẩn nút chưa đủ: lớp này
   chặn cả khi màn hình mở bằng đường khác (AC-062).

Mọi hàm public của Service nhận `TaiKhoan nguoi` và gọi
`PhanQuyen.yeuCau`, trừ năm hàm: `TaiKhoanService.dangNhap` và
`TaiKhoanService.lamMoiPhien` (chạy khi chưa có hoặc đang kiểm tra lại
phiên), `CongNoService.thangHienTai` và `ThongKeService.thangHienTai` (chỉ
đọc đồng hồ, không đọc dữ liệu nghiệp vụ), `ThuPhiService.kiemTraHinhThuc` (hàm tĩnh, chỉ kiểm tra dữ liệu
nhập, không đọc dữ liệu).

| Chức năng | Quản trị viên | Thủ quỹ | Căn cứ |
|---|---|---|---|
| Hộ gia đình, nhân khẩu, tạm trú/tạm vắng, báo cáo nhân khẩu | Có | Không | AC-060, AC-062 |
| Xem danh mục khoản thu | Có | Có | US-005 |
| Sửa đơn giá, tạo khoản thu tự nguyện | Có | Không | AC-016, US-006 |
| Chốt công nợ, sửa khoản thu của hộ | Có | Có | US-009, US-008 |
| Tra cứu phí, thu phí, đóng góp tự nguyện, biên lai | Có | Có | AC-060, US-007, US-010 – US-013 |
| Hủy phiếu thu | Mọi phiếu, trong ngày | Phiếu do mình lập, trong ngày | US-014, giả định 28 |
| In thông báo phí | Có | Có | US-015 |
| Tổng thu tháng ở Trang chính, thống kê theo khoản | Có | Có | US-016, US-019 |
| Hộ nợ đọng (màn Công nợ, thẻ "Hộ còn nợ") | Có | Có | US-017 |
| Xuất báo cáo thu ra Excel | Có | Có | US-018 |
| Xuất danh sách tạm vắng ra Excel | Có | Không | AC-014, như báo cáo nhân khẩu |
| Đăng ký và quản lý tài khoản | Có | Không | US-020, AC-061 |
| Tài khoản của tôi | Có | Có | US-023 |
| Xem nhật ký thao tác | Có | Không | TS-002, AC-069 |

Tra cứu phí (US-010) chỉ hiện thông tin hộ cần cho việc thu: mã phòng, chủ
hộ, các khoản chưa đóng, các quỹ tự nguyện đang mở.

## Chức năng và mã nguồn

| Chức năng | Story | Controller / FXML | Service | DAO | Test |
|---|---|---|---|---|---|
| Đăng nhập | US-021 | `DangNhapController`, `dang-nhap.fxml` | `TaiKhoanService` | `TaiKhoanDao` | `TaiKhoanServiceTest`, `MatKhauUtilTest` |
| Tài khoản của tôi, quản lý tài khoản | US-020, US-022, US-023 | `TaiKhoanController`, `tai-khoan.fxml` | `TaiKhoanService` | `TaiKhoanDao` | `TaiKhoanServiceTest` |
| Hộ gia đình | US-001 | `HoGiaDinhController`, `ho-gia-dinh.fxml` | `HoGiaDinhService` | `HoGiaDinhDao` | `HoGiaDinhServiceTest` |
| Nhân khẩu, đổi chủ hộ | US-002, US-026 | `NhanKhauController`, `nhan-khau.fxml` | `NhanKhauService`, `KiemTraNhanKhau` | `NhanKhauDao` | `NhanKhauServiceTest` |
| Tạm trú, tạm vắng | US-003 | `NhanKhauController` | `DotCuTruService` | `DotCuTruDao` | `DotCuTruServiceTest` |
| Nhật ký thao tác | TS-002 | `TaiKhoanController` (tab "Nhật ký thao tác"), `tai-khoan.fxml` | `NhatKyService` | `NhatKyDao` (ghi trong giao dịch của từng DAO thao tác, `NoiDungNhatKy`) | `NhatKyServiceTest`, `ThaoTacTest`, `NoiDungNhatKyTest`, `NhatKyIT`, `BoCucFxmlTest` |
| Lịch sử biến động nhân khẩu | US-027 | `HoGiaDinhController`, `TrangChinhController`, `BangLichSu` | `BienDongService` | `BienDongDao` (ghi trong giao dịch của DAO hộ, nhân khẩu, đợt) | `BienDongServiceTest`, `HoGiaDinhServiceTest`, `NhanKhauServiceTest`, `DotCuTruServiceTest`, `GiaoDichHoGiaDinhIT` |
| Khung ứng dụng, menu theo vai trò | US-022 | `TrangChinhController`, `trang-chinh.fxml` | `PhanQuyen` | – | `TrangChinhMenuTest` |
| Tổng thu tháng, biểu đồ 6 tháng ở Trang chính | US-016 | `TrangChinhController`, `BieuDoThuPhi`, `trang-chinh.fxml` | `ThongKeService`, `ThangTrucY` | `ThongKeDao` | `ThongKeServiceTest`, `ThangTrucYTest`, `DongTyLeTest`, `BieuDoThuPhiTest`, `ThongKeIT` |
| Thống kê theo khoản | US-019 | `ThongKeController`, `thong-ke.fxml` | `ThongKeService` | `ThongKeDao`, `KhoanPhiDao` | `ThongKeServiceTest`, `ThongKeIT`, `BoCucFxmlTest` |
| Báo cáo thu tháng ra Excel | US-018 | `ThongKeController` (phần "Báo cáo thu theo tháng"), `thong-ke.fxml` | `BaoCaoThuService`, `BaoCaoThuExcel`, `TepExcel` | `BaoCaoThuDao` | `BaoCaoThuServiceTest`, `BaoCaoThuExcelTest`, `TepExcelTest`, `BaoCaoThuIT`, `BoCucFxmlTest` |
| Khoản thu | US-005, US-006 | `KhoanThuController`, `khoan-thu.fxml` | `KhoanPhiService` | `KhoanPhiDao` | `KhoanPhiServiceTest` |
| Chốt, sửa công nợ | US-009, US-008 | `CongNoController`, `cong-no.fxml` (tab "Chốt công nợ") | `CongNoService` | `CongNoDao` | `CongNoServiceTest`, `CongNoIT` |
| Hộ nợ đọng, thẻ "Hộ còn nợ" | US-017 | `CongNoController`, `cong-no.fxml` (tab "Hộ nợ đọng"), `TrangChinhController` | `NoDongService` | `NoDongDao` | `NoDongServiceTest`, `TheHoConNoTest`, `NoDongIT`, `BoCucFxmlTest` |
| Tra cứu, thu phí, đóng góp, biên lai, hủy phiếu | US-007, US-010 – US-014 | `ThuPhiController`, `thu-phi.fxml`, `TomTatPhieuHomNay`, `HopThoai`, `MauIn`, `InAn` | `ThuPhiService` | `PhieuThuDao`, `KhoanPhiDao` | `ThuPhiServiceTest`, `SoTienTest`, `MauInTest`, `TomTatPhieuHomNayTest`, `HopThoaiHuyPhieuTest`, `DangBiKhoaExceptionTest`, `DongHoCsdlTest`, `PhieuThuIT`, `DongHoCsdlIT` |
| Thông báo phí hằng tháng | US-015 | `CongNoController` (nút "In thông báo phí"), `MauIn` | `ThongBaoPhiService` | `PhieuThuDao`, `CongNoDao` | `ThongBaoPhiServiceTest`, `MauInTest`, `PhieuThuIT` |
| Danh sách nhân khẩu tạm vắng (xem, in, xuất Excel) | US-004 | `NhanKhauController`, `DanhSachTamVang`, `MauIn` | `BaoCaoNhanKhauService`, `TamVangExcel`, `TepExcel` | `BaoCaoNhanKhauDao` | `BaoCaoNhanKhauServiceTest`, `TamVangExcelTest`, `DanhSachTamVangTest`, `MauInTest`, `PhieuThuIT` |
| Báo lỗi không lường trước | – | `App`, `LoiKhongMongDoi`, `LoiCsdl` | – | – | `LoiKhongMongDoiTest`, `LoiCsdlTest` |
| Sao lưu, khôi phục thử | TS-001 | – (chạy ngoài ứng dụng) | – | `scripts/sao-luu.ps1`, `khoi-phuc-thu.ps1`, `dang-ky-lich-sao-luu.ps1`, `mysql-chung.ps1` gọi `mysqldump`, `mysql` | `ScriptSaoLuuTest`, `SaoLuuIT`, `src/test/scripts/kiem-thu-sao-luu.ps1` |
| Dữ liệu mẫu | US-001 – US-003, US-005, US-006, US-027 | – | – | `database/seed.sql` | `SeedIT` |
| Bố cục các màn hình | NFR-008 | 9 tệp FXML, `giao-dien.css` | – | – | `BoCucFxmlTest` |

Thêm chức năng mới thì thêm một dòng vào bảng này trong cùng Pull Request.
Chiến lược kiểm thử và bảng AC → test: [../testing.md](../testing.md).

## Nhật ký thao tác

TS-002, giả định 44. Mỗi thao tác thay đổi dữ liệu quan trọng ghi một dòng
vào bảng `nhat_ky` bằng `NhatKyDaoImpl.ghi(conn, nguoiId, ThaoTac,
doiTuong, noiDung)`, gọi **trên kết nối của giao dịch thao tác**, cùng cách
`BienDongDaoImpl.ghi`. Thao tác lỗi hoặc hoàn tác thì không còn dòng nhật
ký; ghi nhật ký lỗi thì thao tác cũng hoàn tác.

- Danh sách thao tác là enum `ThaoTac` (mã lưu CSDL, nhãn có dấu), khớp
  ràng buộc `chk_nky_thao_tac` (`ThaoTacTest` kiểm tra). Khoản bắt buộc
  có sẵn từ `seed.sql`, ứng dụng không tạo, nên không có mã nhật ký.
- DAO cần biết người làm nhận thêm `nguoiId` (vd `HoGiaDinhDao.capNhat`,
  `KhoanPhiDao.capNhatDonGia`, `TaiKhoanDao.capNhatVaiTro`); Quản trị viên
  đặt lại mật khẩu dùng `TaiKhoanDao.datLaiMatKhau`, còn tự đổi mật khẩu
  (`capNhatMatKhau`) và đăng nhập không ghi.
- Sửa hộ, nhân khẩu: khóa dòng (`FOR UPDATE`), đọc giá trị cũ trong cùng
  giao dịch, chỉ ghi trường thật sự đổi, vd "Diện tích: 75,50 m² → 80,00
  m²"; không đổi gì thì không ghi. Đổi đơn giá, vai trò, trạng thái tài
  khoản, số tiền phải nộp ghi "cũ → mới" đọc dưới khóa.
- Không bao giờ ghi mật khẩu hay mã băm.
- Bảng chỉ thêm: trigger `trg_nky_chan_sua`, `trg_nky_chan_xoa` trả
  `SQLSTATE 45000` cho mọi `UPDATE`, `DELETE`. Thân trigger là một câu
  `SIGNAL`, không `BEGIN…END`, để `schema.sql` không cần `DELIMITER`
  và `CsdlThu` vẫn tách lệnh theo dấu `;`.
- Xem: `NhatKyService.theoKhoang` (chỉ Quản trị viên, từ ngày ≤ đến ngày,
  lọc theo loại), mới nhất ở trên, không phân trang (quy mô vài trăm hộ).

## Giao dịch và khóa

Mọi thao tác ghi nhiều bảng chạy trong một giao dịch (`setAutoCommit(false)`)
trên một kết nối, ở mức cô lập mặc định của InnoDB, `REPEATABLE READ`.

- **Thứ tự khóa** khi thu tiền và sửa công nợ: dòng `ho_gia_dinh`, rồi các
  dòng `phai_nop`, cuối cùng dòng `bo_dem_bien_lai` của năm
  (`PhieuThuDaoImpl`, `CongNoDaoImpl.suaSoTien`). Mọi giao dịch ghi trên một
  hộ khóa theo cùng thứ tự nên không tạo vòng chờ; MySQL vẫn hủy (1213, 1205)
  thì ra `DangBiKhoaException`.
- **Quyết định dưới khóa thì đọc có khóa.** Câu `SELECT` thường đầu tiên của
  giao dịch chụp snapshot; mọi `SELECT` thường sau đó đọc snapshot này, kể cả
  khi giao dịch vừa chờ khóa xong và máy khác đã commit trong lúc chờ. Vì
  vậy, câu đọc dùng để quyết định (khoản đã thu chưa, còn là chủ hộ không, đợt
  có chồng ngày không) phải là đọc có khóa (`FOR UPDATE`, `FOR SHARE`), hoặc
  là câu đọc thường **đầu tiên** của giao dịch, chạy sau khi đã giữ khóa mà
  mọi giao dịch ghi dữ liệu đó cũng phải lấy. `CongNoDaoImpl.suaSoTien` từng
  vi phạm quy tắc này: nó đọc `ho_id` bằng câu thường trước khi khóa, nên
  sửa được số tiền của khoản vừa được thu ở máy khác (AC-026, kiểm bằng
  `SuaSoTienDocKhoaIT`); nay kiểm tra "đã thu" bằng `FOR SHARE`.
- **Cẩn thận với `FOR SHARE`/`FOR UPDATE` khi không có dòng khớp:** InnoDB
  khóa cả khoảng trống trong chỉ mục (gap lock), chặn `INSERT` của giao dịch
  khác vào khoảng đó. Hai giao dịch cùng giữ khóa khoảng trống rồi cùng chèn
  vào khoảng đó thì deadlock. Vì vậy lập phiếu (`PhieuThuDaoImpl.lapPhieu`)
  và ghi đợt tạm trú (`DotCuTruDaoImpl.them`) giữ câu kiểm tra là đọc thường,
  đúng nhờ không có câu đọc thường nào trước khóa; thêm câu đọc thường trước
  khóa trong hai giao dịch này sẽ làm lỗi cùng loại quay lại.
- Câu đọc chỉ để ghi nội dung nhật ký (mã căn hộ, tên khoản) dùng đọc thường.
- Trùng dữ liệu (thu trùng, mã căn hộ, CCCD, chủ hộ, tên khoản) được chặn
  bằng ràng buộc `UNIQUE` trên cột sinh hoặc cột thường, độc lập với cách đọc.

Kết quả rà từng giao dịch:
[doc-co-khoa-trong-giao-dich.md](../review/doc-co-khoa-trong-giao-dich.md).

## Kết nối CSDL

`Database` đọc `config/db.properties` rồi biến môi trường
`BLUEMOON_DB_URL`, `BLUEMOON_DB_USER`, `BLUEMOON_DB_PASSWORD` (biến môi
trường ghi đè file) **một lần** ở lần dùng đầu, giữ trong bộ nhớ; mỗi
`getConnection()` chỉ mở một kết nối mới. Đổi cấu hình thì khởi động lại ứng
dụng. Đọc lỗi thì không lưu, lần sau thử đọc lại. Test đặt lại cấu hình bằng
`Database.datLaiChoTest`.

Giờ ghi vào CSDL không phụ thuộc máy chạy ứng dụng hay múi giờ của máy chủ
MySQL (máy chủ trên CI chạy UTC), nên múi giờ được cố định ở ba chỗ:

- `Database.getConnection` chạy `SET time_zone = '+07:00'` cho mỗi phiên, để
  các cột `DEFAULT CURRENT_TIMESTAMP`, `NOW()` và `CURDATE()` do MySQL điền
  luôn theo giờ Việt Nam;
- `Launcher` đặt múi giờ mặc định của JVM là `Asia/Ho_Chi_Minh`;
- DAO ghi và đọc `DATETIME` bằng `LocalDateTime` (`setObject`/`getObject`),
  không qua `Timestamp` vốn dịch giờ theo múi giờ của JVM.

Lý do: phiếu lập 23:30 ngày 31/12 từng bị lưu sang năm sau và ràng buộc
`chk_pt_nam` từ chối (kiểm bằng `MuiGioIT`).

### Những điểm chưa làm

| Điểm | Lý do chưa làm |
|---|---|
| Pool kết nối, chạy tác vụ CSDL ở luồng nền, rút thời gian chờ khóa | Chưa có dữ liệu và tải nhiều người dùng để đo. Xem xét lại khi có số liệu thật. |
| Controller tạo DAO trực tiếp | Nghiệp vụ nằm ở Service và đã được test bằng DAO giả. |
| Bộ đếm biên lai khóa dòng | Cố ý để số biên lai liên tục (giả định 31). |
| Phân trang | Quy mô vài trăm hộ. Thống kê Sprint 4 phải tổng hợp bằng SQL, không tải hết dữ liệu lên bộ nhớ. |
| Phiên làm việc | Mỗi máy một người dùng, đã đọc lại tài khoản trước mỗi màn hình. Tự đăng xuất khi không thao tác để sau. |

## Sao lưu

TS-001, giả định 43. Sao lưu chạy **ngoài ứng dụng**, trên máy đặt MySQL:
Task Scheduler gọi `scripts/sao-luu.ps1` mỗi ngày; ứng dụng Java không
biết gì về sao lưu và không cần quyền gì thêm. Hướng dẫn dùng:
[setup.md](../setup.md), mục 7.

- `mysqldump --single-transaction --routines --triggers
  --set-gtid-purged=OFF --default-character-set=utf8mb4 <CSDL>`: mọi bảng
  là InnoDB nên `--single-transaction` cho ảnh nhất quán mà không khóa bảng
  (máy đang thu tiền vẫn ghi được). Không dùng `--databases` để tệp không
  có `USE`, khôi phục được vào CSDL tên khác (khôi phục thử, kiểm thử).
- Mật khẩu chỉ ở login-path (`mysql_config_editor`) hoặc tệp tùy chọn
  `[client]` ngoài repo, truyền bằng `--login-path`/`--defaults-extra-file`;
  không có tham số mật khẩu, không ghi vào log.
- Script đọc stdout của `mysqldump` thành byte và ghi thẳng ra tệp (không
  qua đường ống PowerShell vốn đổi mã hóa), ghi tệp `.tmp` rồi đổi tên; chỉ
  thành công khi mã thoát 0 và tệp kết thúc bằng `-- Dump completed`. Chỉ
  lần thành công mới xóa bản cũ (giữ N bản mới nhất theo mẫu tên). Mỗi lần
  chạy một dòng `sao-luu.log`; lỗi thoát mã 1 để Task Scheduler ghi lần
  chạy lỗi.
- Tệp tạo lại hai trigger của `nhat_ky` và cột sinh (`phai_nop_da_thu`,
  `chu_ho_cua`, `cccd_dang_o`, `ma_dang_o`) vẫn là cột sinh, nên ràng buộc
  chống thu trùng và nhật ký chỉ thêm còn nguyên sau khôi phục (`SaoLuuIT`).
- `khoi-phuc-thu.ps1` nạp tệp vào `bluemoon_khoi_phuc_thu`, so số dòng từng
  bảng, tổng tiền phiếu thu và trigger với CSDL nguồn; từ chối ghi vào
  `bluemoon` nếu không có `-GhiDe` kèm xác nhận.
- Script lưu UTF-8 có BOM để Windows PowerShell 5.1 đọc đúng tiếng Việt
  (`ScriptSaoLuuTest`).

## Giao diện

Bố cục, màn hình, phương án màu và đặc tả: [ui.md](ui.md). Màu dùng biến
`-mau-*` trong `css/giao-dien.css`; đổi phương án màu chỉ sửa các biến này.
