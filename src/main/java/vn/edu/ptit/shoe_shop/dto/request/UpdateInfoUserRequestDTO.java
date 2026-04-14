package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateInfoUserRequestDTO {
    private String firstName;
    private String lastName;
    private LocalDateTime dateOfBirth;
    private GenderEnum gender;
    private String phone;
}
