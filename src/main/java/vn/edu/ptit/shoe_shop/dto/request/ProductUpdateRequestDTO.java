package vn.edu.ptit.shoe_shop.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;
import vn.edu.ptit.shoe_shop.common.enums.ProductStatusEnum;

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

    MultipartFile image;

    ProductStatusEnum status;
}
