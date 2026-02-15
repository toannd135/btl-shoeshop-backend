package vn.edu.ptit.shoe_shop.dto.request;

import java.time.LocalDate;

import lombok.Getter;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;

@Getter
public class UserUpdateRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String avatarImage;
    private StatusEnum status;
    private String roleId;
}
