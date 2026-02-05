package vn.edu.ptit.shoe_shop.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class CartResponseDTO {
    private UUID cartId;
    private List<CartItemResponseDTO> items;

}
