package com.app.booking.internal.ticket_service.controller;

import com.app.booking.common.model.response.ApiResponse;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.ticket_service.dto.response.TicketDetailResponse;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.service.TicketService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketController {
    TicketService ticketService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/public")
    public ApiResponse<List<Ticket>> getAll(Pageable pageable) {
        return ApiResponse.<List<Ticket>>builder()
                .result(ticketService.findAll(pageable))
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/public/user/{userId}")
    public ApiResponse<List<Ticket>> findAllByUserId(@PathVariable String userId, Pageable pageable) {
        return ApiResponse.<List<Ticket>>builder()
                .result(ticketService.findAllByUserId(userId, pageable))
                .build();
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @GetMapping("/public/organizer/{organizerId}")
    public ApiResponse<List<Ticket>> findAllByOrganizerId(@PathVariable String organizerId, Pageable pageable) {
        return ApiResponse.<List<Ticket>>builder()
                .result(ticketService.findAllByOrganizerId(organizerId, pageable))
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/public/{ticketId}")
    public ApiResponse<TicketDetailResponse> getDetail(@PathVariable Integer ticketId) {
        return ApiResponse.<TicketDetailResponse>builder()
                .result(ticketService.getDetail(ticketId))
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/public/book")
    public ApiResponse<String> booking(@Valid @RequestBody BookRequest request) {
        return ApiResponse.<String>builder()
                .result(ticketService.booking(request))
                .build();
    }
}
