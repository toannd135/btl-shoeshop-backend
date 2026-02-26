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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID orderId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


    @Column(name = "receiver_name",length = 100)
    private String receiverName;

    @Column(name = "province_code",length = 10)
    private String provinceCode;
    
    @Column(name = "receiver_phone",length = 11)
    private String receiverPhone;

    @Column(name = "shipping_address")
    private String shippingAddress;
    
    @Column(name = "total_price",precision = 15,scale = 0)
    private BigDecimal totalPrice;
   
    @Column(name = "discount_amount",precision = 15,scale = 0)
    private BigDecimal discountAmount;

    @Column(name = "shipping_fee",precision = 15,scale = 0)
    private BigDecimal shippingFee;
   
    @Column(name = "final_price",precision = 15,scale = 0)
    private BigDecimal finalPrice;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @Column(name = "note")
    private String note;

    @Column(name = "created_at")
    private Instant createdAt;


    // @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<OrderCoupon> listOrderCoupons= new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "coupon_id",nullable = true)
    private Coupon coupon;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> listOrderItems= new ArrayList<>();


    

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = OrderStatusEnum.PENDING;
        }
    }

     public List<OrderItem> getListOrderItems() {
        return listOrderItems;
    }

    
    public String getProvinceCode() {
        return provinceCode;
    }

     public void setProvinceCode(String provinceCode) {
         this.provinceCode = provinceCode;
     }

     public Coupon getCoupon() {
         return coupon;
     }

     public void setCoupon(Coupon coupon) {
         this.coupon = coupon;
     }

    public void setListOrderItems(List<OrderItem> listOrderItems) {
        this.listOrderItems = listOrderItems;
    }

     public User getUser() {
         return user;
     }

     public void setUser(User user) {
         this.user = user;
     }

     public String getReceiverName() {
         return receiverName;
     }

     public void setReceiverName(String receiverName) {
         this.receiverName = receiverName;
     }

     public String getReceiverPhone() {
         return receiverPhone;
     }

     public void setReceiverPhone(String receiverPhone) {
         this.receiverPhone = receiverPhone;
     }

     public String getShippingAddress() {
         return shippingAddress;
     }

     public void setShippingAddress(String shippingAddress) {
         this.shippingAddress = shippingAddress;
     }

     public BigDecimal getTotalPrice() {
         return totalPrice;
     }

     public void setTotalPrice(BigDecimal totalPrice) {
         this.totalPrice = totalPrice;
     }
     

    //  public List<OrderCoupon> getListOrderCoupons() {
    //     return listOrderCoupons;
    // }

    //  public void setListOrderCoupons(List<OrderCoupon> listOrderCoupons) {
    //      this.listOrderCoupons = listOrderCoupons;
    //  }

     public BigDecimal getDiscountAmount() {
         return discountAmount;
     }

     public void setDiscountAmount(BigDecimal discountAmount) {
         this.discountAmount = discountAmount;
     }

     public BigDecimal getShippingFee() {
         return shippingFee;
     }

     public void setShippingFee(BigDecimal shippingFee) {
         this.shippingFee = shippingFee;
     }

     public BigDecimal getFinalPrice() {
         return finalPrice;
     }

     public void setFinalPrice(BigDecimal finalPrice) {
         this.finalPrice = finalPrice;
     }

     public OrderStatusEnum getStatus() {
         return status;
     }

     public void setStatus(OrderStatusEnum status) {
         this.status = status;
     }

     public String getNote() {
         return note;
     }

     public void setNote(String note) {
         this.note = note;
     }

     public Instant getCreatedAt() {
         return createdAt;
     }

     public void setCreatedAt(Instant createdAt) {
         this.createdAt = createdAt;
     }

     public UUID getOrderId() {
         return orderId;
     }

    
}
