package vn.edu.ptit.shoe_shop.service;

import org.springframework.data.domain.Pageable;
import vn.edu.ptit.shoe_shop.dto.request.ReviewRequest;
import vn.edu.ptit.shoe_shop.dto.response.ReviewResponse;
import vn.edu.ptit.shoe_shop.dto.response.page.ReviewPageResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(UUID variantId, ReviewRequest request);

    void deleteReview(UUID reviewId);

    List<ReviewResponse> getMyReviews();

    ReviewPageResponseDTO searchReviews(Integer rating,
                                        UUID productId,
                                        UUID variantId,
                                        String size,
                                        String color,
                                        String keyword,
                                        Pageable pageable);
}
