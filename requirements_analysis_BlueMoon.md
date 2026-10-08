# BlueMoon -- Khai phá yêu cầu

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

## 6a_Epic

# 6a. Danh sách Epic

> Chuẩn hóa từ Wish List; Epic nhóm theo chủ đề.

| Epic ID | Epic Name | Mô tả ngắn |
|---|---|---|
| EPIC-01 | Quản lý hộ gia đình và nhân khẩu | Quản lý hộ khẩu, nhân khẩu, tạm trú/tạm vắng, báo cáo cho cơ quan chức năng |
| EPIC-02 | Quản lý danh mục và khoản thu | Tính phí tự động, tạo khoản thu tự nguyện, ghi nhận đóng góp, chốt công nợ |
| EPIC-03 | Thu phí và thanh toán | Tra cứu phí, ghi nhận thanh toán, biên lai, hủy phiếu, thông báo thu phí |
| EPIC-04 | Thống kê và báo cáo | Tổng thu, lọc nợ đọng, thống kê theo khoản, xuất Excel |
| EPIC-05 | Tài khoản, bảo mật và nền tảng hệ thống | Đăng ký, đăng nhập, phân quyền, đổi mật khẩu; kiến trúc, sao lưu, nhật ký thay đổi, giao diện |
| EPIC-06 | Phí gửi xe (v2.0, chưa làm ở v1.0) | Quản lý phương tiện và thu phí gửi xe hàng tháng |
| EPIC-07 | Thu hộ điện, nước, internet (v2.0, chưa làm ở v1.0) | Thu hộ các khoản dịch vụ tiện ích theo thông báo của nhà cung cấp |

## 6b_Feature

# 6b. Danh sách Feature

> Feature là chức năng cụ thể của Epic.

| Feature ID | Feature Name | Mô tả ngắn | Thuộc Epic ID |
|---|---|---|---|
| F-01 | Quản lý hộ gia đình | Thêm, sửa hộ và diện tích căn hộ | EPIC-01 |
| F-02 | Quản lý nhân khẩu | Thêm, sửa nhân khẩu; quan hệ với chủ hộ | EPIC-01 |
| F-03 | Tạm trú, tạm vắng | Ghi nhận tạm trú, tạm vắng với ngày bắt đầu và kết thúc | EPIC-01 |
| F-04 | Báo cáo nhân khẩu | Xuất danh sách tạm vắng, tạm trú cho cơ quan chức năng | EPIC-01 |
| F-05 | Tính phí tự động | Phí = diện tích × đơn giá cho phí dịch vụ và phí quản lý | EPIC-02 |
| F-06 | Quản lý khoản thu tự nguyện | Tạo khoản thu, ghi nhận đóng góp (kể cả 0đ), sửa giá trị | EPIC-02 |
| F-07 | Chốt công nợ hàng tháng | Tự tạo danh sách công nợ cho tất cả các phòng | EPIC-02 |
| F-08 | Tra cứu phí theo mã phòng | Hiện các khoản chưa đóng của phòng | EPIC-03 |
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

## 7_User_Story

# 7. Bộ User Story

> User Story được viết theo cấu trúc INVEST:
> **Là... tôi muốn... để...**
>
> TS = Technical Story.

