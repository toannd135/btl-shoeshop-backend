package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.ProductStatusEnum;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponseDTO {
    UUID productId;
    UUID productVariantId;
    String sku;
    String color;
    BigDecimal size;
    Integer quantity;
    BigDecimal basePrice;
    ProductStatusEnum status;

    Instant createdAt;
    Instant updatedAt;
}
