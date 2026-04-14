package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.ptit.shoe_shop.dto.request.UpdateInfoUserRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.auth.PasswordChangeRequestDTO;
import vn.edu.ptit.shoe_shop.service.UserService;

@RestController
@RequiredArgsConstructor
public class InfoController {

    private final UserService userService;

    @PutMapping("/api/v2/users/me/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequestDTO request) {
        return ResponseEntity.ok().body(this.userService.changePassword(request));
    }

    @PutMapping("/api/v2/users/me")
    public ResponseEntity<?> updateInfoUser(@Valid @RequestBody UpdateInfoUserRequestDTO request) {
        return ResponseEntity.ok().body(this.userService.updateInfoUser(request));
    }
}
