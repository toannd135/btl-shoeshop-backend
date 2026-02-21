package vn.edu.ptit.shoe_shop.service;

import java.util.UUID;

public interface RedisService {

    void storeRefreshToken(UUID userId, String jti, String deviceId);
    String getRefreshToken(UUID userId, String deviceId);
    void deleteRefreshTokenByUserId(UUID userId, String deviceId);
    void saveBlacklistedAccessToken(String accessToken, long secondsLeft);
    void deleteRefreshTokenByJti(UUID userId, String jti);
    boolean isRefreshTokenValid(UUID userId, String jti, String refreshToken);
}
