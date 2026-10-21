-- Dữ liệu mẫu cho môi trường dev. Tài khoản: admin / Admin@123
-- ĐỔI MẬT KHẨU này nếu triển khai thật.
USE bluemoon;

INSERT IGNORE INTO tai_khoan (ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro)
VALUES ('admin',
        '$2a$12$a/XOhCEY60CX9B/MclBSt.lsuUfKkP5zsjSKMMVOeiEnhNhKQNjCC',
        'Quản trị viên', 'QUAN_TRI');

-- Ho gia dinh mau (US-001)
INSERT IGNORE INTO ho_gia_dinh
    (ma_can_ho, loai_can_ho, tang, dien_tich, ten_chu_ho, so_dien_thoai,
     ngay_chuyen_den)
VALUES ('K01', 'KIOT', 1, 35.00, 'Tran Van Hung', '0912345678',
        '2023-06-01'),
       ('A0601', 'CAN_HO', 6, 75.50, 'Nguyen Thi Lan', '0987654321',
        '2023-07-15'),
       ('A1205', 'CAN_HO', 12, 92.00, 'Le Minh Quan', NULL, '2024-01-10'),
       ('PH01', 'PENTHOUSE', 30, 250.00, 'Pham Duc Anh', '0901234567',
        '2023-09-20');
