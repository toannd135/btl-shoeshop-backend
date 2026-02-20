package vn.edu.ptit.shoe_shop.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.utils.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.common.utils.security.jwt.TokenProvider;
import vn.edu.ptit.shoe_shop.dto.LoginResult;
import vn.edu.ptit.shoe_shop.dto.request.auth.LoginRequestDTO;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.AuthService;
import vn.edu.ptit.shoe_shop.service.CustomUserDetail;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private AuthenticationManager authenticationManager;
    private SecurityUtils securityUtils;
    private TokenProvider tokenProvider;
    private UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(AuthenticationManager authenticationManager, SecurityUtils securityUtils, TokenProvider tokenProvider
    , UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.securityUtils = securityUtils;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResult login(LoginRequestDTO request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = this.tokenProvider.createAccessToken(authentication);
        // luu access token vao redis
        log.debug("Generated access token: {}", accessToken);
        String refreshToken = this.tokenProvider.createRefreshToken(authentication);
        // luu refresh token vao redis
        log.debug("Generated refresh token: {}", refreshToken);

        LoginResult res = new LoginResult();
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        User user = customUserDetail.getUser();
        LoginResult.UserLoginResult userLoginResult = new LoginResult.UserLoginResult();
        userLoginResult.setUserId(String.valueOf(user.getUserId()));
        userLoginResult.setUsername(user.getUsername());
        userLoginResult.setFullName(user.getFirstName() + " " + user.getLastName());
        userLoginResult.setRoleCode(user.getRole().getCode());
        res.setUser(userLoginResult);
        res.setAccessToken(accessToken);
        res.setRefreshToken(refreshToken);
        return res;

    }

    @Override
//    @Transactional
    public LoginResult getRefreshToken(String refreshToken) {
        // decode refresh token
        Jwt decodeToken = this.tokenProvider.checkValidRefreshToken(refreshToken);
        UUID userId = UUID.fromString(decodeToken.getSubject());

        User user = this.userRepository.findByUserId(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));

        // so sanh refresh token voi refresh token trong redis

        CustomUserDetail userDetail = new CustomUserDetail(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());

        String newAccessToken = this.tokenProvider.createAccessToken(authentication);
        // luu access token vao redis
        log.debug("Generated access token: {}", newAccessToken);
        String newRefreshToken = this.tokenProvider.createRefreshToken(authentication);
        // luu refresh token vao redis
        log.debug("Generated refresh token: {}", newRefreshToken);

        LoginResult res = new LoginResult();
        LoginResult.UserLoginResult userLoginResult = new LoginResult.UserLoginResult();
        userLoginResult.setUserId(String.valueOf(user.getUserId()));
        userLoginResult.setUsername(user.getUsername());
        userLoginResult.setFullName(user.getFirstName() + " " + user.getLastName());
        userLoginResult.setRoleCode(user.getRole().getCode());
        res.setUser(userLoginResult);
        res.setAccessToken(newAccessToken);
        res.setRefreshToken(newRefreshToken);
        return res;
    }
}
