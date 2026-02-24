package vn.edu.ptit.shoe_shop.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import vn.edu.ptit.shoe_shop.dto.response.OrderItemResponse;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.dto.response.UserShortInfo;
import vn.edu.ptit.shoe_shop.entity.Order;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;

        // Map User
        UserShortInfo userInfo = UserShortInfo.builder()
                .userId(order.getUser().getUserId())
                .name(order.getUser().getName())
                .phone(order.getUser().getPhone())
                .email(order.getUser().getEmail())
                .build();

        List<OrderItemResponse> items = order.getListOrderItems().stream().map(item->
            OrderItemResponse.builder()
            .productName(item.getVariant().getProduct().getName())
            .size(item.getVariant().getSize())
            .quantity(item.getQuantity())
            .price(item.getPriceAtPurchase())
            .build()
        ).collect(Collectors.toList());

        // Map Order
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getCreatedAt())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .shippingAddress(order.getShippingAddress())
                .totalPrice(order.getTotalPrice())
                .discountAmount(order.getDiscountAmount())
                .shippingFee(order.getShippingFee())
                .finalPrice(order.getFinalPrice())
                .status(order.getStatus().name())
                .note(order.getNote())
                .user(userInfo)
                .items(items)
                .build();
    }
}