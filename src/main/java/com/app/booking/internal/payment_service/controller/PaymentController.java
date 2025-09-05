package com.app.booking.internal.payment_service.controller;

import com.app.booking.common.ApiResponse;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.ticket_service.entity.Ticket;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PaymentController {
    PaymentService paymentService;
    @GetMapping("/public")
    public ApiResponse<Page<Payment>> getAll(Pageable pageable) {
        return ApiResponse.<Page<Payment>>builder()
                .result(paymentService.findAll(pageable))
                .build();
    }

    @GetMapping("/public/user/{userId}")
    public ApiResponse<Page<Payment>> findAllByOrganizerId(@PathVariable String userId, Pageable pageable) {
        return ApiResponse.<Page<Payment>>builder()
                .result(paymentService.findAllByOrganizerId(userId, pageable))
                .build();
    }

    @PostMapping("/public/{ticketId}")
    public ApiResponse<Payment> create(@PathVariable Integer ticketId) {
        return ApiResponse.<Payment>builder()
                .result(paymentService.create(ticketId))
                .build();
    }

}
