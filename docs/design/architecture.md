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
| Lọc hộ nợ, xuất Excel | Có | Có | US-017, US-018 |
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
| Khoản thu | US-005, US-006 | `KhoanThuController`, `khoan-thu.fxml` | `KhoanPhiService` | `KhoanPhiDao` | `KhoanPhiServiceTest` |
| Chốt, sửa công nợ | US-009, US-008 | `CongNoController`, `cong-no.fxml` | `CongNoService` | `CongNoDao` | `CongNoServiceTest`, `CongNoIT` |
| Tra cứu, thu phí, đóng góp, biên lai, hủy phiếu | US-007, US-010 – US-014 | `ThuPhiController`, `thu-phi.fxml`, `TomTatPhieuHomNay`, `HopThoai`, `MauIn`, `InAn` | `ThuPhiService` | `PhieuThuDao`, `KhoanPhiDao` | `ThuPhiServiceTest`, `SoTienTest`, `MauInTest`, `TomTatPhieuHomNayTest`, `HopThoaiHuyPhieuTest`, `DangBiKhoaExceptionTest`, `DongHoCsdlTest`, `PhieuThuIT`, `DongHoCsdlIT` |
| Thông báo phí hằng tháng | US-015 | `CongNoController` (nút "In thông báo phí"), `MauIn` | `ThongBaoPhiService` | `PhieuThuDao`, `CongNoDao` | `ThongBaoPhiServiceTest`, `MauInTest`, `PhieuThuIT` |
| Danh sách nhân khẩu tạm vắng | US-004 | `NhanKhauController`, `DanhSachTamVang`, `MauIn` | `BaoCaoNhanKhauService` | `BaoCaoNhanKhauDao` | `BaoCaoNhanKhauServiceTest`, `MauInTest`, `PhieuThuIT` |
| Báo lỗi không lường trước | – | `App`, `LoiKhongMongDoi`, `LoiCsdl` | – | – | `LoiKhongMongDoiTest`, `LoiCsdlTest` |
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

## Giao diện

Bố cục, màn hình, phương án màu và đặc tả: [ui.md](ui.md). Màu dùng biến
`-mau-*` trong `css/giao-dien.css`; đổi phương án màu chỉ sửa các biến này.
