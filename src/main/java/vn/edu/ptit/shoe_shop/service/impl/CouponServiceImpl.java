package vn.edu.ptit.shoe_shop.service.impl;

import vn.edu.ptit.shoe_shop.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.constant.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.CouponRequest;
import vn.edu.ptit.shoe_shop.dto.response.CouponResponse;
import vn.edu.ptit.shoe_shop.entity.Coupon;
import vn.edu.ptit.shoe_shop.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.exception.NotFoundException;
import vn.edu.ptit.shoe_shop.repository.CouponRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    // --- CRUD ---

    public CouponResponse createCoupon(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã giảm giá đã tồn tại!");
        }

        Coupon coupon = new Coupon();
        mapRequestToEntity(request, coupon);
        return mapToResponse(couponRepository.save(coupon));
    }

    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CouponResponse updateCoupon(String id, CouponRequest request) {
        UUID couponId;
        try {
            couponId = UUID.fromString(id);
        } catch (Exception e) {
            // TODO: handle exception
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        Coupon coupon = this.couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Coupon"));

        // Cập nhật thông tin
        mapRequestToEntity(request, coupon);
        return mapToResponse(this.couponRepository.save(coupon));
    }

    @Transactional
    public void deleteCoupon(String id) {
         UUID couponId;
        try {
            couponId = UUID.fromString(id);
        } catch (Exception e) {
            // TODO: handle exception
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy Coupon"));
        // Soft delete: Chuyển status thành INACTIVE thay vì xóa hẳn để giữ lịch sử đơn
        // hàng
        coupon.setStatus(StatusEnum.INACTIVE);
        couponRepository.save(coupon);
    }

    // --- VALIDATE & ÁP DỤNG ---

    /**
     * Hàm này được Frontend gọi khi user nhập mã giảm giá trong Giỏ hàng
     * Trả về số tiền được giảm để hiển thị cho user.
     */
    public BigDecimal validateAndCalculateDiscount(String code, BigDecimal orderValue) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Mã giảm giá không tồn tại!"));

        Instant now = Instant.now();

        // 1. Check Trạng thái
        if (coupon.getStatus() != StatusEnum.ACTIVE) {
            throw new RuntimeException("Mã giảm giá không hoạt động!");
        }

        // 2. Check Thời gian
        if (coupon.getStartsAt() != null && now.isBefore(coupon.getStartsAt())) {
            throw new RuntimeException("Mã giảm giá chưa đến thời gian sử dụng!");
        }
        if (coupon.getExpiresAt() != null && now.isAfter(coupon.getExpiresAt())) {
            throw new RuntimeException("Mã giảm giá đã hết hạn!");
        }

        // 3. Check Lượt sử dụng
        if (coupon.getUsageLimit() != null && coupon.getUsageLimit() <= 0) {
            throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng!");
        }

        // 4. Check Giá trị đơn tối thiểu
        if (coupon.getMinOrderValue() != null && orderValue.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu " + coupon.getMinOrderValue() + "đ");
        }

        // 5. Tính toán tiền giảm
        BigDecimal discountAmount = BigDecimal.ZERO;
        switch (coupon.getDiscountType()) {
            case FIXED_AMOUNT:
                discountAmount = coupon.getDiscountValue();
                break;
            case PERCENTAGE:
                discountAmount = orderValue.multiply(coupon.getDiscountValue())
                        .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
                if (coupon.getMaxDiscount() != null && discountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
                    discountAmount = coupon.getMaxDiscount();
                }
                break;
        }

        // Không cho phép giảm quá giá trị đơn hàng
        return discountAmount.compareTo(orderValue) > 0 ? orderValue : discountAmount;
    }

    // --- MAPPER HELPERS ---
    private void mapRequestToEntity(CouponRequest req, Coupon entity) {
        entity.setCode(req.getCode());
        entity.setName(req.getName());
        entity.setDiscountType(req.getDiscountType());
        entity.setDiscountValue(req.getDiscountValue());
        entity.setMinOrderValue(req.getMinOrderValue());
        entity.setMaxDiscount(req.getMaxDiscount());
        entity.setUsageLimit(req.getUsageLimit());
        entity.setStartsAt(req.getStartsAt());
        entity.setExpiresAt(req.getExpiresAt());
        entity.setStatus(req.getStatus());
    }

    private CouponResponse mapToResponse(Coupon entity) {
        // Đếm số đơn hàng đã dùng mã này (để theo dõi lượt sử dụng)
        int usedCount = entity.getListOrders() != null ? entity.getListOrders().size() : 0;

        return CouponResponse.builder()
                .couponId(entity.getCouponId())
                .code(entity.getCode())
                .name(entity.getName())
                .discountType(entity.getDiscountType())
                .discountValue(entity.getDiscountValue())
                .minOrderValue(entity.getMinOrderValue())
                .maxDiscount(entity.getMaxDiscount())
                .usageLimit(entity.getUsageLimit())
                .startsAt(entity.getStartsAt())
                .expiresAt(entity.getExpiresAt())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .timesUsed(usedCount) // Trả về số lượt đã dùng
                .build();
    }
}
