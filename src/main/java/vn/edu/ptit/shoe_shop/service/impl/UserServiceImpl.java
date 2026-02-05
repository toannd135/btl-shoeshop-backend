package vn.edu.ptit.shoe_shop.service.impl;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.constant.RoleConstants;
import vn.edu.ptit.shoe_shop.dto.mapper.UserMapper;
import vn.edu.ptit.shoe_shop.dto.request.UserCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UserUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.repository.RoleRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.UserService;


import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO) {
        if(userCreateRequestDTO.getUsername() != null && this.userRepository.existsByUsername(userCreateRequestDTO.getUsername())){
            throw new DataIntegrityViolationException("Username already exists");
        }
        if(userCreateRequestDTO.getEmail() != null && this.userRepository.existsByEmail(userCreateRequestDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        User user = this.userMapper.toEntity(userCreateRequestDTO);
        // kiem tra role
        if(userCreateRequestDTO.getRole() != null && userCreateRequestDTO.getRole().getId() != null){
            Role role = this.roleRepository.findByRoleId(UUID.fromString(userCreateRequestDTO.getRole().getId()))
                    .orElseThrow(() -> new IdInvalidException("Role not found"));
            if (!role.getCode().equals(RoleConstants.ROLE_ADMIN)){
                throw new IllegalStateException("role not access");
            }
            user.setRole(role);
        }
        //ma hoa passwd

        this.userRepository.save(user);
        UserResponseDTO res = this.userMapper.toResponseDTO(user);
        res.setFullName(user.getFirstName() +  " " + user.getLastName());
        return res;
    }

    @Override
    public UserResponseDTO updateUser(UserUpdateRequestDTO userUpdateRequestDTO, UUID id) {
        User user = this.userRepository.findByUserId(id)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        this.userMapper.updateUserFromDto(userUpdateRequestDTO, user);
        return this.userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUser(UUID id) {
        User user = this.userRepository.findByUserId(id)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        return this.userMapper.toResponseDTO(user);
    }

    @Override
    public UserResponseDTO deleteUser(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteUser'");
    }
}
