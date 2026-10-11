# BlueMoon Apartment Management System — Tài liệu khai phá yêu cầu

| Thông tin | Nội dung |
|---|---|
| Dự án | BlueMoon Apartment Management System |
| Phiên bản phạm vi | v1.0 (định hướng v2.0) |
| Loại tài liệu | Requirement Elicitation — Khai phá và phân tích yêu cầu |
| Công nghệ dự kiến | Java Desktop Application, MySQL, JDBC |
| Cập nhật lần cuối | 09/10/2026 |

---

## Mục lục

| STT | Sản phẩm | Sheet | Thuộc |
|---:|---|---|---|
| 1 | Client's Wish List | `1_Wish_List` | Bước 2 |
| 2 | As-Is Scenario | `2_As_Is` | Bước 2 |
| 3 | Visionary Scenario | `3_Visionary` | Bước 2 |
| 4 | Non-functional Requirements (NFR thô) | `4_NFR` | Bước 2 |
| 5 | Raw Log | `5_Raw_Log` | Bước 2 |
| 6a | Danh sách Epic | `6a_Epic` | Bước 3 |
| 6b | Danh sách Feature | `6b_Feature` | Bước 3 |
| 7 | Bộ User Story | `7_User_Story` | Bước 3 |
| 8 | Acceptance Criteria | `8_AC` | Bước 3 |
| 9 | Product Backlog | `9_Backlog` | Bước 3 |
| 10 | Traceability Matrix | `10_Traceability` | Bước 3 |
| 11 | Definition of Done | `11_DoD` | Ghi chú bổ sung |
| — | Chốt xác nhận và giả định | `Chot_va_Gia_dinh` | Bước 3 |

### Quy ước chung

| Mục | Quy ước |
|---|---|
| Mã định danh | `RAW` (mong muốn thô), `EPIC`, `F` (Feature), `US` (User Story), `TS` (Technical Story), `AC` (Acceptance Criteria), `NFR` |
| Mức ưu tiên (MoSCoW) | **Must** — bắt buộc; **Should** — nên có; **Could** — có thể có; **Won't** — không thực hiện ở v1.0 |
| Story Point (SP) | Ước lượng sơ bộ, chờ nhóm chốt bằng Planning Poker |
| Mô hình Acceptance Criteria | Given – When – Then (GWT) |
| Luồng truy vết | RAW → Epic → Feature → User Story → Acceptance Criteria |

### Viết tắt

| Viết tắt | Ý nghĩa |
|---|---|
| BQT | Ban quản trị chung cư |
| PO | Product Owner |
| UAT | User Acceptance Testing (nghiệm thu người dùng) |
| SP | Story Point |

---

## 1. Client's Wish List

Mong muốn thô từ Ban quản trị, cư dân, thủ quỹ và chính quyền; giữ nguyên văn, chưa lọc.

| ID | Nguồn | Mong muốn thô | Ghi chú |
|---|---|---|---|
| RAW-001 | Ban quản trị | Tôi muốn thêm mới thông tin một hộ gia đình và diện tích căn hộ khi họ bắt đầu chuyển đến. | Cốt lõi để tính phí sau này |
| RAW-002 | Sổ hộ khẩu (tài liệu) | Cần lưu trữ các thông tin cơ bản từ sổ hộ khẩu. | Cấu trúc dữ liệu từ sổ hộ khẩu |
| RAW-003 | Ban quản trị | Tôi muốn quản lý thông tin các thành viên trong từng hộ gia đình. | Theo từng căn hộ |
| RAW-004 | Ban quản trị | Tôi muốn ghi nhận thông tin tạm trú, tạm vắng của cư dân. | Theo dõi biến động nhân khẩu |
| RAW-005 | Ban quản trị | Tôi muốn lưu ngày bắt đầu và ngày kết thúc tạm trú, tạm vắng. | Phục vụ tra cứu |
| RAW-006 | Ban quản trị | Tôi muốn xuất danh sách cư dân tạm vắng để cung cấp cho công an phường khi cần. | Báo cáo nhân khẩu |
| RAW-007 | Thủ quỹ | Tôi muốn hệ thống tự tính phí dịch vụ theo diện tích căn hộ. | Giảm tính toán thủ công |
| RAW-008 | Thủ quỹ | Tôi muốn hệ thống tự tính phí quản lý theo diện tích và đơn giá. | Hạn chế sai sót |
| RAW-009 | Ban quản trị | Tôi muốn tạo các khoản thu tự nguyện theo từng đợt. | Ví dụ: Quỹ vì người nghèo |
| RAW-010 | Thủ quỹ | Tôi muốn ghi nhận số tiền đóng góp tự nguyện của từng hộ. | Có thể bằng 0 đồng |
| RAW-011 | Thủ quỹ | Tôi muốn hệ thống tự tạo danh sách công nợ đầu mỗi tháng. | Hỗ trợ đi thu |
| RAW-012 | Thủ quỹ | Tôi muốn cập nhật khoản thu trước khi hộ thanh toán. | Sửa sai khi nhập |
| RAW-013 | Thủ quỹ | Tôi muốn nhập mã phòng để xem ngay các khoản chưa đóng. | Tra cứu nhanh |
| RAW-014 | Thủ quỹ | Tôi muốn thu tất cả các khoản của một hộ bằng một thao tác. | Tự động cộng tổng |
| RAW-015 | Thủ quỹ | Tôi muốn lựa chọn hình thức thanh toán tiền mặt hoặc chuyển khoản. | Phục vụ đối soát |
| RAW-016 | Thủ quỹ | Tôi muốn in biên lai sau khi thu tiền. | Cung cấp chứng từ |
| RAW-017 | Thủ quỹ | Tôi muốn hệ thống lưu người thực hiện thu tiền. | Truy vết trách nhiệm |
| RAW-018 | Thủ quỹ | Tôi muốn hệ thống tự cộng tổng số tiền phải thu. | Tránh cộng nhầm |
| RAW-019 | Thủ quỹ | Tôi muốn hủy phiếu thu nhầm trong ngày. | Vẫn phải truy vết được |
| RAW-020 | Ban quản trị | Tôi muốn xem tổng số tiền đã thu trong tháng. | Theo dõi tình hình thu |
| RAW-021 | Ban quản trị | Tôi muốn lọc danh sách các hộ nợ phí quá 3 tháng. | Hỗ trợ nhắc nợ |
| RAW-022 | Thủ quỹ | Tôi muốn xuất báo cáo thu tiền ra Excel. | Theo mẫu cũ |
| RAW-023 | Ban quản trị | Tôi muốn xem tổng thu của từng khoản/quỹ theo từng đợt. | Phục vụ báo cáo |
| RAW-024 | Ban quản trị | Hệ thống sử dụng Java máy tính để bàn kết nối MySQL. | Yêu cầu kỹ thuật |
| RAW-025 | Ban quản trị | Tôi muốn đăng nhập bằng tài khoản và mật khẩu. | Kiểm soát truy cập |
| RAW-026 | Người dùng | Tôi muốn đổi mật khẩu và cập nhật thông tin cá nhân. | Bảo mật tài khoản |
| RAW-027 | Quản trị viên | Tôi muốn phân quyền người dùng. | Quản trị viên / nhân viên thu phí |
| RAW-028 | Thủ quỹ | Tôi muốn tra cứu các khoản chưa đóng của một căn hộ. | Hỗ trợ thu phí |
| RAW-029 | Ban quản trị | Tôi muốn quản lý phí gửi xe. | Định hướng v2.0 |
| RAW-030 | Ban quản trị | Tôi muốn thu hộ tiền điện, nước và internet. | Định hướng v2.0 |
| RAW-031 | Thủ quỹ | Tôi muốn ghi nhận khoản đóng góp tự nguyện bằng 0 đồng. | Phân biệt với chưa thu |
| RAW-032 | Thủ quỹ | Tôi muốn in biên lai cho cư dân. | Chứng từ thanh toán |
| RAW-033 | Quản trị viên | Tôi muốn ghi nhật ký các thao tác quan trọng. | Audit log |
| RAW-034 | Quản trị viên | Tôi muốn sao lưu dữ liệu định kỳ. | Hạn chế mất dữ liệu |
| RAW-035 | Quản trị viên | Tôi muốn dữ liệu cư dân không bị lộ cho người không có quyền. | Bảo mật |
| RAW-036 | Ban quản trị | Tôi muốn xuất danh sách nhân khẩu tạm vắng. | Cung cấp cho cơ quan chức năng |
| RAW-037 | Người dùng | Tôi muốn giao diện dễ sử dụng, chữ rõ ràng và ít bước thao tác. | Phù hợp người lớn tuổi |
| RAW-038 | Ban quản trị | Tôi muốn in thông báo các khoản phí hàng tháng cho từng hộ. | Thông báo thu phí |
| RAW-039 | Ban quản trị | Tôi muốn đăng ký tài khoản đăng nhập. | Người có quyền mới được sử dụng |
| RAW-040 | Người dùng | Tôi muốn cập nhật thông tin cá nhân. | Quản lý tài khoản |

