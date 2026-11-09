-- Dữ liệu mẫu cho môi trường dev. Tài khoản: admin / Admin@123
-- ĐỔI MẬT KHẨU này nếu triển khai thật.
USE bluemoon;
-- Doc file theo UTF-8 du may chay mysql dang dung bang ma khac.
SET NAMES utf8mb4;

INSERT IGNORE INTO tai_khoan (ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro)
VALUES ('admin',
        '$2a$12$a/XOhCEY60CX9B/MclBSt.lsuUfKkP5zsjSKMMVOeiEnhNhKQNjCC',
        'Nguyễn Thị Thu Hương', 'QUAN_TRI');

-- Ho gia dinh mau (US-001)
INSERT IGNORE INTO ho_gia_dinh
    (ma_can_ho, loai_can_ho, tang, dien_tich, so_dien_thoai,
     ngay_chuyen_den)
VALUES ('K01', 'KIOT', 1, 35.00, '0912345678', '2023-06-01'),
       ('A0601', 'CAN_HO', 6, 75.50, '0987654321', '2023-07-15'),
       ('A1205', 'CAN_HO', 12, 92.00, NULL, '2024-01-10'),
       ('PH01', 'PENTHOUSE', 30, 250.00, '0901234567', '2023-09-20');

-- Nhan khau mau (US-002): moi ho mot chu ho. Dong nao cung co so CCCD de
-- chay lai file nay khong sinh trung (INSERT IGNORE bo qua CCCD da co).
INSERT IGNORE INTO nhan_khau
    (ho_id, ho_ten, ngay_sinh, gioi_tinh, so_cccd, quan_he)
SELECT h.id, d.ho_ten, d.ngay_sinh, d.gioi_tinh, d.so_cccd, d.quan_he
FROM (SELECT 'K01' AS ma, 'Trần Văn Hùng' AS ho_ten,
             '1975-03-12' AS ngay_sinh, 'NAM' AS gioi_tinh,
             '001075001234' AS so_cccd, 'CHU_HO' AS quan_he
      UNION ALL SELECT 'A0601', 'Nguyễn Thị Lan', '1982-08-25', 'NU',
             '001182004567', 'CHU_HO'
      UNION ALL SELECT 'A0601', 'Hoàng Văn Nam', '1980-01-30', 'NAM',
             '001080007890', 'VO_CHONG'
      UNION ALL SELECT 'A0601', 'Hoàng Minh Châu', '2010-05-14', 'NU',
             '001310002345', 'CON'
      UNION ALL SELECT 'A1205', 'Lê Minh Quân', '1990-11-02', 'NAM',
             '001090005678', 'CHU_HO'
      UNION ALL SELECT 'PH01', 'Phạm Đức Anh', '1968-07-19', 'NAM',
             '001068003456', 'CHU_HO'
      UNION ALL SELECT 'PH01', 'Phạm Thu Hà', '1995-04-08', 'NU',
             '001195006789', 'CON') AS d
JOIN ho_gia_dinh h ON h.ma_dang_o = d.ma;

-- Nguoi tam tru mau (US-003), so CCCD theo tinh thuong tru (036 Nam Dinh,
-- 034 Thai Binh). Chay lai khong sinh trung (INSERT IGNORE theo CCCD).
INSERT IGNORE INTO nhan_khau
    (ho_id, ho_ten, ngay_sinh, gioi_tinh, so_cccd, quan_he, loai_cu_tru,
     noi_thuong_tru)
SELECT h.id, d.ho_ten, d.ngay_sinh, d.gioi_tinh, d.so_cccd, 'KHAC',
       'TAM_TRU', d.noi
FROM (SELECT 'K01' AS ma, 'Vũ Văn Toàn' AS ho_ten,
             '1993-09-21' AS ngay_sinh, 'NAM' AS gioi_tinh,
             '036093004321' AS so_cccd,
             'Xã Hải Phong, huyện Hải Hậu, Nam Định' AS noi
      UNION ALL SELECT 'PH01', 'Đỗ Thị Hoa', '1999-02-14', 'NU',
             '034199008765', 'Phường Trần Lãm, TP Thái Bình, Thái Bình') AS d
JOIN ho_gia_dinh h ON h.ma_dang_o = d.ma;

-- Dot tam tru/tam vang mau (US-003), tinh theo ngay chay seed de luc nao
-- cung thay du ba truong hop: Vu Van Toan tam tru con han, Do Thi Hoa tam
-- tru da qua han (to do), Hoang Van Nam dang tam vang (ten co dau o du lieu). Moi nguoi chi them
-- mot dot neu chua co dot cung loai, nen chay lai khong sinh trung.
INSERT INTO tam_tru_tam_vang (nhan_khau_id, loai, tu_ngay, den_ngay, ly_do)
SELECT n.id, d.loai, CURDATE() + INTERVAL d.tu DAY,
       CURDATE() + INTERVAL d.den DAY, d.ly_do
