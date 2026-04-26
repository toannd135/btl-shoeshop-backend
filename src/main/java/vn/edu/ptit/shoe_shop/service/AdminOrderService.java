package vn.edu.ptit.shoe_shop.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.UpdateOrderStatusRequest;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.dto.response.TopSellingProductResponseDTO;

public interface AdminOrderService {
    Page<OrderResponse> searchOrders(OrderStatusEnum status, String phone,Instant startDate, Instant endDate, Pageable pageable);
    OrderResponse getOrderDetail(String orderId);
    OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request);
    String exportOrdersToCsv(OrderStatusEnum status, Instant startDate, Instant endDate);

    List<TopSellingProductResponseDTO> getTopSellingProducts();

}
