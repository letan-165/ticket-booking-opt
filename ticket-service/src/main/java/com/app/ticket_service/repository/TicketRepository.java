package com.app.ticket_service.repository;

import com.app.ticket_service.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    Page<Ticket> findAllByUserId(String userId, Pageable pageable);

    @Query(value = """
                SELECT t.* 
                FROM tickets t
                JOIN seats s ON t.seat_id = s.id
                JOIN events e ON s.event_id = e.id
                WHERE e.organizer_id = :organizerId
            """, nativeQuery = true)
    Page<Ticket> findAllByOrganizerId(@Param("organizerId") String organizerId, Pageable pageable);

}
