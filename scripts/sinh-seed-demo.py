"""Sinh database/seed-demo.sql: du lieu demo BlueMoon (ho, nhan khau, 6 thang
chot cong no va thu phi, nhat ky).

Chay:  python scripts/sinh-seed-demo.py
Ket qua tat dinh (Random(2026)): chay lai ra dung tep cu. Ngay thang trong
SQL tinh theo CURDATE() luc nap, nhu seed.sql. Moi nguoi trong tep la hu cau.
"""
import random
from pathlib import Path

R = random.Random(2026)
GOC = Path(__file__).resolve().parent.parent
DICH = GOC / "database" / "seed-demo.sql"

# Mat khau thuquy / Thuquy@123, bam BCrypt cost 12 bang MatKhauUtil.bam.
BAM_THU_QUY = "$2a$12$Xq/3XeEhByeKFDZDqGS3euFaQOyguLh/YfLbYhTVbvHsybcfpUtGi"

HO = ["Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Vũ", "Đặng", "Bùi", "Đỗ",
      "Ngô", "Dương", "Phan", "Trịnh", "Đinh", "Mai", "Lương", "Hồ", "Cao",
      "Lý", "Tạ"]
DEM_NAM = ["Văn", "Đức", "Minh", "Quốc", "Hữu", "Thành", "Quang", "Ngọc",
           "Trọng", "Xuân", "Gia", "Anh", "Tiến", "Mạnh"]
TEN_NAM = ["Hùng", "Dũng", "Tuấn", "Hải", "Long", "Sơn", "Thắng", "Cường",
           "Hiếu", "Phong", "Khánh", "Bình", "Trung", "Đạt", "Kiên", "Lâm",
           "Thịnh", "Vinh", "Huy", "Khôi", "Bảo", "Phúc", "An", "Toàn",
           "Nghĩa", "Tùng", "Quân", "Duy", "Hưng", "Lộc"]
DEM_NU = ["Thị", "Thu", "Ngọc", "Thanh", "Minh", "Thùy", "Phương", "Kim",
          "Bảo", "Hồng", "Diệu", "Khánh"]
TEN_NU = ["Lan", "Hương", "Hoa", "Mai", "Hà", "Linh", "Trang", "Ngọc",
          "Thảo", "Yến", "Nhung", "Hạnh", "Dung", "Loan", "Phương", "Vy",
          "Anh", "Chi", "Ngân", "Quỳnh", "My", "Hiền", "Huyền", "Giang",
          "Oanh", "Tâm", "Nga", "Uyên"]

# Ma tinh dau CCCD va noi thuong tru tuong ung (dia danh that, nguoi hu cau).
TINH = [("001", None), ("001", None), ("001", None), ("031", None),
        ("033", None), ("034", None), ("035", None), ("036", None),
        ("037", None), ("038", None), ("040", None), ("030", None)]
NOI_TAM_TRU = [
    ("036", "Xã Nghĩa Lâm, huyện Nghĩa Hưng, Nam Định"),
    ("038", "Xã Hoằng Đạt, huyện Hoằng Hóa, Thanh Hóa"),
    ("040", "Xã Diễn Kim, huyện Diễn Châu, Nghệ An"),
    ("034", "Xã Thụy Liên, huyện Thái Thụy, Thái Bình"),
    ("030", "Phường Phạm Ngũ Lão, TP Hải Dương, Hải Dương"),
    ("037", "Xã Gia Sinh, huyện Gia Viễn, Ninh Bình"),
]

CCCD_DA_DUNG = {"001075001234", "001182004567", "001080007890",
                "001310002345", "001090005678", "001068003456",
                "001195006789", "036093004321", "034199008765"}


def cccd(nam_sinh, nam_gioi, tinh=None):
    """CCCD hu cau: ma tinh + gioi/the ky + 2 so cuoi nam sinh + 6 so."""
    tinh = tinh or R.choice(TINH)[0]
    the_ky = 0 if nam_sinh < 2000 else 2
    gioi = the_ky + (0 if nam_gioi else 1)
    while True:
        so = f"{tinh}{gioi}{nam_sinh % 100:02d}{R.randint(0, 999999):06d}"
        if so not in CCCD_DA_DUNG:
            CCCD_DA_DUNG.add(so)
            return so


