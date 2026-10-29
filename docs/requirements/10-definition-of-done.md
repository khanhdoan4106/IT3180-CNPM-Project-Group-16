# 10. Definition of Done

Một story chỉ được đánh dấu **Hoàn thành** trong Product Backlog khi đạt đủ
các tiêu chí sau.

| # | Tiêu chí | Cách kiểm tra |
|---|---|---|
| 1 | Mọi Acceptance Criteria của story được đáp ứng | Mỗi AC có test tự động hoặc bước kiểm thử tay ghi trong Pull Request |
| 2 | Quy tắc nghiệp vụ có unit test ở tầng Service; toàn bộ test pass | CI (`mvn verify`) xanh trên Pull Request |
| 3 | Dữ liệu ghi đúng vào MySQL, chạy được với `schema.sql` và `seed.sql` hiện tại | Người review chạy thử ứng dụng trên máy mình |
| 4 | Code theo [quy ước mã](../design/coding-convention.md), không lộ mật khẩu hay cấu hình cá nhân | Review Pull Request |
| 5 | Ít nhất một thành viên khác review và Approve trước khi merge | Lịch sử review trên Pull Request |
| 6 | Tài liệu liên quan được cập nhật (ERD, sơ đồ lớp, setup, backlog) | Có trong cùng Pull Request |

Nghiệm thu với đại diện Ban quản trị (UAT) làm theo mốc của dự án, không
theo từng story: UAT lần 1 với bản `v0.3.0`, UAT lần 2 với bản `v1.0.0`.
