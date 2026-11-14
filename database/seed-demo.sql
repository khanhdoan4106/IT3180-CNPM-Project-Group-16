-- Dữ liệu DEMO cho BlueMoon. CHỈ CHẠY MỘT LẦN, trên CSDL vừa dựng lại từ
-- schema.sql + seed.sql (docs/setup.md, mục 5). Không dùng cho môi trường thật.
--
-- Sinh bằng scripts/sinh-seed-demo.py (không sửa tay tệp này). Mọi người, số
-- CCCD, số điện thoại đều hư cấu. Ngày tháng tính theo CURDATE() lúc nạp:
-- chốt công nợ 6 tháng gần nhất (tháng này chỉ chốt và thu từ ngày 3 trong
-- tháng), phiếu thu rải trong từng tháng.
--
-- Chạy lại an toàn: nếu CSDL đã có hộ demo, đã có đợt chốt hay phiếu thu,
-- hoặc chưa chạy seed.sql, tệp không thêm gì (biến @chay = 0) và dòng cuối
-- báo lý do.
--
-- Tài khoản: admin / Admin@123 (seed.sql), thuquy / Thuquy@123 (Thủ quỹ).
USE bluemoon;
SET NAMES utf8mb4;
-- Giờ Việt Nam như ứng dụng (Database.getConnection).
SET time_zone = '+07:00';

START TRANSACTION;

SET @admin = (SELECT id FROM tai_khoan WHERE ten_dang_nhap = 'admin');
SET @chay = @admin IS NOT NULL
    AND NOT EXISTS (SELECT 1 FROM ho_gia_dinh WHERE ma_can_ho = 'B0907')
    AND NOT EXISTS (SELECT 1 FROM dot_thu)
    AND NOT EXISTS (SELECT 1 FROM phieu_thu)
    AND EXISTS (SELECT 1 FROM khoan_phi WHERE ten = 'Phí dịch vụ');
-- Tháng này chỉ chốt và thu khi đã qua ngày 2 (chốt đầu tháng vào ngày 2).
SET @thang_nay = DAY(CURDATE()) >= 3;

-- Thủ quỹ mẫu (US-020), tạo 7 tháng trước.
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro,
                       so_dien_thoai, ngay_tao)
SELECT 'thuquy', '$2a$12$Xq/3XeEhByeKFDZDqGS3euFaQOyguLh/YfLbYhTVbvHsybcfpUtGi', 'Trần Thị Bình', 'THU_QUY', '0912004455',
       TIMESTAMP(CURDATE() - INTERVAL 7 MONTH, '08:30:00')
FROM DUAL WHERE @chay
  AND NOT EXISTS (SELECT 1 FROM tai_khoan WHERE ten_dang_nhap = 'thuquy');
SET @thuquy = (SELECT id FROM tai_khoan WHERE ten_dang_nhap = 'thuquy');

-- ---------- Hộ gia đình (US-001) ----------
DROP TEMPORARY TABLE IF EXISTS demo_ho;
CREATE TEMPORARY TABLE demo_ho (
    ma VARCHAR(20) PRIMARY KEY, loai VARCHAR(10), tang INT,
    dien_tich DECIMAL(6, 2), sdt VARCHAR(15), ngay_den DATE,
    chuyen_di DATE NULL);
INSERT INTO demo_ho VALUES
  ('K02', 'KIOT', 1, 25.20, '0762633522', '2020-09-25', NULL),
  ('K03', 'KIOT', 1, 25.33, '0749677843', '2024-02-07', NULL),
  ('K04', 'KIOT', 1, 33.37, '0820771657', '2021-01-11', NULL),
  ('B0907', 'CAN_HO', 9, 74.28, '0350466696', '2022-09-14', NULL),
  ('C1508', 'CAN_HO', 15, 91.40, '0358197822', '2025-08-19', NULL),
  ('A2203', 'CAN_HO', 22, 95.90, '0369370789', '2021-08-23', NULL),
  ('B1104', 'CAN_HO', 11, 58.27, '0716120303', '2021-04-10', DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 27 DAY),
  ('C0806', 'CAN_HO', 8, 87.21, '0313617158', '2021-10-10', NULL),
  ('A1702', 'CAN_HO', 17, 75.87, NULL, '2022-02-01', NULL),
  ('B2005', 'CAN_HO', 20, 79.20, '0954249537', '2023-06-25', NULL),
  ('B1702', 'CAN_HO', 17, 66.72, NULL, DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 0 DAY, NULL),
  ('C1502', 'CAN_HO', 15, 71.70, '0356503709', DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 0 DAY, NULL),
  ('C2205', 'CAN_HO', 22, 101.59, '0984269694', DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 0 DAY, NULL),
  ('B1903', 'CAN_HO', 19, 112.03, NULL, DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 0 DAY, NULL),
  ('C2705', 'CAN_HO', 27, 85.42, NULL, '2018-07-13', NULL),
  ('A2706', 'CAN_HO', 27, 100.30, '0321771545', '2022-05-02', NULL),
  ('B1907', 'CAN_HO', 19, 111.54, '0950272241', '2020-03-01', NULL),
  ('A1708', 'CAN_HO', 17, 110.55, '0735241709', '2023-08-04', NULL),
  ('A1808', 'CAN_HO', 18, 94.66, '0767950690', '2022-05-25', NULL),
  ('B2105', 'CAN_HO', 21, 105.24, '0359280182', '2023-07-27', NULL),
  ('B2308', 'CAN_HO', 23, 92.47, '0356966881', '2025-11-28', NULL),
  ('C2101', 'CAN_HO', 21, 101.80, '0843837345', '2023-01-02', NULL),
  ('C1203', 'CAN_HO', 12, 96.09, '0903798947', '2024-12-17', NULL),
  ('A2702', 'CAN_HO', 27, 72.46, '0726673639', '2018-08-26', NULL),
  ('C2407', 'CAN_HO', 24, 50.86, '0334559740', '2023-06-06', NULL),
  ('B0908', 'CAN_HO', 9, 95.89, NULL, '2025-03-18', NULL),
  ('A0808', 'CAN_HO', 8, 57.62, '0391258168', '2023-03-15', NULL),
  ('C2005', 'CAN_HO', 20, 82.74, NULL, '2023-01-06', NULL),
  ('C1705', 'CAN_HO', 17, 81.14, NULL, '2025-09-20', NULL),
  ('C2508', 'CAN_HO', 25, 79.57, '0937059680', '2022-11-03', NULL),
  ('B1804', 'CAN_HO', 18, 85.56, '0818028205', '2025-10-16', NULL),
  ('B1301', 'CAN_HO', 13, 102.66, '0764805280', '2020-08-07', NULL),
  ('A0908', 'CAN_HO', 9, 63.32, '0370426917', '2024-07-01', NULL),
  ('C2006', 'CAN_HO', 20, 50.35, '0373477995', '2018-08-09', NULL),
  ('B1406', 'CAN_HO', 14, 96.80, '0784458990', '2016-08-20', NULL),
  ('C1003', 'CAN_HO', 10, 71.25, '0790037885', '2020-11-02', NULL);

INSERT INTO ho_gia_dinh (ma_can_ho, loai_can_ho, tang, dien_tich,
                         so_dien_thoai, ngay_chuyen_den, trang_thai, ngay_tao)
SELECT ma, loai, tang, dien_tich, sdt, ngay_den,
       IF(chuyen_di IS NULL, 'DANG_O', 'DA_CHUYEN_DI'),
       TIMESTAMP(ngay_den, '09:00:00')
FROM demo_ho WHERE @chay ORDER BY ngay_den, ma;

-- ---------- Nhân khẩu (US-002, US-003) ----------
DROP TEMPORARY TABLE IF EXISTS demo_nk;
CREATE TEMPORARY TABLE demo_nk (
    ma VARCHAR(20), ho_ten VARCHAR(100), ngay_sinh DATE, gioi_tinh CHAR(3),
    so_cccd CHAR(12) NULL, quan_he VARCHAR(10), trang_thai VARCHAR(15),
    loai_cu_tru VARCHAR(12), noi_thuong_tru VARCHAR(200) NULL, ngay_den DATE,
    PRIMARY KEY (ma, ho_ten));
