package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateInfoUserRequestDTO {
    private String fullName;      // nhận từ frontend, sẽ split thành firstName + lastName trong service
    private LocalDate dateOfBirth;
    private GenderEnum gender;
    private String phone;
    private String avatarImage;
}
