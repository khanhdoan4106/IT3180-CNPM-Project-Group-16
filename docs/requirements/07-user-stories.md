# 07. User Stories

> **Trạng thái:** bản nháp. Mỗi story theo mẫu "Là... tôi muốn... để...", đáp ứng tiêu chí **INVEST** (Independent, Negotiable, Valuable, Estimable, Small, Testable) và có Acceptance Criteria dạng **Given-When-Then**. Chi tiết có dấu `(*)` là giả định, cần xác nhận. Điểm ước lượng (story point, thang Fibonacci) là sơ bộ.

## Vai trò

| Vai trò | Mô tả |
|---|---|
| Cán bộ quản lý | Tổ trưởng hoặc thành viên Ban quản lý, phụ trách hộ, nhân khẩu, khoản phí |
| Kế toán | Phụ trách đợt thu, ghi nhận nộp phí, công nợ |
| Quản trị hệ thống (*) | Quản lý tài khoản và phân quyền |
| Người dùng | Bất kỳ vai trò nào đã có tài khoản |

---

## E6. Tài khoản và bảo mật

### US-01: Đăng nhập
**Là** người dùng, **tôi muốn** đăng nhập bằng tên đăng nhập và mật khẩu, **để** chỉ người được phép mới truy cập dữ liệu cư dân và thu phí.

**Feature:** F-22 · **Ưu tiên:** Must · **Ước lượng:** 3

- **Given** tài khoản đang hoạt động, **When** nhập đúng tên đăng nhập và mật khẩu, **Then** hệ thống vào trang chủ và chỉ hiển thị chức năng đúng vai trò.
- **Given** màn hình đăng nhập, **When** nhập sai tên đăng nhập hoặc mật khẩu, **Then** hiển thị thông báo chung "Tên đăng nhập hoặc mật khẩu không đúng" và không cho vào.
- **Given** một tài khoản, **When** nhập sai 5 lần liên tiếp (*), **Then** khoá đăng nhập tạm thời 15 phút (*).

### US-02: Đăng xuất
**Là** người dùng, **tôi muốn** đăng xuất, **để** người khác không dùng nhầm tài khoản của tôi trên máy dùng chung.

**Feature:** F-22 · **Ưu tiên:** Must · **Ước lượng:** 1

- **Given** đang đăng nhập, **When** bấm Đăng xuất, **Then** phiên bị huỷ và quay về màn hình đăng nhập.
- **Given** đã đăng xuất, **When** bấm nút Quay lại của trình duyệt, **Then** không xem được dữ liệu trang trước mà bị chuyển về đăng nhập.

### US-03: Đổi mật khẩu
**Là** người dùng, **tôi muốn** đổi mật khẩu của mình, **để** bảo vệ tài khoản.

**Feature:** F-23 · **Ưu tiên:** Must · **Ước lượng:** 3

- **Given** đang đăng nhập, **When** nhập đúng mật khẩu hiện tại và mật khẩu mới hợp lệ (tối thiểu 8 ký tự), **Then** mật khẩu được đổi và hiển thị thông báo thành công.
- **Given** màn hình đổi mật khẩu, **When** nhập sai mật khẩu hiện tại, **Then** không đổi và báo lỗi.
- **Given** màn hình đổi mật khẩu, **When** mật khẩu mới trùng mật khẩu cũ hoặc xác nhận không khớp, **Then** báo lỗi cụ thể và không đổi.

### US-26: Quản lý tài khoản và phân quyền
**Là** quản trị hệ thống, **tôi muốn** tạo tài khoản, gán vai trò và khoá hoặc mở tài khoản, **để** mỗi người chỉ dùng đúng chức năng của mình.

**Feature:** F-24 · **Ưu tiên:** Should · **Ước lượng:** 8

- **Given** quản trị hệ thống đang đăng nhập, **When** tạo tài khoản mới với tên đăng nhập chưa tồn tại và chọn vai trò, **Then** tài khoản được tạo và đăng nhập được.
- **Given** một tài khoản đang hoạt động, **When** quản trị hệ thống khoá nó, **Then** người đó không đăng nhập được nữa.
- **Given** người dùng vai trò Kế toán, **When** truy cập chức năng quản lý tài khoản, **Then** hệ thống từ chối (403).

---

## E1. Quản lý hộ gia đình

### US-04: Thêm hộ mới
**Là** cán bộ quản lý, **tôi muốn** thêm một hộ mới với thông tin chủ hộ và căn hộ, **để** hộ đó được quản lý trong hệ thống.

