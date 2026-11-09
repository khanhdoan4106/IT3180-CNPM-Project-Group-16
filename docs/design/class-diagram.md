# Sơ đồ lớp

Mô tả các lớp trong `src/main/java/com/bluemoon` sau Sprint 3. Kiến trúc
phân tầng và bảng chức năng ↔ mã nguồn xem [architecture.md](architecture.md).

## Phân tầng và phụ thuộc

```mermaid
flowchart LR
    subgraph View["View (FXML + CSS)"]
        V1[dang-nhap.fxml]
        V2[trang-chinh.fxml]
        V3[ho-gia-dinh.fxml]
        V4[nhan-khau.fxml]
        V5[tai-khoan.fxml]
        V6[khoan-thu.fxml]
        V7[cong-no.fxml]
        V8[thu-phi.fxml]
    end
    subgraph Controller
        C1[DangNhapController]
        C2[TrangChinhController]
        C3[HoGiaDinhController]
        C4[NhanKhauController]
        C5[TaiKhoanController]
        C6[KhoanThuController]
        C7[CongNoController]
        C8[ThuPhiController]
        IN[MauIn + InAn]
    end
    subgraph Service
        S1[TaiKhoanService]
        S2[HoGiaDinhService]
        S3[NhanKhauService]
        S4[DotCuTruService]
        S5[KhoanPhiService]
        S6[CongNoService]
        S7[BienDongService]
        S8[ThuPhiService]
        S9[ThongBaoPhiService]
        S10[BaoCaoNhanKhauService]
        PQ[PhanQuyen]
    end
    subgraph DAO
        D1[TaiKhoanDao]
        D2[HoGiaDinhDao]
        D3[NhanKhauDao]
        D4[DotCuTruDao]
        D5[KhoanPhiDao]
        D6[CongNoDao]
        D7[BienDongDao]
        D8[PhieuThuDao]
        D9[BaoCaoNhanKhauDao]
    end
    DB[(MySQL bluemoon)]

    V1 --> C1 --> S1 --> D1
    V2 --> C2
    C2 --> S1
    C2 --> S6
    C2 --> S7
    V3 --> C3 --> S2 --> D2
    C3 --> S6
    C3 --> S7 --> D7
    V4 --> C4 --> S3 --> D3
    C4 --> S2
    C4 --> S4 --> D4
    V5 --> C5 --> S1
    V6 --> C6 --> S5 --> D5
    V7 --> C7 --> S6 --> D6
    S6 --> D5
    S6 --> D2
    S2 --> D3
    S3 --> D2
    S4 --> D2
    S4 --> D3
    V8 --> C8 --> S8 --> D8
    S8 --> D5
    C7 --> S9 --> D8
    S9 --> D6
    C4 --> S10 --> D9
    C7 & C8 & C4 -.-> IN
    S1 & S2 & S3 & S4 & S5 & S6 & S7 & S8 & S9 & S10 -.-> PQ
    D1 & D2 & D3 & D4 & D5 & D6 & D7 & D8 & D9 --> DB
```

DAO hộ gia đình, nhân khẩu, đợt tạm trú/tạm vắng ghi `bien_dong_nhan_khau`
trong cùng giao dịch (qua `BienDongDaoImpl.ghi`), nên `BienDongDao` chỉ có
hàm đọc.

## Lớp nghiệp vụ chính

