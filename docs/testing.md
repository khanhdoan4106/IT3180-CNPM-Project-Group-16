# Kiểm thử

Tài liệu này mô tả cách nhóm kiểm thử BlueMoon: loại test nào kiểm cái gì,
chạy thế nào, tiêu chí chấp nhận (AC) nào đã có test tự động, và việc nào
còn phải kiểm tay. Quy tắc chung khi hoàn thành một story nằm ở
[Definition of Done](requirements/10-definition-of-done.md).

## 1. Chiến lược

| Loại | Lớp / thư mục | Kiểm cái gì | Cần gì để chạy |
|---|---|---|---|
| Test đơn vị (Service) | `src/test/java/com/bluemoon/service` | Quy tắc nghiệp vụ, kiểm tra dữ liệu, phân quyền (`PhanQuyen`), thông báo lỗi | Không cần MySQL: Service nhận DAO qua constructor, test dùng DAO giả trong bộ nhớ (`KhoGia`, `KhoThuPhiGia`, `KhoPhieuThuGia`) |
| Test đơn vị (tiện ích, DAO không cần CSDL) | `util`, `dao`, `config`, `controller`, `xuat` | Ghi tệp Excel rồi đọc lại bằng POI (ô số, ô ngày, cột, tổng), ghi tệp tạm rồi đổi tên, băm mật khẩu, đọc ngày, đổi mã lỗi MySQL thành lỗi nghiệp vụ (`DangBiKhoaException`, `TrungKhoaException`), đồng hồ `DongHoCsdl` với `Clock.fixed`, chọn thông báo lỗi (`LoiCsdl`, `LoiKhongMongDoi`), số tiền bằng chữ | Không |
| Test bố cục giao diện | `BoCucFxmlTest`, `MauInTest`, `HopThoaiHuyPhieuTest` | Nạp từng FXML ở cửa sổ 1120×645 bằng JavaFX chạy không màn hình (Monocle), kiểm không nút hay nhãn nào bị cắt thành "…", chữ gợi ý nằm trọn trong ô, mọi nút nằm trong vùng thấy được hoặc cuộn được, dùng đúng phông Be Vietnam Pro; không chữ nào dưới 14 px, nút và ô nhập cao ≥ 36 (nút chính ≥ 40), hai nút cạnh nhau cách ≥ 8 px, bảng không còn chữ "No content in table" (TS-004); số tiền lớn nhất vẫn vừa cột biên lai; hộp thoại hủy phiếu có đúng nút | Không cần màn hình, chạy được trên CI |
| Test hành vi giao diện | `PhimEnterTest`, `TrangChinhMenuTest`, `MauPhuTest`, `DeDocTest`, `ThuMotHoNamBuocTest`, `BieuDoThuPhiTest`, `TheHoConNoTest` | Tải FXML thật trên cửa sổ ảo (Monocle), gửi sự kiện Enter vào từng ô và xem nút nào chạy; thu một hộ trên màn Thu phí thật bằng phím và chuột giả lập, đếm thao tác và xem tiêu điểm sau mỗi bước (AC-072); tương phản mọi cặp chữ/nền đo từ CSS, chữ gốc 16 px, độ cao ô/nút/dòng bảng, viền tiêu điểm màu chính, CSS được JavaFX đọc hết; bấm logo về Trang chính, nút menu đang chọn có nền `#1d4ed8` do JavaFX vẽ, mục "Thống kê" nằm giữa Thu phí và Tài khoản; màu chữ phụ, tiêu đề cột và tương phản WCAG AA; biểu đồ thu phí vẽ thật đúng quy tắc biểu đồ (tên trục, trục Y từ 0, cột tháng hiện tại khác màu, số trên đầu cột); thẻ "Hộ còn nợ" mở màn Công nợ ở tab "Hộ nợ đọng" | Không cần màn hình, chạy được trên CI |
| Test tài liệu | `TruyVetTaiLieuTest`, `UiMdTest`, `TestingMdTest` | Mã RAW trích trong chú thích khớp bảng truy vết, `ui.md` giữ đủ quy tắc biểu đồ và bảng màu, mọi `Lớp#phương_thức` trong bảng AC → test ở đây còn tồn tại | Không |
| Kiểm thử script sao lưu | `src/test/scripts/kiem-thu-sao-luu.ps1`, `ScriptSaoLuuTest` | `sao-luu.ps1` với `mysqldump` giả: lỗi giữa chừng, tệp bị cắt dù mã 0, chỉ xóa bản cũ khi thành công, không đụng tệp khác mẫu tên, dòng log; tùy chọn `mysqldump` theo giả định 43, script lưu UTF-8 có BOM, không có tham số mật khẩu | PowerShell 5.1 hoặc `pwsh`, không cần MySQL; CI chạy ở job `build` |
| Kiểm thử tích hợp (`*IT`) | `src/test/java/com/bluemoon/it` | Phần chỉ MySQL thật mới chứng minh được: khóa dòng, deadlock, ràng buộc CSDL, giao dịch hoàn tác, giờ máy chủ, dữ liệu mẫu | MySQL 8, CSDL riêng có chữ `test` trong tên, profile `-Pmysql` |
| Kiểm tay | Theo bảng ở mục 5 | In thật qua máy in, hộp thoại máy in, nhìn bằng mắt, hai máy cùng dùng | Máy có giao diện |

Nguyên tắc: viết test cho Service trước khi sửa hay refactor; lỗi chỉ xảy ra
khi hai máy cùng ghi (trùng khóa, deadlock) thì có thêm một test `*IT` để
chứng minh trên MySQL thật, vì DAO giả không tái hiện được.

### Các lớp kiểm thử tích hợp

