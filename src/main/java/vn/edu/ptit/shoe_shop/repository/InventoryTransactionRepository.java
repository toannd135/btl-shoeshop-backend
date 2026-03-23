package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.common.enums.ITEnum;
import vn.edu.ptit.shoe_shop.entity.InventoryTransaction;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {
    @Query("""
        SELECT it FROM InventoryTransaction it
        WHERE (:variantId IS NULL OR it.variant.productVariantId = :variantId)
        AND (:type IS NULL OR it.type = :type)
        AND (:fromDate IS NULL OR it.createdAt >= :fromDate)
        AND (:toDate IS NULL OR it.createdAt <= :toDate)
    """)
    List<InventoryTransaction> search(UUID variantId, ITEnum type, Instant fromDate, Instant toDate);
}
