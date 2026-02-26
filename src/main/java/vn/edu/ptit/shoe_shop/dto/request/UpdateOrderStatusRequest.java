package vn.edu.ptit.shoe_shop.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;

@Setter
@Getter
public class UpdateOrderStatusRequest {
    @NotNull(message = "Trạng thái mới không được để trống")
    private OrderStatusEnum newStatus;
    // Ghi chú nội bộ cho Admin (Ví dụ: "Khách gọi điện yêu cầu đổi địa chỉ", "Hết hàng màu đỏ")
    private String adminNote;
}
