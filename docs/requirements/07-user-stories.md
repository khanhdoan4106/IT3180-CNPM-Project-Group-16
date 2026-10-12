# 07. User Stories và Acceptance Criteria

> Nguồn: `BlueMoon_San_pham_giao_nop_2.xlsx`, sheet `7_User_Story và 8_AC`. 25 User Story (US) và 4 câu chuyện kỹ thuật (TS), kèm 72 Acceptance Criteria dạng Given-When-Then (Giả sử, Khi, Thì).
>
> Ước lượng (SP) lấy từ [08-product-backlog.md](08-product-backlog.md). Vì sheet gốc đặt AC riêng, mỗi AC ở đây vẫn giữ nguyên mã `AC-xxx` để khớp với [ma trận truy vết](09-traceability-matrix.md).

## EPIC-01: Quản lý hộ gia đình và nhân khẩu

### US-001: Quản lý hộ gia đình

Là Ban quản trị, tôi muốn thêm thông tin hộ gia đình và diện tích căn hộ khi cư dân mới chuyển đến, để quản lý cư dân và làm cơ sở tính phí.

- **Epic / Feature:** EPIC-01 / F-01
- **Nguồn:** RAW-001
- **Ưu tiên:** Must
- **Ước lượng:** 3 SP
- **Phụ thuộc:** TS-003

**Acceptance Criteria**

- **AC-001:** **Giả sử** BQT đã đăng nhập, **khi** nhập đầy đủ mã căn hộ, tên chủ hộ, diện tích rồi lưu, **thì** hệ thống tạo hộ gia đình mới và hiển thị trong danh sách.
- **AC-002:** **Giả sử** đang nhập thông tin căn hộ, **khi** nhập diện tích căn hộ (m²), **thì** hệ thống lưu đúng giá trị diện tích đã nhập.
- **AC-003:** **Giả sử** thiếu thông tin bắt buộc, **khi** nhấn lưu, **thì** hệ thống báo các trường còn thiếu và không lưu.
- **AC-004:** **Giả sử** mã căn hộ đã tồn tại, **khi** thêm hộ trùng mã căn hộ, **thì** hệ thống báo trùng và không lưu.

### US-002: Quản lý nhân khẩu

Là Ban quản trị, tôi muốn lưu thông tin nhân khẩu và quan hệ với chủ hộ, để quản lý chính xác thành viên từng hộ.

- **Epic / Feature:** EPIC-01 / F-02
- **Nguồn:** RAW-002, RAW-003
- **Ưu tiên:** Must
- **Ước lượng:** 5 SP
- **Phụ thuộc:** US-001

**Acceptance Criteria**

- **AC-005:** **Giả sử** hộ đã tồn tại, **khi** thêm nhân khẩu với họ tên, ngày sinh, giới tính, số CCCD, **thì** hệ thống lưu nhân khẩu vào hộ và cập nhật số thành viên.
- **AC-006:** **Giả sử** đang khai báo nhân khẩu, **khi** chọn quan hệ với chủ hộ (chủ hộ, vợ/chồng, con, khác), **thì** hệ thống lưu quan hệ đã chọn.
- **AC-007:** **Giả sử** hộ đã có chủ hộ, **khi** khai báo thêm một chủ hộ thứ hai, **thì** hệ thống từ chối và báo mỗi hộ chỉ có một chủ hộ.
- **AC-008:** **Giả sử** số CCCD đã tồn tại, **khi** lưu nhân khẩu mới, **thì** hệ thống cảnh báo trùng số CCCD.

### US-003: Tạm trú, tạm vắng

Là Ban quản trị, tôi muốn ghi nhận tạm trú, tạm vắng kèm ngày bắt đầu và kết thúc, để theo dõi biến động nhân khẩu.

- **Epic / Feature:** EPIC-01 / F-03
- **Nguồn:** RAW-004, RAW-005
- **Ưu tiên:** Must
- **Ước lượng:** 5 SP
- **Phụ thuộc:** US-002

