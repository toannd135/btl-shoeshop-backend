package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddVariantRequestDTO {
    @NotBlank(message = "Thiếu id của người dùng!")
    private String userId;
    @NotBlank(message = "Thiếu id của sản phẩm!")
    private String variantId;
    @NotNull(message = "Thiếu số lượng sản phẩm!")
    @Min(value = 1,message = "Số lượng sản phẩm không phù hợp!")
    private Integer quantity;

}
