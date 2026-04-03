package vn.edu.ptit.shoe_shop.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.ptit.shoe_shop.common.enums.ITEnum;
import vn.edu.ptit.shoe_shop.common.enums.ITStatusEnum;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.exception.NotFoundException;
import vn.edu.ptit.shoe_shop.common.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.dto.logEvent.PurchaseViewEvent;
import vn.edu.ptit.shoe_shop.mapper.OrderMapper;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.entity.InventoryTransaction;
import vn.edu.ptit.shoe_shop.entity.Order;
import vn.edu.ptit.shoe_shop.entity.OrderItem;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.repository.InventoryTransactionRepository;
import vn.edu.ptit.shoe_shop.repository.OrderRepository;
import vn.edu.ptit.shoe_shop.repository.ProductVariantRepository;
import vn.edu.ptit.shoe_shop.service.OrderService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ObjectMapper objectMapper;

    // --- 1. Lịch sử đơn hàng / Danh sách đơn hàng ---
    public Page<OrderResponse> getUserOrders(String userId, OrderStatusEnum status, Pageable pageable) {
        Page<Order> orders;
        UUID userIdUUID;
        try {
            userIdUUID=UUID.fromString(userId);
        } catch (Exception e) {
            // TODO: handle exception
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        // Nếu client truyền lên status thì lọc, không thì lấy tất cả
        if (status != null) {
            orders = orderRepository.findAllByUser_UserIdAndStatusOrderByCreatedAtDesc(userIdUUID, status, pageable);
        } else {
            orders = orderRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userIdUUID, pageable);
        }

        // Chuyển đổi Page<Order> sang Page<OrderResponse>
        return orders.map(orderMapper::toOrderResponse);
    }

    // --- 2. Chi tiết đơn hàng / Theo dõi trạng thái ---
    public OrderResponse getOrderDetail(String userId, String orderId) {
        UUID userIdUUID;
        UUID orderIdUUID;
        try {
            userIdUUID=UUID.fromString(userId);
            orderIdUUID=UUID.fromString(orderId);
        } catch (Exception e) {
            // TODO: handle exception
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        Order order = orderRepository.findByOrderIdAndUser_UserId(orderIdUUID, userIdUUID)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng hoặc bạn không có quyền truy cập!"));
                
        return orderMapper.toOrderResponse(order);
    }

    // --- 3. Hủy đơn hàng ---
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse cancelOrder(String userId, String orderId, String cancelReason) {
        UUID userIdUUID;
        UUID orderIdUUID;
        try {
            userIdUUID=UUID.fromString(userId);
            orderIdUUID=UUID.fromString(orderId);
        } catch (Exception e) {
            // TODO: handle exception
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        Order order = orderRepository.findByOrderIdAndUser_UserId(orderIdUUID, userIdUUID)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng!"));

        // CỰC KỲ QUAN TRỌNG: Chỉ cho phép hủy khi đơn hàng đang ở trạng thái PENDING (Chờ xác nhận)
        // Nếu đã đóng gói (PROCESSING) hoặc đang giao (SHIPPING) thì không cho user tự hủy nữa
        if (order.getStatus() != OrderStatusEnum.PENDING) {
            throw new RuntimeException("Không thể hủy đơn hàng đang trong trạng thái: " + order.getStatus().name());
        }
        for (OrderItem item : order.getListOrderItems()) {
            // Lấy variant với lock để tránh đồng thời
            ProductVariant variant = productVariantRepository.findByIdWithLock(
                            item.getVariant().getProductVariantId())
                    .orElseThrow(() -> new NotFoundException("Variant not found: " + item.getVariant().getProductVariantId()));

            // Cộng lại số lượng đã trừ lúc mua
            variant.setQuantity(variant.getQuantity() + item.getQuantity());

            // Tạo inventory transaction
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .type(ITEnum.CUSTOMER_RETURN)
                    .quantityChange(item.getQuantity())
                    .variant(variant)
                    .reason("Cancel order: " + orderId + " - " + cancelReason)
                    .status(ITStatusEnum.COMPLETED)
                    .build();
            inventoryTransactionRepository.save(transaction);
        }

        // Cập nhật trạng thái và lý do
        order.setStatus(OrderStatusEnum.CANCELLED);
        order.setNote("User hủy: " + cancelReason);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    public OrderResponse trackingOrder(String userId, String orderId) {
        UUID userIdUUID;
        UUID orderIdUUID;
        try {
            userIdUUID=UUID.fromString(userId);
            orderIdUUID=UUID.fromString(orderId);
        } catch (Exception e) {
            // TODO: handle exception
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        Order order = orderRepository.findByOrderIdAndUser_UserId(orderIdUUID, userIdUUID)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng!"));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    public OrderResponse updateStatusOrder(String orderId,OrderStatusEnum status) {
        UUID orderIdUUID;
        try {
            orderIdUUID=UUID.fromString(orderId);
        } catch (Exception e) {
            // TODO: handle exception
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        Order order = orderRepository.findByOrderId(orderIdUUID)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng!"));
        order.setStatus(status);
        this.orderRepository.save(order);
        if(status.equals(OrderStatusEnum.DELIVERED)) {
            for (OrderItem item : order.getListOrderItems()) {
                sendAdminBehaviorEvent(item.getVariant().getProductVariantId());
            }
        }
        return orderMapper.toOrderResponse(order);
    }

    private void sendAdminBehaviorEvent(UUID productId) {
        try {
            PurchaseViewEvent event = PurchaseViewEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .userId(String.valueOf(SecurityUtils.getCurrentUserId()))
                    .productId(productId.toString())
                    .action("PURCHASE")
                    .timestamp(System.currentTimeMillis())
                    .build();

            log.info("USER_EVENT_JSON: {}", objectMapper.writeValueAsString(event));

        } catch (Exception e) {
            log.error("Failed to log event for Big Data pipeline", e);
        }
    }

    
}
