package vn.edu.ptit.shoe_shop.dto.mapper;

import org.mapstruct.Mapper;
import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleCreateRequestDTO roleRequest);
    RoleResponseDTO toResponseDTO(Role role);
}
