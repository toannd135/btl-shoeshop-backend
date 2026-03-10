package vn.edu.ptit.shoe_shop.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.common.constant.EmailPattern;
import vn.edu.ptit.shoe_shop.common.constant.RedisKeyConstants;
import vn.edu.ptit.shoe_shop.common.enums.ProviderEnum;
import vn.edu.ptit.shoe_shop.common.enums.RoleEnum;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.TokenExpiredOrUsedException;
import vn.edu.ptit.shoe_shop.dto.request.auth.RegisterRequestDTO;
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
import vn.edu.ptit.shoe_shop.service.CartService;
import vn.edu.ptit.shoe_shop.service.EmailService;
import vn.edu.ptit.shoe_shop.service.RedisService;
import vn.edu.ptit.shoe_shop.service.UserService;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static vn.edu.ptit.shoe_shop.entity.QUser.user;


@Service
public class UserServiceImpl implements UserService {
    private final CartService cartService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final EmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, RedisService redisService,
                           RoleRepository roleRepository, UserRepositoryCustom userRepositoryCustom,
                           EmailService emailService, PasswordEncoder passwordEncoder,CartService cartService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.redisService = redisService;
        this.roleRepository = roleRepository;
        this.userRepositoryCustom = userRepositoryCustom;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService;
        this.emailService = emailService;
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
        // ma hoa password
        String passwordHashed = this.passwordEncoder.encode(userCreateRequestDTO.getPassword());
        user.setPassword(passwordHashed);
        log.debug("Password hashed for user");
        this.userRepository.save(user);
        log.info("User created successfully with ID: {}", user.getUserId());


        // luu vao cache


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
        Role role = this.roleRepository.findByRoleId(userUpdateRequestDTO.getRole().getId())
                .orElseThrow(() -> {
                    log.debug("Role not found with ID: {}", userUpdateRequestDTO.getRole().getId());
                    return new IdInvalidException("Role not found");
                });
        user.setRole(role);
        log.debug("Role set for user. Role name: {}", role.getName());
        this.userRepository.save(user);
        log.info("User updated successfully with ID: {}", user.getUserId());
        UserResponseDTO res = this.userMapper.toResponseDTO(user);
        res.setFullName(user.getFirstName() +  " " + user.getLastName());
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
        res.setFullName(user.getFirstName() +  " " + user.getLastName());
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
        if(user.getRole().getName().equals(RoleEnum.ADMIN.name())) {
            throw new IllegalStateException("cannot delete admin");
        }
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

    @Override
    public User getUserByUsernameOrEmail(String username) {
        if(username == null || username.isEmpty()) {
            log.debug("Username is null or empty");
            throw new IdInvalidException("Username cannot be null or empty");
        }
        if(EmailPattern.EMAIL_PATTERN.matcher(username).matches()) {
            return this.userRepository.findByEmail(username).orElseThrow(() -> {
                log.debug("User not found with email: {}", username);
                return new IdInvalidException("User not found");
            });
        }
        else {
            return this.userRepository.findByUsername(username).orElseThrow(() -> {
                log.debug("User not found with username: {}", username);
                return new IdInvalidException("User not found");
            });
        }
    }

    @Override
    @Transactional
    public String register(RegisterRequestDTO registerRequestDTO) {
        // kiem tra redis
        if(Boolean.TRUE.equals(this.redisService.isExistsInSet(
                RedisKeyConstants.EMAIL_SET, registerRequestDTO.getEmail()))) {
            throw new IllegalStateException("Email already exists");
        }
        if(Boolean.TRUE.equals(this.redisService.isExistsInSet(
                RedisKeyConstants.USERNAME_SET, registerRequestDTO.getUsername()))) {
            throw new IllegalStateException("Username already exists");
        }

        // kiem tra database
        if(this.userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }
        if(this.userRepository.existsByUsername(registerRequestDTO.getUsername())) {
            throw new IllegalStateException("Username already exists");
        }

        User newUser = this.userMapper.registerDTOToUser(registerRequestDTO);
        newUser.setStatus(StatusEnum.INACTIVE);
        newUser.setProvider(ProviderEnum.SERVER);
        newUser.setPassword(this.passwordEncoder.encode(registerRequestDTO.getPassword()));
        Role role = this.roleRepository.findByName(RoleEnum.USER.name())
                .orElseThrow(() -> new IdInvalidException("Role not found"));
        newUser.setRole(role);

        this.userRepository.save(newUser);
        this.cartService.createCart(newUser.getUserId().toString());
        // this.redisTemplate.opsForSet().add(RedisKeyConstants.EMAIL_SET, newUser.getEmail());
        // this.redisTemplate.opsForSet().add(RedisKeyConstants.USERNAME_SET, newUser.getUsername());


        // //gui email
        // return "";

        sendVerificationEmail(newUser);
        this.redisService.addToSet(RedisKeyConstants.EMAIL_SET, newUser.getEmail());
        this.redisService.addToSet(RedisKeyConstants.USERNAME_SET, newUser.getUsername());
        setRandomTTL("users:usernames", 24 * 60);
        setRandomTTL("users:emails", 24 * 60);
        return "Registration successful. Please check your email to verify your account";
    }

    private void setRandomTTL(String key, long baseMinutes) {
        long randomMinutes = ThreadLocalRandom.current().nextLong(120);
        long finalTtl = baseMinutes + randomMinutes;
        this.redisService.expireKey(key, finalTtl);
        log.info("Key [{}] set to expire in {} minutes (~{} hours).", key, finalTtl, String.format("%.1f", finalTtl / 60.0));
    }

    private void sendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        this.redisService.storeVerificationToken(token, user.getUserId(), 600L);

        String verifyUrl = "http://localhost:8080/api/v1/auth/verify?token=" + token;
        Map<String, Object> variables = new HashMap<>();
        variables.put("confirmationLink", verifyUrl);
        this.emailService.sendEmailFromTemplateSync(
                user.getEmail(),
                "Please confirm account.",
                "registerConfirmation",
                variables
        );
    }

    @Override
    @Transactional
    public String verifyUser(String token) {
        String userId = this.redisService.getUserIdFromVerificationToken(token);
        if (userId == null) {
            throw new TokenExpiredOrUsedException("Invalid or expired verification token");
        }
        User user = this.userRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getStatus().equals(StatusEnum.ACTIVE)) {
            throw new TokenExpiredOrUsedException("Token invalid or already used");
        }
        user.setStatus(StatusEnum.ACTIVE);
        this.userRepository.save(user);
        this.redisService.deleteVerificationToken(token);

        return "Account verified successfully";
    }

    
}
