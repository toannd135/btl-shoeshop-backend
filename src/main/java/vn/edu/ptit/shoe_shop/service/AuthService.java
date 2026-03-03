package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.LoginResult;
import vn.edu.ptit.shoe_shop.dto.request.auth.ForgotPasswordRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.auth.LoginRequestDTO;
import vn.edu.ptit.shoe_shop.dto.request.auth.ResetPasswordRequestDTO;
import vn.edu.ptit.shoe_shop.dto.response.ForgotPasswordResponseDTO;

public interface AuthService {
    LoginResult login(LoginRequestDTO request, String deviceId);
    LoginResult getNewToken(String refreshToken, String accessToken);
    void logout(String refreshToken, String accessToken);
    ForgotPasswordResponseDTO forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);
    String otpVerification(String otp);
    String resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO);
}
