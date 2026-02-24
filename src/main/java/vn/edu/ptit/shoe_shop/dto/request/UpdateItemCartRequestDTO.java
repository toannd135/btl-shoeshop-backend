package vn.edu.ptit.shoe_shop.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.constant.enums.StepTypeEnum;

@Setter
@Getter
public class UpdateItemCartRequestDTO {
    @NotBlank(message = "Thiếu id của giỏ hàng!")
    private String cartItemId;
   @NotNull
    private StepTypeEnum step;
}
