package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.constant.GenderEnum;

import java.util.UUID;


@Getter
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

    @NotNull
    @Min(1)
    Integer basePrice;

}
