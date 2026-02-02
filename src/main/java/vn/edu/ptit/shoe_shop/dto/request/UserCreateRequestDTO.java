package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;

@Getter
public class UserCreateRequestDTO {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String avatarImage;
}
