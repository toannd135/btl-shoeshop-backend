package vn.edu.ptit.shoe_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import vn.edu.ptit.shoe_shop.common.Auditable;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "purchase_orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseOrder extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "po_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    UUID poId;

    LocalDateTime expectedDeliveryDate;

    String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    OrderStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    Supplier supplier;

    @OneToMany(mappedBy = "purchaseOrder",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    List<POItem> listPOItems  = new ArrayList<>();

    @Override
    protected void onCreate() {
        super.onCreate();

        if (this.status == null) {
            this.status = OrderStatusEnum.PENDING;
        }
    }

}