> **Lưu ý:** một số mong muốn trùng hoặc chồng lấn nhau (RAW-006 ↔ RAW-036; RAW-016 ↔ RAW-032; RAW-013 ↔ RAW-028; RAW-014 ↔ RAW-018; RAW-026 ↔ RAW-040). Các dòng này được giữ nguyên để bảo toàn nguồn gốc và được gộp về cùng một User Story ở Traceability Matrix.

---

## 2. As-Is Scenario

### 2.1. Hiện trạng

Công tác quản lý chung cư hiện chủ yếu được thực hiện thủ công hoặc bằng các file Excel riêng lẻ.

**Quản lý hộ gia đình**

- Thông tin hộ gia đình được ghi nhận thủ công.
- Thông tin căn hộ và diện tích cần được nhập và quản lý.
- Việc cập nhật thông tin cư dân có thể phát sinh sai sót.
- Chưa có hệ thống tập trung để quản lý dữ liệu.

**Quản lý nhân khẩu**

- Thông tin thành viên trong hộ được lưu trữ dựa trên hồ sơ hiện có.
- Việc theo dõi quan hệ giữa các thành viên còn thủ công.
- Thông tin tạm trú, tạm vắng cần được cập nhật và tra cứu khi có yêu cầu.

**Quản lý phí — quy trình hiện tại**

1. Xác định các hộ cần thu.
2. Tính phí dựa trên diện tích và đơn giá.
3. Lập danh sách công nợ.
4. Thu tiền từ cư dân.
5. Ghi nhận khoản thu.
6. In hoặc lập biên lai.
7. Tổng hợp báo cáo.

### 2.2. Các vấn đề tồn tại

| STT | Vấn đề | Hệ quả |
|---:|---|---|
| 1 | Tính toán thủ công | Dễ sai sót |
| 2 | Dữ liệu phân tán | Khó quản lý tập trung |
| 3 | Khó tra cứu nhanh công nợ | Mất thời gian khi thu phí |
| 4 | Khó kiểm soát lịch sử thay đổi dữ liệu | Thiếu cơ sở đối chiếu |
| 5 | Lập báo cáo mất nhiều thời gian | Chậm thông tin cho BQT |
| 6 | Khó phân quyền người sử dụng | Rủi ro lộ dữ liệu |
| 7 | Nguy cơ mất dữ liệu nếu không sao lưu | Mất dữ liệu thu phí, cư dân |
| 8 | Khó truy vết người thực hiện thao tác | Khó xác định trách nhiệm |

---

## 3. Visionary Scenario

BlueMoon hướng tới một hệ thống quản lý chung cư tập trung, hỗ trợ Ban quản trị và Thủ quỹ quản lý cư dân, tính phí, thu phí và lập báo cáo.

### 3.1. Mục tiêu

- Quản lý tập trung thông tin căn hộ.
- Quản lý thông tin cư dân và nhân khẩu.
- Theo dõi tạm trú, tạm vắng.
- Tự động tính các khoản phí.
- Quản lý các khoản thu tự nguyện.
- Quản lý công nợ.
- Hỗ trợ thu phí nhanh chóng.
- In biên lai.
- Lập báo cáo.
- Phân quyền người dùng.
- Sao lưu và bảo vệ dữ liệu.

### 3.2. Trạng thái mong muốn

> Ban quản trị có thể quản lý toàn bộ thông tin chung cư trên một hệ thống thống nhất, giảm thao tác thủ công, hạn chế sai sót và nâng cao khả năng tra cứu, đối soát và báo cáo.

---

## 4. Non-functional Requirements

| ID | Nhóm yêu cầu | Mô tả |
|---|---|---|
| NFR-001 | Hiệu năng | Các thao tác tra cứu thông thường phải phản hồi nhanh (mục tiêu < 3 giây). |
| NFR-002 | Bảo mật | Chỉ người dùng được cấp quyền mới có thể truy cập dữ liệu tương ứng. |
| NFR-003 | Sao lưu | Dữ liệu cần được sao lưu định kỳ. |
| NFR-004 | Khả năng sử dụng | Giao diện rõ ràng, dễ sử dụng, hạn chế số bước thao tác. |
| NFR-005 | Tính toàn vẹn dữ liệu | Hệ thống phải kiểm tra dữ liệu đầu vào và hạn chế dữ liệu không hợp lệ. |

---

## 5. Raw Log

