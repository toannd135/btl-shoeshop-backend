package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.entity.OrderPayment;

@Repository
public interface PaymentMethodRepository extends JpaRepository<OrderPayment,UUID>{
    Optional<OrderPayment> findByOrderPaymentId(UUID orderPaymentId);
}
