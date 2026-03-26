package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;
import vn.edu.ptit.shoe_shop.common.enums.ProductStatusEnum;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponseDTO {
    UUID productId;
    String name;
    String brand;
    String description;
    UUID categoryId;
    GenderEnum gender;
    String imageUrl;

    ProductStatusEnum status;
    Instant createdAt;
    Instant updatedAt;
}
