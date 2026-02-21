package vn.edu.ptit.shoe_shop.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import vn.edu.ptit.shoe_shop.entity.Cart;
import vn.edu.ptit.shoe_shop.entity.CartItem;
import vn.edu.ptit.shoe_shop.service.ShippingService;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Override
    public BigDecimal calculateFee(String provinceCode, Cart cart) {
        BigDecimal shippingFee=BigDecimal.ZERO;
       Integer totalWeight=0;
       for(CartItem item: cart.getItems())
       {
        totalWeight+= item.getVariant().getWeightGram();
       }
       // Phí cơ bản theo vùng miền
        if ("001".equalsIgnoreCase(provinceCode) || "059".equalsIgnoreCase(provinceCode)) {
            shippingFee = shippingFee.add(new BigDecimal("20000")); // Nội thành
        } else {
            shippingFee = shippingFee.add(new BigDecimal("35000")); // Ngoại tỉnh
        }

        // Phụ phí nếu giày nặng (ví dụ giày bảo hộ)
        if (totalWeight > 2) {
            shippingFee = shippingFee.add(new BigDecimal("10000"));
        }
        
        return shippingFee;
    }
    
}
