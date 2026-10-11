# 03. Visionary Scenario

> **Trạng thái:** bản nháp, mô tả quy trình mong muốn khi có BlueMoon v1.0, tương ứng với từng kịch bản trong `02-as-is-scenario.md`. Chi tiết có dấu `(*)` là giả định, cần xác nhận với khách hàng.

## 1. Tác nhân và quyền

| Tác nhân | Quyền chính trong hệ thống |
|---|---|
| Tổ trưởng / Ban quản lý | Quản lý hộ, nhân khẩu, khoản phí, tra cứu, thống kê |
| Kế toán | Tạo đợt thu, ghi nhận nộp phí, công nợ, in biên lai, thống kê thu phí |
| Quản trị hệ thống (*) | Quản lý tài khoản và phân quyền |
| Cư dân | Không dùng hệ thống trong v1.0; làm việc qua Tổ trưởng và Kế toán (cổng cư dân để dành v2.0) |

## 2. Kịch bản A: Đăng ký hộ và nhân khẩu mới

1. Tổ trưởng đăng nhập, chọn **Thêm hộ mới**.
2. Hệ thống sinh mã hộ tự động; Tổ trưởng nhập thông tin hộ (chủ hộ, số căn hộ, điện thoại).
3. Tổ trưởng thêm từng nhân khẩu vào hộ (họ tên, ngày sinh, giới tính, CCCD, quan hệ với chủ hộ).
4. Hệ thống kiểm tra tự động: bắt buộc nhập đủ trường, CCCD không trùng, mỗi hộ có đúng một chủ hộ.
5. Tổ trưởng lưu; hộ và nhân khẩu hiển thị ngay trong danh sách và có thể tra cứu.

## 3. Kịch bản B: Biến động nhân khẩu

1. Tổ trưởng mở hộ liên quan và chọn đúng nghiệp vụ: thêm người mới, khai chuyển đi, khai qua đời, khai tạm trú, khai tạm vắng, đổi chủ hộ hoặc tách hộ.
2. Hệ thống cập nhật trạng thái cư trú và ghi nhận ngày, lý do.
3. Các thống kê nhân khẩu và danh sách phí của hộ tự động phản ánh thay đổi mới (ví dụ hộ đã chuyển đi không còn trong đợt thu sau).

## 4. Kịch bản C: Thu phí định kỳ

1. Tổ trưởng hoặc Kế toán tạo **khoản phí** (nếu chưa có) với loại, đơn giá, cách tính.
2. Kế toán tạo **đợt thu**: chọn khoản phí, kỳ thu, hạn nộp.
3. Hệ thống **tự động tính** số tiền phải nộp cho từng hộ đang cư trú, Kế toán có thể miễn giảm cho từng hộ (ghi rõ lý do).
4. Khi cư dân nộp tiền, Kế toán chọn hộ, nhập số tiền, ngày nộp, hình thức nộp; hệ thống cập nhật trạng thái và **in biên lai**.
5. Kế toán mở **công nợ** để xem ngay hộ nào chưa nộp, còn nợ bao nhiêu và lọc theo đợt thu.

## 5. Kịch bản D: Tra cứu, thống kê và báo cáo

1. Tổ trưởng gõ tên, số căn hộ hoặc CCCD; hệ thống trả về hộ hoặc nhân khẩu phù hợp trong chưa tới 2 giây.
2. Kế toán và Tổ trưởng chọn kỳ cần xem và hệ thống hiển thị tổng thu, số hộ đã và chưa nộp, số nhân khẩu theo nhóm tuổi, giới tính, trạng thái.
3. Khi cần nộp báo cáo, người dùng bấm **Xuất báo cáo** để lấy file Excel hoặc PDF.

## 6. Kịch bản E: Tài khoản và bảo mật

1. Người dùng đăng nhập bằng tên đăng nhập và mật khẩu; mỗi vai trò chỉ thấy chức năng được phép.
2. Người dùng có thể tự đổi mật khẩu bất kỳ lúc nào; mật khẩu lưu dưới dạng băm.
3. Các thao tác quan trọng (sửa hộ, sửa nhân khẩu, ghi nhận nộp, miễn giảm) được lưu lại: ai làm, lúc nào (*).

## 7. Lợi ích so với As-Is

| Bất cập (As-Is) | Cách hệ thống giải quyết | Mong muốn |
|---|---|---|
| P-01 Dữ liệu rời rạc | Một cơ sở dữ liệu duy nhất, có tra cứu nhanh | W-01, W-02, W-05 |
| P-02 Cập nhật biến động thủ công | Nghiệp vụ biến động có sẵn, dữ liệu cập nhật đồng bộ | W-03, W-04 |
| P-03 Tính tiền bằng tay | Tự động tính theo cách tính của khoản phí | W-06, W-07 |
| P-04 Ghi sổ, biên lai tay | Ghi nhận trên hệ thống, in biên lai | W-08, W-10 |
| P-05 Không có công nợ tức thời | Màn hình công nợ theo đợt thu | W-09 |
| P-06 Báo cáo thủ công | Thống kê tự động, xuất Excel hoặc PDF | W-11, W-12 |
| P-07 Không phân quyền, không lịch sử | Đăng nhập, phân quyền, nhật ký thay đổi | W-13, W-14, W-15, W-16 |

## 8. Ngoài phạm vi v1.0 (roadmap v2.0)

- Thu phí gửi xe (W-17).
- Thu tiền điện, nước và dịch vụ khác (W-18).
- Cổng tra cứu dành cho cư dân, thông báo tự động qua tin nhắn hoặc email, thanh toán trực tuyến.