| ID | Nội dung | Nguồn |
|---|---|---|
| RAW-001 | Thêm mới thông tin hộ gia đình và diện tích căn hộ | Ban quản trị |
| RAW-002 | Lưu thông tin cơ bản từ sổ hộ khẩu | Sổ hộ khẩu |
| RAW-003 | Quản lý thành viên từng hộ | Ban quản trị |
| RAW-004 | Ghi nhận tạm trú, tạm vắng | Ban quản trị |
| RAW-005 | Lưu ngày bắt đầu và kết thúc | Ban quản trị |
| RAW-006 | Xuất danh sách nhân khẩu tạm vắng | Ban quản trị |
| RAW-007 | Tự tính phí dịch vụ | Thủ quỹ |
| RAW-008 | Tự tính phí quản lý | Thủ quỹ |
| RAW-009 | Tạo khoản thu tự nguyện | Ban quản trị |
| RAW-010 | Ghi nhận đóng góp tự nguyện | Thủ quỹ |
| RAW-011 | Tự tạo danh sách công nợ | Thủ quỹ |
| RAW-012 | Sửa khoản thu trước khi thanh toán | Thủ quỹ |
| RAW-013 | Tra cứu phí chưa đóng | Thủ quỹ |
| RAW-014 | Thu tất cả khoản của hộ | Thủ quỹ |
| RAW-015 | Chọn phương thức thanh toán | Thủ quỹ |
| RAW-016 | In biên lai | Thủ quỹ |
| RAW-017 | Lưu người thu | Thủ quỹ |
| RAW-018 | Tự cộng tổng tiền | Thủ quỹ |
| RAW-019 | Hủy phiếu thu | Thủ quỹ |
| RAW-020 | Xem tổng thu tháng | Ban quản trị |
| RAW-021 | Lọc nợ quá hạn | Ban quản trị |
| RAW-022 | Xuất báo cáo Excel | Thủ quỹ |
| RAW-023 | Thống kê theo khoản thu | Ban quản trị |
| RAW-024 | Java Desktop + MySQL | Ban quản trị |
| RAW-025 | Đăng nhập | Ban quản trị |
| RAW-026 | Đổi mật khẩu | Người dùng |
| RAW-027 | Phân quyền | Quản trị viên |
| RAW-028 | Tra cứu công nợ | Thủ quỹ |
| RAW-029 | Quản lý phí gửi xe | Ban quản trị |
| RAW-030 | Thu hộ điện, nước, internet | Ban quản trị |
| RAW-031 | Ghi nhận đóng góp 0 đồng | Thủ quỹ |
| RAW-032 | In biên lai | Thủ quỹ |
| RAW-033 | Nhật ký thao tác | Quản trị viên |
| RAW-034 | Sao lưu dữ liệu | Quản trị viên |
| RAW-035 | Bảo mật dữ liệu cư dân | Quản trị viên |
| RAW-036 | Xuất danh sách tạm vắng | Ban quản trị |
| RAW-037 | Giao diện thân thiện | Người dùng |
| RAW-038 | Thông báo thu phí | Ban quản trị |
| RAW-039 | Đăng ký tài khoản | Ban quản trị |
| RAW-040 | Cập nhật thông tin cá nhân | Người dùng |

---

## 6a. Danh sách Epic

> Chuẩn hóa từ Wish List; Epic nhóm theo chủ đề.

| Epic ID | Tên Epic | Mô tả ngắn | Phiên bản |
|---|---|---|---|
| EPIC-01 | Quản lý hộ gia đình và nhân khẩu | Quản lý hộ khẩu, nhân khẩu, tạm trú/tạm vắng, báo cáo cho cơ quan chức năng | v1.0 |
| EPIC-02 | Quản lý danh mục và khoản thu | Tính phí tự động, tạo khoản thu tự nguyện, ghi nhận đóng góp, chốt công nợ | v1.0 |
| EPIC-03 | Thu phí và thanh toán | Tra cứu phí, ghi nhận thanh toán, biên lai, hủy phiếu, thông báo thu phí | v1.0 |
| EPIC-04 | Thống kê và báo cáo | Tổng thu, lọc nợ đọng, thống kê theo khoản, xuất Excel | v1.0 |
| EPIC-05 | Tài khoản, bảo mật và nền tảng hệ thống | Đăng ký, đăng nhập, phân quyền, đổi mật khẩu; kiến trúc, sao lưu, nhật ký thay đổi, giao diện | v1.0 |
| EPIC-06 | Phí gửi xe | Quản lý phương tiện và thu phí gửi xe hàng tháng | v2.0 |
| EPIC-07 | Thu hộ điện, nước, internet | Thu hộ các khoản dịch vụ tiện ích theo thông báo của nhà cung cấp | v2.0 |

---

## 6b. Danh sách Feature

> Feature là chức năng cụ thể thuộc một Epic.

| Feature ID | Tên Feature | Mô tả ngắn | Epic |
|---|---|---|---|
| F-01 | Quản lý hộ gia đình | Thêm, sửa hộ và diện tích căn hộ | EPIC-01 |
| F-02 | Quản lý nhân khẩu | Thêm, sửa nhân khẩu; quan hệ với chủ hộ | EPIC-01 |
| F-03 | Tạm trú, tạm vắng | Ghi nhận tạm trú, tạm vắng với ngày bắt đầu và kết thúc | EPIC-01 |
| F-04 | Báo cáo nhân khẩu | Xuất danh sách tạm vắng, tạm trú cho cơ quan chức năng | EPIC-01 |
| F-05 | Tính phí tự động | Phí = diện tích × đơn giá cho phí dịch vụ và phí quản lý | EPIC-02 |
| F-06 | Quản lý khoản thu tự nguyện | Tạo khoản thu, ghi nhận đóng góp (kể cả 0 đồng), sửa giá trị | EPIC-02 |
| F-07 | Chốt công nợ hàng tháng | Tự tạo danh sách công nợ cho tất cả các phòng | EPIC-02 |
| F-08 | Tra cứu phí theo mã phòng | Hiển thị các khoản chưa đóng của phòng | EPIC-03 |
| F-09 | Ghi nhận thanh toán | Thu tất cả, chọn hình thức, lưu người thu | EPIC-03 |
| F-10 | Biên lai và hủy phiếu thu | In biên lai, hủy phiếu thu nhầm trong ngày | EPIC-03 |
| F-11 | Thông báo thu phí | In thông báo các khoản phí hàng tháng cho từng hộ | EPIC-03 |
| F-12 | Tổng thu trên màn hình chính | Hiển thị tổng thu trong tháng | EPIC-04 |
| F-13 | Lọc nợ đọng | Lọc danh sách hộ nợ quá 3 tháng | EPIC-04 |
| F-14 | Xuất báo cáo Excel | Xuất báo cáo thu tiền theo mẫu thủ công cũ | EPIC-04 |
| F-15 | Thống kê theo khoản thu | Tổng thu theo từng khoản/quỹ và đợt | EPIC-04 |
| F-16 | Đăng ký tài khoản | Tạo tài khoản người dùng | EPIC-05 |
| F-17 | Đăng nhập và phân quyền | Xác thực; vai trò Quản trị viên / Nhân viên thu phí | EPIC-05 |
| F-18 | Quản lý tài khoản | Đổi mật khẩu, cập nhật thông tin cá nhân | EPIC-05 |
| F-19 | Quản lý phí gửi xe (v2.0) | Quản lý biển số và tính phí xe máy/ô tô | EPIC-06 |
| F-20 | Thu hộ điện, nước, internet (v2.0) | Import file thông báo và thu hộ | EPIC-07 |
| F-21 | Nền tảng hệ thống | Kiến trúc Java (máy tính để bàn) + MySQL, sao lưu, nhật ký thay đổi, giao diện thân thiện | EPIC-05 |

---

## 7. Bộ User Story

> User Story theo cấu trúc INVEST: **Là… tôi muốn… để…**
> TS = Technical Story (câu chuyện kỹ thuật).

