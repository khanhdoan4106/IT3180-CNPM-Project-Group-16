# Quản lý chung cư BlueMoon v1

> Tài liệu khai phá yêu cầu (Requirement Elicitation) cho hệ thống quản lý thu phí và thông tin chung cư.

## 1. Giới thiệu

**Quản lý chung cư v1** là bài tập phân tích và khai phá yêu cầu cho một hệ thống hỗ trợ quản lý hoạt động chung cư.

Hệ thống hướng đến việc số hóa các quy trình hiện đang được thực hiện bằng Excel và sổ sách giấy, tập trung vào:

- Quản lý hộ gia đình và nhân khẩu
- Quản lý các khoản thu
- Ghi nhận thanh toán và quản lý công nợ
- Tra cứu và thống kê
- Quản lý tài khoản và phân quyền
- Báo cáo nhân khẩu
- Quản lý các khoản đóng góp tự nguyện

Tài liệu được xây dựng theo quy trình:

```text
Yêu cầu thô
    ↓
As-Is Scenario
    ↓
Visionary Scenario
    ↓
Epic & Feature
    ↓
User Story
    ↓
Acceptance Criteria
    ↓
Product Backlog
    ↓
Traceability Matrix
```

---

## 2. Mục tiêu

Dự án tập trung vào các mục tiêu chính:

1. Thu thập và hệ thống hóa các yêu cầu từ các bên liên quan.
2. Phân tích quy trình hiện tại và xác định các vấn đề tồn tại.
3. Mô tả trạng thái mong muốn của hệ thống.
4. Phân loại yêu cầu chức năng (FR) và phi chức năng (NFR).
5. Chuẩn hóa yêu cầu thành Epic, Feature và User Story.
6. Xây dựng Acceptance Criteria theo mô hình Given – When – Then.
7. Ưu tiên yêu cầu bằng phương pháp MoSCoW.
8. Xây dựng Product Backlog và kế hoạch Sprint dự kiến.
9. Thiết lập khả năng truy vết từ yêu cầu thô đến tiêu chí nghiệm thu.

---

## 3. Các bên liên quan

| Bên liên quan | Vai trò / nhu cầu |
|---|---|
| Ban quản trị | Quản lý hộ gia đình, nhân khẩu, các khoản thu, báo cáo và tài khoản |
| Thủ quỹ | Tra cứu hộ gia đình, ghi nhận thanh toán và quản lý công nợ |
| Cư dân | Tra cứu các khoản phí đã đóng và còn nợ |
| Chính quyền địa phương | Nhận báo cáo nhân khẩu, tạm trú, tạm vắng |
| Tổ dân phố | Phối hợp trong các khoản đóng góp |

---

# 4. Client's Wish List

Danh sách yêu cầu thô gồm **33 mục**, được mã hóa từ `RAW-001` đến `RAW-033`.

Các nguồn chính:

- Ban quản trị
- Thủ quỹ
- Cư dân
- Chính quyền địa phương
- Đề bài
- Ảnh sổ thu

### Các vấn đề nổi bật

- Thu phí thủ công bằng Excel và sổ sách.
- Mất thời gian khi tìm kiếm thông tin hộ gia đình.
- Dễ xảy ra sai sót khi nhập và tổng hợp dữ liệu.
- Khó đối chiếu công nợ.
- Sổ thu không có lịch sử thay đổi.
- Khó tổng hợp các khoản đóng góp.
- Thông báo bằng giấy dễ bị bỏ sót.

Chi tiết toàn bộ `RAW-001` đến `RAW-033` được lưu trong tài liệu và `Raw_Log.xlsx`.

---

# 5. As-Is Scenario

## Quy trình hiện tại

