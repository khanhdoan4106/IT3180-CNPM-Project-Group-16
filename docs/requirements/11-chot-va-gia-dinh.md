# 11. Chốt xác nhận và giả định của nhóm

## Xác nhận với các bên liên quan

Cột "Ngày" và "Phản hồi" còn trống, nhóm điền sau khi xác nhận.

| Bên liên quan / PO | Nội dung xác nhận | Ngày | Phản hồi / Chỉnh sửa |
|---|---|---|---|
| Đại diện Ban quản trị | Xác nhận danh sách Epic, User Story và Product Backlog |  |  |
| Đại diện Thủ quỹ | Xác nhận quy trình thu phí và các Acceptance Criteria |  |  |
| Đại diện Cư dân | Xác nhận nhu cầu tra cứu công nợ và thông báo phí |  |  |

## Lưu ý và giả định của nhóm

1. Các phát biểu của Ban quản trị, Thủ quỹ, Cư dân, Chính quyền là mô phỏng phỏng vấn (chưa phỏng vấn thật); cần thay bằng dữ liệu thu thập thật nếu có. Sổ thu, sổ hộ khẩu, sổ quỹ, file Excel là tài liệu được phân tích.
2. v1.0 chỉ Ban quản trị có tài khoản (RAW-025); Thủ quỹ và Nhân viên thu phí là người dùng nội bộ (giả định cần xác nhận). Cư dân không có tài khoản, nên thông tin của họ được bảo vệ bằng phân quyền (RAW-027).
3. Ước lượng (điểm story point) và phân Sprint là ước lượng sơ bộ, cần nhóm chốt lại bằng họp ước lượng (Planning Poker).
4. Câu chuyện kỹ thuật (TS) và Tiêu chí hoàn thành (Definition of Done) là ghi chú bổ sung theo hướng dẫn.
5. **Số CCCD (US-002):** người từ đủ 14 tuổi bắt buộc nhập số CCCD; dưới 14 tuổi được để trống. Căn cứ: Luật Căn cước 2023, công dân từ đủ 14 tuổi phải làm thẻ căn cước, dưới 14 tuổi làm theo nhu cầu. Mục đích: báo cáo nhân khẩu cho công an phường (RAW-006, RAW-036) đủ dữ liệu.
6. **Trùng số CCCD (AC-008):** AC ghi "cảnh báo", hệ thống **chặn hẳn** và không lưu (chặt hơn AC). Chỉ chặn trùng giữa các nhân khẩu **đang ở**: người đã chuyển đi (cả hộ chuyển đi, hoặc một thành viên rời hộ khi chuyển căn, tách hộ) được khai báo lại ở hộ khác với cùng số CCCD. Mỗi nhân khẩu có trạng thái cư trú riêng (đang ở / đã chuyển đi); đánh dấu hộ chuyển đi thì toàn bộ thành viên cũng chuyển đi. Số thành viên của hộ (AC-005) chỉ đếm người đang ở.
7. **Nhân khẩu không bị xóa:** thành viên rời hộ được đánh dấu chuyển đi để giữ lịch sử. Chủ hộ không chuyển đi riêng được; đổi chủ hộ để ở US-026.