| US ID | Tiêu đề | User Story | Epic / Feature | Nguồn | Ưu tiên | Phụ thuộc |
|---|---|---|---|---|---|---|
| US-001 | Quản lý hộ gia đình | Là Ban quản trị, tôi muốn thêm thông tin hộ gia đình và diện tích căn hộ khi cư dân mới chuyển đến, để quản lý cư dân và làm cơ sở tính phí. | EPIC-01 / F-01 | RAW-001 | Must | TS-003 |
| US-002 | Quản lý nhân khẩu | Là Ban quản trị, tôi muốn lưu thông tin nhân khẩu và quan hệ với chủ hộ, để quản lý chính xác thành viên từng hộ. | EPIC-01 / F-02 | RAW-002, RAW-003 | Must | US-001 |
| US-003 | Tạm trú, tạm vắng | Là Ban quản trị, tôi muốn ghi nhận tạm trú, tạm vắng kèm ngày bắt đầu và kết thúc, để theo dõi biến động nhân khẩu. | EPIC-01 / F-03 | RAW-004, RAW-005 | Must | US-002 |
| US-004 | Báo cáo nhân khẩu | Là Ban quản trị, tôi muốn xuất danh sách nhân khẩu tạm vắng, để cung cấp cho công an phường và cơ quan chức năng khi được yêu cầu. | EPIC-01 / F-04 | RAW-006, RAW-036 | Should | US-003 |
| US-005 | Tính phí tự động | Là Thủ quỹ, tôi muốn hệ thống tự tính phí dịch vụ và phí quản lý theo diện tích × đơn giá, để tránh tính tay sai sót. | EPIC-02 / F-05 | RAW-007, RAW-008 | Must | US-001 |
| US-006 | Tạo khoản thu tự nguyện | Là Ban quản trị, tôi muốn tự tạo khoản thu tự nguyện mới theo từng đợt (VD Quỹ vì người nghèo), để quản lý linh hoạt các quỹ phát sinh. | EPIC-02 / F-06 | RAW-009 | Must | |
| US-007 | Ghi nhận đóng góp tự nguyện | Là Thủ quỹ, tôi muốn ghi nhận số tiền đóng góp tùy ý (kể cả 0đ) cho nhiều quỹ của một hộ, để phản ánh đúng tinh thần tự nguyện. | EPIC-02 / F-06 | RAW-010, RAW-031 | Must | US-006 |
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
| US-024 | Phí gửi xe (v2.0) | Là Ban quản trị, tôi muốn quản lý biển số và tự tính phí gửi xe (70.000 đ/xe máy, 1.200.000 đ/ô tô), để thu đúng theo phương tiện (v2.0). | EPIC-06 / F-19 | RAW-029 | Won't | US-001 |
| US-025 | Thu hộ điện nước internet (v2.0) | Là Ban quản trị, tôi muốn nhập file thông báo từ điện lực, nhà mạng, để thu hộ điện, nước, internet (v2.0). | EPIC-07 / F-20 | RAW-030 | Won't | US-009 |
| TS-001 | Sao lưu dữ liệu | Câu chuyện kỹ thuật: sao lưu dữ liệu MySQL định kỳ (hàng ngày/tuần) để tránh mất dữ liệu. | EPIC-05 / F-21 | RAW-034 | Should | TS-003 |
| TS-002 | Nhật ký thao tác | Câu chuyện kỹ thuật: ghi nhật ký mọi thao tác thay đổi dữ liệu quan trọng. | EPIC-05 / F-21 | RAW-033, RAW-017 | Should | TS-003 |
| TS-003 | Nền tảng Java máy tính để bàn + MySQL | Câu chuyện kỹ thuật: thiết lập ứng dụng Java máy tính để bàn kết nối CSDL MySQL tập trung. | EPIC-05 / F-21 | RAW-024 | Must | |
| TS-004 | Giao diện thân thiện | Câu chuyện kỹ thuật: rà soát giao diện dễ dùng cho người lớn tuổi (chữ rõ, ít bước thao tác). | EPIC-05 / F-21 | RAW-037 | Could | |

## 8_AC

# 8. Acceptance Criteria

> Viết theo mô hình GWT (Given – When – Then), có thể kiểm thử được.

