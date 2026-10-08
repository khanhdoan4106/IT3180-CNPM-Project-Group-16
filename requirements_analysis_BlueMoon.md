# Phân tích yêu cầu – BlueMoon

## 1. Tổng quan

### 1.1. Tên dự án

**BlueMoon**

### 1.2. Mục đích

Tài liệu phân tích yêu cầu nhằm tổng hợp, phân loại và chuẩn hóa các thông tin thu thập được từ phía người dùng/khách hàng. Đây là cơ sở để xác định phạm vi hệ thống, các chức năng cần xây dựng và các yêu cầu liên quan trước khi bước vào giai đoạn thiết kế và phát triển.

### 1.3. Mục tiêu

- Xác định vấn đề và nhu cầu của hệ thống.
- Thu thập và làm rõ các yêu cầu từ các bên liên quan.
- Phân loại yêu cầu theo từng nhóm.
- Xác định phạm vi của hệ thống.
- Làm cơ sở cho thiết kế, triển khai và kiểm thử hệ thống.

---

## 2. Bối cảnh và vấn đề

Hệ thống được xây dựng nhằm giải quyết các nhu cầu và vấn đề được xác định trong quá trình thu thập yêu cầu.

Việc phân tích hiện trạng giúp xác định:

- Quy trình đang được thực hiện như thế nào.
- Những khó khăn và hạn chế của quy trình hiện tại.
- Nhu cầu của người sử dụng.
- Những chức năng cần được cải thiện hoặc bổ sung.
- Các vấn đề cần giải quyết bằng hệ thống mới.

---

## 3. Các bên liên quan

Các bên liên quan có thể bao gồm:

| Bên liên quan | Vai trò |
|---|---|
| Người sử dụng | Sử dụng các chức năng của hệ thống |
| Quản trị viên | Quản lý và cấu hình hệ thống |
| Chủ sở hữu/khách hàng | Xác định nhu cầu và mục tiêu của hệ thống |
| Nhóm phát triển | Phân tích, thiết kế và xây dựng hệ thống |
| Các bên liên quan khác | Cung cấp thông tin và yêu cầu liên quan |

---

## 4. Hiện trạng (As-Is)

### 4.1. Quy trình hiện tại

Quy trình hiện tại được mô tả dựa trên thông tin thu thập từ các bên liên quan.

Các hoạt động chính cần xem xét:

1. Tiếp nhận yêu cầu.
2. Xử lý thông tin.
3. Thực hiện nghiệp vụ.
4. Lưu trữ dữ liệu.
5. Tra cứu và quản lý thông tin.
6. Xử lý các trường hợp phát sinh.

### 4.2. Vấn đề của hệ thống hiện tại

Một số vấn đề cần được xem xét:

- Quy trình có thể còn thực hiện thủ công.
- Thông tin có thể bị phân tán.
- Việc tìm kiếm và quản lý dữ liệu chưa tối ưu.
- Có khả năng xảy ra sai sót trong quá trình nhập và xử lý dữ liệu.
- Khó theo dõi trạng thái và lịch sử xử lý.
- Khả năng tổng hợp và báo cáo thông tin còn hạn chế.

---

## 5. Nhu cầu của khách hàng

Các nhu cầu được xác định gồm:

- Quản lý thông tin tập trung.
- Hỗ trợ người dùng thực hiện các nghiệp vụ thuận tiện hơn.
- Giảm các thao tác thủ công.
- Hạn chế sai sót trong quá trình xử lý.
- Hỗ trợ tìm kiếm và tra cứu thông tin.
- Theo dõi trạng thái và lịch sử hoạt động.
- Hỗ trợ quản trị và kiểm soát dữ liệu.
- Có khả năng mở rộng khi nhu cầu phát sinh.

---

## 6. Danh sách yêu cầu

### 6.1. Yêu cầu chức năng

Hệ thống cần cung cấp các nhóm chức năng chính:

#### Quản lý thông tin

- Thêm thông tin.
- Xem thông tin.
- Cập nhật thông tin.
- Xóa thông tin.
- Tìm kiếm và tra cứu thông tin.

#### Quản lý người dùng

- Đăng nhập.
- Đăng xuất.
- Quản lý tài khoản.
- Phân quyền người dùng.
- Kiểm soát quyền truy cập.

#### Xử lý nghiệp vụ

- Tiếp nhận yêu cầu từ người dùng.
- Kiểm tra và xử lý dữ liệu.
- Cập nhật trạng thái xử lý.
- Lưu lại lịch sử hoạt động.
- Thông báo kết quả cho người dùng.

#### Báo cáo và thống kê

- Tổng hợp dữ liệu.
- Thống kê các thông tin cần thiết.
- Hiển thị kết quả theo từng tiêu chí.
- Hỗ trợ theo dõi tình trạng hoạt động của hệ thống.

---

## 7. Yêu cầu phi chức năng

### 7.1. Hiệu năng

- Hệ thống cần phản hồi trong thời gian hợp lý.
- Có khả năng xử lý nhiều yêu cầu đồng thời.
- Thời gian truy vấn dữ liệu cần được tối ưu.

