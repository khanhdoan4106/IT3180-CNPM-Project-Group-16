package com.bluemoon.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/**
 * Thang trục Y của biểu đồ cột tiền (mục "Quy tắc biểu đồ" trong
 * docs/design/ui.md): bắt đầu từ 0, chia đều theo mốc tròn 1, 2, 2,5, 5 ×
 * 10^n, giá trị lớn nhất nằm trong thang. Đơn vị chọn theo giá trị lớn nhất
 * để nhãn không thành 0,0x: từ 1 triệu trở lên là "Triệu đồng", dưới 1
 * triệu là "Nghìn đồng".
 *
 * @param donVi tên đơn vị, ghi vào tên trục, vd "Triệu đồng"
 * @param heSo số đồng của một đơn vị (1.000.000 hoặc 1.000)
 * @param tren giá trị trên cùng của thang, theo đơn vị
 * @param buoc khoảng cách giữa hai mốc, theo đơn vị
 */
public record ThangTrucY(String donVi, BigDecimal heSo, BigDecimal tren,
        BigDecimal buoc) {

    /** Đơn vị khi giá trị lớn nhất từ 1 triệu trở lên. */
    public static final String TRIEU = "Triệu đồng";
    /** Đơn vị khi giá trị lớn nhất dưới 1 triệu. */
    public static final String NGHIN = "Nghìn đồng";
    /**
     * Số khoảng chia mong muốn giữa 0 và giá trị lớn nhất (thang có 5 hoặc
     * 6 khoảng, thêm một bước khi cột cao nhất sát đỉnh).
     */
    static final int SO_KHOANG = 5;

    /** Khoảng trống tối thiểu trên cột cao nhất, tính theo bước. */
    static final BigDecimal KHOANG_TREN = new BigDecimal("0.3");

    private static final BigDecimal MOT_TRIEU = new BigDecimal("1000000");
    private static final BigDecimal MOT_NGHIN = new BigDecimal("1000");
    private static final BigDecimal[] MOC = {BigDecimal.ONE,
        new BigDecimal("2"), new BigDecimal("2.5"), new BigDecimal("5")};

    /**
     * Tính thang cho các giá trị tiền (đồng). Toàn 0 hoặc rỗng thì dùng
     * thang mặc định 0 – 5 nghìn đồng, bước 1, không chia cho 0.
     *
     * @param giaTri các giá trị, không âm
     * @return thang trục Y
     */
    public static ThangTrucY tinh(Collection<BigDecimal> giaTri) {
        BigDecimal lonNhat = giaTri.stream().reduce(BigDecimal.ZERO,
                BigDecimal::max);
        boolean trieu = lonNhat.compareTo(MOT_TRIEU) >= 0;
        BigDecimal heSo = trieu ? MOT_TRIEU : MOT_NGHIN;
        String donVi = trieu ? TRIEU : NGHIN;
        if (lonNhat.signum() <= 0) {
            return new ThangTrucY(donVi, heSo, BigDecimal.valueOf(SO_KHOANG),
                    BigDecimal.ONE);
        }
        BigDecimal m = lonNhat.divide(heSo, 6, RoundingMode.HALF_UP);
        BigDecimal buoc = mocTronToiThieu(m.divide(
                BigDecimal.valueOf(SO_KHOANG), 6, RoundingMode.CEILING));
        BigDecimal tren = m.divide(buoc, 0, RoundingMode.CEILING)
                .multiply(buoc);
        // Chua cho cho nhan so tren dau cot cao nhat (it nhat 0,3 buoc).
        if (tren.subtract(m).compareTo(buoc.multiply(KHOANG_TREN)) < 0) {
            tren = tren.add(buoc);
        }
        return new ThangTrucY(donVi, heSo, tren.stripTrailingZeros(),
                buoc.stripTrailingZeros());
    }

    /** Mốc tròn (1, 2, 2,5, 5 × 10^n) nhỏ nhất không nhỏ hơn x (x &gt; 0). */
    static BigDecimal mocTronToiThieu(BigDecimal x) {
        // 10^n nho nhat ma 10^n * 5 >= x, roi thu tung moc tu nho den lon.
        BigDecimal luyThua = BigDecimal.ONE.scaleByPowerOfTen(
                x.precision() - x.scale() - 1);
        for (int lan = 0; lan < 2; lan++) {
            for (BigDecimal moc : MOC) {
                BigDecimal ung = moc.multiply(luyThua);
                if (ung.compareTo(x) >= 0) {
                    return ung;
                }
            }
            luyThua = luyThua.scaleByPowerOfTen(1);
        }
        throw new IllegalStateException("Khong tim duoc moc tron cho " + x);
    }

    /**
     * Đổi số tiền (đồng) sang đơn vị của thang.
     *
     * @param soTien số tiền, đồng
     * @return giá trị theo đơn vị
     */
    public double doi(BigDecimal soTien) {
        return soTien.divide(heSo, 6, RoundingMode.HALF_UP).doubleValue();
    }
}
