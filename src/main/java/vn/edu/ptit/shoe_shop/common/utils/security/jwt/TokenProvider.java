package vn.edu.ptit.shoe_shop.common.utils.security.jwt;

import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import vn.edu.ptit.shoe_shop.common.constant.JwtConstants;
import vn.edu.ptit.shoe_shop.common.constant.TokenConstants;
import vn.edu.ptit.shoe_shop.service.CustomUserDetail;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenProvider {
    private final JwtEncoder jwtEncoder;

    public TokenProvider(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Value("${app.jwt.base64-secret}")
    private String jwtKey;

    @Value("${app.jwt.access-token-validity-in-seconds}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public String createAccessToken(Authentication authentication) {
        CustomUserDetail userPrincipal = (CustomUserDetail) authentication.getPrincipal();
        //header
        JwsHeader jwtHeader = JwsHeader.with(JwtConstants.Header.ALGORITHM).build();
        //payload
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(JwtConstants.Claims.FIRST_NAME, userPrincipal.getUser().getFirstName());
        extraClaims.put(JwtConstants.Claims.LAST_NAME, userPrincipal.getUser().getLastName());
        extraClaims.put(JwtConstants.Claims.ROLE, userPrincipal.getUser().getRole().getCode());
        extraClaims.put(JwtConstants.Claims.USERNAME,userPrincipal.getUser().getUsername());
        extraClaims.put(JwtConstants.Claims.EMAIL, userPrincipal.getUser().getEmail());
        extraClaims.put(JwtConstants.Claims.TOKEN_TYPE, TokenConstants.ACCESS_TOKEN);
        extraClaims.put(JwtConstants.Claims.USER_PREFIX, null);
        extraClaims.put(JwtConstants.Claims.SESSION_PREFIX, null);

        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);
        JwtClaimsSet jwtPayLoad = JwtClaimsSet.builder()
                .subject(String.valueOf(userPrincipal.getUser().getUserId()))
                .claims(claim -> claim.putAll(extraClaims))
                .issuedAt(now)
                .expiresAt(validity)
                .build();
        //encode & signature
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader,jwtPayLoad)).getTokenValue();
    }

    public String createRefreshToken(Authentication authentication) {
        CustomUserDetail userPrincipal = (CustomUserDetail) authentication.getPrincipal();
        //header
        JwsHeader jwtHeader = JwsHeader.with(JwtConstants.Header.ALGORITHM).build();
        //payload
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put(JwtConstants.Claims.FIRST_NAME, userPrincipal.getUser().getFirstName());
        extraClaims.put(JwtConstants.Claims.LAST_NAME, userPrincipal.getUser().getLastName());
        extraClaims.put(JwtConstants.Claims.ROLE, userPrincipal.getUser().getRole().getCode());
        extraClaims.put(JwtConstants.Claims.USERNAME,userPrincipal.getUser().getUsername());
        extraClaims.put(JwtConstants.Claims.EMAIL, userPrincipal.getUser().getEmail());
        extraClaims.put(JwtConstants.Claims.TOKEN_TYPE, TokenConstants.REFRESH_TOKEN);
        extraClaims.put(JwtConstants.Claims.USER_PREFIX, null);
        extraClaims.put(JwtConstants.Claims.SESSION_PREFIX, null);

        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        JwtClaimsSet jwtPayLoad = JwtClaimsSet.builder()
                .subject(String.valueOf(userPrincipal.getUser().getUserId()))
                .claims(claim -> claim.putAll(extraClaims))
                .issuedAt(now)
                .expiresAt(validity)
                .build();
        //encode & signature
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader,jwtPayLoad)).getTokenValue();
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

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JwtConstants.Header.ALGORITHM.getName());
    }

}
