package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDTO {
    Integer productId;
    String title;
    String description;
    Integer categoryId;
    LocalDateTime createdAt;
    List<ProductVariantResponseDTO> productVariants;
}
