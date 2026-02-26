package vn.edu.ptit.shoe_shop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;

public interface OrderService {
    public Page<OrderResponse> getUserOrders(String userId, OrderStatusEnum status, Pageable pageable);
    public OrderResponse getOrderDetail(String userId, String orderId);
    public OrderResponse cancelOrder(String userId, String orderId, String cancelReason);
    public OrderResponse trackingOrder(String userId, String orderId);
    public OrderResponse updateStatusOrder(String userId, String orderId, OrderStatusEnum status);
}