**Feature:** F-01 · **Ưu tiên:** Must · **Ước lượng:** 5

- **Given** form thêm hộ, **When** nhập đủ thông tin bắt buộc và lưu, **Then** hệ thống tạo hộ với mã hộ tự sinh, duy nhất.
- **Given** form thêm hộ, **When** bỏ trống trường bắt buộc, **Then** hiển thị lỗi ngay tại trường đó và không lưu.
- **Given** số căn hộ đã có hộ đang quản lý, **When** lưu hộ mới cùng số căn hộ, **Then** hệ thống cảnh báo trùng và không lưu (*).

### US-05: Cập nhật thông tin hộ
**Là** cán bộ quản lý, **tôi muốn** sửa thông tin của hộ, **để** dữ liệu luôn đúng thực tế.

**Feature:** F-02 · **Ưu tiên:** Must · **Ước lượng:** 3

- **Given** một hộ đang được quản lý, **When** sửa thông tin hợp lệ và lưu, **Then** thông tin mới hiển thị ngay và thao tác được ghi vào nhật ký.
- **Given** form sửa hộ, **When** để trống trường bắt buộc, **Then** báo lỗi và không lưu.
- **Given** một hộ, **When** sửa mã hộ, **Then** hệ thống không cho phép (mã hộ cố định).

### US-06: Xem danh sách và chi tiết hộ
**Là** cán bộ quản lý, **tôi muốn** xem danh sách hộ và chi tiết từng hộ kèm các thành viên, **để** nắm tình hình cư dân.

**Feature:** F-03 · **Ưu tiên:** Must · **Ước lượng:** 3

- **Given** có nhiều hộ, **When** mở danh sách, **Then** hiển thị phân trang kèm mã hộ, chủ hộ, số căn hộ, trạng thái.
- **Given** danh sách hộ, **When** chọn một hộ, **Then** hiển thị chi tiết hộ và danh sách thành viên.
- **Given** chưa có hộ nào, **When** mở danh sách, **Then** hiển thị thông báo "Chưa có hộ nào" thay vì lỗi.

### US-13: Ngừng quản lý hộ đã chuyển đi
**Là** cán bộ quản lý, **tôi muốn** đánh dấu một hộ đã chuyển đi, **để** hộ đó không còn bị tính vào các đợt thu mới nhưng lịch sử vẫn còn.

**Feature:** F-06 · **Ưu tiên:** Should · **Ước lượng:** 3

- **Given** một hộ đang quản lý, **When** đánh dấu đã chuyển đi và ghi ngày, **Then** trạng thái hộ đổi thành "Đã chuyển đi" và các nhân khẩu trong hộ cũng đổi trạng thái tương ứng.
- **Given** một hộ đã chuyển đi, **When** tạo đợt thu mới, **Then** hộ này không nằm trong danh sách tính phí.
- **Given** một hộ còn công nợ, **When** đánh dấu đã chuyển đi, **Then** hệ thống cảnh báo còn nợ và yêu cầu xác nhận (*).

### US-11: Đổi chủ hộ
**Là** cán bộ quản lý, **tôi muốn** chuyển vai trò chủ hộ sang một thành viên khác trong hộ, **để** hộ luôn có người đại diện đúng thực tế.

**Feature:** F-04 · **Ưu tiên:** Should · **Ước lượng:** 3

- **Given** hộ có từ 2 thành viên, **When** chọn thành viên khác làm chủ hộ, **Then** chủ hộ mới được cập nhật và chủ hộ cũ đổi quan hệ theo lựa chọn.
- **Given** hộ chỉ có một thành viên, **When** thực hiện đổi chủ hộ, **Then** hệ thống từ chối và giải thích.
- **Given** sau khi đổi, **When** xem hộ, **Then** hộ chỉ có đúng một chủ hộ.

### US-12: Tách hộ
**Là** cán bộ quản lý, **tôi muốn** tách một số thành viên thành hộ mới, **để** phản ánh việc các thành viên ở riêng.

**Feature:** F-05 · **Ưu tiên:** Could · **Ước lượng:** 8

- **Given** hộ có từ 2 thành viên, **When** chọn một số thành viên (không phải tất cả) và nhập thông tin hộ mới, **Then** hệ thống tạo hộ mới chứa các thành viên đã chọn và chọn một người làm chủ hộ.
- **Given** thao tác tách hộ, **When** chọn tất cả thành viên, **Then** hệ thống từ chối vì hộ cũ sẽ không còn ai.
- **Given** việc tách hộ thành công, **When** xem hộ cũ, **Then** các thành viên đã tách không còn xuất hiện ở hộ cũ.

