-- BlueMoon - lược đồ MySQL 8. Mỗi sprint thêm bảng của chức năng mình làm.
CREATE DATABASE IF NOT EXISTS bluemoon
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bluemoon;

-- US-020, US-021, US-023: đăng ký, đăng nhập, đổi mật khẩu
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
    so_dien_thoai   VARCHAR(15)   NULL,
    ngay_chuyen_den DATE          NOT NULL,
    trang_thai      ENUM('DANG_O', 'DA_CHUYEN_DI') NOT NULL DEFAULT 'DANG_O',
    ngay_tao        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ma_dang_o       VARCHAR(20) AS
        (IF(trang_thai = 'DANG_O', ma_can_ho, NULL)) STORED,
    CONSTRAINT uq_ho_ma_dang_o UNIQUE (ma_dang_o),
    CONSTRAINT chk_ho_dien_tich CHECK (dien_tich > 0),
    CONSTRAINT chk_ho_tang CHECK (tang BETWEEN 1 AND 30),
    INDEX idx_ho_ma_can_ho (ma_can_ho)
);

-- US-002: nhan khau (RAW-002, RAW-003). Chu ho la nhan khau co quan_he
-- CHU_HO; cot sinh chu_ho_cua chi co gia tri o dong chu ho nen UNIQUE dam
-- bao moi ho toi da mot chu ho, ung dung them ho cung chu ho trong mot giao
-- dich nen moi ho co dung mot chu ho.
-- so_cccd NULL chi khi duoi 14 tuoi (ung dung kiem tra). Nguoi roi ho chi
-- doi trang_thai, khong xoa; CCCD chi duy nhat trong cac nhan khau DANG_O
-- (cot sinh cccd_dang_o), nen nguoi chuyen can/tach ho khai bao lai duoc.
-- Trang thai nam ngay trong nhan_khau vi cot sinh khong doc duoc bang ho.
CREATE TABLE IF NOT EXISTS nhan_khau (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    ho_id       INT          NOT NULL,
    ho_ten      VARCHAR(100) NOT NULL,
    ngay_sinh   DATE         NOT NULL,
    gioi_tinh   ENUM('NAM', 'NU') NOT NULL,
    so_cccd     CHAR(12)     NULL,
    quan_he     ENUM('CHU_HO', 'VO_CHONG', 'CON', 'KHAC') NOT NULL,
    trang_thai  ENUM('DANG_O', 'DA_CHUYEN_DI') NOT NULL DEFAULT 'DANG_O',
    -- US-003: loai dang ky cu tru, khong doi theo ngay. noi_thuong_tru bat
    -- buoc voi nguoi TAM_TRU (ung dung kiem tra).
    loai_cu_tru ENUM('THUONG_TRU', 'TAM_TRU') NOT NULL DEFAULT 'THUONG_TRU',
    noi_thuong_tru VARCHAR(200) NULL,
    ngay_tao    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    chu_ho_cua  INT AS (IF(quan_he = 'CHU_HO', ho_id, NULL)) STORED,
    cccd_dang_o CHAR(12) AS
        (IF(trang_thai = 'DANG_O', so_cccd, NULL)) STORED,
    CONSTRAINT fk_nk_ho FOREIGN KEY (ho_id) REFERENCES ho_gia_dinh (id),
    CONSTRAINT uq_nk_cccd_dang_o UNIQUE (cccd_dang_o),
    CONSTRAINT uq_nk_chu_ho UNIQUE (chu_ho_cua),
    INDEX idx_nk_so_cccd (so_cccd),
    INDEX idx_nk_ho_ten (ho_ten)
);

-- US-003: cac dot tam tru, tam vang (RAW-004, RAW-005). Moi dot co ngay bat
-- dau va ket thuc (AC-010, AC-011). "Dang tam tru/tam vang" tinh theo ngay
-- hom nay, khong luu thanh cot. Ung dung chan cac dot cua cung mot nguoi
-- chong ngay nhau (kiem tra trong giao dich co khoa dong nhan_khau).
CREATE TABLE IF NOT EXISTS tam_tru_tam_vang (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    nhan_khau_id INT          NOT NULL,
    loai         ENUM('TAM_TRU', 'TAM_VANG') NOT NULL,
    tu_ngay      DATE         NOT NULL,
    den_ngay     DATE         NOT NULL,
    ly_do        VARCHAR(200) NULL,
    ngay_tao     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ttv_nhan_khau FOREIGN KEY (nhan_khau_id)
        REFERENCES nhan_khau (id),
    CONSTRAINT chk_ttv_ngay CHECK (den_ngay >= tu_ngay),
    INDEX idx_ttv_nhan_khau (nhan_khau_id, tu_ngay)
);

-- Bảng sẽ thêm (xem docs/design/erd.md):
--   Sprint 2: khoan_phi, dot_thu, phai_nop (US-005, US-006, US-009)
--   Sprint 3: phieu_thu (US-010 đến US-014)
-- Quy ước: không dùng ON DELETE CASCADE; hộ ngừng quản lý chỉ đổi trạng thái.
