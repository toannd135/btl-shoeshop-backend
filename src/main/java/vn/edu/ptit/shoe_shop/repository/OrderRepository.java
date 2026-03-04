package vn.edu.ptit.shoe_shop.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,UUID> {
    Optional<Order> findByOrderId(UUID orderId);
    // 1. Lấy tất cả lịch sử đơn hàng của 1 user (Có phân trang)
    Page<Order> findAllByUser_UserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    // 2. Lấy đơn hàng theo trạng thái (Ví dụ: Đang giao, Đã hủy)
    Page<Order> findAllByUser_UserIdAndStatusOrderByCreatedAtDesc(UUID userId, OrderStatusEnum status, Pageable pageable);

    // 3. Tìm chính xác 1 đơn hàng của 1 user (Dùng cho Chi tiết & Hủy đơn)
    Optional<Order> findByOrderIdAndUser_UserId(UUID orderId, UUID userId);

    // 4. Lọc động cho Admin (Tìm theo trạng thái, số điện thoại, và khoảng thời gian)
    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:phone IS NULL OR o.receiverPhone LIKE %:phone%) AND " +
           "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR o.createdAt <= :endDate) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> searchOrdersForAdmin(@Param("status") OrderStatusEnum status,
                                     @Param("phone") String phone,
                                     @Param("startDate") Instant startDate,
                                     @Param("endDate") Instant endDate,
                                     Pageable pageable);

    // 5. Dành cho Export (Lấy toàn bộ data không phân trang dựa trên filter)
    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR o.createdAt <= :endDate) " +
           "ORDER BY o.createdAt DESC")
    List<Order> getOrdersForExport(@Param("status") OrderStatusEnum status,
                                   @Param("startDate") Instant startDate,
                                   @Param("endDate") Instant endDate);
}
