package vn.edu.ptit.shoe_shop.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantUpdateRequestDTO {
    String color;
    String sku;
    Double size;
    Integer quantity;
    StatusEnum status;
}
