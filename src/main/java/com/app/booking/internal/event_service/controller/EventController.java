package com.app.booking.internal.event_service.controller;

import com.app.booking.common.ApiResponse;
import com.app.booking.common.enums.SeatStatus;
import com.app.booking.internal.event_service.dto.request.EventRequest;
import com.app.booking.internal.event_service.dto.request.SeatStatusRequest;
import com.app.booking.internal.event_service.dto.response.EventResponse;
import com.app.booking.internal.event_service.entity.Event;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.service.EventService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventController {
    EventService eventService;

    @GetMapping("/public")
    public ApiResponse<Page<Event>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.<Page<Event>>builder()
                .result(eventService.getAll(pageable))
                .build();
    }

    @GetMapping("/public/organizers/{id}")
    public ApiResponse<Page<Event>> findAllByOrganizerId(@PathVariable String id,@PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.<Page<Event>>builder()
                .result(eventService.findAllByOrganizerId(id,pageable))
                .build();
    }

    @GetMapping("/public/{id}")
    public ApiResponse<EventResponse> getDetail(@PathVariable Long id) {
        return ApiResponse.<EventResponse>builder()
                .result(eventService.getDetail(id))
                .build();
    }

    @PostMapping("/public")
    public ApiResponse<EventResponse> create(@Valid @RequestBody EventRequest request) {
        return ApiResponse.<EventResponse>builder()
                .result(eventService.create(request))
                .build();
    }

    @PatchMapping("/public/{id}")
    public ApiResponse<Event> update(@PathVariable Long id,@Valid @RequestBody EventRequest request) {
        return ApiResponse.<Event>builder()
                .result(eventService.update(id, request))
                .build();
    }

    @DeleteMapping("/public/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ApiResponse.<Boolean>builder()
                .result(true)
                .build();
    }

    @PatchMapping("/public/seats/{seatId}")
    public ApiResponse<Seat> updateStatusSeats(@PathVariable Long seatId, @RequestBody SeatStatusRequest request) {
        return ApiResponse.<Seat>builder()
                .result(eventService.updateStatusSeats(seatId,request))
                .build();
    }


}
