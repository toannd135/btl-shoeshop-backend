package vn.edu.ptit.shoe_shop.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerificationRequestDTO {
    @NotBlank(message = "OTP code is required")
    @Size(min = 6, max = 6, message = "OTP must be exactly 6 characters")
    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "OTP must contain only digits"
    )
    private String otp;

    @NotBlank(message = "Email is required")
    @Size(min = 3, max = 100, message = "Email must be between 3 and 100 characters")
    @Pattern(
            regexp = "^(?!.*[\\s])(?!.*[@]{2,})[A-Za-z0-9._%+-@]+$",
            message = "Username can only contain letters, numbers, dots, underscores, and @ symbol. No spaces or consecutive @ symbols allowed."
    )
    private String email;
}
