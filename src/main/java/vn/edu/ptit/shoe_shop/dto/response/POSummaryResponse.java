package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class POSummaryResponse {
    UUID poId;
    UUID supplierId;
    OrderStatusEnum status;
    Instant createdAt;
    Instant updatedAt;
}
