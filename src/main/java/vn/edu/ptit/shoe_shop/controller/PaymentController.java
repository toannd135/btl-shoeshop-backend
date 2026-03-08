package vn.edu.ptit.shoe_shop.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import vn.edu.ptit.shoe_shop.common.enums.OrderStatusEnum;
import vn.edu.ptit.shoe_shop.common.enums.PaymentMethodEnum;
import vn.edu.ptit.shoe_shop.common.enums.PaymentStatusEnum;
import vn.edu.ptit.shoe_shop.common.exception.IdInvalidException;
import vn.edu.ptit.shoe_shop.config.VNPayConfig;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import vn.edu.ptit.shoe_shop.dto.response.PaymentResponse;
import vn.edu.ptit.shoe_shop.dto.response.VNPayPaymentResponse;
import vn.edu.ptit.shoe_shop.entity.Order;
import vn.edu.ptit.shoe_shop.entity.OrderItem;
import vn.edu.ptit.shoe_shop.entity.OrderPayment;
import vn.edu.ptit.shoe_shop.entity.ProductVariant;
import vn.edu.ptit.shoe_shop.repository.OrderPaymentRepository;
import vn.edu.ptit.shoe_shop.repository.OrderRepository;
import vn.edu.ptit.shoe_shop.service.impl.OrderPaymentService;

