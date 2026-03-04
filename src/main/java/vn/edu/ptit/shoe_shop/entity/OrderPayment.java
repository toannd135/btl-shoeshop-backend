package vn.edu.ptit.shoe_shop.entity;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import vn.edu.ptit.shoe_shop.common.enums.PaymentMethodEnum;
import vn.edu.ptit.shoe_shop.common.enums.PaymentStatusEnum;


@Entity
@Table(name = "order_payments")
@Builder
public class OrderPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_payment_id", columnDefinition = "CHAR(36)")
    @JdbcTypeCode(Types.VARCHAR)
    private UUID orderPaymentId;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", precision = 15, scale = 0)
    private BigDecimal amount;

    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum paymentStatus;

    @Column(name = "vnpay_transaction_ref")
    private String vnpayTransactionRef;
    @Column(name = "paid_at")
    private Instant paidAt;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(columnDefinition = "TEXT")
    private String responseData;
    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        if (this.paymentStatus == null) {
            this.paymentStatus = PaymentStatusEnum.UNPAID;
        }
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public UUID getOrderPaymentId() {
        return orderPaymentId;
    }

  

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getVnpayTransactionRef() {
        return vnpayTransactionRef;
    }

    public void setVnpayTransactionRef(String vnpayTransactionRef) {
        this.vnpayTransactionRef = vnpayTransactionRef;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    

}
