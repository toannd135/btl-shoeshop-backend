package vn.edu.ptit.shoe_shop.entity;

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
import vn.edu.ptit.shoe_shop.constant.enums.ProductStatusEnum;

@Entity
@Table(name = "product_variants")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "product_variant_id")
    private UUID productVariantId;

    @Column(length = 100, unique = true)
    private String sku;
    @Column(name = "size", precision = 4)
    private BigDecimal size;

    @Column(name = "size_system", length = 10)
    private String sizeSystem;

    @Column(length = 50)
    private String color;

    @Column(name = "color_code", length = 20)
    private String colorCode;

    @Column(name = "price_override", precision = 15, scale = 0)
    private BigDecimal priceOverride;

    private Integer quantity;

    @Column(name = "weight_gram")
    private Integer weightGram;

    @Enumerated(EnumType.STRING)
    private ProductStatusEnum status;

    // 1 bien the product co the la nhieu cartitem
    @JsonIgnore
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    // nhieu bien the co the thuoc ve 1 product
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 1 product_variant co the co nhieu anh
    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> listProductVariantImages = new ArrayList<>();

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> listOrderItems = new ArrayList<>();
    // @Column(name = "status", nullable = false)
    // @Enumerated(EnumType.STRING)
    // private StatusEnum status;

    // @Column(name = "createdAt", nullable = false)
    // private Instant createdAt;

    // @Column(name = "createdBy", nullable = false)
    // private String createdBy;

    // @Column(name = "updatedAt")
    // private Instant updatedAt;

    // @Column(name = "updatedBy")
    // private String updatedBy;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "role_id", nullable = false)
    // private Role role;

    // @PrePersist
    // public void handleBeforeCreate() {
    // this.createdBy = String.valueOf(this.userId);
    // this.createdAt = Instant.now();
    // this.updatedAt = this.createdAt;
    // if (this.status == null) {
    // this.status = StatusEnum.ACTIVE;
    // }
    // }

    // @PreUpdate
    // public void handleBeforeUpdate() {
    // this.createdBy = String.valueOf(this.userId);
    // this.updatedAt = Instant.now();
    // }
}