**Acceptance Criteria**

- **AC-009:** **Giả sử** nhân khẩu thuộc một hộ, **khi** ghi nhận tạm trú hoặc tạm vắng với ngày bắt đầu và ngày kết thúc, **thì** hệ thống lưu và cập nhật trạng thái cư trú.
- **AC-010:** **Giả sử** đang ghi nhận tạm trú/tạm vắng, **khi** để trống ngày bắt đầu hoặc ngày kết thúc, **thì** hệ thống báo lỗi và không lưu.
- **AC-011:** **Giả sử** ngày kết thúc trước ngày bắt đầu, **khi** lưu, **thì** hệ thống từ chối.

### US-004: Báo cáo nhân khẩu

Là Ban quản trị, tôi muốn xuất danh sách nhân khẩu tạm vắng, để cung cấp cho công an phường và cơ quan chức năng khi được yêu cầu.

- **Epic / Feature:** EPIC-01 / F-04
- **Nguồn:** RAW-006, RAW-036
- **Ưu tiên:** Should
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-003

**Acceptance Criteria**

- **AC-012:** **Giả sử** có nhân khẩu đang tạm vắng, **khi** BQT chọn xuất danh sách tạm vắng, **thì** hệ thống tạo danh sách gồm họ tên, căn hộ, thời gian tạm vắng.
- **AC-013:** **Giả sử** không có nhân khẩu tạm vắng, **khi** xuất danh sách, **thì** hệ thống thông báo không có dữ liệu.
- **AC-014:** **Giả sử** danh sách đã tạo, **khi** chọn xuất file, **thì** hệ thống tạo file Excel/PDF đủ các trường theo quy định báo cáo.

## EPIC-02: Quản lý danh mục và khoản thu

### US-005: Tính phí tự động

Là Thủ quỹ, tôi muốn hệ thống tự tính phí dịch vụ và phí quản lý theo diện tích × đơn giá, để tránh tính tay sai sót.

- **Epic / Feature:** EPIC-02 / F-05
- **Nguồn:** RAW-007, RAW-008
- **Ưu tiên:** Must
- **Ước lượng:** 5 SP
- **Phụ thuộc:** US-001

**Acceptance Criteria**

- **AC-015:** **Giả sử** căn hộ có diện tích và đơn giá phí dịch vụ trong khoảng 2.500–16.500 đ/m², **khi** hệ thống tính phí, **thì** phí dịch vụ = diện tích × đơn giá.
- **AC-016:** **Giả sử** căn hộ có diện tích, **khi** tính phí quản lý, **thì** hệ thống dùng đơn giá mặc định 7.000 đ/m² (BQT có thể thay đổi).
- **AC-017:** **Giả sử** đơn giá phí dịch vụ nằm ngoài khoảng 2.500–16.500 đ/m², **khi** lưu cấu hình, **thì** hệ thống cảnh báo.
- **AC-018:** **Giả sử** dữ liệu tính phí hợp lệ, **khi** xem khoản phí, **thì** tổng tiền hiển thị chính xác.

### US-006: Tạo khoản thu tự nguyện

Là Ban quản trị, tôi muốn tự tạo khoản thu tự nguyện mới theo từng đợt (VD Quỹ vì người nghèo), để quản lý linh hoạt các quỹ phát sinh.

- **Epic / Feature:** EPIC-02 / F-06
- **Nguồn:** RAW-009
- **Ưu tiên:** Must
- **Ước lượng:** 3 SP
- **Phụ thuộc:** (không)

**Acceptance Criteria**

- **AC-019:** **Giả sử** BQT ở màn hình quản lý khoản thu, **khi** tạo khoản thu tự nguyện mới (tên, thời gian), **thì** khoản thu xuất hiện trong danh mục.
- **AC-020:** **Giả sử** tên khoản thu đã tồn tại trong cùng đợt, **khi** lưu, **thì** hệ thống báo trùng.
- **AC-021:** **Giả sử** tạo khoản thu tự nguyện, **khi** không nhập số tiền cố định, **thì** hệ thống vẫn cho lưu.

