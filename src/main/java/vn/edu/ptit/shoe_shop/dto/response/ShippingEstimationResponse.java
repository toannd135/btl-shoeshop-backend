package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Setter
@Getter
@Builder
public class ShippingEstimationResponse {
    private BigDecimal shippingFee;        // Phí vận chuyển
    private Instant estimatedDeliveryDate; // Thời gian giao hàng dự kiến
    private String deliveryMessage;        // Lời nhắn (Ví dụ: "Giao tiêu chuẩn (2-3 ngày)")
}