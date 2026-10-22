# 08. Product Backlog (MoSCoW)

## Tổng hợp

**Theo MoSCoW** (tính trên 30 mục US và TS):

| Ưu tiên | Số mục |
|---|---|
| Must | 13 |
| Should | 13 |
| Could | 2 |
| Won't | 2 |

**Theo sprint:**

| Sprint | Tổng SP |
|---|---|
| Sprint 1 | 24 |
| Sprint 2 | 24 |
| Sprint 3 | 28 |
| Sprint 4 | 28 |
| **Tổng** | **104** |

## EPIC-01: Quản lý hộ gia đình và nhân khẩu

Ưu tiên epic: **Must** · Sprint: **Sprint 1–3**

| ID | Mô tả ngắn | Ưu tiên | Sprint | Trạng thái | SP |
|---|---|---|---|---|---|
| US-001 | Thêm thông tin hộ gia đình và diện tích căn hộ khi cư dân mới chuyển đến | Must | Sprint 1 | Hoàn thành | 3 |
| US-002 | Lưu thông tin nhân khẩu và quan hệ với chủ hộ | Must | Sprint 1 | Hoàn thành | 5 |
| US-003 | Ghi nhận tạm trú, tạm vắng kèm ngày bắt đầu và kết thúc | Must | Sprint 1 | Hoàn thành | 5 |
| US-004 | Xuất danh sách nhân khẩu tạm vắng | Should | Sprint 3 | Chưa bắt đầu | 3 |
| US-026 | Đổi chủ hộ (chủ hộ mất, chuyển đi, tách hộ) | Should | Chưa xếp | Chưa bắt đầu | Chưa ước lượng |

## EPIC-02: Quản lý danh mục và khoản thu

Ưu tiên epic: **Must** · Sprint: **Sprint 2–3**

| ID | Mô tả ngắn | Ưu tiên | Sprint | Trạng thái | SP |
|---|---|---|---|---|---|
| US-005 | Hệ thống tự tính phí dịch vụ và phí quản lý theo diện tích × đơn giá | Must | Sprint 2 | Chưa bắt đầu | 5 |
| US-006 | Tự tạo khoản thu tự nguyện mới theo từng đợt (VD Quỹ vì người nghèo) | Must | Sprint 2 | Chưa bắt đầu | 3 |
| US-007 | Ghi nhận số tiền đóng góp tùy ý (kể cả 0đ) cho nhiều quỹ của một hộ | Must | Sprint 3 | Chưa bắt đầu | 5 |
| US-008 | Cập nhật lại giá trị khoản thu của một hộ trước khi hộ đóng | Should | Sprint 2 | Chưa bắt đầu | 3 |
| US-009 | Hệ thống tự tạo danh sách công nợ cho tất cả các phòng vào đầu mỗi tháng | Must | Sprint 2 | Chưa bắt đầu | 5 |

## EPIC-03: Thu phí và thanh toán

Ưu tiên epic: **Must** · Sprint: **Sprint 3**

| ID | Mô tả ngắn | Ưu tiên | Sprint | Trạng thái | SP |
|---|---|---|---|---|---|
| US-010 | Nhập mã phòng để xem ngay các khoản chưa đóng | Must | Sprint 3 | Chưa bắt đầu | 3 |
| US-011 | Thu tất cả các khoản của hộ bằng một nút và hệ thống tự cộng tổng | Must | Sprint 3 | Chưa bắt đầu | 5 |
| US-012 | Chọn tiền mặt hoặc chuyển khoản và hệ thống tự lưu người thu | Must | Sprint 3 | Chưa bắt đầu | 3 |
| US-013 | In biên lai từ phần mềm sau khi thu | Should | Sprint 3 | Chưa bắt đầu | 3 |
| US-014 | Hủy phiếu thu nhầm trong ngày | Should | Sprint 3 | Chưa bắt đầu | 3 |
| US-015 | In thông báo các khoản phí hàng tháng cho từng hộ | Should | Sprint 3 | Chưa bắt đầu | 3 |

## EPIC-04: Thống kê và báo cáo

Ưu tiên epic: **Should** · Sprint: **Sprint 4**

| ID | Mô tả ngắn | Ưu tiên | Sprint | Trạng thái | SP |
|---|---|---|---|---|---|
| US-016 | Xem tổng tiền đã thu trong tháng ngay trên màn hình chính | Should | Sprint 4 | Chưa bắt đầu | 5 |
| US-017 | Lọc danh sách hộ nợ phí quá 3 tháng | Should | Sprint 4 | Chưa bắt đầu | 3 |
| US-018 | Xuất báo cáo thu tiền ra Excel theo mẫu thủ công cũ | Could | Sprint 4 | Chưa bắt đầu | 5 |
| US-019 | Xem tổng thu của từng khoản/quỹ theo đợt | Should | Sprint 4 | Chưa bắt đầu | 3 |

## EPIC-05: Tài khoản, bảo mật và nền tảng hệ thống

Ưu tiên epic: **Must** · Sprint: **Sprint 1–4**

| ID | Mô tả ngắn | Ưu tiên | Sprint | Trạng thái | SP |
|---|---|---|---|---|---|
| US-020 | Đăng ký tài khoản đăng nhập | Must | Sprint 1 | Hoàn thành | 3 |
| US-021 | Đăng nhập bằng tên đăng nhập và mật khẩu | Must | Sprint 1 | Hoàn thành | 3 |
| US-022 | Phân quyền Quản trị viên và Nhân viên thu phí | Should | Sprint 2 | Chưa bắt đầu | 5 |
| US-023 | Đổi mật khẩu và cập nhật thông tin cá nhân | Should | Sprint 2 | Chưa bắt đầu | 3 |
| TS-001 | Sao lưu dữ liệu MySQL định kỳ (hàng ngày/tuần) để tránh mất dữ liệu. | Should | Sprint 4 | Chưa bắt đầu | 5 |
| TS-002 | Ghi nhật ký mọi thao tác thay đổi dữ liệu quan trọng. | Should | Sprint 4 | Chưa bắt đầu | 5 |
| TS-003 | Thiết lập ứng dụng Java máy tính để bàn kết nối CSDL MySQL tập trung. | Must | Sprint 1 | Chưa bắt đầu | 5 |
| TS-004 | Rà soát giao diện dễ dùng cho người lớn tuổi (chữ rõ, ít bước thao tác). | Could | Sprint 4 | Chưa bắt đầu | 2 |

## EPIC-06: Phí gửi xe (v2.0, chưa làm ở v1.0)

Ưu tiên epic: **Won't** · Sprint: **Chưa xếp (v2.0)**

| ID | Mô tả ngắn | Ưu tiên | Sprint | Trạng thái | SP |
|---|---|---|---|---|---|
| US-024 | Quản lý biển số và tự tính phí gửi xe (70.000 đ/xe máy, 1.200.000 đ/ô tô) | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | 0 |

## EPIC-07: Thu hộ điện, nước, internet (v2.0, chưa làm ở v1.0)

Ưu tiên epic: **Won't** · Sprint: **Chưa xếp (v2.0)**

| ID | Mô tả ngắn | Ưu tiên | Sprint | Trạng thái | SP |
|---|---|---|---|---|---|
| US-025 | Import file thông báo từ điện lực, nhà mạng | Won't | Chưa xếp (v2.0) | Chưa bắt đầu | 0 |
