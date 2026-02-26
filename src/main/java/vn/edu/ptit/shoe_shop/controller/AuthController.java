package vn.edu.ptit.shoe_shop.controller;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.common.constant.TokenConstants;
import vn.edu.ptit.shoe_shop.common.exception.BadCredentialsException;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;
import vn.edu.ptit.shoe_shop.dto.LoginResult;
import vn.edu.ptit.shoe_shop.dto.request.auth.LoginRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.auth.RegisterRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.auth.LoginResponseDTO;
import vn.edu.ptit.shoe_shop.service.AuthService;
import vn.edu.ptit.shoe_shop.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
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
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @CookieValue(TokenConstants.REFRESH_TOKEN) String refreshToken) {

        if (refreshToken.equals(TokenConstants.FAKE_TOKEN)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if(!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")){
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }
        String accessToken = bearerToken.substring(7);
        if (refreshToken.equals(TokenConstants.FAKE_TOKEN)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        LoginResult res = this.authService.getNewToken(refreshToken, accessToken);

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
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(new LoginResponseDTO(res.getAccessToken(), user));
    }

    @PostMapping("/logout")
    @ApiMessage("Logout successful")
    public ResponseEntity<Void> logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @CookieValue(TokenConstants.REFRESH_TOKEN) String refreshToken) {

        if(!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")){
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }
        String accessToken = bearerToken.substring(7);
        if (refreshToken.equals(TokenConstants.FAKE_TOKEN)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        this.authService.logout(refreshToken, accessToken);
        ResponseCookie deleteCookie = ResponseCookie.from(TokenConstants.REFRESH_TOKEN, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @PostMapping("/register")
    @ApiMessage("Register successful")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO){
        String newUser = this.userService.register(registerRequestDTO);
        return ResponseEntity.ok().body("ok");
    }

}
