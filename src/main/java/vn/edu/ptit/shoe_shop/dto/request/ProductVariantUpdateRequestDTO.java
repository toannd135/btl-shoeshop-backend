package vn.edu.ptit.shoe_shop.dto.request;


import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantUpdateRequestDTO {
    String color;
    String sku;
    BigDecimal size;
    Integer quantity;
    @Min(1)
    BigDecimal basePrice;
    StatusEnum status;
}
