package vn.edu.ptit.shoe_shop.dto.request;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class CheckoutRequest {
    @NotEmpty(message = "userId is required!")
    private String userId;
    @NotEmpty(message = "cartId is required!")
    private String cartId;
    @Nullable
    private String couponCode; // Có thể null
    @NotEmpty(message = "receiverName is required!")
    private String receiverName;
    @NotEmpty(message = "receiverPhone is required!")
    private String receiverPhone;
    @NotEmpty(message = "shippingAddress is required!")
    private String shippingAddress;
    @NotEmpty(message = "provinceCode is required!")
    private String provinceCode;
    @Nullable
    private String note;
    
}
