package vn.edu.ptit.shoe_shop.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleUpdateRequestDTO {
    private String status;

    private List<RolePermissionUpdateRequestDTO> permissions;

    @Getter
    public static class RolePermissionUpdateRequestDTO {
        private UUID id;
    }
}
