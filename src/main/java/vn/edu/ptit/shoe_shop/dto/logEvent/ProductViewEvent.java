package vn.edu.ptit.shoe_shop.dto.logEvent;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductViewEvent {
    private String eventId;
    private String userId;
    private String productId;
    private String categoryId;
    private String action;
    private long timestamp;
    private String ipAddress;
    private String userAgent;
}