def ten(nam_gioi, ho=None):
    ho = ho or R.choice(HO)
    if nam_gioi:
        return f"{ho} {R.choice(DEM_NAM)} {R.choice(TEN_NAM)}"
    return f"{ho} {R.choice(DEM_NU)} {R.choice(TEN_NU)}"


def ngay_sinh(nam):
    return f"{nam}-{R.randint(1, 12):02d}-{R.randint(1, 28):02d}"


def q(s):
    """Chuoi SQL."""
    return "NULL" if s is None else "'" + str(s).replace("'", "''") + "'"


# ---------- bieu thuc ngay theo CURDATE() ----------
def ngay_lui(n):
    """Ngay cach hom nay n ngay (n > 0 la qua khu)."""
    return f"CURDATE() - INTERVAL {n} DAY" if n >= 0 \
        else f"CURDATE() + INTERVAL {-n} DAY"


def dau_thang(k):
    return f"DATE_FORMAT(CURDATE() - INTERVAL {k} MONTH, '%Y-%m-01')"


def ngay_thang(k, d):
    return f"{dau_thang(k)} + INTERVAL {d - 1} DAY"


# ---------- ho gia dinh ----------
class Ho:
    def __init__(self, ma, loai, tang, dt, sdt, ngay_den, den_k=None,
                 chuyen_di=None):
        self.ma, self.loai, self.tang, self.dt = ma, loai, tang, dt
        self.sdt, self.ngay_den = sdt, ngay_den   # ngay_den: bieu thuc SQL
        self.den_k = den_k        # chuyen den o thang k (None: tu lau)
        self.chuyen_di = chuyen_di  # (k, ngay) neu ca ho da chuyen di
        self.nk = []


class NK:
    def __init__(self, ho_ten, ns, nam_gioi, quan_he, cccd_so,
                 trang_thai="DANG_O", cu_tru="THUONG_TRU", noi=None,
                 ngay_den=None):
        self.ho_ten, self.ns, self.nam_gioi = ho_ten, ns, nam_gioi
        self.quan_he, self.cccd, self.trang_thai = quan_he, cccd_so, trang_thai
        self.cu_tru, self.noi, self.ngay_den = cu_tru, noi, ngay_den


def sdt():
    return R.choice(["09", "03", "08", "07"]) + f"{R.randint(0, 99999999):08d}"


def dien_tich_can_ho():
    return round(R.uniform(45, 120), 2)


def gia_dinh(so_nguoi, nam_chu=None):
    """Thanh vien mot ho: chu ho, vo/chong, con, bo/me (KHAC)."""
    nam_gioi = R.random() < 0.6 if nam_chu is None else nam_chu
    ns_chu = R.randint(1955, 1994)
    ho_chu = R.choice(HO)
    ds = [NK(ten(nam_gioi, ho_chu), ngay_sinh(ns_chu), nam_gioi, "CHU_HO",
             cccd(ns_chu, nam_gioi))]
    con_lai = so_nguoi - 1
    ho_bo = ho_chu
    ns_tre_nhat = ns_chu
    if con_lai > 0 and (so_nguoi >= 3 or R.random() < 0.7):
        ns_vc = ns_chu + R.randint(-4, 6)
        ho_vc = R.choice(HO)
        ds.append(NK(ten(not nam_gioi, ho_vc), ngay_sinh(ns_vc), not nam_gioi,
                     "VO_CHONG", cccd(ns_vc, not nam_gioi)))
        if not nam_gioi:
            ho_bo = ho_vc
        ns_tre_nhat = max(ns_chu, ns_vc)
        con_lai -= 1
    cha_me = None
    if con_lai >= 2 and so_nguoi >= 5:
        con_lai -= 1
        cha_me = True
    da_dung = {d.ho_ten for d in ds}
    # Con nho hon bo me tre nhat it nhat 22 tuoi, anh chi em cach nhau
    # 2-4 nam. Tranh nam sinh 2010-2013 de khoi sat moc 14 tuoi (bat buoc
    # CCCD) khi demo; sinh tu 2014 thi de trong CCCD.
    tu = ns_tre_nhat + 22
    den = min(ns_tre_nhat + 40, 2023)
    nam = R.randint(tu, max(tu, den - 3 * (con_lai - 1)))
    for _ in range(con_lai):
        if 2010 <= nam <= 2013:
            nam += 4
        nam = min(nam, 2024)
        gt = R.random() < 0.5
        while True:
            t = ten(gt, ho_bo)
            if t not in da_dung:
                break
        da_dung.add(t)
        so = None if nam >= 2014 else cccd(nam, gt)
        ds.append(NK(t, ngay_sinh(nam), gt, "CON", so))
        nam += R.randint(2, 4)
    if cha_me:
        ns = max(1936, ns_chu - R.randint(24, 32))
        gt = R.random() < 0.4
        ho_cm = ho_chu if gt else R.choice(HO)
        ds.append(NK(ten(gt, ho_cm), ngay_sinh(ns), gt, "KHAC", cccd(ns, gt)))
    return ds


