package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.CheckoutRequest;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.entity.Order;

public interface CheckoutService {
    public OrderResponse processCheckout(CheckoutRequest request);
}