Mọi hàm của Service nhận `TaiKhoan nguoi` (người đang đăng nhập) làm tham số
đầu và kiểm tra quyền bằng `PhanQuyen.yeuCau`; sơ đồ lược bỏ tham số này.
Ngoại lệ: `dangNhap`, `lamMoiPhien`, `thangHienTai` (xem
[architecture.md](architecture.md#phân-quyền)).

```mermaid
classDiagram
    direction LR

    class TaiKhoanService {
        +dangNhap(tenDangNhap, matKhau) TaiKhoan
        +dangKy(tenDangNhap, hoTen, matKhau, nhapLai, vaiTro) TaiKhoan
        +doiMatKhau(matKhauCu, matKhauMoi, nhapLai) void
        +capNhatThongTin(hoTen, soDienThoai) TaiKhoan
        +danhSach() List~TaiKhoan~
        +doiVaiTro(id, vaiTro) TaiKhoan
        +datHoatDong(id, hoatDong) TaiKhoan
        +datLaiMatKhau(id, matKhauMoi, nhapLai) void
        +lamMoiPhien(tk) TaiKhoan
    }
    class HoGiaDinhService {
        +them(ThongTinHo, ThongTinNhanKhau chuHo, ThongTinDot) HoGiaDinh
        +capNhat(id, ThongTinHo) HoGiaDinh
        +danhDauChuyenDi(id) void
        +timKiem(tuKhoa, gomDaChuyenDi) List~TomTatHo~
    }
    class NhanKhauService {
        +them(hoId, ThongTinNhanKhau, ThongTinDot) NhanKhau
        +capNhat(id, ThongTinNhanKhau) NhanKhau
        +danhDauChuyenDi(id) void
        +doiChuHo(hoId, chuHoMoiId, quanHeCu) void
        +theoHo(hoId) List~NhanKhau~
    }
    class DotCuTruService {
        +ghiNhan(nhanKhauId, ThongTinDot) DotCuTru
        +theoNhanKhau(nhanKhauId) List~DotCuTru~
        +tinhTrangTheoHo(hoId) Map
    }
    class KhoanPhiService {
        +danhSach() List~KhoanPhi~
        +suaDonGia(id, donGia) KhoanPhi
        +taoKhoanTuNguyen(ten, tuNgay, denNgay, soTien) KhoanPhi
    }
    class CongNoService {
        +xemTruoc(thang) KetQuaTinhCongNo
        +chot(thang) KetQuaChot
        +theoThang(thang) List~PhaiNop~
        +suaSoTien(id, soTien, ghiChu) PhaiNop
        +tongChuaDong(hoId) BigDecimal
        +canNhacChot() boolean
    }
    class BienDongService {
        +theoHo(hoId) List~BienDong~
        +ganDay() List~BienDong~
    }
    class ThuPhiService {
        +traCuu(maPhong) List~TomTatHo~
        +thongTin(hoId) ThongTinThu
        +thu(hoId, phaiNopIds, dongGop, hinhThuc) PhieuThu
        +huy(id, lyDo) PhieuThu
        +choIn(id) PhieuThu
        +daIn(id) void
        +homNay() List~PhieuThu~
        +lichSu(hoId) List~PhieuThu~
    }
    class ThongBaoPhiService {
        +motHo(thang, hoId) ThongBaoPhi
        +tatCa(thang) List~ThongBaoPhi~
    }
    class BaoCaoNhanKhauService {
        +tamVang(ngay) List~TamVang~
    }

    TaiKhoanService --> TaiKhoanDao
    HoGiaDinhService --> HoGiaDinhDao
    HoGiaDinhService --> NhanKhauDao
    NhanKhauService --> NhanKhauDao
    NhanKhauService --> HoGiaDinhDao
    DotCuTruService --> DotCuTruDao
    DotCuTruService --> NhanKhauDao
    DotCuTruService --> HoGiaDinhDao
    KhoanPhiService --> KhoanPhiDao
    CongNoService --> CongNoDao
    CongNoService --> KhoanPhiDao
    CongNoService --> HoGiaDinhDao
    BienDongService --> BienDongDao
    ThuPhiService --> PhieuThuDao
    ThuPhiService --> KhoanPhiDao
    ThongBaoPhiService --> PhieuThuDao
    ThongBaoPhiService --> CongNoDao
    BaoCaoNhanKhauService --> BaoCaoNhanKhauDao

    class TaiKhoan
    class HoGiaDinh
    class NhanKhau
    class DotCuTru
    class KhoanPhi
    class PhaiNop
    class BienDong
    class PhieuThu
    class ChiTietPhieuThu
    HoGiaDinh "1" o-- "1..*" NhanKhau
    NhanKhau "1" o-- "0..*" DotCuTru
    HoGiaDinh "1" o-- "0..*" PhaiNop
    KhoanPhi "1" o-- "0..*" PhaiNop
    HoGiaDinh "1" o-- "0..*" BienDong
    HoGiaDinh "1" o-- "0..*" PhieuThu
    PhieuThu "1" *-- "1..*" ChiTietPhieuThu
    ChiTietPhieuThu "0..*" --> "0..1" PhaiNop
    PhieuThu --> HinhThucThanhToan
    TaiKhoan --> VaiTro
    KhoanPhi --> LoaiKhoanPhi
    KhoanPhi --> CachTinhPhi
    KhoanPhi --> TrangThaiKhoanPhi
    BienDong --> LoaiBienDong
```

Mỗi `XxxDao` là interface, có `XxxDaoImpl` cài bằng JDBC; test dùng DAO giả
trong bộ nhớ (`KhoGia`, `KhoThuPhiGia`, `KhoPhieuThuGia`); phần cần MySQL thật
(khóa dòng, deadlock, giao dịch) có thêm các lớp `*IT` ([testing.md](../testing.md)).

## Ghi chú thiết kế

- Service nhận DAO qua constructor nên test dùng DAO giả trong bộ nhớ, không
  cần MySQL (xem `src/test/java/com/bluemoon/service`).
- `ThongTinHo`, `ThongTinNhanKhau`, `ThongTinDot` là dữ liệu nhập từ form,
  tách khỏi model để Service kiểm tra hợp lệ trước khi tạo model.
- `KiemTraNhanKhau` gom các quy tắc kiểm tra nhân khẩu dùng chung; `LoiTrung`
  đổi lỗi trùng khóa của CSDL (khi hai máy cùng lưu) thành thông báo nghiệp
  vụ; `SoTien` đọc và hiển thị số tiền kiểu Việt Nam.
- `PhanQuyen` kiểm tra vai trò ở Service (lớp thứ hai sau việc ẩn menu).
- Lớp tiện ích của tầng Controller (không phải màn hình): `HopThoai` (nút
  hộp thoại tiếng Việt: "Đồng ý", "Hủy", "Đóng", và cặp nút chính + "Đóng"
  cho hộp thoại hủy phiếu), `LoiCsdl` (phân biệt mất kết nối với lỗi dữ liệu
  để chọn thông báo), `LoiKhongMongDoi` (bộ xử lý lỗi toàn cục, `App` gọi
  khi khởi động, hiện hộp thoại thay vì chỉ in console),
  `TomTatPhieuHomNay` (số phiếu, tiền đã thu, số phiếu hủy của dải tóm tắt
  tab Phiếu thu hôm nay).
- `NghiepVuException`: lỗi nghiệp vụ hiển thị cho người dùng;
  `DaoException` (và lớp con `TrungKhoaException`): lỗi truy cập CSDL;
  `CauHinhException`: lỗi đọc `config/db.properties`.
- `PhienLamViec` giữ tài khoản đang đăng nhập; `DieuHuong` chuyển màn hình;
  `BangLichSu` dựng bảng lịch sử biến động dùng chung cho hai màn hình.
- `MauIn` dựng trang in (biên lai, thông báo phí, danh sách tạm vắng);
  `InAn` xem trước rồi in bằng `PrinterJob`, dùng chung cho ba chức năng.
- `XungDotException` (con của `DaoException`): DAO kiểm tra lại dưới khóa
  dòng và phát hiện dữ liệu vừa đổi ở máy khác (khoản vừa được thu, số tiền
  vừa sửa, hết số biên lai); Service đổi thành thông báo nghiệp vụ.
- `DangBiKhoaException` (con của `DaoException`): MySQL huỷ giao dịch vì
  deadlock (1213) hoặc chờ khóa quá hạn (1205); Service hiện "Có người khác
  đang thao tác trên hộ này, vui lòng thử lại".
- `DongHoCsdl` (extends `Clock`): giờ máy chủ MySQL (`SELECT NOW()`), dùng
  cho `ThuPhiService`, `CongNoService` khi chạy thật; test dùng
  `Clock.fixed` (giả định 38).
- `ThongTinThu` (record lồng trong `ThuPhiService`): hộ, khoản chưa đóng và
  các quỹ đang mở kèm tình trạng đóng góp của hộ (`DongGopQuy`).
