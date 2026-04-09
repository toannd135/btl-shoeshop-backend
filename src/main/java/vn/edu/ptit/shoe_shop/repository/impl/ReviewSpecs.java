package vn.edu.ptit.shoe_shop.repository.impl;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.entity.Review;

import java.math.BigDecimal;
import java.util.UUID;
public class ReviewSpecs {

    public static Specification<Review> ratingEquals(Integer rating) {
        return (root, query, cb) ->
                rating == null ? cb.conjunction()
                        : cb.equal(root.get("rating"), rating);
    }

    public static Specification<Review> productIdEquals(UUID productId) {
        return (root, query, cb) -> {
            if (productId == null) return cb.conjunction();

            Join<Review, ProductVariant> variant = root.join("variant");
            Join<ProductVariant, Product> product = variant.join("product");

            return cb.equal(product.get("productId"), productId);
        };
    }

    public static Specification<Review> variantIdEquals(UUID variantId) {
        return (root, query, cb) ->
                variantId == null ? cb.conjunction()
                        : cb.equal(root.get("variant").get("productVariantId"), variantId);
    }

    public static Specification<Review> sizeEquals(BigDecimal size) {
        return (root, query, cb) -> {
            if (size == null) return cb.conjunction();
            Join<Review, ProductVariant> variant = root.join("variant");
            return cb.equal(variant.get("size"), size);
        };
    }

    public static Specification<Review> colorEquals(String color) {
        return (root, query, cb) -> {
            if (color == null || color.isBlank()) return cb.conjunction();
            Join<Review, ProductVariant> variant = root.join("variant");
            return cb.equal(variant.get("color"), color);
        };
    }

    public static Specification<Review> keywordInNote(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("note")), "%" + keyword.toLowerCase() + "%");
        };
    }
}