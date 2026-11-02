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
    -- US-023: so dien thoai lien he, khong bat buoc
    so_dien_thoai VARCHAR(15)  NULL,
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

-- US-027: lich su bien dong nhan khau trong ho (Bo bai tap ch.7: them/xoa,
-- thoi gian). Ghi trong cung giao dich voi thao tac tren ho, nhan khau, dot
-- tam tru/tam vang; khong sua, khong xoa.
CREATE TABLE IF NOT EXISTS bien_dong_nhan_khau (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    ho_id        INT          NOT NULL,
    nhan_khau_id INT          NOT NULL,
    loai         ENUM('CHUYEN_DEN', 'CHUYEN_DI', 'TAM_TRU', 'TAM_VANG',
                      'DOI_CHU_HO') NOT NULL,
    ngay         DATE         NOT NULL,
    ghi_chu      VARCHAR(200) NULL,
    nguoi_ghi    INT          NOT NULL,
    thoi_diem    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bd_ho FOREIGN KEY (ho_id) REFERENCES ho_gia_dinh (id),
    CONSTRAINT fk_bd_nhan_khau FOREIGN KEY (nhan_khau_id)
        REFERENCES nhan_khau (id),
    CONSTRAINT fk_bd_nguoi_ghi FOREIGN KEY (nguoi_ghi)
        REFERENCES tai_khoan (id),
    INDEX idx_bd_ho (ho_id, ngay),
    INDEX idx_bd_ngay (ngay)
);

-- US-005, US-006: danh mục khoản thu. Khoản bắt buộc (phí dịch vụ, phí
-- quản lý) tính theo diện tích × đơn giá (đ/m²). Khoản tự nguyện có thời
-- gian thu riêng (bắt buộc có ngày kết thúc); số tiền mỗi hộ có thể cố
-- định hoặc để trống (AC-021).
-- Một tên chỉ dùng một lần trong cùng đợt, tức cùng ngày bắt đầu (AC-020).
-- Đổi đơn giá chỉ ảnh hưởng các tháng chốt sau; tháng đã chốt giữ số cũ
-- trong phai_nop. Khoản ngừng thu chỉ đổi trạng thái, không xóa.
CREATE TABLE IF NOT EXISTS khoan_phi (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    ten        VARCHAR(100)  NOT NULL,
    loai       ENUM('BAT_BUOC', 'TU_NGUYEN') NOT NULL,
    cach_tinh  ENUM('THEO_DIEN_TICH', 'CO_DINH_THEO_HO', 'KHONG_CO_DINH')
                             NOT NULL,
    don_gia    DECIMAL(12, 2) NULL,
    tu_ngay    DATE          NOT NULL,
    den_ngay   DATE          NULL,
    trang_thai ENUM('DANG_THU', 'DA_DONG') NOT NULL DEFAULT 'DANG_THU',
    ngay_tao   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_kp_ten_dot UNIQUE (ten, tu_ngay),
    CONSTRAINT chk_kp_don_gia CHECK (
        (cach_tinh = 'KHONG_CO_DINH' AND don_gia IS NULL)
        OR (cach_tinh <> 'KHONG_CO_DINH' AND don_gia IS NOT NULL
            AND don_gia > 0)),
    CONSTRAINT chk_kp_bat_buoc CHECK (
        loai = 'TU_NGUYEN' OR cach_tinh = 'THEO_DIEN_TICH'),
    CONSTRAINT chk_kp_ngay CHECK (
        (loai = 'BAT_BUOC' OR den_ngay IS NOT NULL)
        AND (den_ngay IS NULL OR den_ngay >= tu_ngay))
);

-- US-009: mỗi tháng một đợt chốt công nợ (UNIQUE thang). Chốt lại cùng tháng
-- chỉ bổ sung hộ/khoản còn thiếu, không tạo trùng (AC-028).
CREATE TABLE IF NOT EXISTS dot_thu (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    thang       DATE     NOT NULL,
    nguoi_chot  INT      NOT NULL,
    ngay_chot   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dt_nguoi_chot FOREIGN KEY (nguoi_chot)
        REFERENCES tai_khoan (id),
    CONSTRAINT uq_dt_thang UNIQUE (thang),
    CONSTRAINT chk_dt_ngay_dau_thang CHECK (DAY(thang) = 1)
);

-- US-009, US-008: khoản phải nộp của từng hộ trong một tháng đã chốt (AC-027).
-- Lưu lại diện tích và đơn giá lúc chốt để đối chiếu (AC-018). Sửa giá trị
-- (US-008) đổi so_tien, giữ so_tien_goc và ghi người sửa, lý do. Từ Sprint 3,
-- khoản đã có phiếu thu thì không sửa được (AC-026).
CREATE TABLE IF NOT EXISTS phai_nop (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    dot_thu_id   INT           NOT NULL,
    ho_id        INT           NOT NULL,
    khoan_phi_id INT           NOT NULL,
    dien_tich    DECIMAL(6, 2)  NOT NULL,
    don_gia      DECIMAL(12, 2) NOT NULL,
    so_tien_goc  DECIMAL(14, 2) NOT NULL,
    so_tien      DECIMAL(14, 2) NOT NULL,
    nguoi_sua    INT           NULL,
    ngay_sua     DATETIME      NULL,
    ghi_chu      VARCHAR(200)  NULL,
    CONSTRAINT fk_pn_dot_thu FOREIGN KEY (dot_thu_id) REFERENCES dot_thu (id),
    CONSTRAINT fk_pn_ho FOREIGN KEY (ho_id) REFERENCES ho_gia_dinh (id),
    CONSTRAINT fk_pn_khoan_phi FOREIGN KEY (khoan_phi_id)
        REFERENCES khoan_phi (id),
    CONSTRAINT fk_pn_nguoi_sua FOREIGN KEY (nguoi_sua)
        REFERENCES tai_khoan (id),
    CONSTRAINT uq_pn_dot_ho_khoan UNIQUE (dot_thu_id, ho_id, khoan_phi_id),
    CONSTRAINT chk_pn_so_tien CHECK (so_tien >= 0 AND so_tien_goc >= 0),
    INDEX idx_pn_ho (ho_id, dot_thu_id)
);

-- Bảng sẽ thêm (xem docs/design/erd.md):
--   Sprint 3: phieu_thu (US-010 đến US-014)
-- Quy ước: không dùng ON DELETE CASCADE; hộ ngừng quản lý chỉ đổi trạng thái.
