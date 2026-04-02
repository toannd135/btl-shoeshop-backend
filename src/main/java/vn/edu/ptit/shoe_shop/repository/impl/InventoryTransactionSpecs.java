package vn.edu.ptit.shoe_shop.repository.impl;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import vn.edu.ptit.shoe_shop.common.enums.ITEnum;
import vn.edu.ptit.shoe_shop.entity.InventoryTransaction;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;

import java.time.Instant;
import java.util.UUID;

public class InventoryTransactionSpecs {

    public static Specification<InventoryTransaction> variantIdEquals(UUID variantId) {
        return (root, query, cb) -> {
            if (variantId == null) return null;
            Join<InventoryTransaction, ProductVariant> variant = root.join("variant");
            return cb.equal(variant.get("productVariantId"), variantId);
        };
    }

    public static Specification<InventoryTransaction> typeEquals(ITEnum type) {
        return (root, query, cb) -> {
            if (type == null) return null;
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<InventoryTransaction> fromDateGreaterThanOrEqual(Instant fromDate) {
        return (root, query, cb) -> {
            if (fromDate == null) return null;
            return cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
        };
    }

    public static Specification<InventoryTransaction> toDateLessThanOrEqual(Instant toDate) {
        return (root, query, cb) -> {
            if (toDate == null) return null;
            return cb.lessThanOrEqualTo(root.get("createdAt"), toDate);
        };
    }
}