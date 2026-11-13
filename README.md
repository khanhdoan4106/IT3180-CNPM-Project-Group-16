# BlueMoon – Phần mềm quản lý chung cư

Ứng dụng desktop giúp Ban quản trị chung cư BlueMoon quản lý hộ gia đình,
nhân khẩu và thu phí, thay cho sổ sách và Excel.

Bài tập lớn IT3180 Nhập môn Công nghệ phần mềm – Nhóm 16, Đại học Bách khoa
Hà Nội.

## Bối cảnh

Chung cư BlueMoon (ngã tư Văn Phú, hoàn thành năm 2023) có 30 tầng gồm
kiot, tầng đế, căn hộ và penthouse, do Ban quản trị (BQT) do cư dân bầu ra
điều hành. Hiện BQT quản lý bằng sổ tay và Excel: tính phí bằng tay, thông
báo thu bằng giấy, sổ thu không có số biên lai, khó tổng hợp báo cáo nhân
khẩu khi công an phường yêu cầu.

Các khoản thu chính:

| Khoản thu | Cách tính |
|---|---|
| Phí dịch vụ (vệ sinh, rác, an ninh, cảnh quan) | Diện tích × đơn giá 2.500 – 16.500 đ/m², bắt buộc hằng tháng |
| Phí quản lý chung cư | Diện tích × 7.000 đ/m² (mặc định), bắt buộc hằng tháng |
| Đóng góp tự nguyện (quỹ vì người nghèo, quỹ biển đảo…) | Theo đợt, không bắt buộc, đóng bao nhiêu ghi bấy nhiêu (kể cả 0 đ) |

Người dùng: **Quản trị viên** (BQT – quản lý cư dân, khoản thu, tài khoản,
báo cáo) và **Thủ quỹ** (tra cứu, thu phí, in biên lai). Cư dân không dùng
trực tiếp phần mềm.

## Trạng thái

| Mốc | Nội dung | Trạng thái |
|---|---|---|
| Sprint 1 – [`v0.1.0`](../../releases/tag/v0.1.0) | Đăng nhập, đăng ký tài khoản, hộ gia đình, nhân khẩu, tạm trú/tạm vắng | Hoàn thành |
| Sprint 2 – [`v0.2.0`](../../releases/tag/v0.2.0) | Khoản thu, chốt và sửa công nợ, phân quyền hai lớp, quản lý tài khoản, đổi mật khẩu, đổi chủ hộ, lịch sử biến động nhân khẩu | Hoàn thành (AC-026 của US-008 làm cùng phiếu thu ở Sprint 3) |
| Sprint 3 – [`v0.3.0`](../../releases/tag/v0.3.0) | Ghi nhận đóng góp, thu phí, biên lai, hủy phiếu, thông báo, báo cáo nhân khẩu | Hoàn thành (xuất tệp danh sách tạm vắng, AC-014, làm cùng Sprint 4) |
| Sprint 4 – `v1.0.0` | Thống kê, xuất Excel, sao lưu, nhật ký thao tác, rà soát giao diện | Chưa bắt đầu |

Chi tiết từng story: [Product Backlog](docs/requirements/08-product-backlog.md).
Thay đổi theo từng bản phát hành: [CHANGELOG.md](CHANGELOG.md).

## Phạm vi v1.0

- **Hộ gia đình:** hộ, nhân khẩu, quan hệ với chủ hộ, tạm trú/tạm vắng,
  báo cáo nhân khẩu cho công an phường.
- **Thu phí:** phí dịch vụ và phí quản lý tính theo diện tích, khoản đóng
  góp tự nguyện theo đợt, chốt công nợ hằng tháng, thu nhiều khoản một lần,
  biên lai, hủy phiếu thu nhầm.
- **Người dùng:** tài khoản do Quản trị viên cấp, đăng nhập, phân quyền
  Quản trị viên / Thủ quỹ, đổi mật khẩu.
