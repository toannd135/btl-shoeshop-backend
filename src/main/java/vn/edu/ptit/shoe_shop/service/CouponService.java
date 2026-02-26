package vn.edu.ptit.shoe_shop.service;

import java.math.BigDecimal;
import java.util.List;

import vn.edu.ptit.shoe_shop.dto.request.CouponRequest;
import vn.edu.ptit.shoe_shop.dto.response.CouponResponse;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);
    List<CouponResponse> getAllCoupons();
    CouponResponse updateCoupon(String id, CouponRequest request);
    void deleteCoupon(String id);
    BigDecimal validateAndCalculateDiscount(String code, BigDecimal orderValue);
}
