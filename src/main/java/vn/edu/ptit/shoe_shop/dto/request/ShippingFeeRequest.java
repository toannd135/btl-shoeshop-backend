package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ShippingFeeRequest {
    @NotBlank(message = "Mã tỉnh/thành phố nhận không được để trống")
    private String toProvinceCode;
    
    @NotBlank(message = "Mã quận/huyện nhận không được để trống")
    private String toDistrictCode;

    @NotNull(message = "Tổng khối lượng không được để trống")
    @Min(value = 1, message = "Khối lượng phải lớn hơn 0 gram")
    private Integer totalWeightInGrams; // Ví dụ: 1000g (1kg)

    // Kích thước hộp (Dùng để tính khối lượng quy đổi)
    private Integer lengthInCm;
    private Integer widthInCm;
    private Integer heightInCm;
}