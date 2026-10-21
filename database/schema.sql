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

-- US-001: quan ly ho gia dinh (RAW-001, RAW-010, RAW-030, RAW-033).
-- Ho chuyen di chi doi trang_thai, khong xoa. Ma can ho chi duy nhat trong
-- cac ho DANG_O (cot sinh ma_dang_o), nen can ho cu nhan duoc ho moi.
CREATE TABLE IF NOT EXISTS ho_gia_dinh (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    ma_can_ho       VARCHAR(20)   NOT NULL,
    loai_can_ho     ENUM('CAN_HO', 'KIOT', 'PENTHOUSE') NOT NULL,
    tang            TINYINT       NOT NULL,
    dien_tich       DECIMAL(6, 2) NOT NULL,
    ten_chu_ho      VARCHAR(100)  NOT NULL,
    so_dien_thoai   VARCHAR(15)   NULL,
    ngay_chuyen_den DATE          NOT NULL,
    trang_thai      ENUM('DANG_O', 'DA_CHUYEN_DI') NOT NULL DEFAULT 'DANG_O',
    ngay_tao        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ma_dang_o       VARCHAR(20) AS
        (IF(trang_thai = 'DANG_O', ma_can_ho, NULL)) STORED,
    CONSTRAINT uq_ho_ma_dang_o UNIQUE (ma_dang_o),
    CONSTRAINT chk_ho_dien_tich CHECK (dien_tich > 0),
    CONSTRAINT chk_ho_tang CHECK (tang BETWEEN 1 AND 30),
    INDEX idx_ho_ma_can_ho (ma_can_ho),
    INDEX idx_ho_ten_chu_ho (ten_chu_ho)
);

-- TODO (Sprint 1): nhan_khau, tam_tru_tam_vang (US-002, US-003)
-- TODO (Sprint 2): khoan_phi
-- TODO (Sprint 3): dot_thu, phai_nop, phieu_thu
-- Quy ước: không dùng ON DELETE CASCADE; hộ ngừng quản lý chỉ đổi trạng thái.
