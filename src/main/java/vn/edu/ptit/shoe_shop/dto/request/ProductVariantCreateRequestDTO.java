package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantCreateRequestDTO {
    @NotNull
    String color;
    @NotNull
    String sku;
    @NotNull
    Double size;
    @NotNull
    Integer quantity;

    @NotNull
    @Min(1)
    Integer basePrice;

    StatusEnum status;

}