| AC ID | US ID | Giả sử (Given) | Khi (When) | Thì (Then) |
|---|---|---|---|---|
| AC-001 | US-001 | Giả sử BQT đã đăng nhập | Khi nhập đầy đủ mã căn hộ, tên chủ hộ, diện tích rồi lưu | Thì hệ thống tạo hộ gia đình mới và hiển thị trong danh sách |
| AC-002 | US-001 | Giả sử đang nhập thông tin căn hộ | Khi nhập diện tích căn hộ (m²) | Thì hệ thống lưu đúng giá trị diện tích đã nhập |
| AC-003 | US-001 | Giả sử thiếu thông tin bắt buộc | Khi nhấn lưu | Thì hệ thống báo các trường còn thiếu và không lưu |
| AC-004 | US-001 | Giả sử mã căn hộ đã tồn tại | Khi thêm hộ trùng mã căn hộ | Thì hệ thống báo trùng và không lưu |
| AC-005 | US-002 | Giả sử hộ đã tồn tại | Khi thêm nhân khẩu với họ tên, ngày sinh, giới tính, số CCCD | Thì hệ thống lưu nhân khẩu vào hộ và cập nhật số thành viên |
| AC-006 | US-002 | Giả sử đang khai báo nhân khẩu | Khi chọn quan hệ với chủ hộ (chủ hộ, vợ/chồng, con, khác) | Thì hệ thống lưu quan hệ đã chọn |
| AC-007 | US-002 | Giả sử hộ đã có chủ hộ | Khi khai báo thêm một chủ hộ thứ hai | Thì hệ thống từ chối và báo mỗi hộ chỉ có một chủ hộ |
| AC-008 | US-002 | Giả sử số CCCD đã tồn tại | Khi lưu nhân khẩu mới | Thì hệ thống cảnh báo trùng số CCCD |
| AC-009 | US-003 | Giả sử nhân khẩu thuộc một hộ | Khi ghi nhận tạm trú hoặc tạm vắng với ngày bắt đầu và ngày kết thúc | Thì hệ thống lưu và cập nhật trạng thái cư trú |
| AC-010 | US-003 | Giả sử đang ghi nhận tạm trú/tạm vắng | Khi để trống ngày bắt đầu hoặc ngày kết thúc | Thì hệ thống báo lỗi và không lưu |
| AC-011 | US-003 | Giả sử ngày kết thúc trước ngày bắt đầu | Khi lưu | Thì hệ thống từ chối |
| AC-012 | US-004 | Giả sử có nhân khẩu đang tạm vắng | Khi BQT chọn xuất danh sách tạm vắng | Thì hệ thống tạo danh sách gồm họ tên, căn hộ, thời gian tạm vắng |
| AC-013 | US-004 | Giả sử không có nhân khẩu tạm vắng | Khi xuất danh sách | Thì hệ thống thông báo không có dữ liệu |
| AC-014 | US-004 | Giả sử danh sách đã tạo | Khi chọn xuất file | Thì hệ thống tạo file Excel/PDF đủ các trường theo quy định báo cáo |
| AC-015 | US-005 | Giả sử căn hộ có diện tích và đơn giá phí dịch vụ trong khoảng 2.500–16.500 đ/m² | Khi hệ thống tính phí | Thì phí dịch vụ = diện tích × đơn giá |
| AC-016 | US-005 | Giả sử căn hộ có diện tích | Khi tính phí quản lý | Thì hệ thống dùng đơn giá mặc định 7.000 đ/m² (BQT có thể thay đổi) |
| AC-017 | US-005 | Giả sử đơn giá phí dịch vụ nằm ngoài khoảng 2.500–16.500 đ/m² | Khi lưu cấu hình | Thì hệ thống cảnh báo |
| AC-018 | US-005 | Giả sử dữ liệu tính phí hợp lệ | Khi xem khoản phí | Thì tổng tiền hiển thị chính xác |
| AC-019 | US-006 | Giả sử BQT ở màn hình quản lý khoản thu | Khi tạo khoản thu tự nguyện mới (tên, thời gian) | Thì khoản thu xuất hiện trong danh mục |
| AC-020 | US-006 | Giả sử tên khoản thu đã tồn tại trong cùng đợt | Khi lưu | Thì hệ thống báo trùng |
| AC-021 | US-006 | Giả sử tạo khoản thu tự nguyện | Khi không nhập số tiền cố định | Thì hệ thống vẫn cho lưu |
| AC-022 | US-007 | Giả sử khoản thu tự nguyện đang mở | Khi ghi nhận hộ đóng số tiền bất kỳ | Thì hệ thống lưu đúng số tiền |
| AC-023 | US-007 | Giả sử hộ không đóng quỹ | Khi ghi nhận 0đ | Thì hệ thống lưu trạng thái "không đóng", khác "chưa thu" |
| AC-024 | US-007 | Giả sử hộ đóng nhiều quỹ | Khi nhập các khoản trên cùng màn hình | Thì hệ thống lưu riêng từng khoản cho từng quỹ |
| AC-025 | US-008 | Giả sử khoản thu của hộ chưa có thanh toán | Khi thủ quỹ cập nhật giá trị | Thì hệ thống lưu giá trị mới |
| AC-026 | US-008 | Giả sử khoản thu đã có thanh toán | Khi cố sửa giá trị | Thì hệ thống từ chối và yêu cầu hủy phiếu thu trước |

# 9. Product Backlog

