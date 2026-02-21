package vn.edu.ptit.shoe_shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.edu.ptit.shoe_shop.dto.request.CheckoutRequest;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.service.CheckoutService;

@RestController
@RequestMapping("api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;
    @PostMapping("")
    public ResponseEntity<OrderResponse> checkout(@RequestBody @Valid CheckoutRequest request) {
        // Gọi xuống tầng Service để xử lý logic
        OrderResponse createdOrder = checkoutService.processCheckout(request);
        return ResponseEntity.ok(createdOrder);
    }
}
