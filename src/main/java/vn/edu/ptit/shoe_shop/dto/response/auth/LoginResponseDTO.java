package vn.edu.ptit.shoe_shop.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private UserLoginResponseDTO user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLoginResponseDTO {
        private String userId;
        private String fullName;
        private String username;
        private String roleCode;
        private String avatarImage;
    }

}
