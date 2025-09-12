package com.app.booking.internal.payment_service.controller;

import com.app.booking.common.ApiResponse;
import com.app.booking.config.VNPay.VNPayConfig;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.payment_service.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PaymentController {
    PaymentService paymentService;
    @GetMapping("/public")
    public ApiResponse<List<Payment>> getAll(Pageable pageable) {
        return ApiResponse.<List<Payment>>builder()
                .result(paymentService.findAll(pageable))
                .build();
    }

    @GetMapping("/public/user/{userId}")
    public ApiResponse<List<Payment>> findAllByOrganizerId(@PathVariable String userId, Pageable pageable) {
        return ApiResponse.<List<Payment>>builder()
                .result(paymentService.findAllByOrganizerId(userId, pageable))
                .build();
    }

    @PostMapping("/public/{ticketId}")
    public ApiResponse<Payment> create(@PathVariable Integer ticketId) {
        return ApiResponse.<Payment>builder()
                .result(paymentService.create(ticketId))
                .build();
    }

    VNPayService vnPayService;

    @PostMapping("/create")
    public Map<String, Object> createPayment(
            @RequestParam("amount") int orderTotal,
            @RequestParam("orderInfo") String orderInfo,
            HttpServletRequest request) {

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, baseUrl);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("paymentUrl", vnpayUrl);
        return response;
    }

    @GetMapping("/vnpay-return")
    public Map<String, Object> paymentReturn(HttpServletRequest request) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderInfo);
        response.put("totalPrice", totalPrice);
        response.put("paymentTime", paymentTime);
        response.put("transactionId", transactionId);
        response.put("status", paymentStatus == 1 ? "SUCCESS" : "FAILED");

        return response;
    }
}
