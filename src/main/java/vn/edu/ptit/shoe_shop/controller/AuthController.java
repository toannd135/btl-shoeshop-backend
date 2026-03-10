package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import vn.edu.ptit.shoe_shop.common.constant.TokenConstants;
import vn.edu.ptit.shoe_shop.common.exception.BadCredentialsException;
import vn.edu.ptit.shoe_shop.common.exception.TokenExpiredOrUsedException;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;
import vn.edu.ptit.shoe_shop.dto.LoginResult;
import vn.edu.ptit.shoe_shop.dto.request.auth.*;
import vn.edu.ptit.shoe_shop.dto.response.ForgotPasswordResponseDTO;
import vn.edu.ptit.shoe_shop.dto.response.auth.LoginResponseDTO;
import vn.edu.ptit.shoe_shop.service.AuthService;
import vn.edu.ptit.shoe_shop.service.UserService;

import java.util.Map;

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
        user.setAvatarImage(res.getUser().getAvatarImage());

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

    @PostMapping("/refresh-token")
    @ApiMessage("Refresh token successful")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken,
            @CookieValue(value = TokenConstants.REFRESH_TOKEN, required = false) String refreshToken) {

//        if (refreshToken.equals(TokenConstants.FAKE_TOKEN)) {
//            throw new BadCredentialsException("Invalid refresh token");
//        }
        // if (refreshToken == null) {
        // throw new BadCredentialsException("Refresh token missing");
        // }
        String accessToken = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            accessToken = bearerToken.substring(7);
        }

        LoginResult res = this.authService.getNewToken(refreshToken, accessToken);

        LoginResponseDTO.UserLoginResponseDTO user = new LoginResponseDTO.UserLoginResponseDTO();
        user.setUserId(res.getUser().getUserId());
        user.setUsername(res.getUser().getUsername());
        user.setFullName(res.getUser().getFullName());
        user.setRoleCode(res.getUser().getRoleCode());
        user.setAvatarImage(res.getUser().getAvatarImage());

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
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String bearerToken,
            @CookieValue(value = TokenConstants.REFRESH_TOKEN, required = false) String refreshToken) {

        if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing or invalid Authorization header");
        }
        String accessToken = bearerToken.substring(7);
//        if (refreshToken.equals(TokenConstants.FAKE_TOKEN)) {
//            throw new BadCredentialsException("Invalid refresh token");
//        }
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
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        String newUser = this.userService.register(registerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Registration successful",
                "user", newUser));
    }

    @GetMapping("/verify")
    public ModelAndView verifyAccount(@RequestParam("token") String token) {
        try {
            userService.verifyUser(token);
            return new ModelAndView("registerConfirmationSuscessfully");
        } catch (TokenExpiredOrUsedException e) {
            ModelAndView mav = new ModelAndView("registerConfirmationSuscessfully");
            mav.addObject("message", "Tài khoản của bạn đã được xác thực trước đó.");
            return mav;
        } catch (Exception e) {
            return new ModelAndView("registerConfirmationFail");
        }
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok(principal.getAttributes());
    }

    @PostMapping("/forgot-password")
    @ApiMessage("Request password reset")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        ForgotPasswordResponseDTO  response = this.authService.forgotPassword(forgotPasswordRequestDTO);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/verify-otp")
    @ApiMessage("OTP verification")
    public ResponseEntity<?> otpVerification(@Valid @RequestBody OtpVerificationRequestDTO request) {
        String response = this.authService.otpVerification(request.getOtp(), request.getEmail());
        return ResponseEntity.ok().body(Map.of("message", response));

    }

    @PostMapping("/reset-password")
    @ApiMessage("Reset password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        String response = this.authService.resetPassword(request);
        return ResponseEntity.ok().body(Map.of("message", response));
    }

}
