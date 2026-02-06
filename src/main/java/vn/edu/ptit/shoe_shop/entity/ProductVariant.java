package vn.edu.ptit.shoe_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Entity
@Table(name ="product_variant")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productVariantId;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private Double size;

    @Column(nullable = false)
    private Integer stockQuantity;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.REFRESH,CascadeType.PERSIST})
    @JoinColumn(name = "product_id")
    private Product product;

}
