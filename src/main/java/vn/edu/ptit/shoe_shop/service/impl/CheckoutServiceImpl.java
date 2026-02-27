package vn.edu.ptit.shoe_shop.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode; // Import quan trọng
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.edu.ptit.shoe_shop.common.enums.DiscountTypeEnum;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.exception.NotFoundException;
import vn.edu.ptit.shoe_shop.dto.mapper.OrderMapper;
import vn.edu.ptit.shoe_shop.dto.request.CheckoutRequest;
import vn.edu.ptit.shoe_shop.dto.response.OrderResponse;
import vn.edu.ptit.shoe_shop.entity.Cart;
import vn.edu.ptit.shoe_shop.entity.CartItem;
import vn.edu.ptit.shoe_shop.entity.Coupon;
import vn.edu.ptit.shoe_shop.entity.Order;
import vn.edu.ptit.shoe_shop.entity.OrderItem;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.entity.User;

import vn.edu.ptit.shoe_shop.repository.CartRepository;
import vn.edu.ptit.shoe_shop.repository.CouponRepository;
import vn.edu.ptit.shoe_shop.repository.OrderRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.service.CheckoutService;
import vn.edu.ptit.shoe_shop.service.ShippingService;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final ShippingService shippingService;

    @Override
    @Transactional(rollbackFor = Exception.class) // Rollback nếu có bất kỳ lỗi nào
    public OrderResponse processCheckout(CheckoutRequest request) {
        // 1. Parse ID
        UUID cartId;
        UUID userId;
        try {
            cartId = UUID.fromString(request.getCartId());
            userId = UUID.fromString(request.getUserId());
        } catch (IllegalArgumentException e) {
            throw new IdInvalidException("Id không đúng định dạng!");
        }

        // --- BƯỚC 1: VALIDATE CART & USER ---
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng!"));
        
        Cart cart = cartRepository.findByCartId(cartId)
                .orElseThrow(() -> new NotFoundException("Giỏ hàng không tồn tại"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống!");
        }

        // Validate tồn kho & Tính Subtotal
        // Lưu ý: Tính toán bằng BigDecimal phải cẩn thận scale
        BigDecimal subTotal = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();
            
            // Check tồn kho
            if (variant.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm " + variant.getProduct().getName()
                        + " (Size: " + variant.getSize() + ") không đủ số lượng!");
            }
            
            // Cộng dồn tiền: Giá * Số lượng
            BigDecimal lineTotal = variant.getBasePrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            subTotal = subTotal.add(lineTotal);
        }

        // --- BƯỚC 2: XỬ LÝ COUPON ---
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new NotFoundException("Mã giảm giá không tồn tại!"));

            // Validate logic Coupon phức tạp hơn
            validateCoupon(coupon, subTotal);

            // Tính toán giảm giá
            if (coupon.getDiscountType() == DiscountTypeEnum.PERCENTAGE) {
                // Công thức: SubTotal * (Value / 100)
                // FIX LỖI: Phải có RoundingMode để tránh lỗi chia số lẻ vô hạn
                discountAmount = subTotal.multiply(coupon.getDiscountValue())
                        .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP); // Làm tròn tiền VNĐ

                // FIX LỖI: Kiểm tra Max Discount (Giảm 10% nhưng tối đa 50k)
                if (coupon.getMaxDiscount() != null && discountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
                    discountAmount = coupon.getMaxDiscount();
                }

            } else if (coupon.getDiscountType() == DiscountTypeEnum.FIXED_AMOUNT) {
                discountAmount = coupon.getDiscountValue();
            }

            // Đảm bảo không giảm quá giá trị đơn hàng
            if (discountAmount.compareTo(subTotal) > 0) {
                discountAmount = subTotal;
            }
        }

        // --- BƯỚC 3: PHÍ SHIP ---
        BigDecimal shippingFee = shippingService.calculateFee(request.getProvinceCode(), cart);

        // --- BƯỚC 4: TẠO ORDER & TRỪ KHO ---
        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setCreatedAt(Instant.now()); // Thêm ngày đặt

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();

            OrderItem snapshotItem = new OrderItem();
            snapshotItem.setOrder(order);
            snapshotItem.setVariant(variant);
            snapshotItem.setQuantity(item.getQuantity());
            snapshotItem.setPriceAtPurchase(variant.getBasePrice());
            
            // OPTION: Tính luôn thành tiền của từng item để dễ thống kê sau này
            // snapshotItem.setTotalPrice(snapshotItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));

            orderItems.add(snapshotItem);

            // TRỪ TỒN KHO TRỰC TIẾP (Không query lại DB để tối ưu)
            // Hibernate sẽ tự động update dòng này do đang trong Transaction
            variant.setQuantity(variant.getQuantity() - item.getQuantity());
            // variantRepository.save(variant); // Không cần thiết nếu entity đang managed, nhưng gọi cũng không sao
        }

        order.setListOrderItems(orderItems);

        // Tổng cuối = (SubTotal - Discount) + Ship
        // Dùng max(0) để an toàn
        BigDecimal finalTotal = subTotal.subtract(discountAmount).add(shippingFee);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) finalTotal = BigDecimal.ZERO;

        order.setTotalPrice(subTotal);
        order.setDiscountAmount(discountAmount);
        order.setShippingFee(shippingFee);
        order.setFinalPrice(finalTotal);
        order.setStatus(OrderStatusEnum.PENDING);
        
        // Lưu Coupon Usage (Nếu có dùng coupon)
        if (coupon != null) {
            // Giảm số lượng coupon còn lại
            if (coupon.getUsageLimit() != null && coupon.getUsageLimit() > 0) {
                 coupon.setUsageLimit(coupon.getUsageLimit() - 1);
                 // couponRepository.save(coupon); // Managed entity tự update
            }
            // TODO: Lưu lịch sử dùng coupon vào bảng order_coupons nếu cần
        }

        // Lưu đơn hàng (Cascade ALL sẽ lưu luôn OrderItems)
        Order savedOrder = orderRepository.save(order);

        // Xóa giỏ hàng
        // Lưu ý: Nếu CartItem có quan hệ chặt chẽ, nên dùng method remove item
        cartRepository.deleteAllByCart_CartId(cartId); 
        // Hoặc: cartRepository.delete(cart); tùy thiết kế DB

        return orderMapper.toOrderResponse(savedOrder);
    }

    // Tách hàm validate coupon để code gọn hơn
    private void validateCoupon(Coupon coupon, BigDecimal orderValue) {
        Instant now = Instant.now();
        
        if (coupon.getExpiresAt().isBefore(now)) {
            throw new RuntimeException("Mã giảm giá đã hết hạn!");
        }
        
        if (coupon.getStartsAt() != null && coupon.getStartsAt().isAfter(now)) {
            throw new RuntimeException("Mã giảm giá chưa đến đợt áp dụng!");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageLimit() <= 0) {
             throw new RuntimeException("Mã giảm giá đã hết lượt sử dụng!");
        }

        if (orderValue.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new RuntimeException("Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã này!");
        }
    }
}