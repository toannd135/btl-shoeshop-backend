package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.UserCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UserUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;

import java.util.UUID;

public interface UserService {
    public UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO);
    public UserResponseDTO updateUser(UserUpdateRequestDTO userUpdateRequestDTO, UUID id);
    public void deleteUser(UUID id);
    public UserResponseDTO fetchUser(UUID id);
}
