package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.SupplierStatusEnum;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierResponse {

    UUID supplierId;

    String supplierName;

    String address;

    String email;

    String phone;

    List<SupplierVariantResponse> variants ;
    SupplierStatusEnum status;

    Instant createdAt;
    Instant updatedAt;
}
