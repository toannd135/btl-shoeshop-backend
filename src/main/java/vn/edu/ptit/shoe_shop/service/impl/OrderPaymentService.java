package vn.edu.ptit.shoe_shop.service.impl;


import vn.edu.ptit.shoe_shop.common.enums.PaymentStatusEnum;
import vn.edu.ptit.shoe_shop.dto.response.PaymentResponse;

public interface OrderPaymentService {
    public PaymentResponse updatePaymentStatus(String paymentId, PaymentStatusEnum status);
}
