package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequestDTO {
    String name;

    String brand;

    String description;

    UUID categoryId;

    GenderEnum gender;

    StatusEnum status;
}
