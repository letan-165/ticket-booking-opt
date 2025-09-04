package com.app.booking.internal.event_service.service;

import com.app.booking.common.enums.SeatStatus;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.event_service.dto.request.EventRequest;
import com.app.booking.internal.event_service.dto.request.SeatStatusRequest;
import com.app.booking.internal.event_service.dto.response.EventResponse;
import com.app.booking.internal.event_service.entity.Event;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.mapper.EventMapper;
import com.app.booking.internal.event_service.repository.EventRepository;
import com.app.booking.internal.event_service.repository.SeatRepository;
import com.app.booking.internal.user_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventService {
    EventRepository eventRepository;
    SeatRepository seatRepository;
    EventMapper eventMapper;
    UserService userService;

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NO_EXISTS));
    }

    public Page<Event> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Page<Event> findAllByOrganizerId(String id,Pageable pageable) {
        userService.userIsExist(id);
        return eventRepository.findAllByOrganizerId(id,pageable);
    }


    public EventResponse getDetail(Long id) {
        var event = findById(id);

        var seats = seatRepository.findAllByEventId(id);
        EventResponse response = eventMapper.toEventResponse(event);
        response.setSeats(seats);
        return response;
    }

    //Transition
    public EventResponse create(EventRequest request) {
        userService.userIsExist(request.getOrganizerId());
        List<Seat> seats= new ArrayList<>();
        Event event = eventRepository.save(eventMapper.toEvent(request));
        for(int i = 0; i < request.getTotalSeats(); i++  ){
            seats.add(Seat.builder()
                            .eventId(event.getId())
                            .status(SeatStatus.AVAILABLE)
                            .seatNumber("G" + i)
                    .build());
        }
        var resSeats = seatRepository.saveAll(seats);
        EventResponse response = eventMapper.toEventResponse(event);
        response.setSeats(resSeats);
        return response;
    }

    //Update totalSeats
    public Event update(Long id,EventRequest request) {
        Event event = findById(id);
        userService.userIsExist(request.getOrganizerId());
        eventMapper.updateEventFromRequest(event, request);
        return eventRepository.save(event);
    }

    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new AppException(ErrorCode.EVENT_NO_EXISTS);
        }
        eventRepository.deleteById(id);
    }

    public Seat updateStatusSeats(Long seatId, SeatStatusRequest request){
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(()->new AppException(ErrorCode.SEAT_NO_EXISTS));

        seat.setStatus(request.getStatus());
        return seatRepository.save(seat);
    }



}
