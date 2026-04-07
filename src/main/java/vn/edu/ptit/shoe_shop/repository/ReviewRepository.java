package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.edu.ptit.shoe_shop.entity.Review;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID>, JpaSpecificationExecutor<Review> {

    // Kiểm tra user đã review variant chưa
    boolean existsByUser_UserIdAndVariant_ProductVariantId(UUID userId, UUID variantId);

    // Lấy review của user hiện tại
    List<Review> findByUser_UserId(UUID userId);
}