package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.edu.ptit.shoe_shop.entity.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant,UUID> {
    Optional<ProductVariant> findByProductVariantId(UUID variantId);
}
