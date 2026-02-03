package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;

import java.time.Instant;

@Getter
public class PermissionCreateRequestDTO {
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private Instant createdAt;
    private Instant updatedAt;

}
