package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleResponseDTO {
    private String roleId;
    private String name;
    private String code;
    private String status;
    private String createdAt;
    private String updatedAt;

    private List<PermissionRoleRequestDTO> permissions;

    @Getter
    @Setter
    public static class PermissionRoleRequestDTO {
        private String name;
    }

}
