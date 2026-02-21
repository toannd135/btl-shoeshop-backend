package vn.edu.ptit.shoe_shop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.constant.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.dto.mapper.OrderMapper;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.entity.Order;
import vn.edu.ptit.shoe_shop.entity.OrderItem;
import vn.edu.ptit.shoe_shop.exception.NotFoundException;
import vn.edu.ptit.shoe_shop.repository.OrderRepository;
import vn.edu.ptit.shoe_shop.service.OrderService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    // --- 1. Lịch sử đơn hàng / Danh sách đơn hàng ---
    public Page<OrderResponse> getUserOrders(UUID userId, OrderStatusEnum status, Pageable pageable) {
        Page<Order> orders;
        
        // Nếu client truyền lên status thì lọc, không thì lấy tất cả
        if (status != null) {
            orders = orderRepository.findAllByUser_UserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable);
        } else {
            orders = orderRepository.findAllByUser_UserIdOrderByCreatedAtDesc(userId, pageable);
        }

        // Chuyển đổi Page<Order> sang Page<OrderResponse>
        return orders.map(orderMapper::toOrderResponse);
    }

    // --- 2. Chi tiết đơn hàng / Theo dõi trạng thái ---
    public OrderResponse getOrderDetail(UUID userId, UUID orderId) {
        Order order = orderRepository.findByOrderIdAndUser_UserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng hoặc bạn không có quyền truy cập!"));
                
        return orderMapper.toOrderResponse(order);
    }

    // --- 3. Hủy đơn hàng ---
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse cancelOrder(UUID userId, UUID orderId, String cancelReason) {
        Order order = orderRepository.findByOrderIdAndUser_UserId(orderId, userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng!"));

        // CỰC KỲ QUAN TRỌNG: Chỉ cho phép hủy khi đơn hàng đang ở trạng thái PENDING (Chờ xác nhận)
        // Nếu đã đóng gói (PROCESSING) hoặc đang giao (SHIPPING) thì không cho user tự hủy nữa
        if (order.getStatus() != OrderStatusEnum.PENDING) {
            throw new RuntimeException("Không thể hủy đơn hàng đang trong trạng thái: " + order.getStatus().name());
        }

        // Cập nhật trạng thái và lý do
        order.setStatus(OrderStatusEnum.CANCELLED);
        order.setNote("User hủy: " + cancelReason); // Giả sử bảng Order có cột note

        // ROLLBACK TỒN KHO: Trả lại số lượng giày vào kho
        for (OrderItem item : order.getListOrderItems()) {
            var variant = item.getVariant();
            // Cộng lại số lượng đã trừ lúc mua
            variant.setQuantity(variant.getQuantity() + item.getQuantity()); 
        }

        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(savedOrder);
    }
}