def ma_can_ho(da_co):
    while True:
        ma = f"{R.choice('ABC')}{R.randint(5, 28):02d}{R.randint(1, 8):02d}"
        if ma not in da_co:
            da_co.add(ma)
            return ma


DA_CO = {"K01", "A0601", "A1205", "PH01"}
# Ma dac biet dung o phan thu phi, dat truoc de khoi trung.
DAC_BIET = ["B0907", "C1508", "A2203", "B1104", "C0806", "A1702", "B2005",
            "B1406", "C1003"]
DA_CO.update(DAC_BIET)

ho_ds = []
QUY_MO = [1] * 5 + [2] * 10 + [3] * 9 + [4] * 6 + [5] * 2 + [6] * 1
R.shuffle(QUY_MO)
# 3 kiot (tang 1), 1 ho da chuyen di, 2 ho doi chu ho, 4 ho moi chuyen den.
kiot = ["K02", "K03", "K04"]
ma_thuong = []
for i in range(33 - len(DAC_BIET) - len(kiot) + 3):
    ma_thuong.append(ma_can_ho(DA_CO))
ma_moi_den = ma_thuong[:4]      # chuyen den o thang k = 4, 3, 2, 1

for i, ma in enumerate(kiot):
    nam_den = R.randint(2019, 2024)
    h = Ho(ma, "KIOT", 1, round(R.uniform(20, 40), 2), sdt(),
           q(f"{nam_den}-{R.randint(1, 12):02d}-{R.randint(1, 28):02d}"))
    h.nk = gia_dinh(R.choice([1, 2, 2]))
    ho_ds.append(h)

for j, ma in enumerate(DAC_BIET[:7] + ma_thuong):
    tang = int(ma[1:3])
    nam_den = R.randint(2018, 2025)
    ngay_den = q(f"{nam_den}-{R.randint(1, 12):02d}-{R.randint(1, 28):02d}")
    den_k = None
    if ma in ma_moi_den:
        den_k = 4 - ma_moi_den.index(ma)
        ngay_den = ngay_thang(den_k, 1)
    h = Ho(ma, "CAN_HO", tang, dien_tich_can_ho(),
           sdt() if R.random() < 0.85 else None, ngay_den, den_k)
    h.nk = gia_dinh(QUY_MO[j % len(QUY_MO)])
    ho_ds.append(h)

# B1104: ca ho chuyen di ngay 28 cua thang k = 3 (truoc lan chot thang 2).
b1104 = next(h for h in ho_ds if h.ma == "B1104")
b1104.chuyen_di = (3, 28)
b1104.ngay_den = q("2021-04-10")
b1104.nk = gia_dinh(3, nam_chu=True)
for n in b1104.nk:
    n.trang_thai = "DA_CHUYEN_DI"

# B1406: doi chu ho, bo (chu ho cu) roi ho 45 ngay truoc, con trai lam chu ho.
nam_bo = 1941
bo = NK("Đặng Văn Khải", ngay_sinh(nam_bo), True, "KHAC",
        cccd(nam_bo, True), trang_thai="DA_CHUYEN_DI")
con = NK("Đặng Quốc Hưng", ngay_sinh(1968), True, "CHU_HO", cccd(1968, True))
dau = NK("Lương Thị Minh Nguyệt", ngay_sinh(1971), False, "VO_CHONG",
         cccd(1971, False))
