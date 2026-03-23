package vn.edu.ptit.shoe_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import vn.edu.ptit.shoe_shop.common.Auditable;
import vn.edu.ptit.shoe_shop.common.enums.SupplierStatusEnum;

import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "suppliers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Supplier extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "supplier_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    UUID supplierId;

    @Column(nullable = false)
    String supplierName;

    @Column(nullable = false)
    String address;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SupplierStatusEnum status;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    private List<PurchaseOrder> purchaseOrders;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    List<SupplierVariant> supplierVariants;

    protected void onCreate() {
        super.onCreate();

        if (this.status == null) {
            this.status = SupplierStatusEnum.ENABLED;
        }
    }
}
