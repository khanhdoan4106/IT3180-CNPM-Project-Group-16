# 06. Epic / Feature List

> **Trạng thái:** bản nháp. Mỗi Feature truy về ít nhất một mong muốn ở `01-wish-list.md` và được cụ thể hóa thành User Story ở `07-user-stories.md`.

## 1. Epic

| Epic | Tên | Mục tiêu | Mong muốn | Phiên bản |
|---|---|---|---|---|
| E1 | Quản lý hộ gia đình | Lưu và cập nhật thông tin các hộ trong chung cư | W-01, W-04 | v1.0 |
| E2 | Quản lý nhân khẩu | Lưu thông tin từng nhân khẩu và các biến động cư trú | W-02, W-03 | v1.0 |
| E3 | Quản lý khoản phí | Định nghĩa khoản phí, tạo đợt thu, tính phí cho từng hộ | W-06, W-07 | v1.0 |
| E4 | Thu phí | Ghi nhận nộp phí, theo dõi công nợ, in biên lai | W-08, W-09, W-10 | v1.0 |
| E5 | Tra cứu và thống kê | Tìm nhanh dữ liệu và tổng hợp báo cáo | W-05, W-11, W-12 | v1.0 |
| E6 | Tài khoản và bảo mật | Đăng nhập, đổi mật khẩu, phân quyền, truy vết | W-13, W-14, W-15, W-16 | v1.0 |
| E7 | Phí gửi xe | Thu phí gửi xe theo phương tiện | W-17 | v2.0 |
| E8 | Điện, nước và dịch vụ | Thu tiền điện, nước theo chỉ số tiêu thụ | W-18 | v2.0 |

## 2. Feature

| Epic | ID | Feature | Mô tả ngắn | Mong muốn | Phiên bản |
|---|---|---|---|---|---|
| E1 | F-01 | Thêm hộ mới | Tạo hộ với mã hộ tự sinh, chủ hộ, số căn hộ, liên hệ | W-01 | v1.0 |
| E1 | F-02 | Cập nhật thông tin hộ | Sửa thông tin hộ đã có | W-01 | v1.0 |
| E1 | F-03 | Xem danh sách và chi tiết hộ | Danh sách phân trang, xem chi tiết kèm thành viên | W-01 | v1.0 |
| E1 | F-04 | Đổi chủ hộ | Chuyển vai trò chủ hộ sang thành viên khác trong hộ | W-04 | v1.0 |
| E1 | F-05 | Tách hộ | Tách một số thành viên thành hộ mới | W-04 | v1.0 |
| E1 | F-06 | Ngừng quản lý hộ | Đánh dấu hộ đã chuyển đi, giữ nguyên lịch sử | W-04 | v1.0 |
| E2 | F-07 | Thêm nhân khẩu | Thêm thành viên vào hộ với đầy đủ thông tin cá nhân | W-02 | v1.0 |
| E2 | F-08 | Cập nhật nhân khẩu | Sửa thông tin nhân khẩu | W-02 | v1.0 |
| E2 | F-09 | Khai báo chuyển đi hoặc qua đời | Cập nhật trạng thái cư trú kèm ngày và lý do | W-03 | v1.0 |
| E2 | F-10 | Khai báo tạm trú, tạm vắng | Ghi nhận khoảng thời gian tạm trú hoặc tạm vắng | W-03 | v1.0 |
| E3 | F-11 | Quản lý danh mục khoản phí | Tạo, sửa, ngừng áp dụng khoản phí (loại, đơn giá, cách tính) | W-06 | v1.0 |
| E3 | F-12 | Tạo đợt thu và tính phí | Tạo đợt thu theo kỳ, tự tính số tiền từng hộ | W-07 | v1.0 |
| E3 | F-13 | Miễn giảm phí cho hộ | Miễn hoặc giảm số tiền phải nộp của một hộ, kèm lý do | W-07 | v1.0 |
| E4 | F-14 | Ghi nhận nộp phí | Ghi số tiền, ngày, hình thức nộp, người thu | W-08 | v1.0 |
| E4 | F-15 | Theo dõi công nợ | Danh sách hộ chưa nộp hoặc còn nợ theo đợt thu | W-09 | v1.0 |
| E4 | F-16 | In biên lai | Tạo biên lai cho mỗi lần nộp | W-10 | v1.0 |
| E5 | F-17 | Tra cứu hộ và nhân khẩu | Tìm theo tên, số căn hộ, CCCD, trạng thái | W-05 | v1.0 |
| E5 | F-18 | Tra cứu khoản thu | Xem lịch sử thu theo hộ, theo đợt, theo khoản | W-11 | v1.0 |
| E5 | F-19 | Thống kê thu phí | Tổng thu, số hộ đã và chưa nộp theo đợt, khoản | W-11 | v1.0 |
| E5 | F-20 | Thống kê nhân khẩu | Theo nhóm tuổi, giới tính, trạng thái cư trú | W-11 | v1.0 |
| E5 | F-21 | Xuất báo cáo | Xuất Excel hoặc PDF cho thống kê | W-12 | v1.0 |
| E6 | F-22 | Đăng nhập và đăng xuất | Xác thực bằng tên đăng nhập và mật khẩu | W-13 | v1.0 |
| E6 | F-23 | Đổi mật khẩu | Người dùng tự đổi mật khẩu của mình | W-14 | v1.0 |
| E6 | F-24 | Quản lý tài khoản và phân quyền | Tạo tài khoản, gán vai trò, khoá hoặc mở tài khoản | W-15, W-16 | v1.0 |
| E7 | F-25 | Quản lý phí gửi xe | Đăng ký phương tiện của hộ, tính phí gửi xe | W-17 | v2.0 |
| E8 | F-26 | Thu tiền điện, nước | Nhập chỉ số, tính tiền theo bậc hoặc đơn giá | W-18 | v2.0 |
