# Kiến trúc

Ứng dụng desktop JavaFX, dữ liệu tập trung ở MySQL (RAW-024, NFR-007).

```
View (FXML + CSS) -> Controller -> Service -> DAO -> MySQL
                                      |
                                    Model
```

| Tầng | Trách nhiệm | Không được làm |
|---|---|---|
| View (FXML) | Bố cục, kiểu dáng | Chứa logic nghiệp vụ |
| Controller | Nhận sự kiện, gọi service, hiển thị kết quả/lỗi | Gọi SQL, tính toán nghiệp vụ |
| Service | Quy tắc nghiệp vụ, kiểm tra hợp lệ | Biết về JavaFX |
| DAO | SQL qua PreparedStatement | Chứa quy tắc nghiệp vụ |
| Model | Dữ liệu thuần | Truy cập CSDL, in ấn |

## Quy ước
- Mỗi chức năng: `KhoanThu` (model), `KhoanThuDao`/`KhoanThuDaoImpl`,
  `KhoanThuService`, `KhoanThuController`, `khoan-thu.fxml`.
- Service nhận DAO qua constructor để test bằng DAO giả (xem
  `TaiKhoanServiceTest`).
- Luôn viết test cho service trước khi sửa/refactor.
- SQL luôn dùng `PreparedStatement`, không nối chuỗi từ dữ liệu người dùng.
- Không xóa cứng dữ liệu nghiệp vụ; đổi trạng thái để giữ lịch sử.

## Giao diện (theo hướng dẫn giảng viên)
- Đăng nhập, tạo/thu khoản phí: 1120x645. Màn hình chính: 1440x1024.
- Chọn một layout thống nhất (đầu-thân hoặc trái-đầu-thân) cho toàn app.
- Palette màu và bảng đặc tả giao diện lưu ở `docs/design/`.
