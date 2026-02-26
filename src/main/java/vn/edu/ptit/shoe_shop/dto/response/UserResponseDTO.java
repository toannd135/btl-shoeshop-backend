package vn.edu.ptit.shoe_shop.dto.response;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;


@Getter
@Setter
public class UserResponseDTO {
    private String name;
    private Date birthOfDate;
    private GenderEnum gender;
    private String userId;
    private String fullName;
    private String username;
    private String email;
    private String phone;
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
