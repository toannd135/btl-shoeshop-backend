package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.UserRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;

public interface UserService {
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO);
}