- **Báo cáo:** tổng thu tháng, nợ đọng, thống kê theo khoản thu, xuất Excel.

Ngoài phạm vi (v2.0): phí gửi xe, thu hộ điện/nước/internet.

## Công nghệ và kiến trúc

Java 21, JavaFX 21 (FXML, CSS), MySQL 8 qua JDBC, Maven, JUnit 5, BCrypt.

```
View (FXML) → Controller → Service → DAO → MySQL
```

Mỗi tầng chỉ gọi tầng ngay dưới; Service chứa quy tắc nghiệp vụ và được test
bằng DAO giả, không cần MySQL. Chi tiết: [docs/design](docs/design).

## Chạy thử

1. Cài JDK 21, MySQL 8, IntelliJ IDEA.
2. Chạy `database/schema.sql` rồi `database/seed.sql`, tạo user MySQL cho
   ứng dụng.
3. Sao chép `config/db.properties.example` thành `config/db.properties`,
   điền user và mật khẩu MySQL.
4. Chạy lớp `com.bluemoon.Launcher`. Tài khoản mẫu: `admin` / `Admin@123`.

Hướng dẫn từng bước và lỗi thường gặp: [docs/setup.md](docs/setup.md).

Sao lưu CSDL hằng ngày (TS-001): trên máy đặt MySQL, tạo login-path
`bluemoon-sao-luu` bằng `mysql_config_editor`, rồi chạy
`powershell -ExecutionPolicy Bypass -File scripts\dang-ky-lich-sao-luu.ps1`
để Task Scheduler chạy `scripts/sao-luu.ps1` lúc 22:00 mỗi ngày. Khôi phục
thử bằng `scripts/khoi-phuc-thu.ps1`. Chi tiết:
[docs/setup.md, mục 7](docs/setup.md#7-sao-lưu-và-khôi-phục).

Chạy test: IntelliJ → Maven → Lifecycle → `test` (hoặc `mvn verify`), không
cần MySQL. Kiểm thử tích hợp trên MySQL thật: `mvn verify -Pmysql`. CI có hai
job cho mỗi Pull Request: `build` (`mvn verify`, kiểm thử script sao lưu) và
`build-mysql` (MySQL 8.0, `mvn verify -Pmysql`, chạy thật script sao lưu và
khôi phục thử) ([.github/workflows/ci.yml](.github/workflows/ci.yml)).
Chiến lược kiểm thử và bảng AC → test: [docs/testing.md](docs/testing.md).

## Cấu trúc thư mục

```text
.
├── src/main/java/com/bluemoon/
│   ├── Launcher.java, App.java   điểm khởi động ứng dụng JavaFX
│   ├── config/                   đọc config/db.properties, mở kết nối MySQL
│   ├── model/                    lớp dữ liệu và enum (HoGiaDinh, NhanKhau, TaiKhoan…)
│   ├── dao/                      truy cập MySQL bằng PreparedStatement
│   ├── service/                  quy tắc nghiệp vụ, kiểm tra dữ liệu
│   ├── controller/               xử lý sự kiện của từng màn hình
│   └── util/                     điều hướng màn hình, băm mật khẩu, định dạng ngày
├── src/main/resources/com/bluemoon/
│   ├── view/                     FXML: dang-nhap, trang-chinh, ho-gia-dinh, nhan-khau, tai-khoan, khoan-thu, cong-no, thu-phi
│   └── css/giao-dien.css         màu và kiểu chữ dùng chung
├── src/test/java/com/bluemoon/   JUnit 5; service test dùng DAO giả (KhoGia), *IT chạy trên MySQL thật (-Pmysql)
├── scripts/                      PowerShell: sao lưu (sao-luu.ps1), đăng ký lịch, khôi phục thử
├── database/
│   ├── schema.sql                tạo CSDL và bảng
│   └── seed.sql                  dữ liệu mẫu, tài khoản admin
├── config/db.properties.example  mẫu cấu hình kết nối (bản thật không commit)
├── docs/
│   ├── requirements/             01–11: wish list → user story → backlog → traceability
│   ├── design/                   kiến trúc, ERD, sơ đồ lớp, use case, giao diện, quy ước mã
│   ├── meeting-notes/            biên bản họp
│   ├── review/                   báo cáo rà soát cuối Sprint và rà soát tổng thể
│   ├── testing.md                kiểm thử: cách chạy, bảng AC → test, việc kiểm tay
│   ├── setup.md                  cài đặt và chạy
│   ├── thuat-ngu.md              thuật ngữ dùng thống nhất
│   └── workflow.md               quy trình làm việc của nhóm
├── .github/                      CI (workflows/ci.yml, hai job), mẫu Issue và Pull Request
├── CHANGELOG.md                  thay đổi theo từng bản phát hành
└── pom.xml                       Maven: JavaFX, MySQL Connector, BCrypt, JUnit
```

## Quy trình làm việc

Issue → nhánh `feature/US-xxx-ten` → commit `feat(US-xxx): ...` → Pull
Request → thành viên khác review → merge vào `main` → cuối Sprint ghi
`CHANGELOG.md` và tạo Release. Chi tiết: [docs/workflow.md](docs/workflow.md).

## Tài liệu

| Nhóm | Tài liệu |
|---|---|
| Yêu cầu | [Wish list](docs/requirements/01-wish-list.md), [As-is](docs/requirements/02-as-is-scenario.md), [Visionary](docs/requirements/03-visionary-scenario.md), [NFR](docs/requirements/04-nfr.md), [Raw log](docs/requirements/05-raw-log.md), [Epic/Feature](docs/requirements/06-epic-feature-list.md), [User Story](docs/requirements/07-user-stories.md), [Backlog](docs/requirements/08-product-backlog.md), [Traceability](docs/requirements/09-traceability-matrix.md), [Definition of Done](docs/requirements/10-definition-of-done.md), [Giả định](docs/requirements/11-chot-va-gia-dinh.md), [Thuật ngữ](docs/thuat-ngu.md) |
| Thiết kế | [Kiến trúc](docs/design/architecture.md), [ERD](docs/design/erd.md), [Sơ đồ lớp](docs/design/class-diagram.md), [Use case](docs/design/use-case.md), [Giao diện](docs/design/ui.md), [Quy ước mã](docs/design/coding-convention.md); mục lục: [docs/design](docs/design) |
| Vận hành | [Cài đặt](docs/setup.md), [Quy trình làm việc](docs/workflow.md), [Kiểm thử](docs/testing.md), [Nhật ký thay đổi](CHANGELOG.md) |
| Rà soát | [Rà soát Sprint 2](docs/review/sprint-2-ra-soat.md), [Rà soát Sprint 3](docs/review/sprint-3-ra-soat.md), [Rà soát tổng thể](docs/review/ra-soat-tong-the.md), [Đọc có khóa trong giao dịch](docs/review/doc-co-khoa-trong-giao-dich.md) |
| Họp nhóm | [Biên bản họp](docs/meeting-notes) |

## Thành viên

| Mã | Họ tên | MSSV | Vai trò |
|---|---|---|---|
| TV1 | Đoàn Quốc Khánh | 202416243 | Quản lý dự án / Scrum Master |
| TV2 | Nguyễn Đình Dũng | 202416173 | Phân tích yêu cầu, thiết kế giao diện |
| TV3 | Đinh Phi Hùng | 202416215 | Lập trình CSDL và nghiệp vụ |
| TV4 | Cao Thành Đạt | 202416152 | Lập trình giao diện JavaFX |
| TV5 | Vũ Nhật Huy | 202416229 | Kiểm thử và tài liệu |

Mã TV1–TV5 dùng thống nhất trong Project Charter, Timeline và kế hoạch quản
lý rủi ro.
