package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.entity.Supplier;
import vn.edu.ptit.shoe_shop.entity.SupplierVariant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierVariantRepository extends JpaRepository<SupplierVariant, UUID> {
    @Query("""
       SELECT sv
       FROM SupplierVariant sv
       JOIN FETCH sv.variant
       WHERE sv.supplier = :supplier
       """)
    List<SupplierVariant> findBySupplierFetchVariant(Supplier supplier);

    @Query("""
       SELECT sv.variant.productVariantId
       FROM SupplierVariant sv
       WHERE sv.supplier.supplierId = :supplierId
       """)
    List<UUID> findVariantIdsBySupplierId(UUID supplierId);

    List<SupplierVariant> findBySupplierAndVariantIn(Supplier supplier, List<ProductVariant> variant);

    Optional<SupplierVariant> findBySupplierAndVariant(Supplier supplier, ProductVariant variant);

    Optional<SupplierVariant> findBySupplier_SupplierIdAndVariant_ProductVariantId(UUID supplierId, UUID variantId);
}
