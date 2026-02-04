package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponseDTO {
    Integer CategoryId;
    String CategoryName;
    Integer ParentId;
    Integer   productCount;
    List<CateProResponseDTO> products;
}
