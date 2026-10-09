# BlueMoon Apartment Management System

> Hệ thống quản lý thu phí và thông tin cư dân cho chung cư BlueMoon.

## 1. Giới thiệu

**BlueMoon Apartment Management System** là phần mềm hỗ trợ Ban quản trị chung cư BlueMoon trong việc quản lý thông tin hộ gia đình, nhân khẩu và các khoản thu định kỳ.

Chung cư BlueMoon tọa lạc tại khu vực ngã tư Văn Phú, được khởi công xây dựng năm 2021 và hoàn thành vào năm 2023. Chung cư có diện tích xây dựng khoảng 450 m², gồm 30 tầng với các khu vực kiot, tầng đế, khu căn hộ và tầng penthouse.

Hiện tại, hoạt động quản lý cư dân và thu phí tại chung cư chủ yếu được thực hiện thủ công, kết hợp với Excel và các loại sổ sách giấy. Phương thức này gây ra nhiều hạn chế:

- Khó quản lý tập trung thông tin hộ gia đình và nhân khẩu.
- Dễ xảy ra sai sót khi tính và tổng hợp các khoản thu.
- Mất nhiều thời gian khi tra cứu công nợ của từng hộ.
- Khó theo dõi lịch sử giao dịch và người thực hiện thu phí.
- Việc thống kê, báo cáo phải thực hiện thủ công.
- Khó kiểm soát các khoản đóng góp tự nguyện theo từng đợt.

Dự án được xây dựng nhằm số hóa các quy trình trên, giúp Ban quản trị quản lý dữ liệu tập trung, giảm thao tác thủ công và nâng cao độ chính xác trong quá trình thu phí.

---

## 2. Bài toán

Hàng tháng, Ban quản trị cần lập danh sách các khoản phí phải đóng của từng hộ gia đình và thực hiện thu tiền.

Các khoản thu chính trong phiên bản **v1.0** gồm:

### 2.1. Phí dịch vụ chung cư

Đây là khoản phí bắt buộc được thu định kỳ hàng tháng để phục vụ các hoạt động như:

- Vệ sinh và bảo dưỡng khu vực chung.
- Thu gom rác thải.
- Chăm sóc cảnh quan, sân vườn.
- Đảm bảo an ninh.
- Bảo dưỡng các tiện ích chung.

Phí dịch vụ được tính dựa trên diện tích căn hộ và đơn giá:

```text
Phí dịch vụ = Diện tích căn hộ × Đơn giá
```

Đơn giá hiện tại nằm trong khoảng **2.500 – 16.500 đồng/m²/tháng**.

### 2.2. Phí quản lý chung cư

Đây là khoản phí bắt buộc hàng tháng nhằm phục vụ hoạt động quản lý và vận hành chung cư.

Đối với BlueMoon, mức phí quản lý từ khoảng **7.000 đồng/m²/tháng**.

Hệ thống cho phép quản lý đơn giá và tự động tính toán khoản phí dựa trên diện tích căn hộ.

### 2.3. Các khoản đóng góp tự nguyện

Ban quản trị có thể phối hợp với chính quyền địa phương hoặc tổ dân phố để tổ chức các đợt quyên góp như:

- Quỹ vì người nghèo.
- Quỹ biển đảo.
- Quỹ từ thiện.
- Các chương trình đóng góp khác.

Các khoản đóng góp:

- Được tạo theo từng đợt.
- Không bắt buộc.
- Không quy định số tiền cố định.
- Có thể ghi nhận số tiền đóng góp bằng 0 đồng.

---

## 3. Mục tiêu dự án

Hệ thống hướng tới các mục tiêu chính:

- Số hóa quy trình quản lý thu phí tại chung cư.
- Quản lý tập trung thông tin hộ gia đình và nhân khẩu.
- Tự động hóa việc tính toán các khoản phí.
- Quản lý công nợ của từng hộ.
- Hỗ trợ tra cứu và thu phí nhanh chóng.
- Lưu lại lịch sử giao dịch và người thực hiện.
- Hỗ trợ in biên lai và thông báo thu phí.
- Cung cấp các báo cáo và thống kê cơ bản.
- Hỗ trợ Ban quản trị cung cấp thông tin cư dân khi cơ quan chức năng yêu cầu.
- Giảm sai sót do quá trình quản lý thủ công bằng Excel và sổ sách.

---

## 4. Phạm vi hệ thống

### 4.1. Phiên bản v1.0

Phiên bản đầu tiên tập trung vào các nghiệp vụ cốt lõi.

#### Quản lý hộ gia đình và nhân khẩu

