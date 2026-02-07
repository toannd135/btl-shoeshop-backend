package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
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
}
