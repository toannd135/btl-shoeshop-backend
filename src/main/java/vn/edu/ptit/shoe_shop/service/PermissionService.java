package vn.edu.ptit.shoe_shop.service;

import java.util.UUID;

import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PermissionUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;

public interface PermissionService {
    public PermissionResponseDTO createPermission(PermissionCreateRequestDTO permissionCreateRequestDTO);
    PermissionResponseDTO updatePermission(PermissionUpdateRequestDTO permissionRequestDTO, UUID id);
    void  deletePermission(UUID id);
    PermissionResponseDTO fetchPermission(UUID id);
}
