package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.edu.ptit.shoe_shop.entity.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,UUID> {
    Optional<ProductVariant> findByProductVariantId(UUID variantId);
}
