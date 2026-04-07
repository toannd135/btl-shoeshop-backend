package vn.edu.ptit.shoe_shop.service;

import java.io.IOException;
import java.time.Instant;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.UpdateOrderStatusRequest;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;

public interface AdminOrderService {
    public Page<OrderResponse> searchOrders(OrderStatusEnum status, String phone,Instant startDate, Instant endDate, Pageable pageable);
    public OrderResponse getOrderDetail(String orderId);
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request);
    public void exportOrdersToCsv(OrderStatusEnum status, Instant startDate, Instant endDate, HttpServletResponse response) throws IOException;
}
