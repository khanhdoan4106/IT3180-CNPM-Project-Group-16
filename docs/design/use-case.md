# Sơ đồ use case – BlueMoon v1.0

Lấy từ [07-user-stories.md](../requirements/07-user-stories.md): mỗi use
case là một User Story, tác nhân là vai trò trong câu "Là …, tôi muốn …".
Không gồm US-024, US-025 (v2.0, Won't).

## Tác nhân

| Tác nhân | Vai trò trong hệ thống (tài khoản) | Ghi chú |
|---|---|---|
| Ban quản trị | Quản trị viên (`QUAN_TRI`) | Quản lý cư dân, khoản thu, tài khoản, báo cáo |
| Thủ quỹ | Nhân viên thu phí (`THU_QUY`) | Thu phí, biên lai, công nợ |
| Người dùng | Cả hai vai trò | Đăng nhập, đổi mật khẩu |

Tên vai trò "Thủ quỹ" và "Nhân viên thu phí" đang dùng song song trong tài
liệu yêu cầu; nhóm sẽ chốt một tên trước khi làm US-022.

## Sơ đồ

```mermaid
flowchart LR
    BQT(["👤 Ban quản trị"])
    TQ(["👤 Thủ quỹ"])
    ND(["👤 Người dùng"])
    BQT -. là .-> ND
    TQ -. là .-> ND

    subgraph HT["Hệ thống BlueMoon v1.0"]
        direction TB
        subgraph M1["Quản lý hộ gia đình"]
            UC001(["US-001 Quản lý hộ gia đình"])
            UC002(["US-002 Quản lý nhân khẩu"])
            UC003(["US-003 Tạm trú, tạm vắng"])
            UC004(["US-004 Báo cáo nhân khẩu"])
            UC026(["US-026 Đổi chủ hộ"])
        end
        subgraph M2["Quản lý thu phí"]
            UC005(["US-005 Tính phí tự động"])
            UC006(["US-006 Tạo khoản thu tự nguyện"])
            UC007(["US-007 Ghi nhận đóng góp tự nguyện"])
            UC008(["US-008 Sửa khoản thu"])
            UC009(["US-009 Chốt công nợ"])
            UC010(["US-010 Tra cứu phí"])
            UC011(["US-011 Thu tất cả các khoản"])
            UC012(["US-012 Hình thức thanh toán, người thu"])
            UC013(["US-013 In biên lai"])
            UC014(["US-014 Hủy phiếu thu"])
            UC015(["US-015 Thông báo thu phí"])
            UC016(["US-016 Tổng thu tháng"])
            UC017(["US-017 Lọc nợ đọng"])
            UC018(["US-018 Xuất báo cáo Excel"])
            UC019(["US-019 Thống kê theo khoản thu"])
        end
        subgraph M3["Quản lý người dùng"]
            UC020(["US-020 Đăng ký tài khoản"])
            UC021(["US-021 Đăng nhập"])
            UC022(["US-022 Phân quyền"])
            UC023(["US-023 Đổi mật khẩu, thông tin cá nhân"])
        end
    end

    BQT --- UC001 & UC002 & UC003 & UC004 & UC026
    BQT --- UC006 & UC015 & UC016 & UC017 & UC019
    BQT --- UC020 & UC022
    TQ --- UC005 & UC007 & UC008 & UC009 & UC010
    TQ --- UC011 & UC012 & UC013 & UC014 & UC018
    ND --- UC021 & UC023
```

## Quan hệ include / extend chính

Đề xuất của nhóm dựa trên mô tả story; cần chốt lại khi thiết kế chi tiết
module thu phí (Sprint 2–3).

| Use case | Quan hệ | Use case liên quan | Ý nghĩa |
|---|---|---|---|
| Mọi use case (trừ US-021) | include | US-021 Đăng nhập | Phải đăng nhập trước khi dùng chức năng |
| US-011 Thu tất cả các khoản | include | US-010 Tra cứu phí | Thu phí bắt đầu bằng tra cứu theo mã phòng |
| US-011 Thu tất cả các khoản | include | US-012 Hình thức thanh toán | Mỗi lần thu ghi hình thức và người thu |
| US-013 In biên lai | extend | US-011 Thu tất cả các khoản | In biên lai sau khi thu (tùy chọn) |
| US-014 Hủy phiếu thu | extend | US-011 Thu tất cả các khoản | Chỉ khi thu nhầm, trong ngày |
| US-009 Chốt công nợ | include | US-005 Tính phí tự động | Công nợ đầu tháng dựa trên phí tính theo diện tích |

## Trạng thái theo Sprint

| Module | Đã xong (Sprint 1) | Sprint 2 | Sprint 3 | Sprint 4 |
|---|---|---|---|---|
| Hộ gia đình | US-001, 002, 003 | | US-004 | |
| Thu phí | | US-005, 006, 008, 009 | US-007, 010–015 | US-016–019 |
| Người dùng | US-020, 021 | US-022, 023 | | |
