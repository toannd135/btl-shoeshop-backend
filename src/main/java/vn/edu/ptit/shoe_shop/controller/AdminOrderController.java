package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.constant.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.UpdateOrderStatusRequest;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.service.AdminOrderService;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
// @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // Mở cái này ra khi có Spring Security
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // 1. Danh sách & Filter
    // Ví dụ: GET /api/admin/orders?status=PENDING&phone=090&page=0&size=20
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @RequestParam(required = false) OrderStatusEnum status,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminOrderService.searchOrders(status, phone, startDate, endDate, pageable));
    }

    // 2. Xem chi tiết
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable String id) {
        return ResponseEntity.ok(adminOrderService.getOrderDetail(id));
    }

    // 3. Cập nhật trạng thái
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable String id,
             @RequestBody @Valid UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(adminOrderService.updateOrderStatus(id, request));
    }

    // 4. Export dữ liệu ra CSV
    // Trả về file tải xuống cho trình duyệt
    @GetMapping("/export")
    public ResponseEntity<Resource> exportOrders(
            @RequestParam(required = false) OrderStatusEnum status,
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate) {
            
         String csvData = adminOrderService.exportOrdersToCsv(status, startDate, endDate);
    ByteArrayResource resource = new ByteArrayResource(csvData.getBytes(StandardCharsets.UTF_8));

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=orders.csv")
            .contentType(MediaType.parseMediaType("text/csv"))
            .body(resource);
    }
}