package vn.edu.ptit.shoe_shop.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDTO {
    @NotBlank
    private String username; // email or username
    @NotBlank
    private String password;
}
