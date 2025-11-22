package com.app.event_service.controller;

import com.app.event_service.dto.request.EventRequest;
import com.app.event_service.dto.request.SeatStatusRequest;
import com.app.event_service.dto.response.EventResponse;
import com.app.event_service.entity.Event;
import com.app.event_service.entity.Seat;
import com.app.event_service.service.EventService;
import com.app.ticket_common_library.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventController {
    EventService eventService;

    @GetMapping("/public/guest")
    public ApiResponse<List<Event>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.<List<Event>>builder()
                .result(eventService.getAll(pageable))
                .build();
    }

    @GetMapping("/public/guest/{id}")
    public ApiResponse<EventResponse> getDetail(@PathVariable Integer id) {
        return ApiResponse.<EventResponse>builder()
                .result(eventService.getDetail(id))
                .build();
    }

    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @GetMapping("/public/organizers/{id}")
    public ApiResponse<List<Event>> findAllByOrganizerId(@PathVariable String id, @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.<List<Event>>builder()
                .result(eventService.findAllByOrganizerId(id, pageable))
                .build();
    }

    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @PostMapping("/public")
    public ApiResponse<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ApiResponse.<EventResponse>builder()
                .result(eventService.create(request))
                .build();
    }

    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @PatchMapping("/public/{id}")
    public ApiResponse<Event> update(@PathVariable Integer id, @RequestBody EventRequest request) {
        return ApiResponse.<Event>builder()
                .result(eventService.update(id, request))
                .build();
    }

    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @DeleteMapping("/public/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Integer id) {
        eventService.delete(id);
        return ApiResponse.<Boolean>builder()
                .result(true)
                .build();
    }

    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    @PatchMapping("/public/seats/{seatId}")
    public ApiResponse<Seat> updateStatusSeats(@PathVariable Integer seatId, @RequestBody SeatStatusRequest request) {
        return ApiResponse.<Seat>builder()
                .result(eventService.updateStatusSeats(seatId, request))
                .build();
    }


}
