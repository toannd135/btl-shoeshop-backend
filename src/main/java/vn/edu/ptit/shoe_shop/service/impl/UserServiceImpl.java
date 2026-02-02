package vn.edu.ptit.shoe_shop.service.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.mapper.UserMapper;
import vn.edu.ptit.shoe_shop.dto.request.UserCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UserUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.UserService;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO) {
        if(userCreateRequestDTO.getEmail() != null && this.userRepository.existsByEmail(userCreateRequestDTO.getName())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        // kiem tra role
        User user = this.userMapper.toEntity(userCreateRequestDTO);
        //ma hoa passwd
        this.userRepository.save(user);
        return this.userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(UserUpdateRequestDTO userUpdateRequestDTO, UUID id) {
        User user = this.userRepository.findById(id).orElseThrow(() -> new IdInvalidException("User not found"));
        this.userMapper.updateUserFromDto(userUpdateRequestDTO, user);
        return this.userMapper.toResponseDTO(user);
    }
}