| ID | Mô tả ngắn | Ưu tiên | Sprint dự kiến | Trạng thái | Ước lượng (SP) | Epic |
|---|---|---|---|---|---:|---|
| EPIC-01 | Quản lý hộ gia đình và nhân khẩu | Must | Sprint 1–3 | Chưa bắt đầu | | EPIC-01 |
| US-001 | Thêm thông tin hộ gia đình và diện tích căn hộ khi cư dân mới chuyển đến | Must | Sprint 1 | Chưa bắt đầu | 3 | EPIC-01 |
| US-002 | Lưu thông tin nhân khẩu và quan hệ với chủ hộ | Must | Sprint 1 | Chưa bắt đầu | 5 | EPIC-01 |
| US-003 | Ghi nhận tạm trú, tạm vắng kèm ngày bắt đầu và kết thúc | Must | Sprint 1 | Chưa bắt đầu | 5 | EPIC-01 |
| US-004 | Xuất danh sách nhân khẩu tạm vắng | Should | Sprint 3 | Chưa bắt đầu | 3 | EPIC-01 |
| EPIC-02 | Quản lý danh mục và khoản thu | Must | Sprint 2–3 | Chưa bắt đầu | | EPIC-02 |
| US-005 | Hệ thống tự tính phí dịch vụ và phí quản lý theo diện tích × đơn giá | Must | Sprint 2 | Chưa bắt đầu | 5 | EPIC-02 |
| US-006 | Tự tạo khoản thu tự nguyện mới theo từng đợt | Must | Sprint 2 | Chưa bắt đầu | 3 | EPIC-02 |
| US-007 | Ghi nhận số tiền đóng góp tùy ý, kể cả 0đ, cho nhiều quỹ của một hộ | Must | Sprint 3 | Chưa bắt đầu | 5 | EPIC-02 |
| US-008 | Cập nhật lại giá trị khoản thu của một hộ trước khi hộ đóng | Should | Sprint 2 | Chưa bắt đầu | 3 | EPIC-02 |
| US-009 | Hệ thống tự tạo danh sách công nợ cho tất cả các phòng vào đầu mỗi tháng | Must | Sprint 2 | Chưa bắt đầu | 5 | EPIC-02 |
| EPIC-03 | Thu phí và thanh toán | Must | Sprint 3 | Chưa bắt đầu | | EPIC-03 |
| US-010 | Nhập mã phòng để xem ngay các khoản chưa đóng | Must | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| US-011 | Thu tất cả các khoản của hộ bằng một nút và hệ thống tự cộng tổng | Must | Sprint 3 | Chưa bắt đầu | 5 | EPIC-03 |
| US-012 | Chọn tiền mặt hoặc chuyển khoản và hệ thống tự lưu người thu | Must | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| US-013 | In biên lai từ phần mềm sau khi thu | Should | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| US-014 | Hủy phiếu thu nhầm trong ngày | Should | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| US-015 | In thông báo các khoản phí hàng tháng cho từng hộ | Should | Sprint 3 | Chưa bắt đầu | 3 | EPIC-03 |
| EPIC-04 | Thống kê và báo cáo | Should | Sprint 4 | Chưa bắt đầu | | EPIC-04 |
| US-016 | Xem tổng tiền đã thu trong tháng ngay trên màn hình chính | Should | Sprint 4 | Chưa bắt đầu | 5 | EPIC-04 |
| US-017 | Lọc danh sách hộ nợ phí quá 3 tháng | Should | Sprint 4 | Chưa bắt đầu | 3 | EPIC-04 |
| US-018 | Xuất báo cáo thu tiền ra Excel theo mẫu thủ công cũ | Could | Sprint 4 | Chưa bắt đầu | 5 | EPIC-04 |
| US-019 | Xem tổng thu của từng khoản/quỹ theo đợt | Should | Sprint 4 | Chưa bắt đầu | 3 | EPIC-04 |
| EPIC-05 | Tài khoản, bảo mật và nền tảng hệ thống | Must | Sprint 1–4 | Chưa bắt đầu | | EPIC-05 |
| US-020 | Đăng ký tài khoản đăng nhập | Must | Sprint 1 | Chưa bắt đầu | 3 | EPIC-05 |
| US-021 | Đăng nhập bằng tên đăng nhập và mật khẩu | Must | Sprint 1 | Chưa bắt đầu | 3 | EPIC-05 |
| US-022 | Phân quyền Quản trị viên và Nhân viên thu phí | Should | Sprint 2 | Chưa bắt đầu | 5 | EPIC-05 |
| US-023 | Đổi mật khẩu và cập nhật thông tin cá nhân | Should | Sprint 2 | Chưa bắt đầu | 3 | EPIC-05 |
| TS-001 | Sao lưu dữ liệu MySQL định kỳ | Should | Sprint 4 | Chưa bắt đầu | 5 | EPIC-05 |
| TS-002 | Ghi nhật ký mọi thao tác thay đổi dữ liệu quan trọng | Should | Sprint 4 | Chưa bắt đầu | 5 | EPIC-05 |
| TS-003 | Thiết lập ứng dụng Java máy tính để bàn kết nối CSDL MySQL tập trung | Must | Sprint 1 | Chưa bắt đầu | 5 | EPIC-05 |
| TS-004 | Rà soát giao diện dễ dùng cho người lớn tuổi | Could | Sprint 4 | Chưa bắt đầu | 2 | EPIC-05 |
| EPIC-06 | Phí gửi xe (v2.0, chưa làm ở v1.0) | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | | EPIC-06 |
| US-024 | Quản lý biển số và tự tính phí gửi xe (70.000 đ/xe máy, 1.200.000 đ/ô tô) | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | 0 | EPIC-06 |
| EPIC-07 | Thu hộ điện, nước, internet (v2.0, chưa làm ở v1.0) | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | | EPIC-07 |
| US-025 | Import file thông báo từ điện lực, nhà mạng | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | 0 | EPIC-07 |

