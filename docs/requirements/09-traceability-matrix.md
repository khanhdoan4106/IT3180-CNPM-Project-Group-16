# 09. Traceability Matrix

Truy vết từ mong muốn của khách hàng (`01-wish-list.md`) tới Epic/Feature (`06-epic-feature-list.md`), User Story (`07-user-stories.md`) và NFR (`04-nfr.md`). Ưu tiên lấy từ `08-product-backlog.md`.

> **Trạng thái:** bản nháp, cần cập nhật mỗi khi Wish List, Feature hoặc User Story thay đổi.

## 1. Ma trận chính

| Mong muốn | Epic | Feature | User Story | NFR liên quan | Ưu tiên cao nhất | Phiên bản |
|---|---|---|---|---|---|---|
| W-01 Quản lý hộ | E1 | F-01, F-02, F-03 | US-04, US-05, US-06 | NFR-01, NFR-06, NFR-11 | Must | v1.0 |
| W-02 Quản lý nhân khẩu | E2 | F-07, F-08 | US-07, US-08 | NFR-06, NFR-11 | Must | v1.0 |
| W-03 Biến động nhân khẩu | E2 | F-09, F-10 | US-09, US-10 | NFR-06, NFR-08 | Should | v1.0 |
| W-04 Đổi chủ hộ, tách hộ, ngừng quản lý | E1 | F-04, F-05, F-06 | US-11, US-12, US-13 | NFR-06, NFR-08 | Should | v1.0 |
| W-05 Tra cứu hộ, nhân khẩu | E5 | F-17 | US-21 | NFR-01 | Must | v1.0 |
| W-06 Danh mục khoản phí | E3 | F-11 | US-14, US-15 | NFR-06, NFR-13, NFR-14 | Must | v1.0 |
| W-07 Đợt thu, tính phí, miễn giảm | E3 | F-12, F-13 | US-16, US-17 | NFR-08, NFR-14 | Must | v1.0 |
| W-08 Ghi nhận thu phí | E4 | F-14 | US-18 | NFR-08, NFR-14 | Must | v1.0 |
| W-09 Theo dõi công nợ | E4 | F-15 | US-19 | NFR-01 | Must | v1.0 |
| W-10 In biên lai | E4 | F-16 | US-20 | NFR-10 | Should | v1.0 |
| W-11 Thống kê | E5 | F-18, F-19, F-20 | US-22, US-23, US-24 | NFR-02 | Must | v1.0 |
| W-12 Xuất báo cáo | E5 | F-21 | US-25 | NFR-02 | Could | v1.0 |
| W-13 Đăng nhập, đăng xuất | E6 | F-22 | US-01, US-02 | NFR-03, NFR-05 | Must | v1.0 |
| W-14 Đổi mật khẩu | E6 | F-23 | US-03 | NFR-03 | Must | v1.0 |
| W-15 Phân quyền | E6 | F-24 | US-26 | NFR-04 | Should | v1.0 |
| W-16 Lịch sử thay đổi | E6 | (đáp ứng bằng NFR-08, ghi trong US-05, US-08, US-17) | — | NFR-08 | Should (xuyên suốt) | v1.0 |
| W-17 Phí gửi xe | E7 | F-25 | (v2.0, chưa viết story) | NFR-13 | Won't (v1.0) | v2.0 |
| W-18 Điện, nước | E8 | F-26 | (v2.0, chưa viết story) | NFR-13 | Won't (v1.0) | v2.0 |

## 2. Truy vết ngược: User Story tới Feature và mong muốn

| User Story | Feature | Mong muốn |
|---|---|---|
| US-01 | F-22 | W-13 |
| US-02 | F-22 | W-13 |
| US-03 | F-23 | W-14 |
| US-04 | F-01 | W-01 |
| US-05 | F-02 | W-01, W-16 |
| US-06 | F-03 | W-01 |
| US-07 | F-07 | W-02 |
| US-08 | F-08 | W-02, W-16 |
| US-09 | F-09 | W-03 |
| US-10 | F-10 | W-03 |
| US-11 | F-04 | W-04 |
| US-12 | F-05 | W-04 |
| US-13 | F-06 | W-04 |
| US-14 | F-11 | W-06 |
| US-15 | F-11 | W-06 |
| US-16 | F-12 | W-07 |
| US-17 | F-13 | W-07, W-16 |
| US-18 | F-14 | W-08, W-16 |
| US-19 | F-15 | W-09 |
| US-20 | F-16 | W-10 |
| US-21 | F-17 | W-05 |
| US-22 | F-18 | W-11 |
| US-23 | F-19 | W-11 |
| US-24 | F-20 | W-11 |
| US-25 | F-21 | W-12 |
| US-26 | F-24 | W-15 |

## 3. Kiểm tra độ phủ

- Mọi mong muốn W-01 đến W-16 có ít nhất một Feature và User Story (W-16 được đáp ứng bằng NFR-08 và ghi trong các story có thao tác ghi dữ liệu).
- Mọi Feature F-01 đến F-24 có ít nhất một User Story.
- Mọi User Story truy về ít nhất một mong muốn.
- W-17, W-18 (v2.0) chỉ có Feature, chưa có User Story vì ngoài phạm vi v1.0.
- Các NFR còn lại xuất hiện ít nhất một lần trong ma trận chính. Riêng NFR-07 (sao lưu), NFR-09 (khả dụng) và NFR-12 (bảo trì) là yêu cầu chung của toàn hệ thống nên không gắn với một mong muốn cụ thể.
