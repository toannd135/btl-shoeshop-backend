package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SellingProductResponseDTO {
    private UUID productId;
    private Long totalSold;
}
