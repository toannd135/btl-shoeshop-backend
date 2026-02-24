package vn.edu.ptit.shoe_shop.dto.request.auth;

import lombok.Getter;

@Getter
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phone;
    private String password;
    private String confirmPassword;
}
