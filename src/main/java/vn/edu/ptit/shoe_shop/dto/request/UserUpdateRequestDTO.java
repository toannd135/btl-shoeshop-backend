package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String avatarImage;
    private String status;
}
