package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;

@Getter
public class PermissionCreateRequestDTO {
    private String name;
    private String apiPath;
    private String method;
    private String module;
}
