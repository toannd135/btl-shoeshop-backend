package vn.edu.ptit.shoe_shop.dto.request.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleSearchRequestDTO {
    private String name;
    private String code;
    private String status;
    private String description;
    private String createdAt;
    private String updatedAt;
}
