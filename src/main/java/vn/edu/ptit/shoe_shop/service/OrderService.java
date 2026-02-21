package vn.edu.ptit.shoe_shop.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.edu.ptit.shoe_shop.constant.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;

public interface OrderService {
    public Page<OrderResponse> getUserOrders(UUID userId, OrderStatusEnum status, Pageable pageable);
    public OrderResponse getOrderDetail(UUID userId, UUID orderId);
     public OrderResponse cancelOrder(UUID userId, UUID orderId, String cancelReason);
}
