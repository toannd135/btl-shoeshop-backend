package vn.edu.ptit.shoe_shop.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePurchaseOrderItemRequestDTO {
    UUID variantId;
    Integer quantity;
}
