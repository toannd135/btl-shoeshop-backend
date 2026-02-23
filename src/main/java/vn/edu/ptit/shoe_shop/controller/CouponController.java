package vn.edu.ptit.shoe_shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import vn.edu.ptit.shoe_shop.dto.request.CouponRequest;
import vn.edu.ptit.shoe_shop.dto.response.CouponResponse;
import vn.edu.ptit.shoe_shop.service.impl.CouponServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponServiceImpl couponService;

    // 1. Tạo mới mã
    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody @Valid CouponRequest request) {
        return ResponseEntity.ok(couponService.createCoupon(request));
    }

    // 2. Lấy danh sách (Có cả số lượt đã dùng trong Response)
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    // 3. Cập nhật mã
    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable String id, @RequestBody @Valid CouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request));
    }

    // 4. Xóa (Soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok("Xóa (vô hiệu hóa) mã giảm giá thành công!");
    }

    // 5. Validate & Áp dụng coupon
    // Client gọi: GET /api/coupons/validate?code=SUMMER10&orderValue=500000
    @GetMapping("/validate")
    public ResponseEntity<?> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal orderValue) {
        try {
            BigDecimal discount = couponService.validateAndCalculateDiscount(code, orderValue);
            return ResponseEntity.ok(Map.of(
                    "isValid", true,
                    "discountAmount", discount,
                    "message", "Áp dụng mã thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "isValid", false,
                    "discountAmount", 0,
                    "message", e.getMessage()
            ));
        }
    }
}
