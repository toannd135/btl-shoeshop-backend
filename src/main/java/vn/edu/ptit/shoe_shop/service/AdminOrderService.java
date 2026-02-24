package vn.edu.ptit.shoe_shop.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.edu.ptit.shoe_shop.constant.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.UpdateOrderStatusRequest;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;

public interface AdminOrderService {
    public Page<OrderResponse> searchOrders(OrderStatusEnum status, String phone,Instant startDate, Instant endDate, Pageable pageable);
    public OrderResponse getOrderDetail(String orderId);
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request);
    public String exportOrdersToCsv(OrderStatusEnum status, Instant startDate, Instant endDate);
}
