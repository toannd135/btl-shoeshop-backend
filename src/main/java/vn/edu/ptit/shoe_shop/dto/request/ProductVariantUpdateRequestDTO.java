package vn.edu.ptit.shoe_shop.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantUpdateRequestDTO {
    String color;
    Double size;
    Integer price;
    String image;
    Integer stockQuantity;
}
