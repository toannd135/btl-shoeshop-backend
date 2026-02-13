package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.constant.GenderEnum;
import vn.edu.ptit.shoe_shop.constant.StatusEnum;

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

    StatusEnum status;
}