| US ID | Tiêu đề | User Story | Epic / Feature | Nguồn | Ưu tiên | Phụ thuộc |
|---|---|---|---|---|---|---|
| US-001 | Quản lý hộ gia đình | Là Ban quản trị, tôi muốn thêm thông tin hộ gia đình và diện tích căn hộ khi cư dân mới chuyển đến, để quản lý cư dân và làm cơ sở tính phí. | EPIC-01 / F-01 | RAW-001 | Must | TS-003 |
| US-002 | Quản lý nhân khẩu | Là Ban quản trị, tôi muốn lưu thông tin nhân khẩu và quan hệ với chủ hộ, để quản lý chính xác thành viên từng hộ. | EPIC-01 / F-02 | RAW-002, RAW-003 | Must | US-001 |
| US-003 | Tạm trú, tạm vắng | Là Ban quản trị, tôi muốn ghi nhận tạm trú, tạm vắng kèm ngày bắt đầu và kết thúc, để theo dõi biến động nhân khẩu. | EPIC-01 / F-03 | RAW-004, RAW-005 | Must | US-002 |
| US-004 | Báo cáo nhân khẩu | Là Ban quản trị, tôi muốn xuất danh sách nhân khẩu tạm vắng, để cung cấp cho công an phường và cơ quan chức năng khi được yêu cầu. | EPIC-01 / F-04 | RAW-006, RAW-036 | Should | US-003 |
| US-005 | Tính phí tự động | Là Thủ quỹ, tôi muốn hệ thống tự tính phí dịch vụ và phí quản lý theo diện tích × đơn giá, để tránh tính tay sai sót. | EPIC-02 / F-05 | RAW-007, RAW-008 | Must | US-001 |
| US-006 | Tạo khoản thu tự nguyện | Là Ban quản trị, tôi muốn tự tạo khoản thu tự nguyện mới theo từng đợt (VD: Quỹ vì người nghèo), để quản lý linh hoạt các quỹ phát sinh. | EPIC-02 / F-06 | RAW-009 | Must | — |
| US-007 | Ghi nhận đóng góp tự nguyện | Là Thủ quỹ, tôi muốn ghi nhận số tiền đóng góp tùy ý (kể cả 0 đồng) cho nhiều quỹ của một hộ, để phản ánh đúng tinh thần tự nguyện. | EPIC-02 / F-06 | RAW-010, RAW-031 | Must | US-006 |
| US-008 | Sửa khoản thu | Là Thủ quỹ, tôi muốn cập nhật lại giá trị khoản thu của một hộ trước khi hộ đóng, để sửa kịp thời khi nhập sai. | EPIC-02 / F-06 | RAW-012 | Should | US-006 |
| US-009 | Chốt công nợ | Là Thủ quỹ, tôi muốn hệ thống tự tạo danh sách công nợ cho tất cả các phòng vào đầu mỗi tháng, để có danh sách đi thu mà không phải lập tay. | EPIC-02 / F-07 | RAW-011 | Must | US-005, US-006 |
| US-010 | Tra cứu phí | Là Thủ quỹ, tôi muốn nhập mã phòng để xem ngay các khoản chưa đóng, để thu tiền nhanh khi cư dân đến đóng. | EPIC-03 / F-08 | RAW-013, RAW-028 | Must | US-001, US-009 |
| US-011 | Thu tất cả các khoản | Là Thủ quỹ, tôi muốn thu tất cả các khoản của hộ bằng một nút và hệ thống tự cộng tổng, để thu nhanh và không cộng nhầm. | EPIC-03 / F-09 | RAW-014, RAW-018 | Must | US-010 |
| US-012 | Hình thức thanh toán và người thu | Là Thủ quỹ, tôi muốn chọn tiền mặt hoặc chuyển khoản và hệ thống tự lưu người thu, để đối soát và truy vết trách nhiệm. | EPIC-03 / F-09 | RAW-015, RAW-017 | Must | US-011 |
| US-013 | In biên lai | Là Thủ quỹ, tôi muốn in biên lai từ phần mềm sau khi thu, để cư dân có chứng từ đối chiếu. | EPIC-03 / F-10 | RAW-016, RAW-032 | Should | US-011 |
| US-014 | Hủy phiếu thu | Là Thủ quỹ, tôi muốn hủy phiếu thu nhầm trong ngày, để sửa sai mà vẫn truy vết được. | EPIC-03 / F-10 | RAW-019 | Should | US-011 |
| US-015 | Thông báo thu phí | Là Ban quản trị, tôi muốn in thông báo các khoản phí hàng tháng cho từng hộ, để cư dân biết rõ khoản phải đóng. | EPIC-03 / F-11 | RAW-038 | Should | US-009 |
| US-016 | Tổng thu tháng | Là Ban quản trị, tôi muốn xem tổng tiền đã thu trong tháng ngay trên màn hình chính, để nắm nhanh tình hình thu. | EPIC-04 / F-12 | RAW-020 | Should | US-011 |
| US-017 | Lọc nợ đọng | Là Ban quản trị, tôi muốn lọc danh sách hộ nợ phí quá 3 tháng, để nhắc nhở kịp thời. | EPIC-04 / F-13 | RAW-021 | Should | US-009, US-011 |
| US-018 | Xuất báo cáo Excel | Là Thủ quỹ, tôi muốn xuất báo cáo thu tiền ra Excel theo mẫu thủ công cũ, để lưu trữ và đối chiếu với quy trình cũ. | EPIC-04 / F-14 | RAW-022 | Could | US-011 |
| US-019 | Thống kê theo khoản thu | Là Ban quản trị, tôi muốn xem tổng thu của từng khoản/quỹ theo đợt, để báo cáo kết quả từng quỹ. | EPIC-04 / F-15 | RAW-023 | Should | US-007, US-011 |
| US-020 | Đăng ký tài khoản | Là Ban quản trị, tôi muốn đăng ký tài khoản đăng nhập, để người có quyền được dùng hệ thống. | EPIC-05 / F-16 | RAW-039 | Must | TS-003 |
| US-021 | Đăng nhập | Là Ban quản trị, tôi muốn đăng nhập bằng tên đăng nhập và mật khẩu, để chỉ người có tài khoản truy cập được dữ liệu. | EPIC-05 / F-17 | RAW-025 | Must | US-020 |
| US-022 | Phân quyền | Là Quản trị viên, tôi muốn phân quyền Quản trị viên và Nhân viên thu phí, để mỗi người chỉ dùng chức năng được giao và thông tin cư dân không bị lộ. | EPIC-05 / F-17 | RAW-027, RAW-035 | Should | US-021 |
| US-023 | Quản lý tài khoản | Là Người dùng, tôi muốn đổi mật khẩu và cập nhật thông tin cá nhân, để giữ an toàn tài khoản. | EPIC-05 / F-18 | RAW-026, RAW-040 | Should | US-021 |
| US-024 | Phí gửi xe (v2.0) | Là Ban quản trị, tôi muốn quản lý biển số và tự tính phí gửi xe (70.000 đ/xe máy, 1.200.000 đ/ô tô), để thu đúng theo phương tiện. | EPIC-06 / F-19 | RAW-029 | Won't | US-001 |
| US-025 | Thu hộ điện, nước, internet (v2.0) | Là Ban quản trị, tôi muốn nhập file thông báo từ điện lực, nhà mạng, để thu hộ điện, nước, internet. | EPIC-07 / F-20 | RAW-030 | Won't | US-009 |
| TS-001 | Sao lưu dữ liệu | Sao lưu dữ liệu MySQL định kỳ (hàng ngày/tuần) để tránh mất dữ liệu. | EPIC-05 / F-21 | RAW-034 | Should | TS-003 |
| TS-002 | Nhật ký thao tác | Ghi nhật ký mọi thao tác thay đổi dữ liệu quan trọng. | EPIC-05 / F-21 | RAW-033, RAW-017 | Should | TS-003 |
| TS-003 | Nền tảng Java Desktop + MySQL | Thiết lập ứng dụng Java máy tính để bàn kết nối CSDL MySQL tập trung. | EPIC-05 / F-21 | RAW-024 | Must | — |
| TS-004 | Giao diện thân thiện | Rà soát giao diện dễ dùng cho người lớn tuổi (chữ rõ, ít bước thao tác). | EPIC-05 / F-21 | RAW-037 | Could | — |

