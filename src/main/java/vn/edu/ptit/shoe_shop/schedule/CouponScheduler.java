package vn.edu.ptit.shoe_shop.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.shoe_shop.constant.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.repository.CouponRepository;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j // Dùng để in log ra console
public class CouponScheduler {

    private final CouponRepository couponRepository;

    /**
     * Hàm này sẽ tự động chạy vào lúc 00:00:00 (nửa đêm) mỗi ngày.
     * Giải thích biểu thức Cron ("0 0 0 * * ?"):
     * - 0: Giây thứ 0
     * - 0: Phút thứ 0
     * - 0: Giờ thứ 0 (Nửa đêm)
     * - *: Mỗi ngày trong tháng
     * - *: Mỗi tháng
     * - ?: Bất kỳ ngày nào trong tuần
     * Bạn có thể thêm múi giờ: zone = "Asia/Ho_Chi_Minh" nếu server của bạn chạy giờ UTC.
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    @Transactional // Bắt buộc phải có khi dùng @Modifying query
    public void scanAndExpireCoupons() {
        log.info("Bắt đầu quét và cập nhật các mã giảm giá đã hết hạn...");
        
        Instant now = Instant.now();
        
        // Cập nhật tất cả các mã đang ACTIVE và có thời gian hết hạn <= hiện tại thành EXPIRED
        int updatedCount = couponRepository.updateStatusForExpiredCoupons(
                StatusEnum.EXPIRED,
                StatusEnum.ACTIVE,
                now
        );
        
        log.info("Quét hoàn tất! Đã chuyển đổi trạng thái thành EXPIRED cho {} mã giảm giá.", updatedCount);
    }
    
    /* * (Tùy chọn) Để test ngay lập tức mà không cần đợi đến nửa đêm,
     * bạn có thể uncomment đoạn code dưới đây để nó chạy mỗi 1 phút:
     */
    // @Scheduled(cron = "0 * * * * ?") 
    // @Transactional
    // public void testScanEveryMinute() { ... }
}