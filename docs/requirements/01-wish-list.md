# 01. Client's Wish List

> **Trạng thái:** bản nháp đầu tiên, suy ra từ phạm vi BlueMoon v1.0 trong đề bài. Các chi tiết có dấu `(*)` là **giả định của nhóm**, cần xác nhận lại với khách hàng trong buổi phỏng vấn. Cột "Xác nhận" để `Chưa` cho đến khi có ghi chép tương ứng trong `05-raw-log.md`.

**Khách hàng:** Ban quản lý chung cư BlueMoon (Tổ trưởng, Kế toán).

**Mục tiêu chung:** thay sổ sách và file Excel rời rạc bằng một hệ thống tập trung để quản lý hộ gia đình, nhân khẩu và thu phí.

## Danh sách mong muốn

| ID | Mong muốn của khách hàng | Nhóm | Phiên bản | Nguồn | Xác nhận |
|---|---|---|---|---|---|
| W-01 | Quản lý thông tin hộ gia đình: thêm, sửa, xem (mã hộ, chủ hộ, số căn hộ, điện thoại liên hệ) | Hộ gia đình | v1.0 | Đề bài | Chưa |
| W-02 | Quản lý nhân khẩu trong từng hộ: họ tên, ngày sinh, giới tính, CCCD, quan hệ với chủ hộ, nghề nghiệp (*) | Nhân khẩu | v1.0 | Đề bài | Chưa |
| W-03 | Ghi nhận biến động nhân khẩu: sinh, chuyển đến, chuyển đi, qua đời, tạm trú, tạm vắng (*) | Nhân khẩu | v1.0 | Giả định nhóm | Chưa |
| W-04 | Thay đổi chủ hộ, tách hộ, ngừng quản lý hộ đã chuyển đi (*) | Hộ gia đình | v1.0 | Giả định nhóm | Chưa |
| W-05 | Tra cứu hộ và nhân khẩu nhanh theo nhiều tiêu chí (tên, số căn hộ, CCCD, trạng thái) | Tra cứu | v1.0 | Đề bài | Chưa |
| W-06 | Quản lý danh mục khoản phí: tên khoản, loại (bắt buộc hoặc tự nguyện), đơn giá, cách tính, kỳ thu | Khoản phí | v1.0 | Đề bài | Chưa |
| W-07 | Tạo đợt thu phí và tự động tính số tiền phải nộp của từng hộ; cho phép miễn giảm cho từng hộ (*) | Khoản phí | v1.0 | Đề bài | Chưa |
| W-08 | Ghi nhận việc thu phí: hộ nào nộp, bao nhiêu, ngày nào, ai thu; nộp một phần hoặc nộp gộp nhiều kỳ (*) | Thu phí | v1.0 | Đề bài | Chưa |
| W-09 | Theo dõi hộ chưa nộp hoặc còn nợ để nhắc nhở | Thu phí | v1.0 | Đề bài | Chưa |
| W-10 | In biên lai hoặc phiếu thu cho cư dân (*) | Thu phí | v1.0 | Giả định nhóm | Chưa |
| W-11 | Thống kê: tổng thu theo đợt và theo khoản phí, số hộ đã và chưa nộp, nhân khẩu theo độ tuổi, giới tính, trạng thái cư trú | Thống kê | v1.0 | Đề bài | Chưa |
| W-12 | Xuất báo cáo ra Excel hoặc PDF để nộp cấp trên (*) | Thống kê | v1.0 | Giả định nhóm | Chưa |
| W-13 | Đăng nhập, đăng xuất để chỉ người được phép truy cập | Tài khoản | v1.0 | Đề bài | Chưa |
| W-14 | Đổi mật khẩu | Tài khoản | v1.0 | Đề bài | Chưa |
| W-15 | Phân quyền theo vai trò (Tổ trưởng, Kế toán, Quản trị hệ thống) (*) | Tài khoản | v1.0 | Giả định nhóm | Chưa |
| W-16 | Lưu lịch sử thay đổi dữ liệu quan trọng (ai sửa gì, khi nào) (*) | Bảo mật | v1.0 | Giả định nhóm | Chưa |
| W-17 | Thu phí gửi xe | Mở rộng | v2.0 | Đề bài (roadmap) | Chưa |
| W-18 | Thu tiền điện, nước và các dịch vụ khác | Mở rộng | v2.0 | Đề bài (roadmap) | Chưa |

## Câu hỏi còn mở (cần hỏi khách hàng)

1. Phí tính theo diện tích căn hộ, theo số nhân khẩu hay mức cố định cho mỗi hộ? Mỗi khoản phí một cách tính hay dùng chung?
2. Hộ có được nộp một phần, nộp trước nhiều kỳ không? Có tính phạt chậm nộp không?
3. Một hộ có thể sở hữu nhiều căn hộ không? Một căn hộ có thể có nhiều hộ thuê lại không?
4. Ai được xem CCCD và thông tin cá nhân đầy đủ? Cư dân có được tự tra cứu khoản phí của mình không (để dành v2.0)?
5. Biên lai in giấy hay chỉ cần xác nhận điện tử? Có mẫu biên lai có sẵn không?
6. Dữ liệu hiện có đang ở đâu (sổ giấy, Excel) và cần nhập ban đầu như thế nào?
7. Khoản đóng góp tự nguyện có cần đối chiếu với tổng quỹ không?
