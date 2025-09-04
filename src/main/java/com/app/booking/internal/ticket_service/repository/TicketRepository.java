package com.app.booking.internal.ticket_service.repository;

import com.app.booking.internal.ticket_service.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Page<Ticket> findAllByUserId(String userId, Pageable pageable);
}
