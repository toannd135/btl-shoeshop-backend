package vn.edu.ptit.shoe_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="po_items",uniqueConstraints = @UniqueConstraint(
        columnNames = {"po_detail_id","sup_var_id"}
))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class POItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "po_detail_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    UUID poDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id", nullable = false)
    PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sup_var_id", nullable = false)
    SupplierVariant variant;

    @Column(nullable = false)
    Integer quantity;
}
