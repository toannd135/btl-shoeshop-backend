package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID orderId;
    private Instant orderDate;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;

    // Tiền bạc
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal finalPrice;
    private String status;

    private String note;
    // Chứa thông tin User rút gọn (Không password, không cart)
    private UserShortInfo user;

    // Danh sách sản phẩm rút gọn
    private List<OrderItemResponse> items;
}
