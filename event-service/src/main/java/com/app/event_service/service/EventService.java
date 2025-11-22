package com.app.event_service.service;

import com.app.event_service.dto.request.EventRequest;
import com.app.event_service.dto.request.SeatStatusRequest;
import com.app.event_service.dto.response.EventResponse;
import com.app.event_service.entity.Event;
import com.app.event_service.entity.Seat;
import com.app.event_service.mapper.EventMapper;
import com.app.event_service.repository.EventRepository;
import com.app.event_service.repository.SeatRepository;
import com.app.event_service.repository.client.AuthClient;
import com.app.ticket_common_library.common.enums.SeatStatus;
import com.app.ticket_common_library.common.exception.AppException;
import com.app.ticket_common_library.common.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    AuthClient authClient;

    public Event findById(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NO_EXISTS));
    }

    @Cacheable(value = "events", keyGenerator = "pageableKeyGenerator")
    public List<Event> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable).getContent();
    }


    @Cacheable(value = "events", keyGenerator = "pageableKeyGenerator")
    public List<Event> findAllByOrganizerId(String id, Pageable pageable) {
        authClient.checkUserToken(id);
        return eventRepository.findAllByOrganizerId(id, pageable).getContent();
    }


    @Cacheable(value = "event", keyGenerator = "simpleKeyGenerator")
    public EventResponse getDetail(Integer id) {
        var event = findById(id);
        var seats = seatRepository.findAllByEventId(id);
        EventResponse response = eventMapper.toEventResponse(event);
        response.setSeats(seats);
        return response;
    }

    @Transactional
    @CacheEvict(value = "events", allEntries = true)
    public EventResponse create(EventRequest request) {
        authClient.checkUserToken(request.getOrganizerId());
        List<Seat> seats = new ArrayList<>();
        Event event = eventRepository.save(eventMapper.toEvent(request));
        for (int i = 0; i < request.getTotalSeats(); i++) {
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

    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public Event update(Integer id, EventRequest request) {
        authClient.checkUserToken(request.getOrganizerId());
        Event event = findById(id);
        request.setTotalSeats(event.getTotalSeats());
        eventMapper.updateEventFromRequest(event, request);
        return eventRepository.save(event);
    }

    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public void delete(Integer id) {
        Event event = findById(id);
        authClient.checkUserToken(event.getOrganizerId());
        eventRepository.deleteById(id);
    }

    @CacheEvict(value = {"events", "event"}, allEntries = true)
    public Seat updateStatusSeats(Integer seatId, SeatStatusRequest request) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NO_EXISTS));
        Event event = findById(seat.getEventId());
        authClient.checkUserToken(event.getOrganizerId());

        seat.setStatus(request.getStatus());
        return seatRepository.save(seat);
    }


}
