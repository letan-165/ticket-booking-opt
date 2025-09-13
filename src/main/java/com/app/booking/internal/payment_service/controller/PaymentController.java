package com.app.booking.internal.payment_service.controller;

import com.app.booking.common.ApiResponse;
import com.app.booking.config.VNPay.VNPayConfig;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.entity.TransactionPay;
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
    VNPayService vnPayService;

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

    @GetMapping("/vnpay/return")
    public ApiResponse<TransactionPay> paymentReturn(HttpServletRequest request) {
        return ApiResponse.<TransactionPay>builder()
                .result(paymentService.paid(request))
                .build();
    }
}
