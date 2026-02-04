package vn.edu.ptit.shoe_shop.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantResponseDTO {
    Integer productId;
    Integer productVariantId;
    String color;
    Double size;
    Integer price;
    String image;
    Integer stockQuantity;
}
