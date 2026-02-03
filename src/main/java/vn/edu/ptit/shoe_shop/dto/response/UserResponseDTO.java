package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserResponseDTO {
    private String userId;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String avatarImage;
    private String status;
    private String createdAt;
    private String createdBy;
    private String updatedBy;
    private String updatedAt;

    private UserRoleResponseDTO role;

    @Getter
    @Setter
    public static class UserRoleResponseDTO {
        private String name;
        private String code;
    }
}
