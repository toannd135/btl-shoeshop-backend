package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.dto.response.RevenueReportDto;
import vn.edu.ptit.shoe_shop.dto.response.TopProductDto;
import vn.edu.ptit.shoe_shop.entity.Order;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Order, UUID> {

    // 1. Thống kê doanh thu theo từng ngày (Sử dụng Native Query vì JPQL xử lý Date
    // khó hơn)
    // Lưu ý: Hàm DATE() dùng cho MySQL/PostgreSQL. Nếu dùng SQL Server sẽ khác đôi
    // chút.
    @Query(value = "SELECT DATE(o.created_at) AS reportDate, " +
            "SUM(o.final_price) AS totalRevenue, " +
            "COUNT(o.order_id) AS totalOrders " +
            "FROM orders o " +
            "WHERE o.status = 'DELIVERED' " +
            "AND o.created_at >= :startDate AND o.created_at <= :endDate " +
            "GROUP BY DATE(o.created_at) " +
            "ORDER BY reportDate ASC", nativeQuery = true)
    List<RevenueReportDto> getDailyRevenue(@Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    // 2. Thống kê Top Sản Phẩm Bán Chạy (Join giữa Order và OrderItem)
    @Query(value = """
    SELECT p.product_id AS productId,
           p.name AS productName,
           SUM(oi.quantity) AS totalSold,
           SUM(oi.quantity * oi.price_at_purchase) AS totalRevenue
    FROM order_items oi
    JOIN orders o ON oi.order_id = o.order_id
    JOIN product_variants pv ON oi.variant_id = pv.product_variant_id
    JOIN products p ON pv.product_id = p.product_id
    WHERE o.status = 'DELIVERED'
      AND o.created_at >= :startDate
      AND o.created_at <= :endDate
    GROUP BY p.product_id, p.name
    ORDER BY totalSold DESC
""", nativeQuery = true)
List<TopProductDto> getTopSellingProducts(
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate,
        Pageable pageable);
}