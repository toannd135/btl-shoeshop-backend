package vn.edu.ptit.shoe_shop.dto.mapper;

import org.mapstruct.Mapper;
import vn.edu.ptit.shoe_shop.dto.request.UserRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequestDTO user);

    UserResponseDTO toResponseDTO(User user);
}