| Lớp | Nội dung |
|---|---|
| `CongNoIT` | Chốt công nợ, đơn giá và số tiền điền sẵn đọc từ CSDL, số quá lớn bị chặn trước khi tới MySQL |
| `SuaSoTienDocKhoaIT` | Máy B bắt đầu sửa số tiền một khoản (câu đọc đầu giao dịch đã chụp snapshot), máy A lập phiếu thu khoản đó và commit, rồi B mới khóa và kiểm tra: B không sửa được, số tiền và nhật ký giữ nguyên. Điều phối bằng điểm chèn `CongNoDaoImpl.sauKhiDocHo`, chạy tuần tự, không `sleep`; không có phiếu thì vẫn sửa được |
| `PhieuThuIT` | Thu tất cả rồi hủy khôi phục công nợ, hai máy cùng thu một khoản chỉ một phiếu thành công, sửa và thu song song không deadlock, đếm lần in, thông báo phí, danh sách tạm vắng, hộ đã chuyển đi không ghi đóng góp |
| `GiaoDichHoGiaDinhIT` | Lỗi giữa giao dịch thì không ghi hộ lẫn lịch sử; đổi chủ hộ từ chối người vừa chuyển đi ở máy khác |
| `DongHoCsdlIT` | Giờ đọc từ MySQL gần giờ máy, múi giờ Việt Nam |
| `MuiGioIT` | JVM đặt UTC, New York, Kiritimati: phiếu lập 23:30 ngày 31/12 vẫn là 23:30 cùng ngày (không sang năm sau), giờ hủy phiếu, giờ biến động và giờ sửa khoản do CSDL điền đọc lại đúng giờ Việt Nam; phiên MySQL luôn ở `+07:00` dù máy chủ đặt UTC (chạy thử bằng `mysqld --default-time-zone=+00:00`, như máy chủ trên CI) |
| `NhatKyIT` | Mỗi nhóm thao tác (hộ, nhân khẩu, tạm trú/tạm vắng, khoản thu, công nợ, phiếu thu, tài khoản) ghi đúng một dòng nhật ký với người làm, thao tác, đối tượng, nội dung đúng; sửa hộ, nhân khẩu chỉ ghi trường thật sự đổi; không ghi mã băm, không ghi tự đổi mật khẩu; thao tác xung đột hoặc trùng khóa không để lại dòng; ghi nhật ký lỗi thì thao tác hoàn tác; MySQL từ chối `UPDATE`, `DELETE` trên `nhat_ky`; chạy lại `schema.sql` không mất nhật ký; thời điểm theo giờ Việt Nam dù máy chủ chạy UTC; lọc theo khoảng ngày, loại thao tác, mới nhất ở trên |
| `ThongKeIT` | Tổng thu tháng không tính phiếu đã hủy; ranh giới tháng (23:59:59 ngày cuối tháng, 00:00:00 ngày đầu tháng sau); nợ tháng trước đóng tháng này tính tháng này; 6 tháng có tháng trống là 0; khoản bắt buộc theo tháng chốt (khoản sửa về 0 đ không tính phải đóng) và tỷ lệ thu tháng hiện tại; khoản tự nguyện đếm hộ đã đóng và hộ ghi 0 đ; chưa có giao dịch thì 0 đ |
| `NoDongIT` | Hộ nợ đọng: khoản đã thu không tính, khoản trong phiếu đã hủy tính lại là nợ, khoản sửa về 0 đ và đóng góp tự nguyện không tính, hai khoản cùng tháng chỉ tính một tháng, hộ đã chuyển đi vẫn có (kèm chủ hộ cũ), tổng nợ, thứ tự (số tháng, tổng nợ, mã căn hộ), tóm tắt cho Trang chính |
| `BaoCaoThuIT` | Báo cáo thu tháng: phiếu đã hủy không có (khoản chỉ có trong phiếu hủy không thành cột), ranh giới tháng (23:59:59 cuối tháng, 00:00:00 đầu tháng sau), chủ hộ, số biên lai, phiếu thu hai tháng phí dịch vụ cộng vào một cột, tháng chưa có phiếu bị từ chối, tệp ghi từ CSDL thật đọc lại được |
| `SaoLuuIT` | Chạy `mysqldump` với đúng các tùy chọn đọc từ `scripts/sao-luu.ps1` trên dữ liệu đủ mọi bảng (hộ, nhân khẩu, tạm vắng, biến động, khoản thu, công nợ, phiếu thu đã hủy và chưa hủy, nhật ký), nạp vào CSDL mới tên khác bằng `mysql`: số dòng mọi bảng, tổng tiền phiếu thu, tên có dấu, cột sinh (`phai_nop_da_thu`, `chu_ho_cua`, `cccd_dang_o`, `ma_dang_o`) vẫn đúng và vẫn là cột sinh, `UNIQUE` trên cột sinh vẫn chặn, trigger vẫn chặn `UPDATE`/`DELETE` trên `nhat_ky`; tự bỏ qua kèm thông báo nếu máy không có `mysqldump`/`mysql` |
| `SeedIT` | Dựng CSDL từ `schema.sql`, chạy `seed.sql` ba lần: số dòng không đổi, tên có dấu (so trong Java, không dựa `=` của MySQL), hai quỹ mẫu đang mở, ba trường hợp tạm trú/tạm vắng |

Mỗi lớp `*IT` bỏ qua (không lỗi) nếu chưa đặt biến môi trường hoặc tên CSDL
không có chữ `test`, để không xóa nhầm CSDL `bluemoon` đang dùng.

## 2. Cách chạy

### Test đơn vị (không cần MySQL)

| Cách | Lệnh / thao tác |
|---|---|
| IntelliJ | Maven → bluemoon → Lifecycle → `test` |
| Dòng lệnh | `mvn test` hoặc `mvn verify` (cần Maven 3.9+ trong PATH) |

Kết quả mong đợi: `BUILD SUCCESS`, 366 test (1 bỏ qua: `ChupManHinhTest`
chỉ chạy khi có `-DchupAnh`). `mvn verify` không có
`-Pmysql` không chạy lớp `*IT`.

Chạy riêng test bố cục sau khi sửa FXML hoặc CSS:

```
mvn test -Dtest=BoCucFxmlTest
```

Chụp ảnh các màn hình (PNG, Monocle, không điều khiển màn hình) cho báo cáo
rà soát giao diện; chỉ chạy khi có `-DchupAnh`:

```
mvn test -Dtest=ChupManHinhTest,ThuMotHoNamBuocTest,TrangChinhSoLieuTest -DchupAnh=docs/review/ts-004 -DchupTienTo=sau
```

### Kiểm thử tích hợp (cần MySQL)

1. Tạo CSDL **riêng có chữ `test`**, vì mỗi test xóa và tạo lại mọi bảng của
   CSDL đó:
   ```sql
   CREATE DATABASE bluemoon_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   GRANT ALL ON bluemoon_test.* TO 'bluemoon'@'localhost';
   ```
