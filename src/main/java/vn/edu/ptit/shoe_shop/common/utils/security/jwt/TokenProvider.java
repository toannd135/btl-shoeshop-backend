package vn.edu.ptit.shoe_shop.common.utils.security.jwt;

import com.nimbusds.jose.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.common.constant.JwtConstants;
import vn.edu.ptit.shoe_shop.common.constant.TokenConstants;
import vn.edu.ptit.shoe_shop.common.enums.ProviderEnum;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.service.CustomUserDetail;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TokenProvider {
    private final JwtEncoder jwtEncoder;

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    public TokenProvider(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Value("${app.jwt.base64-secret}")
    private String jwtKey;

    @Value("${app.jwt.access-token-validity-in-seconds}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    @Transactional
    public String createAccessToken(Authentication authentication, String deviceId) {
        CustomUserDetail userPrincipal = (CustomUserDetail) authentication.getPrincipal();
        //header
        JwsHeader jwtHeader = JwsHeader.with(JwtConstants.Header.ALGORITHM).build();
        //payload
        Map<String, Object> extraClaims = buildCustomClaims(userPrincipal, deviceId, TokenConstants.ACCESS_TOKEN);

        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);
        String jti = UUID.randomUUID().toString();
        JwtClaimsSet jwtPayLoad = JwtClaimsSet.builder()
                .subject(String.valueOf(userPrincipal.getUser().getUserId()))
                .id(jti)
                .claims(claim -> claim.putAll(extraClaims))
                .issuedAt(now)
                .expiresAt(validity)
                .build();
        //encode & signature
        String accessToken =  this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader,jwtPayLoad)).getTokenValue();
        log.debug("Created access token for userId {}: {}", userPrincipal.getUser().getUserId(), accessToken);
        return accessToken;
    }

    @Transactional
    public String createRefreshToken(Authentication authentication, String deviceId) {
        CustomUserDetail userPrincipal = (CustomUserDetail) authentication.getPrincipal();
        //header
        JwsHeader jwtHeader = JwsHeader.with(JwtConstants.Header.ALGORITHM).build();
        //payload
        Map<String, Object> extraClaims = buildCustomClaims(userPrincipal, deviceId,TokenConstants.REFRESH_TOKEN);

        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);
        String jti = UUID.randomUUID().toString();
        JwtClaimsSet jwtPayLoad = JwtClaimsSet.builder()
                .subject(String.valueOf(userPrincipal.getUser().getUserId()))
                .id(jti)
                .claims(claim -> claim.putAll(extraClaims))
                .issuedAt(now)
                .expiresAt(validity)
                .build();
        //encode & signature
        String refreshToken =  this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader,jwtPayLoad)).getTokenValue();
        log.debug("Created refresh token for userId {}: {}", userPrincipal.getUser().getUserId(), refreshToken);
        return refreshToken;
    }


    @Transactional
    public String createAccessTokenForOAuth2(User user, OAuth2User oAuth2User, String deviceId) {
        //header
        JwsHeader jwtHeader = JwsHeader.with(JwtConstants.Header.ALGORITHM).build();
        //payload
        Map<String, Object> extraClaims = buildCustomForOAuth2Claims(oAuth2User, deviceId,TokenConstants.ACCESS_TOKEN);
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);
        String jti = UUID.randomUUID().toString();

        JwtClaimsSet jwtPayLoad = JwtClaimsSet.builder()
                .subject(String.valueOf(user.getUserId()))
                .id(jti)
                .claims(claim -> claim.putAll(extraClaims))
                .issuedAt(now)
                .expiresAt(validity)
                .build();

        String accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader, jwtPayLoad)).getTokenValue();
        log.debug("Created OAuth2 access token for email {}: {}", oAuth2User.getAttribute("email"), accessToken);
        return accessToken;
    }

    @Transactional
    public String createRefreshTokenForOAuth2(User user, OAuth2User oAuth2User, String deviceId) {
        //header
        JwsHeader jwtHeader = JwsHeader.with(JwtConstants.Header.ALGORITHM).build();
        //payload
        Map<String, Object> extraClaims = buildCustomForOAuth2Claims(oAuth2User, deviceId,TokenConstants.REFRESH_TOKEN);
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);
        String jti = UUID.randomUUID().toString();

        JwtClaimsSet jwtPayLoad = JwtClaimsSet.builder()
                .subject(String.valueOf(user.getUserId()))
                .id(jti)
                .claims(claim -> claim.putAll(extraClaims))
                .issuedAt(now)
                .expiresAt(validity)
                .build();

        String refreshToken = this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader, jwtPayLoad)).getTokenValue();
        log.debug("Created OAuth2 access token for email {}: {}", oAuth2User.getAttribute("email"), refreshToken);
        return refreshToken;
    }


    public Jwt checkValidRefreshToken(String refreshToken) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(JwtConstants.Header.ALGORITHM).build();
        try {
            return jwtDecoder.decode(refreshToken);
        } catch (Exception e) {
            throw e;
        }
    }

    public Jwt checkValidAccessToken(String accessToken) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(JwtConstants.Header.ALGORITHM).build();
        try {
            return jwtDecoder.decode(accessToken);
        } catch (Exception e) {
            throw e;
        }
    }

    public String getJtiFromToken(String token) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(JwtConstants.Header.ALGORITHM).build();
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getId();
        } catch (Exception e) {
            throw e;
        }
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JwtConstants.Header.ALGORITHM.getName());
    }

    private Map<String, Object> buildCustomClaims(CustomUserDetail userPrincipal, String deviceId, String tokenType) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(JwtConstants.Claims.FIRST_NAME, userPrincipal.getUser().getFirstName());
        extraClaims.put(JwtConstants.Claims.LAST_NAME, userPrincipal.getUser().getLastName());
        extraClaims.put(JwtConstants.Claims.ROLE, userPrincipal.getUser().getRole().getCode());
        extraClaims.put(JwtConstants.Claims.USERNAME,userPrincipal.getUser().getUsername());
        extraClaims.put(JwtConstants.Claims.EMAIL, userPrincipal.getUser().getEmail());
        extraClaims.put(JwtConstants.Claims.TOKEN_TYPE, tokenType);
        extraClaims.put(JwtConstants.Claims.PROVIDER, userPrincipal.getUser().getProvider());
        extraClaims.put(JwtConstants.Claims.DEVICE_ID, deviceId);
        extraClaims.put(JwtConstants.Claims.USER_PREFIX, null);
        extraClaims.put(JwtConstants.Claims.SESSION_PREFIX, null);
        return extraClaims;
    }

    private Map<String, Object> buildCustomForOAuth2Claims(OAuth2User auth2User, String deviceId, String tokenType) {
        String firstName = auth2User.getAttribute("give_name");
        String lastName = auth2User.getAttribute("family_name");
        String role = "ROLE_USER";
        String email = auth2User.getAttribute("email");

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(JwtConstants.Claims.FIRST_NAME, firstName);
        extraClaims.put(JwtConstants.Claims.LAST_NAME, lastName);
        extraClaims.put(JwtConstants.Claims.ROLE, role);
        extraClaims.put(JwtConstants.Claims.EMAIL, email);
        extraClaims.put(JwtConstants.Claims.TOKEN_TYPE, tokenType);
        extraClaims.put(JwtConstants.Claims.PROVIDER, ProviderEnum.GOOGLE);
        extraClaims.put(JwtConstants.Claims.DEVICE_ID, deviceId);
        extraClaims.put(JwtConstants.Claims.USER_PREFIX, null);
        extraClaims.put(JwtConstants.Claims.SESSION_PREFIX, null);
        return extraClaims;
    }

}
