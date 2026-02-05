package vn.edu.ptit.shoe_shop.dto.response;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.constant.enums.GenderEnum;

@Getter
@Setter
public class UserResponseDTO {
    private String name;
    private Date birthOfDate;
    private GenderEnum gender;
}
