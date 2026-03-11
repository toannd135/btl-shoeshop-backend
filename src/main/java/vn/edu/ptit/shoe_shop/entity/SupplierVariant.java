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
@Table(name = "supplier_variants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"supplier_id", "variant_id"}))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sup_var_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    UUID supVarId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id",nullable = false)
    ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "supplier_id",nullable = false)
    Supplier supplier;

    @Column(nullable = false)
    BigDecimal cost;

    String note;
}
