-- BlueMoon - lược đồ MySQL 8. Mỗi sprint thêm bảng của chức năng mình làm.
CREATE DATABASE IF NOT EXISTS bluemoon
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bluemoon;

-- US-01, US-03: đăng nhập, đổi mật khẩu
CREATE TABLE IF NOT EXISTS tai_khoan (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    ten_dang_nhap VARCHAR(50)  NOT NULL UNIQUE,
    mat_khau_bam  VARCHAR(100) NOT NULL,
    ho_ten        VARCHAR(100) NOT NULL,
    vai_tro       ENUM('QUAN_TRI', 'THU_QUY') NOT NULL,
    hoat_dong     BOOLEAN      NOT NULL DEFAULT TRUE,
    ngay_tao      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- TODO (Sprint 1): ho_gia_dinh (US-04..06)
-- TODO (Sprint 2): nhan_khau, khoan_phi
-- TODO (Sprint 3): dot_thu, phai_nop, phieu_thu
-- Quy ước: không dùng ON DELETE CASCADE; hộ ngừng quản lý chỉ đổi trạng thái.
