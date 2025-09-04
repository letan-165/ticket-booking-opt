package com.app.booking.internal.ticket_service.service;

import com.app.booking.common.enums.SeatStatus;
import com.app.booking.common.enums.TicketStatus;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.repository.SeatRepository;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.ticket_service.dto.response.TicketDetailResponse;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.mapper.TicketMapper;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import com.app.booking.internal.user_service.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {
    TicketRepository ticketRepository;
    SeatRepository seatRepository;
    UserService userService;
    TicketMapper ticketMapper;


    public Ticket findById(Long ticketId){
        return ticketRepository.findById(ticketId)
                .orElseThrow(()-> new AppException(ErrorCode.TICKET_NO_EXISTS));
    }

    public Page<Ticket> findAll(Pageable pageable){
        return ticketRepository.findAll(pageable);
    }

    public Page<Ticket> findAllByUserId(String userId,Pageable pageable){
        userService.userIsExist(userId);
        return ticketRepository.findAllByUserId(userId, pageable);
    }

    public Ticket create(BookRequest request){
        userService.userIsExist(request.getUserId());
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(()-> new AppException(ErrorCode.SEAT_NO_EXISTS));

        if (!seat.getStatus().equals(SeatStatus.AVAILABLE))
            throw new AppException(ErrorCode.STATUS_UNABLE);

        Ticket ticket = Ticket.builder()
                .userId(request.getUserId())
                .seatId(request.getSeatId())
                .bookingTime(LocalDateTime.now())
                .status(TicketStatus.BOOKED)
                .build();

        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);
        return ticketRepository.save(ticket);
    }

    public TicketDetailResponse getDetail(Long ticketId){
        Ticket ticket = findById(ticketId);
        Seat seat = seatRepository.findById(ticket.getSeatId())
                .orElseThrow(()-> new AppException(ErrorCode.SEAT_NO_EXISTS));

        TicketDetailResponse response = ticketMapper.toTicketDetailResponse(ticket);
        response.setSeat(seat);
        return response;
    }

    public Ticket confirm(Long ticketId){
        Ticket ticket = findById(ticketId);
        Seat seat = seatRepository.findById(ticket.getSeatId())
                .orElseThrow(()-> new AppException(ErrorCode.SEAT_NO_EXISTS));
        seat.setStatus(SeatStatus.LOCKED);
        seatRepository.save(seat);
        ticket.setStatus(TicketStatus.CONFIRMED);
        return ticketRepository.save(ticket);
    }

    public Ticket cancel(Long ticketId){
        Ticket ticket = findById(ticketId);
        Seat seat = seatRepository.findById(ticket.getSeatId())
                .orElseThrow(()-> new AppException(ErrorCode.SEAT_NO_EXISTS));
        seat.setStatus(SeatStatus.AVAILABLE);
        seatRepository.save(seat);
        ticket.setStatus(TicketStatus.CANCELLED);
        return ticketRepository.save(ticket);
    }






}
