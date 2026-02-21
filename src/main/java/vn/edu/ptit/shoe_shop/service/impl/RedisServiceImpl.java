package vn.edu.ptit.shoe_shop.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.common.constant.TokenConstants;
import vn.edu.ptit.shoe_shop.service.RedisService;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Logger log = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Value("${app.jwt.access-token-validity-in-seconds}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public void storeRefreshToken(UUID userId, String jti, String deviceId) {
        String key = buildRefreshTokenKey(userId, deviceId);
        this.redisTemplate.opsForValue().set(key, jti, refreshTokenExpiration, TimeUnit.SECONDS);
        log.debug("Stored redis refresh token for userId: {}", userId);
    }

    @Override
    public String getRefreshToken(UUID userId, String deviceId) {
        String key = buildRefreshTokenKey(userId, deviceId);
        Object value = this.redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public void deleteRefreshTokenByUserId(UUID userId, String deviceId) {
        String key = buildRefreshTokenKey(userId, deviceId);
        this.redisTemplate.delete(key);
    }

    @Override
    public void saveBlacklistedAccessToken(String accessToken, long secondsLeft) {
        String key = buildBlackListAccessTokenKey(accessToken);
        this.redisTemplate.opsForValue().set(key, "logout", secondsLeft, TimeUnit.SECONDS);
        log.debug("Blacklisted access token: {}", accessToken);
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        String key = buildBlackListAccessTokenKey(accessToken);
        return this.redisTemplate.hasKey(key);
    }

    @Override
    public void deleteRefreshTokenByJti(UUID userId, String jti) {
        String key = TokenConstants.REFRESH_PREFIX + userId.toString() + ":" + jti;
        this.redisTemplate.delete(key);
    }

    @Override
    public boolean isRefreshTokenValid(UUID userId, String jti, String refreshToken) {
        return refreshToken.equals(this.redisTemplate.opsForValue()
                .get(TokenConstants.REFRESH_PREFIX + userId.toString() + ":" + jti));
    }

    private String buildRefreshTokenKey(UUID userId, String deviceId) {
        return new StringBuilder()
                .append(TokenConstants.REFRESH_PREFIX)
                .append(":")
                .append(userId.toString())
                .append(":")
                .append(deviceId)
                .toString();
    }

    private String buildBlackListAccessTokenKey(String accessToken) {
        return new StringBuilder()
                .append(TokenConstants.BLACKLIST_PREFIX)
                .append(":")
                .append(accessToken)
                .toString();
    }
}
