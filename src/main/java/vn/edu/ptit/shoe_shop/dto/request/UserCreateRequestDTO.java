package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;

@Getter
public class UserCreateRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String avatarImage;

    private UserRoleCreateRequestDTO role;

    @Getter
    public static class UserRoleCreateRequestDTO {
        private String id;
    }
}
