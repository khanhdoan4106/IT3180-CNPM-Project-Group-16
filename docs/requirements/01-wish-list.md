# 01. Client's Wish List

| ID | Nguồn | Mong muốn thô (nguyên văn) | Ghi chú |
|---|---|---|---|
| RAW-001 | Ban quản trị | Tôi muốn thêm mới thông tin một hộ gia đình và diện tích căn hộ khi họ bắt đầu chuyển đến. | Cốt lõi để tính phí sau này |
| RAW-002 | Sổ hộ khẩu (tài liệu) | Cần lưu trữ các thông tin cơ bản của nhân khẩu: Họ tên, Ngày sinh, Giới tính, Số CCCD. | Cấu trúc dữ liệu từ sổ hộ khẩu |
| RAW-003 | Ban quản trị | Khi khai báo nhân khẩu, phải ghi rõ người này là chủ hộ, hay là vợ/chồng/con của chủ hộ. | Cần danh sách chọn có sẵn |
| RAW-004 | Ban quản trị | Khi có người thân lên chơi vài ngày, tôi cần ghi nhận tạm trú cho họ trên hệ thống. | Yêu cầu chức năng |
| RAW-005 | Quy định địa phương | Ngày bắt đầu và ngày kết thúc tạm trú/tạm vắng bắt buộc phải có, không được để trống. | Kiểm tra dữ liệu đầu vào |
| RAW-006 | Ban quản trị | Tôi cần xuất danh sách những người đang tạm vắng để nộp cho công an phường kiểm tra định kỳ. | Yêu cầu từ cơ quan chức năng |
| RAW-007 | Quy định BQT | Phí dịch vụ phải tự động tính bằng cách lấy diện tích căn hộ nhân với đơn giá (từ 2.500đ đến 16.500đ/m²). | Tránh tính tay sai sót |
| RAW-008 | Quy định BQT | Phí quản lý chung cư thì cứ lấy diện tích nhân mặc định với 7.000đ/m². | Yêu cầu chức năng |
| RAW-009 | Ban quản trị | Mỗi đợt lễ tết, tôi muốn tự tạo một khoản thu tự nguyện mới như "Quỹ vì người nghèo", "Quỹ Trung thu". | Danh mục động |
| RAW-010 | Sổ quỹ (tài liệu) | Các quỹ tự nguyện thì không ép buộc số tiền, cư dân đóng bao nhiêu ghi bấy nhiêu, có người đóng 0đ. | Xử lý số tiền linh hoạt |
| RAW-011 | Thủ quỹ | Đầu mỗi tháng, phần mềm phải tự động tạo danh sách công nợ cho tất cả các phòng để tôi đi thu. | Yêu cầu chức năng |
| RAW-012 | Thủ quỹ | Nếu tôi lỡ nhập sai giá trị quỹ của một nhà, tôi phải có quyền cập nhật lại trước khi cư dân đóng. | Yêu cầu chức năng |
| RAW-013 | Thủ quỹ | Chỉ cần gõ mã phòng (VD: 1205) là phải hiện ra ngay danh sách các loại phí nhà đó chưa đóng. | Tối ưu thời gian thu |
| RAW-014 | Quan sát thủ quỹ | Giao diện thu tiền cần có nút "Thu tất cả" thay vì phải tick chọn từng khoản nhỏ lắt nhắt. | Trải nghiệm thao tác |
| RAW-015 | Thủ quỹ | Cư dân chuyển khoản rất nhiều, nên tôi cần chọn hình thức đóng là "Tiền mặt" hoặc "Chuyển khoản". | Phục vụ đối soát |
| RAW-016 | Cư dân | Đóng tiền xong thủ quỹ phải in cho tôi cái biên lai từ phần mềm để mang về đối chiếu. | Yêu cầu chức năng |
| RAW-017 | Ban quản trị | Hệ thống phải tự động lưu lại tên của người thu ngân (tài khoản đăng nhập) đã thực hiện giao dịch thu tiền. | Truy vết trách nhiệm |
| RAW-018 | File Excel (As-Is) | Dùng Excel hay bị cộng nhầm, phần mềm phải tự cộng tổng tiền chính xác 100% ra màn hình. | Giải quyết điểm đau |
| RAW-019 | Thủ quỹ | Nếu tôi ấn thu tiền nhầm nhà bên cạnh, tôi phải hủy được phiếu thu đó ngay trong ngày. | Yêu cầu chức năng |
| RAW-020 | Ban quản trị | Tôi muốn xem ngay trên màn hình chính tháng này tổng cộng cả chung cư thu được bao nhiêu tiền rồi. | Yêu cầu chức năng |
| RAW-021 | Ban quản trị | Cần lọc ra danh sách những nhà nào nợ phí quá 3 tháng để Ban quản trị đi gõ cửa nhắc nhở. | Yêu cầu chức năng |
| RAW-022 | Thủ quỹ | Tôi muốn xuất báo cáo danh sách thu tiền ra file Excel giống y hệt mẫu thủ công cũ để lưu trữ. | Khớp với quy trình cũ |
| RAW-023 | Ban quản trị | Tôi muốn tra cứu xem riêng một quỹ như "Quỹ vì biển đảo" đợt này thu được tổng cộng bao nhiêu. | Yêu cầu chức năng |
| RAW-024 | Ban quản trị | Phần mềm phải là ứng dụng máy tính để bàn viết bằng Java, dữ liệu lưu tập trung trên MySQL Server. | Ràng buộc kỹ thuật |
| RAW-025 | Ban quản trị | Chỉ Ban quản trị có tài khoản (tên đăng nhập/mật khẩu) mới đăng nhập được vào hệ thống để xem dữ liệu. | Yêu cầu bảo mật cơ bản |
| RAW-026 | Ban quản trị | Tôi cần tự đổi mật khẩu định kỳ để đảm bảo an toàn. | Yêu cầu chức năng |
| RAW-027 | Cư dân | Thông tin nhân khẩu và tiền nợ của nhà tôi không được để nhà khác xem được. | Phân quyền, quyền riêng tư |
| RAW-028 | Quan sát thủ quỹ | Lúc nhập mã phòng tìm kiếm thì phải hiện kết quả ngay lập tức (dưới 3 giây), chờ lâu cư dân phàn nàn. | Yêu cầu hiệu năng |
| RAW-029 | Ban quản trị | Sang năm sẽ thu phí gửi xe, phần mềm cần quản lý biển số và tính tự động 70k/xe máy, 1,2 triệu/ô tô. | Lộ trình v2.0 |
| RAW-030 | Ban quản trị | Tương lai cần có chức năng nhập file thông báo từ điện lực/nhà mạng để chung cư thu hộ điện nước luôn. | Lộ trình v2.0 |
| RAW-031 | Sổ thu (phân tích tài liệu) | Một hộ đóng nhiều quỹ trên cùng một dòng (người nghèo, biển đảo VN, lễ Đình Giùng, quỹ tổ DP, trẻ thơ, nhân đạo từ thiện, tình nghĩa, khuyến học, người cao tuổi); mức tiền mỗi quỹ không đồng nhất. | Phân tích ảnh sổ thu |
| RAW-032 | Sổ thu (phân tích tài liệu) | Sổ thu viết tay không có ngày thu hay số biên lai; chữ viết tay dễ nhầm số, khó tổng hợp. | Điểm đau từ sổ thu |
| RAW-033 | Sổ thu (phân tích tài liệu) | Sổ thu không có lịch sử chỉnh sửa, không biết ai sửa gì lúc nào. | Nhu cầu ẩn: nhật ký thay đổi |
| RAW-034 | Ban quản trị | Dữ liệu thu phí và nhân khẩu cần được sao lưu thường xuyên để tránh mất mát. | Yêu cầu phi chức năng |
| RAW-035 | Ban quản trị | Cần phân quyền rõ ràng giữa quản trị viên và nhân viên thu phí. | Yêu cầu phi chức năng |
| RAW-036 | Chính quyền địa phương | Việc báo cáo nhân khẩu, tạm trú, tạm vắng phải đúng quy định pháp luật hiện hành. | Yêu cầu pháp lý |
| RAW-037 | Cư dân | Giao diện cần dễ dùng, chữ rõ ràng, người lớn tuổi cũng thao tác được. | Khả dụng |
| RAW-038 | Ban quản trị | Hàng tháng chúng tôi phải lập danh sách các khoản phí cần đóng cho từng hộ rồi gửi thông báo thu tiền. | Quy trình hiện tại |
| RAW-039 | Ban quản trị | Người dùng cần đăng ký tài khoản trước khi sử dụng hệ thống. | Luồng nghiệp vụ 1 |
| RAW-040 | Ban quản trị | Tôi muốn cập nhật thông tin cá nhân của tài khoản (họ tên, số điện thoại). | Yêu cầu chức năng |
