package com.app.booking.internal.ticket_service.service;

import com.app.booking.common.enums.SeatStatus;
import com.app.booking.common.enums.TicketStatus;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.repository.SeatRepository;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.repository.PaymentRepository;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.ticket_service.dto.response.TicketDetailResponse;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.mapper.TicketMapper;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import com.app.booking.internal.user_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {
    PaymentRepository paymentRepository;
    TicketRepository ticketRepository;
    SeatRepository seatRepository;
    UserService userService;
    TicketMapper ticketMapper;
    PaymentService paymentService;

     public Seat findSeatById(Integer id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NO_EXISTS));
    }

    public Ticket findById(Integer ticketId){
        return ticketRepository.findById(ticketId)
                .orElseThrow(()-> new AppException(ErrorCode.TICKET_NO_EXISTS));
    }

    @Cacheable(value = "tickets", keyGenerator = "pageableKeyGenerator")
    public List<Ticket> findAll(Pageable pageable){
        return ticketRepository.findAll(pageable).getContent();
    }

    @Cacheable(value = "tickets", keyGenerator = "pageableKeyGenerator")
    public List<Ticket> findAllByUserId(String userId,Pageable pageable){
        userService.userIsExist(userId);
        return ticketRepository.findAllByUserId(userId, pageable).getContent();
    }

    @Transactional
    @CacheEvict(value = "tickets", allEntries = true)
    public TicketDetailResponse create(BookRequest request){
        userService.userIsExist(request.getUserId());
        Seat seat = seatRepository.findSeatForUpdate(request.getSeatId())
                .orElseThrow(()-> new AppException(ErrorCode.SEAT_NO_EXISTS));

        if (!seat.getStatus().equals(SeatStatus.AVAILABLE))
            throw new AppException(ErrorCode.TICKET_NO_AVAILABLE);

        Integer price = seatRepository.findPriceBySeatId(request.getSeatId());
        if(price==null)
            throw new AppException(ErrorCode.PRICE_EVENT_INVALID);

        seat.setStatus(SeatStatus.LOCKED);
        Seat seatRes = seatRepository.save(seat);

        Ticket ticket = Ticket.builder()
                .userId(request.getUserId())
                .seatId(request.getSeatId())
                .bookingTime(LocalDateTime.now())
                .price(price)
                .status(TicketStatus.BOOKED)
                .build();

        TicketDetailResponse ticketRes = ticketMapper.toTicketDetailResponse(ticketRepository.save(ticket));
        Payment paymentRes = paymentService.create(ticketRes.getId());
        ticketRes.setSeat(seatRes);
        ticketRes.setPayment(paymentRes);

        return ticketRes;
    }

    @Cacheable(value = "ticket", keyGenerator = "simpleKeyGenerator")
    public TicketDetailResponse getDetail(Integer ticketId){
        Ticket ticket = findById(ticketId);
        Seat seat = findSeatById(ticket.getSeatId());
        Payment payment = paymentRepository.findByTicketId(ticketId)
                .orElseThrow(()->new AppException(ErrorCode.PAYMENT_NO_EXISTS));

        TicketDetailResponse response = ticketMapper.toTicketDetailResponse(ticket);
        response.setSeat(seat);
        response.setPayment(payment);
        return response;
    }


}
