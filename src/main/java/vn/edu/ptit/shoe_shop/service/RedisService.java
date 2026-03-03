package vn.edu.ptit.shoe_shop.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public interface RedisService {

    void storeRefreshToken(UUID userId, String jti, String deviceId);
    String getRefreshToken(UUID userId, String deviceId);
    void deleteRefreshTokenByUserId(UUID userId, String deviceId);
    void saveBlacklistedAccessToken(String accessToken, long secondsLeft);
    boolean isBlacklisted(String accessToken);
    void deleteRefreshTokenByJti(UUID userId, String jti);
    boolean isRefreshTokenValid(UUID userId, String jti, String refreshToken);
    boolean isExistsInSet(String key, String value);
    void addToSet(String key, String value);
    void expireKey(String key, long finalTtl);
    void storeVerificationToken(String key, UUID userId, Long ttlSeconds);
    String getUserIdFromVerificationToken(String token);
    void deleteVerificationToken(String token);
    String generateAndSaveOTP(String email, long ttl);
    String getOtp(String email);
    void deleteOtp(String email) ;
    void storeResetToken(String email, String token);
    String getResetToken(String email);
    void deleteResetToken(String email);
}
