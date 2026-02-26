package vn.edu.ptit.shoe_shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import vn.edu.ptit.shoe_shop.dto.request.RoleCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.RoleUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.RoleResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "name", source = "name", qualifiedByName = "normalizerRoleName")
    @Mapping(target = "code", source = "code", qualifiedByName = "normalizerRoleCode")
    Role toEntity(RoleCreateRequestDTO roleRequest);

    @Mapping(target = "name", source = "name", qualifiedByName = "normalizerRoleName")
    @Mapping(target = "code", source = "code", qualifiedByName = "normalizerRoleCode")
    RoleResponseDTO toResponseDTO(Role role);

    void updateRoleEntityToDto(RoleUpdateRequestDTO roleUpdateRequestDTO, @MappingTarget Role role);

    @Named("normalizerRoleName")
    default String normalizeNameRole(String roleName) {
        return roleName == null ? null : roleName.trim().toUpperCase();
    }

    @Named("normalizerRoleCode")
    default String normalizerRoleCode(String roleCode) {
        return roleCode == null ? null : roleCode.trim().replaceAll(" ", "_").toUpperCase();
    }
}