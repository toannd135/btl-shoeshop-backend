package vn.edu.ptit.shoe_shop.dto.mapper;

import org.mapstruct.Mapper;
import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toEntity(PermissionCreateRequestDTO permission);

    PermissionResponseDTO toResponseDto(Permission permission);
}
