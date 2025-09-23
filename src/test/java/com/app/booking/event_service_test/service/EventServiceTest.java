package com.app.booking.event_service_test.service;

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
import com.app.booking.internal.event_service.service.EventService;
import com.app.booking.internal.user_service.service.UserService;
import com.app.booking.common.model_mock.EntityMock;
import com.app.booking.common.model_mock.RequestMock;
import com.app.booking.common.model_mock.ResponseMock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class EventServiceTest {
    @Autowired
    @InjectMocks
    EventService eventService;

    @Mock EventRepository eventRepository;
    @Mock SeatRepository seatRepository;
    @Mock EventMapper eventMapper;
    @Mock UserService userService;

    Integer eventId = 1;
    Event event;
    EventResponse eventResponse;

    @BeforeEach
    void initData(){
        event = EntityMock.eventMock();
        eventResponse = ResponseMock.eventMock();
    }

    @Test
    void getDetail_success(){
        when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event));
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        EventResponse response = eventService.getDetail(eventId);
        verify(seatRepository).findAllByEventId(eventId);

        assertThat(response).isEqualTo(eventResponse);
    }

    @Test
    void getDetail_fail_EVENT_NO_EXISTS(){
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        var exception = assertThrows(AppException.class,
                ()-> eventService.getDetail(eventId));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.EVENT_NO_EXISTS);
    }

    @Test
    void create_success(){
        int totalSeat = 10;
        EventRequest request = RequestMock.eventMock(totalSeat);

        when(eventMapper.toEvent(request)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toEventResponse(event)).thenReturn(eventResponse);

        EventResponse response = eventService.create(request);

        verify(userService).userIsExist(eq(request.getOrganizerId()));
        ArgumentCaptor<List<Seat>> captor = ArgumentCaptor.forClass(List.class);
        verify(seatRepository).saveAll(captor.capture());
        var seats = captor.getValue();

        assertThat(seats.size()).isEqualTo(totalSeat);
        assertThat(response).isEqualTo(eventResponse);
    }

    @Test
    void update_success(){
        EventRequest request = Mockito.mock(EventRequest.class);

        when(eventRepository.findById(eventId)).thenReturn(Optional.ofNullable(event));
        when(eventRepository.save(event)).thenReturn(event);

        Event response = eventService.update(eventId,request );

        verify(request).setTotalSeats(eq(event.getTotalSeats()));
        verify(eventMapper).updateEventFromRequest(eq(event),eq(request));

        assertThat(response).isEqualTo(event);
    }

    @Test
    void updateStatusSeats_success(){
        Seat seat = new Seat();
        int seatId = 1;
        seat.setId(seatId);

        SeatStatusRequest request = SeatStatusRequest.builder()
                .status(SeatStatus.LOCKED)
                .build();

        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(seatRepository.save(seat)).thenReturn(seat);

        Seat response = eventService.updateStatusSeats(seatId,request);

        assertThat(seat.getStatus()).isEqualTo(SeatStatus.LOCKED);
        assertThat(response).isEqualTo(seat);
    }

    @Test
    void updateStatusSeats_fail_SEAT_NO_EXISTS(){
        Seat seat = new Seat();
        int seatId = 1;
        seat.setId(seatId);

        SeatStatusRequest request = SeatStatusRequest.builder()
                .status(SeatStatus.LOCKED)
                .build();

        when(seatRepository.findById(1)).thenReturn(Optional.empty());
        var exception = assertThrows(AppException.class,
                ()-> eventService.updateStatusSeats(1, request));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SEAT_NO_EXISTS);
    }
}
