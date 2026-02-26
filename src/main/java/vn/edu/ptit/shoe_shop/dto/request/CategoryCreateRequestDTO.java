package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateRequestDTO {
    @NotBlank
    String categoryName;
    UUID parentId;
    StatusEnum status;
}
