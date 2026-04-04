package vn.edu.ptit.shoe_shop.dto.logEvent;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseViewEvent {
    private String eventId;
    private String userId;
    private String productId;
    private String variantId;
    private String action;
    private long timestamp;
    private String ipAddress;
    private String userAgent;

}
