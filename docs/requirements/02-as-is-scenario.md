# 02. As-Is Scenario

> **Trạng thái:** bản nháp, mô tả quy trình hiện tại theo giả định thông thường của một tổ dân cư hoặc chung cư. Các bước cần được đối chiếu và sửa theo lời kể thật của khách hàng (xem `05-raw-log.md`). Mọi chi tiết đều là giả định `(*)` cho tới khi được xác nhận.

## 1. Tác nhân tham gia

| Tác nhân | Vai trò trong quy trình hiện tại |
|---|---|
| Cư dân / Chủ hộ | Khai báo thông tin hộ và nhân khẩu, nộp phí |
| Tổ trưởng / Ban quản lý | Lập và cập nhật sổ hộ khẩu, nhân khẩu; thông báo khoản thu |
| Kế toán | Tính số tiền phải thu, thu tiền, ghi sổ, lập báo cáo |
| Cấp trên (phường, ban quản lý tòa nhà) | Nhận báo cáo định kỳ |

## 2. Bối cảnh hiện tại (*)

Thông tin hộ và nhân khẩu được ghi trong sổ giấy hoặc một file Excel do Tổ trưởng giữ. Việc thu phí do Kế toán ghi trong sổ thu và một file Excel riêng. Hai nguồn dữ liệu này không liên kết với nhau.

## 3. Kịch bản A: Đăng ký hộ và nhân khẩu mới

1. Chủ hộ đến văn phòng ban quản lý và điền phiếu khai báo giấy (thông tin hộ, danh sách thành viên).
2. Tổ trưởng kiểm tra phiếu, đối chiếu với giấy tờ (CCCD, sổ hộ khẩu hoặc hợp đồng).
3. Tổ trưởng chép thông tin vào sổ giấy và file Excel.
4. Tổ trưởng cấp hoặc xác nhận mã hộ, báo lại cho chủ hộ.
5. Phiếu khai báo được lưu vào tủ hồ sơ.

## 4. Kịch bản B: Biến động nhân khẩu

1. Chủ hộ báo bằng miệng hoặc nộp giấy khi có người sinh, chuyển đến, chuyển đi hoặc qua đời; hoặc khai tạm trú, tạm vắng.
2. Tổ trưởng sửa tay trên sổ và file Excel; ghi chú ngày và lý do.
3. Khi đổi chủ hộ hoặc tách hộ, Tổ trưởng phải sửa nhiều chỗ (sổ hộ, danh sách nhân khẩu, sổ thu phí) nên dễ sót.

## 5. Kịch bản C: Thu phí định kỳ

1. Ban quản lý quyết định khoản phí và mức thu của kỳ này (phí quản lý, phí dịch vụ, khoản đóng góp).
2. Kế toán tự tính số tiền phải nộp cho từng hộ bằng máy tính hoặc công thức Excel.
3. Kế toán thông báo bằng bảng tin hoặc nhóm chat cư dân.
4. Cư dân đến nộp tiền mặt (hoặc chuyển khoản, rồi gửi ảnh xác nhận).
5. Kế toán ghi vào sổ thu và viết biên lai tay.
6. Cuối kỳ, Kế toán rà từng dòng để biết hộ nào chưa nộp và nhắc trực tiếp.

## 6. Kịch bản D: Tra cứu, thống kê và báo cáo

1. Khi cần tra một hộ hoặc một người, Tổ trưởng lật sổ hoặc dùng chức năng tìm của Excel.
2. Cuối tháng hoặc quý, Kế toán và Tổ trưởng đếm tay số hộ đã nộp, tổng tiền thu, số nhân khẩu theo nhóm tuổi rồi tổng hợp báo cáo cấp trên.

## 7. Điểm bất cập

| ID | Bất cập | Hậu quả | Mong muốn liên quan |
|---|---|---|---|
| P-01 | Dữ liệu hộ và nhân khẩu nằm ở sổ giấy và Excel rời rạc | Dễ lệch, khó tìm, mất khi hỏng file hoặc mất sổ | W-01, W-02, W-05 |
| P-02 | Biến động nhân khẩu cập nhật thủ công ở nhiều nơi | Sót, sai thông tin, không biết số liệu hiện thời | W-03, W-04 |
| P-03 | Tự tính tiền từng hộ bằng tay | Sai số, tốn thời gian mỗi kỳ thu | W-06, W-07 |
| P-04 | Ghi sổ và biên lai tay | Chữ khó đọc, thất lạc, khó đối chiếu | W-08, W-10 |
| P-05 | Không có danh sách nợ tức thời | Phải rà thủ công, nhắc nợ không kịp thời | W-09 |
| P-06 | Tổng hợp báo cáo bằng tay | Mất nhiều ngày, số liệu không nhất quán | W-11, W-12 |
| P-07 | Không phân quyền, không có lịch sử sửa đổi | Ai cũng sửa được file; không biết ai đã sửa gì | W-13, W-15, W-16 |
