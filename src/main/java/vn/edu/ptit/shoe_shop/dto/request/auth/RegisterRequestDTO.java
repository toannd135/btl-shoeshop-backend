package vn.edu.ptit.shoe_shop.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequestDTO {
    private String firstName;
    private String lastName;
    @NotBlank(message = "Email is required")
    @Size(min = 3, max = 100, message = "Email must be between 3 and 100 characters")
    @Pattern(
            regexp = "^(?!.*[\\s])(?!.*[@]{2,})[A-Za-z0-9._%+-@]+$",
            message = "Username can only contain letters, numbers, dots, underscores, and @ symbol. No spaces or consecutive @ symbols allowed."
    )
    private String email;
    private String username;
    private String phone;
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,64}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    private String confirmPassword;
}
