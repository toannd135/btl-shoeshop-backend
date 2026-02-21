package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.entity.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,UUID> {
    Optional<Coupon> findByCode(String code);
}
