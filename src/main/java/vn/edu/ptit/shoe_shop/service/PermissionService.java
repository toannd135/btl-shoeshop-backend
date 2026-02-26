package vn.edu.ptit.shoe_shop.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PermissionUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.search.PermissionSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.PermissionPageResponseDTO;

public interface PermissionService {
    PermissionResponseDTO createPermission(PermissionCreateRequestDTO permissionCreateRequestDTO);
    PermissionResponseDTO updatePermission(PermissionUpdateRequestDTO permissionRequestDTO, UUID id);
    void  deletePermission(UUID id);
    PermissionResponseDTO fetchPermission(UUID id);
    PermissionPageResponseDTO searchPermissions(PermissionSearchRequestDTO request, Pageable pageable);
}
