# Quy ước viết mã Java

| Mục | Quy ước |
|---|---|
| Định dạng | Thụt lề 4 dấu cách, không dùng tab; mỗi dòng tối đa 80 cột; luôn dùng `{}` cho `if/for/while` |
| Đặt tên | Lớp `PascalCase`, phương thức và biến `camelCase`, hằng `UPPER_SNAKE` |
| Ngôn ngữ trong mã | Tên lớp, biến, phương thức nghiệp vụ bằng tiếng Việt không dấu (`KhoanThu`, `tinhTongTien`), nhất quán toàn dự án |
| Chữ hiển thị cho người dùng | Tiếng Việt có dấu (nhãn, nút, thông báo lỗi) |
| Javadoc | Bắt buộc cho lớp và phương thức public; không bắt buộc cho getter và phương thức `@Override` đã có Javadoc ở interface |
| SQL | Luôn dùng `PreparedStatement`, không nối chuỗi dữ liệu người dùng vào câu lệnh |
| Dữ liệu nghiệp vụ | Không xóa cứng; đổi trạng thái để giữ lịch sử |
| Gỡ lỗi | Không để lại mã bị comment-out hay `System.out.println` |

Quy trình nhánh, commit và review: [workflow.md](../workflow.md).