INSERT INTO demo_nk VALUES
  ('K02', 'Trần Minh Nhung', '1990-08-26', 'NU', '001190459749', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2020-09-25'),
  ('K02', 'Đỗ Ngọc Lộc', '1994-03-14', 'NAM', '030094502421', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2020-09-25'),
  ('K03', 'Mai Thị Hoa', '1964-01-15', 'NU', '038164573141', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2024-02-07'),
  ('K03', 'Mai Thanh Ngọc', '1988-01-25', 'NU', '001188651243', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2024-02-07'),
  ('K04', 'Hoàng Trọng Cường', '1986-08-19', 'NAM', '001086627191', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2021-01-11'),
  ('K04', 'Hoàng Gia Sơn', '2017-03-26', 'NAM', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2021-01-11'),
  ('B0907', 'Trịnh Đức Dũng', '1961-06-21', 'NAM', '034061979940', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2022-09-14'),
  ('B0907', 'Trịnh Ngọc Khánh', '1983-08-11', 'NAM', '040083318422', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2022-09-14'),
  ('C1508', 'Tạ Thanh Hiền', '1984-12-03', 'NU', '037184418546', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2025-08-19'),
  ('C1508', 'Vũ Văn Bảo', '1990-09-27', 'NAM', '001090473768', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2025-08-19'),
  ('A2203', 'Vũ Diệu Uyên', '1963-03-13', 'NU', '033163446016', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2021-08-23'),
  ('A2203', 'Trịnh Hữu Sơn', '1960-06-06', 'NAM', '033060486450', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2021-08-23'),
  ('B1104', 'Vũ Trọng Thắng', '1970-09-25', 'NAM', '034070419252', 'CHU_HO', 'DA_CHUYEN_DI', 'THUONG_TRU', NULL, '2021-04-10'),
  ('B1104', 'Vũ Kim Hoa', '1970-01-12', 'NU', '001170784156', 'VO_CHONG', 'DA_CHUYEN_DI', 'THUONG_TRU', NULL, '2021-04-10'),
  ('B1104', 'Vũ Hồng Uyên', '1996-04-27', 'NU', '037196816798', 'CON', 'DA_CHUYEN_DI', 'THUONG_TRU', NULL, '2021-04-10'),
  ('C0806', 'Đinh Ngọc Mai', '1961-11-02', 'NU', '034161757447', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2021-10-10'),
  ('C0806', 'Đinh Minh Ngân', '1986-06-06', 'NU', '001186305173', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2021-10-10'),
  ('A1702', 'Nguyễn Thị Mai', '1981-04-01', 'NU', '033181128996', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2022-02-01'),
  ('A1702', 'Hồ Hữu Long', '1989-07-09', 'NAM', '036089021275', 'KHAC', 'DANG_O', 'TAM_TRU', 'Xã Nghĩa Lâm, huyện Nghĩa Hưng, Nam Định', CURDATE() - INTERVAL 150 DAY),
  ('B2005', 'Tạ Ngọc Đạt', '1968-11-04', 'NAM', '040068392321', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2023-06-25'),
  ('B2005', 'Tạ Mạnh Bình', '1998-12-14', 'NAM', '001098771109', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2023-06-25'),
  ('B1702', 'Ngô Phương Nhung', '1962-10-01', 'NU', '037162359120', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('B1702', 'Cao Văn Toàn', '1961-02-06', 'NAM', '038061128644', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('B1702', 'Cao Trọng Bình', '1987-11-10', 'NAM', '038087679365', 'CON', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('C1502', 'Vũ Bảo Lan', '1963-11-23', 'NU', '037163716966', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('C1502', 'Trịnh Hữu Tùng', '1967-03-08', 'NAM', '001067017401', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('C1502', 'Trịnh Phương Thảo', '1997-11-19', 'NU', '034197145392', 'CON', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('C1502', 'Trịnh Bảo Hương', '1999-09-27', 'NU', '040199908199', 'CON', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('C2205', 'Trần Thanh Hiền', '1985-09-28', 'NU', '001185972270', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('C2205', 'Phan Tiến Cường', '1987-09-27', 'NAM', '033087360382', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('C2205', 'Phan Ngọc Bảo', '2016-08-18', 'NAM', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('C2205', 'Phan Diệu Ngân', '2019-05-07', 'NU', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('B1903', 'Đinh Thị My', '1988-03-03', 'NU', '033188390902', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('B1903', 'Hoàng Anh Toàn', '1988-02-01', 'NAM', '037088227669', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 0 DAY),
  ('C2705', 'Hồ Thu Hoa', '1965-03-24', 'NU', '035165349815', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2018-07-13'),
  ('C2705', 'Mai Kim Giang', '1997-05-09', 'NU', '034197467609', 'KHAC', 'DANG_O', 'TAM_TRU', 'Xã Thụy Liên, huyện Thái Thụy, Thái Bình', CURDATE() - INTERVAL 40 DAY),
  ('A2706', 'Cao Minh Hạnh', '1973-03-13', 'NU', '031173783289', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2022-05-02'),
  ('A2706', 'Nguyễn Mạnh Khôi', '1979-11-16', 'NAM', '035079372934', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2022-05-02'),
  ('A2706', 'Nguyễn Trọng Hùng', '2018-03-11', 'NAM', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2022-05-02'),
  ('B1907', 'Đặng Trọng Long', '1974-10-20', 'NAM', '031074132670', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2020-03-01'),
  ('B1907', 'Ngô Mạnh Khánh', '2004-03-04', 'NAM', '030204642159', 'KHAC', 'DANG_O', 'TAM_TRU', 'Phường Phạm Ngũ Lão, TP Hải Dương, Hải Dương', CURDATE() - INTERVAL 175 DAY),
  ('A1708', 'Phan Kim Vy', '1962-02-18', 'NU', '037162259026', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2023-08-04'),
  ('A1708', 'Phan Thùy Hoa', '1998-02-26', 'NU', '001198816889', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2023-08-04'),
  ('A1808', 'Lương Quang Khánh', '1975-03-07', 'NAM', '036075230865', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2022-05-25'),
  ('A1808', 'Đặng Anh Phong', '2002-12-25', 'NAM', '037202422965', 'KHAC', 'DANG_O', 'TAM_TRU', 'Xã Gia Sinh, huyện Gia Viễn, Ninh Bình', CURDATE() - INTERVAL 160 DAY),
  ('B2105', 'Đinh Minh Nhung', '1963-02-11', 'NU', '033163882041', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2023-07-27'),
  ('B2105', 'Đỗ Minh Vinh', '1966-11-09', 'NAM', '031066347887', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2023-07-27'),
  ('B2105', 'Đỗ Thu Thảo', '1992-08-21', 'NU', '033192602289', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2023-07-27'),
  ('B2105', 'Đỗ Trọng An', '1995-07-11', 'NAM', '034095706862', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2023-07-27'),
  ('B2308', 'Trần Quang Toàn', '1976-03-01', 'NAM', '033076586071', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2025-11-28'),
  ('B2308', 'Trần Gia Khôi', '2002-08-28', 'NAM', '035202873381', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2025-11-28'),
  ('C2101', 'Mai Trọng Hiếu', '1968-10-21', 'NAM', '034068082824', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2023-01-02'),
  ('C2101', 'Đặng Bảo Oanh', '1972-01-04', 'NU', '001172346012', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2023-01-02'),
  ('C2101', 'Mai Thanh Quỳnh', '2001-12-10', 'NU', '040301179073', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2023-01-02'),
  ('C1203', 'Trần Minh Vinh', '1981-12-17', 'NAM', '031081989315', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2024-12-17'),
  ('C1203', 'Dương Thùy Hạnh', '1984-06-20', 'NU', '036184267753', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2024-12-17'),
  ('C1203', 'Trần Minh Hương', '2021-11-16', 'NU', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2024-12-17'),
  ('C1203', 'Bùi Mạnh Khánh', '2003-08-26', 'NAM', '040203071572', 'KHAC', 'DANG_O', 'TAM_TRU', 'Xã Diễn Kim, huyện Diễn Châu, Nghệ An', CURDATE() - INTERVAL 90 DAY),
  ('A2702', 'Trịnh Hữu Thắng', '1987-10-02', 'NAM', '035087904851', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2018-08-26'),
  ('A2702', 'Hồ Diệu Chi', '1984-05-16', 'NU', '038184095125', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2018-08-26'),
  ('A2702', 'Trịnh Bảo Quỳnh', '2022-06-16', 'NU', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2018-08-26'),
  ('C2407', 'Hồ Phương My', '1988-02-20', 'NU', '031188030278', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2023-06-06'),
  ('C2407', 'Đinh Trọng Duy', '1987-09-05', 'NAM', '037087221541', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2023-06-06'),
  ('B0908', 'Cao Xuân Toàn', '1980-09-07', 'NAM', '001080636246', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2025-03-18'),
  ('B0908', 'Hoàng Khánh Hà', '1984-03-26', 'NU', '040184600773', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2025-03-18'),
  ('B0908', 'Cao Thị Thảo', '2007-03-05', 'NU', '033307986681', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2025-03-18'),
  ('A0808', 'Lương Trọng Hùng', '1994-05-08', 'NAM', '037094381846', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2023-03-15'),
  ('A0808', 'Lê Kim Hiền', '1998-12-14', 'NU', '001198943856', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2023-03-15'),
  ('A0808', 'Lương Trọng Dũng', '2020-08-01', 'NAM', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2023-03-15'),
  ('A0808', 'Lương Xuân Duy', '2022-12-23', 'NAM', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2023-03-15'),
  ('C2005', 'Đặng Mạnh Quân', '1982-03-08', 'NAM', '031082693761', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2023-01-06'),
  ('C2005', 'Đặng Thùy Linh', '1985-09-28', 'NU', '035185756138', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2023-01-06'),
  ('C2005', 'Đặng Thành An', '2020-04-10', 'NAM', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2023-01-06'),
  ('C1705', 'Phạm Tiến Tuấn', '1967-06-23', 'NAM', '040067652408', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2025-09-20'),
  ('C1705', 'Phạm Ngọc Trang', '1965-11-14', 'NU', '031165943173', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2025-09-20'),
  ('C1705', 'Phạm Minh Bảo', '1995-05-10', 'NAM', '040095827799', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2025-09-20'),
  ('C1705', 'Phạm Văn Lâm', '1999-05-27', 'NAM', '037099482558', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2025-09-20'),
  ('C1705', 'Phạm Minh Hải', '2002-04-12', 'NAM', '001202586832', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2025-09-20'),
  ('C1705', 'Lý Thị Huyền', '1937-11-18', 'NU', '034137791576', 'KHAC', 'DANG_O', 'THUONG_TRU', NULL, '2025-09-20'),
  ('C2508', 'Lý Văn Tuấn', '1960-09-15', 'NAM', '038060114082', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2022-11-03'),
  ('B1804', 'Lê Minh Vinh', '1980-05-01', 'NAM', '001080785310', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2025-10-16'),
  ('B1804', 'Tạ Bảo Mai', '1977-12-28', 'NU', '034177752077', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2025-10-16'),
  ('B1804', 'Lê Ngọc Cường', '2006-08-23', 'NAM', '034206751791', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2025-10-16'),
  ('B1804', 'Lê Trọng Duy', '2008-01-16', 'NAM', '034208439214', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2025-10-16'),
  ('B1301', 'Đặng Quang Toàn', '1986-01-17', 'NAM', '036086822715', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2020-08-07'),
  ('B1301', 'Ngô Thanh Uyên', '1984-09-11', 'NU', '033184804243', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2020-08-07'),
  ('B1301', 'Đặng Hồng Lan', '2014-06-20', 'NU', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2020-08-07'),
  ('B1301', 'Đặng Trọng Hải', '2018-10-12', 'NAM', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2020-08-07'),
  ('B1301', 'Mai Phương Tâm', '1961-02-26', 'NU', '001161844310', 'KHAC', 'DANG_O', 'THUONG_TRU', NULL, '2020-08-07'),
  ('B1301', 'Hoàng Phương My', '1999-11-01', 'NU', '038199465650', 'KHAC', 'DANG_O', 'TAM_TRU', 'Xã Hoằng Đạt, huyện Hoằng Hóa, Thanh Hóa', CURDATE() - INTERVAL 120 DAY),
  ('A0908', 'Lê Hữu Sơn', '1978-01-07', 'NAM', '030078883065', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2024-07-01'),
  ('A0908', 'Cao Thu Phương', '1984-09-22', 'NU', '001184372945', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2024-07-01'),
  ('A0908', 'Lê Khánh Phương', '2020-09-21', 'NU', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2024-07-01'),
  ('A0908', 'Lê Thanh My', '2022-11-16', 'NU', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2024-07-01'),
  ('A0908', 'Lê Gia Tuấn', '1950-05-07', 'NAM', '001050300837', 'KHAC', 'DANG_O', 'THUONG_TRU', NULL, '2024-07-01'),
  ('C2006', 'Cao Thanh Vy', '1992-01-18', 'NU', '034192239385', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2018-08-09'),
  ('C2006', 'Trịnh Ngọc Tuấn', '1996-04-02', 'NAM', '001096433592', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2018-08-09'),
  ('C2006', 'Trịnh Kim Hiền', '2021-10-06', 'NU', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2018-08-09'),
  ('B1406', 'Đặng Quốc Hưng', '1968-06-06', 'NAM', '036068576838', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2016-08-20'),
  ('B1406', 'Lương Thị Minh Nguyệt', '1971-02-05', 'NU', '001171853966', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2016-08-20'),
  ('B1406', 'Đặng Gia Bảo', '2016-07-03', 'NAM', NULL, 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2016-08-20'),
  ('B1406', 'Đặng Văn Khải', '1941-12-05', 'NAM', '040041837705', 'KHAC', 'DA_CHUYEN_DI', 'THUONG_TRU', NULL, '2016-08-20'),
  ('C1003', 'Ngô Thị Thanh Mai', '1979-10-11', 'NU', '037179988385', 'CHU_HO', 'DANG_O', 'THUONG_TRU', NULL, '2020-11-02'),
  ('C1003', 'Bùi Đức Long', '1976-06-03', 'NAM', '030076714765', 'VO_CHONG', 'DANG_O', 'THUONG_TRU', NULL, '2020-11-02'),
  ('C1003', 'Bùi Ngọc Diệp', '2008-10-01', 'NU', '040308264544', 'CON', 'DANG_O', 'THUONG_TRU', NULL, '2020-11-02');

INSERT INTO nhan_khau (ho_id, ho_ten, ngay_sinh, gioi_tinh, so_cccd, quan_he,
                       trang_thai, loai_cu_tru, noi_thuong_tru, ngay_tao)
SELECT h.id, d.ho_ten, d.ngay_sinh, d.gioi_tinh, d.so_cccd, d.quan_he,
       d.trang_thai, d.loai_cu_tru, d.noi_thuong_tru,
       TIMESTAMP(d.ngay_den, '09:10:00')
FROM demo_nk d JOIN ho_gia_dinh h ON h.ma_can_ho = d.ma
WHERE @chay ORDER BY h.id, d.quan_he = 'CHU_HO' DESC, d.ngay_sinh;

-- Đợt tạm trú (6, hai đợt đã quá hạn) và tạm vắng (4, còn hạn).
DROP TEMPORARY TABLE IF EXISTS demo_dot;
CREATE TEMPORARY TABLE demo_dot (
    ma VARCHAR(20), ho_ten VARCHAR(100), loai VARCHAR(10), tu_ngay DATE,
    den_ngay DATE, ly_do VARCHAR(200));
INSERT INTO demo_dot VALUES
  ('A1702', 'Hồ Hữu Long', 'TAM_TRU', CURDATE() - INTERVAL 150 DAY, CURDATE() + INTERVAL 200 DAY, 'Thuê nhà làm việc'),
  ('B1301', 'Hoàng Phương My', 'TAM_TRU', CURDATE() - INTERVAL 120 DAY, CURDATE() + INTERVAL 60 DAY, 'Đi học'),
  ('C1203', 'Bùi Mạnh Khánh', 'TAM_TRU', CURDATE() - INTERVAL 90 DAY, CURDATE() + INTERVAL 270 DAY, 'Làm việc tại khu công nghiệp'),
  ('C2705', 'Mai Kim Giang', 'TAM_TRU', CURDATE() - INTERVAL 40 DAY, CURDATE() + INTERVAL 140 DAY, 'Ở cùng người thân'),
  ('B1907', 'Ngô Mạnh Khánh', 'TAM_TRU', CURDATE() - INTERVAL 175 DAY, CURDATE() - INTERVAL 15 DAY, 'Thuê nhà làm việc'),
  ('A1808', 'Đặng Anh Phong', 'TAM_TRU', CURDATE() - INTERVAL 160 DAY, CURDATE() - INTERVAL 40 DAY, 'Đi học'),
  ('C2101', 'Mai Trọng Hiếu', 'TAM_VANG', CURDATE() - INTERVAL 25 DAY, CURDATE() + INTERVAL 35 DAY, 'Công tác Đà Nẵng'),
  ('A0908', 'Lê Hữu Sơn', 'TAM_VANG', CURDATE() - INTERVAL 10 DAY, CURDATE() + INTERVAL 50 DAY, 'Về quê chăm mẹ'),
  ('C1508', 'Tạ Thanh Hiền', 'TAM_VANG', CURDATE() - INTERVAL 40 DAY, CURDATE() + INTERVAL 20 DAY, 'Điều trị tại bệnh viện tỉnh'),
  ('C2407', 'Hồ Phương My', 'TAM_VANG', CURDATE() - INTERVAL 3 DAY, CURDATE() + INTERVAL 90 DAY, 'Đi làm việc ở Bình Dương');

INSERT INTO tam_tru_tam_vang (nhan_khau_id, loai, tu_ngay, den_ngay, ly_do,
                              ngay_tao)
SELECT n.id, d.loai, d.tu_ngay, d.den_ngay, d.ly_do,
       TIMESTAMP(d.tu_ngay, '08:45:00')
FROM demo_dot d JOIN ho_gia_dinh h ON h.ma_can_ho = d.ma
JOIN nhan_khau n ON n.ho_id = h.id AND n.ho_ten = d.ho_ten
WHERE @chay;

-- ---------- Biến động nhân khẩu (US-027) ----------
-- Dòng mẫu của seed.sql có thời điểm ghi là lúc chạy seed, nên luôn đứng
-- đầu "Biến động nhân khẩu gần đây" (xếp theo thời điểm ghi) dù ngày sự kiện
-- đã cũ. Đặt thời điểm ghi của chúng về ngày sự kiện cho khớp dữ liệu demo.
UPDATE bien_dong_nhan_khau SET thoi_diem = TIMESTAMP(ngay, '09:00:00')
WHERE @chay AND thoi_diem > TIMESTAMP(ngay + INTERVAL 1 DAY, '00:00:00');

-- Chuyển đến: ngày hộ chuyển đến (người tạm trú: ngày bắt đầu tạm trú).
INSERT INTO bien_dong_nhan_khau (ho_id, nhan_khau_id, loai, ngay, ghi_chu,
                                 nguoi_ghi, thoi_diem)
SELECT h.id, n.id, 'CHUYEN_DEN', d.ngay_den, NULL, @admin,
       TIMESTAMP(d.ngay_den, '09:10:00')
FROM demo_nk d JOIN ho_gia_dinh h ON h.ma_can_ho = d.ma
JOIN nhan_khau n ON n.ho_id = h.id AND n.ho_ten = d.ho_ten
WHERE @chay;

INSERT INTO bien_dong_nhan_khau (ho_id, nhan_khau_id, loai, ngay, ghi_chu,
                                 nguoi_ghi, thoi_diem)
SELECT h.id, n.id, d.loai, d.tu_ngay,
       CONCAT('Đến ', DATE_FORMAT(d.den_ngay, '%d/%m/%Y')), @admin,
       TIMESTAMP(d.tu_ngay, '08:45:00')
FROM demo_dot d JOIN ho_gia_dinh h ON h.ma_can_ho = d.ma
JOIN nhan_khau n ON n.ho_id = h.id AND n.ho_ten = d.ho_ten
WHERE @chay;

-- Đổi chủ hộ B1406 (45 ngày trước, chủ hộ cũ rời hộ) và C1003 (100 ngày
-- trước, chủ hộ cũ ở lại); cả hộ B1104 chuyển đi ngày 28 của 3 tháng trước.
DROP TEMPORARY TABLE IF EXISTS demo_bd;
CREATE TEMPORARY TABLE demo_bd (
    ma VARCHAR(20), ho_ten VARCHAR(100), loai VARCHAR(12), ngay DATE,
    ghi_chu VARCHAR(200));
INSERT INTO demo_bd VALUES
  ('B1406', 'Đặng Quốc Hưng', 'DOI_CHU_HO', CURDATE() - INTERVAL 45 DAY,
   'Làm chủ hộ thay Đặng Văn Khải'),
  ('B1406', 'Đặng Văn Khải', 'CHUYEN_DI', CURDATE() - INTERVAL 45 DAY, 'Chủ hộ cũ rời hộ'),
  ('C1003', 'Ngô Thị Thanh Mai', 'DOI_CHU_HO', CURDATE() - INTERVAL 100 DAY,
   'Làm chủ hộ thay Bùi Đức Long'),
  ('B1104', 'Vũ Trọng Thắng', 'CHUYEN_DI', DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 27 DAY, 'Cả hộ chuyển đi'),
  ('B1104', 'Vũ Kim Hoa', 'CHUYEN_DI', DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 27 DAY, 'Cả hộ chuyển đi'),
  ('B1104', 'Vũ Hồng Uyên', 'CHUYEN_DI', DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 27 DAY, 'Cả hộ chuyển đi');

INSERT INTO bien_dong_nhan_khau (ho_id, nhan_khau_id, loai, ngay, ghi_chu,
                                 nguoi_ghi, thoi_diem)
SELECT h.id, n.id, d.loai, d.ngay, d.ghi_chu, @admin,
       TIMESTAMP(d.ngay, '10:00:00')
FROM demo_bd d JOIN ho_gia_dinh h ON h.ma_can_ho = d.ma
JOIN nhan_khau n ON n.ho_id = h.id AND n.ho_ten = d.ho_ten
WHERE @chay;

-- ---------- Khoản thu ----------
-- Quỹ khuyến học: quỹ tự nguyện đã đóng, thu từ đầu 4 tháng trước đến hết
-- 2 tháng trước (US-006); để Thống kê có quỹ đã thu xong.
INSERT INTO khoan_phi (ten, loai, cach_tinh, don_gia, tu_ngay, den_ngay,
                       trang_thai, ngay_tao)
SELECT 'Quỹ khuyến học', 'TU_NGUYEN', 'KHONG_CO_DINH', NULL, DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01'),
       LAST_DAY(CURDATE() - INTERVAL 2 MONTH), 'DANG_THU',
       TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') - INTERVAL 3 DAY, '14:00:00')
FROM DUAL WHERE @chay;

-- ---------- Chốt công nợ 6 tháng (US-009) ----------
INSERT INTO dot_thu (thang, nguoi_chot, ngay_chot)
SELECT DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01'), @admin, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 1 DAY, '08:30:00') FROM DUAL WHERE @chay
UNION ALL
SELECT DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01'), @admin, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 1 DAY, '08:30:00') FROM DUAL WHERE @chay
UNION ALL
SELECT DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01'), @admin, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 1 DAY, '08:30:00') FROM DUAL WHERE @chay
UNION ALL
SELECT DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01'), @admin, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 1 DAY, '08:30:00') FROM DUAL WHERE @chay
UNION ALL
SELECT DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01'), @admin, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 1 DAY, '08:30:00') FROM DUAL WHERE @chay
UNION ALL
SELECT DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01'), @admin, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL 1 DAY, '08:30:00') FROM DUAL WHERE @chay AND @thang_nay;

-- Khoản phải nộp: như CongNoService.xemTruoc, mọi hộ đang ở lúc chốt (đã
-- chuyển đến trước cuối tháng) × khoản bắt buộc áp dụng trong tháng,
-- số tiền = diện tích × đơn giá làm tròn đến đồng.
INSERT INTO phai_nop (dot_thu_id, ho_id, khoan_phi_id, dien_tich, don_gia,
                      so_tien_goc, so_tien)
SELECT d.id, h.id, k.id, h.dien_tich, k.don_gia,
       ROUND(h.dien_tich * k.don_gia, 0), ROUND(h.dien_tich * k.don_gia, 0)
FROM dot_thu d
JOIN ho_gia_dinh h ON h.ngay_chuyen_den <= LAST_DAY(d.thang)
LEFT JOIN demo_ho dh ON dh.ma = h.ma_can_ho
JOIN khoan_phi k ON k.loai = 'BAT_BUOC' AND k.trang_thai = 'DANG_THU'
     AND k.tu_ngay <= LAST_DAY(d.thang)
     AND (k.den_ngay IS NULL OR k.den_ngay >= d.thang)
WHERE @chay
  AND (h.trang_thai = 'DANG_O' OR d.ngay_chot < dh.chuyen_di)
ORDER BY d.thang, h.id, k.id;

-- ---------- Phiếu thu 6 tháng (US-010 – US-014, US-007) ----------
DROP TEMPORARY TABLE IF EXISTS demo_pt;
CREATE TEMPORARY TABLE demo_pt (
    khoa INT PRIMARY KEY, ma VARCHAR(20), k_thu INT, thoi_diem DATETIME,
    hinh_thuc VARCHAR(15), nguoi INT, so_lan_in INT,
    ly_do_huy VARCHAR(200) NULL, phut_huy INT NULL);
DROP TEMPORARY TABLE IF EXISTS demo_pt_thang;
CREATE TEMPORARY TABLE demo_pt_thang (khoa INT, k INT);
DROP TEMPORARY TABLE IF EXISTS demo_pt_quy;
CREATE TEMPORARY TABLE demo_pt_quy (
    khoa INT, ten VARCHAR(100), so_tien DECIMAL(14, 2));
INSERT INTO demo_pt VALUES
  (36, 'B0907', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 2 DAY, '08:25:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (96, 'B2105', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 2 DAY, '10:05:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (154, 'B1301', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 2 DAY, '10:50:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (115, 'C1203', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 2 DAY, '11:40:00'), 'CHUYEN_KHOAN', @admin, 0, NULL, NULL),
  (64, 'C2205', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 2 DAY, '13:35:00'), 'CHUYEN_KHOAN', @admin, 0, NULL, NULL),
  (65, 'C2205', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 2 DAY, '13:45:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (152, 'B1804', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 2 DAY, '15:45:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (72, 'C2705', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 2 DAY, '16:45:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (89, 'A1708', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '08:15:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (28, 'K03', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '08:40:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (137, 'C2005', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '08:50:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (77, 'A2706', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '09:25:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (132, 'A0808', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '10:40:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (163, 'A0908', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '10:40:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (1, 'K01', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '11:50:00'), 'TIEN_MAT', @thuquy, 2, NULL, NULL),
  (8, 'A0601', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '11:50:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (44, 'C0806', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '13:10:00'), 'TIEN_MAT', @admin, 0, NULL, NULL),
  (133, 'A0808', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '13:55:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (113, 'C1203', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '14:55:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (27, 'K03', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 3 DAY, '16:10:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (43, 'C0806', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 4 DAY, '09:05:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (59, 'B1702', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 4 DAY, '10:05:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (24, 'K02', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 4 DAY, '10:25:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (129, 'B0908', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 4 DAY, '10:50:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (48, 'A1702', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 4 DAY, '13:45:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (147, 'C2508', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 4 DAY, '14:10:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (102, 'B2308', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 4 DAY, '15:45:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (63, 'C1502', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 4 DAY, '16:55:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (75, 'A2706', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '08:15:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (93, 'A1808', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '08:15:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (92, 'A1808', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '08:40:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (50, 'A1702', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '09:40:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (114, 'C1203', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '10:15:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (9, 'A0601', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '10:25:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (109, 'C2101', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '10:40:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (125, 'C2407', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '10:40:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (45, 'C0806', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '11:25:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (118, 'A2702', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '14:35:00'), 'TIEN_MAT', @admin, 0, NULL, NULL),
  (165, 'C2006', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '14:55:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (14, 'A1205', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '16:20:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (120, 'A2702', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 5 DAY, '16:55:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (116, 'C1203', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 6 DAY, '08:40:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (169, 'B1406', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 6 DAY, '09:25:00'), 'TIEN_MAT', @admin, 2, NULL, NULL),
  (38, 'C1508', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 6 DAY, '09:50:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (79, 'A2706', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 6 DAY, '11:15:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (166, 'C2006', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 6 DAY, '11:25:00'), 'TIEN_MAT', @admin, 2, NULL, NULL),
  (179, 'C1003', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 6 DAY, '11:25:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (87, 'A1708', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 6 DAY, '16:10:00'), 'CHUYEN_KHOAN', @thuquy, 0, NULL, NULL),
  (52, 'B2005', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 7 DAY, '09:15:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (7, 'A0601', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 7 DAY, '09:25:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (172, 'B1406', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 7 DAY, '10:15:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (86, 'A1708', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 7 DAY, '10:40:00'), 'CHUYEN_KHOAN', @admin, 2, NULL, NULL),
  (32, 'K04', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 7 DAY, '13:10:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (73, 'C2705', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 7 DAY, '14:20:00'), 'CHUYEN_KHOAN', @thuquy, 0, NULL, NULL),
  (61, 'C1502', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 7 DAY, '15:45:00'), 'TIEN_MAT', @thuquy, 2, NULL, NULL),
  (175, 'C1003', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 7 DAY, '16:10:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (123, 'C2407', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '08:15:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (162, 'A0908', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '08:50:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (55, 'B2005', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '10:05:00'), 'TIEN_MAT', @admin, 0, 'Chọn nhầm hình thức thanh toán', 10),
  (145, 'C1705', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '10:05:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (180, 'B2005', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '10:35:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (47, 'A1702', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '10:50:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (101, 'B2308', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '11:40:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (121, 'A2702', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '15:35:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (99, 'B2105', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '16:10:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (31, 'K04', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '16:35:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (148, 'C2508', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 8 DAY, '16:55:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (69, 'C2705', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 9 DAY, '13:35:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (141, 'C1705', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 10 DAY, '10:50:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (103, 'B2308', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 10 DAY, '11:40:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (171, 'B1406', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 10 DAY, '13:35:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (98, 'B2105', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 10 DAY, '14:35:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (105, 'B2308', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 10 DAY, '16:10:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (37, 'B0907', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 11 DAY, '08:50:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (46, 'C0806', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 11 DAY, '09:20:00'), 'TIEN_MAT', @thuquy, 0, 'Thu nhầm hộ', 15),
  (70, 'C2705', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 11 DAY, '09:40:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (16, 'PH01', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 11 DAY, '11:05:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (139, 'C2005', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 11 DAY, '11:15:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (159, 'A0908', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 11 DAY, '14:45:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (26, 'K03', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 11 DAY, '15:10:00'), 'TIEN_MAT', @admin, 0, NULL, NULL),
  (85, 'A1708', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 12 DAY, '08:05:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (128, 'B0908', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 12 DAY, '08:40:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (127, 'B0908', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 12 DAY, '09:40:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (167, 'C2006', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 12 DAY, '13:35:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (60, 'B1702', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 12 DAY, '15:20:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (135, 'A0808', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 12 DAY, '15:35:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (71, 'C2705', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 13 DAY, '09:05:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (97, 'B2105', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 13 DAY, '13:10:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (5, 'K01', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 13 DAY, '14:55:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (54, 'B2005', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 13 DAY, '16:35:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (4, 'K01', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 14 DAY, '08:40:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (84, 'B1907', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 14 DAY, '08:50:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (112, 'C1203', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 14 DAY, '11:25:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (91, 'A1808', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 14 DAY, '15:10:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (134, 'A0808', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '08:05:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (94, 'A1808', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '10:05:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (3, 'K01', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '10:50:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (144, 'C1705', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '11:15:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (53, 'B2005', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '11:50:00'), 'TIEN_MAT', @admin, 0, NULL, NULL),
  (13, 'A1205', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '13:45:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (17, 'PH01', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '13:55:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (130, 'B0908', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '15:35:00'), 'CHUYEN_KHOAN', @admin, 0, NULL, NULL),
  (150, 'B1804', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '15:45:00'), 'TIEN_MAT', @admin, 2, NULL, NULL),
  (23, 'K02', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 15 DAY, '16:55:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (58, 'B1702', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 16 DAY, '08:40:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (12, 'A1205', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 16 DAY, '08:50:00'), 'TIEN_MAT', @admin, 0, NULL, NULL),
  (56, 'B2005', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 16 DAY, '09:25:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (20, 'PH01', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 16 DAY, '10:25:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (164, 'C2006', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 16 DAY, '10:25:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (119, 'A2702', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 16 DAY, '11:25:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (161, 'A0908', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 16 DAY, '11:25:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (156, 'B1301', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 16 DAY, '11:40:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (108, 'C2101', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 16 DAY, '14:45:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (136, 'C2005', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 17 DAY, '09:50:00'), 'CHUYEN_KHOAN', @thuquy, 0, NULL, NULL),
  (49, 'A1702', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 17 DAY, '11:40:00'), 'TIEN_MAT', @thuquy, 2, NULL, NULL),
  (76, 'A2706', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 17 DAY, '16:45:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (160, 'A0908', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 18 DAY, '08:40:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (78, 'A2706', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 18 DAY, '10:50:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (83, 'B1907', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 18 DAY, '11:05:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (19, 'PH01', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 18 DAY, '11:25:00'), 'TIEN_MAT', @thuquy, 2, NULL, NULL),
  (142, 'C1705', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 18 DAY, '11:25:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (138, 'C2005', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 18 DAY, '13:35:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (155, 'B1301', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 19 DAY, '09:25:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (30, 'K04', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 19 DAY, '15:45:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (11, 'A1205', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 19 DAY, '15:55:00'), 'TIEN_MAT', @admin, 0, NULL, NULL),
  (170, 'B1406', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 19 DAY, '16:45:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (88, 'A1708', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 20 DAY, '08:15:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (124, 'C2407', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 20 DAY, '09:40:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (18, 'PH01', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 20 DAY, '09:50:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (173, 'B1406', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 20 DAY, '09:50:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (107, 'C2101', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 20 DAY, '10:40:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (151, 'B1804', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 20 DAY, '15:35:00'), 'CHUYEN_KHOAN', @thuquy, 2, NULL, NULL),
  (157, 'B1301', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 20 DAY, '16:10:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (25, 'K02', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 21 DAY, '10:50:00'), 'CHUYEN_KHOAN', @admin, 0, NULL, NULL),
  (40, 'A2203', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 21 DAY, '10:50:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (81, 'B1907', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 21 DAY, '14:10:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (39, 'A2203', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 22 DAY, '09:15:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (158, 'B1301', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 22 DAY, '11:25:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (22, 'K02', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 22 DAY, '11:50:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (33, 'K04', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 22 DAY, '13:35:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (146, 'C2508', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 22 DAY, '13:35:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (104, 'B2308', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 22 DAY, '15:35:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (2, 'K01', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 23 DAY, '10:05:00'), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (126, 'C2407', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 23 DAY, '10:15:00'), 'CHUYEN_KHOAN', @admin, 0, NULL, NULL),
  (10, 'A0601', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 23 DAY, '11:05:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (42, 'C0806', 5, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01') + INTERVAL 24 DAY, '10:05:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (149, 'C2508', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 24 DAY, '11:05:00'), 'TIEN_MAT', @thuquy, 2, NULL, NULL),
  (153, 'B1804', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 24 DAY, '11:40:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (177, 'C1003', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 24 DAY, '11:50:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (82, 'B1907', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 24 DAY, '14:20:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (41, 'A2203', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 25 DAY, '08:25:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (34, 'K04', 1, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 1 MONTH, '%Y-%m-01') + INTERVAL 25 DAY, '09:15:00'), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (62, 'C1502', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 25 DAY, '09:25:00'), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (178, 'C1003', 2, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 2 MONTH, '%Y-%m-01') + INTERVAL 25 DAY, '13:20:00'), 'TIEN_MAT', @admin, 0, NULL, NULL),
  (143, 'C1705', 3, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 25 DAY, '14:35:00'), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (176, 'C1003', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 25 DAY, '14:45:00'), 'TIEN_MAT', @admin, 0, NULL, NULL),
  (57, 'B1702', 4, TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 4 MONTH, '%Y-%m-01') + INTERVAL 25 DAY, '15:10:00'), 'CHUYEN_KHOAN', @admin, 1, NULL, NULL),
  (6, 'K01', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(3, DAY(CURDATE())) - 1) DAY, '09:15:00'), NOW() - INTERVAL 158 MINUTE), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (29, 'K03', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(3, DAY(CURDATE())) - 1) DAY, '13:55:00'), NOW() - INTERVAL 159 MINUTE), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (35, 'K04', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(3, DAY(CURDATE())) - 1) DAY, '14:45:00'), NOW() - INTERVAL 160 MINUTE), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (95, 'A1808', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(3, DAY(CURDATE())) - 1) DAY, '15:35:00'), NOW() - INTERVAL 161 MINUTE), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (110, 'C2101', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(4, DAY(CURDATE())) - 1) DAY, '08:25:00'), NOW() - INTERVAL 162 MINUTE), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (67, 'B1903', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(6, DAY(CURDATE())) - 1) DAY, '08:40:00'), NOW() - INTERVAL 163 MINUTE), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (111, 'C2101', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(7, DAY(CURDATE())) - 1) DAY, '16:10:00'), NOW() - INTERVAL 164 MINUTE), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (74, 'C2705', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(9, DAY(CURDATE())) - 1) DAY, '08:15:00'), NOW() - INTERVAL 165 MINUTE), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (168, 'C2006', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(9, DAY(CURDATE())) - 1) DAY, '11:25:00'), NOW() - INTERVAL 166 MINUTE), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (21, 'PH01', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(9, DAY(CURDATE())) - 1) DAY, '15:35:00'), NOW() - INTERVAL 167 MINUTE), 'CHUYEN_KHOAN', @thuquy, 0, NULL, NULL),
  (68, 'B1903', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(12, DAY(CURDATE())) - 1) DAY, '10:05:00'), NOW() - INTERVAL 168 MINUTE), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (174, 'B1406', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(13, DAY(CURDATE())) - 1) DAY, '15:35:00'), NOW() - INTERVAL 169 MINUTE), 'TIEN_MAT', @thuquy, 2, NULL, NULL),
  (122, 'A2702', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(15, DAY(CURDATE())) - 1) DAY, '10:15:00'), NOW() - INTERVAL 170 MINUTE), 'TIEN_MAT', @thuquy, 2, NULL, NULL),
  (80, 'A2706', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(16, DAY(CURDATE())) - 1) DAY, '09:50:00'), NOW() - INTERVAL 171 MINUTE), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (51, 'A1702', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(17, DAY(CURDATE())) - 1) DAY, '13:10:00'), NOW() - INTERVAL 172 MINUTE), 'TIEN_MAT', @thuquy, 2, NULL, NULL),
  (140, 'C2005', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(17, DAY(CURDATE())) - 1) DAY, '13:45:00'), NOW() - INTERVAL 173 MINUTE), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (66, 'C2205', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(18, DAY(CURDATE())) - 1) DAY, '09:05:00'), NOW() - INTERVAL 174 MINUTE), 'TIEN_MAT', @thuquy, 0, NULL, NULL),
  (15, 'A1205', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(19, DAY(CURDATE())) - 1) DAY, '08:25:00'), NOW() - INTERVAL 175 MINUTE), 'TIEN_MAT', @admin, 1, NULL, NULL),
  (117, 'C1203', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(22, DAY(CURDATE())) - 1) DAY, '10:25:00'), NOW() - INTERVAL 176 MINUTE), 'TIEN_MAT', @thuquy, 1, NULL, NULL),
  (106, 'B2308', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(24, DAY(CURDATE())) - 1) DAY, '08:25:00'), NOW() - INTERVAL 177 MINUTE), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL),
  (131, 'B0908', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(25, DAY(CURDATE())) - 1) DAY, '08:25:00'), NOW() - INTERVAL 178 MINUTE), 'CHUYEN_KHOAN', @admin, 2, NULL, NULL),
  (90, 'A1708', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(25, DAY(CURDATE())) - 1) DAY, '15:20:00'), NOW() - INTERVAL 179 MINUTE), 'TIEN_MAT', @admin, 0, NULL, NULL),
  (100, 'B2105', 0, LEAST(TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 0 MONTH, '%Y-%m-01') + INTERVAL (LEAST(26, DAY(CURDATE())) - 1) DAY, '09:15:00'), NOW() - INTERVAL 180 MINUTE), 'CHUYEN_KHOAN', @thuquy, 1, NULL, NULL);
INSERT INTO demo_pt_thang VALUES (1, 5), (2, 4), (3, 3), (4, 2), (5, 1), (6, 0), (7, 5), (8, 4), (9, 3), (10, 2), (11, 5), (12, 4), (13, 3), (14, 2), (15, 0), (16, 5), (17, 4), (18, 3), (19, 2), (20, 1), (21, 0), (22, 5), (23, 4), (24, 3), (25, 1), (26, 5), (27, 4), (28, 2), (29, 1), (30, 5), (31, 4), (32, 3), (33, 2), (34, 1), (35, 0), (36, 5), (37, 4), (38, 5), (39, 5), (40, 4), (41, 3), (42, 5), (43, 4), (44, 3), (45, 2), (46, 1), (47, 5), (48, 4), (49, 3), (49, 2), (50, 1), (51, 0), (52, 5), (53, 4), (54, 3), (55, 2), (56, 1), (57, 4), (58, 3), (59, 2), (60, 1), (61, 3), (62, 2), (63, 1), (64, 2), (65, 1), (66, 0), (67, 1), (68, 0), (69, 5), (70, 4), (71, 3), (72, 2), (73, 1), (74, 0), (75, 5), (76, 4), (77, 3), (78, 2), (79, 1), (80, 0), (81, 5), (82, 4), (83, 2), (84, 1), (85, 5), (86, 4), (87, 3), (88, 2), (89, 1), (90, 0), (91, 4), (92, 3), (93, 2), (94, 1), (95, 0), (96, 4), (97, 3), (98, 2), (99, 1), (100, 0), (101, 5), (102, 4), (103, 3), (104, 2), (105, 1), (106, 0), (107, 5), (108, 4), (109, 2), (110, 1), (111, 0), (112, 5), (113, 4), (114, 3), (115, 2), (116, 1), (117, 0), (118, 5), (119, 4), (120, 3), (121, 2), (122, 0), (123, 5), (124, 4), (125, 3), (126, 1), (127, 5), (128, 3), (129, 2), (130, 1), (131, 0), (132, 5), (133, 3), (134, 2), (135, 1), (136, 5), (137, 3), (138, 2), (139, 1), (140, 0), (141, 5), (142, 4), (143, 3), (144, 2), (145, 1), (146, 5), (147, 4), (148, 3), (149, 1), (150, 4), (151, 3), (152, 2), (153, 1), (154, 5), (155, 4), (156, 3), (157, 2), (158, 1), (159, 5), (160, 4), (161, 3), (162, 2), (163, 1), (164, 5), (165, 4), (166, 3), (167, 2), (168, 0), (169, 5), (170, 4), (171, 3), (172, 2), (173, 1), (174, 0), (175, 5), (176, 4), (177, 3), (178, 2), (179, 1), (180, 2);
INSERT INTO demo_pt_quy VALUES
  (1, 'Quỹ khuyến học', 500000),
  (6, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 0),
  (8, 'Quỹ khuyến học', 100000),
  (14, 'Quỹ khuyến học', 0),
  (15, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 200000),
  (19, 'Quỹ khuyến học', 50000),
  (23, 'Quỹ khuyến học', 100000),
  (27, 'Quỹ khuyến học', 50000),
  (31, 'Quỹ khuyến học', 0),
  (35, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 200000),
  (35, 'Quỹ biển đảo', 50000),
  (37, 'Quỹ khuyến học', 100000),
  (44, 'Quỹ khuyến học', 100000),
  (51, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 0),
  (51, 'Quỹ biển đảo', 50000),
  (53, 'Quỹ khuyến học', 100000),
  (57, 'Quỹ khuyến học', 200000),
  (61, 'Quỹ khuyến học', 500000),
  (67, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 100000),
  (70, 'Quỹ khuyến học', 100000),
  (74, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 50000),
  (76, 'Quỹ khuyến học', 500000),
  (80, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 100000),
  (82, 'Quỹ khuyến học', 500000),
  (90, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 200000),
  (92, 'Quỹ khuyến học', 100000),
  (96, 'Quỹ khuyến học', 100000),
  (102, 'Quỹ khuyến học', 100000),
  (106, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 200000),
  (106, 'Quỹ biển đảo', 50000),
  (114, 'Quỹ khuyến học', 200000),
  (117, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 200000),
  (118, 'Quỹ khuyến học', 100000),
  (122, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 200000),
  (125, 'Quỹ khuyến học', 500000),
  (129, 'Quỹ khuyến học', 100000),
  (131, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 200000),
  (134, 'Quỹ khuyến học', 500000),
  (137, 'Quỹ khuyến học', 0),
  (140, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 50000),
  (140, 'Quỹ biển đảo', 50000),
  (143, 'Quỹ khuyến học', 200000),
  (147, 'Quỹ khuyến học', 200000),
  (150, 'Quỹ khuyến học', 50000),
  (160, 'Quỹ khuyến học', 100000),
  (165, 'Quỹ khuyến học', 100000),
  (168, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 0),
  (174, CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE())), 100000),
  (174, 'Quỹ biển đảo', 50000),
  (176, 'Quỹ khuyến học', 0);

-- Các dòng của mỗi phiếu: khoản phải nộp của những tháng phiếu thu, rồi
-- đóng góp tự nguyện. Tháng này chỉ có phiếu khi đã chốt (@thang_nay).
DROP TEMPORARY TABLE IF EXISTS demo_dong;
CREATE TEMPORARY TABLE demo_dong (
    khoa INT, khoan_phi_id INT, phai_nop_id INT NULL,
    so_tien DECIMAL(14, 2));
INSERT INTO demo_dong
SELECT t.khoa, pn.khoan_phi_id, pn.id, pn.so_tien
FROM demo_pt_thang t
JOIN demo_pt p ON p.khoa = t.khoa
JOIN ho_gia_dinh h ON h.ma_can_ho = p.ma
JOIN dot_thu d ON d.thang = DATE_FORMAT(CURDATE() - INTERVAL t.k MONTH,
                                        '%Y-%m-01')
JOIN phai_nop pn ON pn.dot_thu_id = d.id AND pn.ho_id = h.id
WHERE @chay AND (p.k_thu > 0 OR @thang_nay);
INSERT INTO demo_dong
SELECT q.khoa, k.id, NULL, q.so_tien
FROM demo_pt_quy q
JOIN demo_pt p ON p.khoa = q.khoa
JOIN khoan_phi k ON k.ten = q.ten AND k.loai = 'TU_NGUYEN'
WHERE @chay AND (p.k_thu > 0 OR @thang_nay);

-- Phiếu: số biên lai PT-<năm>-<5 chữ số> liên tục theo năm, theo thời điểm
-- (giả định 31); tổng = tổng các dòng.
DROP TEMPORARY TABLE IF EXISTS demo_tong;
CREATE TEMPORARY TABLE demo_tong AS
SELECT khoa, SUM(so_tien) AS tong FROM demo_dong GROUP BY khoa;
INSERT INTO phieu_thu (nam, so, ho_id, tong_tien, hinh_thuc, nguoi_thu,
                       thoi_diem, so_lan_in, da_huy, nguoi_huy,
                       thoi_diem_huy, ly_do_huy)
SELECT YEAR(p.thoi_diem),
       COALESCE(b.so_cuoi, 0) + ROW_NUMBER() OVER (
           PARTITION BY YEAR(p.thoi_diem) ORDER BY p.thoi_diem, p.khoa),
       h.id, t.tong, IF(t.tong = 0, NULL, p.hinh_thuc), p.nguoi,
       p.thoi_diem, IF(t.tong = 0, 0, p.so_lan_in), p.ly_do_huy IS NOT NULL,
       IF(p.ly_do_huy IS NULL, NULL, p.nguoi),
       IF(p.ly_do_huy IS NULL, NULL,
          p.thoi_diem + INTERVAL p.phut_huy MINUTE),
       p.ly_do_huy
FROM demo_pt p
JOIN demo_tong t ON t.khoa = p.khoa
JOIN ho_gia_dinh h ON h.ma_can_ho = p.ma
LEFT JOIN bo_dem_bien_lai b ON b.nam = YEAR(p.thoi_diem)
WHERE @chay
ORDER BY p.thoi_diem, p.khoa;

INSERT INTO phieu_thu_chi_tiet (phieu_thu_id, khoan_phi_id, phai_nop_id,
                                so_tien, con_hieu_luc)
SELECT pt.id, dd.khoan_phi_id, dd.phai_nop_id, dd.so_tien, NOT pt.da_huy
FROM demo_dong dd
JOIN demo_pt p ON p.khoa = dd.khoa
JOIN ho_gia_dinh h ON h.ma_can_ho = p.ma
JOIN phieu_thu pt ON pt.ho_id = h.id AND pt.thoi_diem = p.thoi_diem
WHERE @chay
ORDER BY pt.id, dd.phai_nop_id IS NULL, dd.phai_nop_id;

INSERT INTO bo_dem_bien_lai (nam, so_cuoi)
SELECT nam, MAX(so) FROM phieu_thu WHERE @chay GROUP BY nam
ON DUPLICATE KEY UPDATE so_cuoi = GREATEST(so_cuoi, VALUES(so_cuoi));

-- ---------- Nhật ký thao tác (TS-002): chỉ thêm, đúng khuôn chữ ứng dụng ----------
INSERT INTO nhat_ky (thoi_diem, tai_khoan_id, thao_tac, doi_tuong, noi_dung)
SELECT ngay_tao, @admin, 'TAO_TAI_KHOAN', 'Tài khoản thuquy',
       'Họ tên: Trần Thị Bình; vai trò: Thủ quỹ'
FROM tai_khoan WHERE @chay AND ten_dang_nhap = 'thuquy';

INSERT INTO nhat_ky (thoi_diem, tai_khoan_id, thao_tac, doi_tuong, noi_dung)
SELECT ngay_tao, @admin, 'TAO_KHOAN_TU_NGUYEN', CONCAT('Khoản thu ', ten),
       CONCAT('Thu từ ', DATE_FORMAT(tu_ngay, '%d/%m/%Y'), ' đến ',
              DATE_FORMAT(den_ngay, '%d/%m/%Y'), '; Không cố định')
FROM khoan_phi WHERE @chay AND ten = 'Quỹ khuyến học';

-- Hộ mới chuyển đến trong 6 tháng: thêm hộ (kèm chủ hộ) và các thành viên.
INSERT INTO nhat_ky (thoi_diem, tai_khoan_id, thao_tac, doi_tuong, noi_dung)
SELECT h.ngay_tao, @admin, 'THEM_HO', CONCAT('Hộ ', h.ma_can_ho),
       CONCAT('Chủ hộ ', c.ho_ten, '; ',
              CASE h.loai_can_ho WHEN 'KIOT' THEN 'Ki-ốt'
                   WHEN 'PENTHOUSE' THEN 'Penthouse' ELSE 'Căn hộ' END,
              ', tầng ', h.tang, ', ', FORMAT(h.dien_tich, 2, 'de_DE'),
              ' m², chuyển đến ', DATE_FORMAT(h.ngay_chuyen_den, '%d/%m/%Y'))
FROM ho_gia_dinh h JOIN nhan_khau c ON c.chu_ho_cua = h.id
WHERE @chay AND h.ngay_chuyen_den >= DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01');

INSERT INTO nhat_ky (thoi_diem, tai_khoan_id, thao_tac, doi_tuong, noi_dung)
SELECT n.ngay_tao + INTERVAL 5 MINUTE, @admin, 'THEM_NHAN_KHAU',
       CONCAT('Nhân khẩu ', n.ho_ten, ', hộ ', h.ma_can_ho),
       CONCAT('Quan hệ với chủ hộ: ',
              CASE n.quan_he WHEN 'VO_CHONG' THEN 'Vợ/chồng'
                   WHEN 'CON' THEN 'Con' ELSE 'Khác' END, '; ',
              IF(n.loai_cu_tru = 'TAM_TRU',
                 CONCAT('Tạm trú ', DATE_FORMAT(t.tu_ngay, '%d/%m/%Y'), ' – ',
                        DATE_FORMAT(t.den_ngay, '%d/%m/%Y'),
                        IF(t.ly_do IS NULL, '', CONCAT('. Lý do: ', t.ly_do))),
                 'Thường trú'))
FROM nhan_khau n JOIN ho_gia_dinh h ON h.id = n.ho_id
LEFT JOIN tam_tru_tam_vang t ON t.nhan_khau_id = n.id AND t.loai = 'TAM_TRU'
WHERE @chay AND n.quan_he <> 'CHU_HO'
  AND n.ngay_tao >= TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 5 MONTH, '%Y-%m-01'), '00:00:00')
  AND h.ma_can_ho IN (SELECT ma FROM demo_ho);

INSERT INTO nhat_ky (thoi_diem, tai_khoan_id, thao_tac, doi_tuong, noi_dung)
SELECT t.ngay_tao, @admin, 'GHI_TAM_TRU_TAM_VANG',
       CONCAT('Nhân khẩu ', n.ho_ten, ', hộ ', h.ma_can_ho),
       CONCAT(IF(t.loai = 'TAM_TRU', 'Tạm trú', 'Tạm vắng'), ' ',
              DATE_FORMAT(t.tu_ngay, '%d/%m/%Y'), ' – ',
              DATE_FORMAT(t.den_ngay, '%d/%m/%Y'),
              IF(t.ly_do IS NULL, '', CONCAT('. Lý do: ', t.ly_do)))
FROM tam_tru_tam_vang t JOIN nhan_khau n ON n.id = t.nhan_khau_id
JOIN ho_gia_dinh h ON h.id = n.ho_id
WHERE @chay AND t.loai = 'TAM_VANG' AND h.ma_can_ho IN (SELECT ma FROM demo_ho);

INSERT INTO nhat_ky (thoi_diem, tai_khoan_id, thao_tac, doi_tuong, noi_dung)
SELECT TIMESTAMP(CURDATE() - INTERVAL 100 DAY, '10:00:00'), @admin, 'DOI_CHU_HO', 'Hộ C1003',
       'Chủ hộ: Bùi Đức Long → Ngô Thị Thanh Mai; Bùi Đức Long ở lại, quan hệ với chủ hộ: Vợ/chồng'
FROM DUAL WHERE @chay
UNION ALL
SELECT TIMESTAMP(CURDATE() - INTERVAL 45 DAY, '10:00:00'), @admin, 'DOI_CHU_HO', 'Hộ B1406',
       'Chủ hộ: Đặng Văn Khải → Đặng Quốc Hưng; Đặng Văn Khải rời hộ'
FROM DUAL WHERE @chay
UNION ALL
SELECT TIMESTAMP(DATE_FORMAT(CURDATE() - INTERVAL 3 MONTH, '%Y-%m-01') + INTERVAL 27 DAY, '10:00:00'), @admin, 'HO_CHUYEN_DI',
       'Hộ B1104', 'Cả hộ chuyển đi, 3 nhân khẩu'
FROM DUAL WHERE @chay;

INSERT INTO nhat_ky (thoi_diem, tai_khoan_id, thao_tac, doi_tuong, noi_dung)
SELECT d.ngay_chot, d.nguoi_chot, 'CHOT_CONG_NO',
       CONCAT('Tháng ', DATE_FORMAT(d.thang, '%m/%Y')),
       CONCAT('Thêm ', COUNT(*), ' khoản phải nộp cho ',
              COUNT(DISTINCT p.ho_id), ' hộ')
FROM dot_thu d JOIN phai_nop p ON p.dot_thu_id = d.id
WHERE @chay GROUP BY d.id, d.ngay_chot, d.nguoi_chot, d.thang;

INSERT INTO nhat_ky (thoi_diem, tai_khoan_id, thao_tac, doi_tuong, noi_dung)
SELECT pt.thoi_diem, pt.nguoi_thu, 'LAP_PHIEU_THU',
       CONCAT('Phiếu PT-', pt.nam, '-', LPAD(pt.so, 5, '0')),
       CONCAT('Hộ ', h.ma_can_ho, '; ',
              (SELECT COUNT(*) FROM phieu_thu_chi_tiet c
               WHERE c.phieu_thu_id = pt.id),
              ' khoản, tổng ', FORMAT(pt.tong_tien, 0, 'de_DE'), ' đ, ',
              CASE pt.hinh_thuc WHEN 'TIEN_MAT' THEN 'tiền mặt'
                   WHEN 'CHUYEN_KHOAN' THEN 'chuyển khoản'
                   ELSE 'không thu tiền' END)
FROM phieu_thu pt JOIN ho_gia_dinh h ON h.id = pt.ho_id
WHERE @chay ORDER BY pt.thoi_diem, pt.id;

INSERT INTO nhat_ky (thoi_diem, tai_khoan_id, thao_tac, doi_tuong, noi_dung)
SELECT pt.thoi_diem_huy, pt.nguoi_huy, 'HUY_PHIEU_THU',
       CONCAT('Phiếu PT-', pt.nam, '-', LPAD(pt.so, 5, '0')),
       CONCAT('Hộ ', h.ma_can_ho, '; lý do: ', pt.ly_do_huy)
FROM phieu_thu pt JOIN ho_gia_dinh h ON h.id = pt.ho_id
WHERE @chay AND pt.da_huy;

COMMIT;

SELECT IF(@chay,
          CONCAT('Đã nạp dữ liệu demo: ',
                 (SELECT COUNT(*) FROM ho_gia_dinh), ' hộ, ',
                 (SELECT COUNT(*) FROM nhan_khau), ' nhân khẩu, ',
                 (SELECT COUNT(*) FROM phieu_thu), ' phiếu thu'),
          'Không nạp: CSDL đã có dữ liệu demo, đợt chốt hoặc phiếu thu, hoặc chưa chạy seed.sql. Dựng lại CSDL (docs/setup.md, mục 2) rồi chạy seed.sql và tệp này.')
       AS ket_qua;