---

## 8. Acceptance Criteria

> Viết theo mô hình GWT (Given – When – Then), có thể kiểm thử được.
> **AC-001 → AC-026** là nội dung gốc. **AC-027 → AC-072** được bổ sung theo đúng dải mã đã tham chiếu trong Traceability Matrix; cần nhóm và PO rà soát (xem phần Giả định).

### EPIC-01 — Quản lý hộ gia đình và nhân khẩu

| AC ID | US ID | Giả sử (Given) | Khi (When) | Thì (Then) |
|---|---|---|---|---|
| AC-001 | US-001 | BQT đã đăng nhập | Nhập đầy đủ mã căn hộ, tên chủ hộ, diện tích rồi lưu | Hệ thống tạo hộ gia đình mới và hiển thị trong danh sách |
| AC-002 | US-001 | Đang nhập thông tin căn hộ | Nhập diện tích căn hộ (m²) | Hệ thống lưu đúng giá trị diện tích đã nhập |
| AC-003 | US-001 | Thiếu thông tin bắt buộc | Nhấn lưu | Hệ thống báo các trường còn thiếu và không lưu |
| AC-004 | US-001 | Mã căn hộ đã tồn tại | Thêm hộ trùng mã căn hộ | Hệ thống báo trùng và không lưu |
| AC-005 | US-002 | Hộ đã tồn tại | Thêm nhân khẩu với họ tên, ngày sinh, giới tính, số CCCD | Hệ thống lưu nhân khẩu vào hộ và cập nhật số thành viên |
| AC-006 | US-002 | Đang khai báo nhân khẩu | Chọn quan hệ với chủ hộ (chủ hộ, vợ/chồng, con, khác) | Hệ thống lưu quan hệ đã chọn |
| AC-007 | US-002 | Hộ đã có chủ hộ | Khai báo thêm một chủ hộ thứ hai | Hệ thống từ chối và báo mỗi hộ chỉ có một chủ hộ |
| AC-008 | US-002 | Số CCCD đã tồn tại | Lưu nhân khẩu mới | Hệ thống cảnh báo trùng số CCCD |
| AC-009 | US-003 | Nhân khẩu thuộc một hộ | Ghi nhận tạm trú hoặc tạm vắng với ngày bắt đầu và ngày kết thúc | Hệ thống lưu và cập nhật trạng thái cư trú |
| AC-010 | US-003 | Đang ghi nhận tạm trú/tạm vắng | Để trống ngày bắt đầu hoặc ngày kết thúc | Hệ thống báo lỗi và không lưu |
| AC-011 | US-003 | Ngày kết thúc trước ngày bắt đầu | Lưu | Hệ thống từ chối |
| AC-012 | US-004 | Có nhân khẩu đang tạm vắng | BQT chọn xuất danh sách tạm vắng | Hệ thống tạo danh sách gồm họ tên, căn hộ, thời gian tạm vắng |
| AC-013 | US-004 | Không có nhân khẩu tạm vắng | Xuất danh sách | Hệ thống thông báo không có dữ liệu |
| AC-014 | US-004 | Danh sách đã tạo | Chọn xuất file | Hệ thống tạo file Excel/PDF đủ các trường theo quy định báo cáo |

### EPIC-02 — Quản lý danh mục và khoản thu

| AC ID | US ID | Giả sử (Given) | Khi (When) | Thì (Then) |
|---|---|---|---|---|
| AC-015 | US-005 | Căn hộ có diện tích và đơn giá phí dịch vụ trong khoảng 2.500–16.500 đ/m² | Hệ thống tính phí | Phí dịch vụ = diện tích × đơn giá |
| AC-016 | US-005 | Căn hộ có diện tích | Tính phí quản lý | Hệ thống dùng đơn giá mặc định 7.000 đ/m² (BQT có thể thay đổi) |
| AC-017 | US-005 | Đơn giá phí dịch vụ nằm ngoài khoảng 2.500–16.500 đ/m² | Lưu cấu hình | Hệ thống cảnh báo |
| AC-018 | US-005 | Dữ liệu tính phí hợp lệ | Xem khoản phí | Tổng tiền hiển thị chính xác |
| AC-019 | US-006 | BQT ở màn hình quản lý khoản thu | Tạo khoản thu tự nguyện mới (tên, thời gian) | Khoản thu xuất hiện trong danh mục |
| AC-020 | US-006 | Tên khoản thu đã tồn tại trong cùng đợt | Lưu | Hệ thống báo trùng |
| AC-021 | US-006 | Tạo khoản thu tự nguyện | Không nhập số tiền cố định | Hệ thống vẫn cho lưu |
| AC-022 | US-007 | Khoản thu tự nguyện đang mở | Ghi nhận hộ đóng số tiền bất kỳ | Hệ thống lưu đúng số tiền |
| AC-023 | US-007 | Hộ không đóng quỹ | Ghi nhận 0 đồng | Hệ thống lưu trạng thái "không đóng", khác với "chưa thu" |
| AC-024 | US-007 | Hộ đóng nhiều quỹ | Nhập các khoản trên cùng màn hình | Hệ thống lưu riêng từng khoản cho từng quỹ |
| AC-025 | US-008 | Khoản thu của hộ chưa có thanh toán | Thủ quỹ cập nhật giá trị | Hệ thống lưu giá trị mới |
| AC-026 | US-008 | Khoản thu đã có thanh toán | Cố sửa giá trị | Hệ thống từ chối và yêu cầu hủy phiếu thu trước |
| AC-027 | US-009 | Đã cấu hình đơn giá và có các hộ đang quản lý | Thủ quỹ chốt công nợ cho một tháng | Hệ thống tạo khoản phải thu phí dịch vụ và phí quản lý cho tất cả các hộ |
| AC-028 | US-009 | Công nợ của tháng đã được chốt | Chốt lại cùng tháng | Hệ thống không tạo trùng và thông báo tháng đã chốt |
| AC-029 | US-009 | Có khoản thu tự nguyện đang mở | Chốt công nợ | Hệ thống đưa khoản thu tự nguyện vào công nợ từng hộ ở trạng thái "chưa thu" |

### EPIC-03 — Thu phí và thanh toán

