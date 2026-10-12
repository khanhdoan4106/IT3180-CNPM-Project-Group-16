# 06. Epic / Feature List

> Nguồn: `BlueMoon_San_pham_giao_nop_2.xlsx`, sheet `6a_Epic và 6b_Feature`. 7 Epic và 21 Feature. EPIC-06 và EPIC-07 thuộc v2.0, chưa làm ở v1.0.

## Epic

| Epic ID | Tên Epic | Mô tả ngắn | Số Feature |
|---|---|---|---|
| EPIC-01 | Quản lý hộ gia đình và nhân khẩu | Quản lý hộ khẩu, nhân khẩu, tạm trú/tạm vắng, báo cáo cho cơ quan chức năng | 4 |
| EPIC-02 | Quản lý danh mục và khoản thu | Tính phí tự động, tạo khoản thu tự nguyện, ghi nhận đóng góp, chốt công nợ | 3 |
| EPIC-03 | Thu phí và thanh toán | Tra cứu phí, ghi nhận thanh toán, biên lai, hủy phiếu, thông báo thu phí | 4 |
| EPIC-04 | Thống kê và báo cáo | Tổng thu, lọc nợ đọng, thống kê theo khoản, xuất Excel | 4 |
| EPIC-05 | Tài khoản, bảo mật và nền tảng hệ thống | Đăng ký, đăng nhập, phân quyền, đổi mật khẩu; kiến trúc, sao lưu, nhật ký thay đổi, giao diện | 4 |
| EPIC-06 | Phí gửi xe (v2.0, chưa làm ở v1.0) | Quản lý phương tiện và thu phí gửi xe hàng tháng | 1 |
| EPIC-07 | Thu hộ điện, nước, internet (v2.0, chưa làm ở v1.0) | Thu hộ các khoản dịch vụ tiện ích theo thông báo của nhà cung cấp | 1 |

## Feature

| Feature ID | Tên Feature | Mô tả ngắn | Thuộc Epic |
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
