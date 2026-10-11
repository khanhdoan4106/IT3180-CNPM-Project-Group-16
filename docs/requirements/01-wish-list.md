# 01. Client's Wish List

> **Trạng thái:** bản nháp đầu tiên, suy ra từ phạm vi BlueMoon v1.0 trong đề bài. Các chi tiết có dấu `(*)` là **giả định của nhóm**, cần xác nhận lại với khách hàng trong buổi phỏng vấn. Cột "Xác nhận" để `Chưa` cho đến khi có ghi chép tương ứng trong `05-raw-log.md`.

**Khách hàng:** Ban quản lý chung cư BlueMoon (Tổ trưởng, Kế toán).

**Mục tiêu chung:** thay sổ sách và file Excel rời rạc bằng một hệ thống tập trung để quản lý hộ gia đình, nhân khẩu và thu phí.

## Danh sách mong muốn

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

## Câu hỏi còn mở (cần hỏi khách hàng)

1. Phí tính theo diện tích căn hộ, theo số nhân khẩu hay mức cố định cho mỗi hộ? Mỗi khoản phí một cách tính hay dùng chung?
2. Hộ có được nộp một phần, nộp trước nhiều kỳ không? Có tính phạt chậm nộp không?
3. Một hộ có thể sở hữu nhiều căn hộ không? Một căn hộ có thể có nhiều hộ thuê lại không?
4. Ai được xem CCCD và thông tin cá nhân đầy đủ? Cư dân có được tự tra cứu khoản phí của mình không (để dành v2.0)?
5. Biên lai in giấy hay chỉ cần xác nhận điện tử? Có mẫu biên lai có sẵn không?
6. Dữ liệu hiện có đang ở đâu (sổ giấy, Excel) và cần nhập ban đầu như thế nào?
7. Khoản đóng góp tự nguyện có cần đối chiếu với tổng quỹ không?
