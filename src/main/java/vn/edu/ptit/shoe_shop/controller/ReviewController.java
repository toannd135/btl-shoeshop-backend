package vn.edu.ptit.shoe_shop.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.ptit.shoe_shop.dto.request.ReviewRequest;
import vn.edu.ptit.shoe_shop.dto.response.ReviewResponse;
import vn.edu.ptit.shoe_shop.dto.response.page.ReviewPageResponseDTO;
import vn.edu.ptit.shoe_shop.service.ReviewService;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/reviews")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {

    ReviewService reviewService;

    @PostMapping("/variants/{variantId}")
    @ApiMessage("Review created successfully")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable UUID variantId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response = reviewService.createReview(variantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{reviewId}")
    @ApiMessage("Review deleted successfully")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{reviewId}")
    @ApiMessage("Review deleted by admin successfully")
    public ResponseEntity<Void> adminDeleteReview(@PathVariable UUID reviewId) {
        reviewService.adminDeleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-reviews")
    @ApiMessage("My reviews retrieved successfully")
    public ResponseEntity<List<ReviewResponse>> getMyReviews() {
        return ResponseEntity.ok(reviewService.getMyReviews());
    }

    @GetMapping("/search")
    @ApiMessage("Reviews filtered and paginated successfully")
    public ResponseEntity<ReviewPageResponseDTO> searchReviews(
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID variantId,
            @RequestParam(required = false) BigDecimal size,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int sizePerPage,
            @RequestParam(defaultValue = "createdAt") String sort
    ) {
        Pageable pageable = PageRequest.of(page - 1, sizePerPage, Sort.by(sort).descending());
        ReviewPageResponseDTO response = reviewService.searchReviews(
                rating, productId, variantId, size, color, keyword, pageable
        );
        return ResponseEntity.ok(response);
    }
}