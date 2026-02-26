package vn.edu.ptit.shoe_shop.dto.request;

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
public class CategoryUpdateRequestDTO {
    String categoryName;
    UUID parentId;
    StatusEnum status;
}
