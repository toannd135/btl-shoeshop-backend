package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserUpdateRequestDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String avatarImage;
    private String status;
    private UserRoleUpdateRequestDTO role;

    @Getter
    @Setter
    public static class UserRoleUpdateRequestDTO {
        private UUID id;
    }
}