2. Đặt biến môi trường (PowerShell) rồi chạy:
   ```powershell
   $env:BLUEMOON_DB_URL = "jdbc:mysql://localhost:3306/bluemoon_test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8"
   mvn verify -Pmysql
   ```
   Nếu user không phải `bluemoon`, đặt thêm `BLUEMOON_DB_USER` và
   `BLUEMOON_DB_PASSWORD`.

Kết quả mong đợi: 366 test đơn vị và 62 kiểm thử tích hợp đều đạt (MySQL
8.0). Hai test cấu hình (`DatabaseConfigTest`) tự bỏ qua khi đã đặt
`BLUEMOON_DB_*`, nên log hiện "Skipped: 2" ở lần chạy này.

### Trên GitHub

Workflow [`ci.yml`](../.github/workflows/ci.yml) chạy cho mỗi Pull Request
và mỗi lần đẩy vào `main`, gồm hai job; nhóm chỉ merge Pull Request khi cả
hai xanh:

| Job | Làm gì |
|---|---|
| `build` | JDK 21, `mvn -B verify`: test đơn vị và test bố cục; `pwsh src/test/scripts/kiem-thu-sao-luu.ps1` |
| `build-mysql` | Service `mysql:8.0`, CSDL `bluemoon_test`, `mvn -B verify -Pmysql`: thêm 62 kiểm thử tích hợp; rồi chạy thật `scripts/sao-luu.ps1` trên CSDL dựng từ `schema.sql`, `seed.sql` và hai phiếu thu mẫu (một phiếu đã hủy; tệp tùy chọn tạo trong `RUNNER_TEMP`), `scripts/khoi-phuc-thu.ps1` trên tệp vừa tạo (phải `KHỚP`, rồi sửa nguồn thêm 100.000 đ cho cùng chữ số đầu và phải báo lệch tổng tiền, mã 1), và một lần sao lưu với mật khẩu sai (mã thoát khác 0, dòng `LỖI`, không còn tệp dở, giữ bản cũ) |

## 3. Số test hiện tại

