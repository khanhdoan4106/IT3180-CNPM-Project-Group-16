# Kiểm thử

Tài liệu này mô tả cách nhóm kiểm thử BlueMoon: loại test nào kiểm cái gì,
chạy thế nào, tiêu chí chấp nhận (AC) nào đã có test tự động, và việc nào
còn phải kiểm tay. Quy tắc chung khi hoàn thành một story nằm ở
[Definition of Done](requirements/10-definition-of-done.md).

## 1. Chiến lược

| Loại | Lớp / thư mục | Kiểm cái gì | Cần gì để chạy |
|---|---|---|---|
| Test đơn vị (Service) | `src/test/java/com/bluemoon/service` | Quy tắc nghiệp vụ, kiểm tra dữ liệu, phân quyền (`PhanQuyen`), thông báo lỗi | Không cần MySQL: Service nhận DAO qua constructor, test dùng DAO giả trong bộ nhớ (`KhoGia`, `KhoThuPhiGia`, `KhoPhieuThuGia`) |
| Test đơn vị (tiện ích, DAO không cần CSDL) | `util`, `dao`, `config`, `controller` | Băm mật khẩu, đọc ngày, đổi mã lỗi MySQL thành lỗi nghiệp vụ (`DangBiKhoaException`, `TrungKhoaException`), đồng hồ `DongHoCsdl` với `Clock.fixed`, chọn thông báo lỗi (`LoiCsdl`, `LoiKhongMongDoi`), số tiền bằng chữ | Không |
| Test bố cục giao diện | `BoCucFxmlTest`, `MauInTest`, `HopThoaiHuyPhieuTest` | Nạp từng FXML ở cửa sổ 1120×645 bằng JavaFX chạy không màn hình (Monocle), kiểm không nút hay nhãn nào bị cắt thành "…", chữ gợi ý nằm trọn trong ô, mọi nút nằm trong vùng thấy được hoặc cuộn được, dùng đúng phông Be Vietnam Pro; số tiền lớn nhất vẫn vừa cột biên lai; hộp thoại hủy phiếu có đúng nút | Không cần màn hình, chạy được trên CI |
| Test hành vi giao diện | `PhimEnterTest`, `TrangChinhMenuTest`, `MauPhuTest` | Tải FXML thật trên cửa sổ ảo (Monocle), gửi sự kiện Enter vào từng ô và xem nút nào chạy; bấm logo về Trang chính, nút menu đang chọn có nền `#1d4ed8` do JavaFX vẽ; màu chữ phụ, tiêu đề cột và tương phản WCAG AA | Không cần màn hình, chạy được trên CI |
| Test tài liệu | `TruyVetTaiLieuTest`, `UiMdTest`, `TestingMdTest` | Mã RAW trích trong chú thích khớp bảng truy vết, `ui.md` giữ đủ quy tắc biểu đồ và bảng màu, mọi `Lớp#phương_thức` trong bảng AC → test ở đây còn tồn tại | Không |
| Kiểm thử tích hợp (`*IT`) | `src/test/java/com/bluemoon/it` | Phần chỉ MySQL thật mới chứng minh được: khóa dòng, deadlock, ràng buộc CSDL, giao dịch hoàn tác, giờ máy chủ, dữ liệu mẫu | MySQL 8, CSDL riêng có chữ `test` trong tên, profile `-Pmysql` |
| Kiểm tay | Theo bảng ở mục 5 | In thật qua máy in, hộp thoại máy in, nhìn bằng mắt, hai máy cùng dùng | Máy có giao diện |

Nguyên tắc: viết test cho Service trước khi sửa hay refactor; lỗi chỉ xảy ra
khi hai máy cùng ghi (trùng khóa, deadlock) thì có thêm một test `*IT` để
chứng minh trên MySQL thật, vì DAO giả không tái hiện được.

### Các lớp kiểm thử tích hợp

