package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariantImageResponseDTO {
    UUID productId;

    UUID productVariantId;

    UUID imageId;

    String imageURL;

    Boolean isPrimary;

    StatusEnum status;

    Instant createdAt;
    Instant updatedAt;
}
