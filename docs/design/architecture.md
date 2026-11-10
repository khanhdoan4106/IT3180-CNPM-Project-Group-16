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
- Mỗi chức năng: `KhoanThu` (model), `KhoanThuDao`/`KhoanThuDaoImpl`,
  `KhoanThuService`, `KhoanThuController`, `khoan-thu.fxml`.
- Service nhận DAO qua constructor để test bằng DAO giả (xem
  `TaiKhoanServiceTest`).
- Luôn viết test cho service trước khi sửa/refactor.
- SQL luôn dùng `PreparedStatement`, không nối chuỗi từ dữ liệu người dùng.
- Không xóa cứng dữ liệu nghiệp vụ; đổi trạng thái để giữ lịch sử.

## Phân quyền

Kiểm tra ở hai lớp (NFR-002, NFR-009):

1. **Controller** ẩn mục menu và nút mà vai trò không được dùng.
2. **Service** kiểm tra lại vai trò của người đang đăng nhập trước mỗi thao
   tác, ném `NghiepVuException` nếu không có quyền. Ẩn nút chưa đủ: lớp này
   chặn cả khi màn hình mở bằng đường khác (AC-062).

Mọi hàm public của Service nhận `TaiKhoan nguoi` và gọi
`PhanQuyen.yeuCau`, trừ ba hàm: `TaiKhoanService.dangNhap` và
`TaiKhoanService.lamMoiPhien` (chạy khi chưa có hoặc đang kiểm tra lại
phiên), `CongNoService.thangHienTai` (chỉ đọc đồng hồ, không đọc dữ liệu nghiệp vụ).

| Chức năng | Quản trị viên | Thủ quỹ | Căn cứ |
|---|---|---|---|
| Hộ gia đình, nhân khẩu, tạm trú/tạm vắng, báo cáo nhân khẩu | Có | Không | AC-060, AC-062 |
| Xem danh mục khoản thu | Có | Có | US-005 |
| Sửa đơn giá, tạo khoản thu tự nguyện | Có | Không | AC-016, US-006 |
| Chốt công nợ, sửa khoản thu của hộ | Có | Có | US-009, US-008 |
| Tra cứu phí, thu phí, đóng góp tự nguyện, biên lai | Có | Có | AC-060, US-007, US-010 – US-013 |
| Hủy phiếu thu | Mọi phiếu, trong ngày | Phiếu do mình lập, trong ngày | US-014, giả định 28 |
| In thông báo phí | Có | Có | US-015 |
| Thống kê, xuất Excel | Có | Có | US-016 – US-019 |
| Đăng ký và quản lý tài khoản | Có | Không | US-020, AC-061 |
| Tài khoản của tôi | Có | Có | US-023 |

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
| Lịch sử biến động nhân khẩu | US-027 | `HoGiaDinhController`, `TrangChinhController`, `BangLichSu` | `BienDongService` | `BienDongDao` (ghi trong giao dịch của DAO hộ, nhân khẩu, đợt) | `BienDongServiceTest`, `HoGiaDinhServiceTest`, `NhanKhauServiceTest`, `DotCuTruServiceTest`, `GiaoDichHoGiaDinhIT` |
| Khung ứng dụng, menu theo vai trò | US-022 | `TrangChinhController`, `trang-chinh.fxml` | `PhanQuyen` | – | – |
| Khoản thu | US-005, US-006 | `KhoanThuController`, `khoan-thu.fxml` | `KhoanPhiService` | `KhoanPhiDao` | `KhoanPhiServiceTest` |
| Chốt, sửa công nợ | US-009, US-008 | `CongNoController`, `cong-no.fxml` | `CongNoService` | `CongNoDao` | `CongNoServiceTest`, `CongNoIT` |
| Tra cứu, thu phí, đóng góp, biên lai, hủy phiếu | US-007, US-010 – US-014 | `ThuPhiController`, `thu-phi.fxml`, `TomTatPhieuHomNay`, `HopThoai`, `MauIn`, `InAn` | `ThuPhiService` | `PhieuThuDao`, `KhoanPhiDao` | `ThuPhiServiceTest`, `SoTienTest`, `MauInTest`, `TomTatPhieuHomNayTest`, `HopThoaiHuyPhieuTest`, `DangBiKhoaExceptionTest`, `DongHoCsdlTest`, `PhieuThuIT`, `DongHoCsdlIT` |
| Thông báo phí hằng tháng | US-015 | `CongNoController` (nút "In thông báo phí"), `MauIn` | `ThongBaoPhiService` | `PhieuThuDao`, `CongNoDao` | `ThongBaoPhiServiceTest`, `MauInTest`, `PhieuThuIT` |
| Danh sách nhân khẩu tạm vắng | US-004 | `NhanKhauController`, `DanhSachTamVang`, `MauIn` | `BaoCaoNhanKhauService` | `BaoCaoNhanKhauDao` | `BaoCaoNhanKhauServiceTest`, `MauInTest`, `PhieuThuIT` |
| Báo lỗi không lường trước | – | `App`, `LoiKhongMongDoi`, `LoiCsdl` | – | – | `LoiKhongMongDoiTest`, `LoiCsdlTest` |
| Dữ liệu mẫu | US-001 – US-003, US-005, US-006, US-027 | – | – | `database/seed.sql` | `SeedIT` |
| Bố cục các màn hình | NFR-008 | 8 tệp FXML, `giao-dien.css` | – | – | `BoCucFxmlTest` |

Thêm chức năng mới thì thêm một dòng vào bảng này trong cùng Pull Request.
Chiến lược kiểm thử và bảng AC → test: [../testing.md](../testing.md).

## Kết nối CSDL

`Database` đọc `config/db.properties` rồi biến môi trường
`BLUEMOON_DB_URL`, `BLUEMOON_DB_USER`, `BLUEMOON_DB_PASSWORD` (biến môi
trường ghi đè file) **một lần** ở lần dùng đầu, giữ trong bộ nhớ; mỗi
`getConnection()` chỉ mở một kết nối mới. Đổi cấu hình thì khởi động lại ứng
dụng. Đọc lỗi thì không lưu, lần sau thử đọc lại. Test đặt lại cấu hình bằng
`Database.datLaiChoTest`.

### Những điểm chưa làm

| Điểm | Lý do chưa làm |
|---|---|
| Pool kết nối, chạy tác vụ CSDL ở luồng nền, rút thời gian chờ khóa | Chưa có dữ liệu và tải nhiều người dùng để đo. Xem xét lại khi có số liệu thật. |
| Controller tạo DAO trực tiếp | Nghiệp vụ nằm ở Service và đã được test bằng DAO giả. |
| Bộ đếm biên lai khóa dòng | Cố ý để số biên lai liên tục (giả định 31). |
| Phân trang | Quy mô vài trăm hộ. Thống kê Sprint 4 phải tổng hợp bằng SQL, không tải hết dữ liệu lên bộ nhớ. |
| Phiên làm việc | Mỗi máy một người dùng, đã đọc lại tài khoản trước mỗi màn hình. Tự đăng xuất khi không thao tác để sau. |

## Giao diện

Bố cục, màn hình, phương án màu và đặc tả: [ui.md](ui.md). Màu dùng biến
`-mau-*` trong `css/giao-dien.css`; đổi phương án màu chỉ sửa các biến này.