### US-007: Ghi nhận đóng góp tự nguyện

Là Thủ quỹ, tôi muốn ghi nhận số tiền đóng góp tùy ý (kể cả 0đ) cho nhiều quỹ của một hộ, để phản ánh đúng tinh thần tự nguyện.

- **Epic / Feature:** EPIC-02 / F-06
- **Nguồn:** RAW-010, RAW-031
- **Ưu tiên:** Must
- **Ước lượng:** 5 SP
- **Phụ thuộc:** US-006

**Acceptance Criteria**

- **AC-022:** **Giả sử** khoản thu tự nguyện đang mở, **khi** ghi nhận hộ đóng số tiền bất kỳ, **thì** hệ thống lưu đúng số tiền.
- **AC-023:** **Giả sử** hộ không đóng quỹ, **khi** ghi nhận 0đ, **thì** hệ thống lưu trạng thái "không đóng", khác "chưa thu".
- **AC-024:** **Giả sử** hộ đóng nhiều quỹ, **khi** nhập các khoản trên cùng màn hình, **thì** hệ thống lưu riêng từng khoản cho từng quỹ.

### US-008: Sửa khoản thu

Là Thủ quỹ, tôi muốn cập nhật lại giá trị khoản thu của một hộ trước khi hộ đóng, để sửa kịp thời khi nhập sai.

- **Epic / Feature:** EPIC-02 / F-06
- **Nguồn:** RAW-012
- **Ưu tiên:** Should
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-006

**Acceptance Criteria**

- **AC-025:** **Giả sử** khoản thu của hộ chưa có thanh toán, **khi** thủ quỹ cập nhật giá trị, **thì** hệ thống lưu giá trị mới.
- **AC-026:** **Giả sử** khoản thu đã có thanh toán, **khi** cố sửa giá trị, **thì** hệ thống từ chối và yêu cầu hủy phiếu thu trước.

### US-009: Chốt công nợ

Là Thủ quỹ, tôi muốn hệ thống tự tạo danh sách công nợ cho tất cả các phòng vào đầu mỗi tháng, để có danh sách đi thu mà không phải lập tay.

- **Epic / Feature:** EPIC-02 / F-07
- **Nguồn:** RAW-011
- **Ưu tiên:** Must
- **Ước lượng:** 5 SP
- **Phụ thuộc:** US-005, US-006

**Acceptance Criteria**

- **AC-027:** **Giả sử** đầu tháng và đã có danh mục phí, **khi** hệ thống chốt công nợ, **thì** tạo danh sách khoản phải thu cho tất cả các phòng.
- **AC-028:** **Giả sử** công nợ tháng đã được tạo, **khi** chốt lại cùng tháng, **thì** hệ thống không tạo trùng.
- **AC-029:** **Giả sử** hộ chưa có diện tích, **khi** chốt công nợ, **thì** hệ thống cảnh báo và bỏ qua hộ đó.

## EPIC-03: Thu phí và thanh toán

### US-010: Tra cứu phí

Là Thủ quỹ, tôi muốn nhập mã phòng để xem ngay các khoản chưa đóng, để thu tiền nhanh khi cư dân đến đóng.

- **Epic / Feature:** EPIC-03 / F-08
- **Nguồn:** RAW-013, RAW-028
- **Ưu tiên:** Must
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-001, US-009

**Acceptance Criteria**

- **AC-030:** **Giả sử** mã phòng tồn tại, **khi** thủ quỹ nhập mã phòng, **thì** hệ thống hiển thị thông tin hộ và các khoản chưa đóng.
- **AC-031:** **Giả sử** mã phòng không tồn tại, **khi** tìm kiếm, **thì** hệ thống báo "Không tìm thấy phòng".
- **AC-032:** **Giả sử** dữ liệu bình thường, **khi** tìm kiếm theo mã phòng, **thì** kết quả hiển thị trong dưới 3 giây.

