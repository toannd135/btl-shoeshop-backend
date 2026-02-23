package vn.edu.ptit.shoe_shop.entity;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import vn.edu.ptit.shoe_shop.constant.enums.DiscountTypeEnum;
import vn.edu.ptit.shoe_shop.constant.enums.StatusEnum;

@Entity
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "coupon_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID couponId;

    @Column(length = 50,nullable = false)
    private String code;

    @Column(length = 255,nullable = false)
    private String name;

     @Column(name = "discount_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountTypeEnum discountType;

    @Column(name = "discount_value",precision = 15,scale = 0)
    private BigDecimal discountValue;

    @Column(name = "min_order_value",precision = 15,scale = 0)
    private BigDecimal minOrderValue;

    @Column(name = "max_discount",precision = 15,scale = 0)
    private BigDecimal maxDiscount;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "starts_at")
    private Instant startsAt;
    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEnum status;


    @OneToMany(mappedBy = "coupon",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> listOrders = new ArrayList<>();


    @PrePersist
    public void handleBeforeCreate() {
        this.startsAt = Instant.now();
        if (this.status == null) {
            this.status = StatusEnum.ACTIVE;
        }
    }


    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }
    

    public List<Order> getListOrders() {
        return listOrders;
    }


    public void setListOrders(List<Order> listOrders) {
        this.listOrders = listOrders;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public DiscountTypeEnum getDiscountType() {
        return discountType;
    }


    public void setDiscountType(DiscountTypeEnum discountType) {
        this.discountType = discountType;
    }


    public BigDecimal getDiscountValue() {
        return discountValue;
    }


    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }


    public BigDecimal getMinOrderValue() {
        return minOrderValue;
    }


    public void setMinOrderValue(BigDecimal minOrderValue) {
        this.minOrderValue = minOrderValue;
    }


    public BigDecimal getMaxDiscount() {
        return maxDiscount;
    }


    public void setMaxDiscount(BigDecimal maxDiscount) {
        this.maxDiscount = maxDiscount;
    }


    public Integer getUsageLimit() {
        return usageLimit;
    }


    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }


    public Instant getStartsAt() {
        return startsAt;
    }


    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }


    public Instant getExpiresAt() {
        return expiresAt;
    }


    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }


    public StatusEnum getStatus() {
        return status;
    }


    public void setStatus(StatusEnum status) {
        this.status = status;
    }


    public UUID getCouponId() {
        return couponId;
    }


    // public List<OrderCoupon> getListOrderCoupons() {
    //     return listOrderCoupons;
    // }


    // public void setListOrderCoupons(List<OrderCoupon> listOrderCoupons) {
    //     this.listOrderCoupons = listOrderCoupons;
    // }
    
}
