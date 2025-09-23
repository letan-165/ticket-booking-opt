package com.app.booking.internal.payment_service.controller;

import com.app.booking.common.ApiResponse;
import com.app.booking.config.VNPay.VNPayConfig;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.entity.TransactionPay;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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

    @NonFinal
    @Value("${vnp.feBackUrl}")
    String feBackUrl;

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
    public void  paymentReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TransactionPay result = paymentService.paid(request);
        boolean status = result.getResponseCode().equals("00");
        String redirectUrl = feBackUrl + "?paymentId=" + result.getTxnRef() + "&status=" + status;
        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/public/retry/{ticketId}")
    public ApiResponse<String> retryPay(@PathVariable Integer ticketId) {
        return ApiResponse.<String>builder()
                .result(paymentService.retryPay(ticketId))
                .build();
    }



}
