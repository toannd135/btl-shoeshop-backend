package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;

public interface RoleService {
    RoleResponseDTO createRole(RoleCreateRequestDTO roleCreateRequestDTO);
}
