package com.app.booking.internal.ticket_service.controller;

import com.app.booking.common.ApiResponse;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.ticket_service.dto.response.TicketDetailResponse;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TicketController {
    TicketService ticketService;

    @GetMapping("/public")
    public ApiResponse<Page<Ticket>> getAll(Pageable pageable) {
        return ApiResponse.<Page<Ticket>>builder()
                .result(ticketService.findAll(pageable))
                .build();
    }

    @GetMapping("/public/user/{userId}")
    public ApiResponse<Page<Ticket>> getByUser(@PathVariable String userId, Pageable pageable) {
        return ApiResponse.<Page<Ticket>>builder()
                .result(ticketService.findAllByUserId(userId, pageable))
                .build();
    }

    @GetMapping("/public/{ticketId}")
    public ApiResponse<TicketDetailResponse> getDetail(@PathVariable Integer ticketId) {
        return ApiResponse.<TicketDetailResponse>builder()
                .result(ticketService.getDetail(ticketId))
                .build();
    }

    @PostMapping("/public/book")
    public ApiResponse<Ticket> create(@Valid @RequestBody BookRequest request) {
        return ApiResponse.<Ticket>builder()
                .result(ticketService.create(request))
                .build();
    }

    @PatchMapping("/public/cancel/{ticketId}")
    public ApiResponse<Ticket> cancel(@PathVariable Integer ticketId) {
        return ApiResponse.<Ticket>builder()
                .result(ticketService.cancel(ticketId))
                .build();
    }

    @PatchMapping("/public/confirm/{ticketId}")
    public ApiResponse<Ticket> confirm(@PathVariable Integer ticketId) {
        return ApiResponse.<Ticket>builder()
                .result(ticketService.confirm(ticketId))
                .build();
    }
}
