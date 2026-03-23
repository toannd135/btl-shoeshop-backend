package vn.edu.ptit.shoe_shop.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.entity.Product;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    List<ProductVariant> findByProduct(Product product);

    Optional<ProductVariant> findByProductVariantIdAndProduct(UUID id, Product product);

    Optional<ProductVariant> findByProductVariantId(UUID variantId);

    List<ProductVariant> findByQuantityLessThan(int quantity);
}