| AC ID | US ID | Giả sử (Given) | Khi (When) | Thì (Then) |
|---|---|---|---|---|
| AC-030 | US-010 | Công nợ tháng đã chốt | Thủ quỹ nhập mã phòng hợp lệ | Hệ thống hiển thị các khoản chưa đóng và tổng tiền |
| AC-031 | US-010 | Mã phòng không tồn tại | Tra cứu | Hệ thống báo không tìm thấy |
| AC-032 | US-010 | Dữ liệu công nợ bình thường | Tra cứu theo mã phòng | Kết quả hiển thị trong vòng 3 giây |
| AC-033 | US-011 | Hộ có nhiều khoản chưa đóng | Thủ quỹ chọn thu tất cả | Các khoản chuyển sang "đã thanh toán" trong cùng một giao dịch |
| AC-034 | US-011 | Danh sách khoản cần thu đang hiển thị | Xem tổng tiền | Hệ thống tự cộng đúng tổng các khoản được thu |
| AC-035 | US-011 | Hộ không còn khoản chưa đóng | Chọn thu | Hệ thống thông báo không có khoản cần thu |
| AC-036 | US-012 | Đang xác nhận thu tiền | Chọn tiền mặt hoặc chuyển khoản | Hệ thống lưu hình thức thanh toán đã chọn |
| AC-037 | US-012 | Thủ quỹ đã đăng nhập | Ghi nhận giao dịch | Hệ thống lưu tài khoản người thu và thời điểm thu |
| AC-038 | US-012 | Chưa chọn hình thức thanh toán | Xác nhận thu | Hệ thống từ chối và yêu cầu chọn hình thức |
| AC-039 | US-013 | Giao dịch thu đã thành công | Thủ quỹ chọn in biên lai | Biên lai gồm mã phòng, các khoản đã thu, tổng tiền, hình thức, người thu, thời điểm |
| AC-040 | US-013 | Giao dịch đã lưu | In lại biên lai | Hệ thống in lại đúng nội dung giao dịch |
| AC-041 | US-014 | Phiếu thu được lập trong ngày | Thủ quỹ hủy phiếu | Phiếu chuyển sang "đã hủy" và các khoản trở về "chưa đóng" |
| AC-042 | US-014 | Phiếu thu được lập từ ngày trước | Hủy phiếu | Hệ thống từ chối |
| AC-043 | US-014 | Hủy phiếu thu hợp lệ | Xác nhận hủy | Hệ thống lưu người hủy, thời điểm, lý do và giữ nguyên phiếu gốc để truy vết |
| AC-044 | US-015 | Công nợ tháng đã chốt | BQT chọn in thông báo cho một hộ | Thông báo hiển thị các khoản phải đóng và tổng tiền của hộ |
| AC-045 | US-015 | Hộ không có khoản phải đóng | In thông báo | Hệ thống báo hộ không có khoản cần đóng |

### EPIC-04 — Thống kê và báo cáo

| AC ID | US ID | Giả sử (Given) | Khi (When) | Thì (Then) |
|---|---|---|---|---|
| AC-046 | US-016 | Người dùng đã đăng nhập | Mở màn hình chính | Hệ thống hiển thị tổng tiền đã thu trong tháng hiện tại |
| AC-047 | US-016 | Có phiếu thu đã hủy | Tính tổng thu | Phiếu đã hủy không được cộng vào tổng |
| AC-048 | US-017 | Có hộ nợ phí quá 3 tháng | BQT chọn lọc nợ đọng | Hệ thống liệt kê các hộ kèm số tháng nợ và tổng tiền nợ |
| AC-049 | US-017 | Không có hộ nợ quá 3 tháng | Lọc | Hệ thống thông báo không có dữ liệu |
| AC-050 | US-018 | Có dữ liệu thu trong kỳ được chọn | Thủ quỹ chọn xuất Excel | Hệ thống tạo file Excel theo bố cục mẫu thủ công cũ |
| AC-051 | US-018 | Kỳ được chọn không có dữ liệu | Xuất báo cáo | Hệ thống thông báo không có dữ liệu để xuất |
| AC-052 | US-019 | Có khoản thu/quỹ theo nhiều đợt | BQT chọn khoản thu và đợt | Hệ thống hiển thị tổng thu của khoản thu trong đợt đó |
| AC-053 | US-019 | Khoản tự nguyện có hộ đóng 0 đồng | Xem thống kê | Hệ thống phân biệt số hộ đã đóng, không đóng (0 đồng) và chưa ghi nhận |

### EPIC-05 — Tài khoản, bảo mật và nền tảng hệ thống

| AC ID | US ID | Giả sử (Given) | Khi (When) | Thì (Then) |
|---|---|---|---|---|
| AC-054 | US-020 | BQT ở màn hình đăng ký tài khoản | Nhập đủ họ tên, tên đăng nhập, mật khẩu rồi lưu | Hệ thống tạo tài khoản mới |
| AC-055 | US-020 | Tên đăng nhập đã tồn tại | Đăng ký tài khoản | Hệ thống báo trùng và không lưu |
| AC-056 | US-020 | Tài khoản được tạo thành công | Kiểm tra dữ liệu lưu trữ | Mật khẩu được lưu dưới dạng mã hóa, không lưu văn bản thường |
| AC-057 | US-021 | Tài khoản hợp lệ | Nhập đúng tên đăng nhập và mật khẩu | Hệ thống cho vào màn hình chính |
| AC-058 | US-021 | Nhập sai tên đăng nhập hoặc mật khẩu | Đăng nhập | Hệ thống báo lỗi chung, không tiết lộ thông tin nào sai |
| AC-059 | US-021 | Chưa đăng nhập | Truy cập chức năng của hệ thống | Hệ thống chuyển về màn hình đăng nhập |
| AC-060 | US-022 | Quản trị viên đã đăng nhập | Gán vai trò cho một tài khoản | Hệ thống lưu vai trò (Quản trị viên / Nhân viên thu phí) |
| AC-061 | US-022 | Tài khoản có vai trò Nhân viên thu phí | Truy cập chức năng quản lý tài khoản | Hệ thống từ chối truy cập |
| AC-062 | US-022 | Tài khoản có vai trò Nhân viên thu phí | Đăng nhập | Hệ thống chỉ hiển thị các chức năng tra cứu, thu phí, in biên lai, thông báo và báo cáo thu phí |
| AC-063 | US-023 | Người dùng đã đăng nhập | Đổi mật khẩu với mật khẩu cũ đúng | Hệ thống lưu mật khẩu mới |
| AC-064 | US-023 | Người dùng đã đăng nhập | Đổi mật khẩu với mật khẩu cũ sai | Hệ thống từ chối |
| AC-065 | US-023 | Người dùng đã đăng nhập | Cập nhật thông tin cá nhân hợp lệ | Hệ thống lưu thông tin mới |
| AC-066 | TS-001 | Đã cấu hình lịch sao lưu | Đến thời điểm sao lưu | Hệ thống tạo tệp sao lưu dữ liệu MySQL |
| AC-067 | TS-001 | Có tệp sao lưu hợp lệ | Thực hiện khôi phục | Dữ liệu được phục hồi đúng như thời điểm sao lưu |
| AC-068 | TS-002 | Người dùng thay đổi dữ liệu quan trọng | Lưu thay đổi | Hệ thống ghi nhật ký gồm người thực hiện, thao tác, thời điểm và dữ liệu liên quan |
| AC-069 | TS-002 | Nhật ký đã được ghi | Người dùng thao tác trên giao diện | Không thể sửa hoặc xóa nhật ký từ giao diện |
| AC-070 | TS-003 | Máy chủ MySQL hoạt động và cấu hình kết nối đúng | Khởi động ứng dụng | Ứng dụng kết nối CSDL thành công |
| AC-071 | TS-003 | Có từ hai máy cùng cấu hình kết nối | Một máy lưu dữ liệu | Máy còn lại truy xuất được dữ liệu mới |
| AC-072 | TS-004 | Giao diện các chức năng chính đã hoàn thiện | Rà soát khả năng sử dụng | Chữ rõ ràng và luồng thu phí (tra cứu → thu → in biên lai) hoàn thành trong tối đa 4 thao tác chính *(ngưỡng giả định, cần xác nhận)* |

---

## 9. Product Backlog

### 9.1. Danh sách Backlog

