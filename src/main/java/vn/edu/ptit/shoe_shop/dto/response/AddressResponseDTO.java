package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AddressResponseDTO {
    private String addressId;
    private String receiverName;
    private String receiverPhone;
    private String street;
    private String ward;
    private String district;
    private String city;
    private Boolean isDefault;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
