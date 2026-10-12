-- Dữ liệu mẫu cho môi trường dev. Tài khoản: admin / Admin@123
-- ĐỔI MẬT KHẨU này nếu triển khai thật.
USE bluemoon;

INSERT INTO tai_khoan (ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro)
VALUES ('admin',
        '$2a$12$a/XOhCEY60CX9B/MclBSt.lsuUfKkP5zsjSKMMVOeiEnhNhKQNjCC',
        'Quản trị viên', 'QUAN_TRI')
ON DUPLICATE KEY UPDATE ho_ten = VALUES(ho_ten);