| ID | Mô tả ngắn | Ưu tiên | Sprint dự kiến | Trạng thái | SP | Epic |
|---|---|---|---|---|---:|---|
| **EPIC-01** | **Quản lý hộ gia đình và nhân khẩu** | Must | Sprint 1–3 | Chưa bắt đầu | — | EPIC-01 |
| US-001 | Thêm thông tin hộ gia đình và diện tích căn hộ khi cư dân mới chuyển đến | Must | Sprint 1 | Chưa bắt đầu | 3 | EPIC-01 |
| US-002 | Lưu thông tin nhân khẩu và quan hệ với chủ hộ | Must | Sprint 1 | Chưa bắt đầu | 5 | EPIC-01 |
| US-003 | Ghi nhận tạm trú, tạm vắng kèm ngày bắt đầu và kết thúc | Must | Sprint 1 | Chưa bắt đầu | 5 | EPIC-01 |
| US-004 | Xuất danh sách nhân khẩu tạm vắng | Should | Sprint 3 | Chưa bắt đầu | 3 | EPIC-01 |
| **EPIC-02** | **Quản lý danh mục và khoản thu** | Must | Sprint 2–3 | Chưa bắt đầu | — | EPIC-02 |
| US-005 | Tự tính phí dịch vụ và phí quản lý theo diện tích × đơn giá | Must | Sprint 2 | Chưa bắt đầu | 5 | EPIC-02 |
| US-006 | Tự tạo khoản thu tự nguyện mới theo từng đợt | Must | Sprint 2 | Chưa bắt đầu | 3 | EPIC-02 |
| US-007 | Ghi nhận số tiền đóng góp tùy ý, kể cả 0 đồng, cho nhiều quỹ của một hộ | Must | Sprint 3 | Chưa bắt đầu | 5 | EPIC-02 |
| US-008 | Cập nhật lại giá trị khoản thu của một hộ trước khi hộ đóng | Should | Sprint 2 | Chưa bắt đầu | 3 | EPIC-02 |
| US-009 | Tự tạo danh sách công nợ cho tất cả các phòng vào đầu mỗi tháng | Must | Sprint 2 | Chưa bắt đầu | 5 | EPIC-02 |
| **EPIC-03** | **Thu phí và thanh toán** | Must | Sprint 3 | Chưa bắt đầu | — | EPIC-03 |
| US-010 | Nhập mã phòng để xem ngay các khoản chưa đóng | Must | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| US-011 | Thu tất cả các khoản của hộ bằng một nút và tự cộng tổng | Must | Sprint 3 | Chưa bắt đầu | 5 | EPIC-03 |
| US-012 | Chọn tiền mặt hoặc chuyển khoản và tự lưu người thu | Must | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| US-013 | In biên lai từ phần mềm sau khi thu | Should | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| US-014 | Hủy phiếu thu nhầm trong ngày | Should | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| US-015 | In thông báo các khoản phí hàng tháng cho từng hộ | Should | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| **EPIC-04** | **Thống kê và báo cáo** | Should | Sprint 4 | Chưa bắt đầu | — | EPIC-04 |
| US-016 | Xem tổng tiền đã thu trong tháng ngay trên màn hình chính | Should | Sprint 4 | Chưa bắt đầu | 5 | EPIC-04 |
| US-017 | Lọc danh sách hộ nợ phí quá 3 tháng | Should | Sprint 4 | Chưa bắt đầu | 3 | EPIC-04 |
| US-018 | Xuất báo cáo thu tiền ra Excel theo mẫu thủ công cũ | Could | Sprint 4 | Chưa bắt đầu | 5 | EPIC-04 |
| US-019 | Xem tổng thu của từng khoản/quỹ theo đợt | Should | Sprint 4 | Chưa bắt đầu | 3 | EPIC-04 |
| **EPIC-05** | **Tài khoản, bảo mật và nền tảng hệ thống** | Must | Sprint 1–4 | Chưa bắt đầu | — | EPIC-05 |
| US-020 | Đăng ký tài khoản đăng nhập | Must | Sprint 1 | Chưa bắt đầu | 3 | EPIC-05 |
| US-021 | Đăng nhập bằng tên đăng nhập và mật khẩu | Must | Sprint 1 | Chưa bắt đầu | 3 | EPIC-05 |
| US-022 | Phân quyền Quản trị viên và Nhân viên thu phí | Should | Sprint 2 | Chưa bắt đầu | 5 | EPIC-05 |
| US-023 | Đổi mật khẩu và cập nhật thông tin cá nhân | Should | Sprint 2 | Chưa bắt đầu | 3 | EPIC-05 |
| TS-001 | Sao lưu dữ liệu MySQL định kỳ | Should | Sprint 4 | Chưa bắt đầu | 5 | EPIC-05 |
| TS-002 | Ghi nhật ký mọi thao tác thay đổi dữ liệu quan trọng | Should | Sprint 4 | Chưa bắt đầu | 5 | EPIC-05 |
| TS-003 | Thiết lập ứng dụng Java Desktop kết nối CSDL MySQL tập trung | Must | Sprint 1 | Chưa bắt đầu | 5 | EPIC-05 |
| TS-004 | Rà soát giao diện dễ dùng cho người lớn tuổi | Could | Sprint 4 | Chưa bắt đầu | 2 | EPIC-05 |
| **EPIC-06** | **Phí gửi xe (v2.0)** | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | — | EPIC-06 |
| US-024 | Quản lý biển số và tự tính phí gửi xe (70.000 đ/xe máy, 1.200.000 đ/ô tô) | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | — | EPIC-06 |
| **EPIC-07** | **Thu hộ điện, nước, internet (v2.0)** | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | — | EPIC-07 |
| US-025 | Nhập file thông báo từ điện lực, nhà mạng để thu hộ điện, nước, internet | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | — | EPIC-07 |

### 9.2. Tổng hợp khối lượng theo Sprint (v1.0)

| Sprint | Nội dung chính | Tổng SP |
|---|---|---:|
| Sprint 1 | Nền tảng, tài khoản, hộ và nhân khẩu (US-001, 002, 003, 020, 021, TS-003) | 24 |
| Sprint 2 | Tính phí, khoản thu, chốt công nợ, phân quyền (US-005, 006, 008, 009, 022, 023) | 24 |
| Sprint 3 | Thu phí, biên lai, đóng góp tự nguyện, báo cáo nhân khẩu (US-004, 007, 010–015) | 28 |
| Sprint 4 | Thống kê, báo cáo, sao lưu, nhật ký, giao diện (US-016–019, TS-001, 002, 004) | 28 |
| **Tổng** | | **104** |

---

## 10. Traceability Matrix

> Luồng truy vết: RAW → Epic → Feature → User Story → Acceptance Criteria.

