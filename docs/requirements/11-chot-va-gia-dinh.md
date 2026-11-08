# 11. Chốt xác nhận và giả định của nhóm

## Xác nhận yêu cầu

Các bên liên quan (Ban quản trị, Thủ quỹ, Cư dân, Chính quyền) là vai mô
phỏng theo đề bài, chưa phỏng vấn người thật. Danh sách Epic, User Story và
Product Backlog được nhóm chốt khi nộp sản phẩm Giai đoạn 1 (khai phá yêu
cầu) và được giảng viên xem xét. Thay đổi yêu cầu sau đó ghi vào mục dưới
kèm lý do.

## Lưu ý và giả định của nhóm

1. Các phát biểu của Ban quản trị, Thủ quỹ, Cư dân, Chính quyền là mô phỏng phỏng vấn; sổ thu, sổ hộ khẩu, sổ quỹ, file Excel là tài liệu được phân tích.
2. **Tài khoản (RAW-025, RAW-035):** chỉ người được Quản trị viên cấp tài khoản mới đăng nhập được. Có hai vai trò: Quản trị viên và Thủ quỹ (tài liệu cũ gọi là Nhân viên thu phí; nhóm chốt tên Thủ quỹ ngày 01/11/2026). Chỉ Quản trị viên tạo được tài khoản mới (US-020). Cư dân không có tài khoản; thông tin của họ được bảo vệ bằng phân quyền (RAW-027).
3. **Ước lượng:** Story Point và phân Sprint chốt bằng họp Planning Poker đầu mỗi Sprint; điều chỉnh ghi vào Product Backlog.
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
14. **Đơn giá ngoài khoảng bị chặn (AC-017):** AC ghi "cảnh báo", hệ thống **chặn hẳn** như AC-008: phí dịch vụ ngoài 2.500–16.500 đ/m² hoặc phí quản lý dưới 7.000 đ/m² không lưu được. Căn cứ: đề bài nêu khoảng giá cố định (RAW-004, RAW-005).
15. **Chốt công nợ lại cùng tháng chỉ bổ sung (AC-028):** chốt lại không tạo trùng nhưng thêm khoản cho hộ chuyển đến hoặc khoản bắt buộc mới sau lần chốt đầu; khoản đã có, kể cả đã sửa, giữ nguyên. Hộ chuyển đến giữa tháng vẫn tính đủ tháng. Đầu tháng, Trang chính nhắc chốt khi tháng hiện tại chưa chốt (thay cho lịch tự chạy, vì ứng dụng desktop không chạy lúc nửa đêm).
16. **Khoản tự nguyện không tạo công nợ:** chỉ khoản bắt buộc vào danh sách phải nộp; tiền đóng góp ghi khi thu (US-007).
17. **Phân quyền hai lớp (US-022):** menu ẩn chức năng vai trò không được dùng, và Service kiểm tra lại vai trò ở mỗi thao tác. Trước mỗi lần mở màn hình, hệ thống đọc lại tài khoản từ CSDL: tài khoản vừa bị khóa bị đăng xuất, vừa đổi vai trò thì menu đổi theo ngay.
18. **Quản lý tài khoản (US-022, RAW-035):** Quản trị viên đổi vai trò, khóa/mở khóa và đặt lại mật khẩu tạm cho tài khoản khác; không tự khóa hay tự đổi vai trò của mình, nên luôn còn ít nhất một Quản trị viên. Tài khoản không bị xóa.
19. **Thông tin cá nhân (US-023):** người dùng tự sửa họ tên và số điện thoại; tên đăng nhập, vai trò do Quản trị viên quản lý. Mật khẩu mới phải nhập lại và khác mật khẩu cũ.
20. **Lịch sử biến động nhân khẩu (US-027):** mỗi lần nhân khẩu chuyển đến, chuyển đi, đăng ký tạm trú/tạm vắng được ghi một dòng (ngày, người ghi, ghi chú) trong cùng giao dịch với thao tác đó; lịch sử chỉ thêm, không sửa, không xóa. Căn cứ: Bộ bài tập chương 7 yêu cầu lưu lịch sử thay đổi nhân khẩu trong hộ.
21. **Hộ chuyển đi khi còn nợ:** vẫn đánh dấu chuyển đi được (ngoài đời cư dân có thể dọn đi trước khi đóng đủ), nhưng hệ thống hiện tổng nợ và hỏi xác nhận; khoản nợ được giữ nguyên. Tra cứu phí (US-010) tìm được cả hộ đã chuyển đi còn nợ để thu tiếp, và hộ đó vẫn tính trong báo cáo nợ đọng (US-017).
22. **Một đơn giá chung cho mọi loại căn hộ:** khoảng 2.500–16.500 đ/m² là phạm vi Ban quản trị được chọn; mỗi thời điểm áp dụng một mức chung cho căn hộ, kiot và penthouse, đổi được khi cần. Tháng đã chốt giữ đơn giá cũ.
23. **Đóng phí:** khoản bắt buộc phải đóng đủ số tiền của khoản (AC-033: công nợ về 0); khoản tự nguyện đóng bao nhiêu ghi bấy nhiêu, ghi 0 đ là "không đóng", khác "chưa thu" (AC-023).
24. **Tổng thu tháng (US-016, AC-046):** tính theo ngày thu tiền trên phiếu thu chưa hủy, để khớp sổ quỹ (tiền nợ tháng trước đóng trong tháng này tính vào tháng này). Trang chính hiện thêm tỷ lệ thu của công nợ tháng hiện tại.
25. **Nợ quá 3 tháng (US-017):** số tháng nợ là số tháng chốt công nợ còn ít nhất một khoản bắt buộc chưa đóng; lọc các hộ có từ 3 tháng trở lên, kể cả hộ đã chuyển đi.
26. **Đổi chủ hộ (US-026):** trong một giao dịch, chủ hộ cũ đổi sang quan hệ Ban quản trị chọn (vợ/chồng, con, khác) hoặc rời hộ, rồi người được chọn mới thành chủ hộ; ghi lịch sử "Đổi chủ hộ" (và "Chuyển đi" nếu chủ hộ cũ rời hộ).

