package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private UUID reviewId;

    private UUID userId;
    private String userFirstName;
    private String userLastName;

    private UUID variantId;

    private String productName;

    private Integer rating;
    private String note;

    private LocalDateTime createdAt;
}