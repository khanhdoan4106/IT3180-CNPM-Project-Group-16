# BlueMoon -- Sản phẩm giao nộp

> Chuyển đổi toàn bộ nội dung từ file
> `BlueMoon_San_pham_giao_nop (2).xlsx` sang Markdown.

---

## Mục lục

| STT | Sản phẩm | Sheet | Thuộc |
|---:|---|---|---|
| 1 | Client's Wish List | `1_Wish_List` | Bước 2 |
| 2 | As-Is Scenario | `2_As_Is` | Bước 2 |
| 3 | Visionary Scenario | `3_Visionary` | Bước 2 |
| 4 | Danh sách NFR thô | `4_NFR` | Bước 2 |
| 5 | Raw Log | `5_Raw_Log` | Bước 2 |
| 6a | Danh sách Epic | `6a_Epic` | Bước 3 |
| 6b | Danh sách Feature | `6b_Feature` | Bước 3 |
| 7 | Bộ User Story | `7_User_Story` | Bước 3 |
| 8 | Acceptance Criteria | `8_AC` | Bước 3 |
| 9 | Product Backlog | `9_Backlog` | Bước 3 |
| 10 | Traceability Matrix | `10_Traceability` | Bước 3 |
| 11 | Definition of Done | `11_DoD` | Ghi chú bổ sung |
| - | Chốt xác nhận và giả định | `Chot_va_Gia_dinh` | Bước 3 |

---

# 1. Client's Wish List

## Yêu cầu

Mong muốn thô từ Ban quản trị, cư dân, thủ quỹ và chính quyền; nguyên văn, chưa lọc.

| ID | Nguồn | Mong muốn thô | Ghi chú |
|---|---|---|---|
| RAW-001 | Ban quản trị | Tôi muốn thêm mới thông tin một hộ gia đình và diện tích căn hộ khi họ bắt đầu chuyển đến. | Cốt lõi để tính phí sau này |
| RAW-002 | Sổ hộ khẩu (tài liệu) | Cần lưu trữ các thông tin cơ bản từ sổ hộ khẩu | Cấu trúc dữ liệu từ sổ hộ khẩu |
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

---

# 2. As-Is Scenario

## Hiện trạng

Hiện tại công tác quản lý chung cư chủ yếu được thực hiện thủ công hoặc sử dụng các file Excel riêng lẻ.

### Quản lý hộ gia đình

- Thông tin hộ gia đình được ghi nhận thủ công.
- Thông tin căn hộ và diện tích cần được nhập và quản lý.
- Việc cập nhật thông tin cư dân có thể phát sinh sai sót.
- Chưa có hệ thống tập trung để quản lý dữ liệu.

### Quản lý nhân khẩu

- Thông tin thành viên trong hộ được lưu trữ dựa trên hồ sơ hiện có.
- Việc theo dõi quan hệ giữa các thành viên còn thủ công.
- Thông tin tạm trú, tạm vắng cần được cập nhật và tra cứu khi có yêu cầu.

### Quản lý phí

Quy trình hiện tại:

1. Xác định các hộ cần thu.
2. Tính phí dựa trên diện tích và đơn giá.
3. Lập danh sách công nợ.
4. Thu tiền từ cư dân.
5. Ghi nhận khoản thu.
6. In hoặc lập biên lai.
7. Tổng hợp báo cáo.

### Các vấn đề tồn tại

- Tính toán thủ công dễ xảy ra sai sót.
- Dữ liệu phân tán.
- Khó tra cứu nhanh thông tin công nợ.
- Khó kiểm soát lịch sử thay đổi dữ liệu.
- Việc lập báo cáo mất nhiều thời gian.
- Khó phân quyền người sử dụng.
- Có nguy cơ mất dữ liệu nếu không sao lưu.
- Khó truy vết người thực hiện thao tác.

---

# 3. Visionary Scenario

## Tầm nhìn hệ thống

BlueMoon hướng tới xây dựng một hệ thống quản lý chung cư tập trung, hỗ trợ Ban quản trị và Thủ quỹ quản lý cư dân, tính phí, thu phí và lập báo cáo.

### Mục tiêu

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

### Trạng thái mong muốn

Sau khi triển khai hệ thống:

> Ban quản trị có thể quản lý toàn bộ thông tin chung cư trên một hệ thống thống nhất, giảm thao tác thủ công, hạn chế sai sót và nâng cao khả năng tra cứu, đối soát và báo cáo.

---

# 4. Non-functional Requirements

| ID | Yêu cầu | Mô tả |
|---|---|---|
| NFR-001 | Hiệu năng | Các thao tác tra cứu thông thường phải phản hồi nhanh. |
| NFR-002 | Bảo mật | Chỉ người dùng được cấp quyền mới có thể truy cập dữ liệu tương ứng. |
| NFR-003 | Sao lưu | Dữ liệu cần được sao lưu định kỳ. |
| NFR-004 | Khả năng sử dụng | Giao diện rõ ràng, dễ sử dụng, hạn chế số bước thao tác. |
| NFR-005 | Tính toàn vẹn dữ liệu | Hệ thống phải kiểm tra dữ liệu đầu vào và hạn chế dữ liệu không hợp lệ. |

---

# 5. Raw Log

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
| RAW-030 | Thu hộ điện nước internet | Ban quản trị |
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
