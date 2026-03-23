package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class POItemResponse {
    UUID variantId;
    Integer quantity;
    BigDecimal cost;
}
