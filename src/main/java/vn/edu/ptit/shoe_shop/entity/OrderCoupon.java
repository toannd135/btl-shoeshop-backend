// package vn.edu.ptit.shoe_shop.entity;

// import java.sql.Types;
// import java.util.UUID;

// import org.hibernate.annotations.JdbcTypeCode;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;

// @Entity
// @Table(name = "order_coupons")
// public class OrderCoupon {
//     @Id
//     @GeneratedValue(strategy = GenerationType.UUID)
//     @Column(name = "order_coupon_id", columnDefinition = "CHAR(36)")
//     @JdbcTypeCode(Types.VARCHAR)
//     private UUID orderCouponId;

//     @ManyToOne
//     @JoinColumn(name = "order_id",nullable = false)
//     private Order order;

//     @ManyToOne
//     @JoinColumn(name = "coupon_id",nullable = false)
//     private Coupon coupon;

//     public Order getOrder() {
//         return order;
//     }

//     public void setOrder(Order order) {
//         this.order = order;
//     }

//     public Coupon getCoupon() {
//         return coupon;
//     }

//     public void setCoupon(Coupon coupon) {
//         this.coupon = coupon;
//     }

    
// }