- Thêm, sửa thông tin hộ gia đình.
- Quản lý diện tích căn hộ.
- Quản lý thông tin nhân khẩu.
- Quản lý quan hệ giữa nhân khẩu và chủ hộ.
- Quản lý tạm trú, tạm vắng.
- Xuất danh sách nhân khẩu phục vụ báo cáo.

#### Quản lý các khoản thu

- Quản lý phí dịch vụ.
- Quản lý phí quản lý.
- Tạo các khoản thu tự nguyện.
- Ghi nhận số tiền đóng góp.
- Cho phép ghi nhận khoản đóng góp 0 đồng.
- Chốt công nợ hàng tháng.
- Cập nhật khoản thu trước khi thanh toán.

#### Thu phí

- Tra cứu khoản phí theo mã phòng.
- Hiển thị các khoản chưa thanh toán.
- Thu nhiều khoản trong một lần.
- Tự động tính tổng tiền.
- Chọn hình thức thanh toán: tiền mặt, chuyển khoản.
- Lưu thông tin người thực hiện giao dịch.
- In biên lai.
- Hủy phiếu thu nhầm trong ngày.
- In thông báo các khoản phí cần đóng.

#### Thống kê và báo cáo

- Xem tổng tiền đã thu trong tháng.
- Lọc các hộ có công nợ kéo dài.
- Thống kê theo từng khoản thu hoặc quỹ.
- Xuất báo cáo ra Excel.
- Hỗ trợ đối chiếu với quy trình quản lý cũ.

#### Quản lý tài khoản

- Đăng ký tài khoản.
- Đăng nhập.
- Phân quyền người dùng.
- Đổi mật khẩu.
- Cập nhật thông tin cá nhân.

### 4.2. Phiên bản v2.0

Các chức năng sau được định hướng phát triển trong phiên bản tiếp theo.

#### Quản lý phí gửi xe

Quản lý phương tiện đăng ký của từng hộ và tự động tính phí gửi xe.

| Loại phương tiện | Mức phí |
| ---------------- | ------- |
| Xe máy | 70.000 đồng/xe/tháng |
| Ô tô | 1.200.000 đồng/xe/tháng |

#### Thu hộ điện, nước, Internet

Hỗ trợ Ban quản trị thu hộ các khoản dịch vụ tiện ích dựa trên thông báo từ các nhà cung cấp.

Quy trình dự kiến:

```text
Nhận file thông báo
        ↓
Import dữ liệu
        ↓
Đối chiếu với hộ gia đình
        ↓
Tạo khoản phải thu
        ↓
Thu tiền
        ↓
Thống kê và báo cáo
```

---

## 5. Quy trình nghiệp vụ chính

Hệ thống tập trung vào 4 luồng nghiệp vụ chính:

```text
1. Đăng ký tài khoản
        ↓
2. Tạo khoản thu
        ↓
3. Thu phí
        ↓
4. Thống kê các khoản đóng góp
```

### Quy trình tổng quát

```text
┌────────────────────────────┐
│    Đăng nhập hệ thống      │
└─────────────┬──────────────┘
              ↓
┌────────────────────────────┐
│  Quản lý hộ & nhân khẩu    │
└─────────────┬──────────────┘
              ↓
┌────────────────────────────┐
│       Tạo khoản thu        │
└─────────────┬──────────────┘
              ↓
┌────────────────────────────┐
│        Chốt công nợ        │
└─────────────┬──────────────┘
              ↓
┌────────────────────────────┐
│          Thu phí           │
└─────────────┬──────────────┘
              ↓
┌────────────────────────────┐
│    Biên lai / Thông báo    │
└─────────────┬──────────────┘
              ↓
┌────────────────────────────┐
│    Thống kê & Báo cáo      │
└────────────────────────────┘
```

---

## 6. Đối tượng sử dụng

### 6.1. Ban quản trị

Có quyền quản lý các nghiệp vụ chính của hệ thống:

- Quản lý hộ gia đình.
- Quản lý nhân khẩu.
- Quản lý khoản thu.
- Theo dõi công nợ.
- Quản lý tài khoản.
- Xem thống kê và báo cáo.

### 6.2. Nhân viên thu phí / Thủ quỹ

Tập trung vào các nghiệp vụ:

- Tra cứu hộ gia đình.
- Tra cứu công nợ.
- Thu phí.
- Ghi nhận hình thức thanh toán.
- In biên lai.
- In thông báo.
- Xuất báo cáo thu phí.

---

## 7. Kiến trúc hệ thống

Phần mềm được định hướng phát triển dưới dạng Desktop Application.

