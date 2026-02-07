package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateRequestDTO {
    @NotBlank
    String categoryName;
    UUID parentId;
}
