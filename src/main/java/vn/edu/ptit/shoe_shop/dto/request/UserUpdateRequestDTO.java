package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequestDTO {
    private String firstname;
    private String lastname;
    private String username;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String avatarImage;

}
