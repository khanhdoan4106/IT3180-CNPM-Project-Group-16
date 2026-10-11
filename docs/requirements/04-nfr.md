# 04. Non-Functional Requirements (NFR)

> **Trạng thái:** bản nháp. Các chỉ số trong cột "Tiêu chí đo được" là ngưỡng đề xuất `(*)`, cần thống nhất với khách hàng và đội phát triển; con số chính xác nên chốt sau khi biết quy mô dữ liệu thực tế (ước tính ban đầu: dưới 1.000 hộ và dưới 5.000 nhân khẩu).

| ID | Nhóm | Yêu cầu | Tiêu chí đo được |
|---|---|---|---|
| NFR-01 | Hiệu năng | Tra cứu và hiển thị danh sách hộ, nhân khẩu, công nợ phải nhanh | 95% yêu cầu tra cứu hoàn tất dưới 2 giây với 1.000 hộ và 5.000 nhân khẩu |
| NFR-02 | Hiệu năng | Thống kê và xuất báo cáo không làm người dùng chờ lâu | Thống kê hiển thị dưới 5 giây; xuất file báo cáo dưới 10 giây với dữ liệu một năm |
| NFR-03 | Bảo mật | Mật khẩu không được lưu dạng bản rõ | Mật khẩu băm bằng thuật toán có salt (bcrypt hoặc tương đương); độ dài tối thiểu 8 ký tự |
| NFR-04 | Bảo mật | Mỗi vai trò chỉ truy cập chức năng được phép | 100% API và màn hình kiểm tra quyền ở phía server; truy cập trái quyền trả về 403 |
| NFR-05 | Bảo mật | Phiên đăng nhập có thời hạn | Tự đăng xuất sau 30 phút không hoạt động; khoá đăng nhập tạm thời sau 5 lần sai liên tiếp |
| NFR-06 | Toàn vẹn dữ liệu | Dữ liệu phải nhất quán và hợp lệ | CCCD không trùng; mỗi hộ có đúng một chủ hộ; không xoá cứng hộ hoặc khoản phí đã có giao dịch (dùng trạng thái ngừng hoạt động) |
| NFR-07 | Độ tin cậy | Dữ liệu không được mất khi sự cố | Sao lưu cơ sở dữ liệu tối thiểu mỗi ngày; khôi phục thử thành công ít nhất một lần trước khi bàn giao |
| NFR-08 | Truy vết | Thao tác quan trọng phải lưu vết | Mọi thao tác thêm, sửa, miễn giảm, ghi nhận nộp phí lưu người thực hiện, thời điểm, giá trị cũ và mới |
| NFR-09 | Khả dụng | Giao diện dễ dùng với cán bộ không chuyên CNTT | Giao diện tiếng Việt; người dùng mới hoàn thành nghiệp vụ thêm hộ và ghi nhận nộp phí sau tối đa 30 phút hướng dẫn |
| NFR-10 | Tương thích | Chạy được trên máy văn phòng thông thường | Hoạt động trên Chrome, Edge, Firefox bản mới; hiển thị tốt ở độ phân giải từ 1366x768 |
| NFR-11 | Bảo vệ dữ liệu cá nhân | Thông tin cá nhân cư dân (họ tên, CCCD, ngày sinh) chỉ người có quyền được xem | Ẩn bớt CCCD (chỉ hiện đầy đủ cho vai trò được phép); không ghi thông tin cá nhân vào log ứng dụng |
| NFR-12 | Bảo trì | Mã nguồn dễ mở rộng sang v2.0 | Kiến trúc phân tầng (controller, service, repository); kiểm thử đơn vị bao phủ tối thiểu 60% tầng service |
| NFR-13 | Khả năng mở rộng | Dễ thêm loại khoản phí mới (phí gửi xe, điện, nước) | Thêm một cách tính phí mới không phải sửa cấu trúc bảng hiện có |
| NFR-14 | Độ chính xác | Tính tiền không sai số | Lưu tiền bằng kiểu số thập phân (không dùng số thực dấu phẩy động); quy tắc làm tròn thống nhất đến đồng |
