package vn.edu.ptit.shoe_shop.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,UUID> {
    Optional<Coupon> findByCode(String code);
    boolean existsByCode(String code);

    // Thêm hàm update hàng loạt này:
    @Modifying
    @Query("UPDATE Coupon c SET c.status = :expiredStatus " +
           "WHERE c.status = :activeStatus AND c.expiresAt <= :now")
    int updateStatusForExpiredCoupons(@Param("expiredStatus") StatusEnum expiredStatus,
                                      @Param("activeStatus") StatusEnum activeStatus,
                                      @Param("now") Instant now);
}
