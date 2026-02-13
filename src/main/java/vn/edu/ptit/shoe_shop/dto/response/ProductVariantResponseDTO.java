package vn.edu.ptit.shoe_shop.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;

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
    Double size;
    Integer quantity;
    Integer basePrice;
    StatusEnum status;

    Instant createdAt;
    Instant updatedAt;
}
