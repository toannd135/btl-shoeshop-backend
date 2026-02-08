package vn.edu.ptit.shoe_shop.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.search.UserSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.UserPageResponseDTO;
import vn.edu.ptit.shoe_shop.mapper.UserMapper;
import vn.edu.ptit.shoe_shop.dto.request.UserCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UserUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.repository.RoleRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepositoryCustom;
import vn.edu.ptit.shoe_shop.service.UserService;


import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           RoleRepository roleRepository, UserRepositoryCustom userRepositoryCustom) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.userRepositoryCustom = userRepositoryCustom;
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateRequestDTO userCreateRequestDTO) {
        log.debug("Start createUser with username: {}, email: {}",
                userCreateRequestDTO.getUsername(), userCreateRequestDTO.getEmail());
        if(userCreateRequestDTO.getUsername() != null && this.userRepository.existsByUsername(userCreateRequestDTO.getUsername())){
            log.warn("Username {} already exists", userCreateRequestDTO.getUsername());
            throw new DataIntegrityViolationException("Username already exists");
        }
        if(userCreateRequestDTO.getEmail() != null && this.userRepository.existsByEmail(userCreateRequestDTO.getEmail())) {
            log.warn("Email {} already exists", userCreateRequestDTO.getEmail());
            throw new DataIntegrityViolationException("Email already exists");
        }
        User user = this.userMapper.toEntity(userCreateRequestDTO);
        log.debug("Mapped User entity: {}", user);
        // kiem tra role
        if(userCreateRequestDTO.getRole() != null && userCreateRequestDTO.getRole().getId() != null){
            log.debug("Fetching Role with ID: {}", userCreateRequestDTO.getRole().getId());
            Role role = this.roleRepository.findByRoleId(userCreateRequestDTO.getRole().getId())
                    .orElseThrow(() -> {
                        log.debug("Role not found with ID: {}", userCreateRequestDTO.getRole().getId());
                        return new IdInvalidException("Role not found");
                    });
            user.setRole(role);
            log.debug("Role set for user. Role name: {}", role.getName());
        }
        //ma hoa passwd

        this.userRepository.save(user);
        log.info("User created successfully with ID: {}", user.getUserId());
        UserResponseDTO res = this.userMapper.toResponseDTO(user);
        res.setFullName(user.getFirstName() +  " " + user.getLastName());
        log.debug("End createUser with response: {}", res);
        return res;
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(UserUpdateRequestDTO userUpdateRequestDTO, UUID id) {
        log.debug("Start updateUser with ID: {}, data: {}", id, userUpdateRequestDTO);
        User user = this.userRepository.findByUserId(id)
                .orElseThrow(() -> {
                    log.debug("User not found with ID: {}", id);
                    return new IdInvalidException("User not found");
                });
        this.userMapper.updateUserFromDto(userUpdateRequestDTO, user);
        log.debug("Mapped User entity for update: {}", user);
        this.userRepository.save(user);
        log.info("User updated successfully with ID: {}", user.getUserId());
        UserResponseDTO res = this.userMapper.toResponseDTO(user);
        log.debug("End updateUser with response: {}", res);
        return res;
    }

    @Override
    public UserResponseDTO fetchUser(UUID id) {
        log.debug("Start fetchUser with ID: {}", id);
        User user = this.userRepository.findByUserId(id)
                .orElseThrow(() -> {
                    log.debug("User not found with ID: {}", id);
                    return new IdInvalidException("User not found");
                });
        UserResponseDTO res = this.userMapper.toResponseDTO(user);
        log.debug("End fetchUser with response: {}", res);
        return res;
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.debug("Start deleteUser with ID: {}", id);
        User user = this.userRepository.findByUserId(id)
                .orElseThrow(() -> {
                    log.debug("User not found with ID: {}", id);
                    return new IdInvalidException("User not found");
                });
        user.setStatus(StatusEnum.DELETED);
        log.debug("User set status {} for user", user.getStatus());
        this.userRepository.save(user);
        log.debug("User deleted successfully with ID: {}", user.getUserId());
    }

    @Override
    public UserPageResponseDTO search(UserSearchRequestDTO request, Pageable pageable) {
        log.debug("START searchUsers. Criteria: {}, Page: {}, Size: {}, Sort: {}",
                request, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Long startQuery = System.currentTimeMillis();

        Page<User> userPage = this.userRepositoryCustom.searchUsers(request, pageable);

        Long  queryTime = System.currentTimeMillis() - startQuery;
        log.debug("User query executed in {} ms. Total results: {}", queryTime, userPage.getTotalElements());

        List<UserResponseDTO> userResponseDTOS = userPage.getContent()
                .stream().map(user -> {
                    UserResponseDTO userResponseDTO = this.userMapper.toResponseDTO(user);
                    userResponseDTO.setFullName(user.getFirstName() + " " + user.getLastName());
                    return userResponseDTO;
                }).toList();
        UserPageResponseDTO res = new UserPageResponseDTO();
        res.setUsers(userResponseDTOS);
        res.setPage(userPage.getNumber() + 1);
        res.setPageSize(userPage.getSize());
        res.setPages(userPage.getTotalPages());
        res.setTotal(userPage.getTotalElements());

        Long totalTime = System.currentTimeMillis() - startQuery;
        log.info("END searchUsers. Found {} users in {} ms. Page {}/{}", userPage.getTotalElements(), totalTime,
                userPage.getNumber() + 1, userPage.getTotalPages());

        return res;
    }


}
