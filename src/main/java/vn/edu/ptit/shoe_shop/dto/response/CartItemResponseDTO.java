package vn.edu.ptit.shoe_shop.dto.response;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class CartItemResponseDTO  {
     private UUID cartItemId;
    private VariantDTO variant;
    private int quantity;
}