chau = NK("Đặng Gia Bảo", ngay_sinh(2016), True, "CON", None)
b1406 = Ho("B1406", "CAN_HO", 14, 96.80, sdt(), q("2016-08-20"))
b1406.nk = [con, dau, chau, bo]
ho_ds.append(b1406)

# C1003: chong (chu ho cu) nhuong chu ho cho vo 100 ngay truoc, van o lai.
c1003 = Ho("C1003", "CAN_HO", 10, 71.25, sdt(), q("2020-11-02"))
c1003.nk = [NK("Ngô Thị Thanh Mai", ngay_sinh(1979), False, "CHU_HO",
               cccd(1979, False)),
            NK("Bùi Đức Long", ngay_sinh(1976), True, "VO_CHONG",
               cccd(1976, True)),
            NK("Bùi Ngọc Diệp", ngay_sinh(2008), False, "CON",
               cccd(2008, False))]
ho_ds.append(c1003)

# Nguoi tam tru (6): them vao 6 ho dang o, 4 con han, 2 da qua han.
DOT_TAM_TRU = [(-150, 200, "Thuê nhà làm việc"), (-120, 60, "Đi học"),
               (-90, 270, "Làm việc tại khu công nghiệp"),
               (-40, 140, "Ở cùng người thân"),
               (-175, -15, "Thuê nhà làm việc"), (-160, -40, "Đi học")]
ung_vien = [h for h in ho_ds if h.chuyen_di is None and h.loai == "CAN_HO"
            and h.den_k is None and h.ma not in ("B1406", "C1003")]
R.shuffle(ung_vien)
dot_ds = []   # (ma, ho_ten, loai, tu, den, ly_do)
for i, (tu, den, ly_do) in enumerate(DOT_TAM_TRU):
    h = ung_vien[i]
    tinh, noi = NOI_TAM_TRU[i]
    ns = R.randint(1985, 2004)
    gt = R.random() < 0.5
    while True:
        t = ten(gt)
        if t not in {n.ho_ten for n in h.nk}:
            break
    nk = NK(t, ngay_sinh(ns), gt, "KHAC", cccd(ns, gt, tinh),
            cu_tru="TAM_TRU", noi=noi, ngay_den=ngay_lui(-tu))
    h.nk.append(nk)
    dot_ds.append((h.ma, nk.ho_ten, "TAM_TRU", tu, den, ly_do))

# Tam vang (4): nguoi lon thuong tru o 4 ho khac, dang con han.
DOT_TAM_VANG = [(-25, 35, "Công tác Đà Nẵng"), (-10, 50, "Về quê chăm mẹ"),
                (-40, 20, "Điều trị tại bệnh viện tỉnh"),
                (-3, 90, "Đi làm việc ở Bình Dương")]
for i, (tu, den, ly_do) in enumerate(DOT_TAM_VANG):
    h = ung_vien[6 + i]
    nk = next(n for n in h.nk if n.cccd and n.cu_tru == "THUONG_TRU"
              and n.quan_he != "CON")
    dot_ds.append((h.ma, nk.ho_ten, "TAM_VANG", tu, den, ly_do))

# ---------- ke hoach thu phi ----------
# Thang k = 5..0 (0 la thang nay). Ho no dong: khong thu cac thang ghi.
KHONG_THU = {"B0907": {3, 2, 1, 0}, "C1508": {4, 3, 2, 1, 0},
             "A2203": {2, 1, 0}, "B1104": {5, 4, 3}, "A0601": {1, 0},
             "C0806": {0}}
HO_SEED = ["K01", "A0601", "A1205", "PH01"]
phieu = []   # dict: ma, k_thu, ngay, gio, ht, nguoi, in, thang[], quy[], huy


def gio():
    if R.random() < 0.55:
        return f"{R.randint(8, 11):02d}:{R.choice(['05', '15', '25', '40', '50'])}:00"
    return f"{R.randint(13, 16):02d}:{R.choice(['10', '20', '35', '45', '55'])}:00"