Các giả định 27–31 được Scrum Master chốt ngày 08/11/2026, khi rà soát cuối
Sprint 2 ([báo cáo rà soát](../review/sprint-2-ra-soat.md), mục 5).

27. **Phạm vi Sprint 3 và in ấn:** Sprint 3 giữ đủ 28 SP, gồm US-004 (danh sách tạm vắng) và US-015 (thông báo thu phí) bên cạnh phần thu phí (US-007, US-010 – US-014). US-004, US-013 (biên lai) và US-015 in trực tiếp ra máy in bằng một cơ chế chung là `PrinterJob` của JavaFX. Xuất file Excel/PDF để Sprint 4 cùng US-018; vì vậy AC-014 của US-004 (xuất file Excel/PDF) làm ở Sprint 4.
28. **Hủy phiếu thu (US-014):** Thủ quỹ chỉ hủy được phiếu do chính mình lập; Quản trị viên hủy được mọi phiếu. Chỉ hủy trong ngày lập phiếu (AC-042) và bắt buộc nhập lý do. Phiếu hủy không bị xóa mà được giữ lại kèm người hủy, thời điểm hủy và lý do (AC-043); các khoản phải nộp trong phiếu trở về "chưa đóng" (AC-041), các lần đóng góp tự nguyện trong phiếu không còn được tính.
29. **Đóng góp tự nguyện (US-007):** một hộ được đóng góp nhiều lần cho cùng một quỹ trong một đợt; số đã đóng là tổng các lần thuộc phiếu chưa hủy. Mức 0 đ chỉ ghi được khi hộ chưa có lần nào cho quỹ đó, nghĩa là "đã hỏi, không đóng" (AC-023). Sau đó nếu hộ đóng một số tiền lớn hơn 0 thì quỹ đó tính là "đã đóng". Như vậy mỗi hộ, với mỗi quỹ, ở một trong ba trạng thái: chưa thu (chưa có lần nào), không đóng (chỉ có lần 0 đ), đã đóng (tổng lớn hơn 0).
30. **Phiếu chỉ gồm khoản 0 đ:** vẫn được lưu và có số biên lai như mọi phiếu, hiển thị "Không thu tiền" và không in biên lai.
31. **Số biên lai (AC-039):** có dạng `PT-<năm>-<5 chữ số>`, ví dụ `PT-2026-00001`; năm là năm lập phiếu, mỗi năm đánh lại từ 00001 và tăng liên tục, không có khoảng trống. Số được cấp từ một bảng đếm theo năm, khóa dòng bằng `SELECT … FOR UPDATE` trong cùng giao dịch lưu phiếu, không dùng `AUTO_INCREMENT` (cột tự tăng nhảy số khi giao dịch hoàn tác). Phiếu hủy vẫn giữ số của mình; số đó không được cấp lại.
32. **Chốt bù tháng cũ (US-009):** được chốt một tháng đã qua (ví dụ quên chốt), hệ thống tính hộ đã chuyển đến không sau ngày cuối tháng đó và khoản bắt buộc có hiệu lực trong tháng đó. Tuy nhiên chỉ tính được các hộ **còn đang ở tại lúc chốt**: CSDL không lưu ngày chuyển đi của hộ, nên hộ đã ở trong tháng đó nhưng chuyển đi trước lúc chốt bù sẽ không có khoản phải nộp của tháng đó. Vì vậy Ban quản trị nên chốt công nợ ngay trong tháng (Trang chính nhắc khi tháng hiện tại chưa chốt, giả định 15).