FROM (SELECT '036093004321' AS cccd, 'TAM_TRU' AS loai, -60 AS tu,
             120 AS den, 'Thuê nhà làm việc' AS ly_do
      UNION ALL SELECT '034199008765', 'TAM_TRU', -200, -10, 'Đi học'
      UNION ALL SELECT '001080007890', 'TAM_VANG', -5, 25,
             'Công tác nước ngoài') AS d
JOIN nhan_khau n ON n.cccd_dang_o = d.cccd
WHERE NOT EXISTS (SELECT 1 FROM tam_tru_tam_vang t
                  WHERE t.nhan_khau_id = n.id AND t.loai = d.loai);

-- Danh muc khoan thu mau (US-005, US-006). Phi quan ly mac dinh 7.000 d/m2
-- (AC-016); don gia phi dich vu 5.000 d/m2 chi la vi du trong khoang
-- 2.500-16.500 (AC-015). Chay lai khong sinh trung (INSERT IGNORE theo
-- UNIQUE ten + tu_ngay).
INSERT IGNORE INTO khoan_phi
    (ten, loai, cach_tinh, don_gia, tu_ngay, den_ngay)
VALUES ('Phí dịch vụ', 'BAT_BUOC', 'THEO_DIEN_TICH', 5000, '2026-01-01',
        NULL),
       ('Phí quản lý', 'BAT_BUOC', 'THEO_DIEN_TICH', 7000, '2026-01-01',
        NULL);

-- Hai quy tu nguyen mau luon dang mo khi chay seed: tu dau thang nay den
-- cuoi thang sau. Ten khong co nam cung; "Quy vi nguoi ngheo <nam>" lay
-- nam cua ngay chay seed. Chay lai khong sinh trung vi kiem tra theo ten.
INSERT INTO khoan_phi (ten, loai, cach_tinh, don_gia, tu_ngay, den_ngay)
SELECT d.ten, 'TU_NGUYEN', d.cach_tinh, d.don_gia,
       DATE_FORMAT(CURDATE(), '%Y-%m-01'),
       LAST_DAY(CURDATE() + INTERVAL 1 MONTH)
FROM (SELECT CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())) AS ten,
             'KHONG_CO_DINH' AS cach_tinh, NULL AS don_gia
      UNION ALL SELECT 'Quỹ biển đảo', 'CO_DINH_THEO_HO', 50000) AS d
WHERE NOT EXISTS (SELECT 1 FROM khoan_phi k WHERE k.ten = d.ten);

-- Lich su bien dong mau (US-027) cho du lieu tren: moi nhan khau chuyen den
-- tu ngay ho chuyen den (nguoi tam tru: tu ngay bat dau dot tam tru dau), moi dot tam tru/tam vang mot dong. Chay lai khong
-- sinh trung.
INSERT INTO bien_dong_nhan_khau (ho_id, nhan_khau_id, loai, ngay, ghi_chu,
                                 nguoi_ghi)
SELECT n.ho_id, n.id, 'CHUYEN_DEN',
       CASE WHEN n.loai_cu_tru = 'TAM_TRU'
            THEN COALESCE((SELECT MIN(t.tu_ngay) FROM tam_tru_tam_vang t
                           WHERE t.nhan_khau_id = n.id
                             AND t.loai = 'TAM_TRU'), h.ngay_chuyen_den)
            ELSE h.ngay_chuyen_den END, 'Dữ liệu mẫu',
       (SELECT id FROM tai_khoan WHERE ten_dang_nhap = 'admin')
FROM nhan_khau n JOIN ho_gia_dinh h ON h.id = n.ho_id
WHERE NOT EXISTS (SELECT 1 FROM bien_dong_nhan_khau b
                  WHERE b.nhan_khau_id = n.id AND b.loai = 'CHUYEN_DEN');

INSERT INTO bien_dong_nhan_khau (ho_id, nhan_khau_id, loai, ngay, ghi_chu,
                                 nguoi_ghi)
SELECT n.ho_id, n.id, t.loai, t.tu_ngay,
       CONCAT('Đến ', DATE_FORMAT(t.den_ngay, '%d/%m/%Y')),
       (SELECT id FROM tai_khoan WHERE ten_dang_nhap = 'admin')
FROM tam_tru_tam_vang t JOIN nhan_khau n ON n.id = t.nhan_khau_id
WHERE NOT EXISTS (SELECT 1 FROM bien_dong_nhan_khau b
                  WHERE b.nhan_khau_id = n.id AND b.loai = t.loai
                    AND b.ngay = t.tu_ngay);
