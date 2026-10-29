# BlueMoon – Phần mềm quản lý chung cư

Ứng dụng desktop giúp Ban quản trị chung cư BlueMoon quản lý hộ gia đình,
nhân khẩu và thu phí, thay cho sổ sách và Excel.

Bài tập lớn IT3180 Nhập môn Công nghệ phần mềm – Nhóm 16, Đại học Bách khoa
Hà Nội.

## Trạng thái

| Mốc | Nội dung | Trạng thái |
|---|---|---|
| Sprint 1 – [`v0.1.0`](../../releases/tag/v0.1.0) | Đăng nhập, đăng ký tài khoản, hộ gia đình, nhân khẩu, tạm trú/tạm vắng | Hoàn thành |
| Sprint 2 – `v0.2.0` | Khoản thu, chốt công nợ, phân quyền, đổi mật khẩu | Chưa bắt đầu |
| Sprint 3 – `v0.3.0` | Ghi nhận đóng góp, thu phí, biên lai, hủy phiếu, thông báo, báo cáo nhân khẩu | Chưa bắt đầu |
| Sprint 4 – `v1.0.0` | Thống kê, xuất Excel, sao lưu, nhật ký thao tác, rà soát giao diện | Chưa bắt đầu |

Chi tiết từng story: [Product Backlog](docs/requirements/08-product-backlog.md).

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

Chạy test: IntelliJ → Maven → Lifecycle → `test`. CI chạy `mvn verify`
cho mỗi Pull Request ([.github/workflows/ci.yml](.github/workflows/ci.yml)).

## Cấu trúc thư mục

```text
src/main/java/com/bluemoon/   model, dao, service, controller, util, config
src/main/resources/           FXML và CSS
src/test/java/                test JUnit
database/                     schema.sql, seed.sql
config/                       mẫu cấu hình kết nối CSDL
docs/requirements/            yêu cầu: user story, backlog, traceability
docs/design/                  kiến trúc, ERD, sơ đồ lớp, use case
docs/meeting-notes/           biên bản họp nhóm
```

## Quy trình làm việc

Issue → nhánh `feature/US-xxx-ten` → commit `feat(US-xxx): ...` → Pull
Request → thành viên khác review → merge vào `main` → cuối Sprint tạo
Release. Chi tiết: [CONTRIBUTING.md](CONTRIBUTING.md).

## Tài liệu

| Nhóm | Tài liệu |
|---|---|
| Yêu cầu | [Wish list](docs/requirements/01-wish-list.md), [As-is](docs/requirements/02-as-is-scenario.md), [Visionary](docs/requirements/03-visionary-scenario.md), [NFR](docs/requirements/04-nfr.md), [Raw log](docs/requirements/05-raw-log.md), [Epic/Feature](docs/requirements/06-epic-feature-list.md), [User Story](docs/requirements/07-user-stories.md), [Backlog](docs/requirements/08-product-backlog.md), [Traceability](docs/requirements/09-traceability-matrix.md), [Definition of Done](docs/requirements/10-definition-of-done.md), [Giả định](docs/requirements/11-chot-va-gia-dinh.md) |
| Thiết kế | [Kiến trúc](docs/design/architecture.md), [ERD](docs/design/erd.md), [Sơ đồ lớp](docs/design/class-diagram.md), [Use case](docs/design/use-case.md), [Quy ước mã](docs/design/coding-convention.md) |
| Vận hành | [Cài đặt](docs/setup.md), [Đóng góp](CONTRIBUTING.md) |

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
