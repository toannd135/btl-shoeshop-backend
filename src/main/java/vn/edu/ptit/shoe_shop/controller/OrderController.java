package vn.edu.ptit.shoe_shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.constant.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. Lấy danh sách (Lịch sử + Lọc theo trạng thái)
    // Ví dụ gọi: GET /api/orders?status=PENDING&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @RequestParam(required = false) OrderStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userIdStr="123e4567-e89b-12d3-a456-426614174000";
        UUID userId = UUID.fromString(userIdStr);
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(orderService.getUserOrders(userId, status, pageable));
    }

    // 2. Lấy chi tiết đơn hàng (Theo dõi trạng thái nằm trong object trả về)
    // Ví dụ gọi: GET /api/orders/abc-123
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetail(
            @PathVariable UUID orderId) {
         String userIdStr="123e4567-e89b-12d3-a456-426614174000";
        UUID userId = UUID.fromString(userIdStr);
        return ResponseEntity.ok(orderService.getOrderDetail(userId, orderId));
    }

    // 3. Khách hàng bấm hủy đơn
    // Ví dụ gọi: PUT /api/orders/abc-123/cancel
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam(required = false, defaultValue = "Tôi đổi ý") String reason) {
          String userIdStr="123e4567-e89b-12d3-a456-426614174000";
        UUID userId = UUID.fromString(userIdStr);
        OrderResponse response = orderService.cancelOrder(userId, orderId, reason);
        return ResponseEntity.ok(response);
    }
}