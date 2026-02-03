package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;

public interface PermissionService {
    public PermissionResponseDTO createPermission(PermissionCreateRequestDTO permissionCreateRequestDTO);
}
