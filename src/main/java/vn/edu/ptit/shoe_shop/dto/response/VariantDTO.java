package vn.edu.ptit.shoe_shop.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VariantDTO {
    private UUID productVariantId;
    private String sku;
    private BigDecimal size;
    private String color;
    private BigDecimal price;
    private String imageUrl;
}
