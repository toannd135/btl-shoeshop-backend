package vn.edu.ptit.shoe_shop.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.common.constant.JwtConstants;
import vn.edu.ptit.shoe_shop.common.exception.BadCredentialsException;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.utils.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.common.utils.security.jwt.TokenProvider;
import vn.edu.ptit.shoe_shop.dto.LoginResult;
import vn.edu.ptit.shoe_shop.dto.request.auth.LoginRequestDTO;
import vn.edu.ptit.shoe_shop.entity.RefreshToken;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.RefreshTokenRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.AuthService;
import vn.edu.ptit.shoe_shop.service.CustomUserDetail;
import vn.edu.ptit.shoe_shop.service.RedisService;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public AuthServiceImpl(AuthenticationManager authenticationManager, TokenProvider tokenProvider
            , UserRepository userRepository, RedisService redisService, RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.refreshTokenRepository = refreshTokenRepository;
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
        String accessToken = this.tokenProvider.createAccessToken(authentication, deviceId); // save into memory frontend
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
