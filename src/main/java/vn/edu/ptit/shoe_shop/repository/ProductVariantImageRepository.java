package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.entity.ProductVariantImage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantImageRepository extends JpaRepository<ProductVariantImage, UUID> {
    List<ProductVariantImage> findByProductVariant(ProductVariant productVariant);

    Optional<ProductVariantImage> findByImageIdAndProductVariant(UUID imageId, ProductVariant productVariant);
    Optional<ProductVariantImage> findByProductVariantAndIsPrimaryTrue(ProductVariant variant);
}