### US-011: Thu tất cả các khoản

Là Thủ quỹ, tôi muốn thu tất cả các khoản của hộ bằng một nút và hệ thống tự cộng tổng, để thu nhanh và không cộng nhầm.

- **Epic / Feature:** EPIC-03 / F-09
- **Nguồn:** RAW-014, RAW-018
- **Ưu tiên:** Must
- **Ước lượng:** 5 SP
- **Phụ thuộc:** US-010

**Acceptance Criteria**

- **AC-033:** **Giả sử** hộ có nhiều khoản chưa đóng, **khi** thủ quỹ nhấn "Thu tất cả", **thì** hệ thống ghi nhận thu toàn bộ các khoản và công nợ về 0.
- **AC-034:** **Giả sử** thủ quỹ chỉ chọn một số khoản, **khi** xác nhận thu, **thì** hệ thống chỉ ghi nhận các khoản được chọn.
- **AC-035:** **Giả sử** giao dịch đã ghi nhận, **khi** xem tổng tiền, **thì** tổng tự cộng chính xác theo các khoản và có ngày thu.

### US-012: Hình thức thanh toán và người thu

Là Thủ quỹ, tôi muốn chọn tiền mặt hoặc chuyển khoản và hệ thống tự lưu người thu, để đối soát và truy vết trách nhiệm.

- **Epic / Feature:** EPIC-03 / F-09
- **Nguồn:** RAW-015, RAW-017
- **Ưu tiên:** Must
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-011

**Acceptance Criteria**

- **AC-036:** **Giả sử** đang thu tiền, **khi** chọn "Tiền mặt" hoặc "Chuyển khoản", **thì** hệ thống lưu hình thức thanh toán.
- **AC-037:** **Giả sử** giao dịch được xác nhận, **khi** lưu, **thì** hệ thống tự động lưu tên tài khoản thu ngân và thời điểm.
- **AC-038:** **Giả sử** chưa chọn hình thức thanh toán, **khi** xác nhận, **thì** hệ thống báo lỗi.

### US-013: In biên lai

Là Thủ quỹ, tôi muốn in biên lai từ phần mềm sau khi thu, để cư dân có chứng từ đối chiếu.

- **Epic / Feature:** EPIC-03 / F-10
- **Nguồn:** RAW-016, RAW-032
- **Ưu tiên:** Should
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-011

**Acceptance Criteria**

- **AC-039:** **Giả sử** giao dịch thu đã ghi nhận, **khi** thủ quỹ chọn in biên lai, **thì** hệ thống tạo biên lai gồm mã biên lai, hộ, khoản, số tiền, ngày thu, người thu.
- **AC-040:** **Giả sử** biên lai đã in, **khi** in lại, **thì** hệ thống ghi rõ "bản in lại".

### US-014: Hủy phiếu thu

Là Thủ quỹ, tôi muốn hủy phiếu thu nhầm trong ngày, để sửa sai mà vẫn truy vết được.

- **Epic / Feature:** EPIC-03 / F-10
- **Nguồn:** RAW-019
- **Ưu tiên:** Should
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-011

**Acceptance Criteria**

- **AC-041:** **Giả sử** giao dịch được tạo trong ngày, **khi** thủ quỹ hủy phiếu thu và nhập lý do, **thì** hệ thống hủy giao dịch và khôi phục công nợ.
- **AC-042:** **Giả sử** giao dịch của ngày trước, **khi** cố hủy, **thì** hệ thống từ chối.
- **AC-043:** **Giả sử** phiếu đã hủy, **khi** xem lịch sử, **thì** hệ thống vẫn lưu vết phiếu hủy kèm người hủy.

### US-015: Thông báo thu phí

Là Ban quản trị, tôi muốn in thông báo các khoản phí hàng tháng cho từng hộ, để cư dân biết rõ khoản phải đóng.

