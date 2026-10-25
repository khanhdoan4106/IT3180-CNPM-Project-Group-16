# Sơ đồ lớp

Mô tả các lớp đã có trong `src/main/java/com/bluemoon` sau Sprint 1. Kiến
trúc phân tầng xem [architecture.md](architecture.md).

## Phân tầng và phụ thuộc

```mermaid
flowchart LR
    subgraph View["View (FXML + CSS)"]
        V1[dang-nhap.fxml]
        V2[trang-chinh.fxml]
        V3[ho-gia-dinh.fxml]
        V4[nhan-khau.fxml]
        V5[tai-khoan.fxml]
    end
    subgraph Controller
        C1[DangNhapController]
        C2[TrangChinhController]
        C3[HoGiaDinhController]
        C4[NhanKhauController]
        C5[TaiKhoanController]
    end
    subgraph Service
        S1[TaiKhoanService]
        S2[HoGiaDinhService]
        S3[NhanKhauService]
        S4[DotCuTruService]
    end
    subgraph DAO
        D1[TaiKhoanDao]
        D2[HoGiaDinhDao]
        D3[NhanKhauDao]
        D4[DotCuTruDao]
    end
    DB[(MySQL bluemoon)]

    V1 --> C1 --> S1 --> D1
    V2 --> C2
    V3 --> C3 --> S2 --> D2
    V4 --> C4 --> S3 --> D3
    C4 --> S2
    C4 --> S4 --> D4
    V5 --> C5 --> S1
    S2 --> D3
    S3 --> D2
    S4 --> D2
    S4 --> D3
    D1 & D2 & D3 & D4 --> DB
```

## Lớp nghiệp vụ chính

```mermaid
classDiagram
    direction LR

    class TaiKhoanService {
        +dangNhap(tenDangNhap, matKhau) TaiKhoan
        +dangKy(nguoiTao, tenDangNhap, ...) TaiKhoan
        +doiMatKhau(tk, matKhauCu, ...) void
    }
    class HoGiaDinhService {
        +them(ThongTinHo, ThongTinNhanKhau chuHo) HoGiaDinh
        +capNhat(id, ThongTinHo) HoGiaDinh
        +danhDauChuyenDi(id) void
        +timKiem(tuKhoa, gomDaChuyenDi) List~TomTatHo~
    }
    class NhanKhauService {
        +them(hoId, ThongTinNhanKhau) NhanKhau
        +capNhat(id, ThongTinNhanKhau) NhanKhau
        +danhDauChuyenDi(id) void
        +theoHo(hoId) List~NhanKhau~
    }
    class DotCuTruService {
        +ghiNhan(nhanKhauId, ThongTinDot) DotCuTru
        +theoNhanKhau(nhanKhauId) List~DotCuTru~
        +tinhTrangTheoHo(hoId) Map
    }

    class TaiKhoanDao {<<interface>>}
    class HoGiaDinhDao {<<interface>>}
    class NhanKhauDao {<<interface>>}
    class DotCuTruDao {<<interface>>}
    class TaiKhoanDaoImpl
    class HoGiaDinhDaoImpl
    class NhanKhauDaoImpl
    class DotCuTruDaoImpl

    TaiKhoanDao <|.. TaiKhoanDaoImpl
    HoGiaDinhDao <|.. HoGiaDinhDaoImpl
    NhanKhauDao <|.. NhanKhauDaoImpl
    DotCuTruDao <|.. DotCuTruDaoImpl

    TaiKhoanService --> TaiKhoanDao
    HoGiaDinhService --> HoGiaDinhDao
    HoGiaDinhService --> NhanKhauDao
    NhanKhauService --> NhanKhauDao
    NhanKhauService --> HoGiaDinhDao
    DotCuTruService --> DotCuTruDao
    DotCuTruService --> NhanKhauDao
    DotCuTruService --> HoGiaDinhDao

    class TaiKhoan
    class HoGiaDinh
    class NhanKhau
    class DotCuTru
    HoGiaDinh "1" o-- "1..*" NhanKhau
    NhanKhau "1" o-- "0..*" DotCuTru
    TaiKhoan --> VaiTro
    HoGiaDinh --> LoaiCanHo
    HoGiaDinh --> TrangThaiHo
    NhanKhau --> QuanHeChuHo
    NhanKhau --> LoaiCuTru
    NhanKhau --> GioiTinh
    DotCuTru --> LoaiDotCuTru
```

## Ghi chú thiết kế

- Service nhận DAO qua constructor nên test dùng DAO giả trong bộ nhớ, không
  cần MySQL (xem `src/test/java/com/bluemoon/service`).
- `ThongTinHo`, `ThongTinNhanKhau`, `ThongTinDot` là dữ liệu nhập từ form,
  tách khỏi model để Service kiểm tra hợp lệ trước khi tạo model.
- `KiemTraNhanKhau` gom các quy tắc kiểm tra nhân khẩu dùng chung cho
  `HoGiaDinhService`, `NhanKhauService` và `DotCuTruService`; `LoiTrung` đổi
  lỗi trùng khóa của CSDL (khi hai máy cùng lưu) thành thông báo nghiệp vụ.
- `NghiepVuException`: lỗi nghiệp vụ hiển thị cho người dùng;
  `DaoException` (và lớp con `TrungKhoaException`): lỗi truy cập CSDL;
  `CauHinhException`: lỗi đọc `config/db.properties`.
- `PhienLamViec` giữ tài khoản đang đăng nhập; `DieuHuong` chuyển màn hình.
