package vn.edu.ptit.shoe_shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResult {
    private String accessToken;
    private String refreshToken;
    private LoginResult.UserLoginResult user;

    @Getter
    @Setter
    public static class UserLoginResult{
        private String userId;
        private String fullName;
        private String username;
        private String roleCode;
    }
}