def thang_cua(h):
    """Cac thang ho co khoan phai nop (theo luat chot cong no)."""
    if h is None:      # ho seed: chuyen den 2023-2024
        return list(range(5, -1, -1))
    if h.chuyen_di:
        return [k for k in range(5, -1, -1) if k >= h.chuyen_di[0]]
    if h.den_k is not None:
        return [k for k in range(5, -1, -1) if k <= h.den_k]
    return list(range(5, -1, -1))


tat_ca = [(m, None) for m in HO_SEED] + [(h.ma, h) for h in ho_ds]
# Moi thang cu them 3 ho khac chua dong (moi ho mot thang), de ty le dong
# du moi thang khoang 85-90% va co nhieu ho no 1-2 thang.
ung_vien_no = [m for m, h in tat_ca if m not in KHONG_THU
               and m not in ("A1702", "B2005", "K01", "PH01")
               and (h is None or (h.chuyen_di is None and h.den_k is None))]
R.shuffle(ung_vien_no)
for k in range(1, 6):
    for m in ung_vien_no[3 * (k - 1):3 * k]:
        KHONG_THU.setdefault(m, set()).add(k)
for ma, h in tat_ca:
    khong = KHONG_THU.get(ma, set())
    for k in thang_cua(h):
        if k in khong:
            continue
        if k == 0 and R.random() < 0.5 and ma not in ("K01", "PH01"):
            continue                  # thang nay: khoang mot nua da dong
        if ma == "A1702" and k == 3:
            continue                  # dong bu cung thang 2
        tre = k >= 1 and R.random() < 0.12
        k_thu, ngay = (k - 1, R.randint(3, 6)) if tre else (k, R.randint(3, 26))
        cac_thang = [k]
        if ma == "A1702" and k == 2:
            cac_thang = [3, 2]
        phieu.append(dict(ma=ma, k_thu=k_thu, ngay=ngay, gio=gio(),
                          ht=R.choice(["TIEN_MAT", "TIEN_MAT", "CHUYEN_KHOAN"]),
                          nguoi=R.choice(["admin", "thuquy", "thuquy"]),
                          in_=R.choices([0, 1, 2], [15, 75, 10])[0],
                          thang=cac_thang, quy=[], huy=None))

# Phieu huy: C0806 thang 1 thu nham ho (huy, khong thu lai -> no 2 thang);
# B2005 thang 2 chon nham hinh thuc (huy, thu lai 30 phut sau bang CK).
for p in list(phieu):
    if p["ma"] == "C0806" and p["thang"] == [1]:
        p.update(k_thu=1, ngay=12, gio="09:20:00", huy=("Thu nhầm hộ", 15),
                 in_=0)
    if p["ma"] == "B2005" and p["thang"] == [2]:
        p.update(k_thu=2, ngay=9, gio="10:05:00", ht="TIEN_MAT",
                 huy=("Chọn nhầm hình thức thanh toán", 10), in_=0)
        lai = dict(p)
        lai.update(gio="10:35:00", ht="CHUYEN_KHOAN", huy=None, in_=1,
                   quy=[])
        phieu.append(lai)

# Dong gop tu nguyen: Quy khuyen hoc (thang 4..2), quy dang mo thang nay.
da_gop = set()
for p in sorted(phieu, key=lambda p: (-p["k_thu"], p["ngay"])):
    if p["huy"]:
        continue
    if 2 <= p["k_thu"] <= 4 and (p["ma"], "kh") not in da_gop:
        x = R.random()
        if x < 0.35:
            p["quy"].append(("Quỹ khuyến học",
                             R.choice([50000, 100000, 100000, 200000, 500000])))
            da_gop.add((p["ma"], "kh"))
        elif x < 0.45:
            p["quy"].append(("Quỹ khuyến học", 0))
            da_gop.add((p["ma"], "kh"))
    if p["k_thu"] == 0 and (p["ma"], "ngheo") not in da_gop:
        da_gop.add((p["ma"], "ngheo"))
        x = R.random()
        if x < 0.5:
            p["quy"].append(("@ngheo", R.choice([50000, 100000, 200000,
                                                 300000])))
        elif x < 0.6:
            p["quy"].append(("@ngheo", 0))
        if R.random() < 0.3:
            p["quy"].append(("Quỹ biển đảo", 50000))