- **Epic / Feature:** EPIC-03 / F-11
- **Nguồn:** RAW-038
- **Ưu tiên:** Should
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-009

**Acceptance Criteria**

- **AC-044:** **Giả sử** công nợ tháng đã chốt, **khi** chọn in thông báo cho hộ, **thì** hệ thống tạo thông báo liệt kê các khoản và số tiền cần đóng.
- **AC-045:** **Giả sử** hộ không có khoản phải đóng, **khi** in thông báo, **thì** hệ thống báo không có khoản cần đóng.

## EPIC-04: Thống kê và báo cáo

### US-016: Tổng thu tháng

Là Ban quản trị, tôi muốn xem tổng tiền đã thu trong tháng ngay trên màn hình chính, để nắm nhanh tình hình thu.

- **Epic / Feature:** EPIC-04 / F-12
- **Nguồn:** RAW-020
- **Ưu tiên:** Should
- **Ước lượng:** 5 SP
- **Phụ thuộc:** US-011

**Acceptance Criteria**

- **AC-046:** **Giả sử** có dữ liệu thu của tháng, **khi** BQT mở màn hình chính, **thì** hệ thống hiển thị tổng số tiền đã thu trong tháng.
- **AC-047:** **Giả sử** tháng chưa có giao dịch, **khi** mở màn hình chính, **thì** tổng thu hiển thị 0.

### US-017: Lọc nợ đọng

Là Ban quản trị, tôi muốn lọc danh sách hộ nợ phí quá 3 tháng, để nhắc nhở kịp thời.

- **Epic / Feature:** EPIC-04 / F-13
- **Nguồn:** RAW-021
- **Ưu tiên:** Should
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-009, US-011

**Acceptance Criteria**

- **AC-048:** **Giả sử** có hộ nợ phí, **khi** BQT lọc "nợ quá 3 tháng", **thì** hệ thống chỉ hiển thị các hộ nợ từ 3 tháng trở lên.
- **AC-049:** **Giả sử** danh sách đã lọc, **khi** xem danh sách, **thì** hiển thị mã phòng, chủ hộ, số tháng nợ, tổng tiền nợ.

### US-018: Xuất báo cáo Excel

Là Thủ quỹ, tôi muốn xuất báo cáo thu tiền ra Excel theo mẫu thủ công cũ, để lưu trữ và đối chiếu với quy trình cũ.

- **Epic / Feature:** EPIC-04 / F-14
- **Nguồn:** RAW-022
- **Ưu tiên:** Could
- **Ước lượng:** 5 SP
- **Phụ thuộc:** US-011

**Acceptance Criteria**

- **AC-050:** **Giả sử** có dữ liệu thu, **khi** thủ quỹ chọn xuất Excel, **thì** hệ thống tạo file có các cột giống mẫu thủ công cũ.
- **AC-051:** **Giả sử** kỳ chưa có giao dịch, **khi** xuất báo cáo, **thì** hệ thống thông báo không có dữ liệu.

### US-019: Thống kê theo khoản thu

Là Ban quản trị, tôi muốn xem tổng thu của từng khoản/quỹ theo đợt, để báo cáo kết quả từng quỹ.

- **Epic / Feature:** EPIC-04 / F-15
- **Nguồn:** RAW-023
- **Ưu tiên:** Should
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-007, US-011

**Acceptance Criteria**

- **AC-052:** **Giả sử** có nhiều khoản thu, **khi** BQT chọn một khoản (VD quỹ vì biển đảo) và đợt thu, **thì** hệ thống hiển thị tổng thu của khoản đó.
- **AC-053:** **Giả sử** khoản thu chưa có giao dịch, **khi** thống kê, **thì** tổng thu bằng 0.

## EPIC-05: Tài khoản, bảo mật và nền tảng hệ thống

### US-020: Đăng ký tài khoản

Là Ban quản trị, tôi muốn đăng ký tài khoản đăng nhập, để người có quyền được dùng hệ thống.

