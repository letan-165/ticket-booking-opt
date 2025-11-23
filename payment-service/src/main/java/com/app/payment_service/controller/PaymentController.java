package com.app.payment_service.controller;

import com.app.payment_service.entity.Payment;
import com.app.payment_service.entity.TransactionPay;
import com.app.payment_service.service.PaymentService;
import com.app.payment_service.service.VNPayService;
import com.app.ticket_common_library.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;
    VNPayService vnPayService;

    @NonFinal
    @Value("${vnp.feBackUrl}")
    String feBackUrl;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/public")
    public ApiResponse<List<Payment>> getAll(Pageable pageable) {
        return ApiResponse.<List<Payment>>builder()
                .result(paymentService.findAll(pageable))
                .build();
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @GetMapping("/public/organizer/{organizerId}")
    public ApiResponse<List<Payment>> findAllByOrganizerId(@PathVariable String organizerId, Pageable pageable) {
        return ApiResponse.<List<Payment>>builder()
                .result(paymentService.findAllByOrganizerId(organizerId, pageable))
                .build();
    }

    @GetMapping("/vnpay/return")
    public void paymentReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        TransactionPay result = paymentService.paid(request);
        boolean status = result.getResponseCode().equals("00");
        String redirectUrl = feBackUrl + "?paymentId=" + result.getTxnRef() + "&status=" + status;
        response.sendRedirect(redirectUrl);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/public/retry/{ticketId}")
    public ApiResponse<String> retryPay(@PathVariable Integer ticketId) {
        return ApiResponse.<String>builder()
                .result(paymentService.retryPay(ticketId))
                .build();
    }


}
