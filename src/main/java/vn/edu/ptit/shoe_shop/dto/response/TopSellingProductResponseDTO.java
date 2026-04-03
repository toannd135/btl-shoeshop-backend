package vn.edu.ptit.shoe_shop.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class TopSellingProductResponseDTO {
    private UUID productId;
    private String productName;
    private Long totalSold;
    private String imageUrl;
}
