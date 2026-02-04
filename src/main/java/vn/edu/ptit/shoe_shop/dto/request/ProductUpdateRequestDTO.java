package vn.edu.ptit.shoe_shop.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequestDTO {
    String title;
    String description;
    Integer categoryId;
    String primaryImage;
    List<String> images;
}
