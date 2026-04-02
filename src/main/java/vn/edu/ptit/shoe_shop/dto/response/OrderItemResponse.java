package vn.edu.ptit.shoe_shop.dto.response;
import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class OrderItemResponse {
    private UUID productId;
    private String productName;
    private BigDecimal size;
    private Integer quantity;
    private BigDecimal price;
    private String imageUrl;
}