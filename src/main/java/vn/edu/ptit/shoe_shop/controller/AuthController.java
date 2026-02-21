package vn.edu.ptit.shoe_shop.controller;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.common.constant.TokenConstants;
import vn.edu.ptit.shoe_shop.common.exception.BadCredentialsException;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;
import vn.edu.ptit.shoe_shop.dto.LoginResult;
import vn.edu.ptit.shoe_shop.dto.request.auth.LoginRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.auth.LoginResponseDTO;
import vn.edu.ptit.shoe_shop.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    @PostMapping("/login")
    @ApiMessage("Login successful")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO,
            @RequestHeader(value = "X-device-Id", required = false) String deviceId) {

        String finalDeviceId = (deviceId != null) ? deviceId : "unknown-device";

        LoginResult res = this.authService.login(loginRequestDTO, finalDeviceId);
        LoginResponseDTO.UserLoginResponseDTO user = new LoginResponseDTO.UserLoginResponseDTO();
        user.setUserId(res.getUser().getUserId());
        user.setUsername(res.getUser().getUsername());
        user.setFullName(res.getUser().getFullName());
        user.setRoleCode(res.getUser().getRoleCode());

        ResponseCookie refreshTokenCookie = ResponseCookie.from(TokenConstants.REFRESH_TOKEN, res.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Math.toIntExact(refreshTokenExpiration))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new LoginResponseDTO(res.getAccessToken(), user));
    }

    @PostMapping("/refresh-token")
    @ApiMessage("Refresh token successful")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @CookieValue(TokenConstants.REFRESH_TOKEN) String refreshToken) {
        if (refreshToken.equals(TokenConstants.FAKE_TOKEN)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        LoginResult res = this.authService.getNewToken(refreshToken);

        LoginResponseDTO.UserLoginResponseDTO user = new LoginResponseDTO.UserLoginResponseDTO();
        user.setUserId(res.getUser().getUserId());
        user.setUsername(res.getUser().getUsername());
        user.setFullName(res.getUser().getFullName());
        user.setRoleCode(res.getUser().getRoleCode());

        ResponseCookie refreshTokenCookie = ResponseCookie.from(TokenConstants.REFRESH_TOKEN, res.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Math.toIntExact(refreshTokenExpiration))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new LoginResponseDTO(res.getAccessToken(), user));
    }

//    @PostMapping("/logout")
//    @ApiMessage("Logout successful")
//    public ResponseEntity<Void> logout(
//            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
//            @CookieValue(TokenConstants.REFRESH_TOKEN) String refreshToken
//    ) {
//        String accessToken = null;
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            accessToken = bearerToken.substring(7);
//        }
//        if (refreshToken.equals(TokenConstants.FAKE_TOKEN)) {
//            throw new BadCredentialsException("Invalid refresh token");
//        }
//        this.authService.logout(refreshToken, accessToken);
//        ResponseCookie deleteCookie = ResponseCookie.from(TokenConstants.REFRESH_TOKEN, "")
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(0)
//                .sameSite("Lax")
//                .build();
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
//                .build();
//    }
}
