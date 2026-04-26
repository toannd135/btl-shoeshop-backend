package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopSellingProductResponseDTO {
    private UUID productId;
    private String productName;
    private Long totalSold;
    private String imageUrl;
}
