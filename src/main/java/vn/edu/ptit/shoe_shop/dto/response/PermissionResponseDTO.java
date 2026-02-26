package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionResponseDTO {
    private String permissionId;
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private String status;
    private String createdAt;
    private String updatedAt;
}
