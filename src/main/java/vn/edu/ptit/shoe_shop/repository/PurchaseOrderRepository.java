package vn.edu.ptit.shoe_shop.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.entity.PurchaseOrder;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT po FROM PurchaseOrder po 
        WHERE po.poId = :id
    """)
    Optional<PurchaseOrder> findByIdWithLock( UUID id);


    @Query("""
        SELECT po FROM PurchaseOrder po
        LEFT JOIN FETCH po.listPOItems poi
        LEFT JOIN FETCH poi.variant sv
        LEFT JOIN FETCH sv.variant
        WHERE po.poId = :id
    """)
    Optional<PurchaseOrder> findByIdWithDetails( UUID id);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT po FROM PurchaseOrder po
        LEFT JOIN FETCH po.listPOItems
        WHERE po.poId = :id
    """)
    Optional<PurchaseOrder> findByIdWithLockAndItems(UUID id);


    @Query(
            value = """
            select po from PurchaseOrder po
            join fetch po.supplier
        """,
            countQuery = """
            select count(po) from PurchaseOrder po
        """
    )
    Page<PurchaseOrder> findAllWithSupplier(Pageable pageable);
}