### 7.2. Bảo mật

- Người dùng phải được xác thực trước khi sử dụng các chức năng yêu cầu quyền truy cập.
- Phân quyền theo vai trò.
- Bảo vệ dữ liệu người dùng.
- Hạn chế truy cập trái phép.

### 7.3. Khả năng sử dụng

- Giao diện dễ sử dụng.
- Các chức năng được tổ chức rõ ràng.
- Thông báo lỗi và kết quả dễ hiểu.
- Người dùng có thể nhanh chóng làm quen với hệ thống.

### 7.4. Khả năng bảo trì

- Mã nguồn được tổ chức rõ ràng.
- Các thành phần của hệ thống có tính độc lập tương đối.
- Dễ dàng sửa lỗi và bổ sung chức năng mới.

### 7.5. Khả năng mở rộng

Hệ thống cần có khả năng mở rộng để đáp ứng các yêu cầu mới trong tương lai mà không phải thay đổi toàn bộ kiến trúc.

---

## 8. Phạm vi hệ thống

### 8.1. Trong phạm vi

- Quản lý dữ liệu.
- Quản lý người dùng.
- Xử lý các nghiệp vụ chính.
- Tìm kiếm và tra cứu.
- Thống kê và báo cáo.
- Quản lý quyền truy cập.

### 8.2. Ngoài phạm vi

Các chức năng chưa được xác định rõ trong tài liệu yêu cầu hiện tại sẽ được xem xét ở các giai đoạn tiếp theo.

---

## 9. Giả định và ràng buộc

### 9.1. Giả định

- Người dùng có thiết bị và kết nối cần thiết để truy cập hệ thống.
- Người dùng được cung cấp tài khoản phù hợp với vai trò.
- Dữ liệu đầu vào phải đáp ứng các quy tắc được hệ thống quy định.
- Các bên liên quan có thể cung cấp thông tin cần thiết trong quá trình phát triển.

### 9.2. Ràng buộc

- Thời gian phát triển dự án có giới hạn.
- Nguồn lực phát triển có giới hạn.
- Công nghệ sử dụng phải phù hợp với phạm vi dự án.
- Hệ thống phải đáp ứng các yêu cầu về bảo mật và quản lý dữ liệu.

---

## 10. Ưu tiên yêu cầu

Các yêu cầu có thể được ưu tiên theo mức độ quan trọng:

| Mức độ | Ý nghĩa |
|---|---|
| Must Have | Bắt buộc phải có |
| Should Have | Nên có |
| Could Have | Có thể bổ sung |
| Won't Have | Chưa triển khai trong phiên bản hiện tại |

### Must Have

- Đăng nhập và xác thực.
- Quản lý dữ liệu chính.
- Thêm, sửa, xóa và tra cứu dữ liệu.
- Xử lý các nghiệp vụ cốt lõi.
- Phân quyền người dùng.

### Should Have

- Thống kê dữ liệu.
- Báo cáo.
- Lưu lịch sử hoạt động.
- Các chức năng hỗ trợ người dùng.

### Could Have

- Các chức năng nâng cao.
- Tự động hóa một số quy trình.
- Các tính năng hỗ trợ mở rộng.

### Won't Have

Các chức năng chưa thuộc phạm vi phiên bản hiện tại sẽ được xem xét trong những phiên bản tiếp theo.

---

## 11. Tiêu chí chấp nhận

Một yêu cầu được xem là hoàn thành khi:

- Chức năng hoạt động đúng theo yêu cầu.
- Dữ liệu được xử lý chính xác.
- Người dùng có thể thực hiện nghiệp vụ theo quy trình đã xác định.
- Các trường hợp lỗi được xử lý phù hợp.
- Quyền truy cập được kiểm soát đúng.
- Kết quả đầu ra đáp ứng yêu cầu của người dùng.

---

## 12. Các vấn đề cần làm rõ

Một số nội dung cần tiếp tục xác nhận với các bên liên quan:

- Phạm vi chính xác của từng chức năng.
- Quyền hạn của từng nhóm người dùng.
- Quy tắc xử lý dữ liệu.
- Các trường hợp ngoại lệ.
- Yêu cầu về hiệu năng.
- Yêu cầu về bảo mật.
- Các báo cáo và thống kê cần cung cấp.
- Các chức năng dự kiến cho phiên bản tiếp theo.

---

## 13. Kết luận

Tài liệu phân tích yêu cầu là cơ sở để xác định phạm vi, chức năng và các yêu cầu của hệ thống BlueMoon.

Các yêu cầu cần được xác nhận với các bên liên quan trước khi chuyển sang giai đoạn thiết kế và triển khai. Trong quá trình phát triển, tài liệu có thể được cập nhật khi xuất hiện yêu cầu mới hoặc khi các yêu cầu hiện tại được làm rõ.

---

## 14. Tài liệu liên quan

- `BlueMoon_San_pham_giao_nop.xlsx` – File sản phẩm giao nộp.
- `phan tich yeu cau.md` – Tài liệu phân tích yêu cầu.
