package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.LoginResult;
import vn.edu.ptit.shoe_shop.dto.request.auth.LoginRequestDTO;

public interface AuthService {
    LoginResult login(LoginRequestDTO request, String deviceId);
    LoginResult getNewToken(String refreshToken);
    void logout(String refreshToken, String accessToken);
}
