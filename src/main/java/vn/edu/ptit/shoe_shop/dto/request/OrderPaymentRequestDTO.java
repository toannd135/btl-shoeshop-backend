package vn.edu.ptit.shoe_shop.dto.request;

import lombok.Setter;
import vn.edu.ptit.shoe_shop.common.enums.PaymentMethodEnum;
import vn.edu.ptit.shoe_shop.common.enums.PaymentStatusEnum;
import lombok.Getter;
import lombok.Builder;
@Setter
@Getter
@Builder
public class OrderPaymentRequestDTO {
    private String orderId;
    private PaymentMethodEnum paymentMethod;
    private PaymentStatusEnum paymentStatus;
    private String paymentAmount;
    private String paymentCurrency;
}
