package vn.edu.ptit.shoe_shop.dto.request;


import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantUpdateRequestDTO {
    String color;
    String sku;
    Double size;
    Integer quantity;
    @Min(1)
    Integer basePrice;
    StatusEnum status;
}
