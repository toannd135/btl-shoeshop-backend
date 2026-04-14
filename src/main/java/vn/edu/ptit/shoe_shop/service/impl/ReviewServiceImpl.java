package vn.edu.ptit.shoe_shop.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.ptit.shoe_shop.dto.request.ReviewRequest;
import vn.edu.ptit.shoe_shop.dto.response.ReviewResponse;
import vn.edu.ptit.shoe_shop.dto.response.page.ReviewPageResponseDTO;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.entity.Review;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.OrderRepository;
import vn.edu.ptit.shoe_shop.repository.ProductVariantRepository;
import vn.edu.ptit.shoe_shop.repository.ReviewRepository;
import vn.edu.ptit.shoe_shop.repository.UserRepository;
import vn.edu.ptit.shoe_shop.repository.impl.ReviewSpecs;
import vn.edu.ptit.shoe_shop.service.ReviewService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderRepository orderRepository;

    private User getCurrentUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public ReviewResponse createReview(UUID variantId, ReviewRequest request) {

        User user = getCurrentUser();

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        boolean hasPurchased = orderRepository
                .hasUserPurchasedVariant(user.getUserId(), variantId);

        if (!hasPurchased) {
            throw new RuntimeException("You must purchase this variant before reviewing");
        }

        if (reviewRepository.existsByUser_UserIdAndVariant_ProductVariantId(
                user.getUserId(), variantId)) {
            throw new RuntimeException("You already reviewed this variant");
        }

        Review review = Review.builder()
                .user(user)
                .variant(variant)
                .rating(request.getRating())
                .note(request.getNote())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(UUID reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        User user = getCurrentUser();

        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized");
        }

        reviewRepository.delete(review);
    }

    @Transactional
    public void adminDeleteReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepository.delete(review);
    }

    @Transactional
    public List<ReviewResponse> getMyReviews() {

        User user = getCurrentUser();

        return reviewRepository.findByUser_UserId(user.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ReviewPageResponseDTO searchReviews(Integer rating,
                                               UUID productId,
                                               UUID variantId,
                                               BigDecimal size,
                                               String color,
                                               String keyword,
                                               Pageable pageable) {

        Pageable pageableToUse = normalizePageable(pageable);

        Specification<Review> spec = Specification.allOf(
                ReviewSpecs.ratingEquals(rating),
                ReviewSpecs.productIdEquals(productId),
                ReviewSpecs.variantIdEquals(variantId),
                ReviewSpecs.sizeEquals(size),
                ReviewSpecs.colorEquals(color),
                ReviewSpecs.keywordInNote(keyword)
        );

        Page<Review> reviewPage = reviewRepository.findAll(spec, pageableToUse);

        List<ReviewResponse> items = reviewPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        ReviewPageResponseDTO response = new ReviewPageResponseDTO();

        response.setItems(items);
        response.setPage(reviewPage.getNumber() + 1);
        response.setPageSize(reviewPage.getSize());
        response.setTotal(reviewPage.getTotalElements());
        response.setPages(reviewPage.getTotalPages());

        return response;
    }

    private Pageable normalizePageable(Pageable pageable) {

        int defaultPage = 0;
        int defaultSize = 5;

        Sort defaultSort = Sort.by("createdAt").descending();

        if (pageable == null) {
            return PageRequest.of(defaultPage, defaultSize, defaultSort);
        }

        int page = pageable.getPageNumber() < 0 ? defaultPage : pageable.getPageNumber();
        int size = pageable.getPageSize() <= 0 ? defaultSize : pageable.getPageSize();

        Sort sort = pageable.getSort().isUnsorted() ? defaultSort : pageable.getSort();

        return PageRequest.of(page, size, sort);
    }

    private ReviewResponse toResponse(Review review) {

        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUser().getUserId())
                .userFirstName(review.getUser().getFirstName())
                .userLastName(review.getUser().getLastName())
                .variantId(review.getVariant().getProductVariantId())
                .productName(review.getVariant().getProduct().getName())
                .rating(review.getRating())
                .note(review.getNote())
                .createdAt(review.getCreatedAt())
                .build();
    }
}