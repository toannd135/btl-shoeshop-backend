package vn.edu.ptit.shoe_shop.dto.response;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class OrderItemResponse {
    private String productName;
    private BigDecimal size;
    private Integer quantity;
    private BigDecimal price;
}