package vn.edu.ptit.shoe_shop.service;

import java.util.UUID;

import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.RoleUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;

public interface RoleService {
    RoleResponseDTO createRole(RoleCreateRequestDTO roleCreateRequestDTO);
    RoleResponseDTO updateRole(RoleUpdateRequestDTO roleUpdateRequestDTO, UUID id);
    RoleResponseDTO fetchRole(UUID id);
    void deleteRole(UUID id);
}
