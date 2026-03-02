package vn.edu.ptit.shoe_shop.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.common.constant.TokenConstants;
import vn.edu.ptit.shoe_shop.common.enums.ProviderEnum;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.utils.security.jwt.TokenProvider;
import vn.edu.ptit.shoe_shop.entity.RefreshToken;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.RefreshTokenRepository;
import vn.edu.ptit.shoe_shop.repository.RoleRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.RedisService;
import vn.edu.ptit.shoe_shop.service.UserService;

import java.io.IOException;
import java.time.Instant;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    public OAuth2LoginSuccessHandler(@Lazy TokenProvider tokenProvider, RedisService redisService,
            UserRepository userRepository, RoleRepository roleRepository,
            UserService userService, RefreshTokenRepository refreshTokenRepository) {
        this.tokenProvider = tokenProvider;
        this.redisService = redisService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Value("${app.jwt.access-token-validity-in-seconds}")
    private Long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,

            Authentication authentication) throws IOException, ServletException {

        // OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // String email = oAuth2User.getAttribute("email");
        // User user = this.userRepository.findByEmail(email).orElseGet(User::new);

        // user.setEmail(email);
        // user.setFirstName(oAuth2User.getAttribute("given_name"));
        // user.setLastName(oAuth2User.getAttribute("family_name"));
        // user.setAvatarImage(oAuth2User.getAttribute("picture"));
        // user.setProvider(ProviderEnum.GOOGLE);
        // String username = email.split("@")[0];
        // user.setUsername(username);
        // Role role = this.roleRepository.findByCode("ROLE_USER")
        // .orElseThrow(() -> new IdInvalidException("role not found"));
        // user.setRole(role);
        // user.setStatus(vn.edu.ptit.shoe_shop.common.enums.StatusEnum.ACTIVE);
        // this.userRepository.save(user);
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            User user = this.userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                user = new User();
                user.setEmail(email);

                String givenName = oAuth2User.getAttribute("given_name");
                String familyName = oAuth2User.getAttribute("family_name");
                String name = oAuth2User.getAttribute("name");

                if (givenName != null) {
                    user.setFirstName(givenName);
                } else {
                    user.setFirstName(name != null ? name : "Google");
                }
                user.setLastName(familyName != null ? familyName : "");

                user.setAvatarImage(oAuth2User.getAttribute("picture"));
                user.setProvider(ProviderEnum.GOOGLE);

                String username = email.split("@")[0];
                user.setUsername(username);
                user.setPassword("GOOGLE_SSO_ACCOUNT");

                Role role = this.roleRepository.findByCode("ROLE_USER")
                        .orElseThrow(() -> new IdInvalidException("role not found"));
                user.setRole(role);
                user.setStatus(vn.edu.ptit.shoe_shop.common.enums.StatusEnum.ACTIVE);

                user = this.userRepository.save(user);
            } else {
                user.setAvatarImage(oAuth2User.getAttribute("picture"));
                user.setProvider(ProviderEnum.GOOGLE);
                this.userRepository.save(user);
            }

            String accessToken = this.tokenProvider.createAccessTokenForOAuth2(user, oAuth2User, "google_oauth2");
            String refreshToken = this.tokenProvider.createRefreshTokenForOAuth2(user, oAuth2User, "google_oauth2");

            Jwt decodeToken = this.tokenProvider.checkValidRefreshToken(refreshToken);
            String jtiFromRefreshToken = decodeToken.getId();

            this.redisService.storeRefreshToken(user.getUserId(),jtiFromRefreshToken, "google_oauth2");

            RefreshToken tokenEntity = this.refreshTokenRepository.findByUserUserIdAndDeviceId(user.getUserId(), "google_oauth2")
                    .orElse(new RefreshToken());
            tokenEntity.setToken(refreshToken);
            tokenEntity.setExpiryDate(Instant.now().plusSeconds(refreshTokenExpiration));
            tokenEntity.setDeviceId("google_oauth2");
            tokenEntity.setRevoked(false);
            tokenEntity.setUser(user);
            this.refreshTokenRepository.save(tokenEntity);

            ResponseCookie refreshTokenCookie = ResponseCookie.from(TokenConstants.REFRESH_TOKEN, refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Math.toIntExact(refreshTokenExpiration))
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            String targetUrl = "http://localhost:5173/oauth2/redirect?token=" + accessToken;
            getRedirectStrategy().sendRedirect(request, response, targetUrl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}