- **Epic / Feature:** EPIC-05 / F-16
- **Nguồn:** RAW-039
- **Ưu tiên:** Must
- **Ước lượng:** 3 SP
- **Phụ thuộc:** TS-003

**Acceptance Criteria**

- **AC-054:** **Giả sử** thông tin đăng ký hợp lệ (tên đăng nhập, mật khẩu), **khi** đăng ký, **thì** hệ thống tạo tài khoản.
- **AC-055:** **Giả sử** tên đăng nhập đã tồn tại, **khi** đăng ký, **thì** hệ thống báo trùng.
- **AC-056:** **Giả sử** mật khẩu không đạt độ dài tối thiểu, **khi** đăng ký, **thì** hệ thống báo lỗi.

### US-021: Đăng nhập

Là Ban quản trị, tôi muốn đăng nhập bằng tên đăng nhập và mật khẩu, để chỉ người có tài khoản truy cập được dữ liệu.

- **Epic / Feature:** EPIC-05 / F-17
- **Nguồn:** RAW-025
- **Ưu tiên:** Must
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-020

**Acceptance Criteria**

- **AC-057:** **Giả sử** tài khoản hợp lệ, **khi** nhập đúng tên đăng nhập và mật khẩu, **thì** vào được hệ thống.
- **AC-058:** **Giả sử** sai mật khẩu, **khi** đăng nhập, **thì** hệ thống từ chối, không truy cập được dữ liệu.
- **AC-059:** **Giả sử** chưa đăng nhập, **khi** truy cập chức năng quản lý, **thì** hệ thống yêu cầu đăng nhập.

### US-022: Phân quyền

Là Quản trị viên, tôi muốn phân quyền Quản trị viên và Nhân viên thu phí, để mỗi người chỉ dùng chức năng được giao và thông tin cư dân không bị lộ.

- **Epic / Feature:** EPIC-05 / F-17
- **Nguồn:** RAW-027, RAW-035
- **Ưu tiên:** Should
- **Ước lượng:** 5 SP
- **Phụ thuộc:** US-021

**Acceptance Criteria**

- **AC-060:** **Giả sử** tài khoản vai trò Nhân viên thu phí, **khi** đăng nhập, **thì** chỉ dùng được chức năng thu phí và tra cứu.
- **AC-061:** **Giả sử** tài khoản Quản trị viên, **khi** đăng nhập, **thì** dùng được mọi chức năng gồm quản lý tài khoản.
- **AC-062:** **Giả sử** thông tin nhân khẩu và công nợ của một hộ, **khi** người không có quyền truy cập, **thì** hệ thống không hiển thị dữ liệu.

### US-023: Quản lý tài khoản

Là Người dùng, tôi muốn đổi mật khẩu và cập nhật thông tin cá nhân, để giữ an toàn tài khoản.

- **Epic / Feature:** EPIC-05 / F-18
- **Nguồn:** RAW-026, RAW-040
- **Ưu tiên:** Should
- **Ước lượng:** 3 SP
- **Phụ thuộc:** US-021

**Acceptance Criteria**

- **AC-063:** **Giả sử** đã đăng nhập, **khi** nhập mật khẩu cũ đúng và mật khẩu mới hợp lệ, **thì** đổi mật khẩu thành công.
- **AC-064:** **Giả sử** mật khẩu cũ sai, **khi** đổi mật khẩu, **thì** hệ thống từ chối.
- **AC-065:** **Giả sử** đã đăng nhập, **khi** sửa thông tin cá nhân và lưu, **thì** hệ thống cập nhật thông tin.

### TS-001: Sao lưu dữ liệu

Câu chuyện kỹ thuật: sao lưu dữ liệu MySQL định kỳ (hàng ngày/tuần) để tránh mất dữ liệu.

- **Epic / Feature:** EPIC-05 / F-21
- **Nguồn:** RAW-034
- **Ưu tiên:** Should
- **Ước lượng:** 5 SP
- **Phụ thuộc:** TS-003

