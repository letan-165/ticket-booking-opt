package com.app.payment_service.controller.local;

import com.app.payment_service.dto.response.PaymentBookingResponse;
import com.app.payment_service.entity.Payment;
import com.app.payment_service.service.PaymentService;
import com.app.payment_service.service.VNPayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentControllerLocal {
    PaymentService paymentService;
    VNPayService vnPayService;

    @PostMapping("/booking/{price}/{seatNumber}")
    public PaymentBookingResponse booking(@PathVariable Integer price, @PathVariable String seatNumber) {
        return paymentService.booking(price, seatNumber);
    }

    @GetMapping("/{ticketId}")
    public Payment findByTicketId(@PathVariable Integer ticketId) {
        return paymentService.findByTicketId(ticketId);
    }

    @PutMapping("/{ticketId}/{paymentId}")
    public Payment update(@PathVariable Integer paymentId, @PathVariable Integer ticketId) {
        return paymentService.update(paymentId, ticketId);
    }

    @PutMapping("/status/{ticketId}/{status}")
    public Payment updateStatus(@PathVariable Integer paymentId, @PathVariable boolean status) {
        return paymentService.updateStatus(paymentId, status);
    }


}
