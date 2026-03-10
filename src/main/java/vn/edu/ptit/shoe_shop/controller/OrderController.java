package vn.edu.ptit.shoe_shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.common.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.service.OrderService;


@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. Lấy danh sách (Lịch sử + Lọc theo trạng thái)
    // Ví dụ gọi: GET /api/orders?status=PENDING&page=0&size=10
    @GetMapping("")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @RequestParam(required = false) OrderStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId=SecurityUtils.getCurrentUserId().toString();
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(orderService.getUserOrders(userId, status, pageable));
    }

    // 2. Lấy chi tiết đơn hàng (Theo dõi trạng thái nằm trong object trả về)
    // Ví dụ gọi: GET /api/orders/abc-123
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetail(
            @PathVariable String orderId) {
         String userId=SecurityUtils.getCurrentUserId().toString();
        return ResponseEntity.ok(orderService.getOrderDetail(userId, orderId));
    }

    // 3. Khách hàng bấm hủy đơn
    // Ví dụ gọi: PUT /api/orders/abc-123/cancel
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable String orderId,
            @RequestParam(required = false, defaultValue = "Tôi không còn nhu cầu nữa") String reason) {
          String userId=SecurityUtils.getCurrentUserId().toString();
        OrderResponse response = orderService.cancelOrder(userId, orderId, reason);
        return ResponseEntity.ok(response);
    }
    // 4. Khách hàng theo dõi trạng thái đơn hàng
    // Ví dụ gọi: PUT /api/orders/abc-123/tracking
    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<OrderResponse> trackingOrder(
            @PathVariable String orderId) {
        String userId="123e4567-e89b-12d3-a456-426614174000";
        OrderResponse response = orderService.trackingOrder(userId, orderId);
        return ResponseEntity.ok(response);
    }
     // 5. Admin cập nhật trạng thái của đơn hàng
    // Ví dụ gọi: PUT /api/orders/abc-123/status

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatusOrder(
            @PathVariable String orderId,
            @RequestParam OrderStatusEnum status
        ) {

        OrderResponse response = orderService.updateStatusOrder( orderId,status);
        return ResponseEntity.ok(response);
    }
}