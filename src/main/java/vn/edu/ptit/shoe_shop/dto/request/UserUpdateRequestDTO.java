package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateRequestDTO {
    private String name;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String avatarImage;
}
