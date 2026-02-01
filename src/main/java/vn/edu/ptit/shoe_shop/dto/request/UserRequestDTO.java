package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRequestDTO {
    @NotBlank(message = "ten khong duoc de trong")
    private String name;
    private String birthOfDate;
    private String gender;
}
