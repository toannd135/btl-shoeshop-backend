package vn.edu.ptit.shoe_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import vn.edu.ptit.shoe_shop.common.Auditable;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name ="products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID productId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private GenderEnum gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST
    )
    private List<ProductVariant> productVariants = new ArrayList<>();

}