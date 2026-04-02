package vn.edu.ptit.shoe_shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class VNPayPaymentResponse {
    private String code;
    private String message;
    private String paymentUrl;
    private String transactionRef;
}