```text
┌─────────────────────────────────┐
│         Java Desktop App        │
│                                 │
│  ┌──────────┐  ┌─────────────┐  │
│  │ UI Layer │  │  Business   │  │
│  │          │  │   Logic     │  │
│  └────┬─────┘  └──────┬──────┘  │
│       └───────┬───────┘         │
│               ↓                 │
│       Data Access Layer         │
└───────────────┬─────────────────┘
                │
                │ JDBC
                ↓
┌─────────────────────────────────┐
│           MySQL Server          │
│                                 │
│  - Hộ gia đình                  │
│  - Nhân khẩu                    │
│  - Khoản thu                    │
│  - Công nợ                      │
│  - Giao dịch                    │
│  - Tài khoản                    │
│  - Nhật ký hệ thống             │
└─────────────────────────────────┘
```

Dữ liệu được lưu trữ tập trung trên MySQL Server, giúp các máy tính trong hệ thống sử dụng chung một nguồn dữ liệu.

---

## 8. Công nghệ sử dụng

| Thành phần | Công nghệ |
| ---------- | --------- |
| Ngôn ngữ lập trình | Java |
| Loại ứng dụng | Desktop Application |
| Cơ sở dữ liệu | MySQL |
| Kết nối CSDL | JDBC |
| Quản lý mã nguồn | Git / GitHub |
| Mô hình phát triển | Agile / Scrum |
| Phân tích yêu cầu | Epic, Feature, User Story, Acceptance Criteria |
| Quản lý yêu cầu | Product Backlog, Traceability Matrix |

---

## 9. Yêu cầu hệ thống

### 9.1. Bảo mật

- Người dùng phải đăng nhập trước khi sử dụng hệ thống.
- Phân quyền theo vai trò.
- Hạn chế truy cập dữ liệu đối với người không có quyền.
- Cho phép thay đổi mật khẩu.
- Ghi nhận người thực hiện các giao dịch quan trọng.

### 9.2. Hiệu năng

Các thao tác tra cứu thông tin hộ gia đình và công nợ cần có thời gian phản hồi nhanh.

Mục tiêu: **thời gian phản hồi thao tác tra cứu < 3 giây**.

### 9.3. Sao lưu dữ liệu

Dữ liệu MySQL cần được sao lưu định kỳ nhằm hạn chế nguy cơ mất dữ liệu.

### 9.4. Truy vết

Các thao tác thay đổi dữ liệu quan trọng cần được ghi nhận:

```text
Người thực hiện
      +
Thao tác thay đổi
      +
Thời điểm
      +
Dữ liệu liên quan
```

---

## 10. Kết quả mong đợi

Sau khi hoàn thành phiên bản v1.0, hệ thống có thể hỗ trợ Ban quản trị:

- Quản lý tập trung dữ liệu cư dân.
- Tự động hóa việc tính phí.
- Quản lý công nợ theo từng hộ.
- Thu nhiều khoản phí trong một giao dịch.
- Giảm sai sót khi tính tổng tiền.
- Tra cứu thông tin nhanh chóng.
- Theo dõi lịch sử thu phí.
- In biên lai và thông báo.
- Xuất báo cáo.
- Thống kê tình hình thu phí.
- Hạn chế sự phụ thuộc vào sổ sách và Excel.

---

## 11. Định hướng phát triển

```text
                     BlueMoon
                        │
            ┌───────────┴───────────┐
            ↓                       ↓
          v1.0                    v2.0
            │                       │
    ┌───────┼────────┐       ┌──────┴──────┐
    ↓       ↓        ↓       ↓             ↓
  Cư dân  Thu phí  Báo cáo  Gửi xe   Điện/Nước/Internet
    │       │        │
    └───────┴────────┘
            │
            ↓
    Hệ thống quản lý
    chung cư tổng thể
```

### v1.0

- Quản lý hộ gia đình.
- Quản lý nhân khẩu.
- Quản lý khoản thu.
- Thu phí.
- Quản lý công nợ.
- Thống kê.
- Báo cáo.
- Quản lý tài khoản.

### v2.0

- Quản lý phí gửi xe.
- Thu hộ điện.
- Thu hộ nước.
- Thu hộ Internet.

Trong tương lai, hệ thống có thể được mở rộng thành nền tảng quản lý tổng thể các hoạt động vận hành và thu phí tại chung cư.

---

## 12. Trạng thái dự án

| Thông tin | Giá trị |
| --------- | ------- |
| Tên dự án | BlueMoon Apartment Management System |
| Phiên bản | v1.0 |
| Trạng thái | In Development |
| Loại ứng dụng | Desktop Application |
| Ngôn ngữ | Java |
| Database | MySQL |
| Phạm vi | Quản lý cư dân, thu phí, công nợ và báo cáo |

---

## 13. Tác giả

**BlueMoon Apartment Management System**

Dự án được thực hiện nhằm phục vụ mục đích học tập và nghiên cứu, đồng thời mô phỏng quá trình phân tích, thiết kế và xây dựng một hệ thống quản lý thu phí thực tế cho chung cư.
