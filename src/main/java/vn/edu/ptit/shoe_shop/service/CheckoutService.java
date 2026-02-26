package vn.edu.ptit.shoe_shop.service;

import vn.edu.ptit.shoe_shop.dto.request.CheckoutRequest;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
public interface CheckoutService {
    public OrderResponse processCheckout(CheckoutRequest request);
}