---

## E2. Quản lý nhân khẩu

### US-07: Thêm nhân khẩu vào hộ
**Là** cán bộ quản lý, **tôi muốn** thêm thành viên vào một hộ, **để** có đầy đủ thông tin nhân khẩu.

**Feature:** F-07 · **Ưu tiên:** Must · **Ước lượng:** 5

- **Given** form thêm nhân khẩu của một hộ, **When** nhập đủ thông tin hợp lệ (họ tên, ngày sinh, giới tính, quan hệ với chủ hộ) và lưu, **Then** nhân khẩu xuất hiện trong danh sách thành viên của hộ.
- **Given** CCCD đã có ở nhân khẩu khác, **When** lưu, **Then** báo lỗi trùng CCCD và không lưu.
- **Given** hộ chưa có chủ hộ, **When** thêm nhân khẩu đầu tiên, **Then** hệ thống yêu cầu đặt người đó là chủ hộ.

### US-08: Cập nhật thông tin nhân khẩu
**Là** cán bộ quản lý, **tôi muốn** sửa thông tin nhân khẩu, **để** sửa sai sót hoặc cập nhật thay đổi.

**Feature:** F-08 · **Ưu tiên:** Must · **Ước lượng:** 3

- **Given** một nhân khẩu, **When** sửa thông tin hợp lệ và lưu, **Then** thay đổi hiển thị ngay và được ghi vào nhật ký.
- **Given** form sửa, **When** nhập ngày sinh ở tương lai, **Then** báo lỗi và không lưu.
- **Given** form sửa, **When** đổi CCCD thành số đã tồn tại, **Then** báo lỗi trùng.

### US-09: Khai báo chuyển đi hoặc qua đời
**Là** cán bộ quản lý, **tôi muốn** ghi nhận nhân khẩu chuyển đi hoặc qua đời kèm ngày và lý do, **để** số liệu cư trú luôn đúng.

**Feature:** F-09 · **Ưu tiên:** Should · **Ước lượng:** 5

- **Given** một nhân khẩu đang cư trú, **When** khai chuyển đi hoặc qua đời với ngày hợp lệ, **Then** trạng thái đổi tương ứng và nhân khẩu không còn tính vào thống kê cư trú hiện tại.
- **Given** nhân khẩu là chủ hộ, **When** khai chuyển đi hoặc qua đời, **Then** hệ thống yêu cầu chọn chủ hộ mới trước (nếu hộ còn thành viên).
- **Given** ngày khai sớm hơn ngày sinh, **When** lưu, **Then** báo lỗi.

### US-10: Khai báo tạm trú, tạm vắng
**Là** cán bộ quản lý, **tôi muốn** ghi nhận tạm trú, tạm vắng với khoảng thời gian, **để** biết số người thực tế ở chung cư.

**Feature:** F-10 · **Ưu tiên:** Should · **Ước lượng:** 5

- **Given** một nhân khẩu, **When** khai tạm vắng với ngày bắt đầu và ngày kết thúc dự kiến, **Then** hệ thống ghi nhận và hiển thị trạng thái "Tạm vắng" trong khoảng thời gian đó.
- **Given** ngày kết thúc sớm hơn ngày bắt đầu, **When** lưu, **Then** báo lỗi.
- **Given** đã quá ngày kết thúc dự kiến, **When** xem nhân khẩu, **Then** hệ thống gắn cờ để cán bộ xác nhận lại (*).

---

## E3. Quản lý khoản phí

### US-14: Tạo khoản phí
**Là** cán bộ quản lý, **tôi muốn** tạo khoản phí mới với loại, đơn giá và cách tính, **để** dùng cho các đợt thu.

**Feature:** F-11 · **Ưu tiên:** Must · **Ước lượng:** 5

- **Given** form khoản phí, **When** nhập tên, loại (bắt buộc hoặc tự nguyện), cách tính, đơn giá hợp lệ và lưu, **Then** khoản phí xuất hiện trong danh mục.
- **Given** tên khoản phí đã tồn tại, **When** lưu, **Then** báo trùng và không lưu.
- **Given** đơn giá âm hoặc bằng 0 với khoản bắt buộc, **When** lưu, **Then** báo lỗi.

