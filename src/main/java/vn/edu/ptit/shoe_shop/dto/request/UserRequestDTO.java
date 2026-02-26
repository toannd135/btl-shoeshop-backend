package vn.edu.ptit.shoe_shop.dto.request;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;

@Getter
public class UserRequestDTO {
    @NotBlank(message = "ten khong duoc de trong")
    private String name;
    private Date birthOfDate;
    private GenderEnum gender;
}
