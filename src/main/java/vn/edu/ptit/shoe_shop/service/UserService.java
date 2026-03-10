package vn.edu.ptit.shoe_shop.service;

import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.request.UserCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UserUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.auth.RegisterRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.search.UserSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.UserPageResponseDTO;
import vn.edu.ptit.shoe_shop.entity.User;

import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO);
    UserResponseDTO updateUser(UserUpdateRequestDTO userUpdateRequestDTO, UUID id);
    void deleteUser(UUID id);
    UserResponseDTO fetchUser(UUID id);
    UserPageResponseDTO search(UserSearchRequestDTO request, Pageable pageable);
    User getUserByUsernameOrEmail(String username);
    String register(RegisterRequestDTO registerRequestDTO);
    String verifyUser(String token);
}