| Mốc | Test đơn vị | Kiểm thử tích hợp |
|---|---|---|
| `v0.1.0` | 97 | – |
| `v0.2.0` | 169 | có (`mvn verify -Pmysql`) |
| `v0.3.0` (Sprint 3 và các sửa #28 – #35) | 269 | 30 |
| Sprint 4, sau TS-002 | 279 | 40 |
| Sprint 4, sau US-016, US-019 | 318 | 47 |
| Sprint 4, sau US-017 | 326 | 53 |
| Sprint 4, sau US-018 và AC-014 | 347 | 57 |
| Sprint 4, sau TS-001 (chưa phát hành) | 350 | 60 |
| Sprint 4, sau sửa đọc có khóa trong `suaSoTien` (chưa phát hành) | 350 | 62 |
| `v1.0.0` (Sprint 4, sau TS-004) | 366 | 62 |

## 4. Ánh xạ AC → test tự động

Mỗi AC đã làm đều có ít nhất một test; cột "Kiểm tay" ghi phần không tự động
được. Tên test viết tắt dạng `Lớp#hàm`; lớp `*ServiceTest` nằm ở
`src/test/java/com/bluemoon/service`, `*IT` ở `.../it`.

Ký hiệu: ✓ có test tự động đủ; ◐ có test nhưng một phần còn kiểm tay.

### Sprint 1

| AC | Test tự động | Kết quả |
|---|---|---|
| AC-001 tạo hộ khi nhập đủ | `HoGiaDinhServiceTest#themDayDuThanhCongVaHienTrongDanhSach`, `#themHoTaoLuonChuHoVaDemMotThanhVien` | ✓ |
| AC-002 lưu đúng diện tích | `HoGiaDinhServiceTest#dienTichLuuDungGiaTriDaNhap`, `#dienTichKhongHopLeBiTuChoi` | ✓ |
| AC-003 thiếu thông tin thì báo, không lưu | `HoGiaDinhServiceTest#thieuTruongBatBuocBaoDuCacTruongVaKhongLuu` | ✓ |
| AC-004 trùng mã căn hộ | `HoGiaDinhServiceTest#trungMaCanHoBaoTrungVaKhongLuu`, `#haiMayCungThemMotMaCanHoThiBaoTrungChuKhongBaoLoiCsdl` | ✓ |
| AC-005 thêm nhân khẩu | `NhanKhauServiceTest#themNhanKhauLuuVaoHoVaCapNhatSoThanhVien` | ✓ |
| AC-006 quan hệ với chủ hộ | `NhanKhauServiceTest#luuDungQuanHeDaChon` | ✓ |
| AC-007 chỉ một chủ hộ | `NhanKhauServiceTest#themChuHoThuHaiBiTuChoi`, `#haiMayCungThemChuHoThiBaoMotChuHo` | ✓ |
| AC-008 cảnh báo trùng CCCD | `NhanKhauServiceTest#cccdDaTonTaiBiCanhBaoVaKhongLuu`, `#haiMayCungNhapMotCccdThiBaoTrungCccd` | ✓ |
| AC-009 ghi nhận tạm trú, tạm vắng | `DotCuTruServiceTest#ghiNhanTamVangVaCapNhatTinhTrang`, `#tamTruChiGhiChoNguoiTamTruConTamVangChiChoNguoiThuongTru` | ✓ |
| AC-010 thiếu ngày thì báo lỗi | `DotCuTruServiceTest#thieuNgayBatDauHoacKetThucThiBaoLoiVaKhongLuu` | ✓ |
| AC-011 ngày kết thúc trước ngày bắt đầu | `DotCuTruServiceTest#ngayKetThucTruocNgayBatDauBiTuChoi` | ✓ |
| AC-054 đăng ký hợp lệ | `TaiKhoanServiceTest#dangKyHopLeTaoTaiKhoan`, `#dangKyLuuMatKhauDaBam` | ✓ |
| AC-055 trùng tên đăng nhập | `TaiKhoanServiceTest#dangKyTrungTenBaoTrung`, `#dangKyTrungLucLuuBaoTrung` | ✓ |
| AC-056 mật khẩu quá ngắn | `TaiKhoanServiceTest#dangKyMatKhauNganBaoLoi` | ✓ |
| AC-057 đăng nhập đúng | `TaiKhoanServiceTest#dangNhapDungThanhCong` | ✓ |
| AC-058 sai mật khẩu | `TaiKhoanServiceTest#saiMatKhauBiTuChoi`, `#khongCoTaiKhoanThongBaoGiongSaiMatKhau`, `MatKhauUtilTest#saiMatKhauKhongKhop` | ✓ |
| AC-059 chưa đăng nhập thì yêu cầu | `ThuPhiServiceTest#canDangNhap`, `CongNoServiceTest#canDangNhapMoiChotDuoc`, `BienDongServiceTest#chuaDangNhapBiChan` | ✓ |
| AC-070 ứng dụng kết nối MySQL | `DatabaseConfigTest#thieuFileKhongNemMaTraCauHinhRong`, `LoiCsdlTest` (phân biệt mất kết nối, thiếu cấu hình), mọi `*IT` | ◐ khởi động ứng dụng thật kiểm tay |
| AC-071 hai máy thấy cùng dữ liệu | `PhieuThuIT#haiMayCungThuMotKhoanChiMotPhieuThanhCong`, `HoGiaDinhServiceTest#haiMayCungThemMotMaCanHoThiBaoTrungChuKhongBaoLoiCsdl` (hai kết nối cùng CSDL) | ◐ hai máy thật kiểm tay |

### Sprint 2

| AC | Test tự động | Kết quả |
|---|---|---|
| AC-015 phí dịch vụ theo diện tích × đơn giá | `KhoanPhiServiceTest#doiDonGiaPhiDichVuTrongKhoang`, `CongNoServiceTest#xemTruocTinhTheoDienTichNhanDonGia` | ✓ |
| AC-016 phí quản lý mặc định 7.000 đ/m² | `KhoanPhiServiceTest#phiQuanLyKhongDuoiBayNghin` | ✓ |
| AC-017 đơn giá ngoài khoảng 2.500–16.500 | `KhoanPhiServiceTest#tuChoiDonGiaPhiDichVuNgoaiKhoang` | ✓ |
| AC-018 tổng tiền chính xác | `CongNoServiceTest#xemTruocTinhTheoDienTichNhanDonGia`, `#lamTronDenDong` | ✓ |
| AC-019 tạo khoản tự nguyện | `KhoanPhiServiceTest#taoKhoanTuNguyenCoDinhTheoHo`, `#tuChoiThieuHoacSaiThoiGian` | ✓ |
| AC-020 trùng tên trong cùng đợt | `KhoanPhiServiceTest#tuChoiTrungTenTrongCungDot`, `#trungTenDoMayKhacLuuCungLuc` | ✓ |
| AC-021 không nhập số tiền cố định vẫn lưu | `KhoanPhiServiceTest#taoKhoanTuNguyenKhongCoDinh` | ✓ |
| AC-025 sửa khoản chưa thu | `CongNoServiceTest#suaSoTienGiuSoGocVaNguoiSua`, `CongNoIT#soTienPhaiNopDienSanTuCsdlLuuLaiDuoc` | ✓ |
| AC-026 khoản đã thu thì từ chối sửa | `CongNoServiceTest#tuChoiSuaKhoanDaCoPhieuThu`, `ThuPhiServiceTest#khoanDaThuKhongSuaDuocChoDenKhiHuyPhieu`, `PhieuThuIT#nhieuLuongVuaSuaVuaThuCungMotKhoanKhongDeadlock`, `SuaSoTienDocKhoaIT#phieuLapGiuaChungThiKhongSuaDuocKhoanDaThu`, `#khongCoPhieuGiuaChungThiVanSuaDuoc` | ✓ |
| AC-027 chốt công nợ tháng | `CongNoServiceTest#chiTinhKhoanCoHieuLucTrongThang`, `#boQuaHoDaChuyenDiVaKhoanTuNguyen`, `#khongTinhHoChuyenDenSauThangChot`, `CongNoIT#chotThangCuKhongGhiNoHoChuyenDenSau` | ✓ |
| AC-028 chốt lại không tạo trùng | `CongNoServiceTest#chotLuuVaChotLaiKhongTaoTrung`, `#chotLaiBoSungHoMoiVaGiuKhoanDaSua` | ✓ |
| AC-029 hộ chưa có diện tích | `CongNoServiceTest#hoChuaCoDienTichBiBoQuaVaCanhBao` | ✓ |
| AC-060 Thủ quỹ chỉ thu phí và tra cứu | `TaiKhoanServiceTest#thuQuyKhongQuanLyDuocTaiKhoan`, `HoGiaDinhServiceTest#thuQuyKhongXemHaySuaDuocHo`, `KhoanPhiServiceTest#thuQuyKhongDoiDuocDonGia` | ◐ ẩn menu kiểm tay |
| AC-061 Quản trị viên dùng mọi chức năng | `TaiKhoanServiceTest#chiQuanTriDuocDangKy`, `#chiQuanTriVienXemDanhSach`, `KhoanPhiServiceTest#chiQuanTriVienTaoDuocKhoanThu` | ◐ ẩn menu kiểm tay |
| AC-062 không có quyền thì không thấy dữ liệu | `NhanKhauServiceTest#thuQuyKhongXemHaySuaDuocNhanKhau`, `BienDongServiceTest#thuQuyBiChanVaKhongChamTangDuLieu` | ✓ |
| AC-063 đổi mật khẩu | `TaiKhoanServiceTest#doiMatKhauThanhCongLuuBam`, `#matKhauMoiQuaNganBiTuChoi` | ✓ |
| AC-064 mật khẩu cũ sai | `TaiKhoanServiceTest#doiMatKhauSaiMatKhauCu` | ✓ |
| AC-065 sửa thông tin cá nhân | `TaiKhoanServiceTest#capNhatThongTinCaNhan` | ✓ |
| AC-073 đổi chủ hộ khi còn người khác | `NhanKhauServiceTest#doiChuHoChuHoCuThanhVoChong`, `#doiChuHoKhiChuHoCuRoiHo` | ✓ |
| AC-074 hộ chỉ còn một người | `NhanKhauServiceTest#khongDoiChuHoKhiHoChiConMotNguoi` | ✓ |
| AC-075 người được chọn đã chuyển đi | `NhanKhauServiceTest#khongChonNguoiDaChuyenDiLamChuHo`, `GiaoDichHoGiaDinhIT#doiChuHoTuChoiNguoiVuaChuyenDiOMayKhac` | ✓ |
| AC-076 ghi biến động khi lưu thành công | `HoGiaDinhServiceTest#lapHoVaChuyenDiGhiLichSu`, `NhanKhauServiceTest#themVaRoiHoGhiLichSu` | ✓ |
| AC-077 tạm trú, tạm vắng vào lịch sử | `DotCuTruServiceTest#ghiTamVangGhiLichSu`, `NhanKhauServiceTest#themNguoiTamTruLuuLuonDotTamTruDauTien`, `SeedIT#baTruongHopTamTruTamVangVaLichSuTamTru` | ✓ |
| AC-078 lỗi giữa chừng không ghi lịch sử | `GiaoDichHoGiaDinhIT#loiGiuaGiaoDichThiKhongGhiHoVaLichSu` | ✓ |
| AC-079 xem lịch sử của hộ | `BienDongServiceTest#quanTriVienXemLichSuCuaHoVaBienDongGanDay` | ✓ |

### Sprint 3

| AC | Test tự động | Kết quả |
|---|---|---|
| AC-012 danh sách tạm vắng | `BaoCaoNhanKhauServiceTest#danhSachTamVangTheoNgay`, `PhieuThuIT#thongBaoPhiVaDanhSachTamVang` | ✓ nội dung; in thật kiểm tay |
| AC-013 không có dữ liệu | `BaoCaoNhanKhauServiceTest#khongCoAiTamVangThiBaoKhongCoDuLieu` | ✓ |
| AC-022 đóng số tiền bất kỳ | `ThuPhiServiceTest#dongGopNhieuLanCongDon`, `PhieuThuIT#dongGopNhieuLanVaGhiKhongDong` | ✓ |
| AC-023 0 đ là "không đóng" | `ThuPhiServiceTest#ghi0DongLaKhongDongVaPhieuKhongCoHinhThuc`, `PhieuThuIT#dongGopNhieuLanVaGhiKhongDong` | ✓ |
| AC-024 nhiều quỹ một màn | `ThuPhiServiceTest#thuChungKhoanBatBuocVaNhieuQuyTrenMotPhieu`, `#quyKhongMoHoacSoSaiBiTuChoi` | ✓ |
| AC-030 hiển thị hộ và khoản chưa đóng | `ThuPhiServiceTest#traCuuTheoMaKhongPhanBietHoaThuong`, `#thongTinGomKhoanChuaDongVaQuyDangMo`, `#traCuuThayHoDaChuyenDiConNoXepSauHoDangO` | ✓ |
| AC-031 không tìm thấy phòng | `ThuPhiServiceTest#traCuuMaKhongTonTaiBaoKhongTimThay` | ✓ |
| AC-032 dưới 3 giây | – | Kiểm tay: đo 21 ms mỗi lần tra cứu |
| AC-033 thu tất cả, công nợ về 0 | `ThuPhiServiceTest#thuTatCaDuaCongNoVe0VaGhiNguoiThu`, `PhieuThuIT#thuTatCaRoiHuyKhoiPhucCongNo` | ✓ |
| AC-034 chỉ thu khoản đã chọn | `ThuPhiServiceTest#thuMotPhanChiGhiKhoanDuocChon`, `#khongChonKhoanNaoThiBaoLoi` | ✓ |
| AC-035 tổng tự cộng, có ngày thu | `ThuPhiServiceTest#thuTatCaDuaCongNoVe0VaGhiNguoiThu` | ◐ nhãn tổng trên màn hình kiểm tay |
| AC-036 lưu hình thức thanh toán | `ThuPhiServiceTest#thuTatCaDuaCongNoVe0VaGhiNguoiThu` | ✓ |
| AC-037 lưu người thu và thời điểm | `ThuPhiServiceTest#thuTatCaDuaCongNoVe0VaGhiNguoiThu`, `DongHoCsdlTest`, `DongHoCsdlIT` | ✓ |
| AC-038 chưa chọn hình thức thì báo lỗi | `ThuPhiServiceTest#chuaChonHinhThucThiBaoLoi`, `#kiemTraHinhThucChoManHinhTruocKhiXacNhan` (màn Thu phí báo trước hộp xác nhận) | ✓ |
| AC-039 nội dung biên lai | `SoTienTest` (số tiền bằng chữ), `ThuPhiServiceTest#soBienLaiLienTucVaDanhLaiKhiSangNam`, `MauInTest#soTienLonNhatDuocKiemLaMucTranNghiepVu` (cột tiền) | ◐ trang in xem tận mắt |
| AC-040 "bản in lại" | `ThuPhiServiceTest#inLanHaiLaBanInLai`, `PhieuThuIT#inBienLaiDemSoLanIn`, `#daInKhongDemPhieuDaHuy` | ◐ khung "BẢN IN LẠI" trên trang in kiểm tay |
| AC-041 hủy trong ngày, khôi phục công nợ | `ThuPhiServiceTest#thuQuyHuyPhieuMinhLapKhoanTroVeChuaDong`, `PhieuThuIT#thuTatCaRoiHuyKhoiPhucCongNo`, `HopThoaiHuyPhieuTest` | ✓ |
| AC-042 phiếu ngày trước thì từ chối | `ThuPhiServiceTest#khongHuyPhieuNgayTruoc`, `DongHoCsdlTest` | ✓ |
| AC-043 phiếu hủy giữ vết kèm người hủy | `ThuPhiServiceTest#phieuHomNayGomCaPhieuHuy`, `#huyCanLyDoVaKhongHuyHaiLan`, `#thuQuyKhongHuyPhieuNguoiKhacQuanTriThiDuoc`, `TomTatPhieuHomNayTest` | ✓ |
| AC-044 thông báo liệt kê khoản | `ThongBaoPhiServiceTest#thongBaoLietKeKhoanThangVaNoCu`, `#thangChuaChotChuaInDuoc` | ✓ |
| AC-045 không có khoản thì báo | `ThongBaoPhiServiceTest#hoKhongCoKhoanCanDongThiBao` | ✓ |
| AC-072 tối đa 5 bước, chữ rõ | `ThuMotHoNamBuocTest#thuMotHoTrongBonThaoTacTieuDiemDungCho` (4 thao tác, tiêu điểm đúng chỗ sau mỗi bước, xem [bảng các bước](#các-bước-thu-một-hộ-ac-072)), `#maSaiThiTieuDiemVanOOMaPhong`, `DeDocTest#moiCapChuNenDatAa`, `#chuGoc16ChuLoiVaChuNutChinhDungMau`, `#oNhapNutDongBangDuCao`, `#oDangCoTieuDiemCoVienMauChinh`, `#javaFxDocHetMoiLuatCss`, `BoCucFxmlTest#khongCoChuBiCat` (mọi màn: chữ không cắt, ≥ 14 px, nút và ô đủ cao, nút cách ≥ 8 px), `#thanhMenuHienDuTenVaVaiTro` (1120, 1366, 1440 px), `#congNoNhanRongKhongGiuCho`, `NguoiDungThanhMenuTest#tenDaiRutGonKemTooltipDayDu`, `TrangChinhSoLieuTest#soLieuBinhThuongKhongChongChu` (Trang chính có số liệu ở 1440×1024 và 1120×645: nhãn tháng, số trên cột, tên trục không chồng nhau, không chữ bị cắt) | ◐ người chưa dùng thử thu một hộ kiểm tay (mục 5) |

#### Các bước thu một hộ (AC-072)

Bước là một thao tác chủ động của người dùng; gõ mã rồi Enter tính là 1.
`ThuMotHoNamBuocTest` tải `thu-phi.fxml` thật cùng controller, dữ liệu giả
(`ThuPhiGia`: hộ A0601 nợ 3 khoản), gõ phím vào ô đang có tiêu điểm và bấm
chuột vào nút như người dùng; hộp xác nhận và hộp "Đã thu tiền" là hộp
thoại thật.

| Bước | Thao tác | Kiểm sau bước | Đếm |
|---|---|---|---|
| Mở màn | – | Tiêu điểm ở ô mã phòng | 0 |
| 1 | Gõ "a0601", nhấn Enter | 3 khoản chọn sẵn, nút "Thu tất cả" bấm được; tiêu điểm ở "Tiền mặt" | 1 |
| 2 | Xem khoản | – | 0 |
| 3 | Bấm "Tiền mặt" | Đã chọn, tiêu điểm ở "Tiền mặt" | 1 |
| 4 | Bấm "Thu tất cả" | Hộp xác nhận mở, tiêu điểm ở "Đồng ý" (nút mặc định); chưa có phiếu | 1 |
| 5 | Bấm "Đồng ý" | Có 1 phiếu, tổng 1.283.500 đ, tiền mặt; hộp có "In biên lai" và "Để sau" | 1 |
| Sau (không tính) | Bấm "Để sau" | Tiêu điểm về ô mã phòng, "a0601" bôi đen | – |

Tổng 4 thao tác ≤ 5. Mã sai: thông báo đỏ, tiêu điểm vẫn ở ô mã phòng và mã
được bôi đen để gõ đè.

Khác ở Sprint 3, không gắn một AC riêng: chặn thu trùng và deadlock
(`PhieuThuIT#csdlChanThuTrungMotKhoanPhaiNop`, `#haiMayCungThuMotKhoanChiMotPhieuThanhCong`,
`DangBiKhoaExceptionTest`), hộ đã chuyển đi không ghi đóng góp
(`ThuPhiServiceTest#hoDaChuyenDiChiThuKhoanNoKhongGhiDongGop`,
`PhieuThuIT#hoDaChuyenDiKhongGhiDongGopTuNguyen`, giả định 39), dữ liệu mẫu
(`SeedIT`), thông báo cho lỗi không lường trước (`LoiKhongMongDoiTest`).

### Sprint 4

| AC | Test tự động | Kết quả |
|---|---|---|
| AC-068 ghi nhật ký người sửa, nội dung, thời điểm | `NhatKyIT#hoGiaDinhGhiThemSuaChuyenDiVaChiGhiTruongThucSuDoi`, `#nhanKhauGhiThemSuaChuyenDiDoiChuHoVaDotCuTru`, `#khoanThuCongNoVaPhieuThuGhiDungNguoiLam`, `#taiKhoanGhiTaoDoiVaiTroKhoaMoKhoaDatLaiKhongGhiMatKhau`, `#thaoTacXungDotHoacTrungKhoaKhongDeLaiDong`, `#ghiNhatKyLoiThiThaoTacCungHoanTac`, `#thoiDiemLaGioVietNam`, `ThaoTacTest#maKhopRangBuocCheckTrongSchema`, `NoiDungNhatKyTest#chiGhiTruongThucSuDoi` | ✓ |
| AC-069 xem theo thời gian, không sửa/xóa | `NhatKyServiceTest#thuQuyBiTuChoi`, `#locTheoKhoangNgayVaLoaiThaoTac`, `#tuNgaySauDenNgayBaoLoi`, `#thieuNgayBaoLoi`, `NhatKyIT#locTheoKhoangNgayVaLoaiThaoTacMoiNhatTruoc`, `#mysqlTuChoiSuaVaXoaNhatKy`, `#chayLaiSchemaKhongMatNhatKyVaVanChanSua`, `BoCucFxmlTest#khongCoChuBiCat` cho tab "Nhật ký thao tác" của `tai-khoan.fxml` | ◐ tab chỉ hiện với Quản trị viên kiểm tay |

| AC-046 tổng đã thu trong tháng ở Trang chính | `ThongKeServiceTest#tongQuanThangHienTaiCoTyLeKhiDaChot`, `#sauThangCoThangTrongBangKhongVaChiMotLanDoc`, `#caHaiVaiTroDeuXemDuocNguoiKhacBiTuChoi`, `ThongKeIT#phieuDaHuyKhongTinh`, `#ranhGioiThang`, `#noThangTruocDongThangNayTinhThangNay`, `#sauThangCoThangTrong`, `DongTyLeTest#daChotHienPhanTram`, `#chuaChotBaoRo`, `ThangTrucYTest#mauFigma`, `#moiGiaTriNamTrongThangMocTron`, `BieuDoThuPhiTest#coTenHaiTrucVaNhanThang`, `#trucYBatDauTuKhongTheoMocTronCoLuoiNgang`, `#cotThangHienTaiKhacKieu`, `#soLieuTrenDauCot` | ◐ Trang chính với dữ liệu thật xem bằng mắt |
| AC-047 tháng chưa có giao dịch thì 0 | `ThongKeServiceTest#thangKhongCoGiaoDichLaKhong`, `ThangTrucYTest#toanKhongVanCoThangMacDinh`, `BieuDoThuPhiTest#toanKhongVanVeTruc`, `ThongKeIT#phieuDaHuyKhongTinh` | ✓ |
| AC-048 lọc hộ nợ từ 3 tháng trở lên | `NoDongServiceTest#nguongBaThang`, `#tatCaHoConNoTuMotThang`, `#caHaiVaiTroDeuXemDuocChuaDangNhapBiTuChoi`, `NoDongIT#locNoTuBaThangDungThuTu`, `#nhieuKhoanTrongHaiThangChuaPhaiNoDong`, `#khoanDaThuKhongTinhPhieuHuyTinhLai`, `#khoanKhongDongVaTuNguyenKhongTinhTomTat`, `#hoDaChuyenDiVanCo`, `TheHoConNoTest#bamTheMoCongNoOTabHoNoDong`, `#dongPhuCuaThe`, `BoCucFxmlTest#khongCoChuBiCat` cho tab "Hộ nợ đọng" của `cong-no.fxml` | ◐ tab tự tải khi mở và khi đổi lựa chọn kiểm tay |
| AC-049 mã phòng, chủ hộ, số tháng nợ, tổng nợ | `NoDongIT#soThangTongNoVaChuHoDung`, `#hoDaChuyenDiVanCo`, `TheHoConNoTest#chuCuaBangNoDong`, `NoDongServiceTest#tomTatDemHoConNoVaHoNoDong` | ✓ |
| AC-014 xuất danh sách tạm vắng ra tệp | `TamVangExcelTest#duTruongNhuBanIn`, `BaoCaoNhanKhauServiceTest#xuatTamVangRaTepExcel`, `#xuatTamVangKiemQuyenVaDuLieu`, `DanhSachTamVangTest#nutXuatExcelCanhNutInVaVoHieuKhiChuaCoDanhSach`, `TepExcelTest#loiGiuaChungKhongDeLaiTepDich` | ◐ mở tệp bằng Excel thật kiểm tay |
| AC-050 xuất Excel theo mẫu sổ thu | `BaoCaoThuExcelTest#thuTuCotVaTieuDeDamCoDinh`, `#oTienLaSoVaPhieuNhieuThangCongMotCot`, `#oTrongKhacOKhong`, `#oNgayLaNgay`, `#dongTongCongCongTungCotTien`, `#tenSheetTieuDeVaNgayLap`, `BaoCaoThuServiceTest#xuatRaTepDocLaiDuoc`, `#caHaiVaiTroDuocXuatChuaDangNhapBiTuChoi`, `#loiGhiTepThanhThongBaoNghiepVu`, `BaoCaoThuIT#phieuTrongThangDungRanhGioiKhongCoPhieuHuy`, `#chuHoSoBienLaiVaCongCungKhoan`, `#xuatTepTuCsdl`, `TepExcelTest#ghiXongThiCoTepDichVaKhongConTepTam`, `#loiGiuaChungGiuNguyenTepCu`, `#tepDangMoBaoDongTep`, `BoCucFxmlTest#khongCoChuBiCat` cho `thong-ke.fxml` | ◐ mở tệp bằng Excel thật, hộp chọn tệp kiểm tay |
| AC-051 tháng không có dữ liệu | `BaoCaoThuServiceTest#thangKhongCoPhieuBiTuChoiTruocKhiTaoTep`, `BaoCaoThuIT#thangKhongCoPhieuBiTuChoi` | ✓ |
| AC-052 tổng thu một khoản theo đợt | `ThongKeServiceTest#khoanBatBuocTheoThangDaChot`, `#khoanBatBuocCanThangDaChot`, `#khoanTuNguyenKhongCanThang`, `#cacKhoanBatBuocTruoc`, `ThongKeIT#khoanBatBuocTheoThangChot`, `#tuNguyenDemDungHoDongVaHoGhiKhong`, `BoCucFxmlTest#khongCoChuBiCat` cho `thong-ke.fxml` | ◐ màn Thống kê xem bằng mắt |
| AC-053 khoản chưa có giao dịch thì 0 | `ThongKeIT#chuaCoGiaoDichLaKhong` | ✓ |
| AC-066 sao lưu theo lịch, ghi nhận thời điểm | `ScriptSaoLuuTest#tuyChonMysqldumpTheoGiaDinh43`, `#moiScriptCoBomUtf8DeWindowsPowerShellDocDungTiengViet`, `#khongScriptNaoNhanMatKhauQuaThamSo`, `SaoLuuIT#khoiPhucVaoCsdlMoiDuSoDongMoiBangVaTongTien` (tệp kết thúc bằng `-- Dump completed`, không có `CREATE DATABASE`/`USE`); `src/test/scripts/kiem-thu-sao-luu.ps1` (log, tệp dở, giữ N bản); CI chạy `sao-luu.ps1` thật, cả với mật khẩu sai | ◐ đăng ký lịch trên Windows và lần chạy đầu kiểm tay |
| AC-067 khôi phục thử đủ dữ liệu | `SaoLuuIT#khoiPhucVaoCsdlMoiDuSoDongMoiBangVaTongTien`, `#cotSinhVanDungVaVanLaCotSinhSauKhoiPhuc`, `#triggerNhatKyVanChanSuaXoaSauKhoiPhuc`; CI chạy `khoi-phuc-thu.ps1` trên tệp vừa sao lưu, kể cả trường hợp tổng tiền lệch | ✓ |

## 5. Việc còn kiểm tay

Làm trước mỗi lần demo hoặc phát hành, theo thứ tự:

| Việc | Cách kiểm | Liên quan |
|---|---|---|
| Dữ liệu mẫu cho demo | Dựng lại CSDL rồi xem Nhân khẩu và Thu phí theo kịch bản ở [setup.md](setup.md), mục 5 | `seed.sql` |
| In biên lai, thông báo phí, danh sách tạm vắng | Chọn "Microsoft Print to PDF" nếu không có máy in; xem khung "BẢN IN LẠI", số tiền bằng chữ, chữ ký, danh sách tạm vắng in khổ ngang | AC-039, AC-040, AC-012 |
| Hộp thoại máy in và xem trước | Xem bằng mắt, hộp thoại hệ điều hành không tự động được | US-013, US-015, US-004 |
| Số bước thu tiền cho một hộ | Một người chưa dùng ứng dụng (tốt nhất là người lớn tuổi) thử thu một hộ từ lúc mở màn Thu phí đến khi có phiếu, chỉ được nghe "thu phí căn A0601 bằng tiền mặt". Người kiểm đếm số thao tác, ghi chỗ người thử dừng lại, hỏi, hoặc bấm nhầm, ghi vào bảng dưới | AC-072 |
| Tốc độ tra cứu | Tra cứu một hộ, kết quả trong dưới 3 giây | AC-032 |
| Tổng thu và ngày thu trên màn hình | Ở màn Thu phí tra cứu A0601: nhãn "Tổng thu" bằng tổng cột "Số tiền" của các khoản đang chọn; bỏ chọn một khoản thì tổng giảm đúng số đó; nhập 50.000 vào một quỹ thì tổng tăng 50.000 đ. Thu xong, mở tab "Phiếu thu hôm nay": phiếu vừa lập có "Tổng tiền" bằng số đã thấy và "Thời điểm" là ngày giờ hôm nay | AC-035 |
| Khởi động ứng dụng thật | Với MySQL đang chạy và `config/db.properties` đúng: chạy `com.bluemoon.Launcher`, màn Đăng nhập hiện, đăng nhập `admin` được, Trang chính có số liệu. Tắt MySQL (hoặc đổi sai mật khẩu trong file) rồi mở lại ứng dụng và đăng nhập: báo "Không kết nối được cơ sở dữ liệu…" như [setup.md](setup.md) mục 6, không treo, không văng ra | AC-070 |
| Menu theo vai trò | Đăng nhập từng vai trò, đối chiếu menu với bảng phân quyền ở [architecture.md](design/architecture.md); Thủ quỹ không thấy tab "Quản lý tài khoản" và "Nhật ký thao tác" trong màn Tài khoản | AC-060, AC-061, AC-069 |
| Hộp thoại lỗi không lường trước | Khó tái hiện có chủ ý; phần chọn thông báo đã có `LoiKhongMongDoiTest`, hộp thoại hiện ra thì xem bằng mắt | `LoiKhongMongDoi` |
| Hai máy cùng dùng một CSDL | Mở hai bản ứng dụng, thao tác một hộ ở cả hai | AC-071 |
| Trang chính và màn Thống kê với dữ liệu thật | Đăng nhập bằng dữ liệu mẫu; xem thẻ "Đã thu tháng này", biểu đồ 6 tháng (cột tháng này đậm, số trên đầu cột), lập một phiếu rồi bấm logo thấy số đổi; mở Thống kê, xem một khoản bắt buộc và một quỹ | AC-046, AC-052 |
| Hộ nợ đọng | Bấm thẻ "Hộ còn nợ" ở Trang chính: mở Công nợ ở tab "Hộ nợ đọng", menu Công nợ đang chọn; đổi qua lại "Nợ từ 3 tháng trở lên" và "Tất cả hộ còn nợ", số hộ và dòng tổng khớp thẻ ở Trang chính | AC-048, AC-049 |
| Mở tệp Excel đã xuất | Xuất báo cáo thu một tháng có phiếu và danh sách tạm vắng, mở bằng Excel thật: chữ có dấu đúng, cột tiền cộng được (chọn cột thấy Sum), cột ngày lọc được theo ngày, dòng tiêu đề đứng yên khi cuộn; mở tệp trong Excel rồi xuất đè lên: báo "Tệp đang được mở trong chương trình khác…", tệp cũ còn nguyên; chọn tháng chưa có phiếu: báo ngay, không mở hộp chọn tệp | AC-014, AC-050, AC-051 |
| Lịch sao lưu trên Windows | Trên máy đặt MySQL: tạo login-path, chạy thử `sao-luu.ps1`, chạy `dang-ky-lich-sao-luu.ps1`, bấm Run trên tác vụ trong Task Scheduler; xem Last Run Result `0x0`, tab History, dòng `THÀNH CÔNG` mới trong `sao-luu.log`; hôm sau xem lần chạy 22:00 đầu tiên (hoặc lần chạy bù khi bật máy); rồi `khoi-phuc-thu.ps1` trên tệp đó ra `KẾT QUẢ: KHỚP` ([setup.md](setup.md), mục 7) | AC-066, AC-067 |
| Giao diện so với Figma | Mở từng màn ở 1120×645 và đối chiếu thiết kế ([ui.md](design/ui.md)); ảnh chụp trước/sau TS-004 ở [ts-004-giao-dien.md](review/ts-004-giao-dien.md) | NFR |

Kết quả thử thu một hộ với người chưa dùng (AC-072), ghi mỗi lần thử:

| Ngày | Người thử | Số thao tác | Chỗ lúng túng | Xử lý |
|---|---|---|---|---|
| (chưa thử) | | | | |

## 6. Viết test mới

- Test Service: dùng DAO giả cùng thư mục (`KhoGia` cho hộ, nhân khẩu, đợt
  tạm trú/tạm vắng; `KhoThuPhiGia`, `KhoPhieuThuGia` cho khoản thu và thu
  phí; `TaiKhoanServiceTest` có DAO giả riêng cho tài khoản); đặt tên test bằng
  câu tiếng Việt không dấu mô tả hành vi, ví dụ `khongHuyPhieuNgayTruoc`.
- Thời điểm: dùng `Clock.fixed` thay cho giờ máy, không dùng ngày thật.
- Test `*IT`: kế thừa cách bỏ qua của `CsdlThu`, dựng bảng từ `schema.sql`.
- Lỗi chỉ xảy ra khi hai máy xen nhau đúng lúc: tái hiện bằng một điểm chèn
  `protected` rỗng trong DAO (vd `CongNoDaoImpl.sauKhiDocHo`), test ghi đè
  để chạy thao tác của máy kia trên kết nối khác đúng thời điểm đó, thay vì
  nhiều luồng và `sleep` (xem `SuaSoTienDocKhoaIT`).
- So sánh tên có dấu trong dữ liệu: so trong Java hoặc bằng `HEX()`, không
  dựa vào `=` của MySQL, vì collation `utf8mb4_unicode_ci` không phân biệt
  hoa thường và dấu (giả định 40).
- Thêm màn hình hoặc FXML mới thì thêm tên tệp vào danh sách
  `@ValueSource` của `BoCucFxmlTest`.
