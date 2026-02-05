package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class RoleCreateRequestDTO {
    private String name;
    private String code;
    private String description;

    private List<PermissionRoleCreateRequestDTO> permissions;

    @Getter
    public static class PermissionRoleCreateRequestDTO {
        private UUID id;
    }
}