| Bước | Người thực hiện | Công cụ | Đầu ra | Pain Point |
|---|---|---|---|---|
| Lập danh sách thu phí | Ban quản trị | Excel, sổ giấy | Danh sách khoản thu | Mất thời gian, dễ sai |
| Gửi thông báo | Ban quản trị | Giấy in, bảng tin | Thông báo giấy | Dễ bị bỏ sót |
| Thu phí | Thủ quỹ | Sổ, biên lai giấy | Biên lai, ghi chép | Khó tra cứu, dễ nhầm |
| Ghi nhận đóng góp | BQT, Tổ dân phố | Sổ giấy | Thông tin từng quỹ | Khó tổng hợp |
| Kiểm tra công nợ | Thủ quỹ | Sổ sách | Thông tin công nợ | Khó đối chiếu |
| Quản lý nhân khẩu | Ban quản trị | Excel | Danh sách nhân khẩu | Thiếu lịch sử thay đổi |
| Báo cáo chính quyền | Ban quản trị | Excel, báo cáo giấy | Báo cáo nhân khẩu | Tổng hợp thủ công |

---

# 6. Visionary Scenario

Trong trạng thái mong muốn, hệ thống hỗ trợ các quy trình:

1. Đăng nhập và xác thực người dùng.
2. Quản lý hộ gia đình và nhân khẩu.
3. Quản lý biến động nhân khẩu, tạm trú và tạm vắng.
4. Quản lý danh mục các khoản thu.
5. Tự động tạo khoản thu hàng tháng.
6. Ghi nhận thanh toán và in biên lai.
7. Theo dõi công nợ.
8. Tra cứu và thống kê.
9. Quản lý thông tin cá nhân.
10. Xuất báo cáo nhân khẩu.
11. Mở rộng quản lý phí gửi xe ở phiên bản 2.0.
12. Mở rộng thu hộ điện, nước, internet ở phiên bản 2.0.

---

# 7. Non-Functional Requirements

| ID | Yêu cầu | Loại |
|---|---|---|
| NFR-001 | Xác thực người dùng trước khi truy cập | Bảo mật |
| NFR-002 | Phân quyền giữa Quản trị viên và Nhân viên thu phí | Bảo mật |
| NFR-003 | Sao lưu dữ liệu định kỳ | Sao lưu |
| NFR-004 | Ghi audit log các thay đổi dữ liệu quan trọng | Truy vết |
| NFR-005 | Thời gian phản hồi truy vấn dưới 3 giây | Hiệu năng |
| NFR-006 | Tuân thủ quy định pháp lý về báo cáo nhân khẩu | Pháp lý |
| NFR-007 | Ứng dụng desktop Java, dữ liệu lưu trên MySQL Server | Kỹ thuật |
| NFR-008 | Giao diện thân thiện, dễ sử dụng cho người lớn tuổi | Khả dụng |

---

# 8. Epic & Feature

## 8.1. Epic

| ID | Epic | Mô tả |
|---|---|---|
| EP-01 | Quản lý hộ gia đình & nhân khẩu | Quản lý hộ, nhân khẩu và biến động nhân khẩu |
| EP-02 | Quản lý danh mục khoản thu | Quản lý các loại phí và quỹ đóng góp |
| EP-03 | Thu phí | Tạo khoản thu, thanh toán, biên lai và công nợ |
| EP-04 | Tra cứu & Thống kê | Tìm kiếm, thống kê và báo cáo |
| EP-05 | Đăng nhập & Quản lý tài khoản | Đăng ký, đăng nhập, đổi mật khẩu và phân quyền |
| EP-06 | Phí gửi xe (v2.0) | Quản lý phí xe máy và ô tô |
| EP-07 | Thu hộ điện, nước, internet (v2.0) | Thu hộ các dịch vụ tiện ích |

## 8.2. Feature

