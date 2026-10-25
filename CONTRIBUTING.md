# Quy trình đóng góp – Nhóm 16

Tài liệu này áp dụng cho cả 5 thành viên. Mục tiêu: mỗi phần việc đều để lại
vết rõ ràng trên GitHub (Issue, nhánh, commit, Pull Request, review), đúng
người làm.

## 1. Cấu hình Git một lần trên máy

Email phải trùng với email **đã xác minh** trong tài khoản GitHub của chính
bạn (GitHub → Settings → Emails). Sai email thì commit bị tính là
"Anonymous" hoặc tách thành hai người.

```
git config --global user.name "Ho Ten Cua Ban"
git config --global user.email "email-da-xac-minh@example.com"
git config user.email
```

Không sửa file trực tiếp trên giao diện web của GitHub: web tự đặt message
kiểu `Update README.md`, không rõ nội dung.

## 2. Luồng làm việc cho một User Story

1. Nhận Issue được giao (Assignee là mình).
2. Cập nhật `main` và tạo nhánh:
   ```
   git checkout main
   git pull
   git checkout -b feature/US-005-tinh-phi-dich-vu
   ```
3. Code, chạy test (Maven → Lifecycle → `test`), commit nhỏ, thường xuyên.
4. Push nhánh và mở Pull Request vào `main`. Mô tả theo template, ghi
   `Closes #<số issue>`.
5. Ít nhất **1 thành viên khác** review: đọc diff, chạy thử, để lại nhận
   xét, bấm **Approve**. Người review không phải là người viết code.
6. CI xanh và có Approve mới merge. Merge xong xóa nhánh.

Tên nhánh: `feature/US-xxx-<ten>`, `fix/<ten-loi>`, `docs/<noi-dung>`,
`test/<noi-dung>`, `chore/<noi-dung>`.

## 3. Quy ước commit message

```
<loai>(<pham-vi>): <mo ta ngan, dong tu o dau>
```

| Loại | Dùng khi | Ví dụ |
|---|---|---|
| `feat` | Thêm chức năng | `feat(US-005): tinh phi dich vu theo dien tich` |
| `fix` | Sửa lỗi | `fix(US-002): chan trung CCCD khi hai may cung luu` |
| `test` | Thêm/sửa test | `test(US-009): them test chot cong no dau thang` |
| `docs` | Tài liệu | `docs: cap nhat ERD bang khoan_phi` |
| `refactor` | Đổi cấu trúc, không đổi hành vi | `refactor: tach KiemTraNhanKhau khoi service` |
| `chore` | Cấu hình, build | `chore: them plugin dong goi jar` |

Không dùng message mơ hồ: `fix`, `update`, `sua code`, `ok`, `Update X.md`.

## 4. Ghi công khi làm chung

Khi hai người cùng làm một commit (pair programming, một người soạn tài
liệu còn người kia commit), thêm dòng cuối message, cách dòng trống:

```
docs: them bang dac ta man hinh thu phi

Co-authored-by: Ho Ten <email-github-cua-nguoi-do>
```

GitHub ghi công cho cả hai. Chỉ ghi người thực sự tham gia.

## 5. Việc không hiện trong Git

Viết báo cáo, làm slide, họp, review, phỏng vấn khách hàng không có trong
`git log`. Ghi lại ở:

- `docs/meeting-notes/yyyy-mm-dd-<noi-dung>.md`: biên bản mỗi buổi họp,
  ghi rõ việc của từng người.

Không viết lại lịch sử `main` (không force-push, không rebase nhánh đã
push).

## 6. Không được commit

Mật khẩu, `config/db.properties`, file build (`target/`), file IDE. Xem
`.gitignore`.
