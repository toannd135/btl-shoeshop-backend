package vn.edu.ptit.shoe_shop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import vn.edu.ptit.shoe_shop.dto.request.UserCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UserUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "dateOfBirth", source = "dateOfBirth", dateFormat = "dd-MM-yyyy")
    @Mapping(target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UserResponseDTO toResponseDTO(User user);

    UserResponseDTO.UserRoleResponseDTO mapRoleToRoleResponse(Role role);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "normalizeName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "normalizeName")
    @Mapping(target = "username", source = "username", qualifiedByName = "normalizeUsername")
    @Mapping(target = "phone", source = "phone", qualifiedByName = "normalizePhone")
    void updateUserFromDto(UserUpdateRequestDTO userUpdateRequestDTO, @MappingTarget User user);

    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "normalizeName")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "normalizeName")
    @Mapping(target = "username", source = "username", qualifiedByName = "normalizeUsername")
    @Mapping(target = "email", source = "email", qualifiedByName = "normalizeEmail")
    @Mapping(target = "phone", source = "phone", qualifiedByName = "normalizePhone")
    User toEntity(UserCreateRequestDTO dto);

    @Named("normalizeName")
    default String normalizeName(String name) {
        if (name == null) return null;
        String[] words = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1).toLowerCase())
                    .append(" ");
        }
        return sb.toString().trim();
    }

    @Named("normalizeUsername")
    default String normalizeUsername(String username) {
        return username == null ? null : username.trim().toLowerCase();
    }

    @Named("normalizeEmail")
    default String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    @Named("normalizePhone")
    default String normalizePhone(String phone) {
        return phone == null ? null : phone.replaceAll("\\D", "");
    }
}
