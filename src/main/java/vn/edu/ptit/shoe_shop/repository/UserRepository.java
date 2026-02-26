package vn.edu.ptit.shoe_shop.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.dto.UserCredentialDTO;
import vn.edu.ptit.shoe_shop.dto.response.TopCustomerDto;
import vn.edu.ptit.shoe_shop.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    @Query("SELECT new vn.edu.ptit.shoe_shop.dto.UserCredentialDTO(u.username, u.email) FROM User u")
    List<UserCredentialDTO> findAllUserCredentials();
    Optional<User> findByUserId(UUID userId);

    // 1. Đếm tổng khách hàng (Giả sử bạn có RoleEnum.USER để phân biệt với ADMIN)
    // long countByRole(RoleEnum role); 

    // 2. Đếm khách hàng đăng ký sau một mốc thời gian (Ví dụ: 30 ngày qua)
    long countByCreatedAtAfter(Instant startDate);

    // 3. Đếm số khách hàng ĐÃ TỪNG mua hàng thành công (Bỏ qua khách đăng ký nhưng chưa mua)
    @Query(value = "SELECT COUNT(DISTINCT o.user_id) FROM orders o WHERE o.status = 'DELIVERED'", nativeQuery = true)
    long countCustomersWithSuccessfulOrders();

    // 4. Tìm Top khách hàng chi tiêu nhiều nhất (Khách VIP)
    @Query(value = "SELECT u.user_id AS userId, " +
                   "u.name AS fullName, " +
                   "u.email AS email, " +
                   "u.phone AS phone, " +
                   "COUNT(o.order_id) AS totalOrders, " +
                   "SUM(o.final_price) AS totalSpent " +
                   "FROM users u " +
                   "JOIN orders o ON u.user_id = o.user_id " +
                   "WHERE o.status = 'DELIVERED' " +
                   "GROUP BY u.user_id, u.name, u.email, u.phone " +
                   "ORDER BY totalSpent DESC ", nativeQuery = true)
    List<TopCustomerDto> getTopSpendingCustomers(@Param("limit") int limit);

}
