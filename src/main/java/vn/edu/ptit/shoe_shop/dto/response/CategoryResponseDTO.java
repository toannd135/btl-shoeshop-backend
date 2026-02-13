package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponseDTO {
    UUID categoryId;
    String categoryName;
    UUID parentId;

    StatusEnum status;
    Instant createdAt;
    Instant updatedAt;
}