| ID | Feature | Epic |
|---|---|---|
| FE-01 | Quản lý thông tin hộ gia đình | EP-01 |
| FE-02 | Quản lý nhân khẩu trong hộ | EP-01 |
| FE-03 | Quản lý biến động nhân khẩu | EP-01 |
| FE-04 | Báo cáo nhân khẩu | EP-01 |
| FE-05 | Quản lý danh mục phí | EP-02 |
| FE-06 | Tạo khoản thu hàng tháng | EP-03 |
| FE-07 | Ghi nhận thanh toán | EP-03 |
| FE-08 | Quản lý công nợ | EP-03 |
| FE-09 | Tra cứu thông tin | EP-04 |
| FE-10 | Thống kê doanh thu | EP-04 |
| FE-11 | Đăng nhập hệ thống | EP-05 |
| FE-12 | Đổi mật khẩu | EP-05 |
| FE-13 | Quản lý thông tin cá nhân | EP-05 |
| FE-14 | Đăng ký tài khoản | EP-05 |
| FE-15 | Thông báo thu phí | EP-03 |
| FE-16 | Quản lý quỹ đóng góp tự nguyện | EP-02 |
| FE-17 | Phân quyền người dùng | EP-05 |
| FE-18 | Quản lý phí gửi xe (v2.0) | EP-06 |
| FE-19 | Thu hộ điện, nước, internet (v2.0) | EP-07 |

---

# 9. User Stories

Tài liệu gồm **18 User Story** và **2 Technical Story**.

| ID | Nội dung | Ưu tiên |
|---|---|---|
| US-01 | Thêm mới hộ gia đình | Must |
| US-02 | Thêm mới nhân khẩu | Must |
| US-03 | Ghi nhận biến động nhân khẩu | Must |
| US-04 | Tạo danh mục các khoản thu | Must |
| US-05 | Tự động tạo khoản thu hàng tháng | Must |
| US-06 | Ghi nhận thanh toán và in biên lai | Must |
| US-07 | Tra cứu thông tin hộ gia đình | Must |
| US-08 | Xem thống kê doanh thu theo tháng | Should |
| US-09 | Đăng nhập hệ thống | Must |
| US-10 | Đổi mật khẩu | Should |
| US-11 | Tra cứu các khoản phí đã đóng và còn nợ | Could |
| US-12 | Xuất báo cáo nhân khẩu | Should |
| US-13 | Đăng ký tài khoản | Must |
| US-14 | In thông báo các khoản phí cần đóng | Should |
| US-15 | Ghi nhận hộ đóng hoặc không đóng từng quỹ | Must |
| US-16 | Phân quyền Quản trị viên và Nhân viên thu phí | Should |
| US-17 | Quản lý phương tiện và phí gửi xe (v2.0) | Won't |
| US-18 | Thu hộ điện, nước, internet (v2.0) | Won't |
| TS-01 | Sao lưu dữ liệu MySQL định kỳ | Should |
| TS-02 | Ghi audit log các thao tác thay đổi quan trọng | Should |

---

# 10. Acceptance Criteria

Acceptance Criteria được xây dựng theo dạng:

```text
Given → When → Then
```

### Ví dụ: US-01 – Thêm hộ gia đình

**AC1**

- **Given:** Ban quản trị đã đăng nhập.
- **When:** Chọn "Thêm hộ gia đình" và nhập đầy đủ thông tin.
- **Then:** Hệ thống lưu hộ gia đình mới và hiển thị trong danh sách.

**AC2**

- **Given:** Căn hộ đã có hộ gia đình.
- **When:** Thêm hộ trùng căn hộ.
- **Then:** Hệ thống báo trùng và không lưu.

Các User Story trong phạm vi v1.0 được xây dựng Acceptance Criteria tương ứng trong tài liệu gốc.

`US-17` và `US-18` thuộc phạm vi v2.0 nên chưa xây dựng Acceptance Criteria chi tiết.

---

# 11. Product Backlog

Product Backlog được ưu tiên theo phương pháp **MoSCoW**:

| Mức | Ý nghĩa |
|---|---|
| Must | Bắt buộc |
| Should | Nên có |
| Could | Có thể có |
| Won't | Không thực hiện trong phiên bản hiện tại |

## Kế hoạch Sprint dự kiến

| Sprint | Nội dung |
|---|---|
| Sprint 1 | Hộ gia đình, nhân khẩu và tài khoản |
| Sprint 2 | Biến động nhân khẩu, danh mục phí và tạo khoản thu |
| Sprint 3 | Thanh toán, biên lai, tra cứu và thống kê |
| Sprint 4 | Phân quyền, báo cáo, backup và audit log |

