package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;

@Getter
public class PermissionUpdateRequestDTO {
    private String name;
    private String module;
    private String status;
}