**Acceptance Criteria**

- **AC-066:** **Giả sử** MySQL đang hoạt động, **khi** đến lịch sao lưu định kỳ, **thì** hệ thống tạo bản sao lưu và ghi nhận thời điểm.
- **AC-067:** **Giả sử** có bản sao lưu, **khi** khôi phục thử, **thì** dữ liệu thu phí và nhân khẩu phục hồi đầy đủ.

### TS-002: Nhật ký thao tác

Câu chuyện kỹ thuật: ghi nhật ký mọi thao tác thay đổi dữ liệu quan trọng.

- **Epic / Feature:** EPIC-05 / F-21
- **Nguồn:** RAW-033, RAW-017
- **Ưu tiên:** Should
- **Ước lượng:** 5 SP
- **Phụ thuộc:** TS-003

**Acceptance Criteria**

- **AC-068:** **Giả sử** người dùng sửa dữ liệu quan trọng, **khi** lưu, **thì** hệ thống ghi nhật ký gồm người sửa, nội dung sửa, thời điểm.
- **AC-069:** **Giả sử** nhật ký đã có dữ liệu, **khi** BQT xem nhật ký, **thì** hiển thị theo thời gian và không cho sửa/xóa.

### TS-003: Nền tảng Java máy tính để bàn + MySQL

Câu chuyện kỹ thuật: thiết lập ứng dụng Java máy tính để bàn kết nối CSDL MySQL tập trung.

- **Epic / Feature:** EPIC-05 / F-21
- **Nguồn:** RAW-024
- **Ưu tiên:** Must
- **Ước lượng:** 5 SP
- **Phụ thuộc:** (không)

**Acceptance Criteria**

- **AC-070:** **Giả sử** máy chủ MySQL đang chạy, **khi** ứng dụng Java máy tính để bàn khởi động, **thì** ứng dụng kết nối tới CSDL tập trung.
- **AC-071:** **Giả sử** hai máy cùng dùng ứng dụng, **khi** một máy ghi dữ liệu, **thì** máy còn lại thấy cùng dữ liệu.

### TS-004: Giao diện thân thiện

Câu chuyện kỹ thuật: rà soát giao diện dễ dùng cho người lớn tuổi (chữ rõ, ít bước thao tác).

- **Epic / Feature:** EPIC-05 / F-21
- **Nguồn:** RAW-037
- **Ưu tiên:** Could
- **Ước lượng:** 2 SP
- **Phụ thuộc:** (không)

**Acceptance Criteria**

- **AC-072:** **Giả sử** giao diện thu phí, **khi** người lớn tuổi thao tác thử thu một hộ, **thì** hoàn thành trong tối đa 5 bước, chữ đọc rõ.

## EPIC-06: Phí gửi xe (v2.0, chưa làm ở v1.0)

### US-024: Phí gửi xe (v2.0)

Là Ban quản trị, tôi muốn quản lý biển số và tự tính phí gửi xe (70.000 đ/xe máy, 1.200.000 đ/ô tô), để thu đúng theo phương tiện (v2.0).

- **Epic / Feature:** EPIC-06 / F-19
- **Nguồn:** RAW-029
- **Ưu tiên:** Won't
- **Ước lượng:** 0 SP
- **Phụ thuộc:** US-001

_Chưa viết Acceptance Criteria (thuộc v2.0)._

## EPIC-07: Thu hộ điện, nước, internet (v2.0, chưa làm ở v1.0)

### US-025: Thu hộ điện nước internet (v2.0)

Là Ban quản trị, tôi muốn nhập file thông báo từ điện lực, nhà mạng, để thu hộ điện, nước, internet (v2.0).

- **Epic / Feature:** EPIC-07 / F-20
- **Nguồn:** RAW-030
- **Ưu tiên:** Won't
- **Ước lượng:** 0 SP
- **Phụ thuộc:** US-009

_Chưa viết Acceptance Criteria (thuộc v2.0)._
