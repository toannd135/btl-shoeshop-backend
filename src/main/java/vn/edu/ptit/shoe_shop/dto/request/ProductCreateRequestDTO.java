package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ProductCreateRequestDTO {
    @NotBlank
    String name;

    @NotNull
    String brand;

    @NotNull
    String description;

    UUID categoryId;

    @NotNull
    GenderEnum gender;

    MultipartFile image;

    ProductStatusEnum status;
}
