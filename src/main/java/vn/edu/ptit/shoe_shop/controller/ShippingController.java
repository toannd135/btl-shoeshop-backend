package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.edu.ptit.shoe_shop.dto.request.ShippingFeeRequest;
import vn.edu.ptit.shoe_shop.dto.response.ShippingEstimationResponse;
import vn.edu.ptit.shoe_shop.service.impl.ShippingServiceImpl;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingServiceImpl shippingService;

    // POST /api/shipping/estimate
    // Dùng POST để body chứa kích thước, cân nặng giỏ hàng
    @PostMapping("/estimate")
    public ResponseEntity<ShippingEstimationResponse> estimateShipping(
            @Valid @RequestBody ShippingFeeRequest request) {
            
        ShippingEstimationResponse response = shippingService.estimateShipping(request);
        return ResponseEntity.ok(response);
    }
}