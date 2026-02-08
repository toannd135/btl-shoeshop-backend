package vn.edu.ptit.shoe_shop.dto.request.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionSearchRequestDTO {
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private String status;
    private String createdAt;
    private String updatedAt;
}