# 10. Traceability Matrix

> Luồng truy vết: RAW → Epic → Feature → User Story → Acceptance Criteria.

| RAW ID | Epic | Feature | User Story | Acceptance Criteria | Ghi chú | Đã truy vết? |
|---|---|---|---|---|---|---|
| RAW-001 | EPIC-01 | F-01 | US-001 | AC-001–004 | Cốt lõi để tính phí sau này | Có |
| RAW-002 | EPIC-01 | F-02 | US-002 | AC-005–008 | Cấu trúc dữ liệu từ sổ hộ khẩu | Có |
| RAW-003 | EPIC-01 | F-02 | US-002 | AC-005–008 | Cần danh sách chọn có sẵn | Có |
| RAW-004 | EPIC-01 | F-03 | US-003 | AC-009–011 | | Có |
| RAW-005 | EPIC-01 | F-03 | US-003 | AC-009–011 | Kiểm tra dữ liệu đầu vào | Có |
| RAW-006 | EPIC-01 | F-04 | US-004 | AC-012–014 | Yêu cầu từ cơ quan chức năng | Có |
| RAW-007 | EPIC-02 | F-05 | US-005 | AC-015–018 | Tránh tính tay sai sót | Có |
| RAW-008 | EPIC-02 | F-05 | US-005 | AC-015–018 | | Có |
| RAW-009 | EPIC-02 | F-06 | US-006 | AC-019–021 | Danh mục động | Có |
| RAW-010 | EPIC-02 | F-06 | US-007 | AC-022–024 | Xử lý số tiền linh hoạt | Có |
| RAW-011 | EPIC-02 | F-07 | US-009 | AC-027–029 | | Có |
| RAW-012 | EPIC-02 | F-06 | US-008 | AC-025–026 | | Có |
| RAW-013 | EPIC-03 | F-08 | US-010 | AC-030–032 | Tối ưu thời gian thu | Có |
| RAW-014 | EPIC-03 | F-09 | US-011 | AC-033–035 | Trải nghiệm thao tác | Có |
| RAW-015 | EPIC-03 | F-09 | US-012 | AC-036–038 | Phục vụ đối soát | Có |
| RAW-016 | EPIC-03 | F-10 | US-013 | AC-039–040 | | Có |
| RAW-017 | EPIC-03, EPIC-05 | F-09, F-21 | US-012, TS-002 | AC-036–038; AC-068–069 | Truy vết trách nhiệm | Có |
| RAW-018 | EPIC-03 | F-09 | US-011 | AC-033–035 | Giải quyết điểm đau | Có |
| RAW-019 | EPIC-03 | F-10 | US-014 | AC-041–043 | | Có |
| RAW-020 | EPIC-04 | F-12 | US-016 | AC-046–047 | | Có |
| RAW-021 | EPIC-04 | F-13 | US-017 | AC-048–049 | | Có |
| RAW-022 | EPIC-04 | F-14 | US-018 | AC-050–051 | Khớp với quy trình cũ | Có |
| RAW-023 | EPIC-04 | F-15 | US-019 | AC-052–053 | | Có |
| RAW-024 | EPIC-05 | F-21 | TS-003 | AC-070–071 | Ràng buộc kỹ thuật | Có |
| RAW-025 | EPIC-05 | F-17 | US-021 | AC-057–059 | Yêu cầu bảo mật cơ bản | Có |
| RAW-026 | EPIC-05 | F-18 | US-023 | AC-063–065 | | Có |
| RAW-027 | EPIC-05 | F-17 | US-022 | AC-060–062 | Phân quyền, quyền riêng tư | Có |
| RAW-028 | EPIC-03 | F-08 | US-010 | AC-030–032 | Yêu cầu hiệu năng | Có |
| RAW-029 | EPIC-06 | F-19 | US-024 | US-024: chưa viết (v2.0) | Lộ trình v2.0 | Có |
| RAW-030 | EPIC-07 | F-20 | US-025 | US-025: chưa viết (v2.0) | Lộ trình v2.0 | Có |
| RAW-031 | EPIC-02 | F-06 | US-007 | AC-022–024 | Phân tích ảnh sổ thu | Có |
| RAW-032 | EPIC-03 | F-10 | US-013 | AC-039–040 | Điểm đau từ sổ thu | Có |
| RAW-033 | EPIC-05 | F-21 | TS-002 | AC-068–069 | Nhu cầu ẩn: nhật ký thay đổi | Có |
| RAW-034 | EPIC-05 | F-21 | TS-001 | AC-066–067 | | Có |
| RAW-035 | EPIC-05 | F-17 | US-022 | AC-060–062 | | Có |
| RAW-036 | EPIC-01 | F-04 | US-004 | AC-012–014 | Yêu cầu pháp lý | Có |
| RAW-037 | EPIC-05 | F-21 | TS-004 | AC-072 | Khả dụng | Có |
| RAW-038 | EPIC-03 | F-11 | US-015 | AC-044–045 | Quy trình hiện tại | Có |
| RAW-039 | EPIC-05 | F-16 | US-020 | AC-054–056 | Luồng nghiệp vụ 1 | Có |
| RAW-040 | EPIC-05 | F-18 | US-023 | AC-063–065 | | Có |

