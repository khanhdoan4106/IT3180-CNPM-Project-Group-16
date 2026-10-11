# 08. Product Backlog (MoSCoW)

> **Trạng thái:** bản nháp. Ước lượng (story point, thang Fibonacci) và sprint đề xuất mang tính sơ bộ, nhóm điều chỉnh theo lịch môn học và năng lực thực tế. Thứ tự ưu tiên cuối cùng cần khách hàng xác nhận.

## 1. Tổng quan

| Ưu tiên | Số story | Tổng điểm |
|---|---|---|
| Must | 15 | 62 |
| Should | 8 | 35 |
| Could | 3 | 18 |
| Won't (v1.0) | 2 epic | — |

## 2. Backlog

| Thứ tự | Ưu tiên | Story | Tên | Feature | Điểm | Phụ thuộc | Sprint đề xuất |
|---|---|---|---|---|---|---|---|
| 1 | Must | US-01 | Đăng nhập | F-22 | 3 | — | 1 |
| 2 | Must | US-02 | Đăng xuất | F-22 | 1 | US-01 | 1 |
| 3 | Must | US-03 | Đổi mật khẩu | F-23 | 3 | US-01 | 1 |
| 4 | Must | US-04 | Thêm hộ mới | F-01 | 5 | US-01 | 1 |
| 5 | Must | US-06 | Xem danh sách và chi tiết hộ | F-03 | 3 | US-04 | 1 |
| 6 | Must | US-05 | Cập nhật thông tin hộ | F-02 | 3 | US-04 | 1 |
| 7 | Must | US-07 | Thêm nhân khẩu vào hộ | F-07 | 5 | US-04 | 2 |
| 8 | Must | US-08 | Cập nhật thông tin nhân khẩu | F-08 | 3 | US-07 | 2 |
| 9 | Must | US-14 | Tạo khoản phí | F-11 | 5 | US-01 | 2 |
| 10 | Must | US-15 | Sửa hoặc ngừng áp dụng khoản phí | F-11 | 3 | US-14 | 2 |
| 11 | Must | US-21 | Tra cứu hộ và nhân khẩu | F-17 | 5 | US-07 | 2 |
| 12 | Must | US-16 | Tạo đợt thu và tính phí tự động | F-12 | 8 | US-14, US-04 | 3 |
| 13 | Must | US-18 | Ghi nhận nộp phí | F-14 | 5 | US-16 | 3 |
| 14 | Must | US-19 | Theo dõi công nợ | F-15 | 5 | US-18 | 3 |
| 15 | Must | US-23 | Thống kê thu phí | F-19 | 5 | US-18 | 3 |
| 16 | Should | US-09 | Khai báo chuyển đi hoặc qua đời | F-09 | 5 | US-07 | 4 |
| 17 | Should | US-10 | Khai báo tạm trú, tạm vắng | F-10 | 5 | US-07 | 4 |
| 18 | Should | US-11 | Đổi chủ hộ | F-04 | 3 | US-07 | 4 |
| 19 | Should | US-13 | Ngừng quản lý hộ đã chuyển đi | F-06 | 3 | US-04 | 4 |
| 20 | Should | US-20 | In biên lai | F-16 | 3 | US-18 | 4 |
| 21 | Should | US-22 | Tra cứu khoản thu | F-18 | 3 | US-18 | 5 |
| 22 | Should | US-24 | Thống kê nhân khẩu | F-20 | 5 | US-07 | 5 |
| 23 | Should | US-26 | Quản lý tài khoản và phân quyền | F-24 | 8 | US-01 | 5 |
| 24 | Could | US-17 | Miễn giảm phí cho hộ | F-13 | 5 | US-16 | 5 hoặc sau |
| 25 | Could | US-25 | Xuất báo cáo | F-21 | 5 | US-23, US-24 | 5 hoặc sau |
| 26 | Could | US-12 | Tách hộ | F-05 | 8 | US-07, US-11 | Sau |

## 3. Won't have (trong v1.0)

| Hạng mục | Lý do | Phiên bản dự kiến |
|---|---|---|
| E7 / F-25 Quản lý phí gửi xe | Nằm ngoài phạm vi v1.0 theo đề bài | v2.0 |
| E8 / F-26 Thu tiền điện, nước | Nằm ngoài phạm vi v1.0 theo đề bài | v2.0 |
| Cổng tra cứu cho cư dân, thanh toán trực tuyến, thông báo tự động | Chưa nằm trong yêu cầu; cần bàn lại với khách hàng | Sau v2.0 |

## 4. Ghi chú

- **Tiêu chí phân loại:** Must là chức năng thiếu thì hệ thống không thay thế được sổ sách (đăng nhập, hộ, nhân khẩu, khoản phí, đợt thu, ghi nhận nộp, công nợ, thống kê thu, tra cứu). Should giúp nghiệp vụ đầy đủ hơn nhưng có thể tạm làm tay. Could là tiện ích có thể hoãn.
- **Quản lý khoản phí và đợt thu** (US-14, US-15, US-16) là nhóm có độ rủi ro cao nhất về nghiệp vụ (cách tính phí), nên cần chốt với khách hàng sớm.
- **US-12 (Tách hộ)** có điểm cao nhưng giá trị thấp, nên để cuối.
