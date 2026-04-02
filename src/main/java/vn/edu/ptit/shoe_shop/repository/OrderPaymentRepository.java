package vn.edu.ptit.shoe_shop.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.common.enums.PaymentMethodEnum;
import vn.edu.ptit.shoe_shop.common.enums.PaymentStatusEnum;
import vn.edu.ptit.shoe_shop.entity.Order;
import vn.edu.ptit.shoe_shop.entity.OrderPayment;

@Repository
public interface OrderPaymentRepository extends JpaRepository<OrderPayment, UUID> {
    Optional<OrderPayment> findByOrder(Order order);
    // Tìm kiếm theo id
    Optional<OrderPayment> findByOrderPaymentId(UUID orderPaymentId);

    // Tìm kiếm theo trạng thái, phương thức thanh toán và thời gian tạo lớn hơn 1
    // giờ
    List<OrderPayment> findByPaymentStatusAndPaymentMethodAndCreatedAtBefore(
            PaymentStatusEnum status,
            PaymentMethodEnum paymentMethod,
            Instant createdDate);
    // Tìm payment bằng VNPay transaction ref
    Optional<OrderPayment> findByVnpayTransactionRef(String vnpayTransactionRef);
}
