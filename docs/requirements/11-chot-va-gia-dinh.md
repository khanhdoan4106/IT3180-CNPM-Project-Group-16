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
8. **Người tạm trú là nhân khẩu (US-003, AC-009):** người tạm trú được khai báo thành nhân khẩu của hộ, có loại cư trú `TAM_TRU` và nơi thường trú (bắt buộc với người tạm trú). Mỗi đợt tạm trú/tạm vắng là một bản ghi riêng (bảng `tam_tru_tam_vang`) có ngày bắt đầu và ngày kết thúc. Lý do: khớp AC-009 (tạm trú và tạm vắng đều gắn với nhân khẩu); dùng lại được chặn trùng CCCD, quy tắc CCCD từ đủ 14 tuổi và trạng thái chuyển đi của US-002; báo cáo cho công an (US-004) có đủ họ tên, CCCD, nơi thường trú, thời hạn.
9. **Thêm người tạm trú cùng đợt đầu tiên trong một giao dịch:** nhân khẩu `TAM_TRU` và đợt tạm trú đầu tiên được lưu cùng lúc (như thêm hộ kèm chủ hộ), lỗi giữa chừng thì hoàn tác cả hai. Lý do: không để có người tạm trú mà không có thời hạn nào.
10. **Chủ hộ được là người tạm trú:** ở chung cư, cả hộ thuê nhà có thể đều là tạm trú, nên form thêm hộ cho chọn chủ hộ tạm trú (kèm nơi thường trú và đợt tạm trú đầu tiên, cùng giao dịch).
11. **Đợt nào cũng có hạn và không chồng ngày:** ngày bắt đầu và ngày kết thúc đều bắt buộc (AC-010), ngày kết thúc không trước ngày bắt đầu (AC-011). Các đợt của cùng một người, dù tạm trú hay tạm vắng, không được chồng ngày lên nhau; kiểm tra lại trong giao dịch có khóa dòng nhân khẩu để hai máy cùng lưu không lọt. Tạm trú chỉ ghi cho người `TAM_TRU` (gia hạn = thêm đợt mới), tạm vắng chỉ ghi cho người thường trú.
12. **"Đang tạm trú / đang tạm vắng" tính theo ngày, không lưu thành cột:** có đợt chứa ngày hôm nay thì là đang tạm trú/tạm vắng; hết hạn thì tự hết. Cột `trang_thai` của US-002 vẫn chỉ là đang ở / đã chuyển đi.
13. **Loại cư trú chỉ là loại đăng ký:** `loai_cu_tru` không đổi theo ngày. Người tạm trú đã hết hạn mà chưa gia hạn vẫn đang ở, nên màn hình nhân khẩu có cột "Hạn tạm trú" tô đỏ khi đã qua hạn để Ban quản trị nhắc gia hạn (dữ liệu dùng lại cho báo cáo US-004).
