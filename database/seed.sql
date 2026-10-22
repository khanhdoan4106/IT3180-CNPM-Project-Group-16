-- Dữ liệu mẫu cho môi trường dev. Tài khoản: admin / Admin@123
-- ĐỔI MẬT KHẨU này nếu triển khai thật.
USE bluemoon;

INSERT IGNORE INTO tai_khoan (ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro)
VALUES ('admin',
        '$2a$12$a/XOhCEY60CX9B/MclBSt.lsuUfKkP5zsjSKMMVOeiEnhNhKQNjCC',
        'Quản trị viên', 'QUAN_TRI');

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
FROM (SELECT 'K01' AS ma, 'Tran Van Hung' AS ho_ten,
             '1975-03-12' AS ngay_sinh, 'NAM' AS gioi_tinh,
             '001075001234' AS so_cccd, 'CHU_HO' AS quan_he
      UNION ALL SELECT 'A0601', 'Nguyen Thi Lan', '1982-08-25', 'NU',
             '001182004567', 'CHU_HO'
      UNION ALL SELECT 'A0601', 'Hoang Van Nam', '1980-01-30', 'NAM',
             '001080007890', 'VO_CHONG'
      UNION ALL SELECT 'A0601', 'Hoang Minh Chau', '2010-05-14', 'NU',
             '001310002345', 'CON'
      UNION ALL SELECT 'A1205', 'Le Minh Quan', '1990-11-02', 'NAM',
             '001090005678', 'CHU_HO'
      UNION ALL SELECT 'PH01', 'Pham Duc Anh', '1968-07-19', 'NAM',
             '001068003456', 'CHU_HO'
      UNION ALL SELECT 'PH01', 'Pham Thu Ha', '1995-04-08', 'NU',
             '001195006789', 'CON') AS d
JOIN ho_gia_dinh h ON h.ma_dang_o = d.ma;

-- Nguoi tam tru mau (US-003), so CCCD theo tinh thuong tru (036 Nam Dinh,
-- 034 Thai Binh). Chay lai khong sinh trung (INSERT IGNORE theo CCCD).
INSERT IGNORE INTO nhan_khau
    (ho_id, ho_ten, ngay_sinh, gioi_tinh, so_cccd, quan_he, loai_cu_tru,
     noi_thuong_tru)
SELECT h.id, d.ho_ten, d.ngay_sinh, d.gioi_tinh, d.so_cccd, 'KHAC',
       'TAM_TRU', d.noi
FROM (SELECT 'K01' AS ma, 'Vu Van Toan' AS ho_ten,
             '1993-09-21' AS ngay_sinh, 'NAM' AS gioi_tinh,
             '036093004321' AS so_cccd,
             'Xa Hai Phong, huyen Hai Hau, Nam Dinh' AS noi
      UNION ALL SELECT 'PH01', 'Do Thi Hoa', '1999-02-14', 'NU',
             '034199008765', 'Phuong Tran Lam, TP Thai Binh, Thai Binh') AS d
JOIN ho_gia_dinh h ON h.ma_dang_o = d.ma;

-- Dot tam tru/tam vang mau (US-003), tinh theo ngay chay seed de luc nao
-- cung thay du ba truong hop: Vu Van Toan tam tru con han, Do Thi Hoa tam
-- tru da qua han (to do), Hoang Van Nam dang tam vang. Moi nguoi chi them
-- mot dot neu chua co dot cung loai, nen chay lai khong sinh trung.
INSERT INTO tam_tru_tam_vang (nhan_khau_id, loai, tu_ngay, den_ngay, ly_do)
SELECT n.id, d.loai, CURDATE() + INTERVAL d.tu DAY,
       CURDATE() + INTERVAL d.den DAY, d.ly_do
FROM (SELECT '036093004321' AS cccd, 'TAM_TRU' AS loai, -60 AS tu,
             120 AS den, 'Thue nha lam viec' AS ly_do
      UNION ALL SELECT '034199008765', 'TAM_TRU', -200, -10, 'Di hoc'
      UNION ALL SELECT '001080007890', 'TAM_VANG', -5, 25,
             'Cong tac nuoc ngoai') AS d
JOIN nhan_khau n ON n.cccd_dang_o = d.cccd
WHERE NOT EXISTS (SELECT 1 FROM tam_tru_tam_vang t
                  WHERE t.nhan_khau_id = n.id AND t.loai = d.loai);
