package vn.edu.ptit.shoe_shop.dto.request.auth;

import lombok.Getter;

@Getter
public class LoginRequestDTO {
    private String username;
    private String email;
    private String password;
}
