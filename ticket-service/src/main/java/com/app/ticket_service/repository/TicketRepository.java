package com.app.ticket_service.repository;

import com.app.ticket_service.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Page<Ticket> findAllByUserId(String userId, Pageable pageable);

    Page<Ticket> findAllByOrganizerId(String organizerId, Pageable pageable);
}
