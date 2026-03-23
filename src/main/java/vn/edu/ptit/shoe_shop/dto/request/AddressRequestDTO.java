package vn.edu.ptit.shoe_shop.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {
    private String receiverName;
    private String receiverPhone;
    private String street;
    private String ward;
    private String district;
    private String city;
    private Boolean isDefault;
}
