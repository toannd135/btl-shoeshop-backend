package vn.edu.ptit.shoe_shop.service.impl;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.exception.NotFoundException;
import vn.edu.ptit.shoe_shop.common.security.SecurityUtils;
import vn.edu.ptit.shoe_shop.dto.response.TopSellingProductResponseDTO;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.mapper.OrderMapper;
import vn.edu.ptit.shoe_shop.dto.request.UpdateOrderStatusRequest;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.entity.Order;
import vn.edu.ptit.shoe_shop.mapper.ProductMapper;
import vn.edu.ptit.shoe_shop.repository.OrderRepository;
import vn.edu.ptit.shoe_shop.repository.ProductRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.AdminOrderService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderServiceImpl implements AdminOrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    // 1. Lấy danh sách & Filter
    public Page<OrderResponse> searchOrders(OrderStatusEnum status, String phone, 
                                            Instant startDate, Instant endDate, Pageable pageable) {
        Page<Order> orders = orderRepository.searchOrdersForAdmin(status, phone, startDate, endDate, pageable);
        return orders.map(orderMapper::toOrderResponse);
    }

    // 2. Lấy chi tiết
    public OrderResponse getOrderDetail(String orderId) {
        UUID oderIdUUID;
        try {
            oderIdUUID = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        Order order = orderRepository.findById(oderIdUUID)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng ID: " + orderId));
        return orderMapper.toOrderResponse(order);
    }

    // 3. Cập nhật trạng thái đơn hàng (CÓ RÀNG BUỘC)
    @Transactional
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        UUID oderIdUUID;
        try {
            oderIdUUID = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        Order order = orderRepository.findById(oderIdUUID)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng!"));

        OrderStatusEnum currentStatus = order.getStatus();
        OrderStatusEnum newStatus = request.getNewStatus();

        // VALIDATE STATE MACHINE (Chống nhảy cóc trạng thái)
        validateStatusTransition(currentStatus, newStatus);

        order.setStatus(newStatus);
        
        // Cập nhật note (Nối thêm vào note cũ để giữ lịch sử)
        if (request.getAdminNote() != null && !request.getAdminNote().isBlank()) {
            String existingNote = order.getNote() != null ? order.getNote() + " | " : "";
            order.setNote(existingNote + "[" + Instant.now() + "] Admin: " + request.getAdminNote());
        }

        // TODO: Nếu status = CANCELLED, gọi logic cộng lại Tồn kho (như bài trước)
        // TODO: Nếu status = DELIVERED, có thể gọi logic cộng điểm tích lũy cho User

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }




    // Hàm kiểm tra luồng đi của trạng thái
    private void validateStatusTransition(OrderStatusEnum current, OrderStatusEnum next) {
        if (current == next) return; // Không đổi thì bỏ qua

        boolean isValid = switch (current) {
            case PENDING -> next == OrderStatusEnum.CONFIRMED || next == OrderStatusEnum.CANCELLED;
            case CONFIRMED -> next == OrderStatusEnum.SHIPPING || next == OrderStatusEnum.CANCELLED;
            case SHIPPING -> next == OrderStatusEnum.DELIVERED || next == OrderStatusEnum.RETURNED;
            case DELIVERED, CANCELLED, RETURNED -> false; // Trạng thái cuối, KHÔNG THỂ ĐỔI
            default -> false;
        };

        if (!isValid) {
            throw new RuntimeException(
                String.format("Luồng không hợp lệ! Không thể chuyển từ %s sang %s", current.name(), next.name())
            );
        }
    }

    // 4. Sinh file CSV để Export
    public String exportOrdersToCsv(OrderStatusEnum status, Instant startDate, Instant endDate) {
        List<Order> orders = orderRepository.getOrdersForExport(status, startDate, endDate);
        
        StringBuilder csvBuilder = new StringBuilder();
        // Header
        csvBuilder.append("Mã Đơn Hàng,Ngày Đặt,Tên Người Nhận,Số Điện Thoại,Tổng Tiền,Trạng Thái\n");
        
        // Data
        for (Order order : orders) {
            csvBuilder.append(order.getOrderId()).append(",")
                      .append(order.getCreatedAt()).append(",")
                      .append(order.getReceiverName()).append(",")
                      .append(order.getReceiverPhone()).append(",")
                      .append(order.getFinalPrice()).append(",")
                      .append(order.getStatus().name()).append("\n");
        }
        return csvBuilder.toString();
    }

    @Override
    public List<TopSellingProductResponseDTO> getTopSellingProducts() {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IdInvalidException("User not found"));
        if (!user.getRole().getCode().equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException("User access deny");
        }
        Map<String, String> valueInRedis = stringRedisTemplate.<String, String>opsForHash().entries("rec:best-seller");
        if (valueInRedis == null || valueInRedis.isEmpty()) {
            log.warn("No recommendation found in Redis");
            return new ArrayList<>();
        }

        List<String> productIds = valueInRedis.keySet().stream()
                .map(id -> id.toString())
                .toList();
        List<Product> products = productRepository.findByProductIdIn(productIds);

        return products.stream().map(product -> {
                    TopSellingProductResponseDTO dto = new TopSellingProductResponseDTO();
                    dto.setProductId(product.getProductId());
                    dto.setProductName(product.getName());
                    dto.setImageUrl(product.getImageUrl());
                    String sellCount = valueInRedis.get(product.getProductId().toString());
                    dto.setTotalSold(sellCount != null ? Long.parseLong(sellCount) : 0L);

                    return dto;
                }).sorted(Comparator.comparing(TopSellingProductResponseDTO::getTotalSold).reversed())
                .toList();
    }
}
