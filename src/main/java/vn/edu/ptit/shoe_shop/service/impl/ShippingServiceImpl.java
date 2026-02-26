package vn.edu.ptit.shoe_shop.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import vn.edu.ptit.shoe_shop.dto.request.ShippingFeeRequest;
import vn.edu.ptit.shoe_shop.dto.response.ShippingEstimationResponse;
import vn.edu.ptit.shoe_shop.entity.Cart;
import vn.edu.ptit.shoe_shop.entity.CartItem;
import vn.edu.ptit.shoe_shop.service.ShippingService;

@Service
public class ShippingServiceImpl implements ShippingService {
    // Kho hàng mặc định đặt tại Hà Nội (Mã tỉnh giả định là "01")
    private static final String SHOP_PROVINCE_CODE = "01";

    @Override
    public BigDecimal calculateFee(String provinceCode, Cart cart) {
        BigDecimal shippingFee = BigDecimal.ZERO;
        Integer totalWeight = 0;
        for (CartItem item : cart.getItems()) {
            totalWeight += item.getVariant().getWeightGram();
        }
        // Phí cơ bản theo vùng miền
        if ("001".equalsIgnoreCase(provinceCode) || "059".equalsIgnoreCase(provinceCode)) {
            shippingFee = shippingFee.add(new BigDecimal("20000")); // Nội thành
        } else {
            shippingFee = shippingFee.add(new BigDecimal("35000")); // Ngoại tỉnh
        }

        // Phụ phí nếu giày nặng (ví dụ giày bảo hộ)
        if (totalWeight > 2) {
            shippingFee = shippingFee.add(new BigDecimal("10000"));
        }

        return shippingFee;
    }

    /**
     * Hàm này tích hợp cả tính phí và ước tính thời gian để trả về 1 cục cho FE
     * hiển thị
     */
    public ShippingEstimationResponse estimateShipping(ShippingFeeRequest request) {
        // 1. Tính toán khối lượng áp dụng (So sánh khối lượng thực vs Khối lượng thể
        // tích)
        int appliedWeight = calculateAppliedWeight(
                request.getTotalWeightInGrams(),
                request.getLengthInCm(),
                request.getWidthInCm(),
                request.getHeightInCm());

        // 2. Tính phí ship mô phỏng (Sau này bạn gọi API GHN/GHTK ở đoạn này)
        BigDecimal fee = calculateMockedFee(request.getToProvinceCode(), appliedWeight);

        // 3. Ước tính thời gian giao hàng
        int estimatedDays = estimateDeliveryDays(request.getToProvinceCode());
        Instant deliveryDate = Instant.now().plus(estimatedDays, ChronoUnit.DAYS);

        return ShippingEstimationResponse.builder()
                .shippingFee(fee)
                .estimatedDeliveryDate(deliveryDate)
                .deliveryMessage("Giao hàng tiêu chuẩn (" + estimatedDays + " ngày)")
                .build();
    }

    // --- CÁC HÀM LOGIC NỘI BỘ ---

    // Công thức tính Cân nặng quy đổi chuẩn của Giao Hàng Nhanh / Viettel Post
    // Thể tích = (Dài x Rộng x Cao) / 5000 (Đơn vị tính: Gram)
    private int calculateAppliedWeight(int actualWeight, Integer l, Integer w, Integer h) {
        if (l == null || w == null || h == null) {
            return actualWeight; // Nếu không có kích thước, dùng cân nặng thực
        }
        int volumetricWeight = (l * w * h * 1000) / 5000;
        return Math.max(actualWeight, volumetricWeight); // Lấy cái nào nặng hơn
    }

    private BigDecimal calculateMockedFee(String toProvince, int weightInGrams) {
        BigDecimal baseFee;

        // Cước cơ bản (Dưới 2kg)
        if (SHOP_PROVINCE_CODE.equals(toProvince)) {
            baseFee = new BigDecimal("20000"); // Cùng tỉnh (Nội thành Hà Nội)
        } else if (isNorthernProvince(toProvince)) {
            baseFee = new BigDecimal("30000"); // Nội vùng (Miền Bắc)
        } else {
            baseFee = new BigDecimal("40000"); // Liên vùng (Miền Trung / Nam)
        }

        // Phụ phí vượt cân: Cứ mỗi 500g vượt quá 2kg thì cộng thêm 5.000đ
        if (weightInGrams > 2000) {
            int extraWeight = weightInGrams - 2000;
            int extraSteps = (int) Math.ceil((double) extraWeight / 500);
            baseFee = baseFee.add(new BigDecimal(extraSteps * 5000));
        }

        return baseFee;
    }

    private int estimateDeliveryDays(String toProvince) {
        if (SHOP_PROVINCE_CODE.equals(toProvince)) {
            return 1; // Nội thành: Giao ngày hôm sau
        } else if (isNorthernProvince(toProvince)) {
            return 2; // Miền Bắc: 2 ngày
        } else {
            return 4; // Miền Trung/Nam: 3-4 ngày
        }
    }

    // Giả lập danh sách mã tỉnh Miền Bắc
    private boolean isNorthernProvince(String provinceCode) {
        // Thực tế bạn sẽ lookup trong bảng database `provinces`
        return provinceCode.startsWith("0") || provinceCode.startsWith("1");
    }

}
