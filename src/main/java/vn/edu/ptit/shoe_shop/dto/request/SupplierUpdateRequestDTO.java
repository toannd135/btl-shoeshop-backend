package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.SupplierStatusEnum;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierUpdateRequestDTO {
    @NotNull
    String supplierName;

    @NotNull
    String address;

    @NotNull
    String email;

    @NotNull
    String phone;

    SupplierStatusEnum status;
}