### US-15: Sửa hoặc ngừng áp dụng khoản phí
**Là** cán bộ quản lý, **tôi muốn** sửa hoặc ngừng áp dụng khoản phí, **để** danh mục phản ánh quy định hiện hành mà không mất dữ liệu cũ.

**Feature:** F-11 · **Ưu tiên:** Must · **Ước lượng:** 3

- **Given** một khoản phí chưa dùng, **When** sửa thông tin và lưu, **Then** thay đổi áp dụng cho các đợt thu tạo sau.
- **Given** một khoản phí đã có đợt thu, **When** sửa đơn giá, **Then** các đợt thu cũ vẫn giữ đơn giá cũ.
- **Given** một khoản phí đã có đợt thu, **When** xoá, **Then** hệ thống không xoá mà chuyển sang trạng thái ngừng áp dụng.

### US-16: Tạo đợt thu và tính phí tự động
**Là** kế toán, **tôi muốn** tạo đợt thu cho một khoản phí và để hệ thống tự tính số tiền từng hộ, **để** khỏi tính tay.

**Feature:** F-12 · **Ưu tiên:** Must · **Ước lượng:** 8

- **Given** một khoản phí đang áp dụng, **When** tạo đợt thu với kỳ thu và hạn nộp hợp lệ, **Then** hệ thống tính số tiền phải nộp cho mọi hộ đang cư trú theo cách tính của khoản phí.
- **Given** đợt thu đã tạo, **When** xem chi tiết, **Then** thấy danh sách hộ kèm số tiền phải nộp và tổng phải thu khớp với tổng từng hộ.
- **Given** một khoản phí đã có đợt thu cùng kỳ, **When** tạo thêm đợt thu trùng kỳ, **Then** hệ thống cảnh báo trùng và yêu cầu xác nhận (*).

### US-17: Miễn giảm phí cho hộ
**Là** kế toán, **tôi muốn** miễn hoặc giảm phí cho một hộ trong đợt thu kèm lý do, **để** xử lý các trường hợp đặc biệt.

**Feature:** F-13 · **Ưu tiên:** Could · **Ước lượng:** 5

- **Given** một hộ trong đợt thu, **When** nhập số tiền giảm hợp lệ (không vượt số phải nộp) và lý do, **Then** số phải nộp được cập nhật và thao tác được ghi vào nhật ký.
- **Given** nhập số tiền giảm lớn hơn số phải nộp, **When** lưu, **Then** báo lỗi.
- **Given** hộ đã nộp đủ, **When** miễn giảm, **Then** hệ thống từ chối hoặc yêu cầu xử lý hoàn tiền riêng (*).

---

## E4. Thu phí

### US-18: Ghi nhận nộp phí
**Là** kế toán, **tôi muốn** ghi nhận một lần nộp tiền của hộ, **để** theo dõi chính xác đã thu những gì.

**Feature:** F-14 · **Ưu tiên:** Must · **Ước lượng:** 5

- **Given** một hộ còn phải nộp trong đợt thu, **When** nhập số tiền, ngày nộp, hình thức và lưu, **Then** hệ thống ghi nhận và cập nhật số còn nợ của hộ.
- **Given** số tiền nộp bằng số còn nợ, **When** lưu, **Then** trạng thái của hộ trong đợt đó chuyển thành "Đã nộp đủ".
- **Given** số tiền nộp lớn hơn số còn nợ, **When** lưu, **Then** hệ thống cảnh báo và không cho lưu (*).

### US-19: Theo dõi công nợ
**Là** kế toán, **tôi muốn** xem danh sách hộ chưa nộp hoặc còn nợ theo đợt thu, **để** nhắc nhở đúng người.

**Feature:** F-15 · **Ưu tiên:** Must · **Ước lượng:** 5

- **Given** một đợt thu, **When** mở công nợ, **Then** hiển thị các hộ chưa nộp hoặc nộp thiếu, kèm số còn nợ và tổng nợ.
- **Given** màn hình công nợ, **When** lọc theo đợt thu hoặc khoản phí, **Then** danh sách và tổng nợ cập nhật đúng theo bộ lọc.
- **Given** mọi hộ đã nộp đủ, **When** mở công nợ, **Then** hiển thị "Không còn hộ nào nợ".

### US-20: In biên lai
**Là** kế toán, **tôi muốn** in biên lai cho mỗi lần nộp, **để** cư dân có chứng từ.

