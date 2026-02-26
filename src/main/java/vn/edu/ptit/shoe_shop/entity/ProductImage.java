package vn.edu.ptit.shoe_shop.entity;

import java.sql.Types;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_images")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "product_image_id")
    private UUID productImageId;
    @Column(name = "image_url",length = 255)
    private String imageUrl;
    @Column(name = "is_primary")
    private Boolean isPrimary;
    @Column(name = "display_order")
    private Integer displayOrder;
    // nhieu anh co the cung 1 product
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    // nhieu anh co the cung 1 product_variant
    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    private ProductVariant productVariant;

}