Các chức năng thuộc `EP-06` và `EP-07` được xác định là **Won't-have trong v1.0** và dự kiến xem xét ở phiên bản 2.0.

---

# 12. Traceability Matrix

Ma trận truy vết đảm bảo mối liên hệ giữa các artefact:

```text
RAW Requirement
      ↓
Epic
      ↓
Feature
      ↓
User Story
      ↓
Acceptance Criteria
```

### Ví dụ

```text
RAW-005
  ↓
EP-04 – Tra cứu & Thống kê
  ↓
FE-09 – Tra cứu thông tin
  ↓
US-07 – Tra cứu thông tin hộ gia đình
  ↓
US-07: AC1, AC2
```

Mục tiêu của Traceability Matrix là đảm bảo yêu cầu có thể được truy vết xuyên suốt từ nguồn ban đầu đến tiêu chí nghiệm thu.

Chi tiết ma trận được lưu trong `Traceability_Matrix.xlsx`.

---

# 13. Xác nhận với các bên liên quan

| Bên liên quan | Nội dung cần xác nhận |
|---|---|
| Đại diện Ban quản trị | Epic, User Story và Product Backlog |
| Đại diện Thủ quỹ | Quy trình thu phí và Acceptance Criteria |
| Đại diện Cư dân | Nhu cầu tra cứu công nợ |

---

# 14. Giả định và vấn đề cần xác nhận

- Vai trò **Thủ quỹ, Cư dân và Nhân viên thu phí** cần được xác nhận.
- `RAW-017`: mức phí dịch vụ cụ thể của BlueMoon chưa được chốt.
- `RAW-018`: mức phí quản lý cụ thể chưa được chốt.
- `RAW-025`: cần xác nhận ai tạo và ai duyệt tài khoản.
- `RAW-026`: đề bài ghi `"1.200.000 nghìn đồng"` cho phí ô tô; tài liệu đang giả định là **1.200.000 đồng/xe/tháng** và cần xác nhận lại.
- `RAW-027`: Java và MySQL là ràng buộc kỹ thuật áp dụng cho toàn hệ thống.
- `RAW-031`: yêu cầu về giao diện thân thiện là NFR áp dụng trên toàn hệ thống.
- Các thông tin phỏng vấn trong tài liệu là **mô phỏng dựa trên đề bài**, cần thay bằng dữ liệu thực tế nếu có.

---

# 15. Cấu trúc repository

```text
QuanLyChungCu/
├── README.md
├── Raw_Log.xlsx
├── Product_Backlog.xlsx
└── Traceability_Matrix.xlsx
```

| File | Nội dung |
|---|---|
| `README.md` | Tổng quan và tài liệu khai phá yêu cầu |
| `Raw_Log.xlsx` | Danh sách yêu cầu thô |
| `Product_Backlog.xlsx` | Product Backlog |
| `Traceability_Matrix.xlsx` | Ma trận truy vết |

---

# 16. Phạm vi phiên bản

## Version 1.0

Tập trung vào:

- Quản lý hộ gia đình
- Quản lý nhân khẩu
- Quản lý biến động nhân khẩu
- Quản lý các khoản thu
- Thu phí
- Quản lý công nợ
- Tra cứu
- Thống kê
- Quản lý tài khoản
- Phân quyền
- Báo cáo nhân khẩu
- Sao lưu dữ liệu
- Audit log

## Version 2.0

Các nội dung được định hướng mở rộng:

- Quản lý phí gửi xe
- Thu hộ điện, nước, internet

---

# 17. Trạng thái dự án

**Status:** Requirement Elicitation / Analysis

Tài liệu hiện tập trung vào **phân tích và khai phá yêu cầu**, chưa phải tài liệu triển khai hoàn chỉnh của hệ thống phần mềm.

---

## Nhóm thực hiện
Nhóm 16 

