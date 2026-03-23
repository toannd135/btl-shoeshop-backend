package vn.edu.ptit.shoe_shop.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderResponse {
    UUID poId;
    UUID supplierId;
    String note;
    OrderStatusEnum status;
    LocalDateTime expectedDeliveryDate;
    List<POItemResponse> items;
    Instant createdAt;
    Instant updatedAt;
}