| Lớp | Nội dung |
|---|---|
| `CongNoIT` | Chốt công nợ, đơn giá và số tiền điền sẵn đọc từ CSDL, số quá lớn bị chặn trước khi tới MySQL |
| `PhieuThuIT` | Thu tất cả rồi hủy khôi phục công nợ, hai máy cùng thu một khoản chỉ một phiếu thành công, sửa và thu song song không deadlock, đếm lần in, thông báo phí, danh sách tạm vắng, hộ đã chuyển đi không ghi đóng góp |
| `GiaoDichHoGiaDinhIT` | Lỗi giữa giao dịch thì không ghi hộ lẫn lịch sử; đổi chủ hộ từ chối người vừa chuyển đi ở máy khác |
| `DongHoCsdlIT` | Giờ đọc từ MySQL gần giờ máy, múi giờ Việt Nam |
| `MuiGioIT` | JVM đặt UTC, New York, Kiritimati: phiếu lập 23:30 ngày 31/12 vẫn là 23:30 cùng ngày (không sang năm sau), giờ hủy phiếu, giờ biến động và giờ sửa khoản do CSDL điền đọc lại đúng giờ Việt Nam; phiên MySQL luôn ở `+07:00` dù máy chủ đặt UTC (chạy thử bằng `mysqld --default-time-zone=+00:00`, như máy chủ trên CI) |
| `SeedIT` | Dựng CSDL từ `schema.sql`, chạy `seed.sql` ba lần: số dòng không đổi, tên có dấu (so trong Java, không dựa `=` của MySQL), hai quỹ mẫu đang mở, ba trường hợp tạm trú/tạm vắng |

Mỗi lớp `*IT` bỏ qua (không lỗi) nếu chưa đặt biến môi trường hoặc tên CSDL
không có chữ `test`, để không xóa nhầm CSDL `bluemoon` đang dùng.

## 2. Cách chạy

### Test đơn vị (không cần MySQL)

| Cách | Lệnh / thao tác |
|---|---|
| IntelliJ | Maven → bluemoon → Lifecycle → `test` |
| Dòng lệnh | `mvn test` hoặc `mvn verify` (cần Maven 3.9+ trong PATH) |

Kết quả mong đợi: `BUILD SUCCESS`, 269 test. `mvn verify` không có
`-Pmysql` không chạy lớp `*IT`.

Chạy riêng test bố cục sau khi sửa FXML hoặc CSS:

```
mvn test -Dtest=BoCucFxmlTest
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

Kết quả mong đợi: 269 test đơn vị và 30 kiểm thử tích hợp đều đạt (MySQL
8.0). Hai test cấu hình (`DatabaseConfigTest`) tự bỏ qua khi đã đặt
`BLUEMOON_DB_*`, nên log hiện "Skipped: 2" ở lần chạy này.

### Trên GitHub

Workflow [`ci.yml`](../.github/workflows/ci.yml) chạy cho mỗi Pull Request
và mỗi lần đẩy vào `main`, gồm hai job; nhóm chỉ merge Pull Request khi cả
hai xanh:

| Job | Làm gì |
|---|---|
| `build` | JDK 21, `mvn -B verify`: test đơn vị và test bố cục |
| `build-mysql` | Service `mysql:8.0`, CSDL `bluemoon_test`, `mvn -B verify -Pmysql`: thêm 30 kiểm thử tích hợp |

## 3. Số test hiện tại

| Mốc | Test đơn vị | Kiểm thử tích hợp |
|---|---|---|
| `v0.1.0` | 97 | – |
| `v0.2.0` | 169 | có (`mvn verify -Pmysql`) |
| `v0.3.0` (Sprint 3 và các sửa #28 – #35) | 269 | 30 |

## 4. Ánh xạ AC → test tự động

Mỗi AC đã làm đều có ít nhất một test; cột "Kiểm tay" ghi phần không tự động
được. Tên test viết tắt dạng `Lớp#hàm`; lớp `*ServiceTest` nằm ở
`src/test/java/com/bluemoon/service`, `*IT` ở `.../it`.

Ký hiệu: ✓ có test tự động đủ; ◐ có test nhưng một phần còn kiểm tay;
Sprint 4 là chưa làm.

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
| AC-026 khoản đã thu thì từ chối sửa | `CongNoServiceTest#tuChoiSuaKhoanDaCoPhieuThu`, `ThuPhiServiceTest#khoanDaThuKhongSuaDuocChoDenKhiHuyPhieu`, `PhieuThuIT#nhieuLuongVuaSuaVuaThuCungMotKhoanKhongDeadlock` | ✓ |
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
| AC-072 tối đa 5 bước, chữ rõ | `BoCucFxmlTest#khongCoChuBiCat` cho `thu-phi.fxml` (chữ không bị cắt) | ◐ số bước thao tác kiểm tay: lập phiếu 4–5 thao tác |

