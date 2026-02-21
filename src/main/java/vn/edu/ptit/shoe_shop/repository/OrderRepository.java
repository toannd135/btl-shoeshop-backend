package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.constant.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,UUID> {
    // 1. Lấy tất cả lịch sử đơn hàng của 1 user (Có phân trang)
    Page<Order> findAllByUser_UserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // 2. Lấy đơn hàng theo trạng thái (Ví dụ: Đang giao, Đã hủy)
    Page<Order> findAllByUser_UserIdAndStatusOrderByCreatedAtDesc(UUID userId, OrderStatusEnum status, Pageable pageable);

    // 3. Tìm chính xác 1 đơn hàng của 1 user (Dùng cho Chi tiết & Hủy đơn)
    Optional<Order> findByOrderIdAndUser_UserId(UUID orderId, UUID userId);
}
