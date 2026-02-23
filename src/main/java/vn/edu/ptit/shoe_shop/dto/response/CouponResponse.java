package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.constant.enums.DiscountTypeEnum;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CouponResponse {
    private UUID couponId;
    private String code;
    private String name;
    private DiscountTypeEnum discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private Instant startsAt;
    private Instant expiresAt;
    private String status;
    private int timesUsed; // Phục vụ "Theo dõi lượt sử dụng"
}