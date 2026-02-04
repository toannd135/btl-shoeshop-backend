package vn.edu.ptit.shoe_shop.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequestDTO {
    String CategoryName;
    Integer ParentId;

    Set<Integer> AddProductIds;
    Set<Integer> RemoveProductIds;
}