| RAW ID | Epic | Feature | User Story | Acceptance Criteria | Ghi chú | Đã truy vết |
|---|---|---|---|---|---|---|
| RAW-001 | EPIC-01 | F-01 | US-001 | AC-001–004 | Cốt lõi để tính phí sau này | Có |
| RAW-002 | EPIC-01 | F-02 | US-002 | AC-005–008 | Cấu trúc dữ liệu từ sổ hộ khẩu | Có |
| RAW-003 | EPIC-01 | F-02 | US-002 | AC-005–008 | Cần danh sách chọn có sẵn | Có |
| RAW-004 | EPIC-01 | F-03 | US-003 | AC-009–011 | — | Có |
| RAW-005 | EPIC-01 | F-03 | US-003 | AC-009–011 | Kiểm tra dữ liệu đầu vào | Có |
| RAW-006 | EPIC-01 | F-04 | US-004 | AC-012–014 | Yêu cầu từ cơ quan chức năng | Có |
| RAW-007 | EPIC-02 | F-05 | US-005 | AC-015–018 | Tránh tính tay sai sót | Có |
| RAW-008 | EPIC-02 | F-05 | US-005 | AC-015–018 | — | Có |
| RAW-009 | EPIC-02 | F-06 | US-006 | AC-019–021 | Danh mục động | Có |
| RAW-010 | EPIC-02 | F-06 | US-007 | AC-022–024 | Xử lý số tiền linh hoạt | Có |
| RAW-011 | EPIC-02 | F-07 | US-009 | AC-027–029 | — | Có |
| RAW-012 | EPIC-02 | F-06 | US-008 | AC-025–026 | — | Có |
| RAW-013 | EPIC-03 | F-08 | US-010 | AC-030–032 | Tối ưu thời gian thu | Có |
| RAW-014 | EPIC-03 | F-09 | US-011 | AC-033–035 | Trải nghiệm thao tác | Có |
| RAW-015 | EPIC-03 | F-09 | US-012 | AC-036–038 | Phục vụ đối soát | Có |
| RAW-016 | EPIC-03 | F-10 | US-013 | AC-039–040 | — | Có |
| RAW-017 | EPIC-03, EPIC-05 | F-09, F-21 | US-012, TS-002 | AC-036–038; AC-068–069 | Truy vết trách nhiệm | Có |
| RAW-018 | EPIC-03 | F-09 | US-011 | AC-033–035 | Giải quyết điểm đau | Có |
| RAW-019 | EPIC-03 | F-10 | US-014 | AC-041–043 | — | Có |
| RAW-020 | EPIC-04 | F-12 | US-016 | AC-046–047 | — | Có |
| RAW-021 | EPIC-04 | F-13 | US-017 | AC-048–049 | — | Có |
| RAW-022 | EPIC-04 | F-14 | US-018 | AC-050–051 | Khớp với quy trình cũ | Có |
| RAW-023 | EPIC-04 | F-15 | US-019 | AC-052–053 | — | Có |
| RAW-024 | EPIC-05 | F-21 | TS-003 | AC-070–071 | Ràng buộc kỹ thuật | Có |
| RAW-025 | EPIC-05 | F-17 | US-021 | AC-057–059 | Yêu cầu bảo mật cơ bản | Có |
| RAW-026 | EPIC-05 | F-18 | US-023 | AC-063–065 | — | Có |
| RAW-027 | EPIC-05 | F-17 | US-022 | AC-060–062 | Phân quyền, quyền riêng tư | Có |
| RAW-028 | EPIC-03 | F-08 | US-010 | AC-030–032 | Yêu cầu hiệu năng | Có |
| RAW-029 | EPIC-06 | F-19 | US-024 | Chưa viết (v2.0) | Lộ trình v2.0 | Có |
| RAW-030 | EPIC-07 | F-20 | US-025 | Chưa viết (v2.0) | Lộ trình v2.0 | Có |
| RAW-031 | EPIC-02 | F-06 | US-007 | AC-022–024 | Phân tích ảnh sổ thu | Có |
| RAW-032 | EPIC-03 | F-10 | US-013 | AC-039–040 | Điểm đau từ sổ thu | Có |
| RAW-033 | EPIC-05 | F-21 | TS-002 | AC-068–069 | Nhu cầu ẩn: nhật ký thay đổi | Có |
| RAW-034 | EPIC-05 | F-21 | TS-001 | AC-066–067 | — | Có |
| RAW-035 | EPIC-05 | F-17 | US-022 | AC-060–062 | — | Có |
| RAW-036 | EPIC-01 | F-04 | US-004 | AC-012–014 | Yêu cầu pháp lý | Có |
| RAW-037 | EPIC-05 | F-21 | TS-004 | AC-072 | Khả dụng | Có |
| RAW-038 | EPIC-03 | F-11 | US-015 | AC-044–045 | Quy trình hiện tại | Có |
| RAW-039 | EPIC-05 | F-16 | US-020 | AC-054–056 | Luồng nghiệp vụ 1 | Có |
| RAW-040 | EPIC-05 | F-18 | US-023 | AC-063–065 | — | Có |

---

## 11. Definition of Done

| # | Tiêu chí hoàn thành | Áp dụng cho |
|---:|---|---|
| 1 | Mã nguồn hoàn thành và được đồng nghiệp xem xét (Code Review) | Mọi story |
| 2 | Kiểm thử đơn vị (Unit Test) đạt yêu cầu | Mọi story |
| 3 | Kiểm thử tích hợp (Integration Test) thành công | Mọi story |
| 4 | Tất cả Acceptance Criteria của story được kiểm thử đạt | Mọi story |
| 5 | Dữ liệu được lưu đúng trên MySQL Server | Story có ghi dữ liệu |
| 6 | Triển khai thành công lên môi trường thử nghiệm (Staging) | Mọi story |
| 7 | Đại diện Ban quản trị nghiệm thu (UAT) và xác nhận | Mọi story |

---

## 12. Chốt thông tin với các bên liên quan và giả định

> Yêu cầu: xác nhận với PO/BQT; ghi rõ giả định.

### 12.1. Xác nhận với các bên liên quan

| Bên liên quan | Nội dung cần xác nhận | Ngày xác nhận | Trạng thái | Phản hồi / Chỉnh sửa |
|---|---|---|---|---|
| Đại diện Ban quản trị | Danh sách Epic, User Story và Product Backlog | — | Chờ xác nhận | — |
| Đại diện Thủ quỹ | Quy trình thu phí và các Acceptance Criteria | — | Chờ xác nhận | — |
| Đại diện Cư dân | Nhu cầu tra cứu công nợ và thông báo phí | — | Chờ xác nhận | — |

### 12.2. Lưu ý và giả định của nhóm

1. Các phát biểu của Ban quản trị, Thủ quỹ, Cư dân và Chính quyền là **mô phỏng phỏng vấn** (chưa phỏng vấn thật); cần thay bằng dữ liệu thu thập thực tế nếu có. Sổ thu, sổ hộ khẩu, sổ quỹ và file Excel là tài liệu được phân tích.
2. Ở v1.0, Ban quản trị là đối tượng có tài khoản (RAW-025); Thủ quỹ và Nhân viên thu phí là người dùng nội bộ **(giả định, cần xác nhận)**. Việc phân vai Quản trị viên / Nhân viên thu phí (US-022) đòi hỏi các vai trò này cũng có tài khoản, nên cần PO làm rõ danh sách đối tượng được cấp tài khoản. Cư dân không có tài khoản; thông tin của họ được bảo vệ bằng phân quyền (RAW-027).
3. Ước lượng Story Point và phân Sprint là sơ bộ, cần nhóm chốt lại bằng Planning Poker.
4. Câu chuyện kỹ thuật (TS) và Definition of Done là ghi chú bổ sung theo hướng dẫn.
5. Các Acceptance Criteria **AC-027 → AC-072** được bổ sung để khớp dải mã trong Traceability Matrix (bản gốc chỉ có đến AC-026); nội dung được suy ra từ User Story và README dự án, cần nhóm và PO rà soát. Ngưỡng "tối đa 4 thao tác" trong AC-072 là giả định.
6. Thuật ngữ vai trò cần thống nhất khi hoàn thiện: "Ban quản trị", "Quản trị viên", "Thủ quỹ" và "Nhân viên thu phí" đang được dùng song song trong các User Story.
