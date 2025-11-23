package com.app.ticket_service.service;

import com.app.ticket_common_library.common.enums.SeatStatus;
import com.app.ticket_common_library.common.enums.TicketStatus;
import com.app.ticket_common_library.common.exception.AppException;
import com.app.ticket_common_library.common.exception.ErrorCode;
import com.app.ticket_service.dto.Payment;
import com.app.ticket_service.dto.Seat;
import com.app.ticket_service.dto.request.BookRequest;
import com.app.ticket_service.dto.response.PaymentBookingResponse;
import com.app.ticket_service.dto.response.TicketDetailResponse;
import com.app.ticket_service.entity.Ticket;
import com.app.ticket_service.mapper.TicketMapper;
import com.app.ticket_service.messaging.dto.CreateBookingMessaging;
import com.app.ticket_service.messaging.mq.BookingMQ;
import com.app.ticket_service.repository.TicketRepository;
import com.app.ticket_service.repository.client.AuthClient;
import com.app.ticket_service.repository.client.EventClient;
import com.app.ticket_service.repository.client.PaymentClient;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {
    PaymentClient paymentClient;
    TicketRepository ticketRepository;
    EventClient eventClient;
    AuthClient authClient;
    TicketMapper ticketMapper;
    RabbitTemplate rabbitTemplate;

    public Ticket findById(Integer ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_NO_EXISTS));
    }

    @Cacheable(value = "tickets", keyGenerator = "pageableKeyGenerator")
    public List<Ticket> findAll(Pageable pageable) {
        return ticketRepository.findAll(pageable).getContent();
    }

    @Cacheable(value = "tickets", keyGenerator = "pageableKeyGenerator")
    public List<Ticket> findAllByUserId(String userId, Pageable pageable) {
        authClient.checkUserToken(userId);
        return ticketRepository.findAllByUserId(userId, pageable).getContent();
    }

    @Cacheable(value = "tickets", keyGenerator = "pageableKeyGenerator")
    public List<Ticket> findAllByOrganizerId(String organizerId, Pageable pageable) {
        authClient.checkUserToken(organizerId);
        return ticketRepository.findAllByOrganizerId(organizerId, pageable).getContent();
    }

    @Transactional
    @CacheEvict(value = "tickets", allEntries = true)
    public String booking(BookRequest request) {
        //Check validation //
        authClient.checkUserToken(request.getUserId());
        Seat seat = eventClient.findSeatForUpdate(request.getSeatId());

        if (!seat.getStatus().equals(SeatStatus.AVAILABLE))
            throw new AppException(ErrorCode.TICKET_NO_AVAILABLE);

        Integer price = eventClient.findPriceBySeatId(request.getSeatId());
        if (price == null)
            throw new AppException(ErrorCode.PRICE_EVENT_INVALID);
        //End check//
        seat.setStatus(SeatStatus.LOCKED);
        eventClient.save(seat);

        PaymentBookingResponse response = paymentClient.booking(price, seat.getSeatNumber());

        rabbitTemplate.convertAndSend(BookingMQ.CREATE_BOOKING_QUEUE, CreateBookingMessaging.builder()
                .userId(request.getUserId())
                .price(price)
                .paymentId(response.getPaymentId())
                .seatId(seat.getId())
                .build());

        return response.getUrl();
    }

    @Cacheable(value = "ticket", keyGenerator = "simpleKeyGenerator")
    public TicketDetailResponse getDetail(Integer ticketId) {
        Ticket ticket = findById(ticketId);
        authClient.checkUserToken(ticket.getUserId());
        Seat seat = eventClient.findSeatById(ticket.getSeatId());
        Payment payment = paymentClient.findByTicketId(ticketId);

        TicketDetailResponse response = ticketMapper.toTicketDetailResponse(ticket);
        response.setSeat(seat);
        response.setPayment(payment);
        return response;
    }

    //consumer used
    @CacheEvict(value = "ticket", allEntries = true)
    public Ticket save(Ticket ticket) {
        if (!eventClient.existsById(ticket.getSeatId()))
            throw new AppException(ErrorCode.SEAT_NO_EXISTS);
        return ticketRepository.save(ticket);
    }

    @CacheEvict(value = "tickets", allEntries = true)
    public void updateStatus(Integer ticketId, boolean isPaid) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new AppException(ErrorCode.TICKET_NO_EXISTS));

        Seat seat = eventClient.findSeatById(ticket.getSeatId());

        if (!seat.getStatus().equals(SeatStatus.LOCKED))
            throw new AppException(ErrorCode.TICKET_NO_AVAILABLE);

        SeatStatus seatStatus = isPaid ? SeatStatus.BOOKED : SeatStatus.AVAILABLE;
        TicketStatus ticketStatus = isPaid ? TicketStatus.CONFIRMED : TicketStatus.CANCELLED;


        seat.setStatus(seatStatus);
        eventClient.save(seat);
        ticket.setStatus(ticketStatus);
        ticketRepository.save(ticket);
    }

}
