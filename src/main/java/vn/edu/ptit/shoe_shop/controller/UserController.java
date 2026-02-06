package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.ptit.shoe_shop.dto.request.UserCreateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.UserUpdateRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.UserResponseDTO;
import vn.edu.ptit.shoe_shop.service.UserService;
import vn.edu.ptit.shoe_shop.utils.annotation.ApiMessage;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @ApiMessage("User created successfully")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateRequestDTO userCreateRequestDTO){
        UserResponseDTO res = this.userService.createUser(userCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/users/{id}")
    @ApiMessage("User updated successfully")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody @Valid UserUpdateRequestDTO userUpdateRequestDTO,
                                                      @PathVariable UUID id ) {
        UserResponseDTO res = this.userService.updateUser(userUpdateRequestDTO, id);
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("User deleted successfully")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{id}")
    @ApiMessage("User retrieved successfully")
    public ResponseEntity<UserResponseDTO> fetchUser(@PathVariable UUID id) {
        UserResponseDTO res = this.userService.fetchUser(id);
        return ResponseEntity.ok().body(res);
    }

}