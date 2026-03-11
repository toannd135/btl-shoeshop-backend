package vn.edu.ptit.shoe_shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.entity.POItem;
import vn.edu.ptit.shoe_shop.entity.PurchaseOrder;
import vn.edu.ptit.shoe_shop.entity.SupplierVariant;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface POItemRepository extends JpaRepository<POItem, UUID> {
    Optional<POItem> findByPurchaseOrderAndVariant(PurchaseOrder po, SupplierVariant supplierVariant);

}
