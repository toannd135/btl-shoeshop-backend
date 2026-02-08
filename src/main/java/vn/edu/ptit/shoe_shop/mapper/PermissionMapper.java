package vn.edu.ptit.shoe_shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import vn.edu.ptit.shoe_shop.dto.request.PermissionCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.PermissionUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.PermissionResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(target = "name", source = "name", qualifiedByName = "normalizerPermissionName")
    @Mapping(target = "apiPath", source = "apiPath", qualifiedByName = "normalizerPermissionApiPath")
    @Mapping(target = "method", source = "method", qualifiedByName = "normalizerPermissionMethod")
    @Mapping(target = "module", source = "module", qualifiedByName = "normalizerPermissionModule")
    Permission toEntity(PermissionCreateRequestDTO permission);

    PermissionResponseDTO toResponseDto(Permission permission);


    @Mapping(target = "permissionId", ignore = true)
    @Mapping(target = "apiPath", ignore = true)
    @Mapping(target = "name", source = "name", qualifiedByName = "normalizerPermissionName")
    @Mapping(target = "method", ignore = true)
    @Mapping(target = "module", source = "module", qualifiedByName = "normalizerPermissionModule")
    void updatePermissionEntityToDto(PermissionUpdateRequestDTO permissionUpdateRequestDTO, @MappingTarget Permission permission);

    @Named("normalizerPermissionName")
    default String normalizerPermissionName(String name){
        if(name == null) return null;
        String[] words = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1).toLowerCase())
                    .append(" ");
        }
        return sb.toString().trim();
    }

    @Named("normalizerPermissionApiPath")
    default String normalizerPermissionApiPath(String apiPath){
        if (apiPath == null) return null;
        String path = apiPath.trim().toLowerCase();
        path = path.replaceAll("/+", "/");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    @Named("normalizerPermissionMethod")
    default String normalizerPermissionMethod(String method) {
        return method == null ? null : method.toUpperCase();
    }

    @Named("normalizerPermissionModule")
    default String normalizerPermissionModule(String module) {
        if (module == null) return null;
        String result = module.trim().replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
        return result;
    }


}