# Mot ho khong co hai phieu cung thoi diem.
da_co_gio = set()
for p in phieu:
    while (p["ma"], p["k_thu"], p["ngay"], p["gio"]) in da_co_gio:
        p["gio"] = gio()
    da_co_gio.add((p["ma"], p["k_thu"], p["ngay"], p["gio"]))
for i, p in enumerate(phieu, start=1):
    p["khoa"] = i

# ---------- viet SQL ----------
out = []
w = out.append


def ngay_gio_phieu(p, chi_so):
    if p["k_thu"] == 0:
        ngay = (f"{dau_thang(0)} + INTERVAL (LEAST({p['ngay']}, "
                f"DAY(CURDATE())) - 1) DAY")
        return (f"LEAST(TIMESTAMP({ngay}, '{p['gio']}'), "
                f"NOW() - INTERVAL {chi_so} MINUTE)")
    return f"TIMESTAMP({ngay_thang(p['k_thu'], p['ngay'])}, '{p['gio']}')"


w("""-- Dữ liệu DEMO cho BlueMoon. CHỈ CHẠY MỘT LẦN, trên CSDL vừa dựng lại từ
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
""")

w(f"""-- Thủ quỹ mẫu (US-020), tạo 7 tháng trước.
INSERT INTO tai_khoan (ten_dang_nhap, mat_khau_bam, ho_ten, vai_tro,
                       so_dien_thoai, ngay_tao)
SELECT 'thuquy', {q(BAM_THU_QUY)}, 'Trần Thị Bình', 'THU_QUY', '0912004455',
       TIMESTAMP(CURDATE() - INTERVAL 7 MONTH, '08:30:00')
FROM DUAL WHERE @chay
  AND NOT EXISTS (SELECT 1 FROM tai_khoan WHERE ten_dang_nhap = 'thuquy');
SET @thuquy = (SELECT id FROM tai_khoan WHERE ten_dang_nhap = 'thuquy');
""")

# ho
w("""-- ---------- Hộ gia đình (US-001) ----------
DROP TEMPORARY TABLE IF EXISTS demo_ho;
CREATE TEMPORARY TABLE demo_ho (
    ma VARCHAR(20) PRIMARY KEY, loai VARCHAR(10), tang INT,
    dien_tich DECIMAL(6, 2), sdt VARCHAR(15), ngay_den DATE,
    chuyen_di DATE NULL);
INSERT INTO demo_ho VALUES""")
rows = []
for h in ho_ds:
    cd = "NULL" if not h.chuyen_di else ngay_thang(*h.chuyen_di)
    rows.append(f"  ({q(h.ma)}, {q(h.loai)}, {h.tang}, {h.dt:.2f}, "
                f"{q(h.sdt)}, {h.ngay_den}, {cd})")
w(",\n".join(rows) + ";")
w("""
INSERT INTO ho_gia_dinh (ma_can_ho, loai_can_ho, tang, dien_tich,
                         so_dien_thoai, ngay_chuyen_den, trang_thai, ngay_tao)
SELECT ma, loai, tang, dien_tich, sdt, ngay_den,
       IF(chuyen_di IS NULL, 'DANG_O', 'DA_CHUYEN_DI'),
       TIMESTAMP(ngay_den, '09:00:00')
FROM demo_ho WHERE @chay ORDER BY ngay_den, ma;
""")

# nhan khau
w("""-- ---------- Nhân khẩu (US-002, US-003) ----------
DROP TEMPORARY TABLE IF EXISTS demo_nk;
CREATE TEMPORARY TABLE demo_nk (
    ma VARCHAR(20), ho_ten VARCHAR(100), ngay_sinh DATE, gioi_tinh CHAR(3),
    so_cccd CHAR(12) NULL, quan_he VARCHAR(10), trang_thai VARCHAR(15),
    loai_cu_tru VARCHAR(12), noi_thuong_tru VARCHAR(200) NULL, ngay_den DATE,
    PRIMARY KEY (ma, ho_ten));
INSERT INTO demo_nk VALUES""")
rows = []
for h in ho_ds:
    for n in h.nk:
        nd = n.ngay_den or h.ngay_den
        rows.append(f"  ({q(h.ma)}, {q(n.ho_ten)}, {q(n.ns)}, "
                    f"{q('NAM' if n.nam_gioi else 'NU')}, {q(n.cccd)}, "
                    f"{q(n.quan_he)}, {q(n.trang_thai)}, {q(n.cu_tru)}, "
                    f"{q(n.noi)}, {nd})")
