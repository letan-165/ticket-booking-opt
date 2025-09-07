package com.app.booking.internal.payment_service.controller;

import com.app.booking.common.ApiResponse;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                .result(paymentService.findAll(pageable).getContent())
                .build();
    }

    @GetMapping("/public/user/{userId}")
    public ApiResponse<List<Payment>> findAllByOrganizerId(@PathVariable String userId, Pageable pageable) {
        return ApiResponse.<List<Payment>>builder()
                .result(paymentService.findAllByOrganizerId(userId, pageable).getContent())
                .build();
    }

    @PostMapping("/public/{ticketId}")
    public ApiResponse<Payment> create(@PathVariable Integer ticketId) {
        return ApiResponse.<Payment>builder()
                .result(paymentService.create(ticketId))
                .build();
    }

    @PatchMapping("/public/pay/{paymentID}/{isSuccess}")
    public ApiResponse<Payment> pay(@PathVariable Integer paymentID,@PathVariable Boolean isSuccess) {
        return ApiResponse.<Payment>builder()
                .result(paymentService.pay(paymentID,isSuccess))
                .build();
    }
}