@RestController
@RequestMapping ("api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderRepository orderRepository;
    private final OrderPaymentRepository paymentRepository;
    private final OrderPaymentService orderPaymentService;

    // @PreAuthorize("isAuthenticated()")
    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestParam("orderId") String orderId,
            @RequestParam("paymentMethod") String paymentMethod, // "VNPAY" hoặc "CASH"
            @RequestParam(value = "bankCode", defaultValue = "NCB") String bankCode,
            HttpServletRequest request) {
        UUID orderIdUUID;
        try {
            orderIdUUID = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        try {
            
            Order order = orderRepository.findByOrderId(UUID.fromString(orderId))
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

            // Kiểm tra nếu order đã có payment
            Optional<OrderPayment> existingPayment = paymentRepository.findByOrder(order);
            if (existingPayment.isPresent()) {
                OrderPayment payment = existingPayment.get();
                PaymentResponse response = PaymentResponse.builder()
                        .id(payment.getOrderPaymentId().toString())
                        .orderId(order.getOrderId().toString())
                        .paymentMethod(payment.getPaymentMethod().toString())
                        .amount(payment.getAmount())
                        .status(payment.getPaymentStatus().toString())
                        .build();
                return ResponseEntity.ok(response);
            }

            // Xử lý theo payment method
            if (PaymentMethodEnum.VNPAY.name().equalsIgnoreCase(paymentMethod)) {
                return handleVNPayPayment(order, bankCode, request);
            } else if (PaymentMethodEnum.COD.name().equalsIgnoreCase(paymentMethod)) {
                return handleCashPayment(order);
            } else {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Invalid payment method")
                );
            }


        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Payment creation failed: " + e.getMessage())
            );
        }
    }

    // XỬ LÝ VNPAY PAYMENT
    private ResponseEntity<?> handleVNPayPayment(Order order, String bankCode, HttpServletRequest request)
            throws UnsupportedEncodingException {

        BigDecimal amount = order.getFinalPrice();
        long vnp_Amount = amount.longValue() * 100;
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPayConfig.getIpAddress(request); // Sửa IP thực

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.version);
        vnp_Params.put("vnp_Command", VNPayConfig.command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + order.getOrderId().toString() + " Ma: " + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.returnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build query URL
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext();) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);

            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if (itr.hasNext()) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnpSecretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        String paymentUrl = VNPayConfig.baseUrl + "?" + query.toString();

        OrderPayment payment = OrderPayment.builder()
                .order(order)
                .paymentMethod(PaymentMethodEnum.VNPAY)
                .amount(amount)
                .vnpayTransactionRef(vnp_TxnRef)
                .paymentStatus(PaymentStatusEnum.PENDING)
                .createdAt(Instant.now())
                .build();
        paymentRepository.save(payment);

        VNPayPaymentResponse paymentResponse = new VNPayPaymentResponse();
        paymentResponse.setCode("00");
        paymentResponse.setMessage("Success");
        paymentResponse.setPaymentUrl(paymentUrl);
        paymentResponse.setTransactionRef(vnp_TxnRef);

        return ResponseEntity.ok().body(paymentResponse);
    }

    private ResponseEntity<?> handleCashPayment(Order order) {
        OrderPayment payment = OrderPayment.builder()
                .order(order)
                .paymentMethod(PaymentMethodEnum.COD)
                .amount(order.getFinalPrice())
                .paymentStatus(PaymentStatusEnum.PENDING)
                .createdAt(Instant.now())
                .build();
        paymentRepository.save(payment);

        PaymentResponse response = PaymentResponse.builder()
                .id(payment.getOrderPaymentId().toString())
                .orderId(order.getOrderId().toString())
                .paymentMethod("CASH")
                .amount(payment.getAmount())
                .status(PaymentStatusEnum.UNPAID.name())
                .build();

        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @PostMapping("/cash/confirm")
    public ResponseEntity<?> confirmCashPayment(@RequestParam("orderId") String orderId) {
        UUID orderIdUUID;
        try {
            orderIdUUID = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            throw new IdInvalidException("Id không đúng định dạng!");
        }
        Order order = orderRepository.findByOrderId(orderIdUUID)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderPayment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Payment not found for order"));

        if (!PaymentMethodEnum.COD.equals(payment.getPaymentMethod())) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Payment method is not CASH")
            );
        }

        payment.setPaymentStatus(PaymentStatusEnum.COMPLETED);
        payment.setPaidAt(Instant.now());
        paymentRepository.save(payment);

        order.setStatus(OrderStatusEnum.CONFIRMED);
        orderRepository.save(order);

        PaymentResponse response = PaymentResponse.builder()
                .id(payment.getOrderPaymentId().toString())
                .orderId(order.getOrderId().toString())
                .paymentMethod("CASH")
                .amount(payment.getAmount())
                .status(PaymentStatusEnum.COMPLETED.name())
                .build();

        return ResponseEntity.ok().body(response);
    }

    @Transactional
    @GetMapping("/payment_infor")
    public ResponseEntity<?> paymentInfor(
            @RequestParam Map<String, String> allParams) {

        String responseCode = allParams.get("vnp_ResponseCode");
        String vnpayTransactionRef = allParams.get("vnp_TxnRef");
        String transactionNo = allParams.get("vnp_TransactionNo");

        ApiResponse vnpayApiResponse = new ApiResponse<>();

        try {
            OrderPayment payment = paymentRepository.findByVnpayTransactionRef(vnpayTransactionRef)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            Order order = payment.getOrder();

            if ("00".equals(responseCode)) {
                // THANH TOÁN THÀNH CÔNG
                payment.setPaymentStatus(PaymentStatusEnum.COMPLETED);
                payment.setTransactionId(transactionNo);
                payment.setPaidAt(Instant.now());
                payment.setResponseData(allParams.toString());
                paymentRepository.save(payment);

                order.setStatus(OrderStatusEnum.CONFIRMED);
                orderRepository.save(order);

                vnpayApiResponse.setStatusCode(HttpStatus.OK.value());
                vnpayApiResponse.setMessage("Thanh toán thành công! Đơn hàng #" + order.getOrderId().toString() + " đã được xác nhận.");

            } else {
                // THANH TOÁN THẤT BẠI - HUỶ ORDER
                payment.setPaymentStatus(PaymentStatusEnum.FAILED);
                payment.setResponseData(allParams.toString());
                paymentRepository.save(payment);
                cancelOrderAndRestoreStock(order);

                vnpayApiResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                vnpayApiResponse.setMessage("Thanh toán thất bại! Đơn hàng #" + order.getOrderId().toString() + " đã bị huỷ. Mã lỗi: " + responseCode);
            }

        } catch (Exception e) {
            vnpayApiResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            vnpayApiResponse.setMessage("Lỗi xử lý: " + e.getMessage());
        }

        return ResponseEntity.ok().body(vnpayApiResponse);
    }

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<?> updatePaymentStatus(
            @PathVariable String paymentId,
                @RequestParam PaymentStatusEnum status) {  // URL: /api/payment/10/status?status=COMPLETED

                    PaymentResponse paymentResponse = orderPaymentService.updatePaymentStatus(paymentId, status);
                    return ResponseEntity.ok(paymentResponse);
    }

    // HUỶ ORDER, TRẢ LẠI STOCK
    private void cancelOrderAndRestoreStock(Order order) {
        try {
            // Trả lại stock cho từng sản phẩm
            for (OrderItem detail : order.getListOrderItems()) {
                ProductVariant variation = detail.getVariant();
                variation.setQuantity(variation.getQuantity() + detail.getQuantity());
            }

            // Cập nhật order status thành CANCELED
            order.setStatus(OrderStatusEnum.CANCELLED);
            orderRepository.save(order);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi huỷ order #" + order.getOrderId().toString(), e);
        }
    }
}