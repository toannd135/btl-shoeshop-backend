package vn.edu.ptit.shoe_shop.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.RoleUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.search.RoleSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.search.UserSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.RolePageResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.UserPageResponseDTO;

public interface RoleService {
    RoleResponseDTO createRole(RoleCreateRequestDTO roleCreateRequestDTO);
    RoleResponseDTO updateRole(RoleUpdateRequestDTO roleUpdateRequestDTO, UUID id);
    RoleResponseDTO fetchRole(UUID id);
    void deleteRole(UUID id);
    RolePageResponseDTO searchRoles(RoleSearchRequestDTO request, Pageable pageable);
}
