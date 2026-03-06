package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.ptit.shoe_shop.dto.request.UserCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UserUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.search.UserSearchRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.page.UserPageResponseDTO;
import vn.edu.ptit.shoe_shop.service.UserService;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @ApiMessage("User created successfully")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateRequestDTO userCreateRequestDTO){
        log.info("START createUser with username: {}, email: {}", userCreateRequestDTO.getUsername(),
                userCreateRequestDTO.getEmail());

        UserResponseDTO res = this.userService.createUser(userCreateRequestDTO);

        log.info("END createUser successfully. User ID: {}, Username: {}", res.getUserId(), res.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/users/{id}")
    @ApiMessage("User updated successfully")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody @Valid UserUpdateRequestDTO userUpdateRequestDTO,
                                                      @PathVariable UUID id ) {
        log.info("START updateUser with ID: {}, update data: {}", id, userUpdateRequestDTO);

        UserResponseDTO res = this.userService.updateUser(userUpdateRequestDTO, id);

        log.info("END updateUser successfully for ID: {}", id);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("User deleted successfully")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("START deleteUser with ID: {}", id);

        this.userService.deleteUser(id);

        log.info("END deleteUser successfully for ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}")
    @ApiMessage("User retrieved successfully")
    public ResponseEntity<UserResponseDTO> fetchUser(@PathVariable UUID id) {
        log.info("START fetchUser with ID: {}", id);

        UserResponseDTO res = this.userService.fetchUser(id);

        log.info("END fetchUser successfully for ID: {}, Username: {}", id, res.getUsername());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/users")
    @ApiMessage("Get users successfully")
    public ResponseEntity<UserPageResponseDTO> searchUsers(@ModelAttribute @Valid UserSearchRequestDTO request,
                                                           Pageable pageable){
        log.info("START searchUsers with criteria: {}, page: {}, size: {}, sort: {}",
                request, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        UserPageResponseDTO userPageResponseDTO = this.userService.search(request, pageable);

        log.info("END searchUsers successfully. Total users: {}, total pages: {}", userPageResponseDTO.getTotal(),
                userPageResponseDTO.getPages());
        return ResponseEntity.ok().body(userPageResponseDTO);
    }

    
}