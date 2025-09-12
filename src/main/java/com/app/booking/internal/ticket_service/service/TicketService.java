package com.app.booking.internal.ticket_service.service;

import com.app.booking.common.enums.PaymentStatus;
import com.app.booking.common.enums.SeatStatus;
import com.app.booking.common.enums.TicketStatus;
import com.app.booking.common.exception.AppException;
import com.app.booking.common.exception.ErrorCode;
import com.app.booking.config.RabbitMQ.BookingConfig;
import com.app.booking.config.RabbitMQ.LockSeatConfig;
import com.app.booking.internal.event_service.entity.Seat;
import com.app.booking.internal.event_service.repository.SeatRepository;
import com.app.booking.internal.payment_service.entity.Payment;
import com.app.booking.internal.payment_service.repository.PaymentRepository;
import com.app.booking.internal.payment_service.service.PaymentService;
import com.app.booking.internal.payment_service.service.VNPayService;
import com.app.booking.internal.ticket_service.dto.request.BookRequest;
import com.app.booking.internal.ticket_service.dto.response.TicketDetailResponse;
import com.app.booking.internal.ticket_service.entity.Ticket;
import com.app.booking.internal.ticket_service.mapper.TicketMapper;
import com.app.booking.internal.ticket_service.repository.TicketRepository;
import com.app.booking.internal.user_service.service.UserService;
import com.app.booking.messaging.dto.CreateBookingConsumer;
import com.app.booking.messaging.dto.LockSeatDQL;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    VNPayService vnPayService;
    RabbitTemplate rabbitTemplate;

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
    public String booking(BookRequest request){
        //Check validation //
        userService.userIsExist(request.getUserId());
        Seat seat = seatRepository.findSeatForUpdate(request.getSeatId())
                .orElseThrow(()-> new AppException(ErrorCode.SEAT_NO_EXISTS));

        if (!seat.getStatus().equals(SeatStatus.AVAILABLE))
            throw new AppException(ErrorCode.TICKET_NO_AVAILABLE);

        Integer price = seatRepository.findPriceBySeatId(request.getSeatId());
        if(price==null)
            throw new AppException(ErrorCode.PRICE_EVENT_INVALID);
        //End check//
        seat.setStatus(SeatStatus.LOCKED);
        Seat seatRes = seatRepository.save(seat);
        Integer paymentId = paymentRepository.save(new Payment()).getId();

        rabbitTemplate.convertAndSend(BookingConfig.CREATE_BOOKING_QUEUE, CreateBookingConsumer.builder()
                        .userId(request.getUserId())
                        .price(price)
                        .paymentId(paymentId)
                        .seatId(seat.getId())
                .build());

        return vnPayService.create(paymentId,price,"Thanh toán loại ghế: " + seat.getSeatNumber());
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

    //consumer used
    @CacheEvict(value = "ticket", allEntries = true)
    public Ticket save(Ticket ticket){
        if(!seatRepository.existsById(ticket.getSeatId()))
            throw new AppException(ErrorCode.SEAT_NO_EXISTS);
        return ticketRepository.save(ticket);
    }

    @CacheEvict(value = "tickets", allEntries = true)
    public void updateStatus(Integer ticketId, boolean isPaid){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(()-> new AppException(ErrorCode.TICKET_NO_EXISTS));

        SeatStatus seatStatus = isPaid ? SeatStatus.BOOKED : SeatStatus.AVAILABLE;
        TicketStatus ticketStatus = isPaid ? TicketStatus.CONFIRMED : TicketStatus.CANCELLED;

        Seat seat = seatRepository.findById(ticket.getSeatId())
                .orElseThrow(() -> new AppException(ErrorCode.SEAT_NO_EXISTS));

        seat.setStatus(seatStatus);
        seatRepository.save(seat);
        ticket.setStatus(ticketStatus);
        ticketRepository.save(ticket);
    }

}
