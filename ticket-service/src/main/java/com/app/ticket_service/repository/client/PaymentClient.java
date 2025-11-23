package com.app.ticket_service.repository.client;

import com.app.ticket_common_library.config.security.FeignConfig;
import com.app.ticket_service.dto.Payment;
import com.app.ticket_service.dto.response.PaymentBookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "payment-client", url = "${app.service.payment}" + "/payments", configuration = FeignConfig.class)
public interface PaymentClient {
    @PostMapping("/booking/{price}/{seatNumber}")
    PaymentBookingResponse booking(@PathVariable Integer price, @PathVariable String seatNumber);

    @GetMapping("/{ticketId}")
    Payment findByTicketId(@PathVariable Integer ticketId);

    @PutMapping("/{ticketId}/{paymentId}")
    Payment update(@PathVariable Integer paymentId, @PathVariable Integer ticketId);

    @PutMapping("/status/{ticketId}/{status}")
    Payment updateStatus(@PathVariable Integer paymentId, @PathVariable boolean status);
}
