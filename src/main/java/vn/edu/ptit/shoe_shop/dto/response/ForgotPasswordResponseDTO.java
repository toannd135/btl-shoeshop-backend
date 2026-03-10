package vn.edu.ptit.shoe_shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ForgotPasswordResponseDTO {
    private String email;
    private long timeToLive;
}
