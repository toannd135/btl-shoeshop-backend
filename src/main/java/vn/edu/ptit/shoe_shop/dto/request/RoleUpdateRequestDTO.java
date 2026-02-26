package vn.edu.ptit.shoe_shop.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class RoleUpdateRequestDTO {
    private String description;
    private String status;

    private List<RolePermissionUpdateRequestDTO> permissions;

    @Getter
    public static class RolePermissionUpdateRequestDTO {
        private UUID id;
    }
}