w(",\n".join(rows) + ";")
w("""
INSERT INTO nhan_khau (ho_id, ho_ten, ngay_sinh, gioi_tinh, so_cccd, quan_he,
                       trang_thai, loai_cu_tru, noi_thuong_tru, ngay_tao)
SELECT h.id, d.ho_ten, d.ngay_sinh, d.gioi_tinh, d.so_cccd, d.quan_he,
       d.trang_thai, d.loai_cu_tru, d.noi_thuong_tru,
       TIMESTAMP(d.ngay_den, '09:10:00')
FROM demo_nk d JOIN ho_gia_dinh h ON h.ma_can_ho = d.ma
WHERE @chay ORDER BY h.id, d.quan_he = 'CHU_HO' DESC, d.ngay_sinh;
""")

# dot tam tru / tam vang
w("""-- Đợt tạm trú (6, hai đợt đã quá hạn) và tạm vắng (4, còn hạn).
DROP TEMPORARY TABLE IF EXISTS demo_dot;
CREATE TEMPORARY TABLE demo_dot (
    ma VARCHAR(20), ho_ten VARCHAR(100), loai VARCHAR(10), tu_ngay DATE,
    den_ngay DATE, ly_do VARCHAR(200));
INSERT INTO demo_dot VALUES""")
w(",\n".join(f"  ({q(m)}, {q(t)}, {q(l)}, {ngay_lui(-tu)}, {ngay_lui(-den)}, "
             f"{q(ly)})" for m, t, l, tu, den, ly in dot_ds) + ";")
w("""
INSERT INTO tam_tru_tam_vang (nhan_khau_id, loai, tu_ngay, den_ngay, ly_do,
                              ngay_tao)
SELECT n.id, d.loai, d.tu_ngay, d.den_ngay, d.ly_do,
       TIMESTAMP(d.tu_ngay, '08:45:00')
FROM demo_dot d JOIN ho_gia_dinh h ON h.ma_can_ho = d.ma
JOIN nhan_khau n ON n.ho_id = h.id AND n.ho_ten = d.ho_ten
WHERE @chay;
""")

# bien dong
w(f"""-- ---------- Biến động nhân khẩu (US-027) ----------
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
  ('B1406', 'Đặng Quốc Hưng', 'DOI_CHU_HO', {ngay_lui(45)},
   'Làm chủ hộ thay Đặng Văn Khải'),
  ('B1406', 'Đặng Văn Khải', 'CHUYEN_DI', {ngay_lui(45)}, 'Chủ hộ cũ rời hộ'),
  ('C1003', 'Ngô Thị Thanh Mai', 'DOI_CHU_HO', {ngay_lui(100)},
   'Làm chủ hộ thay Bùi Đức Long')""" + "".join(
    f",\n  ('B1104', {q(n.ho_ten)}, 'CHUYEN_DI', {ngay_thang(3, 28)}, "
    f"'Cả hộ chuyển đi')" for n in b1104.nk) + ";")
w("""
INSERT INTO bien_dong_nhan_khau (ho_id, nhan_khau_id, loai, ngay, ghi_chu,
                                 nguoi_ghi, thoi_diem)
SELECT h.id, n.id, d.loai, d.ngay, d.ghi_chu, @admin,
       TIMESTAMP(d.ngay, '10:00:00')
FROM demo_bd d JOIN ho_gia_dinh h ON h.ma_can_ho = d.ma
JOIN nhan_khau n ON n.ho_id = h.id AND n.ho_ten = d.ho_ten
WHERE @chay;
""")

