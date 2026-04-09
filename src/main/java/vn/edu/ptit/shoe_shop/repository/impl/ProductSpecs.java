package vn.edu.ptit.shoe_shop.repository.impl;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class ProductSpecs {

    public static Specification<Product> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String like = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("brand")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    public static Specification<Product> priceBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;

            Join<Product, ProductVariant> variant = root.join("productVariants");

            if (min != null && max != null)
                return cb.between(variant.get("basePrice"), min, max);

            if (min != null)
                return cb.greaterThanOrEqualTo(variant.get("basePrice"), min);

            return cb.lessThanOrEqualTo(variant.get("basePrice"), max);
        };
    }

    public static Specification<Product> variationSizeIn(List<BigDecimal> sizes) {
        return (root, query, cb) -> {
            if (sizes == null || sizes.isEmpty()) return null;

            Join<Product, ProductVariant> variant = root.join("productVariants");
            return variant.get("size").in(sizes);
        };
    }

    public static Specification<Product> variationColorIn(List<String> colors) {
        return (root, query, cb) -> {
            if (colors == null || colors.isEmpty()) return null;

            Join<Product, ProductVariant> variant = root.join("productVariants");
            return variant.get("color").in(colors);
        };
    }

    public static Specification<Product> categoryIdEquals(UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;

            return cb.equal(root.get("category").get("categoryId"), categoryId);
        };
    }

    public static Specification<Product> genderEquals(GenderEnum gender) {
        return (root, query, cb) -> {
            if (gender == null) return null;

            return cb.equal(root.get("gender"), gender);
        };
    }

    public static Specification<Product> brandEquals(String brand) {
        return (root, query, cb) -> {
            if (brand == null || brand.isBlank()) return null;

            return cb.equal(cb.lower(root.get("brand")), brand.toLowerCase());
        };
    }
}