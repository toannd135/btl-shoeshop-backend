package vn.edu.ptit.shoe_shop.dto.request.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchRequestDTO {
    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String gender;
    private String dateOfBirth;
    private String status;
    private String roleName;
    private String createdAt;
    private String createdBy;
    private String updatedBy;
    private String updatedAt;
}
