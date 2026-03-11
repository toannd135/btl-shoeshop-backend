package vn.edu.ptit.shoe_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import vn.edu.ptit.shoe_shop.common.Auditable;
import vn.edu.ptit.shoe_shop.common.enums.ITEnum;
import vn.edu.ptit.shoe_shop.common.enums.ITStatusEnum;

import java.sql.Types;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventory_transactions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryTransaction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "it_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    UUID itId;

    @Column(nullable = false)
    Integer quantityChange;

    @Column(nullable = false)
    ITEnum type;

    @Column(nullable = false)
    String reason;

    @Column(nullable = false)
    ITStatusEnum status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    ProductVariant variant;

    @Override
    public void onCreate()
    {
        super.onCreate();

        if(status == null)
            {
            status = ITStatusEnum.PENDING;
            }
    }
}