# khoan tu nguyen da qua
w(f"""-- ---------- Khoản thu ----------
-- Quỹ khuyến học: quỹ tự nguyện đã đóng, thu từ đầu 4 tháng trước đến hết
-- 2 tháng trước (US-006); để Thống kê có quỹ đã thu xong.
INSERT INTO khoan_phi (ten, loai, cach_tinh, don_gia, tu_ngay, den_ngay,
                       trang_thai, ngay_tao)
SELECT 'Quỹ khuyến học', 'TU_NGUYEN', 'KHONG_CO_DINH', NULL, {dau_thang(4)},
       LAST_DAY(CURDATE() - INTERVAL 2 MONTH), 'DANG_THU',
       TIMESTAMP({dau_thang(4)} - INTERVAL 3 DAY, '14:00:00')
FROM DUAL WHERE @chay;
""")

# chot cong no
w("-- ---------- Chốt công nợ 6 tháng (US-009) ----------")
w("INSERT INTO dot_thu (thang, nguoi_chot, ngay_chot)")
w("\nUNION ALL\n".join(
    f"SELECT {dau_thang(k)}, @admin, "
    f"TIMESTAMP({ngay_thang(k, 2)}, '08:30:00') FROM DUAL "
    f"WHERE @chay{' AND @thang_nay' if k == 0 else ''}"
    for k in range(5, -1, -1)) + ";")
w("""
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
""")

# phieu thu
w("""-- ---------- Phiếu thu 6 tháng (US-010 – US-014, US-007) ----------
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
INSERT INTO demo_pt VALUES""")
rows = []
for i, p in enumerate(sorted(phieu, key=lambda p: (p["k_thu"] == 0,
                                                   p["ngay"], p["gio"])), 1):
    nguoi = "@admin" if p["nguoi"] == "admin" else "@thuquy"
    ly, phut = (p["huy"] if p["huy"] else (None, None))
    rows.append(f"  ({p['khoa']}, {q(p['ma'])}, {p['k_thu']}, "
                f"{ngay_gio_phieu(p, i)}, {q(p['ht'])}, {nguoi}, {p['in_']}, "
                f"{q(ly)}, {'NULL' if phut is None else phut})")
w(",\n".join(rows) + ";")
w("INSERT INTO demo_pt_thang VALUES " + ", ".join(
    f"({p['khoa']}, {k})" for p in phieu for k in p["thang"]) + ";")
quy_rows = [(p["khoa"], t, s) for p in phieu for t, s in p["quy"]]
w("INSERT INTO demo_pt_quy VALUES\n" + ",\n".join(
    f"  ({k}, " + ("CONCAT('Quỹ vì người nghèo ', YEAR(CURDATE()))"
                   if t == "@ngheo" else q(t)) + f", {s})"
    for k, t, s in quy_rows) + ";")
w("""
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
""")

# nhat ky
w(f"""-- ---------- Nhật ký thao tác (TS-002): chỉ thêm, đúng khuôn chữ ứng dụng ----------
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
WHERE @chay AND h.ngay_chuyen_den >= {dau_thang(5)};

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
  AND n.ngay_tao >= TIMESTAMP({dau_thang(5)}, '00:00:00')
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
SELECT TIMESTAMP({ngay_lui(100)}, '10:00:00'), @admin, 'DOI_CHU_HO', 'Hộ C1003',
       'Chủ hộ: Bùi Đức Long → Ngô Thị Thanh Mai; Bùi Đức Long ở lại, quan hệ với chủ hộ: Vợ/chồng'
FROM DUAL WHERE @chay
UNION ALL
SELECT TIMESTAMP({ngay_lui(45)}, '10:00:00'), @admin, 'DOI_CHU_HO', 'Hộ B1406',
       'Chủ hộ: Đặng Văn Khải → Đặng Quốc Hưng; Đặng Văn Khải rời hộ'
FROM DUAL WHERE @chay
UNION ALL
SELECT TIMESTAMP({ngay_thang(3, 28)}, '10:00:00'), @admin, 'HO_CHUYEN_DI',
       'Hộ B1104', 'Cả hộ chuyển đi, {len(b1104.nk)} nhân khẩu'
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
""")

DICH.write_text("\n".join(out), encoding="utf-8", newline="\n")
so_nk = sum(len(h.nk) for h in ho_ds)
print(f"{DICH}: {len(ho_ds)} ho, {so_nk} nhan khau, {len(phieu)} phieu (du kien)")
