# Thuật ngữ

Một khái niệm chỉ dùng một tên trong tài liệu, giao diện và mã nguồn.

| Thuật ngữ | Nghĩa | Trong mã / CSDL |
|---|---|---|
| Ban quản trị (BQT) | Ban do cư dân bầu, điều hành chung cư; dùng phần mềm với vai trò Quản trị viên | `VaiTro.QUAN_TRI` |
| Thủ quỹ | Người của BQT phụ trách thu phí; tương ứng tác nhân Kế toán trong Bộ bài tập. Tài liệu cũ gọi là Nhân viên thu phí | `VaiTro.THU_QUY` |
| Hộ gia đình | Những người cùng ở một căn hộ, kiot hoặc penthouse; mỗi hộ có đúng một chủ hộ | `ho_gia_dinh` |
| Nhân khẩu | Một người thuộc hộ, kể cả người tạm trú | `nhan_khau` |
| Đang ở / Đã chuyển đi | Trạng thái của hộ và nhân khẩu; không xóa, chỉ đổi trạng thái | `DANG_O`, `DA_CHUYEN_DI` |
| Đợt tạm trú / tạm vắng | Một khoảng thời gian có ngày bắt đầu và kết thúc | `tam_tru_tam_vang` |
| Khoản thu | Một mục trong danh mục thu: bắt buộc (phí dịch vụ, phí quản lý) hoặc tự nguyện (các quỹ) | `khoan_phi` |
| Đợt (khoản tự nguyện) | Thời gian thu của một khoản tự nguyện, từ ngày – đến ngày | `khoan_phi.tu_ngay`, `den_ngay` |
| Chốt công nợ | Tạo khoản phải nộp tháng này cho mọi hộ đang ở, theo diện tích × đơn giá | `dot_thu` |
| Khoản phải nộp | Số tiền một hộ phải nộp cho một khoản bắt buộc trong một tháng | `phai_nop` |
| Công nợ | Tổng các khoản phải nộp chưa đóng của một hộ | tính từ `phai_nop` |
| Phiếu thu | Một lần thu tiền của hộ, có thể gồm nhiều khoản; có hình thức thanh toán và người thu | `phieu_thu` |
| Mã phòng | Cách gọi khác của mã căn hộ ở quầy thu | `ho_gia_dinh.ma_can_ho` |
| Số biên lai | Số của phiếu thu, dạng `PT-<năm>-<5 chữ số>`, đánh lại mỗi năm; phiếu hủy giữ số | `phieu_thu.nam`, `phieu_thu.so` |
| Biên lai | Bản in của phiếu thu đưa cho cư dân | – |
| Đóng góp tự nguyện | Số tiền hộ đóng cho một quỹ (khoản thu tự nguyện); đóng nhiều lần được, ghi 0 đ là "không đóng" | dòng `phieu_thu_chi_tiet` không có `phai_nop_id` |
| Quỹ | Khoản thu tự nguyện có thời gian thu riêng (vd Quỹ vì người nghèo) | `khoan_phi.loai = TU_NGUYEN` |
| Thông báo phí | Giấy báo các khoản hộ cần đóng đến hết một tháng đã chốt, in cho từng hộ | – |
| Hủy phiếu | Hủy phiếu thu nhầm trong ngày; khoản phải nộp trở lại chưa đóng, phiếu vẫn được lưu | – |
