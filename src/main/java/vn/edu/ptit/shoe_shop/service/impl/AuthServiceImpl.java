package vn.edu.ptit.shoe_shop.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.common.constant.JwtConstants;
import vn.edu.ptit.shoe_shop.common.enums.ProviderEnum;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.BadCredentialsException;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.security.jwt.TokenProvider;
import vn.edu.ptit.shoe_shop.dto.LoginResult;
import vn.edu.ptit.shoe_shop.dto.request.auth.ForgotPasswordRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.auth.LoginRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.auth.ResetPasswordRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ForgotPasswordResponseDTO;
import vn.edu.ptit.shoe_shop.entity.RefreshToken;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.RefreshTokenRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.AuthService;
import vn.edu.ptit.shoe_shop.common.security.service.CustomUserDetail;
import vn.edu.ptit.shoe_shop.service.EmailService;
import vn.edu.ptit.shoe_shop.service.RedisService;
import vn.edu.ptit.shoe_shop.service.UserService;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;


    private final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public AuthServiceImpl(AuthenticationManager authenticationManager, TokenProvider tokenProvider,UserService userService
            , UserRepository userRepository, RedisService redisService, RefreshTokenRepository refreshTokenRepository
            , EmailService emailService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public LoginResult login(LoginRequestDTO request, String deviceId) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // get user info
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        User user = customUserDetail.getUser();
        UUID userId = user.getUserId();
        // create access token
        String accessToken = this.tokenProvider.createAccessToken(authentication, deviceId);
        // create refresh token
        String refreshToken = this.tokenProvider.createRefreshToken(authentication, deviceId);
        String refreshJti = this.tokenProvider.getJtiFromToken(refreshToken);
        // save refresh token into redis, database
        RefreshToken tokenEntity = this.refreshTokenRepository.findByUserUserIdAndDeviceId(userId, deviceId)
                .orElse(new RefreshToken());
        tokenEntity.setToken(refreshToken);
        tokenEntity.setExpiryDate(Instant.now().plusSeconds(refreshTokenExpiration));
        tokenEntity.setDeviceId(deviceId);
        tokenEntity.setRevoked(false);
        tokenEntity.setUser(user);
        this.refreshTokenRepository.save(tokenEntity);
        log.debug("Saved refresh token in Database for userId: {}", userId);
        this.redisService.storeRefreshToken(userId, refreshJti ,deviceId);

        return buildLoginResult(user, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginResult getNewToken(String refreshToken, String accessToken) {
        // decode refresh token
        Jwt decodeToken = this.tokenProvider.checkValidRefreshToken(refreshToken);
        UUID userId = UUID.fromString(decodeToken.getSubject());
        String deviceId = decodeToken.getClaimAsString(JwtConstants.Claims.DEVICE_ID);
        String jtiFromRefreshToken = decodeToken.getId();
        Instant expiresAt = decodeToken.getExpiresAt();
        // check redis

        String jtiInRedis = this.redisService.getRefreshToken(userId, deviceId);
        if(jtiInRedis == null || !jtiInRedis.equals(jtiFromRefreshToken)) {
            throw new BadCredentialsException("login fail");
        }

        User user = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.debug("User not found with ID: {}", userId);
                    return new IdInvalidException("User not found");
                });

        if (accessToken != null && !accessToken.isEmpty()) {
            revokeAccessToken(accessToken);
        }

        // check token db
        RefreshToken refreshTokenEntity = this.refreshTokenRepository.findByUserUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new BadCredentialsException("Refresh token session not found"));

        if (refreshTokenEntity.getRevoked()) {
            log.debug("Refresh token is revoked for userId: {}", userId);
            throw new BadCredentialsException("Refresh token is revoked");
        }
        if (refreshTokenEntity.getExpiryDate().isBefore(Instant.now())) {
            log.debug("Refresh token in DB has expired for userId: {}", userId);
            this.refreshTokenRepository.delete(refreshTokenEntity);
            throw new BadCredentialsException("Refresh token expired");
        }
        // thu hoi access token
        revokeAccessToken(accessToken);

        CustomUserDetail userDetail = new CustomUserDetail(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
        // new access token
        String newAccessToken = this.tokenProvider.createAccessToken(authentication, deviceId); // save into memory frontend

        // new refresh token
        String newRefreshToken = this.tokenProvider.createRefreshToken(authentication, deviceId);
        String newRefreshJti = this.tokenProvider.getJtiFromToken(newRefreshToken);
        // luu refresh token vao database
        refreshTokenEntity.setToken(newRefreshToken);
        refreshTokenEntity.setExpiryDate(Instant.now().plusSeconds(refreshTokenExpiration));
        this.refreshTokenRepository.save(refreshTokenEntity);
        log.debug("Saved refresh token in Database for userId: {}", userId);
        this.redisService.storeRefreshToken(userId, newRefreshJti, deviceId);

       return buildLoginResult(user, newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String refreshToken, String accessToken) {
        Jwt decodeRefreshToken = this.tokenProvider.checkValidRefreshToken(refreshToken);
        UUID userId = UUID.fromString(decodeRefreshToken.getSubject());
        String deviceId = decodeRefreshToken.getClaimAsString(JwtConstants.Claims.DEVICE_ID);
        String jtiFromRefreshToken = decodeRefreshToken.getId();
        this.redisService.deleteRefreshTokenByJti(userId, jtiFromRefreshToken);

        RefreshToken refreshTokenEntity = this.refreshTokenRepository.findByUserUserIdAndDeviceId(userId, deviceId)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not found"));
        refreshTokenEntity.setRevoked(true);
        this.refreshTokenRepository.save(refreshTokenEntity);

        // thu hoi access token
        revokeAccessToken(accessToken);
    }

    @Override
    public ForgotPasswordResponseDTO forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
        String email = forgotPasswordRequestDTO.getEmail();

        User user = this.userService.getUserByUsernameOrEmail(email);
        if(!user.getStatus().equals(StatusEnum.ACTIVE)) {
            throw new IllegalArgumentException("User account is not active");
        }
        if(!user.getProvider().equals(ProviderEnum.SERVER)) {
            throw new IllegalArgumentException("User account is not provided server");
        }
        long ttl = 3L;
        String otp = this.redisService.generateAndSaveOTP(email, ttl);

        Map<String, Object> variables = new HashMap<>();
        variables.put("otp", otp);
        this.emailService.sendEmailFromTemplateSync(
                user.getEmail(),
                "OTP for Password Reset",
                "otpVerify",
                variables
        );
        return new ForgotPasswordResponseDTO(email, ttl);
    }

    @Override
    public String otpVerification(String otp, String email) {
        String otpInRedis = this.redisService.getOtp(email);
        if(otpInRedis == null) {
            throw new IllegalArgumentException("Otp time out");
        }
        if(!otpInRedis.equals(otp)) {
            throw new IllegalArgumentException("Otp not matching");
        }
        String resetTokenValue = UUID.randomUUID().toString();
        this.redisService.storeResetToken(email, resetTokenValue);
        return resetTokenValue;
    }

    @Override
    public String resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        String newPassword = resetPasswordRequestDTO.getNewPassword();
        String confirmNewPassword = resetPasswordRequestDTO.getConfirmNewPassword();
        if(!newPassword.equals(confirmNewPassword)) {
            throw new IllegalArgumentException("Password not match");
        }
        String email = resetPasswordRequestDTO.getEmail();
        String tokenInRedis = this.redisService.getResetToken(email);
        if(!tokenInRedis.equals(resetPasswordRequestDTO.getResetToken())) {
            throw  new IllegalArgumentException("token expired");
        }
        User user = this.userService.getUserByUsernameOrEmail(email);
        user.setPassword(this.passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
        this.redisService.deleteResetToken(user.getEmail());
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", user.getUsername());
        emailService.sendEmailFromTemplateSync(
                user.getEmail(),
                "Password Changed Successfully",
                "resetPasswordNotifition",
                variables
        );

        return "Password has been reset successfully. An email confirmation has been sent.";
    }

    private void revokeAccessToken(String accessToken) {
        try {
            Jwt decodeAccessToken = this.tokenProvider.checkValidAccessToken(accessToken);
            Instant expiresAt = decodeAccessToken.getExpiresAt();
            long remainingTimeInSeconds = Duration.between(Instant.now(), expiresAt).getSeconds();
            if (remainingTimeInSeconds > 0) {
                this.redisService.saveBlacklistedAccessToken(accessToken, remainingTimeInSeconds);
                log.debug("Access Token revoked. Redis TTL set to: {}s", remainingTimeInSeconds);
            }
        } catch (Exception e) {
            log.debug("Revocation skipped: Token is already invalid or expired.");
        }
    }

    private LoginResult buildLoginResult(User user, String at, String rt) {
        LoginResult res = new LoginResult();
        LoginResult.UserLoginResult uRes = new LoginResult.UserLoginResult();
        uRes.setUserId(String.valueOf(user.getUserId()));
        uRes.setUsername(user.getUsername());
        uRes.setFullName(user.getFirstName() + " " + user.getLastName());
        uRes.setRoleCode(user.getRole().getCode());
        res.setUser(uRes);
        res.setAccessToken(at);
        res.setRefreshToken(rt);
        return res;
    }

}