# 11. Definition of Done

| # | Tiêu chí hoàn thành (Definition of Done) | Áp dụng cho |
|---:|---|---|
| 1 | Mã nguồn hoàn thành và được đồng nghiệp xem xét (Code Review) | Mọi story |
| 2 | Kiểm thử đơn vị (Unit Test) đạt yêu cầu | Mọi story |
| 3 | Kiểm thử tích hợp (Integration Test) thành công | Mọi story |
| 4 | Tất cả Acceptance Criteria của story được kiểm thử đạt | Mọi story |
| 5 | Dữ liệu được lưu đúng trên MySQL Server | Story có ghi dữ liệu |
| 6 | Triển khai thành công lên môi trường thử nghiệm (Staging) | Mọi story |
| 7 | Đại diện Ban quản trị nghiệm thu (UAT) và xác nhận | Mọi story |

---

# Chốt thông tin với các bên liên quan và giả định

> Yêu cầu: xác nhận với PO/BQT; ghi rõ giả định.

## Các bên liên quan

| Bên liên quan / PO | Nội dung xác nhận | Ngày | Phản hồi / Chỉnh sửa |
|---|---|---|---|
| Đại diện Ban quản trị | Xác nhận danh sách Epic, User Story và Product Backlog | | |
| Đại diện Thủ quỹ | Xác nhận quy trình thu phí và các Acceptance Criteria | | |
| Đại diện Cư dân | Xác nhận nhu cầu tra cứu công nợ và thông báo phí | | |

## Lưu ý và giả định của nhóm

1. Các phát biểu của Ban quản trị, Thủ quỹ, Cư dân, Chính quyền là mô phỏng phỏng vấn (chưa phỏng vấn thật); cần thay bằng dữ liệu thu thập thật nếu có. Sổ thu, sổ hộ khẩu, sổ quỹ, file Excel là tài liệu được phân tích.

2. v1.0 chỉ Ban quản trị có tài khoản (RAW-025); Thủ quỹ và Nhân viên thu phí là người dùng nội bộ (giả định cần xác nhận). Cư dân không có tài khoản, nên thông tin của họ được bảo vệ bằng phân quyền (RAW-027).

3. Ước lượng điểm Story Point và phân Sprint là ước lượng sơ bộ, cần nhóm chốt lại bằng họp ước lượng (Planning Poker).

4. Câu chuyện kỹ thuật (TS) và Tiêu chí hoàn thành (Definition of Done) là ghi chú bổ sung theo hướng dẫn.
