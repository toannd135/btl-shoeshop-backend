package vn.edu.ptit.shoe_shop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import vn.edu.ptit.shoe_shop.common.Auditable;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.ptit.shoe_shop.common.enums.ProductStatusEnum;

@Entity
@Table(name ="product_variants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_variant_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID productVariantId;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private Double size;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer basePrice;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.REFRESH,CascadeType.PERSIST})
    @JoinColumn(name = "product_id")
    // 1 bien the product co the la nhieu cartitem
    @JsonIgnore
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    // nhieu bien the co the thuoc ve 1 product
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(
            mappedBy = "productVariant",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductVariantImage> productVariantImages = new ArrayList<>();
    // 1 product_variant co the co nhieu anh
    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> listProductVariantImages = new ArrayList<>();

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> listOrderItems = new ArrayList<>();

}
