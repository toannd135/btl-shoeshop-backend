package vn.edu.ptit.shoe_shop.service;

import java.math.BigDecimal;

import vn.edu.ptit.shoe_shop.dto.request.ShippingFeeRequest;
import vn.edu.ptit.shoe_shop.dto.response.ShippingEstimationResponse;
import vn.edu.ptit.shoe_shop.entity.Cart;

public interface ShippingService {
    public BigDecimal calculateFee(String provinceCode,Cart cart);
    public ShippingEstimationResponse estimateShipping(ShippingFeeRequest request);
    
}