Khác ở Sprint 3, không gắn một AC riêng: chặn thu trùng và deadlock
(`PhieuThuIT#csdlChanThuTrungMotKhoanPhaiNop`, `#haiMayCungThuMotKhoanChiMotPhieuThanhCong`,
`DangBiKhoaExceptionTest`), hộ đã chuyển đi không ghi đóng góp
(`ThuPhiServiceTest#hoDaChuyenDiChiThuKhoanNoKhongGhiDongGop`,
`PhieuThuIT#hoDaChuyenDiKhongGhiDongGopTuNguyen`, giả định 39), dữ liệu mẫu
(`SeedIT`), thông báo cho lỗi không lường trước (`LoiKhongMongDoiTest`).

### Chưa làm (Sprint 4)

AC-014 (xuất tệp danh sách tạm vắng), AC-046 – AC-053 (thống kê, báo cáo
Excel), AC-066, AC-067 (sao lưu), AC-068, AC-069 (nhật ký thao tác). Test sẽ
thêm cùng story; bảng này cập nhật khi Sprint 4 hoàn thành.

## 5. Việc còn kiểm tay

Làm trước mỗi lần demo hoặc phát hành, theo thứ tự:

| Việc | Cách kiểm | Liên quan |
|---|---|---|
| Dữ liệu mẫu cho demo | Dựng lại CSDL rồi xem Nhân khẩu và Thu phí theo kịch bản ở [setup.md](setup.md), mục 5 | `seed.sql` |
| In biên lai, thông báo phí, danh sách tạm vắng | Chọn "Microsoft Print to PDF" nếu không có máy in; xem khung "BẢN IN LẠI", số tiền bằng chữ, chữ ký, danh sách tạm vắng in khổ ngang | AC-039, AC-040, AC-012 |
| Hộp thoại máy in và xem trước | Xem bằng mắt, hộp thoại hệ điều hành không tự động được | US-013, US-015, US-004 |
| Số bước thu tiền cho một hộ | Một người chưa dùng thử thao tác từ nhập mã phòng đến phiếu thu, đếm bước | AC-072 |
| Tốc độ tra cứu | Tra cứu một hộ, kết quả trong dưới 3 giây | AC-032 |
| Menu theo vai trò | Đăng nhập từng vai trò, đối chiếu menu với bảng phân quyền ở [architecture.md](design/architecture.md) | AC-060, AC-061 |
| Hộp thoại lỗi không lường trước | Khó tái hiện có chủ ý; phần chọn thông báo đã có `LoiKhongMongDoiTest`, hộp thoại hiện ra thì xem bằng mắt | `LoiKhongMongDoi` |
| Hai máy cùng dùng một CSDL | Mở hai bản ứng dụng, thao tác một hộ ở cả hai | AC-071 |
| Giao diện so với Figma | Mở từng màn ở 1120×645 và đối chiếu thiết kế ([ui.md](design/ui.md)) | NFR |

## 6. Viết test mới

- Test Service: dùng DAO giả cùng thư mục (`KhoGia` cho hộ, nhân khẩu, đợt
  tạm trú/tạm vắng; `KhoThuPhiGia`, `KhoPhieuThuGia` cho khoản thu và thu
  phí; `TaiKhoanServiceTest` có DAO giả riêng cho tài khoản); đặt tên test bằng
  câu tiếng Việt không dấu mô tả hành vi, ví dụ `khongHuyPhieuNgayTruoc`.
- Thời điểm: dùng `Clock.fixed` thay cho giờ máy, không dùng ngày thật.
- Test `*IT`: kế thừa cách bỏ qua của `CsdlThu`, dựng bảng từ `schema.sql`.
- So sánh tên có dấu trong dữ liệu: so trong Java hoặc bằng `HEX()`, không
  dựa vào `=` của MySQL, vì collation `utf8mb4_unicode_ci` không phân biệt
  hoa thường và dấu (giả định 40).
- Thêm màn hình hoặc FXML mới thì thêm tên tệp vào danh sách
  `@ValueSource` của `BoCucFxmlTest`.
