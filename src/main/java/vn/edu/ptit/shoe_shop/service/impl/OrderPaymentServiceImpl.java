package vn.edu.ptit.shoe_shop.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.edu.ptit.shoe_shop.common.enums.PaymentStatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.common.exception.NotFoundException;
import vn.edu.ptit.shoe_shop.dto.response.PaymentResponse;
import vn.edu.ptit.shoe_shop.entity.OrderPayment;
import vn.edu.ptit.shoe_shop.repository.OrderPaymentRepository;
@Service
@RequiredArgsConstructor
public class OrderPaymentServiceImpl implements OrderPaymentService {
    private final OrderPaymentRepository paymentRepository;
    public PaymentResponse updatePaymentStatus(String paymentId, PaymentStatusEnum status) {
        UUID orderPaymentId;
        try {
            orderPaymentId = UUID.fromString(paymentId);
        } catch (IllegalArgumentException e) {
            throw new IdInvalidException("Id không đúng định dạng!");
        }
            OrderPayment payment = paymentRepository.findByOrderPaymentId(orderPaymentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy thanh toán!"));

        payment.setPaymentStatus(status);
        OrderPayment updatedPayment = paymentRepository.save(payment);

        return PaymentResponse.builder()
                .id(updatedPayment.getOrderPaymentId().toString())
                .orderId(updatedPayment.getOrder().getOrderId().toString())
                .paymentMethod(updatedPayment.getPaymentMethod().toString())
                .amount(updatedPayment.getAmount())
                .status(updatedPayment.getPaymentStatus().toString())
                .transactionId(updatedPayment.getTransactionId())
                .createdAt(updatedPayment.getCreatedAt())
                .paidAt(updatedPayment.getPaidAt())
                .build();
    }
}
