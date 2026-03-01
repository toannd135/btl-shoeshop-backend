package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;

import java.util.UUID;

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
    private String status;
    private UserRoleCreateRequestDTO role;

    @Getter
    public static class UserRoleCreateRequestDTO {
        private UUID id;
    }
}
