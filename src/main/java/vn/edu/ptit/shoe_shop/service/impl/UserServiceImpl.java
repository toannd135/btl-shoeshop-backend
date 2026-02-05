package vn.edu.ptit.shoe_shop.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.ptit.shoe_shop.dto.mapper.UserMapper;
import vn.edu.ptit.shoe_shop.dto.request.UserRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = this.userMapper.toEntity(userRequestDTO);
        this.userRepository.save(user);
        return this.userMapper.toResponseDTO(user);
    }
}
