package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrderCreateRequestDTO {
    String note;
    OrderStatusEnum status;
    @NotNull
    LocalDateTime expectedDeliveryDate;
}