**Feature:** F-16 · **Ưu tiên:** Should · **Ước lượng:** 3

- **Given** một lần nộp đã ghi nhận, **When** chọn In biên lai, **Then** hiển thị biên lai có mã số, tên hộ, khoản phí, số tiền, ngày, người thu.
- **Given** một biên lai đã in, **When** in lại, **Then** biên lai giữ nguyên mã số và ghi chú "Bản in lại" (*).
- **Given** lần nộp đã bị huỷ, **When** chọn In biên lai, **Then** hệ thống từ chối.

---

## E5. Tra cứu và thống kê

### US-21: Tra cứu hộ và nhân khẩu
**Là** cán bộ quản lý, **tôi muốn** tìm hộ hoặc nhân khẩu theo tên, số căn hộ, CCCD, trạng thái, **để** tìm nhanh khi cần.

**Feature:** F-17 · **Ưu tiên:** Must · **Ước lượng:** 5

- **Given** có dữ liệu, **When** nhập một phần tên hoặc số căn hộ, **Then** trả về các kết quả phù hợp trong chưa tới 2 giây.
- **Given** kết quả tìm kiếm, **When** chọn một kết quả, **Then** mở đúng trang chi tiết hộ hoặc nhân khẩu.
- **Given** không có kết quả phù hợp, **When** tìm kiếm, **Then** hiển thị "Không tìm thấy kết quả".

### US-22: Tra cứu khoản thu
**Là** kế toán, **tôi muốn** xem lịch sử các lần nộp theo hộ, theo đợt hoặc theo khoản phí, **để** đối chiếu khi cần.

**Feature:** F-18 · **Ưu tiên:** Should · **Ước lượng:** 3

- **Given** một hộ đã nộp nhiều lần, **When** mở lịch sử nộp của hộ, **Then** hiển thị đầy đủ các lần nộp theo thứ tự thời gian.
- **Given** màn hình tra cứu khoản thu, **When** lọc theo khoảng ngày, **Then** chỉ hiển thị các lần nộp trong khoảng đó.
- **Given** không có giao dịch phù hợp, **When** lọc, **Then** hiển thị "Không có dữ liệu".

### US-23: Thống kê thu phí
**Là** kế toán, **tôi muốn** xem tổng thu và số hộ đã hoặc chưa nộp theo đợt thu và theo khoản phí, **để** nắm tiến độ thu.

**Feature:** F-19 · **Ưu tiên:** Must · **Ước lượng:** 5

- **Given** một đợt thu, **When** mở thống kê, **Then** hiển thị tổng phải thu, tổng đã thu, tổng còn nợ, số hộ đã nộp đủ và chưa nộp đủ.
- **Given** thống kê, **When** đối chiếu với tổng các lần nộp ở tra cứu khoản thu, **Then** hai con số khớp nhau.
- **Given** đợt thu chưa có lần nộp nào, **When** mở thống kê, **Then** tổng đã thu bằng 0 và không báo lỗi.

### US-24: Thống kê nhân khẩu
**Là** cán bộ quản lý, **tôi muốn** thống kê nhân khẩu theo nhóm tuổi, giới tính và trạng thái cư trú, **để** làm báo cáo cho cấp trên.

**Feature:** F-20 · **Ưu tiên:** Should · **Ước lượng:** 5

- **Given** có dữ liệu nhân khẩu, **When** mở thống kê, **Then** hiển thị số lượng theo nhóm tuổi, giới tính và trạng thái cư trú.
- **Given** tổng các nhóm trong thống kê, **When** cộng lại, **Then** bằng tổng số nhân khẩu đang tính.
- **Given** nhân khẩu đã chuyển đi hoặc qua đời, **When** xem thống kê cư trú hiện tại, **Then** không tính các nhân khẩu này.

### US-25: Xuất báo cáo
**Là** cán bộ quản lý hoặc kế toán, **tôi muốn** xuất thống kê ra Excel hoặc PDF, **để** nộp cho cấp trên.

**Feature:** F-21 · **Ưu tiên:** Could · **Ước lượng:** 5

- **Given** một màn hình thống kê, **When** chọn Xuất Excel, **Then** tải về file có số liệu khớp màn hình.
- **Given** một màn hình thống kê, **When** chọn Xuất PDF, **Then** tải về file PDF dễ in, hiển thị đúng tiếng Việt.
- **Given** thống kê không có dữ liệu, **When** xuất, **Then** file vẫn hợp lệ và ghi rõ không có dữ liệu.
