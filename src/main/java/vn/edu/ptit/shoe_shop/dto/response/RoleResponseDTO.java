package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoleResponseDTO {
    private String roleId;
    private String name;
    private String code;
    private String description;

    private List<PermissionRoleRequestDTO> permissions;

    @Getter
    @Setter
    public static class PermissionRoleRequestDTO {
        private String name;
    }

}
