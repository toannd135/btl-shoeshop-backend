package vn.edu.ptit.shoe_shop.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.mapper.UserMapper;
import vn.edu.ptit.shoe_shop.dto.request.UserRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        User user = this.userMapper.toEntity(userRequestDTO);
        this.userRepository.save(user);
        return this.userMapper.toResponseDTO(user);
    }
}
