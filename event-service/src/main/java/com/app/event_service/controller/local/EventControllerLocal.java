package com.app.event_service.controller.local;

import com.app.event_service.entity.Seat;
import com.app.event_service.repository.SeatRepository;
import com.app.ticket_common_library.common.exception.AppException;
import com.app.ticket_common_library.common.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventControllerLocal {
    SeatRepository seatRepository;

    @GetMapping("/{id}")
    public Seat findSeatById(@PathVariable Integer id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NO_EXISTS));
    }

    @GetMapping("/lock/{id}")
    public Seat findSeatForUpdate(@PathVariable Integer id) {
        return seatRepository.findSeatForUpdate(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NO_EXISTS));
    }

    @GetMapping("/price/{id}")
    public Integer findPriceBySeatId(@PathVariable Integer id) {
        return seatRepository.findPriceBySeatId(id);
    }

    @PostMapping
    public void save(Seat seat) {
        seatRepository.save(seat);
    }

    @GetMapping("/exists/{id}")
    public boolean existsById(@PathVariable Integer id) {
        return seatRepository.existsById(id);
    }


}
